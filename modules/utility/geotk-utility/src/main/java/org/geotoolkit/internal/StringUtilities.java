/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal;

import java.text.NumberFormat;
import java.text.DecimalFormat;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.Strings;
import org.geotoolkit.util.converter.Classes;


/**
 * Utility methods working on {@link String} or {@link CharSequence}. Some methods duplicate
 * functionalities already provided in {@code String}, but working on {@code CharSequence}.
 * This avoid the need to convert a {@code CharSequence} to a {@code String} for some simple
 * tasks that do not really need such conversion.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 * @module
 */
@Static
public final class StringUtilities {
    /**
     * Letters in the range 00C0 (192) to 00FF (255) inclusive with their accent removed,
     * when possible.
     *
     * @since 3.18
     */
    private static final String ASCII = "AAAAAAÆCEEEEIIIIDNOOOOO*OUUUUYÞsaaaaaaæceeeeiiiionooooo/ouuuuyþy";
    // Original letters (with accent) = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ";

    /**
     * Do not allow instantiation of this class.
     */
    private StringUtilities() {
    }

    /**
     * Returns an identity string for the given value. This method returns a string similar to
     * the one returned by the default implementation of {@link Object#toString()}, except that
     * a simple class name (without package name) is used instead than the fully-qualified name.
     *
     * @param  value The object for which to get the identity string, or {@code null}.
     * @return The identity string for the given object.
     *
     * @since 3.17
     */
    public static String identity(final Object value) {
        return Classes.getShortClassName(value) + '@' + Integer.toHexString(System.identityHashCode(value));
    }

    /**
     * Replaces some unicode characters by ASCII characters on a "best effort basis".
     * Current implementation handles only the characters in the 00C0 to 00FF range,
     * inclusive.
     * <p>
     * Note that if the given character sequence is an instance of {@link StringBuilder},
     * then the replacement will be performed in-place.
     *
     * @param  text The text to scan for unicode characters to replace by ASCII characters.
     * @return The given text with substitution applied, or {@code text} if no replacement
     *         has been applied.
     *
     * @since 3.18
     */
    public static CharSequence toASCII(CharSequence text) {
        StringBuilder buffer = null;
        final int length = text.length();
        for (int i=0; i<length; i++) {
            char c = text.charAt(i);
            final int r = c - 0xC0;
            if (r >= 0 && r<ASCII.length()) {
                c = ASCII.charAt(r);
                if (buffer == null) {
                    if (text instanceof StringBuilder) {
                        buffer = (StringBuilder) text;
                    } else {
                        buffer = new StringBuilder(text);
                        text = buffer;
                    }
                }
                buffer.setCharAt(i, c);
            }
        }
        return text;
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
     *     the basis of character case. For example {@code "transferFunctionType"} become
     *     {@code "transfer function type"}. This works fine for ISO 19115 naming.</p></li>
     *
     *   <li><p>Next this method replaces {@code '_'} by a space in order to take in account
     *     the other naming convention, which is to use {@code '_'} as a word separator as in
     *     {@code "project_name"}. This convention is used by NetCDF attributes.</p></li>
     *
     *   <li>Finally this method makes the first character an upper-case one.</li>
     * </ol>
     *
     * As an exception of the above, if the given identifier contains only upper-case letters,
     * digits and the {@code '_'} character, then the identifier is returned "as is" except for
     * the {@code '_'} which are replaced by {@code '-'}. This work well for identifier like
     * {@code "UTF-8"} or {@code "ISO-LATIN-1"} for example.
     *
     * @param  identifier An identifier with no space, words begin with an upper-case character.
     * @return The identifier with spaces inserted after what looks like words.
     *
     * @since 3.09
     */
    public static String makeSentence(final CharSequence identifier) {
        if (isCode(identifier)) {
            return identifier.toString().replace('_', '-');
        }
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
     * Returns {@code true} if the given string contains only upper case letters or digits.
     * A few punctuation characters like {@code '_'} and {@code '.'} are also accepted.
     * <p>
     * This method is used for identifying character strings that are likely to be code
     * like {@code "UTF-8"} or {@code "ISO-LATIN-1"}.
     *
     * @since 3.17
     */
    private static boolean isCode(final CharSequence identifier) {
        for (int i=identifier.length(); --i>=0;) {
            final char c = identifier.charAt(i);
            if (!((c >= 'A' && c <= 'Z') || (c >= '-' && c <= ':') || c == '_')) {
                return false;
            }
        }
        return true;
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
