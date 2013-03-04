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

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.ParseException;

import org.geotoolkit.lang.Debug;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.CharSequences;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.LoggedFormat;

import static org.geotoolkit.util.collection.XCollections.isNullOrEmpty;


/**
 * An element in a <cite>Well Know Text</cite> (WKT). An {@code Element} is
 * made of {@link String}, {@link Number} and other {@link Element}. For example:
 *
 * {@preformat text
 *     PRIMEM["Greenwich", 0.0, AUTHORITY["some authority", "Greenwich"]]
 * }
 *
 * Each {@code Element} object can contain an arbitrary amount of other elements.
 * The result is a tree, which can be printed with {@link #print} for debugging
 * purpose. Elements can be pull in a <cite>first in, first out</cite> order.
 *
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @level advanced
 * @module
 */
final class Element {
    /**
     * The position where this element starts in the string to be parsed.
     */
    private final int offset;

    /**
     * Keyword of this entity. For example: {@code "PRIMEM"}.
     */
    public final String keyword;

    /**
     * An ordered list of {@link String}s, {@link Number}s and other {@link Element}s.
     * May be {@code null} if the keyword was not followed by a pair of brackets (e.g. "NORTH").
     */
    private final List<Object> list;

    /**
     * Constructs a root element.
     *
     * @param singleton The only children for this root.
     */
    Element(final Element singleton) {
        offset  = 0;
        keyword = null;
        list    = new LinkedList<Object>();
        list.add(singleton);
    }

    /**
     * Constructs a new {@code Element}.
     *
     * @param  text       The text to parse.
     * @param  position   In input, the position where to start parsing from.
     *                    In output, the first character after the separator.
     */
    Element(final Parser parser, final String text, final ParsePosition position) throws ParseException {
        /*
         * Find the first keyword in the specified string. If a keyword is found, then
         * the position is set to the index of the first character after the keyword.
         */
        int lower = position.getIndex();
        final int length = text.length();
        while (lower<length && Character.isWhitespace(text.charAt(lower))) {
            lower++;
        }
        offset = lower;
        int upper = lower;
        while (upper<length && Character.isUnicodeIdentifierPart(text.charAt(upper))) {
            upper++;
        }
        if (upper <= lower) {
            position.setErrorIndex(lower);
            throw unparsableString(text, position);
        }
        final Symbols symbols = parser.getSymbols();
        keyword = text.substring(lower, upper).toUpperCase(symbols.locale);
        position.setIndex(upper);
        /*
         * Parse the opening bracket. According CTS's specification, two characters
         * are acceptable: '[' and '('.  At the end of this method, we will require
         * the matching closing bracket. For example if the opening bracket was '[',
         * then we will require that the closing bracket is ']' and not ')'.
         */
        int bracketIndex = -1;
        do {
            if (++bracketIndex >= symbols.openingBrackets.length) {
                list = null;
                return;
            }
        }
        while (!parseOptionalSeparator(text, position, symbols.openingBrackets[bracketIndex]));
        list = new LinkedList<Object>();
        /*
         * Parse all elements inside the bracket. Elements are parsed sequentially
         * and their type are selected according their first character:
         *
         *   - If the first character is a quote, then the element is parsed as a String.
         *   - Otherwise, if the first character is a unicode identifier start, then the
         *     element is parsed as a chidren Element.
         *   - Otherwise, the element is parsed as a number.
         */
        do {
            if (position.getIndex() >= length) {
                throw missingCharacter(symbols.close, length);
            }
            //
            // Try to parse the next element as a quoted string. We will take
            // it as a string if the first non-blank character is a quote.
            //
            if (parseOptionalSeparator(text, position, symbols.quote)) {
                lower = position.getIndex();
                upper = text.indexOf(symbols.quote, lower);
                if (upper < lower) {
                    position.setErrorIndex(++lower);
                    throw missingCharacter(symbols.quote, lower);
                }
                list.add(text.substring(lower, upper).trim());
                position.setIndex(upper + 1);
                continue;
            }
            //
            // Try to parse the next element as a number. We will take it as a number if
            // the first non-blank character is not the beginning of an unicode identifier.
            //
            lower = position.getIndex();
            if (!Character.isUnicodeIdentifierStart(text.charAt(lower))) {
                final Number number = parser.parseNumber(text, position);
                if (number == null) {
                    // Do not update the error index; it is already updated by NumberFormat.
                    throw unparsableString(text, position);
                }
                list.add(number);
                continue;
            }
            //
            // Try to parse the next element as a boolean. We will take
            // it as a boolean if it is either TRUE or FALSE.
            //
            if (text.regionMatches(true, lower, "TRUE", 0, 4)) {
                position.setIndex(lower + 4);
                list.add(Boolean.TRUE);
                continue;
            }
            if (text.regionMatches(true, lower, "FALSE", 0, 5)) {
                position.setIndex(lower + 5);
                list.add(Boolean.FALSE);
                continue;
            }
            // Otherwise, add the element as a child element.
            list.add(new Element(parser, text, position));
        } while (parseOptionalSeparator(text, position, symbols.separator));
        parseSeparator(text, position, symbols.closingBrackets[bracketIndex]);
    }

