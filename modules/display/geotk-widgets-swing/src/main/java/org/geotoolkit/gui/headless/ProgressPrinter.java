/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.gui.headless;

import java.io.Console;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.BreakIterator;

import org.opengis.util.ProgressListener;
import org.opengis.util.InternationalString;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.SimpleInternationalString;


/**
 * Prints progress report of a lengtly operation to an output stream. Progress are reported
 * as percentage on a single line. This class can also prints warning, which is useful for
 * notifications without stoping the lenghtly task.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 1.0
 * @module
 */
public class ProgressPrinter implements ProgressListener {
    /**
     * Nom de l'opération en cours. Le pourcentage sera écris à la droite de ce nom.
     */
    private InternationalString description;

    /**
     * Flot utilisé pour l'écriture de l'état d'avancement d'un
     * processus ainsi que pour les écritures des commentaires.
     */
    private final PrintWriter out;

    /**
     * Indique si le caractère '\r' ramène au début de la ligne courante sur
     * ce système. On supposera que ce sera le cas si le système n'utilise
     * pas la paire "\r\n" pour changer de ligne (comme le system VAX-VMS).
     */
    private final boolean CR_supported;

    /**
     * Longueur maximale des lignes. L'espace utilisable sera un peu
     * moindre car quelques espaces seront laissés en début de ligne.
     */
    private final int maxLength;

    /**
     * Nombre de caractères utilisés lors de l'écriture de la dernière ligne.
     * Ce champ est mis à jour par la méthode {@link #carriageReturn} chaque
     * fois que l'on déclare que l'on vient de terminer l'écriture d'une ligne.
     */
    private int lastLength;

    /**
     * Position à laquelle commencer à écrire le pourcentage. Cette information
     * est gérée automatiquement par la méthode {@link #progress}. La valeur -1
     * signifie que ni le pourcentage ni la description n'ont encore été écrits.
     */
    private int percentPosition = -1;

    /**
     * Dernier pourcentage écrit. Cette information est utilisée afin d'éviter d'écrire
     * deux fois le même pourcentage, ce qui ralentirait inutilement le système.
     * La valeur -1 signifie qu'on n'a pas encore écrit de pourcentage.
     */
    private float lastPercent = -1;

    /**
     * Format à utiliser pour écrire les pourcentages.
     */
    private NumberFormat format;

    /**
     * Objet utilisé pour couper les lignes correctements lors de l'affichage
     * de messages d'erreurs qui peuvent prendre plusieurs lignes.
     */
    private BreakIterator breaker;

    /**
     * Indique si cet objet a déjà écrit des avertissements. Si
     * oui, on ne réécrira pas le gros titre "avertissements".
     */
    private boolean hasPrintedWarning;

    /**
     * Source du dernier message d'avertissement. Cette information est
     * conservée afin d'éviter de répéter la source lors d'éventuels
     * autres messages d'avertissements.
     */
    private String lastSource;

    /**
     * {@code true} if the action has been canceled.
     */
    private volatile boolean canceled;

    /**
     * Constructs a new object sending progress reports to the
     * {@linkplain java.lang.System#out standard output stream}.
     * The maximal line length is assumed to be 80 characters.
     */
    public ProgressPrinter() {
        this(writer());
    }

    /**
     * Constructs a new object sending progress reports to the specified stream.
     * The maximal line length is assumed 80 characters.
     *
     * @param out The output stream.
     */
    public ProgressPrinter(final PrintWriter out) {
        this(out, 80);
    }

    /**
     * Constructs a new object sending progress reports to the specified stream.
     *
     * @param out The output stream.
     * @param maxLength The maximal line length. This is used by {@link #warningOccurred}
     *        for splitting longer lines into many lines.
     */
    public ProgressPrinter(final PrintWriter out, final int maxLength) {
        this.out = out;
        this.maxLength = maxLength;
        final String lineSeparator = System.getProperty("line.separator", "\n");
        CR_supported = lineSeparator.equals("\r\n") || lineSeparator.equals("\n");
    }

    /**
     * Returns the default writer.
     */
    private static PrintWriter writer() {
        final Console console = System.console();
        return console != null ? console.writer() : new PrintWriter(System.out);
    }

