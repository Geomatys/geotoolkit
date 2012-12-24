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
package org.geotoolkit.math;

import java.io.Writer;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import org.apache.sis.math.StatisticsFormat;

import org.geotoolkit.io.TableWriter;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.internal.io.IOUtilities;

import static java.lang.Math.*;
import static java.lang.Double.isNaN;
import static org.geotoolkit.util.converter.Numbers.isInteger;


/**
 * Holds some statistics about a series of sample values. Given a series of sample values
 * <var>s<sub>0</sub></var>, <var>s<sub>1</sub></var>, <var>s<sub>2</sub></var>,
 * <var>s<sub>3</sub></var>..., this class computes {@linkplain #minimum minimum},
 * {@linkplain #maximum maximum}, {@linkplain #mean mean}, {@linkplain #rms root mean square}
 * and {@linkplain #standardDeviation standard deviation}. Statistics are computed on the fly
 * using the <a href="http://en.wikipedia.org/wiki/Kahan_summation_algorithm">Kahan summation
 * algorithm</a> for reducing the numerical errors; the sample values are never stored in memory.
 * <p>
 * An instance of {@code Statistics} is initially empty (i.e. all statistical values are set
 * to {@link Double#NaN NaN}). The statistics are updated every time an {@link #add(double)}
 * method is invoked with a non-{@linkplain Double#NaN NaN} value. A typical usage of this
 * class is:
 *
 * {@preformat java
 *     double[] data = new double[1000];
 *     // (Compute some data values here...)
 *
 *     Statistics stats = new Statistics();
 *     for (int i=0; i<data.length; i++) {
 *         stats.add(data[i]);
 *     }
 *     System.out.println(stats);
 * }
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @version 3.20
 *
 * @since 1.0
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.math.Statistics}.
 */
@Deprecated
public class Statistics extends org.apache.sis.math.Statistics implements Cloneable {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = -22884277805533726L;

    /**
     * Constructs an initially empty set of statistics.
     * All statistical values are initialized to {@link Double#NaN}.
     */
    public Statistics() {
        super(null);
    }

    /**
     * Returns the range of sample values. This is equivalent to <code>{@link #maximum maximum} -
     * {@link #minimum minimum}</code>, except for rounding error. If no samples were added,
     * then returns {@link Double#NaN NaN}.
     *
     * @return The range of values.
     *
     * @see #minimum
     * @see #maximum
     *
     * @deprecated Renamed {@link #span()}.
     */
    @Deprecated
    public double range() {
        return span();
    }