    /**
     * Returns {@code true} if the next non-whitespace character is the specified separator.
     * Search is performed in string {@code text} from position {@code position}. If the
     * separator is found, then the position is set to the first character after the separator.
     * Otherwise, the position is set on the first non-blank character.
     *
     * @param  text       The text to parse.
     * @param  position   In input, the position where to start parsing from.
     *                    In output, the first character after the separator.
     * @param  separator  The character to search.
     * @return {@code true} if the next non-whitespace character is the separator,
     *         or {@code false} otherwise.
     */
    private static boolean parseOptionalSeparator(final String text, final ParsePosition position,
                                                  final char separator)
    {
        final int length = text.length();
        int index = position.getIndex();
        while (index < length) {
            final char c = text.charAt(index);
            if (Character.isWhitespace(c)) {
                index++;
                continue;
            }
            if (c == separator) {
                position.setIndex(++index);
                return true;
            }
            break;
        }
        position.setIndex(index); // MANDATORY for correct working of the constructor.
        return false;
    }

    /**
     * Moves to the next non-whitespace character and checks if this character is the
     * specified separator. If the separator is found, it is skipped. Otherwise, this
     * method thrown a {@link ParseException}.
     *
     * @param  text       The text to parse.
     * @param  position   In input, the position where to start parsing from.
     *                    In output, the first character after the separator.
     * @param  separator  The character to search.
     * @throws ParseException if the separator was not found.
     */
    private void parseSeparator(final String text, final ParsePosition position, final char separator)
            throws ParseException
    {
        if (!parseOptionalSeparator(text, position, separator)) {
            position.setErrorIndex(position.getIndex());
            throw unparsableString(text, position);
        }
    }




    //////////////////////////////////////////////////////////////////////////////////////
    ////////                                                                      ////////
    ////////    Construction of a ParseException when a string can't be parsed    ////////
    ////////                                                                      ////////
    //////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns a {@link ParseException} with the specified cause. A localized string
     * <code>"Error in <{@link #keyword}>"</code> will be prepend to the message.
     * The error index will be the starting index of this {@code Element}.
     *
     * @param  cause   The cause of the failure, or {@code null} if none.
     * @param  message The message explaining the cause of the failure, or {@code null}
     *                 for reusing the same message than {@code cause}.
     * @return The exception to be thrown.
     */
    public ParseException parseFailed(final Exception cause, String message) {
        if (message == null) {
            message = cause.getLocalizedMessage();
        }
        ParseException exception = new ParseException(complete(message), offset);
        exception = trim("parseFailed", exception);
        exception.initCause(cause);
        return exception;
    }

