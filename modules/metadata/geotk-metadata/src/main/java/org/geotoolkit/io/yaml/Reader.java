/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.io.yaml;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.text.ParseException;
import org.apache.sis.util.Numbers;
import org.apache.sis.util.Characters;
import org.apache.sis.util.CharSequences;


/**
 * Reads objects from the JSON format.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @module
 */
final class Reader {
    /**
     * The factory to use for creating objects.
     */
    private final MetadataFactory factory;

    /**
     * The text to parse.
     */
    private final CharSequence text;

    /**
     * The current parse position.
     */
    private int position;

    /**
     * Index after the last character to parse.
     */
    private final int length;

    /**
     * Creates a new reader.
     *
     * @param text The text to parse.
     */
    Reader(final CharSequence text) {
        this.factory = MetadataFactory.DEFAULT; // Fixed for now, may be configurable in a future version.
        this.text    = text;
        this.length  = text.length();
    }

    /**
     * Advances the {@linkplain #position} to the next non-ignorable character.
     * This method skips all white-spaces.
     */
    private void skipIgnorables() {
        position = CharSequences.skipLeadingWhitespaces(text, position, length);
    }

    /**
     * Skips the list separator (a coma) if present. Other characters (in
     * particular the {@code endOfSequence} character) are not skipped.
     *
     * @param  endOfSequence The character ending the sequence.
     * @return {@code true} if there is more elements in the list.
     * @throws ParseException If an illegal character is found.
     */
    private boolean skipListSeparator(final char endOfSequence) throws ParseException {
        final char c = nextTokenStart();
        if (c == ',') {
            position++; // Skip the ',' character.
            return true;
        } else if (c == endOfSequence) {
            return false;
        } else {
            throw new ParseException("Illegal character: " + c, position);
        }
    }

    /**
     * Returns the first non-whitespace character, or thrown an exception if none.
     */
    private char nextTokenStart() throws ParseException {
        skipIgnorables();
        if (position < length) {
            return text.charAt(position);
        }
        throw new ParseException("Unexpected end of text.", length);
    }

    /**
     * Returns the next token with un-escaped characters. After this method call, the
     * {@linkplain #position} has been set to the first character after the returned token.
     *
     * @param  isQuoted {@code true} if the character at the current {@linkplain #position} is {@code '"'}.
     * @return The next token.
     */
    private CharSequence nextToken(final boolean isQuoted) throws ParseException {
        int i = position;
        if (isQuoted) {
            position++; // Skip the '"' character.
            do if ((i = CharSequences.indexOf(text, '"', i+1, length)) < 0) {
                throw new ParseException("Unexpected end of text.", length);
            } while (text.charAt(i-1) == '\\');
        } else {
            while (++i < length) {
                final char c = text.charAt(i);
                if (c == ',' || c == ':' || c == '\r' || c == '\n') {
                    break;
                }
            }
        }
        CharSequence token = unescape(text, position, i);
        if (isQuoted) {
            i++;
        } else if ("null".contentEquals(token)) {
            token = null;
        }
        position = i;
        return token;
    }

    /**
     * Parses the value. This method tries to return the value as an object of the given type,
     * but the type may sometime be different (for example a {@link String} or a {@link List}).
     *
     * @param  type The value type.
     * @return The parsed value, often (but not always) as an instance of {@code type}.
     * @throws ParseException If an error occurred while parsing.
     */
    final Object parse(final Class<?> type) throws ParseException {
        skipIgnorables();
        if (position >= length) {
            return null;
        }
        final char c = text.charAt(position);
        if (c == '[') {
            /*
             * We have an array of values. Parse all values and return them in a list.
             */
            position++; // Skip the '[' character.
            final List<Object> values = new ArrayList<>();
            boolean hasNext = (nextTokenStart() != ']');
            while (hasNext) {
                values.add(parse(type));
                hasNext = skipListSeparator(']');
            }
            position++; // Skip the ']' character.
            return values;
        }
        if (c != '{') {
            /*
             * We have a string, number or boolean. Find the end of value, either by looking
             * for the closing " or by looking for the separator (comma or line separator).
             */
            Object token = nextToken(c == '"');
            if (token != null) {
                if (Number.class.isAssignableFrom(type) || type == Boolean.class) {
                    token = Numbers.valueOf(token.toString(), type);
                }
            }
            return token;
        }
        /*
         * We have an object. Parse everything until the closing bracket.
         */
        final int objectStart = position++; // Skip the '{' character.
        final Map<String,Class<?>> definition = factory.getDefinition(type);
        final Map<String,Object> properties = new HashMap<>();
        boolean hasNext = (nextTokenStart() != '}');
        while (hasNext) {
            final int entryStart = position;
            final CharSequence token = nextToken(nextTokenStart() == '"');
            if (nextTokenStart() != ':') {
                throw new ParseException("Expected \"key: value\" pair.", position);
            }
            position++; // Skip the ':' character.
            final String key = String.valueOf(token);
            Class<?> childType = definition.get(key);
            if (childType == null) {
                childType = String.class; // TODO
            }
            if (properties.put(key, parse(childType)) != null) {
                throw new ParseException("Duplicated key: \"" + key + "\".", entryStart);
            }
            hasNext = skipListSeparator('}');
        }
        position++; // Skip the '}' character.
        return factory.create(type, definition, properties, objectStart);
    }

    /**
     * Replaces the escape sequences in a subregion of the given character string.
     * This method replaces the escape sequences defined by the Java language and JSON.
     * For any unknown escape sequence, this method just removes the backslash.
     */
    static CharSequence unescape(final CharSequence text, int start, final int stop) throws ParseException {
        int i = CharSequences.indexOf(text, '\\', start, stop);
        if (i < 0) {
            return text.subSequence(start, stop);
        }
        final StringBuilder buffer = new StringBuilder(stop - start - 1);
        do {
            buffer.append(text, start, i);
            if (++i >= stop) {
                throw new ParseException("Unexpected end of text.", stop);
            }
            final char c = text.charAt(i);
            final char r;
            start = ++i;
            switch (c) {
                default:  r =  c;   break;
                case 'b': r = '\b'; break;
                case 'f': r = '\f'; break;
                case 'n': r = '\n'; break;
                case 'r': r = '\r'; break;
                case 't': r = '\t'; break;
                case 'u': {
                    final int s = Math.min(start + Writer.UNICODE_LENGTH, stop);
                    while (i < s && Characters.isHexadecimal(text.charAt(i))) i++;
                    final String u = text.subSequence(start, i).toString();
                    try {
                        r = (char) Integer.parseInt(u, 16);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Invalid character code: " + u, start);
                    }
                    start = i;
                    break;
                }
            }
            buffer.append(r);
        } while ((i = CharSequences.indexOf(text, '\\', start, stop)) >= 0);
        return buffer.append(text, start, stop).toString();
    }
}
