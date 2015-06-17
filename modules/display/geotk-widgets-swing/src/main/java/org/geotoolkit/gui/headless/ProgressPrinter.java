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
package org.geotoolkit.gui.headless;

import java.util.Locale;
import java.util.Objects;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.BreakIterator;

import org.opengis.util.InternationalString;

import org.apache.sis.util.CharSequences;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.process.ProgressController;


/**
 * Prints progress report of a lengthly operation to an output stream. Progress are reported
 * as percentage on a single line. This class can also prints warning, which is useful for
 * notifications without stopping the lengthly task.
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.20
 *
 * @since 1.0
 * @module
 */
public class ProgressPrinter extends ProgressController {
    /**
     * The stream where to write progress reports.
     */
    private final PrintWriter out;

    /**
     * {@code true} if {@code '\r'} brings the cursor back to the beginning of current line.
     * We assume {@code true} if the system do not uses the {@code "\r\n"} pair for going
     * to the next line (like VAX-VMS systems).
     */
    private final boolean CR_supported;

    /**
     * The maximal line length. Note that the amount of spaces really available is slightly
     * less since some spaces will be used at the beginning of the line.
     */
    private final int maxLength;

    /**
     * Amount of characters used last time a line was sent to the output stream.
     * This field is updated by the {@link #carriageReturn} method.
     */
    private int lastLength;

    /**
     * Position where to write the percentage.
     * This field is updated by the {@link #progress} method.
     */
    private int percentPosition;

    /**
     * The last percentage written, or {@code null} if none. This is used in order to avoid
     * sending many time the same line with invisible change.
     */
    private String lastPercent;

    /**
     * The format to use for writing the percentage.
     */
    private NumberFormat format;

    /**
     * Object used for breaking a long warning message on many lines.
     */
    private BreakIterator breaker;

    /**
     * {@code true} if this printer has written at least one warning.
     */
    private boolean hasPrintedWarning;

    /**
     * The source of the last warnings, or {@code null} if unknown.
     * Used in order to avoid repeating this information many time.
     */
    private String lastSource;

    /**
     * Constructs a new object sending progress reports to the
     * {@linkplain java.lang.System#out standard output stream}.
     * The maximal line length is assumed to be 80 characters.
     */
    public ProgressPrinter() {
        this(IOUtilities.standardPrintWriter());
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
        final String lineSeparator = System.lineSeparator();
        CR_supported = lineSeparator.equals("\r\n") || lineSeparator.equals("\n");
    }

    /**
     * Erases the remainder of current line (if needed), then moves the cursor to the beginning
     * of current line. If carriage returns are not supported, then this method will rather move
     * to the next line. If every cases, the cursor will be at the beginning of a line and the
     * {@link #lastLength} field will have the {@code length} value.
     *
     * @param length Number of characters which have been written on the current line.
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
     * Adds dots at the end of current line, just before to write the progress percentage.
     * This method is used only for terminals that do not support carriage returns.
     *
     * @param percent Progress percentage between 0 and 100.
     */
    private void completeBar(final float percent) {
        final int end = (int) ((percent/100) * ((maxLength-2) - percentPosition)); // Round toward 0.
        while (lastLength < end) {
            out.print('.');
            lastLength++;
        }
    }

    /**
     * Notifies this controller that the operation begins.
     */
    @Override
    public synchronized void started() {
        int length = 0;
        final InternationalString task = getTask();
        if (task != null) {
            final String asString = task.toString(getLocale());
            out.print(asString);
            length = asString.length();
        }
        if (CR_supported) {
            carriageReturn(length);
        }
        out.flush();
        percentPosition   = length;
        lastPercent       = null;
        lastSource        = null;
        hasPrintedWarning = false;
    }

    /**
     * Notifies this controller that the operation is suspended.
     */
    @Override
    public synchronized void paused() {
        final String message = Vocabulary.getResources(getLocale()).getString(Vocabulary.Keys.Paused);
        out.print(message);
        carriageReturn(message.length());
    }

    /**
     * Notifies this controller that the operation is resumed.
     */
    @Override
    public synchronized void resumed() {
        final String message = Vocabulary.getResources(getLocale()).getString(Vocabulary.Keys.Resumed);
        out.print(message);
        carriageReturn(message.length());
    }