    /**
     * Returns a {@link ParseException} with a "Unparsable string" message. The error message
     * is built from the specified string starting at the specified position. Properties
     * {@link ParsePosition#getIndex} and {@link ParsePosition#getErrorIndex} must be accurate
     * before this method is invoked.
     *
     * @param  text The unparsable string.
     * @param  position The position in the string.
     * @return An exception with a formatted error message.
     */
    private ParseException unparsableString(final String text, final ParsePosition position) {
        final int errorIndex = position.getErrorIndex();
        String message = LoggedFormat.formatUnparsable(text, position.getIndex(), errorIndex, null);
        message = complete(message);
        return trim("unparsableString", new ParseException(message, errorIndex));
    }

    /**
     * Returns an exception saying that a character is missing.
     *
     * @param c The missing character.
     * @param position The error position.
     */
    private ParseException missingCharacter(final char c, final int position) {
        return trim("missingCharacter", new ParseException(complete(
                Errors.format(Errors.Keys.MISSING_CHARACTER_$1, Character.valueOf(c))), position));
    }

    /**
     * Returns an exception saying that a parameter is missing.
     *
     * @param key The name of the missing parameter.
     */
    private ParseException missingParameter(final String key) {
        int error = offset;
        if (keyword != null) {
            error += keyword.length();
        }
        return trim("missingParameter", new ParseException(complete(
                Errors.format(Errors.Keys.NO_PARAMETER_$1, key)), error));
    }

    /**
     * Appends a prefix {@code "Error in <keyword>: "} before the error message.
     *
     * @param  message The message to complete.
     * @return The completed message.
     */
    private String complete(String message) {
        if (keyword != null) {
            message = Errors.format(Errors.Keys.IN_$1, keyword) + ' ' + message;
        }
        return message;
    }

    /**
     * Removes the exception factory method from the stack trace. The factory is
     * not the place where the failure occurs; the error occurs in the factory's
     * caller.
     *
     * @param  factory   The name of the factory method.
     * @param  exception The exception to trim.
     * @return {@code exception} for convenience.
     */
    private static ParseException trim(final String factory, final ParseException exception) {
        StackTraceElement[] trace = exception.getStackTrace();
        if (trace!=null && trace.length!=0) {
            if (factory.equals(trace[0].getMethodName())) {
                trace = ArraysExt.remove(trace, 0, 1);
                exception.setStackTrace(trace);
            }
        }
        return exception;
    }

    /**
     * Returns {@code true} if this element is the root element. For example in a WKT like
     * {@code "GEOGCS["name", DATUM["name, ...]]"}, this is true for {@code "GEOGCS"} and
     * false for all other elements inside, like {@code "DATUM"}.
     *
     * @return {@code true} if this element is the root element.
     *
     * @since 2.3
     */
    public boolean isRoot() {
        return this.offset == 0;
    }




    //////////////////////////////////////////////////////////////////////////////////////
    ////////                                                                      ////////
    ////////    Pull elements from the tree                                       ////////
    ////////                                                                      ////////
    //////////////////////////////////////////////////////////////////////////////////////