    /**
     * Efface le reste de la ligne (si nécessaire) puis repositionne le curseur au début
     * de la ligne. Si les retours chariot ne sont pas supportés, alors cette méthode va
     * plutôt passer à la ligne suivante. Dans tous les cas, le curseur se trouvera au
     * début d'une ligne et la valeur {@code length} sera affecté au champ
     * {@link #lastLength}.
     *
     * @param length Nombre de caractères qui ont été écrit jusqu'à maintenant sur cette ligne.
     *        Cette information est utilisée pour ne mettre que le nombre d'espaces nécessaires
     *        à la fin de la ligne.
     */
    private void carriageReturn(final int length) {
        if (CR_supported && length<maxLength) {
            for (int i=length; i<lastLength; i++)  {
                out.print(' ');
            }
            out.print('\r');
            out.flush();
        } else {
            out.println();
        }
        lastLength = length;
    }

    /**
     * Ajoute des points à la fin de la ligne jusqu'à représenter le pourcentage spécifié.
     * Cette méthode est utilisée pour représenter les progrès sur un terminal qui ne supporte
     * pas les retours chariots.
     *
     * @param percent Pourcentage accompli de l'opération. Cette valeur doit obligatoirement
     *        se trouver entre 0 et 100 (ça ne sera pas vérifié).
     */
    private void completeBar(final float percent) {
        final int end = (int) ((percent/100)*((maxLength-2)-percentPosition)); // Round toward 0.
        while (lastLength < end) {
            out.print('.');
            lastLength++;
        }
    }

    /**
     * Sets the description of the current task being performed. This method is usually invoked
     * before any progress begins. However, it is legal to invoke this method at any time during
     * the operation, in which case the description display is updated without any change to the
     * percentage accomplished.
     *
     * @since 2.3
     */
    @Override
    public void setTask(final InternationalString task) {
        description = task;
    }

    /**
     * Returns the description of the current task being performed, or {@code null} if none.
     *
     * @since 2.3
     */
    @Override
    public InternationalString getTask() {
        return description;
    }

    /**
     * Sets the description for the lenghtly operation to be reported. This method is usually
     * invoked before any progress begins. However, it is legal to invoke this method at any
     * time during the operation, in which case the description display is updated without
     * any change to the percentage accomplished.
     *
     * @deprecated Replaced by setTask
     */
    @Override
    @Deprecated
    public synchronized void setDescription(final String description) {
        this.description = new SimpleInternationalString(description);
    }

    /**
     * Description for the lengthly operation to be reported, or {@code null} if none.
     *
     * @deprecated Replaced by getTask().toString()
     */
    @Override
    @Deprecated
    public String getDescription() {
        return (description != null) ? description.toString() : null;
    }

    /**
     * Notifies this listener that the operation begins.
     */
    @Override
    public synchronized void started() {
        int length = 0;
        if (description != null) {
            out.print(description);
            length=description.length();
        }
        if (CR_supported) {
            carriageReturn(length);
        }
        out.flush();
        percentPosition   = length;
        lastPercent       = -1;
        lastSource        = null;
        hasPrintedWarning = false;
    }

    /**
     * Notifies this listener of progress in the lengthly operation. Progress are reported
     * as a value between 0 and 100 inclusive. Values out of bounds will be clamped.
     *
     * @param percent The progress as a value between 0 and 100 inclusive.
     */
    @Override
    public synchronized void progress(float percent) {
        if (percent < 0  ) percent = 0;
        if (percent > 100) percent = 100;
        if (CR_supported) {
            /*
             * Si le périphérique de sortie supporte les retours chariot,
             * on écrira l'état d'avancement comme un pourcentage après
             * la description, comme dans "Lecture des données (38%)".
             */
            if (percent != lastPercent) {
                if (format == null) {
                    format = NumberFormat.getPercentInstance();
                }
                final String text = format.format(percent / 100.0);
                int length = text.length();
                percentPosition = 0;
                if (description != null) {
                    out.print(description);
                    out.print(' ');
                    length += (percentPosition=description.length()) + 1;
                }
                out.print('(');
                out.print(text);
                out.print(')');
                length += 2;
                carriageReturn(length);
                lastPercent = percent;
            }
        } else {
            /*
             * Si le périphérique ne supporte par les retours chariots, on
             * écrira l'état d'avancement comme une série de points placés
             * après la description, comme dans "Lecture des données......"
             */
            completeBar(percent);
            lastPercent = percent;
            out.flush();
        }
    }

    /**
     * Returns the current progress as a percent completed.
     *
     * @since 2.3
     */
    @Override
    public float getProgress() {
        return lastPercent;
    }

    /**
     * Indicates that task should be cancelled.
     *
     * @since 2.3
     */
    @Override
    public void setCanceled(final boolean cancel) {
        canceled = cancel;
    }

