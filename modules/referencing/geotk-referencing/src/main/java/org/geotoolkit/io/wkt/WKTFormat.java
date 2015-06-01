/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.io.wkt;

import java.util.Map;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.EOFException;
import java.text.ParseException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.apache.sis.io.wkt.Symbols;
import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.io.wkt.Convention;
import org.geotoolkit.util.Strings;
import org.apache.sis.util.CharSequences;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.geotoolkit.io.ContentFormatException;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * Parser and formatter for <cite>Well Known Text</cite> (WKT) objects. This format handles a
 * pair of {@link Parser} and {@link Formatter}, to be used by {@code parse} and {@code format}
 * methods respectively.
 * <p>
 * {@code WKTFormat} objects allow the following configuration:
 * <p>
 * <ul>
 *   <li>The {@linkplain Symbols symbols} to use (curly braces or brackets, <i>etc.</i>)</li>
 *   <li>The preferred authority of {@linkplain IdentifiedObject#getName() object name} to
 *       format (see {@link Formatter#getName(IdentifiedObject)} for more information)</li>
 *   <li>Whatever ANSI X3.64 colors are allowed or not (default is not)</li>
 *   <li>The indentation</li>
 * </ul>
 *
 * {@section String expansion}
 * Because the strings to be parsed by this class are long and tend to contain repetitive
 * substrings, {@code WKTFormat} provides a mechanism for performing string substitutions
 * before the parsing take place. Long strings can be assigned short names by calls to the
 * <code>{@linkplain #definitions()}.put(<var>key</var>,<var>value</var>)</code> method.
 * After definitions have been added, any call to a parsing method will replace all occurrences
 * of a short name by the associated long string.
 * <p>
 * The short names must comply with the rules of Java identifiers. It is recommended, but not
 * required, to prefix the names by some symbol like {@code "$"} in order to avoid ambiguity.
 * Note however that this class doesn't replace occurrences between quoted text, so string
 * expansion still relatively safe even when used with non-prefixed identifiers.
 * <p>
 * In the example below, the {@code $WGS84} substring which appear in the argument given to the
 * {@code parseObject} method will be expanded into the full {@code GEOGCS["WGS84", ...]} string
 * before the parsing proceed.
 *
 * <blockquote><code>
 * {@linkplain #definitions()}.put("$WGS84",
 * "GEOGCS[\"WGS84\", DATUM[</code> ...<i>etc</i>... <code>]]);<br>
 * Object crs = {@linkplain #parseObject(String) parseObject}("PROJCS[\"Mercator_1SP\",
 * <strong>$WGS84</strong>, PROJECTION[</code> ...<i>etc</i>... <code>]]");
 * </code></blockquote>
 *
 * {@section Synchronization}
 * {@code WKTFormat}s are not synchronized. It is recommended to create separate format instances
 * for each thread. If multiple threads access a format concurrently, it must be synchronized
 * externally.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rémi Eve (IRD)
 * @version 3.20
 *
 * @since 3.00
 * @module
 */
public class WKTFormat extends org.apache.sis.io.wkt.WKTFormat {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -2909110214650709560L;

    /**
     * The mapping between WKT element name and the object class to be created.
     * Keys must be upper case.
     */
    private static final Map<String,Class<?>> TYPES;
    static {
        final Map<String,Class<?>> map = new LinkedHashMap<>(25);
        map.put(        "GEOGCS",        GeographicCRS.class);
        map.put(        "PROJCS",         ProjectedCRS.class);
        map.put(        "GEOCCS",        GeocentricCRS.class);
        map.put(       "VERT_CS",          VerticalCRS.class);
        map.put(      "LOCAL_CS",       EngineeringCRS.class);
        map.put(      "COMPD_CS",          CompoundCRS.class);
        map.put(     "FITTED_CS",           DerivedCRS.class);
        map.put(          "AXIS", CoordinateSystemAxis.class);
        map.put(        "PRIMEM",        PrimeMeridian.class);
        map.put(       "TOWGS84",  BursaWolfParameters.class);
        map.put(      "SPHEROID",            Ellipsoid.class);
        map.put(    "VERT_DATUM",        VerticalDatum.class);
        map.put(   "LOCAL_DATUM",     EngineeringDatum.class);
        map.put(         "DATUM",        GeodeticDatum.class);
        map.put(      "PARAM_MT",        MathTransform.class);
        map.put(     "CONCAT_MT",        MathTransform.class);
        map.put(    "INVERSE_MT",        MathTransform.class);
        map.put("PASSTHROUGH_MT",        MathTransform.class);
        TYPES = map;
    }

    /**
     * The map of definitions. Will be created only if used.
     */
    private Definitions definitions;

    /**
     * Constructs a format using the default factories.
     */
    public WKTFormat() {
        super(null, null);
        super.setConvention(Convention.WKT1);
    }

    /**
     * Sets the symbols used for parsing and formatting WKT.
     *
     * @param symbols The new set of symbols to use for parsing and formatting WKT.
     */
    @Override
    public void setSymbols(final Symbols symbols) {
        final Symbols old = super.getSymbols();
        super.setSymbols(symbols);
        if (!symbols.equals(old)) {
            if (definitions != null) {
                definitions.quote = (char) symbols.getOpeningQuote(0); // TODO (need also close quote).
            }
        }
    }

    /**
     * Returns a map of short identifiers to substitute by WKT string before parsing. See the
     * "<cite>String expansion</cite>" section in <a href="#skip-navbar_top">class javadoc</a>
     * for details.
     * <p>
     * Entries added in the definitions map will have immediate effect in this {@code WKTFormat}
     * object. They must obey the following constraints:
     * <p>
     * <ul>
     *   <li>Keys must be valid identifiers according Java rules.</li>
     *   <li>Values must be valid WKT strings - they will be parsed.</li>
     * </ul>
     * <p>
     * Any attempt to put an illegal key or value in the definitions map will result in
     * an {@link IllegalArgumentException} being thrown.
     *
     * @return A live map of (<var>identifiers</var>, <var>WKT</var>) entries.
     */
    public Map<String,String> definitions() {
        if (definitions == null) {
            definitions = new Definitions(this);
            definitions.quote = (char) getSymbols().getOpeningQuote(0); // TODO (need also close quote).
        }
        return definitions;
    }

    /**
     * Prints to the specified stream a table of all {@linkplain #definitions() definitions}.
     * The table content is inferred from the values added to the definitions map. This method
     * does nothing if the definitions map is empty.
     *
     * @param  out writer The output stream where to write the table.
     * @throws IOException if an error occurred while writing to the output stream.
     */
    public void printDefinitions(final Writer out) throws IOException {
        if (!isNullOrEmpty(definitions)) {
            definitions.print(out, getColors() != null);
        }
    }

    /**
     * Parses the specified text and ensures that the resulting object is of the specified type.
     * If the given text is a valid identifier and this identifier was registered in the
     * {@linkplain #definitions() definitions} map, then the associated object will be returned.
     * Otherwise the given text is parsed as a WKT.
     *
     * @param  <T>  The expected type of the object to be parsed.
     * @param  text The WKT to parse, or an identifier given to the
     *         {@linkplain #definitions() definitions} map.
     * @param  offset The index of the first character to parse in the given text. This
     *         information is explicitly given instead than expecting the caller to compute
     *         {@code text.substring(offset)} in order to provide more accurate error offset
     *         in case of {@link ParseException}.
     * @param  type The expected type of the object to be parsed (usually a
     *         <code>{@linkplain CoordinateReferenceSystem}.class</code> or
     *         <code>{@linkplain MathTransform}.class</code>).
     * @return The parsed object.
     * @throws ParseException if the string can't be parsed.
     */
    public <T> T parse(String text, final int offset, final Class<T> type) throws ParseException {
        Object value;
        text = text.substring(offset);
        if (Strings.isJavaIdentifier(text)) {
            if (definitions == null || (value = definitions.getParsed(text)) == null) {
                throw new ParseException(Errors.format(
                        Errors.Keys.NO_SUCH_AUTHORITY_CODE_2, type, text), 0);
            }
        } else {
            if (definitions != null) {
                text = definitions.substitute(text);
            }
            try {
                value = parseObject(text);
            } catch (ParseException exception) {
                if (definitions != null) {
                    exception = definitions.adjustErrorOffset(exception, offset);
                }
                throw exception;
            }
        }
        final Class<?> actualType = value.getClass();
        if (type.isAssignableFrom(actualType)) {
            return type.cast(value);
        }
        throw new ParseException(Errors.format(
                Errors.Keys.ILLEGAL_CLASS_2, actualType, type), 0);
    }

    /**
     * Reads WKT strings from an input stream and reformats them to the specified
     * output stream. WKT strings are read until the the end-of-stream, or until
     * an unparsable WKT has been hit. In this later case, an error message is
     * formatted to the specified error stream.
     * <p>
     * This method is useful for {@linkplain #setIndentation changing the indentation}, rewriting
     * the WKT using parameter names specified by a {@link #setNameAuthority different authority},
     * for {@linkplain #setColors adding syntax coloring}, expanding the WKT strings according the
     * {@linkplain #definitions() definitions}, <i>etc.</i>
     *
     * @param  in  The input stream.
     * @param  out The output stream.
     * @param  err The error stream.
     * @throws IOException if an error occurred while reading from the input stream
     *         or writing to the output stream.
     */
    public void reformat(final Reader in, final Writer out, final PrintWriter err)
            throws IOException
    {
        final StringBuilder buffer = new StringBuilder();
        final String lineSeparator = System.lineSeparator();
        final Symbols symbols      = getSymbols();
        final int[] bracketCount   = new int[symbols.getNumPairedBrackets()];
        while (true) {
            /*
             * Skips whitespaces, stopping the method if EOF is reached.
             */
            char c;
            do {
                final int ci = in.read();
                if (ci < 0) {
                    return;
                }
                c = (char) ci;
            } while (Character.isWhitespace(c));
            /*
             * Copies next characters until the first opening bracket. Count the bracket
             * that we open, and stop copying characters after we have closed all of them.
             */
            buffer.setLength(0);
copy:       while (true) {
                final int ci = in.read();
                if (ci < 0) {
                    throw new EOFException(Errors.format(Errors.Keys.UNEXPECTED_END_OF_STRING));
                }
                c = (char) ci;
                buffer.append(c);
                for (int i=0; i<bracketCount.length; i++) {
                    if (c == symbols.getOpeningBracket(i)) {
                        bracketCount[i]++;
                        continue copy;
                    }
                }
                for (int i=0; i<bracketCount.length; i++) {
                    final int closingBracket = symbols.getClosingBracket(i);
                    if (c == closingBracket) {
                        if (--bracketCount[i] < 0) {
                            throw new ContentFormatException(Errors.format(
                                    Errors.Keys.NON_EQUILIBRATED_PARENTHESIS_2,
                                    closingBracket, symbols.getOpeningBracket(i)));
                        }
                        for (i=0; i<bracketCount.length; i++) { // NOSONAR: The outer loop will not continue.
                            if (bracketCount[i] != 0) {
                                continue copy;
                            }
                        }
                        break copy;
                    }
                }
            }
            /*
             * Now parses and reformats the WKT.
             */
            final String wkt = buffer.toString();
            final Object object;
            try {
                object = parseObject(wkt);
            } catch (ParseException exception) {
                err.println(exception.getLocalizedMessage());
                reportError(err, wkt, exception.getErrorOffset());
                continue;
            } catch (InvalidParameterValueException exception) {
                err.print(Errors.format(Errors.Keys.IN_1, exception.getParameterName()));
                err.print(' ');
                err.println(exception.getLocalizedMessage());
                continue;
            }
            out.write(lineSeparator);
            out.write(format(object));
            out.write(lineSeparator);
            out.write(lineSeparator);
            out.flush();
        }
    }

    /**
     * Reports a failure while parsing the specified line.
     *
     * @param err  The stream where to report the failure.
     * @param line The line that failed.
     * @param errorOffset The error offset in the specified line. This is usually the
     *        value provided by {@link ParseException#getErrorOffset}.
     */
    private static void reportError(final PrintWriter err, String line, int errorOffset) {
        line = line.replace('\r', ' ').replace('\n', ' ');
        final int WINDOW_WIDTH    = 80; // Arbitrary value.
        int           stop        = line.length();
        int           base        = errorOffset - WINDOW_WIDTH/2;
        final int     baseMax     = stop - WINDOW_WIDTH;
        final boolean hasTrailing = (Math.max(base,0) < baseMax);
        if (!hasTrailing) {
            base = baseMax;
        }
        if (base < 0) {
            base = 0;
        }
        stop = Math.min(stop, base + WINDOW_WIDTH);
        if (hasTrailing) {
            stop -= 3;
        }
        if (base != 0) {
            err.print("...");
            errorOffset += 3;
            base += 3;
        }
        err.print(line.substring(base, stop));
        if (hasTrailing) {
            err.println("...");
        } else {
            err.println();
        }
        err.print(CharSequences.spaces(errorOffset - base));
        err.println('^');
    }

    /**
     * Returns the class of the specified WKT element. For example this method returns
     * <code>{@linkplain ProjectedCRS}.class</code> for element "{@code PROJCS}".
     * <p>
     * This method is the converse of {@link #getNameOf}.
     *
     * @param  element The WKT element name.
     * @return The GeoAPI class of the specified element, or {@code null} if unknown.
     */
    public static Class<?> getClassOf(String element) {
        if (element == null) {
            return null;
        }
        element = element.trim().toUpperCase(Locale.US);
        final Class<?> type = TYPES.get(element);
        assert type == null || type.equals(MathTransform.class) || element.equals(getNameOf(type)) : type;
        return type;
    }

    /**
     * Returns the WKT name of the specified object type. For example this method returns
     * "{@code PROJCS}" for type <code>{@linkplain ProjectedCRS}.class</code>.
     * <p>
     * This method is the converse of {@link #getClassOf}.
     *
     * @param type The GeoAPI class of the specified element.
     * @return The WKT element name, or {@code null} if unknown.
     */
    public static String getNameOf(final Class<?> type) {
        if (type != null) {
            for (final Map.Entry<String,Class<?>> entry : TYPES.entrySet()) {
                final Class<?> candidate = entry.getValue();
                if (candidate.isAssignableFrom(type)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}
