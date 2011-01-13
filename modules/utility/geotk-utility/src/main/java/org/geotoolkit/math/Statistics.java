/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import java.io.Console;
import java.io.IOException;
import java.io.Serializable;
import java.io.OutputStreamWriter;
import java.text.FieldPosition;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.opengis.util.InternationalString;

import org.geotoolkit.io.TableWriter;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.resources.Descriptions;

import static java.lang.Math.*;
import static java.lang.Double.NaN;
import static java.lang.Double.isNaN;
import static java.lang.Double.doubleToLongBits;


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
 * @version 3.00
 *
 * @since 1.0
 * @module
 */
public class Statistics implements Cloneable, Serializable {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = -22884277805533726L;

    /**
     * Valeur minimale qui aie été transmise à la méthode {@link #add(double)}.
     * Lors de la construction, ce champs est initialisé à NaN.
     */
    private double min = NaN;

    /**
     * Valeur maximale qui aie été transmise à la méthode {@link #add(double)}.
     * Lors de la construction, ce champs est initialisé à NaN.
     */
    private double max = NaN;

    /**
     * Somme de toutes les valeurs qui ont été transmises à la méthode {@link #add(double)}.
     * Lors de la construction, ce champs est initialisé à 0.
     */
    private double sum = 0;

    /**
     * Somme des carrés de toutes les valeurs qui ont été transmises à la méthode
     * {@link #add(double)}. Lors de la construction, ce champs est initialisé à 0.
     */
    private double sum2 = 0;

    /**
     * The low-order bits in last update of {@link #sum}.
     * This is used for the Kahan summation algorithm.
     */
    private transient double lowBits;

    /**
     * The low-order bits in last update of {@link #sum2}.
     * This is used for the Kahan summation algorithm.
     */
    private transient double lowBits2;

    /**
     * Nombre de données autres que NaN qui ont été transmises à la méthode
     * {@link #add(double)}. Lors de la construction, ce champs est initialisé à 0.
     */
    private int n = 0;

    /**
     * Nombre de données égales à NaN qui ont été transmises à la méthode {@link #add(double)}.
     * Les NaN sont ingorés lors du calcul des statistiques, mais on les compte quand même
     * au passage. Lors de la construction ce champs est initialisé à 0.
     */
    private int nNaN = 0;

    /**
     * Constructs an initially empty set of statistics.
     * All statistical values are initialized to {@link Double#NaN}.
     */
    public Statistics() {
    }

    /**
     * Resets the statistics to their initial {@link Double#NaN NaN} values.
     * This method reset this object state as if it was just created.
     */
    public void reset() {
        min      = NaN;
        max      = NaN;
        sum      = 0;
        sum2     = 0;
        lowBits  = 0;
        lowBits2 = 0;
        n        = 0;
        nNaN     = 0;
    }

    /**
     * Updates statistics for the specified sample. This {@code add}
     * method is usually invoked inside a {@code for} loop.
     *
     * @param sample The sample value. {@link Double#NaN NaN} values are ignored.
     *
     * @see #add(long)
     * @see #add(Statistics)
     */
    public void add(double sample) {
        /*
         * We could declare the method as strictfp, but according Java specification
         * it makes a difference only for the exponant range, not for the digits. It
         * it was making a difference for the digits as well, we would need to mark
         * this method as strictfp.
         */
        if (!isNaN(sample)) {
            // Two next lines use !(a >= b) instead than
            // (a < b) in order to take NaN in account.
            if (!(min <= sample)) min = sample;
            if (!(max >= sample)) max = sample;

            // According algebraic laws, lowBits should always been zero. But it is
            // not when using floating points with limited precision. Do not simplify!
            double y = sample + lowBits;
            lowBits = y + (sum - (sum += y));

            sample *= sample;
            y = sample + lowBits2;
            lowBits2 = y + (sum2 - (sum2 += y));

            n++;
        } else {
            nNaN++;
        }
    }

    /**
     * Updates statistics for the specified sample. This {@code add}
     * method is usually invoked inside a {@code for} loop.
     *
     * @param sample The sample value.
     *
     * @see #add(double)
     * @see #add(Statistics)
     */
    public void add(final long sample) {
        double fs = sample;
        if (!(min <= fs)) min = fs;
        if (!(max >= fs)) max = fs;

        double y = fs + lowBits;
        lowBits = y + (sum - (sum += y));

        fs *= fs;
        y = fs + lowBits2;
        lowBits2 = y + (sum2 - (sum2 += y));

        n++;
    }

