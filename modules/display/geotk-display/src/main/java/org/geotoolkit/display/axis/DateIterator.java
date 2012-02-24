/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display.axis;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.geotoolkit.io.TableWriter;


/**
 * Itérateur balayant les barres et étiquettes de graduation d'un axe du temps. Cet itérateur
 * retourne les positions des graduations à partir de la date la plus ancienne jusqu'à la date
 * la plus récente. Il choisit les intervalles de graduation en supposant qu'on utilise un
 * calendrier grégorien.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
final class DateIterator implements TickIterator {
    /**
     * Nombre de millisecondes dans certaines unités de temps.
     */
    private static final long SEC  = 1000,
                              MIN  = 60*SEC,
                              HRE  = 60*MIN,
                              DAY  = 24*HRE,
                              YEAR = 365*DAY + (DAY/4) - (DAY/100) + (DAY/400),
                              MNT  = YEAR/12;

    /**
     * Liste des intervales souhaités pour la graduation. Les éléments de
     * cette table doivent obligatoirement apparaître en ordre croissant.
     * Voici un exemple d'interprétation: la présence de {@@code 5*MIN}
     * suivit de {@code 10*MIN} implique que si le pas estimé se trouve
     * entre 5 et 10 minutes, ce sera le pas de 10 minutes qui sera sélectionné.
     */
    private static final long[] INTERVAL = {
        SEC,  2*SEC,  5*SEC, 10*SEC, 15*SEC, 20*SEC, 30*SEC,
        MIN,  2*MIN,  5*MIN, 10*MIN, 15*MIN, 20*MIN, 30*MIN,
        HRE,  2*HRE,  3*HRE,  4*HRE,  6*HRE,  8*HRE, 12*HRE,
        DAY,  2*DAY,  3*DAY,  7*DAY, 14*DAY, 21*DAY,
        MNT,  2*MNT,  3*MNT,  4*MNT,  6*MNT,
        YEAR, 2*YEAR, 3*YEAR, 4*YEAR, 5*YEAR
    };

    /**
     * Intervalles des graduations principales et des sous-graduations correspondants
     * à chaque des intervalles du tableau {@link #INTERVAL}.  Cette classe cherchera
     * d'abord un intervalle en millisecondes dans le tableau {@link #INTERVAL}, puis
     * traduira cet intervalle en champ du calendrier grégorien en lisant les éléments
     * correspondants de ce tableau {@link #ROLL}.
     */
    private static final byte[] ROLL = {
         1, (byte)Calendar.SECOND,        25, (byte)Calendar.MILLISECOND, // x10 millis
         2, (byte)Calendar.SECOND,        50, (byte)Calendar.MILLISECOND, // x10 millis
         5, (byte)Calendar.SECOND,         1, (byte)Calendar.SECOND,
        10, (byte)Calendar.SECOND,         2, (byte)Calendar.SECOND,
        15, (byte)Calendar.SECOND,         5, (byte)Calendar.SECOND,
        20, (byte)Calendar.SECOND,         5, (byte)Calendar.SECOND,
        30, (byte)Calendar.SECOND,         5, (byte)Calendar.SECOND,
         1, (byte)Calendar.MINUTE,        10, (byte)Calendar.SECOND,
         2, (byte)Calendar.MINUTE,        30, (byte)Calendar.SECOND,
         5, (byte)Calendar.MINUTE,         1, (byte)Calendar.MINUTE,
        10, (byte)Calendar.MINUTE,         2, (byte)Calendar.MINUTE,
        15, (byte)Calendar.MINUTE,         5, (byte)Calendar.MINUTE,
        20, (byte)Calendar.MINUTE,         5, (byte)Calendar.MINUTE,
        30, (byte)Calendar.MINUTE,         5, (byte)Calendar.MINUTE,
         1, (byte)Calendar.HOUR_OF_DAY,   15, (byte)Calendar.MINUTE,
         2, (byte)Calendar.HOUR_OF_DAY,   30, (byte)Calendar.MINUTE,
         3, (byte)Calendar.HOUR_OF_DAY,   30, (byte)Calendar.MINUTE,
         4, (byte)Calendar.HOUR_OF_DAY,    1, (byte)Calendar.HOUR_OF_DAY,
         6, (byte)Calendar.HOUR_OF_DAY,    1, (byte)Calendar.HOUR_OF_DAY,
         8, (byte)Calendar.HOUR_OF_DAY,    2, (byte)Calendar.HOUR_OF_DAY,
        12, (byte)Calendar.HOUR_OF_DAY,    2, (byte)Calendar.HOUR_OF_DAY,
         1, (byte)Calendar.DAY_OF_MONTH,   4, (byte)Calendar.HOUR_OF_DAY,
         2, (byte)Calendar.DAY_OF_MONTH,   6, (byte)Calendar.HOUR_OF_DAY,
         3, (byte)Calendar.DAY_OF_MONTH,  12, (byte)Calendar.HOUR_OF_DAY,
         7, (byte)Calendar.DAY_OF_MONTH,   1, (byte)Calendar.DAY_OF_MONTH,
        14, (byte)Calendar.DAY_OF_MONTH,   2, (byte)Calendar.DAY_OF_MONTH,
        21, (byte)Calendar.DAY_OF_MONTH,   7, (byte)Calendar.DAY_OF_MONTH,
         1, (byte)Calendar.MONTH,          7, (byte)Calendar.DAY_OF_MONTH,
         2, (byte)Calendar.MONTH,         14, (byte)Calendar.DAY_OF_MONTH,
         3, (byte)Calendar.MONTH,         14, (byte)Calendar.DAY_OF_MONTH,
         4, (byte)Calendar.MONTH,          1, (byte)Calendar.MONTH,
         6, (byte)Calendar.MONTH,          1, (byte)Calendar.MONTH,
         1, (byte)Calendar.YEAR,           2, (byte)Calendar.MONTH,
         2, (byte)Calendar.YEAR,           4, (byte)Calendar.MONTH,
         3, (byte)Calendar.YEAR,           6, (byte)Calendar.MONTH,
         4, (byte)Calendar.YEAR,           1, (byte)Calendar.YEAR,
         5, (byte)Calendar.YEAR,           1, (byte)Calendar.YEAR
    };

    /**
     * Nombre de colonne dans le tableau {@link ROLL}. Le tableau {@link ROLL} doit être
     * interprété comme une matrice de 4 colonnes et d'un nombre indéterminé de lignes.
     */
    private static final int ROLL_WIDTH = 4;

    /**
     * Liste des champs de dates qui apparaissent dans le tableau {@link ROLL}. Cette liste
     * doit être du champ le plus grand (YEAR) vers le champ le plus petit (MILLISECOND).
     */
    private static final int[] FIELD = {
        Calendar.YEAR,
        Calendar.MONTH,
        Calendar.DAY_OF_MONTH,
        Calendar.HOUR_OF_DAY,
        Calendar.MINUTE,
        Calendar.SECOND,
        Calendar.MILLISECOND
    };

    /**
     * Liste des noms des champs (à des fins de déboguage seulement).
     * Cette liste doit être dans le même ordre que les éléments de {@link #FIELD}.
     */
    private static final String[] FIELD_NAME = {
        "YEAR",
        "MONTH",
        "DAY",
        "HOUR",
        "MINUTE",
        "SECOND",
        "MILLISECOND"
    };

    /**
     * Date de la première graduation principale. Cette valeur est fixée par {@link #init}.
     */
    private long minimum;

    /**
     * Date limite des graduations. La dernière graduation ne sera pas nécessairement à
     * cette date. Cette valeur est fixée par {@link #init}.
     */
    private long maximum;

    /**
     * Estimation de l'intervalle entre deux graduations principales.
     * Cette valeur est fixée par {@link #init}.
     */
    private long increment;

    /**
     * Longueur de l'axe (en points). Cette information est conservée afin d'éviter de
     * refaire toute la procédure {@link #init} si les paramètres n'ont pas changés.
     */
    private float visualLength;

    /**
     * Espace à laisser (en points) entre les graduations principales.
     * Cette information est conservée afin d'éviter de refaire toute
     * la procédure {@link #init} si les paramètres n'ont pas changés.
     */
    private float visualTickSpacing;

    /**
     * Nombre de fois qu'il faut incrémenter le champ {@link #tickField} du
     * calendrier pour passer à la graduation suivante. Cette opération peut
     * se faire avec {@code calendar.add(tickField, tickAdd)}.
     */
    private int tickAdd;

    /**
     * Champ du calendrier qu'il faut incrémenter pour passer à la graduation suivante.
     * Cette opération peut se faire avec {@code calendar.add(tickField, tickAdd)}.
     */
    private int tickField;

    /**
     * Nombre de fois qu'il faut incrémenter le champ {@link #tickField} du calendrier pour
     * passer à la sous-graduation suivante. Cette opération peut se faire avec
     * {@code calendar.add(tickField, tickAdd)}.
     */
    private int subTickAdd;

    /**
     * Champ du calendrier qu'il faut incrémenter pour passer à la sous-graduation suivante.
     * Cette opération peut se faire avec {@code calendar.add(tickField, tickAdd)}.
     */
    private int subTickField;

    /**
     * Date de la graduation principale ou secondaire actuelle.
     * Cette valeur sera modifiée à chaque appel à {@link #next}.
     */
    private long value;

    /**
     * Date de la prochaine graduation principale. Cette
     * valeur sera modifiée à chaque appel à {@link #next}.
     */
    private long nextTick;

    /**
     * Date de la prochaine graduation secondaire. Cette
     * valeur sera modifiée à chaque appel à {@link #next}.
     */
    private long nextSubTick;

    /**
     * Indique si {@link #value} représente une graduation principale.
     */
    private boolean isMajorTick;

    /**
     * Valeurs de {@link #value}, {@link #nextTick} et {@link #nextSubTick0}
     * immédiatement après l'appel de {@link #rewind}.
     */
    private long value0, nextTick0, nextSubTick0;

    /**
     * Valeur de {@link #isMajorTick} immédiatement après l'appel de {@link #rewind}.
     */
    private boolean isMajorTick0;

    /**
     * Calendrier servant à avancer d'une certaine période de temps (jour, semaine, mois...).
     * <strong>Note: Par convention et pour des raisons de performances (pour éviter d'imposer
     * au calendrier de recalculer ses champs trop souvent), ce calendrier devrait toujours
     * contenir la date {@link #nextSubTick}.
     */
    private Calendar calendar;

    /**
     * Objet temporaire à utiliser pour passer des dates
     * en argument à {@link #calendar} et {@link #format}.
     */
    private final Date date = new Date();

    /**
     * Format à utiliser pour écrire les étiquettes de graduation. Ce format ne
     * sera construit que la première fois où {@link #currentLabel} sera appelée.
     */
    private transient DateFormat format;

    /**
     * Code du format utilisé pour construire le champ de date de {@link #format}. Les codes
     * valides sont notamment  {@link DateFormat#SHORT}, {@link DateFormat#MEDIUM} ou {@link
     * DateFormat#LONG}. La valeur -1 indique que le format ne contient pas de champ de date,
     * seulement un champ des heures.
     */
    private transient int dateFormat = -1;

    /**
     * Code du format utilisé pour construire le champ des heures de {@link #format}. Les codes
     * valides sont notamment  {@link DateFormat#SHORT},  {@link DateFormat#MEDIUM}  ou  {@link
     * DateFormat#LONG}. La valeur -1 indique que le format ne contient pas de champ des heures,
     * seulement un champ de date.
     */
    private transient int timeFormat = -1;

    /**
     * Indique si {@link #format} est valide. Le format peut devenir invalide si {@link #init} a
     * été appelée. Dans ce cas, il peut falloir changer le nombre de chiffres après la virgule
     * qu'il écrit.
     */
    private transient boolean formatValid;

    /**
     * Conventions à utiliser pour le formatage des nombres.
     */
    private final Locale locale;

    /**
     * Construit un itérateur pour la graduation d'un axe du temps. La méthode {@link #init}
     * <u>doit</u> être appelée avant que l'itérateur ne soit utilisable.
     *
     * @param timezone Fuseau horaire des dates.
     * @param locale   Conventions à utiliser pour le formatage des dates.
     */
    protected DateIterator(final TimeZone timezone, final Locale locale) {
        assert INTERVAL.length*ROLL_WIDTH == ROLL.length;
        calendar = Calendar.getInstance(timezone, locale);
        this.locale = locale;
    }

    /**
     * Initialise l'itérateur.
     *
     * @param minimum         Date minimale de la première graduation.
     * @param maximum         Date limite des graduations. La dernière graduation
     *                        ne sera pas nécessairement à cette date.
     * @param visualLength    Longueur visuelle de l'axe sur laquelle tracer la graduation.
     *                        Cette longueur doit être exprimée en pixels ou en points.
     * @param visualTickSpace Espace à laisser visuellement entre deux marques de graduation.
     *                        Cet espace doit être exprimé en pixels ou en points (1/72 de pouce).
     */
    protected void init(final long  minimum,
                        final long  maximum,
                        final float visualLength,
                        final float visualTickSpacing)
    {
        if (minimum           == this.minimum      &&
            maximum           == this.maximum      &&
            visualLength      == this.visualLength &&
            visualTickSpacing == this.visualTickSpacing)
        {
            rewind();
            return;
        }
        AbstractGraduation.ensureNonZero("visualLength",      visualLength);
        AbstractGraduation.ensureNonZero("visualTickSpacing", visualTickSpacing);
        this.visualLength      = visualLength;
        this.visualTickSpacing = visualTickSpacing;
        this.formatValid       = false;
        this.minimum           = minimum;
        this.maximum           = maximum;
        this.increment         = Math.round((maximum-minimum) *
                                            ((double)visualTickSpacing/(double)visualLength));
        /*
         * Après avoir fait une estimation de l'intervalle d'échantillonage,
         * vérifie si on trouve cette estimation dans le tableau 'INTERVAL'.
         * Si on trouve la valeur exacte, tant mieux! Sinon, on cherchera
         * l'intervalle le plus proche.
         */
        int index = Arrays.binarySearch(INTERVAL, increment);
        if (index < 0) {
            index= ~index;
            if (index == 0) {
                // L'intervalle est plus petit que le
                // plus petit élément de 'INTERVAL'.
                round(Calendar.MILLISECOND);
                findFirstTick();
                return;
            } else if (index>=INTERVAL.length) {
                // L'intervalle est plus grand que le
                // plus grand élément de 'INTERVAL'.
                increment /= YEAR;
                round(Calendar.YEAR);
                increment *= YEAR;
                findFirstTick();
                return;
            } else {
                // L'index pointe vers un intervalle plus grand que
                // l'intervalle demandé. Vérifie si l'intervalle
                // inférieur ne serait pas plus proche.
                if (increment-INTERVAL[index-1] < INTERVAL[index]-increment) {
                    index--;
                }
            }
        }
        this.increment    = INTERVAL[index]; index *= ROLL_WIDTH;
        this.tickAdd      = ROLL[index+0];
        this.tickField    = ROLL[index+1];
        this.subTickAdd   = ROLL[index+2];
        this.subTickField = ROLL[index+3];
        if (subTickField == Calendar.MILLISECOND) {
            subTickAdd *= 10;
        }
        findFirstTick();
    }

    /**
     * Arrondi {@link #increment} à un nombre qui se lit bien. Le nombre
     * choisit sera un de ceux de la suite 1, 2, 5, 10, 20, 50, 100, 200, 500, etc.
     */
    private void round(final int field) {
        int factor=1;
        while (factor <= increment) {
            factor *= 10;
        }
        if (factor >= 10) {
            factor /= 10;
        }
        increment /= factor;
        if      (increment<=0                ) increment= 1;
        else if (increment>=3 && increment<=4) increment= 5;
        else if (increment>=6                ) increment=10;
        increment    = Math.max(increment*factor, 5);
        tickAdd      = (int)increment;
        subTickAdd   = (int)(increment/(increment==2 ? 4 : 5));
        tickField    = field;
        subTickField = field;
    }

    /**
     * Replace l'itérateur sur la première graduation. La position de la première graduation
     * sera calculée et retenue pour un positionnement plus rapide à l'avenir.
     */
    private void findFirstTick() {
        calendar.clear();
        value = minimum;
        date.setTime(value);
        calendar.setTime(date);
        if (true) {
            // Arrondie la date de départ. Note: ce calcul exige que
            // tous les champs commencent à 0 plutôt que 1, y compris
            // les mois et le jour du mois.
            final int offset = calendar.getActualMinimum(tickField);
            int      toRound = calendar.get(tickField)-offset;
            toRound = (toRound/tickAdd)*tickAdd;
            calendar.set(tickField, toRound+offset);
        }
        truncate(calendar, tickField);
        nextTick=calendar.getTime().getTime();
        nextSubTick=nextTick;
        while (nextTick < minimum) {
            calendar.add(tickField, tickAdd);
            nextTick = calendar.getTime().getTime();
        }
        date.setTime(nextSubTick);
        calendar.setTime(date);
        while (nextSubTick < minimum) {
            calendar.add(subTickField, subTickAdd);
            nextSubTick = calendar.getTime().getTime();
        }
        /* 'calendar' a maintenant la valeur 'nextSubTick', comme le veut la spécification de
         * ce champ. On appelle maintenant 'next' pour transférer cette valeur 'nextSubTick'
         * vers 'value'. Notez que 'next' peut être appelée même si value>maximum.
         */
        next();

        // Retient les positions trouvées.
        this.value0       = this.value;
        this.nextTick0    = this.nextTick;
        this.nextSubTick0 = this.nextSubTick;
        this.isMajorTick0 = this.isMajorTick;

        assert calendar.getTime().getTime() == nextSubTick;
    }

    /**
     * Met à 0 tous les champs du calendrier inférieur au champ {@code field}
     * spécifié. Note: si le calendrier spécifié est {@link #calendar},  il est de
     * la responsabilité de l'appelant de restituer {@link #calendar} dans son état
     * correct après l'appel de cette méthode.
     */
    private static void truncate(final Calendar calendar, int field) {
        for (int i=0; i<FIELD.length; i++) {
            if (FIELD[i] == field) {
                calendar.get(field); // Force la mise à jour des champs.
                while (++i < FIELD.length) {
                    field = FIELD[i];
                    calendar.set(field, calendar.getActualMinimum(field));
                }
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDone() {
        return value > maximum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMajorTick() {
        return isMajorTick;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double currentPosition() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double currentValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String currentLabel() {
        if (!formatValid) {
            date.setTime(minimum);
            calendar.setTime(date);
            final int firstDay =calendar.get(Calendar.DAY_OF_YEAR);
            final int firstYear=calendar.get(Calendar.YEAR);

            date.setTime(maximum);
            calendar.setTime(date);
            final int lastDay =calendar.get(Calendar.DAY_OF_YEAR);
            final int lastYear=calendar.get(Calendar.YEAR);

            final int dateFormat = (firstYear==lastYear && firstDay==lastDay) ? -1 : DateFormat.MEDIUM;
            final int timeFormat;

            if      (increment >= DAY) timeFormat = -1;
            else if (increment >= MIN) timeFormat = DateFormat.SHORT ;
            else if (increment >= SEC) timeFormat = DateFormat.MEDIUM;
            else                       timeFormat = DateFormat.LONG  ;

            if (dateFormat!=this.dateFormat || timeFormat!=this.timeFormat || format==null) {
                this.dateFormat = dateFormat;
                this.timeFormat = timeFormat;
                if (dateFormat == -1) {
                    if (timeFormat == -1) {
                        format = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                    } else {
                        format = DateFormat.getTimeInstance(timeFormat, locale);
                    }
                } else if (timeFormat == -1) {
                    format = DateFormat.getDateInstance(dateFormat, locale);
                } else {
                    format = DateFormat.getDateTimeInstance(dateFormat, timeFormat, locale);
                }
                format.setCalendar(calendar);
            }
            formatValid = true;
        }
        date.setTime(value);
        final String label = format.format(date);
        // Remet 'calendar' dans l'état qu'il est sencé avoir
        // d'après la spécification du champ {@link #calendar}.
        date.setTime(nextSubTick);
        calendar.setTime(date);
        return label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void next() {
        assert calendar.getTime().getTime() == nextSubTick;
        if (nextSubTick < nextTick) {
            isMajorTick = false;
            value = nextSubTick;
            /*
             * IMPORTANT: On suppose ici que 'calendar' a déjà la date 'nextSubTick'. Si ce
             *            n'était pas le cas, il faudrait ajouter les lignes suivantes:
             */
            if (false) {
                date.setTime(value);
                calendar.setTime(date);
                // 'setTime' oblige 'calendar' à recalculer ses
                // champs, ce qui a un impact sur la performance.
            }
            calendar.add(subTickField, subTickAdd);
            nextSubTick = calendar.getTime().getTime();
            // 'calendar' contient maintenant la date 'nextSubTick',
            // comme le veut la spécification du champ {@link #calendar}.
        } else {
            nextMajor();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextMajor() {
        isMajorTick = true;
        value = nextTick;
        date.setTime(value);

        calendar.setTime(date);
        calendar.add(tickField, tickAdd);
        truncate(calendar, tickField);
        nextTick=calendar.getTime().getTime();

        calendar.setTime(date);
        calendar.add(subTickField, subTickAdd);
        nextSubTick = calendar.getTime().getTime();
        // 'calendar' contient maintenant la date 'nextSubTick',
        // comme le veut la spécification du champ {@link #calendar}.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rewind() {
        this.value       = value0;
        this.nextTick    = nextTick0;
        this.nextSubTick = nextSubTick0;
        this.isMajorTick = isMajorTick0;
        // Pour être en accord avec la spécification
        // du champs {@link #calendar}...
        date.setTime(nextSubTick);
        calendar.setTime(date);
    }

    /**
     * Modifie les conventions à utiliser pour écrire les étiquettes de graduation.
     */
    public void setLocale(final Locale locale) {
        if (!locale.equals(this.locale)) {
            calendar    = Calendar.getInstance(getTimeZone(), locale);
            format      = null;
            formatValid = false;
            // Pour être en accord avec la spécification du champs {@link #calendar}...
            date.setTime(nextSubTick);
            calendar.setTime(date);
        }
        assert calendar.getTime().getTime() == nextSubTick;
    }

    /**
     * Retourne le fuseau horaire utilisé pour exprimer les dates dans la graduation.
     */
    public TimeZone getTimeZone() {
        return calendar.getTimeZone();
    }

    /**
     * Modifie le fuseau horaire utilisé pour exprimer les dates dans la graduation.
     */
    public void setTimeZone(final TimeZone timezone) {
        if (!timezone.equals(getTimeZone())) {
            calendar.setTimeZone(timezone);
            format      = null;
            formatValid = false;
            // Pour être en accord avec la spécification
            // du champs {@link #calendar}...
            date.setTime(nextSubTick);
            calendar.setTime(date);
        }
        assert calendar.getTime().getTime() == nextSubTick;
    }

    /**
     * Retourne le nom du champ de {@link Calendar} correspondant à la valeur spécifiée.
     */
    private static String getFieldName(final int field) {
        for (int i=0; i<FIELD.length; i++) {
            if (FIELD[i] == field) {
                return FIELD_NAME[i];
            }
        }
        return String.valueOf(field);
    }

    /**
     * Returns a string representation of this iterator. Used for debugging purpose only.
     */
    @Override
    public String toString() {
        final TableWriter out = new TableWriter(null, " ");
        final DateFormat format = DateFormat.getDateTimeInstance();
        format.setTimeZone(calendar.getTimeZone());

        out.write("Minimum\t=\t");
        out.write(format.format(new Date(minimum)));

        out.write("\nMaximum\t=\t");
        out.write(format.format(new Date(maximum)));

        out.write("\nIncrement\t=\t");
        out.write(String.valueOf(increment / (24*3600000f)));
        out.write(" days");

        out.write("\nTick inc.\t=\t");
        out.write(tickAdd);
        out.write(' ');
        out.write(getFieldName(tickField));

        out.write("\nSubTick inc.\t=\t");
        out.write(subTickAdd);
        out.write(' ');
        out.write(getFieldName(subTickField));

        out.write("\nNext tick\t=\t");
        out.write(format.format(new Date(nextTick)));

        out.write("\nNext subtick\t=\t");
        out.write(format.format(new Date(nextSubTick)));
        out.write('\n');

        return out.toString();
    }
}