    /**
     * Returns {@code true} if this job is cancelled.
     *
     * @since 2.3
     */
    @Override
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Prints a warning. The first time this method is invoked, the localized word "WARNING" will
     * be printed in the middle of a box. If a source is specified, it will be printed only if it
     * is not the same one than the source of the last warning. If a marging is specified, it will
     * be printed of the left side of the first line of the warning message.
     *
     * @param source The source of the warning, or {@code null} if none. This is typically the
     *        filename in process of being parsed.
     * @param margin Text to write on the left side of the warning message, or {@code null} if none.
     *        This is typically the line number where the error occured in the {@code source} file.
     * @param warning The warning message. If this string is longer than the maximal length
     *        specified at construction time (80 characters by default), then it will be splitted
     *        in as many lines as needed and indented according the marging width.
     */
    @Override
    public synchronized void warningOccurred(final String source, String margin, final String warning) {
        carriageReturn(0);
        if (!hasPrintedWarning) {
            printInBox(Vocabulary.format(Vocabulary.Keys.WARNING));
            hasPrintedWarning = true;
        }
        if (!Utilities.equals(source, lastSource)) {
            out.println();
            out.println(source!=null ? source : Vocabulary.format(Vocabulary.Keys.UNTITLED));
            lastSource = source;
        }
        /*
         * Procède à l'écriture de l'avertissement avec (de façon optionnelle)
         * quelque chose dans la marge (le plus souvent un numéro de ligne).
         */
        String prefix = "    ";
        String second = prefix;
        if (margin != null) {
            margin = trim(margin);
            if (margin.length() != 0) {
                prefix = prefix + '(' + margin + ") ";
                second = Utilities.spaces(prefix.length());
            }
        }
        int width = maxLength - prefix.length() - 1;
        if (breaker == null) {
            breaker = BreakIterator.getLineInstance();
        }
        breaker.setText(warning);
        int start = breaker.first(), end = start, nextEnd;
        while ((nextEnd = breaker.next()) != BreakIterator.DONE) {
            while (nextEnd - start > width) {
                if (end <= start) {
                    end = Math.min(nextEnd, start + width);
                }
                out.print(prefix);
                out.println(warning.substring(start, end));
                prefix = second;
                start = end;
            }
            end=Math.min(nextEnd, start + width);
        }
        if (end > start) {
            out.print(prefix);
            out.println(warning.substring(start, end));
        }
        if (!CR_supported && description != null) {
            out.print(description);
            completeBar(lastPercent);
        }
        out.flush();
    }

    /**
     * Prints an exception stack trace in a box.
     */
    @Override
    public synchronized void exceptionOccurred(final Throwable exception) {
        carriageReturn(0);
        printInBox(Vocabulary.format(Vocabulary.Keys.EXCEPTION));
        exception.printStackTrace(out);
        hasPrintedWarning = false;
        out.flush();
    }

    /**
     * Retourne la chaîne {@code margin} sans les éventuelles parenthèses qu'elle pourrait avoir
     * de part et d'autre.
     */
    private static String trim(String margin) {
        margin = margin.trim();
        int lower = 0;
        int upper = margin.length();
        while (lower<upper && margin.charAt(lower+0) == '(') lower++;
        while (lower<upper && margin.charAt(upper-1) == ')') upper--;
        return margin.substring(lower, upper);
    }

    /**
     * Écrit dans une boîte entouré d'astérix le texte spécifié en argument.
     * Ce texte doit être sur une seule ligne et ne pas comporter de retour
     * chariot. Les dimensions de la boîte seront automatiquement ajustées.
     * @param text Texte à écrire (une seule ligne).
     */
    private void printInBox(String text) {
        int length = text.length();
        for (int pass=-2; pass<=2; pass++) {
            switch (Math.abs(pass)) {
                case 2: for (int j=-10; j<length; j++) out.print('*');
                        out.println();
                        break;

                case 1: out.print("**");
                        for (int j=-6; j<length; j++) out.print(' ');
                        out.println("**");
                        break;

                case 0: out.print("**   ");
                        out.print(text);
                        out.println("   **");
                        break;
            }
        }
    }

    /**
     * Notifies this listener that the operation has finished. The progress indicator will
     * shows 100% or disaspears. If warning messages were pending, they will be printed now.
     */
    @Override
    public synchronized void complete() {
        if (!CR_supported) {
            completeBar(100);
        }
        carriageReturn(0);
        out.flush();
    }

    /**
     * Releases any resource hold by this object.
     */
    @Override
    public void dispose() {
    }
}