    /**
     * Updates statistics with all samples from the specified {@code stats}. Invoking this
     * method is equivalent (except for rounding errors)  to invoking {@link #add(double) add}
     * for all samples that were added to {@code stats}.
     *
     * @param stats The statistics to be added to {@code this}, or {@code null} if none.
     */
    public void add(final Statistics stats) {
        if (stats != null) {
            // "if (a<b)" is equivalent to "if (!isNaN(a) && a<b)".
            if (isNaN(min) || stats.min < min) min = stats.min;
            if (isNaN(max) || stats.max > max) max = stats.max;

            double y = stats.sum + lowBits;
            lowBits = y + (sum - (sum += y)) + stats.lowBits;

            y = stats.sum2 + lowBits2;
            lowBits2 = y + (sum2 - (sum2 += y)) + stats.lowBits2;

            n    += stats.n;
            nNaN += stats.nNaN;
        }
    }

    /**
     * Returns the number of {@link Double#NaN NaN} samples.  {@code NaN} samples are
     * ignored in all other statitical computation. This method count them for information
     * purpose only.
     *
     * @return The number of NaN values.
     */
    public int countNaN() {
        return max(nNaN, 0);
    }

    /**
     * Returns the number of samples, excluding {@link Double#NaN NaN} values.
     *
     * @return The number of sample values, excluding NaN.
     */
    public int count() {
        return n;
    }

    /**
     * Returns the minimum sample value, or {@link Double#NaN NaN} if none.
     *
     * @return The minimum sample value.
     *
     * @see #maximum
     */
    public double minimum() {
        return min;
    }