    /**
     * Notifies this controller of progress in the lengthly operation. Progress are reported
     * as a value between 0 and 100 inclusive. Values out of bounds will be clamped.
     *
     * @param percent The progress as a value between 0 and 100 inclusive.
     */
    @Override
    public synchronized void setProgress(float percent) {
        if (percent < 0  ) percent = 0;
        if (percent > 100) percent = 100;
        if (CR_supported) {
            /*
             * Si le périphérique de sortie supporte les retours chariot,
             * on écrira l'état d'avancement comme un pourcentage après
             * la description, comme dans "Lecture des données (38%)".
             */
            if (lastPercent == null || percent != super.getProgress()) {
                if (format == null) {
                    format = NumberFormat.getPercentInstance();
                }
                final String text = format.format(percent / 100.0);
                if (!text.equals(lastPercent)) {
                    int length = text.length();
                    percentPosition = 0;
                    final InternationalString task = getTask();
                    if (task != null) {
                        final String asString = task.toString(getLocale());
                        out.print(asString);
                        out.print(' ');
                        length += (percentPosition = asString.length()) + 1;
                    }
                    out.print('(');
                    out.print(text);
                    out.print(')');
                    length += 2;
                    carriageReturn(length);
                    lastPercent = text;
                }
                super.setProgress(percent);
            }
        } else {
            /*
             * Si le périphérique ne supporte par les retours chariots, on
             * écrira l'état d'avancement comme une série de points placés
             * après la description, comme dans "Lecture des données......"
             */
            completeBar(percent);
            super.setProgress(percent);
            out.flush();
        }
    }

    /**
     * Prints a warning. The first time this method is invoked, the localized word "WARNING" will
     * be printed in the middle of a box. If a source is specified, it will be printed only if it
     * is not the same one than the source of the last warning. If a margin is specified, it will
     * be printed of the left side of the first line of the warning message.
     *
     * @param source The source of the warning, or {@code null} if none. This is typically the
     *        filename in process of being parsed.
     * @param margin Text to write on the left side of the warning message, or {@code null} if none.
     *        This is typically the line number where the error occurred in the {@code source} file.
     * @param warning The warning message. If this string is longer than the maximal length
     *        specified at construction time (80 characters by default), then it will be splitted
     *        in as many lines as needed and indented according the margin width.
     */
    @Override
    public synchronized void warningOccurred(final String source, String margin, final String warning) {
        carriageReturn(0);
        final Locale locale = getLocale();
        if (!hasPrintedWarning) {
            printInBox(Vocabulary.getResources(locale).getString(Vocabulary.Keys.Warning));
            hasPrintedWarning = true;
        }
        if (!Objects.equals(source, lastSource)) {
            out.println();
            out.println(source != null ? source : Vocabulary.getResources(locale).getString(Vocabulary.Keys.Untitled));
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
            if (!margin.isEmpty()) {
                prefix = prefix + '(' + margin + ") ";
                second = CharSequences.spaces(prefix.length()).toString();
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
        final InternationalString task = getTask();
        if (!CR_supported && task != null) {
            out.print(task.toString(locale));
            completeBar(super.getProgress());
        }
        out.flush();
    }

    /**
     * Prints an exception stack trace in a box.
     */
    @Override
    public synchronized void exceptionOccurred(final Throwable exception) {
        carriageReturn(0);
        printInBox(Vocabulary.getResources(getLocale()).getString(Vocabulary.Keys.Exception));
        exception.printStackTrace(out);
        hasPrintedWarning = false;
        out.flush();
    }

    /**
     * Returns the {@code margin} string without parenthesis.
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
     * Writes the given text in a box. The given string shall be a single line.
     * The box dimension will be adjusted automatically.
     */
    private void printInBox(String text) {
        int length = text.length();
        for (int pass=-2; pass<=2; pass++) {
            switch (Math.abs(pass)) {
                case 2: {
                    for (int j=-10; j<length; j++) {
                        out.print('*');
                    }
                    out.println();
                    break;
                }
                case 1: {
                    out.print("**");
                    out.print(CharSequences.spaces(length + 6));
                    out.println("**");
                    break;
                }
                case 0: {
                    out.print("**   ");
                    out.print(text);
                    out.println("   **");
                    break;
                }
            }
        }
    }

    /**
     * Notifies this listener that the operation has finished. The progress indicator will
     * shows 100% or disappears. If warning messages were pending, they will be printed now.
     */
    @Override
    public synchronized void completed() {
        if (!CR_supported) {
            completeBar(100);
        }
        carriageReturn(0);
        out.flush();
    }
}
