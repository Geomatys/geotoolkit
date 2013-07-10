/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.io;

import java.lang.reflect.Array;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Locale;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Classes;
import org.geotoolkit.util.converter.ClassFilter;
import org.geotoolkit.util.converter.ObjectConverter;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.resources.Errors;


/**
 * Parses a line of text data. This class is mostly used for parsing lines in a matrix or a table.
 * Each column may contains numbers, dates, or other objects parseable by some {@link Format}
 * implementations. The example below reads dates in the first column and numbers in all
 * remaining columns.
 *
 * {@preformat java
 *     final LineParser parser = new LineFormat(new Format[] {
 *         DateFormat.getDateTimeInstance(),
 *         NumberFormat.getNumberInstance()
 *     });
 * }
 *
 * {@code LineFormat} may be used for reading a matrix with an unknown number of columns,
 * while requiring that all lines have the same number of columns. The example below gets the
 * number of columns while reading the first line, and ensure that all subsequent lines have
 * the same number of columns. If one line violates this condition, then a {@link ParseException}
 * will be thrown. The check if performed by the {@link #getValues(double[])} method when
 * the {@code data} array is non-nul.
 *
 * {@preformat java
 *     double[] data = null;
 *     final BufferedReader in = new BufferedReader(new FileReader("MATRIX.TXT"));
 *     for (String line; (line = in.readLine()) != null;) {
 *         parser.setLine(line);
 *         data = parser.getValues(data);
 *         // ... process 'data' here ...
 *     });
 *     in.close();
 * }
 *
 * This code can work as well with dates instead of numbers. In this case, the values returned
 * will be microseconds elapsed since January 1st, 1970.
 * <p>
 * A {@link ParseException} may be thrown because a string can't be parsed, because an object
 * can't be converted into a number or because a line don't have the expected number of columns.
 * In all case, it is possible to gets the index of the first problem found using
 * {@link ParseException#getErrorOffset}.
 *
 * {@section Synchronization}
 * Instances of {@code LineFormat} are not thread-safe, since the underlying {@link Format}
 * implementations are not guaranteed to be thread-safe neither.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
public class LineFormat extends Format {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 1257759127594136266L;

    /**
     * Number of valid data in the {@link #data} array. This is the number of data
     * found last time {@link #setLine(String)} has been invoked.
     */
    private transient int count;

    /**
     * Data read last time {@link #setLine(String)} has been invoked.
     * Those data are returned by methods like {@link #getValues(float[])}.
     */
    private Object[] data;

    /**
     * Array of formats to use for parsing a line. Each format object in this array match
     * one column. For example {@code data[4]} will be parsed with {@code format[4]}. If
     * the {@link #data} array is longer than {@link #format}, then the last format is
     * reused for all remaining columns.
     */
    private final Format[] formats;

    /**
     * The {@link ParsePosition} used for specifying the substring to parse.
     */
    private transient ParsePosition position = new ParsePosition(0);

    /**
     * Index of the the first character parsed in each column. For example {@code index[0]}
     * contains the index of the first character read for {@code data[0]}, <i>etc</i>.
     * This array length must be equal to <code>{@linkplain #data}.length + 1</code>. The
     * last element will be the line length.
     */
    private int[] limits;

    /**
     * The line specified in the last call to {@link #setLine(String)}.
     */
    private String line;

    /**
     * A converter from arbitrary objects to numbers. Will be fetch only when first needed.
     * The source type will be inferred from {@code format.parse(String)} return types.
     */
    private transient ObjectConverter<?,Number> toNumber;

    /**
     * Constructs a new line parser for numbers in the default locale.
     */
    public LineFormat() {
        this(NumberFormat.getNumberInstance());
    }

    /**
     * Constructs a new line parser for numbers in the specified locale. For example
     * {@link Locale#US} may be used for reading numbers using the dot as decimal separator.
     *
     * @param locale The locale.
     */
    public LineFormat(final Locale locale) {
        this(NumberFormat.getNumberInstance(locale));
    }

    /**
     * Constructs a new line parser using the specified format for every columns.
     *
     * @param format The format to use.
     */
    public LineFormat(final Format format) {
        if (format == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_FORMAT_2, 0, 1));
        }
        this.data    = new Object[16];
        this.limits  = new int[data.length + 1];
        this.formats = new Format[] {format};
    }

    /**
     * Constructs a new line parser using the specified format objects. For example the first
     * column will be parsed using {@code formats[0]}; the second column will be parsed using
     * {@code formats[1]}, <i>etc</i>. If there is more columns than formats, then the
     * last format object is reused for all remaining columns.
     *
     * @param formats The formats to use for parsing.
     */
    public LineFormat(Format[] formats) {
        if (formats.length == 0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.EMPTY_ARRAY));
        }
        this.formats = formats = formats.clone();
        this.data    = new Object[formats.length];
        this.limits  = new int   [formats.length + 1];
        for (int i=0; i<formats.length; i++) {
            if (formats[i] == null) {
                throw new NullArgumentException(Errors.format(
                        Errors.Keys.NULL_FORMAT_2, i+1, formats.length));
            }
        }
    }

    /**
     * Returns the type of all elements to be parsed by the formats given to the constructor.
     * The default implementation infers the parent type using reflection. For example if the
     * constructor was given a {@link java.text.DateFormat} instance, then this method will
     * returns <code>{@linkplain java.util.Date}.class</code>.
     *
     * @param filter
     *          The type of colunms to include in the search, or {@code null}
     *          if no filtering should be applied. This argument is often
     *          <code>{@linkplain ClassFilter#NUMBER}.negate()</code> in
     *          order to search for base type of non-numerical columns.
     * @return
     *          The base type of columns, or {@code null} if no non-filtered column was found.
     *
     * @since 3.00
     */
    public Class<?> getElementType(final ClassFilter filter) {
        Class<?> type = null;
        final Class<?>[] arguments = new Class<?>[] {String.class};
        for (final Format format : formats) {
            final Class<?> candidate;
            try {
                candidate = format.getClass().getMethod("parse", arguments).getReturnType();
            } catch (NoSuchMethodException e) {
                /*
                 * We are going to work only with the Format.parseObject(String) method anyway.
                 * So if there is no parse(String) method, the safest approach is to give up and
                 * assume that the return type can be anything.
                 */
                return Object.class;
            }
            if (type == null) {
                type = candidate;
            } else if (candidate != null && (filter == null || filter.accepts(candidate))) {
                type = Classes.findCommonClass(type, candidate);
            }
        }
        return type;
    }

    /**
     * Clears this parser. Next call to {@link #getValueCount} will returns 0.
     */
    public void clear() {
        toNumber = null;
        line     = null;
        count    = 0;
        Arrays.fill(data, null);
    }

    /**
     * Parses the specified line. The content is immediately parsed and values
     * can be obtained using one of the {@code getValues(...)} method.
     *
     * @param  line The line to parse.
     * @return The number of elements parsed in the specified line.
     *         The same information can be obtained with {@link #getValueCount}.
     * @throws ParseException If at least one column can't be parsed.
     */
    public int setLine(final String line) throws ParseException {
        return setLine(line, 0, line.length());
    }

    /**
     * Parses a substring of the specified line. The content is immediately parsed
     * and values can be obtained using one of the {@code getValues(...)} method.
     *
     * @param  line  The line to parse.
     * @param  lower Index of the first character in {@code line} to parse.
     * @param  upper Index after the last character in {@code line} to parse.
     * @return The number of elements parsed in the specified line.
     *         The same information can be obtained with {@link #getValueCount}.
     * @throws ParseException If at least one column can't be parsed.
     */
    public int setLine(final String line, int lower, final int upper) throws ParseException {
        /*
         * Retient la ligne que l'utilisateur nous demande
         * de lire et oublie toutes les anciennes valeurs.
         */
        this.line = line;
        Arrays.fill(data, null);
        count = 0;
        /*
         * Procède au balayage de toutes les valeurs qui se trouvent sur la ligne spécifiée.
         * Le balayage s'arrêtera lorsque {@code lower} aura atteint {@code upper}.
         */
load:   while (true) {
            while (true) {
                if (lower >= upper) {
                    break load;
                }
                if (!Character.isWhitespace(line.charAt(lower))) break;
                lower++;
            }
            /*
             * Procède à la lecture de la donnée. Si la lecture échoue, on produira un message d'erreur
             * qui apparaîtra éventuellement en HTML afin de pouvoir souligner la partie fautive.
             */
            position.setIndex(lower);
            final Object datum = formats[Math.min(count, formats.length-1)].parseObject(line, position);
            final int next = position.getIndex();
            if (datum == null || next <= lower) {
                final int error = position.getErrorIndex();
                int end = error;
                while (end < upper && !Character.isWhitespace(line.charAt(end))) end++;
                throw new ParseException(Errors.format(Errors.Keys.PARSE_EXCEPTION_2,
                          line.substring(lower, end).trim(),
                          line.substring(error, Math.min(error+1, end))), error);
            }
            /*
             * Mémorise la nouvelle donnée, en agrandissant
             * l'espace réservée en mémoire si c'est nécessaire.
             */
            if (count >= data.length) {
                data   = Arrays.copyOf(data, count + Math.min(count, 256));
                limits = Arrays.copyOf(limits, data.length + 1);
            }
            limits[count] = lower;
            data[count++] = datum;
            lower = next;
        }
        limits[count] = lower;
        return count;
    }

    /**
     * Returns the number of elements found in the last line parsed by {@link #setLine(String)}.
     *
     * @return Number of elements found in the last parsed line.
     */
    public int getValueCount() {
        return count;
    }

    /**
     * Sets all values in the current line. The {@code values} argument must be an array,
     * which may be of primitive type.
     *
     * @param  values The array to set as values.
     * @throws IllegalArgumentException if {@code values} is not an array.
     *
     * @since 2.4
     */
    public void setValues(final Object values) throws IllegalArgumentException {
        final int length = Array.getLength(values);
        data   = ArraysExt.resize(data,   length);
        limits = ArraysExt.resize(limits, length + 1);
        for (int i=0; i<length; i++) {
            data[i] = Array.get(values, i);
        }
        count = length;
    }

    /**
     * Sets or adds a value to current line. The index should be in the range 0 to
     * {@link #getValueCount} inclusively. If the index is equal to {@link #getValueCount},
     * then {@code value} will be appended as a new column after existing data.
     *
     * @param  index Index of the value to add or modify.
     * @param  value The new value.
     * @throws ArrayIndexOutOfBoundsException If the index is outside the expected range.
     */
    public void setValue(final int index, final Object value) throws ArrayIndexOutOfBoundsException {
        if (index > count) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (value == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_1, "value"));
        }
        if (index == count) {
            if (index == data.length) {
                data = Arrays.copyOf(data, index + Math.min(index, 256));
                limits = Arrays.copyOf(limits, data.length + 1);
            }
            count++;
        }
        data[index] = value;
    }

    /**
     * Returns the value at the specified index. The index should be in the range
     * 0 inclusively to {@link #getValueCount} exclusively.
     *
     * @param  index Index of the value to fetch.
     * @return The value at the specified index.
     * @throws ArrayIndexOutOfBoundsException If the index is outside the expected range.
     */
    public Object getValue(final int index) throws ArrayIndexOutOfBoundsException {
        if (index < count) {
            return data[index];
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    /**
     * Returns {@code data[index]} as a number.
     *
     * @param  index Index of the value to returns.
     * @return The value as a {@link Number}.
     * @throws ParseException if the value can not be converted to a {@link Number}.
     */
    private Number getNumber(final int index) throws ParseException {
        final Object candidate = data[index];
        if (candidate instanceof Number) {
            return ((Number) candidate);
        }
        try {
            if (toNumber == null) {
                final Class<?> type = getElementType(ClassFilter.NUMBER.negate());
                toNumber = ConverterRegistry.system().converter(type, Number.class);
            }
            @SuppressWarnings({"unchecked","rawtypes"})
            final Number n = (Number) ((ObjectConverter) toNumber).convert(candidate);
            return n;
        } catch (NonconvertibleObjectException cause) {
            ParseException exception = new ParseException(Errors.format(
                    Errors.Keys.UNPARSABLE_NUMBER_1, data[index]), limits[index]);
            exception.initCause(cause);
            throw exception;
        }
    }

    /**
     * Copies all values to the specified array. This method is typically invoked after
     * {@link #setLine(String)} for fetching the values just parsed. If {@code array} is
     * null, this method creates and returns a new array with a length equal to number
     * of elements parsed. If {@code array} is not null, then this method will thrown an
     * exception if the array length is not exactly equal to the number of elements
     * parsed.
     *
     * @param  array The array to copy values into.
     * @return {@code array} if it was not null, or a new array otherwise.
     * @throws ParseException If {@code array} was not null and its length is not equal to
     *         the number of elements parsed, or if at least one element can't be parsed.
     */
    public double[] getValues(double[] array) throws ParseException {
        if (array != null) {
            checkLength(array.length);
        } else {
            array = new double[count];
        }
        for (int i=0; i<count; i++) {
            array[i] = getNumber(i).doubleValue();
        }
        return array;
    }

    /**
     * Copies all values to the specified array. This method is typically invoked after
     * {@link #setLine(String)} for fetching the values just parsed. If {@code array} is
     * null, this method creates and returns a new array with a length equal to number
     * of elements parsed. If {@code array} is not null, then this method will thrown an
     * exception if the array length is not exactly equal to the number of elements
     * parsed.
     *
     * @param  array The array to copy values into.
     * @return {@code array} if it was not null, or a new array otherwise.
     * @throws ParseException If {@code array} was not null and its length is not equal to
     *         the number of elements parsed, or if at least one element can't be parsed.
     */
    public float[] getValues(float[] array) throws ParseException {
        if (array != null) {
            checkLength(array.length);
        } else {
            array = new float[count];
        }
        for (int i=0; i<count; i++) {
            array[i] = getNumber(i).floatValue();
        }
        return array;
    }

    /**
     * Copies all values to the specified array. This method is typically invoked after
     * {@link #setLine(String)} for fetching the values just parsed. If {@code array} is
     * null, this method creates and returns a new array with a length equal to number
     * of elements parsed. If {@code array} is not null, then this method will thrown an
     * exception if the array length is not exactly equal to the number of elements
     * parsed.
     *
     * @param  array The array to copy values into.
     * @return {@code array} if it was not null, or a new array otherwise.
     * @throws ParseException If {@code array} was not null and its length is not equal to
     *         the number of elements parsed, or if at least one element can't be parsed.
     */
    public long[] getValues(long[] array) throws ParseException {
        if (array != null) {
            checkLength(array.length);
        } else {
            array = new long[count];
        }
        for (int i=0; i<count; i++) {
            final Number n = getNumber(i);
            if ((array[i] = n.longValue()) != n.doubleValue()) {
                throw notAnInteger(i);
            }
        }
        return array;
    }

    /**
     * Copies all values to the specified array. This method is typically invoked after
     * {@link #setLine(String)} for fetching the values just parsed. If {@code array} is
     * null, this method creates and returns a new array with a length equal to number
     * of elements parsed. If {@code array} is not null, then this method will thrown an
     * exception if the array length is not exactly equal to the number of elements
     * parsed.
     *
     * @param  array The array to copy values into.
     * @return {@code array} if it was not null, or a new array otherwise.
     * @throws ParseException If {@code array} was not null and its length is not equal to
     *         the number of elements parsed, or if at least one element can't be parsed.
     */
    public int[] getValues(int[] array) throws ParseException {
        if (array != null) {
            checkLength(array.length);
        } else {
            array = new int[count];
        }
        for (int i=0; i<count; i++) {
            final Number n = getNumber(i);
            if ((array[i] = n.intValue()) != n.doubleValue()) {
                throw notAnInteger(i);
            }
        }
        return array;
    }

    /**
     * Copies all values to the specified array. This method is typically invoked after
     * {@link #setLine(String)} for fetching the values just parsed. If {@code array} is
     * null, this method creates and returns a new array with a length equal to number
     * of elements parsed. If {@code array} is not null, then this method will thrown an
     * exception if the array length is not exactly equal to the number of elements
     * parsed.
     *
     * @param  array The array to copy values into.
     * @return {@code array} if it was not null, or a new array otherwise.
     * @throws ParseException If {@code array} was not null and its length is not equal to
     *         the number of elements parsed, or if at least one element can't be parsed.
     */
    public short[] getValues(short[] array) throws ParseException {
        if (array != null) {
            checkLength(array.length);
        } else {
            array = new short[count];
        }
        for (int i=0; i<count; i++) {
            final Number n = getNumber(i);
            if ((array[i] = n.shortValue()) != n.doubleValue()) {
                throw notAnInteger(i);
            }
        }
        return array;
    }

    /**
     * Copies all values to the specified array. This method is typically invoked after
     * {@link #setLine(String)} for fetching the values just parsed. If {@code array} is
     * null, this method creates and returns a new array with a length equal to number
     * of elements parsed. If {@code array} is not null, then this method will thrown an
     * exception if the array length is not exactly equal to the number of elements
     * parsed.
     *
     * @param  array The array to copy values into.
     * @return {@code array} if it was not null, or a new array otherwise.
     * @throws ParseException If {@code array} was not null and its length is not equal to
     *         the number of elements parsed, or if at least one element can't be parsed.
     */
    public byte[] getValues(byte[] array) throws ParseException {
        if (array != null) {
            checkLength(array.length);
        } else {
            array = new byte[count];
        }
        for (int i=0; i<count; i++) {
            final Number n = getNumber(i);
            if ((array[i] = n.byteValue()) != n.doubleValue()) {
                throw notAnInteger(i);
            }
        }
        return array;
    }

    /**
     * Ensures that the number of columns just parsed is equal to the number of columns expected.
     * If a mismatch is found, then an exception is thrown.
     *
     * @param  expected The expected number of columns.
     * @throws ParseException If the number of columns parsed is not equal to the number expected.
     */
    private void checkLength(final int expected) throws ParseException {
        if (count != expected) {
            final int lower = limits[Math.min(count, expected  )];
            final int upper = limits[Math.min(count, expected+1)];
            throw new ParseException(Errors.format(count<expected ?
                                     Errors.Keys.LINE_TOO_SHORT_2 : Errors.Keys.LINE_TOO_LONG_3,
                                     count, expected, line.substring(lower,upper).trim()), lower);
        }
    }

    /**
     * Creates an exception for a value not being an integer.
     *
     * @param  i The value index.
     * @return The exception.
     */
    private ParseException notAnInteger(final int i) {
        return new ParseException(Errors.format(Errors.Keys.NOT_AN_INTEGER_1,
                line.substring(limits[i], limits[i+1])), limits[i]);
    }

    /**
     * Returns a string representation of current line. All columns are formatted using
     * the {@link Format} object specified at construction time. Columns are separated
     * by tabulation.
     */
    @Override
    public String toString() {
        return toString(new StringBuffer()).toString();
    }

    /**
     * Formats a string representation of current line. All columns are formatted using
     * the {@link Format} object specified at construction time. Columns are separated
     * by tabulation.
     */
    private StringBuffer toString(StringBuffer buffer) {
        final FieldPosition field = new FieldPosition(0);
        for (int i=0; i<count; i++) {
            if (i != 0) {
                buffer.append('\t');
            }
            buffer = formats[Math.min(formats.length-1, i)].format(data[i], buffer, field);
        }
        return buffer;
    }

    /**
     * Formats an object and appends the resulting text to a given string buffer.
     * This method invokes <code>{@linkplain #setValues setValues}(values)</code>,
     * then formats all columns using the {@link Format} object specified at
     * construction time. Columns are separated by tabulation.
     *
     * @param  values   The values to format.
     * @param  position Ignored in default implementation.
     * @return All columns formatted on a single line.
     *
     * @since 2.4
     */
    @Override
    public StringBuffer format(final Object values, final StringBuffer toAppendTo,
                               final FieldPosition position)
    {
        setValues(values);
        return toString(toAppendTo);
    }

    /**
     * Returns the index of the end of the specified line.
     */
    private static int getLineEnd(final String source, int offset, final boolean s) {
        final int length = source.length();
        while (offset < length) {
            final char c = source.charAt(offset);
            if ((c == '\r' || c == '\n') == s) {
                break;
            }
            offset++;
        }
        return offset;
    }

    /**
     * Parses text from a string to produce an object.
     *
     * @param  source The text to parse.
     * @param  position The position where to begin the parsing.
     * @return The values in each column.
     *
     * @since 2.4
     */
    @Override
    public Object[] parseObject(final String source, final ParsePosition position) {
        final int lower = position.getIndex();
        final int upper = getLineEnd(source, lower, true);
        try {
            setLine(source.substring(lower, upper));
            position.setIndex(getLineEnd(source, upper, false));
            return Arrays.copyOf(data, count);
        } catch (ParseException e) {
            position.setErrorIndex(e.getErrorOffset());
            return null; // As of java.text.Format contract.
        }
    }

    /**
     * Parses text from the beginning of the given string to produce an object.
     *
     * @param  source The text to parse.
     * @return The values in each column.
     *
     * @since 2.4
     */
    @Override
    public Object[] parseObject(final String source) throws ParseException {
        setLine(source.substring(0, getLineEnd(source, 0, true)));
        return Arrays.copyOf(data, count);
    }

    /**
     * Returns a clone of this parser. In current implementation, this
     * clone is <strong>not</strong> for usage in concurrent thread,
     * since the underlying formats are not cloned.
     */
    @Override
    public LineFormat clone() {
        final LineFormat copy = (LineFormat) super.clone();
        copy.data   = data.clone();
        copy.limits = limits.clone();
        return copy;
    }

    /**
     * Invoked on serialization. Trims the data array to the minimum requested length.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        data = ArraysExt.resize(data, count);
        limits = ArraysExt.resize(limits, count + 1);
    }

    /**
     * Invoked on deserialization. Restores the fields that are inferred
     * from the serialized ones.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        count = data.length;
        position = new ParsePosition(0);
    }
}
