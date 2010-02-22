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

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.Strings;


/**
 * Utility methods working on {@link String} or {@link CharSequence}. Some methods duplicate
 * functionalities already provided in {@code String}, but working on {@code CharSequence}.
 * This avoid the need to convert a {@code CharSequence} to a {@code String} for some simple
 * tasks that do not really need such conversion.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
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
     * Counts the number of occurence of the given character.
     *
     * @param  text The text in which to count the number of occurence.
     * @param  c The character to count.
     * @return The number of occurences of the given character.
     *
     * @since 3.03
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static int count(final CharSequence text, final char c) {
        return Strings.count(text, c);
    }

    /**
     * Replaces every occurences of the given string in the given buffer.
     *
     * @param buffer The string in which to perform the replacements.
     * @param target The string to replace.
     * @param replacement The replacement for the target string.
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static void replace(final StringBuilder buffer, final String target, final String replacement) {
        Strings.replace(buffer, target, replacement);
    }

    /**
     * Removes every occurences of the given string in the given buffer.
     *
     * @param buffer The string in which to perform the removals.
     * @param toRemove The string to remove.
     *
     * @since 3.06
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static void remove(final StringBuilder buffer, final String toRemove) {
        Strings.remove(buffer, toRemove);
    }

    /**
     * Reformats a multi-line text as a single line text. More specifically, for each
     * occurence of line feed (the {@code '\n'} character) found in the given buffer,
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
     * Trims the fractional part of the given string, provided that it doesn't change the value.
     * More specifically, this method removes the trailing {@code ".0"} characters if any. This
     * method is invoked before to parse an integer or to parse a date (for omitting fractional
     * seconds).
     *
     * @param  value The value to trim.
     * @return The value without the trailing {@code ".0"} part.
     *
     * @since 3.06
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static String trimFractionalPart(String value) {
        value = value.trim();
        return Strings.trimFractionalPart(value);
    }

    /**
     * If the given buffer ends with {@code ".0"}, removes those trailling characters.
     * This method can be invoked immediately after a double value has been formatted
     * in the buffer.
     *
     * @param buffer The buffer to trim.
     *
     * @since 3.09
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static void trimTrailingZero(final StringBuilder buffer) {
        final int length = buffer.length() - 2;
        if (length >= 0 && buffer.charAt(length) == '.' && buffer.charAt(length+1) == '0') {
            buffer.setLength(length);
        }
    }

    /**
     * Returns the given identifier (e.g. a class name) with spaces inserted after words.
     * A word begin with a upper-case character following a lower-case character. For
     * example if the given identifier is {@code "PixelInterleavedSampleModel"}, then this
     * method returns {@code "Pixel interleaved sample model"}.
     *
     * @param  identifier An identifier with no space, words begin with an upper-case character.
     * @return The identifier with spaces inserted after what looks like words.
     *
     * @since 3.03
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static StringBuilder separateWords(final CharSequence identifier) {
        return Strings.camelCaseToWords(identifier, true);
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
        final StringBuilder buffer = separateWords(identifier);
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
     * Returns the acronym of the given text, or the test itself if it already looks like
     * an acronym. Texts that are all upper-case are considered acronyms.
     *
     * @param  text The text for which to create an acronym, or {@code null}.
     * @return The acronym, or {@code null} if the given text was null.
     *
     * @since 3.07
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static String acronym(String text) {
        return Strings.camelCaseToAcronym(text);
    }

    /**
     * Returns {@code true} if the second string is an acronym of the first string. An
     * acronym is a sequence of {@linkplain Character#isLetterOrDigit letters or digits}
     * built from at least one character of each word in the complete group. More than
     * one character from the same word may appear in the acronym, but they must always
     * be the first consecutive characters.
     * <p>
     * <b>Example:</b> Given the string {@code "Open Geospatial Consortium"}, the following
     * strings are recognized as acronym: {@code "OGC"}, {@code "O.G.C."}, {@code "OpGeoCon"}.
     * The comparison is case-insensitive.
     *
     * @param  complete The complete string.
     * @param  acronym A possible acronym of the complete string.
     * @return {@code true} if the second string is an acronym of the first one.
     *
     * @since 3.03
     *
     * @deprecated Moved to the {@link Strings} class. WARNING: Argument order is swapped.
     */
    @Deprecated
    public static boolean equalsAcronym(final CharSequence complete, final CharSequence acronym) {
        return Strings.isAcronymForWords(acronym, complete);
    }

    /**
     * Returns {@code true} if the two given strings are equal, ignoring case. This method assumes
     * an ASCII character set, which is okay for simple needs like checking for a SQL keyword. For
     * comparaison that are valide in a wider range of Unicode character set, use the Java {@link
     * String#equalsIgnoreCase} method instead.
     *
     * @param  s1 The first string to compare.
     * @param  s2 The second string to compare.
     * @return {@code true} if the two given strings are equal, ignoring case.
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static boolean equalsIgnoreCase(final CharSequence s1, final CharSequence s2) {
        return Strings.equalsIgnoreCase(s1, s2);
    }

    /**
     * Returns {@code true} if the given string at the given offset contains the given part.
     *
     * @param string The string for which to tests for the presense of {@code part}.
     * @param offset The offset at which {@code part} is to be tested.
     * @param part   The part which may be present in {@code string}.
     * @return {@code true} if {@code string} contains {@code part} at the given {@code offset}.
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static boolean regionMatches(final CharSequence string, final int offset, final CharSequence part) {
        return Strings.regionMatches(string, offset, part);
    }

    /**
     * Returns {@code true} if every characters in the given character sequence are
     * {@linkplain Character#isUpperCase(char) upper-case}.
     *
     * @param  text The character sequence to test.
     * @return {@code true} if every character are upper-case.
     *
     * @since 3.07
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static boolean isUpperCase(final CharSequence text) {
        return Strings.isUpperCase(text);
    }

    /**
     * Returns the leading part which is common in to the two given strings.
     * If one of those string is {@code null}, then the other string is returned.
     *
     * @param s1 The first string, or {@code null}.
     * @param s2 The second string, or {@code null}.
     * @return The common prefix of both strings, or {@code null} if both strings are null.
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static String commonPrefix(final String s1, final String s2) {
        return Strings.commonPrefix(s1, s2);
    }

    /**
     * Returns the trailing part which is common in to the two given strings.
     * If one of those string is {@code null}, then the other string is returned.
     *
     * @param s1 The first string, or {@code null}.
     * @param s2 The second string, or {@code null}.
     * @return The common suffix of both strings, or {@code null} if both strings are null.
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static String commonSuffix(final String s1, final String s2) {
        return Strings.commonSuffix(s1, s2);
    }

    /**
     * Returns {@code true} if the given character sequence starts with the given prefix.
     *
     * @param sequence    The sequence to test.
     * @param prefix      The expected prefix.
     * @param ignoreCase  {@code true} if the case should be ignored.
     * @return {@code true} if the given sequence starts with the given prefix.
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static boolean startsWith(final CharSequence sequence, final CharSequence prefix, final boolean ignoreCase) {
        return Strings.startsWith(sequence, prefix, ignoreCase);
    }

    /**
     * Returns {@code true} if the given character sequence ends with the given suffix.
     *
     * @param sequence    The sequence to test.
     * @param suffix      The expected suffix.
     * @param ignoreCase  {@code true} if the case should be ignored.
     * @return {@code true} if the given sequence ends with the given suffix.
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static boolean endsWith(final CharSequence sequence, final CharSequence suffix, final boolean ignoreCase) {
        return Strings.endsWith(sequence, suffix, ignoreCase);
    }

    /**
     * Returns the index of the first character after the given number of lines.
     * This method counts the number of occurence of {@code '\n'}, {@code '\r'}
     * or {@code "\r\n"}. When {@code numToSkip} occurences have been found, the
     * index of the first character after the last occurence is returned.
     *
     * @param string    The string in which to skip a determined amount of lines.
     * @param numToSkip The number of lines to skip. Can be positive, zero or negative.
     * @param startAt   Index at which to start the search.
     * @return Index of the first character after the last skipped line.
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static int skipLines(final CharSequence string, int numToSkip, int startAt) {
        return Strings.skipLines(string, numToSkip, startAt);
    }

    /**
     * Splits a multi-lines string. Each element in the returned array will be a single line.
     * If the given text is already a single line, then this method returns a singleton which
     * contain the given text
     *
     * @param  text The text to split.
     * @return The lines in the text, or {@code null} if the given text was null.
     *
     * @deprecated Moved to the {@link Strings} class.
     */
    @Deprecated
    public static String[] splitLines(final String text) {
        return Strings.getLinesFromMultilines(text);
    }
}