    /**
     * Returns the maximum sample value, or {@link Double#NaN NaN} if none.
     *
     * @return The maximum sample value.
     *
     * @see #minimum
     */
    public double maximum() {
        return max;
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
     */
    public double range() {
        return max - min;
    }

    /**
     * Returns the sum, or 0 if none.
     *
     * @return The sum.
     *
     * @since 3.00
     */
    public double sum() {
        return sum;
    }

    /**
     * Returns the mean value, or {@link Double#NaN NaN} if none.
     *
     * @return The mean value.
     */
    public double mean() {
        return sum / n;
    }

    /**
     * Returns the root mean square, or {@link Double#NaN NaN} if none.
     *
     * @return The root mean square.
     */
    public double rms() {
        return sqrt(sum2 / n);
    }

    /**
     * Returns the standard deviation. If the sample values given to the {@code add(...)}
     * methods have a uniform distribution, then the returned value should be close to
     * <code>sqrt({@linkplain #range range}<sup>2</sup> / 12)</code>. If they have a
     * gaussian distribution (which is the most common case), then the returned value
     * is related to the <a href="http://en.wikipedia.org/wiki/Error_function">error
     * function</a>.
     * <p>
     * As a remainder, the table below gives the probability for a sample value to be
     * inside the {@linkplain #mean mean} &plusmn; <var>n</var>&times;deviation range,
     * assuming that the distribution is gaussian (first column) or assuming that the
     * distribution is uniform (second column).
     *
     * <table align=center>
     *   <tr><th>n</th><th>gaussian</th><th>uniform</th>
     *   <tr><td>&nbsp;0.5&nbsp;</td><td>&nbsp;69.1%&nbsp;</td><td>&nbsp;28.9%&nbsp;</td></tr>
     *   <tr><td>&nbsp;1.0&nbsp;</td><td>&nbsp;84.2%&nbsp;</td><td>&nbsp;57.7%&nbsp;</td></tr>
     *   <tr><td>&nbsp;1.5&nbsp;</td><td>&nbsp;93.3%&nbsp;</td><td>&nbsp;86.6%&nbsp;</td></tr>
     *   <tr><td>&nbsp;2.0&nbsp;</td><td>&nbsp;97.7%&nbsp;</td><td>&nbsp; 100%&nbsp;</td></tr>
     *   <tr><td>&nbsp;3.0&nbsp;</td><td>&nbsp;99.9%&nbsp;</td><td>&nbsp; 100%&nbsp;</td></tr>
     * </table>
     *
     * @param allPopulation
     *          {@code true} if sample values given to {@code add} methods are the totality
     *          of the population under study, or {@code false} if they are only a sampling.
     * @return
     *      The standard deviation.
     */
    public double standardDeviation(final boolean allPopulation) {
        return sqrt((sum2 - sum*sum/n) / (allPopulation ? n : n-1));
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
            locale = Locale.getDefault();
        }
        final double extremum = max(abs(min), abs(max));
        if (extremum >= 1E+10 || extremum <= 1E-4) {
            /*
             * The above threshold is high so that geocentric and projected coordinates in metres
             * are not formatted with scientific notation (a threshold of 1E+7 would be enough).
             *
             * Below we arbitrarily keep 5 fraction digits (so a total of 6 digits). If this
             * choice is modified, then we should also change the number 5 below in this method.
             */
            return new DecimalFormat("0.00000E00", DecimalFormatSymbols.getInstance(locale));
        }
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
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
     */
    public String toString(Locale locale, final boolean tabulations) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        final NumberFormat countFormat = NumberFormat.getIntegerInstance(locale);
        final NumberFormat valueFormat = getNumberFormat(locale);
        final String[] values = new String[6];
        for (int i=0; i<values.length; i++) {
            final Number value = value(i);
            final NumberFormat format = Classes.isInteger(value.getClass()) ? countFormat : valueFormat;
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
     */
    public static void printTable(CharSequence[] header, Statistics[] statistics, Locale locale) {
        final Writer out;
        final Console console = System.console();
        if (console != null) {
            out = console.writer();
        } else {
            out = new OutputStreamWriter(System.out);
        }
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
     */
    public static void writeTable(final Writer out, final CharSequence[] header,
            final Statistics[] statistics, Locale locale) throws IOException
    {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        final TableWriter table = new TableWriter(out, TableWriter.SINGLE_VERTICAL_LINE);
        table.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        if (header != null) {
            table.nextColumn();
            final int length = min(statistics.length, header.length);
            for (int i=0; i<length; i++) {
                CharSequence label = header[i];
                if (label != null) {
                    if (label instanceof InternationalString) {
                        label = ((InternationalString) label).toString(locale);
                    }
                    table.write(label.toString());
                }
                table.nextColumn();
            }
            table.nextLine();
            table.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        }
        final NumberFormat   countFormat = NumberFormat.getIntegerInstance(locale);
        final NumberFormat[] formats     = new NumberFormat[statistics.length];
        final StringBuffer   buffer      = new StringBuffer();
        final FieldPosition  dummy       = new FieldPosition(0);
        final String[]       rows        = new String[6];
        for (int j=0; j<rows.length; j++) {
            for (int i=0; i<statistics.length; i++) {
                final Statistics stats = statistics[i];
                if (j == 0) {
                    table.setColumnAlignment(i+1, TableWriter.ALIGN_RIGHT);
                    formats[i] = stats.getNumberFormat(locale);
                }
                if (i != 0) {
                    buffer.append('\t');
                }
                final Number value = stats.value(j);
                final NumberFormat format = Classes.isInteger(value.getClass()) ? countFormat : formats[i];
                format.format(value, buffer, dummy);
            }
            rows[j] = buffer.toString();
            buffer.setLength(0);
        }
        table.write(toString(locale, rows));
        for (int i=1; i<=statistics.length; i++) {
            table.setColumnAlignment(i, TableWriter.ALIGN_RIGHT);
        }
        table.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        table.flush();
    }

    /**
     * Returns a clone of this statistics.
     *
     * @return A clone of this statistics.
     */
    @Override
    public Statistics clone() {
        try {
            return (Statistics) super.clone();
        } catch (CloneNotSupportedException exception) {
            // Should not happen since we are cloneable
            throw new AssertionError(exception);
        }
    }

    /**
     * Returns a hash code value for this statistics.
     */
    @Override
    public int hashCode() {
        final long code = (doubleToLongBits(min) +
                     31 * (doubleToLongBits(max) +
                     31 * (doubleToLongBits(sum) +
                     31 * (doubleToLongBits(sum2)))));
        return (int) code ^ (int) (code >>> 32) ^ n;
    }

    /**
     * Compares this statistics with the specified object for equality.
     *
     * @param object The object to compare with.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object) {
        if (object!=null && getClass().equals(object.getClass())) {
            final Statistics cast = (Statistics) object;
            return n == cast.n &&
                   Utilities.equals(min,  cast.min) &&
                   Utilities.equals(max,  cast.max) &&
                   Utilities.equals(sum,  cast.sum) &&
                   Utilities.equals(sum2, cast.sum2);
        }
        return false;
    }

    /**
     * Holds some statistics about a series of sample values and the difference between them.
     * Given a series of sample values <var>s<sub>0</sub></var>, <var>s<sub>1</sub></var>,
     * <var>s<sub>2</sub></var>, <var>s<sub>3</sub></var>..., this class computes statistics
     * in the same way than {@link Statistics} and additionally computes statistics for
     * <var>s<sub>1</sub></var>-<var>s<sub>0</sub></var>,
     * <var>s<sub>2</sub></var>-<var>s<sub>1</sub></var>,
     * <var>s<sub>3</sub></var>-<var>s<sub>2</sub></var>...,
     * which are stored in a {@link #getDeltaStatistics delta} statistics object.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 1.0
     */
    public static class Delta extends Statistics {
        /**
         * Serial number for compatibility with different versions.
         */
        private static final long serialVersionUID = 3464306833883333219L;

        /**
         * Statistics about the differences between consecutive sample values.
         */
        private Statistics delta;

        /**
         * Last value given to an {@link #add(double) add} method as
         * a {@code double}, or {@link Double#NaN NaN} if none.
         */
        private double last = NaN;

        /**
         * Last value given to an {@link #add(long) add}
         * method as a {@code long}, or 0 if none.
         */
        private long lastAsLong;

        /**
         * Constructs an initially empty set of statistics.
         * All statistical values are initialized to {@link Double#NaN}.
         */
        public Delta() {
            delta = new Statistics();
            delta.nNaN = -1; // Do not count the first NaN, which will always be the first value.
        }

        /**
         * Constructs an initially empty set of statistics using the specified
         * object for {@link #getDeltaStatistics delta} statistics. This method
         * allows chaining different kind of statistics objects. For example, one
         * could write:
         *
         * {@preformat java
         *     new Statistics.Delta(new Statistics.Delta());
         * }
         *
         * Which would compute statistics of sample values, statistics of difference between
         * consecutive sample values, and statistics of difference of difference between
         * consecutive sample values. Other kinds of {@link Statistics} object could be
         * chained as well.
         *
         * @param delta The object where to stores delta statistics.
         */
        public Delta(final Statistics delta) {
            this.delta = delta;
            delta.reset();
            delta.nNaN = -1; // Do not count the first NaN, which will always be the first value.
        }

        /**
         * Returns the statistics about difference between consecutives values.
         * Given a series of sample values <var>s<sub>0</sub></var>, <var>s<sub>1</sub></var>,
         * <var>s<sub>2</sub></var>, <var>s<sub>3</sub></var>..., this is statistics for
         * <var>s<sub>1</sub></var>-<var>s<sub>0</sub></var>,
         * <var>s<sub>2</sub></var>-<var>s<sub>1</sub></var>,
         * <var>s<sub>3</sub></var>-<var>s<sub>2</sub></var>...,
         *
         * @return The object where delta statistics are stored.
         */
        public Statistics getDeltaStatistics() {
            return delta;
        }

        /**
         * Resets the statistics to their initial {@link Double#NaN NaN} values.
         * This method reset this object state as if it was just created.
         */
        @Override
        public void reset() {
            super.reset();
            delta.reset();
            delta.nNaN = -1; // Do not count the first NaN, which will always be the first value.
            last       = NaN;
            lastAsLong = 0;
        }

        /**
         * Updates statistics for the specified sample. The {@link #getDeltaStatistics delta}
         * statistics are updated with <code>sample - sample<sub>last</sub></code> value,
         * where <code>sample<sub>last</sub></code> is the last value given to the previous
         * call of an {@code add(...)} method.
         */
        @Override
        public void add(final double sample) {
            super.add(sample);
            delta.add(sample - last);
            last       = sample;
            lastAsLong = (long)sample;
        }

        /**
         * Updates statistics for the specified sample. The {@link #getDeltaStatistics delta}
         * statistics are updated with <code>sample - sample<sub>last</sub></code> value,
         * where <code>sample<sub>last</sub></code> is the last value given to the previous
         * call of an {@code add(...)} method.
         */
        @Override
        public void add(final long sample) {
            super.add(sample);
            if (last == (double) lastAsLong) {
                // 'lastAsLong' may have more precision than 'last' since the cast to the
                // 'double' type may loose some digits. Invoke the 'delta.add(long)' version.
                delta.add(sample - lastAsLong);
            } else {
                // The sample value is either fractional, outside 'long' range,
                // infinity or NaN. Invoke the 'delta.add(double)' version.
                delta.add(sample - last);
            }
            last       = sample;
            lastAsLong = sample;
        }

        /**
         * Update statistics with all samples from the specified {@code stats}. Invoking this
         * method is equivalent (except for rounding errors)  to invoking {@link #add(double) add}
         * for all samples that were added to {@code stats}.  The {@code stats} argument
         * must be an instance of {@code Statistics.Delta}.
         *
         * @param  stats The statistics to be added to {@code this},
         *         or {@code null} if none.
         * @throws ClassCastException If {@code stats} is not an instance of
         *         {@code Statistics.Delta}.
         */
        @Override
        public void add(final Statistics stats) throws ClassCastException {
            if (stats != null) {
                final Delta toAdd = (Delta) stats;
                if (toAdd.delta.nNaN >= 0) {
                    delta.add(toAdd.delta);
                    last       = toAdd.last;
                    lastAsLong = toAdd.lastAsLong;
                    super.add(stats);
                }
            }
        }

        /**
         * Returns a clone of this statistics.
         */
        @Override
        public Delta clone() {
            Delta copy = (Delta) super.clone();
            copy.delta = copy.delta.clone();
            return copy;
        }

        /**
         * Tests this statistics with the specified object for equality.
         */
        @Override
        public boolean equals(final Object obj) {
            return super.equals(obj) && delta.equals(((Delta) obj).delta);
        }

        /**
         * Returns a hash code value for this statistics.
         */
        @Override
        public int hashCode() {
            return super.hashCode() + 31*delta.hashCode();
        }
    }
}