    /**
     * Removes the next {@link Number} from the list and returns it.
     *
     * @param  key The parameter name. Used for formatting
     *         an error message if no number are found.
     * @return The next {@link Number} on the list as a {@code double}.
     * @throws ParseException if no more number is available.
     */
    public double pullDouble(final String key) throws ParseException {
        final Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object instanceof Number) {
                iterator.remove();
                return ((Number) object).doubleValue();
            }
        }
        throw missingParameter(key);
    }

    /**
     * Removes the next {@link Number} from the list and returns it
     * as an integer.
     *
     * @param  key The parameter name. Used for formatting
     *         an error message if no number are found.
     * @return The next {@link Number} on the list as an {@code int}.
     * @throws ParseException if no more number is available, or the number is not an integer.
     */
    public int pullInteger(final String key) throws ParseException {
        final Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object instanceof Number) {
                iterator.remove();
                final Number number = (Number) object;
                if (number instanceof Float || number instanceof Double) {
                    throw new ParseException(complete(Errors.format(
                            Errors.Keys.ILLEGAL_ARGUMENT_$2, key, number)), offset);
                }
                return number.intValue();
            }
        }
        throw missingParameter(key);
    }

    /**
     * Removes the next {@link Boolean} from the list and returns it.
     *
     * @param  key The parameter name. Used for formatting
     *         an error message if no boolean are found.
     * @return The next {@link Boolean} on the list as a {@code boolean}.
     * @throws ParseException if no more boolean is available.
     */
    public boolean pullBoolean(final String key) throws ParseException {
        final Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object instanceof Boolean) {
                iterator.remove();
                return ((Boolean) object).booleanValue();
            }
        }
        throw missingParameter(key);
    }

    /**
     * Removes the next {@link String} from the list and returns it.
     *
     * @param  key The parameter name. Used for formatting
     *         an error message if no number are found.
     * @return The next {@link String} on the list.
     * @throws ParseException if no more string is available.
     */
    public String pullString(final String key) throws ParseException {
        final Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object instanceof String) {
                iterator.remove();
                return (String) object;
            }
        }
        throw missingParameter(key);
    }

    /**
     * Removes the next {@link Object} from the list and returns it.
     *
     * @param  key The parameter name. Used for formatting
     *         an error message if no number are found.
     * @return The next {@link Object} on the list (never {@code null}).
     * @throws ParseException if no more object is available.
     */
    public Object pullObject(final String key) throws ParseException {
        final Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object != null) {
                iterator.remove();
                return object;
            }
        }
        throw missingParameter(key);
    }

    /**
     * Removes the next {@link Element} from the list and returns it.
     *
     * @param  key The element name (e.g. {@code "PRIMEM"}).
     * @return The next {@link Element} on the list.
     * @throws ParseException if no more element is available.
     */
    public Element pullElement(final String key) throws ParseException {
        final Element element = pullOptionalElement(key);
        if (element != null) {
            return element;
        }
        throw missingParameter(key);
    }

    /**
     * Removes the next {@link Element} from the list and returns it.
     *
     * @param  key The element name (e.g. {@code "PRIMEM"}).
     * @return The next {@link Element} on the list,
     *         or {@code null} if no more element is available.
     */
    public Element pullOptionalElement(String key) {
        key = key.toUpperCase();
        final Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object instanceof Element) {
                final Element element = (Element) object;
                if (element.list!=null && element.keyword.equals(key)) {
                    iterator.remove();
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * Removes and returns the next {@link Element} with no bracket.
     * The key is used only for only for formatting an error message.
     *
     * @param  key The parameter name. Used only for formatting an error message.
     * @return The next {@link Element} in the list, with no bracket.
     * @throws ParseException if no more void element is available.
     */
    public Element pullVoidElement(final String key) throws ParseException {
        final Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object instanceof Element) {
                final Element element = (Element) object;
                if (element.list == null) {
                    iterator.remove();
                    return element;
                }
            }
        }
        throw missingParameter(key);
    }

    /**
     * Returns the next element, or {@code null} if there is no more
     * element. The element is <strong>not</strong> removed from the list.
     *
     * @return The next element, or {@code null} if there is no more elements.
     */
    public Object peek() {
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Closes this element.
     *
     * @throws ParseException If the list still contains some unprocessed elements.
     */
    public void close() throws ParseException {
        if (!isNullOrEmpty(list)) {
            throw new ParseException(complete(Errors.format(Errors.Keys.UNEXPECTED_PARAMETER_$1,
                    list.get(0))), offset + keyword.length());
        }
    }

    /**
     * Returns the keyword. This overriding is needed for correct
     * formatting of the error message in {@link #close}.
     */
    @Override
    public String toString() {
        return keyword;
    }

    /**
     * Prints this {@code Element} as a tree.
     * This method is used for debugging purpose only.
     *
     * @param out   The output stream.
     * @param level The indentation level (usually 0).
     */
    @Debug
    public void print(final PrintWriter out, final int level) {
        final int tabWidth = 4;
        out.print(CharSequences.spaces(tabWidth * level));
        out.println(keyword);
        if (list == null) {
            return;
        }
        final int size = list.size();
        for (int j=0; j<size; j++) {
            final Object object = list.get(j);
            if (object instanceof Element) {
                ((Element) object).print(out, level+1);
            } else {
                out.print(CharSequences.spaces(tabWidth * (level+1)));
                out.println(object);
            }
        }
    }
}