    /**
     * Suggests a formatter for writing a set of data described by this statistics. This method
     * configures the formatter using heuristic rules based on the range of values and their
     * standard deviation. It can be used for reasonable default formatting when the user didn't
     * specify an explicit one.
     *
     * @param  locale The locale for the formatter, or {@code null} for the default.
     * @return A proposed formatter for data described by this statistics.
     *
     * @since 3.00
     */
    public NumberFormat getNumberFormat(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault(Locale.Category.FORMAT);
        }
        return configure(null, locale);
    }

    /**
     * Configures the given formatter for writing a set of data described by this statistics.
     * This method applies the same heuristic rules than {@link #getNumberFormat(Locale)}.
     *
     * @param format The format to configure.
     *
     * @since 3.20
     */
    public void configure(final NumberFormat format) {
        ArgumentChecks.ensureNonNull("format", format);
        configure(format, null);
    }

    /**
     * Implementation of {@link #getNumberFormat(Locale)} and {@link #configure(NumberFormat)}.
     */
    private NumberFormat configure(NumberFormat format, final Locale locale) {
        final double min = minimum();
        final double max = maximum();
        final double extremum = max(abs(min), abs(max));
        if (extremum >= 1E+10 || extremum <= 1E-4) {
            /*
             * The above threshold is high so that geocentric and projected coordinates in metres
             * are not formatted with scientific notation (a threshold of 1E+7 is not enough).
             *
             * Below we arbitrarily keep 5 fraction digits (so a total of 6 digits). If this
             * choice is modified, then we should also change the number 5 below in this method.
             */
            if (format == null) {
                format = new DecimalFormat("0.00000E00", DecimalFormatSymbols.getInstance(locale));
            } else if (format instanceof DecimalFormat) {
                ((DecimalFormat) format).applyPattern("0.00000E00");
            }
        } else {
            if (format == null) {
                format = NumberFormat.getNumberInstance(locale);
            }
            /*
             * Computes a representative range of values. We take 2 standard deviations away
             * from the mean. Assuming that data have a gaussian distribution, this is 97.7%
             * of data. If the data have a uniform distribution, then this is 100% of data.
             */
            final double mean  = mean();
            final double delta = 2 * standardDeviation(true);
            final double range = Math.min(max, mean+delta) - Math.max(min, mean-delta);
            /*
             * Gets the order of magnitude of the range, as the number of decimal after the first digit
             * (negative if range < 1). We arbitrarily choose to keep 5 decimals after the first digit
             * (if this choice is modified, then we should change also the exponential notation above).
             * Note that it make a difference only if at least one decimal is a fraction digit (we don't
             * substitute integer digits by 0).
             */
            final double magnitude = floor(log10(range));
            if (!isNaN(magnitude)) {
                final int digits = max(5 - (int) magnitude, 0);
                format.setMinimumFractionDigits(digits);
                format.setMaximumFractionDigits(digits);
            }
        }
        return format;
    }

    /**
     * Returns a string representation of this statistics. This method invokes
     * {@link #toString(Locale, boolean)}  using the default locale and spaces
     * separator.
     */
    @Override
    public final String toString() {
        return toString(null, false);
    }

    /**
     * Returns a localized string representation of this statistics. This string
     * will span multiple lines, one for each statistical value. For example:
     *
     * {@preformat text
     *     Compte:      8726
     *     Minimum:    6.853
     *     Maximum:    8.259
     *     Moyenne:    7.421
     *     RMS:        7.846
     *     Écart-type: 6.489
     * }
     *
     * @param locale
     *          The locale to use for formatting the string representation,
     *          or {@code null} for the default one.
     * @param tabulations
     *          If {@code true}, then labels (e.g. "Minimum") and values (e.g. "6.853")
     *          are separated by tabulations. Otherwise, they are separated by spaces.
     * @return
     *          A string representation of this statistics object.
     *
     * @deprecated Replaced by {@link StatisticsFormat}.
     */
    @Deprecated
    public String toString(Locale locale, final boolean tabulations) {
        Locale fmtLoc = locale;
        if (locale == null) {
            locale = Locale.getDefault(Locale.Category.DISPLAY);
            fmtLoc = Locale.getDefault(Locale.Category.FORMAT);
        }
        final NumberFormat countFormat = NumberFormat.getIntegerInstance(fmtLoc);
        final NumberFormat valueFormat = getNumberFormat(fmtLoc);
        final String[] values = new String[6];
        for (int i=0; i<values.length; i++) {
            final Number value = value(i);
            final NumberFormat format = isInteger(value.getClass()) ? countFormat : valueFormat;
            values[i] = format.format(value(i));
        }
        String text = toString(locale, values);
        if (!tabulations) {
            final TableWriter buffer = new TableWriter(null, 1);
            buffer.write(text);
            buffer.setColumnAlignment(1, TableWriter.ALIGN_RIGHT);
            text = buffer.toString();
        }
        return text;
    }

    /**
     * Formats the values in the given locale. For each index <var>i</var>, the
     * {@code values[i]} element must be formatted from the value returned by the
     * {@code value(i)} method.
     */
    private static String toString(final Locale locale, final String[] values) {
        return Descriptions.getResources(locale).getString(
                Descriptions.Keys.STATISTICS_TO_STRING_$6, values);
    }

    /**
     * Returns a value suitable for the {@link Descriptions.Keys#STATISTICS_TO_STRING_$6}
     * localized resource. The two first value can be formatted as integers. The other ones
     * are numbers.
     *
     * @param  ordinal The ordinal of the value to be obtained.
     * @return The value for the given ordinal.
     */
    private Number value(final int ordinal) {
        switch (ordinal) {
            case 0:  return count();
            case 1:  return minimum();
            case 2:  return maximum();
            case 3:  return mean();
            case 4:  return rms();
            case 5:  return standardDeviation(false);
            default: throw new AssertionError(ordinal);
        }
    }

    /**
     * Prints to the {@linkplain System#out standard output stream} the given array
     * of statistics as a table. This is mostly a convenience method for debugging.
     *
     * @param  header     The column headers in the table, or {@code null} if none.
     * @param  statistics The statistics to format.
     * @param  locale     The locale, or {@code null} for the default locale.
     *
     * @since 3.00
     *
     * @deprecated Replaced by {@link StatisticsFormat}.
     */
    @Deprecated
    public static void printTable(CharSequence[] header, Statistics[] statistics, Locale locale) {
        final Writer out = IOUtilities.standardWriter();
        try {
            writeTable(out, header, statistics, locale);
        } catch (IOException e) {
            // Should never happen since we are writing to System.out.
            throw new AssertionError(e);
        }
    }

    /**
     * Formats the given array of statistics as a table.
     *
     * @param  out        Where to format the statistics table.
     * @param  header     The column headers in the table, or {@code null} if none.
     * @param  statistics The statistics to format.
     * @param  locale     The locale, or {@code null} for the default locale.
     * @throws IOException if an error occurred while writing to {@code out}.
     *
     * @since 3.00
     *
     * @deprecated Replaced by {@link StatisticsFormat}.
     */
    @Deprecated
    public static void writeTable(final Writer out, final CharSequence[] header,
            final Statistics[] statistics, final Locale locale) throws IOException
    {
        final org.apache.sis.math.Statistics[] copy;
        if (header == null) {
            copy = statistics;
        } else {
            copy = new org.apache.sis.math.Statistics[statistics.length];
            for (int i=0; i<copy.length; i++) {
                copy[i] = new org.apache.sis.math.Statistics(header[i]);
                copy[i].add(statistics[i]);
            }
        }
        final StatisticsFormat format;
        if (locale != null) {
            format = StatisticsFormat.getInstance(locale);
        } else {
            format = StatisticsFormat.getInstance();
        }
        format.setBorderWidth(1);
        format.format(copy, out);
    }

    /**
     * Returns a clone of this statistics.
     *
     * @return A clone of this statistics.
     */
    @Override
    public Statistics clone() {
        return (Statistics) super.clone();
    }
}
