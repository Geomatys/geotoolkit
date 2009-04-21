/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Locale;
import java.text.NumberFormat;
import org.geotoolkit.math.XMath;


/**
 * Itérateur balayant les barres et étiquettes de graduation d'un axe.
 * Cet itérateur retourne les positions des graduations à partir de la
 * valeur minimale jusqu'à la valeur maximale.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.0
 *
 * @since 2.0
 * @module
 */
class NumberIterator implements TickIterator {
    /**
     * Petite quantité utilisée pour éviter les
     * erreurs d'arrondissements dans les comparisons.
     */
    private static final double EPS = 1E-8;

    /**
     * Valeur de la première graduation principale.
     * Cette valeur est fixée par {@link #init}.
     */
    private double minimum;

    /**
     * Valeur limite des graduations. La dernière graduation n'aura pas
     * nécessairement cette valeur. Cette valeur est fixée par {@link #init}.
     */
    private double maximum;

    /**
     * Intervalle entre deux graduations principales.
     * Cette valeur est fixée par {@link #init}.
     */
    private double increment;

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
     * Nombre de sous-divisions dans une graduation principale.
     * Cette valeur est fixée par {@link #init}.
     */
    private int subTickCount;

    /**
     * Index de la première sous-graduation dans la première graduation principale.
     * Cette valeur est fixée par {@link #init}.
     */
    private int subTickStart;

    /**
     * Index de la graduation principale en cours de traçage. Cette valeur
     * commence à 0 et sera modifiée à chaque appel à {@link #next}.
     */
    private int tickIndex;

    /**
     * Index de la graduation secondaire en cours de traçage. Cette
     * valeur va de 0 inclusivement jusqu'à {@link #subTickCount}
     * exclusivement. Elle sera modifiée à chaque appel à {@link #next}.
     */
    private int subTickIndex;

    /**
     * Valeur de la graduation principale ou secondaire actuelle.
     * Cette valeur sera modifiée à chaque appel à {@link #next}.
     */
    private double value;

    /**
     * Format à utiliser pour écrire les étiquettes de graduation. Ce format ne
     * sera construit que la première fois où {@link #currentLabel} sera appelée.
     */
    private transient NumberFormat format;

    /**
     * Indique si {@link #format} est valide. Le format peut devenir invalide si
     * {@link #init} a été appelée. Dans ce cas, il peut falloir changer le nombre
     * de chiffres après la virgule qu'il écrit.
     */
    private transient boolean formatValid;

    /**
     * Conventions à utiliser pour le formatage des nombres.
     */
    private Locale locale;

    /**
     * Construit un itérateur par défaut. La méthode {@link #init}
     * <u>doit</u> être appelée avant que cet itérateur ne soit utilisable.
     *
     * @param locale Conventions à utiliser pour le formatage des nombres.
     */
    protected NumberIterator(final Locale locale) {
        this.locale = locale;
    }

    /**
     * Initialise l'itérateur.
     *
     * @param minimum
     *          Valeur minimale de la première graduation.
     * @param maximum
     *          Valeur limite des graduations. La dernière graduation n'aura pas
     *          nécessairement cette valeur.
     * @param visualLength
     *          Longueur visuelle de l'axe sur laquelle tracer la graduation.
     *          Cette longueur doit être exprimée en pixels ou en points.
     * @param visualTickSpacing
     *          Espace à laisser visuellement entre deux marques de graduation.
     *          Cet espace doit être exprimé en pixels ou en points (1/72 de pouce).
     */
    protected void init(double minimum,
                  final double maximum,
                  final float  visualLength,
                  final float  visualTickSpacing)
    {
        if (minimum           == this.minimum      &&
            maximum           == this.maximum      &&
            visualLength      == this.visualLength &&
            visualTickSpacing == this.visualTickSpacing)
        {
            rewind();
            return;
        }
        AbstractGraduation.ensureFinite ("minimum",           minimum);
        AbstractGraduation.ensureFinite ("maximum",           maximum);
        AbstractGraduation.ensureFinite ("visualLength",      visualLength); // May be 0.
        AbstractGraduation.ensureNonNull("visualTickSpacing", visualTickSpacing);
        this.visualLength      = visualLength;
        this.visualTickSpacing = visualTickSpacing;
        /*
         * Estime le pas qui donnera au moins l'espacement spécifié entre
         * chaque graduation.  Détermine ensuite si ce pas est de l'ordre
         * des dizaines, centaines ou autre et on ramènera temporairement
         * ce pas à l'ordre des unitées.
         */
        double increment = (maximum - minimum) * (visualTickSpacing / visualLength);
        final double factor = XMath.pow10((int) Math.floor(Math.log10(increment)));
        increment /= factor;
        if (Double.isNaN(increment) || Double.isInfinite(increment) || increment==0) {
            this.minimum      = minimum;
            this.maximum      = maximum;
            this.increment    = Double.NaN;
            this.value        = Double.NaN;
            this.tickIndex    = 0;
            this.subTickIndex = 0;
            this.subTickStart = 0;
            this.subTickCount = 1;
            this.formatValid  = false;
            return;
        }
        /*
         * Le pas se trouve maintenant entre 1 et 10. On l'ajuste maintenant
         * pour lui donner des valeurs qui ne sont habituellement pas trop
         * difficiles à lire.
         */
        final int subTickCount;
        if      (increment <= 1.0) {increment = 1.0; subTickCount=5;}
        else if (increment <= 2.0) {increment = 2.0; subTickCount=4;}
        else if (increment <= 2.5) {increment = 2.5; subTickCount=5;}
        else if (increment <= 4.0) {increment = 4.0; subTickCount=4;}
        else if (increment <= 5.0) {increment = 5.0; subTickCount=5;}
        else                       {increment =10.0; subTickCount=5;}
        increment = increment*factor;
        /*
         * Arrondie maintenant le minimum sur une des graduations principales.
         * Détermine ensuite combien de graduations secondaires il faut sauter
         * sur la première graduation principale.
         */
        final double tmp = minimum;
        minimum          = Math.floor(minimum/increment+EPS)*increment;
        int subTickStart = (int)Math.ceil((tmp-minimum-EPS)*(subTickCount/increment));
        final int  extra = subTickStart / subTickCount;
        minimum      += extra*increment;
        subTickStart -= extra*subTickCount;

        this.increment    = increment;
        this.subTickCount = subTickCount;
        this.maximum      = maximum + Math.abs(maximum*EPS);
        this.minimum      = minimum;
        this.subTickStart = subTickStart;
        this.subTickIndex = subTickStart;
        this.tickIndex    = 0;
        this.value        = minimum + increment*(subTickStart/(double)subTickCount);
        this.formatValid  = false;
    }

    /**
     * Indique s'il reste des graduations à retourner. Cette méthode retourne {@code true}
     * tant que {@link #currentValue} ou {@link #currentLabel} peuvent être appelées.
     */
    @Override
    public boolean hasNext() {
        return value <= maximum;
    }

    /**
     * Indique si la graduation courante est une graduation majeure.
     *
     * @return {@code true} si la graduation courante est une
     *         graduation majeure, ou {@code false} si elle
     *         est une graduation mineure.
     */
    @Override
    public boolean isMajorTick() {
        return subTickIndex == 0;
    }

    /**
     * Returns the position where to draw the current tick.  The position is scaled
     * from the graduation's minimum to maximum.    This is usually the same number
     * than {@link #currentValue}. The mean exception is for logarithmic graduation,
     * in which the tick position is not proportional to the tick value.
     */
    @Override
    public double currentPosition() {
        return value;
    }

    /**
     * Retourne la valeur de la graduation courante. Cette méthode
     * peut être appelée pour une graduation majeure ou mineure.
     */
    @Override
    public double currentValue() {
        return value;
    }

    /**
     * Retourne l'étiquette de la graduation courante. On n'appele généralement
     * cette méthode que pour les graduations majeures, mais elle peut aussi
     * être appelée pour les graduations mineures. Cette méthode retourne
     * {@code null} s'il n'y a pas d'étiquette pour la graduation courante.
     */
    @Override
    public String currentLabel() {
        if (!formatValid) {
            if (format == null) {
                format = NumberFormat.getNumberInstance(locale);
            }
            /*
             * Trouve le nombre de chiffres après la virgule nécessaires pour représenter les
             * étiquettes de la graduation. Impose une limite de six chiffres, limite qui pourrait
             * être atteinte notamment avec les nombres périodiques (par exemple des intervalles
             * de temps exprimés en fractions de jours).
             */
            int precision;
            double step = Math.abs(increment);
            for (precision=0; precision<6; precision++) {
                final double check = Math.rint(step*1E+4) % 1E+4;
                if (!(check > step*EPS)) { // 'step' may be NaN
                    break;
                }
                step *= 10;
            }
            format.setMinimumFractionDigits(precision);
            format.setMaximumFractionDigits(precision);
            formatValid = true;
        }
        return format.format(currentValue());
    }

    /**
     * Passe à la graduation suivante.
     */
    @Override
    public void next() {
        if (++subTickIndex >= subTickCount) {
            subTickIndex = 0;
            tickIndex++;
        }
        // On n'utilise pas "+=" afin d'éviter les erreurs d'arrondissements.
        value = minimum + increment * (tickIndex + subTickIndex / (double)subTickCount);
    }

    /**
     * Passe directement à la graduation majeure suivante.
     */
    @Override
    public void nextMajor() {
        subTickIndex = 0;
        value = minimum + increment * (++tickIndex);
    }

    /**
     * Replace l'itérateur sur la première graduation.
     */
    @Override
    public void rewind() {
        tickIndex    = 0;
        subTickIndex = subTickStart;
        value        = minimum + increment*(subTickStart/(double)subTickCount);
    }

    /**
     * Retourne les conventions à utiliser pour
     * écrire les étiquettes de graduation.
     */
    @Override
    public final Locale getLocale() {
        return locale;
    }

    /**
     * Modifie les conventions à utiliser pour
     * écrire les étiquettes de graduation.
     */
    public final void setLocale(final Locale locale) {
        if (!locale.equals(this.locale)) {
            this.locale = locale;
            this.format = null;
            formatValid = false;
        }
    }
}
