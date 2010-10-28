/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal;

import java.text.NumberFormat;
import java.text.DecimalFormat;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.Strings;


/**
 * Utility methods working on {@link String} or {@link CharSequence}. Some methods duplicate
 * functionalities already provided in {@code String}, but working on {@code CharSequence}.
 * This avoid the need to convert a {@code CharSequence} to a {@code String} for some simple
 * tasks that do not really need such conversion.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.00
 * @module
 */
@Static
public final class StringUtilities {
    /**
     * Do not allow instantiation of this class.
     */
    private StringUtilities() {
    }

    /**
     * Reformats a multi-line text as a single line text. More specifically, for each
     * occurrence of line feed (the {@code '\n'} character) found in the given buffer,
     * this method performs the following steps:
     * <p>
     * <ol>
     *   <li>Remove the line feed character and the {@linkplain Character#isWhitespace(char)
     *       white spaces} around them.</li>
     *   <li>If the last character before the line feed and the first character after the
     *       line feed are both {@linkplain Character#isLetterOrDigit(char) letter or digit},
     *       then a space will be inserted between them. Otherwise they will be no space.</li>
     * </ol>
     *
     * @param buffer The string in which to perform the removal.
     */
    public static void removeLF(final StringBuilder buffer) {
        int i = buffer.length();
        while ((i = buffer.lastIndexOf("\n", i)) >= 0) {
            final int length = buffer.length();
            int nld = 0;
            int upper = i;
            while (++upper < length) {
                final char c = buffer.charAt(upper);
                if (!Character.isWhitespace(c)) {
                    if (Character.isLetterOrDigit(c)) {
                        nld++;
                    }
                    break;
                }
            }
            while (i != 0) {
                final char c = buffer.charAt(--i);
                if (!Character.isWhitespace(c)) {
                    if (Character.isLetterOrDigit(c)) {
                        nld++;
                    }
                    i++;
                    break;
                }
            }
            if (nld == 2) {
                upper--;
            }
            buffer.delete(i, upper);
            if (nld == 2) {
                buffer.setCharAt(i, ' ');
            }
        }
    }

    /**
     * Makes a sentence from the given identifier. This methods performs the following steps:
     *
     * <ol>
     *   <li><p>Invoke {@link #separateWords(CharSequence)}, which separate the words on
     *     the basis of character case. For example {@code "transfertFunctionType"} become
     *     {@code "transfert function type"}. This works fine for ISO 19115 naming.</p></li>
     *
     *   <li><p>Next this method replaces {@code '_'} by a space in order to take in account
     *     the other naming convention, which is to use {@code '_'} as a word separator as in
     *     {@code "project_name"}. This convention is used by NetCDF attributes.</p></li>
     *
     *   <li>Finally this method makes the first character an upper-case one.</li>
     * </ol>
     *
     * @param  identifier An identifier with no space, words begin with an upper-case character.
     * @return The identifier with spaces inserted after what looks like words.
     *
     * @since 3.09
     */
    public static String makeSentence(final CharSequence identifier) {
        final StringBuilder buffer = Strings.camelCaseToWords(identifier, true);
        final int length = buffer.length();
        for (int i=0; i<length; i++) {
            if (buffer.charAt(i) == '_') {
                buffer.setCharAt(i, ' ');
            }
        }
        if (length != 0) {
            buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
        }
        return buffer.toString().trim();
    }

    /**
     * Returns the token starting at the given offset in the given text. For the purpose of this
     * method, a "token" is any sequence of consecutive characters of the same type, as defined
     * below.
     * <p>
     * Let define <var>c</var> as the first non-blank character located at an index equals or
     * greater than the given offset. Then the characters that are considered of the same type
     * are:
     * <p>
     * <ul>
     *   <li>If <var>c</var> is a
     *       {@linkplain Character#isJavaIdentifierStart(char) Java identifier start},
     *       then any following character that are
     *       {@linkplain Character#isJavaIdentifierPart(char) Java identifier part}.</li>
     *   <li>Otherwise any character for which {@link Character#getType(char)} returns
     *       the same value than for <var>c</var>.</li>
     * </ul>
     *
     * @param  text The text for which to get the token.
     * @param  offset Index of the fist character to consider in the given text.
     * @return A sub-sequence of {@code text} starting at the given offset, or an empty string
     *         if there is no non-blank character at or after the given offset.
     *
     * @since 3.06
     */
    public static CharSequence token(final CharSequence text, int offset) {
        final int length = text.length();
        int upper = offset;
        /*
         * Skip whitespaces. At the end of this loop,
         * 'c' will be the first non-blank character.
         */
        char c;
        do if (upper >= length) return "";
        while (Character.isWhitespace(c = text.charAt(upper++)));
        /*
         * Advance over all characters "of the same type".
         */
        offset = upper - 1;
        if (Character.isJavaIdentifierStart(c)) {
            while (upper<length && Character.isJavaIdentifierPart(text.charAt(upper))) {
                upper++;
            }
        } else {
            final int type = Character.getType(text.charAt(offset));
            while (upper<length && Character.getType(text.charAt(upper)) == type) {
                upper++;
            }
        }
        return text.subSequence(offset, upper);
    }

    /**
     * Returns the separator to use between numbers. Current implementation returns the coma
     * character, unless the given number already use the coma as the decimal separator.
     *
     * @param  format The format used for formatting numbers.
     * @return The character to use as a separator between numbers.
     *
     * @since 3.11
     */
    public static char getSeparator(final NumberFormat format) {
        if (format instanceof DecimalFormat) {
            final char c = ((DecimalFormat) format).getDecimalFormatSymbols().getDecimalSeparator();
            if (c == ',') {
                return ';';
            }
        }
        return ',';
    }
}
