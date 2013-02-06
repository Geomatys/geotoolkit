/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util;

import org.apache.sis.util.CharSequences;
import org.apache.sis.util.StringBuilders;
import java.util.Arrays;
import org.geotoolkit.lang.Static;
import static java.lang.Character.*;


/**
 * Utility methods working on {@link String} or {@link CharSequence} instances. Some methods
 * defined in this class duplicate the functionalities already provided in the {@code String}
 * class, but works on a generic {@code CharSequence} instance instead than {@code String}.
 * Other methods perform their work directly on the provided {@link StringBuilder}.
 *
 * {@section Unicode support}
 * Every methods defined in this class work on <cite>code points</cite> instead than characters
 * when appropriate. Consequently those methods should behave correctly with characters outside
 * the <cite>Basic Multilingual Plane</cite> (BMP).
 *
 * {@section Handling of null values}
 * Some methods accept a {@code null} argument, in particular the methods converting the
 * given {@code String} to another {@code String} which may be the same. For example the
 * {@link #camelCaseToAcronym(String)} method returns {@code null} if the string to convert is
 * {@code null}. Some other methods like {@link #count(String, char)} handles {@code null}
 * argument like an empty string. The methods that don't accept a {@code null} argument are
 * explicitly documented as throwing a {@link NullPointerException}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see Arrays#toString(Object[])
 * @see XArrays#containsIgnoreCase(String[], String)
 *
 * @since 3.09 (derived from 3.00)
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link CharSequences} and {@link StringBuilders}.
 */
@Deprecated
public final class Strings extends Static {
    /**
     * An array of zero-length. This constant play a role equivalents to
     * {@link java.util.Collections#EMPTY_LIST}.
     *
     * @since 3.20
     *
     * @deprecated Moved to {@link CharSequences#EMPTY_ARRAY} in Apache SIS.
     */
    @Deprecated
    public static final String[] EMPTY = CharSequences.EMPTY_ARRAY;

    /**
     * Do not allow instantiation of this class.
     */
    private Strings() {
    }

    /**
     * Returns a string of the specified length filled with white spaces.
     * This method tries to return a pre-allocated string if possible.
     *
     * @param  length The string length. Negative values are clamped to 0.
     * @return A string of length {@code length} filled with white spaces.
     *
     * @deprecated Moved to {@link CharSequences#spaces(int)} in Apache SIS.
     */
    @Deprecated
    public static String spaces(int length) {
        return CharSequences.spaces(length).toString();
    }

    /**
     * Returns the {@linkplain CharSequence#length()} of the given characters sequence,
     * or 0 if {@code null}.
     *
     * @param  text The character sequence from which to get the length, or {@code null}.
     * @return The length of the character sequence, or 0 if the argument is {@code null}.
     *
     * @deprecated Moved to {@link CharSequences#length(CharSequence)} in Apache SIS.
     */
    @Deprecated
    public static int length(final CharSequence text) {
        return CharSequences.length(text);
    }

    /**
     * Returns the number of occurrences of the {@code toSearch} string in the given {@code text}.
     * The search is case-sensitive.
     *
     * @param  text String to search in, or {@code null}.
     * @param  toSearch The string to search in the given {@code text}.
     *         Must contain at least one character.
     * @return The number of occurrence of {@code toSearch} in {@code text},
     *         or 0 if {@code text} was null or empty.
     * @throws IllegalArgumentException If the {@code toSearch} array is null or empty.
     *
     * @since 3.11
     *
     * @deprecated Moved to {@link CharSequences#count(CharSequence, String)} in Apache SIS.
     */
    @Deprecated
    public static int count(final String text, final String toSearch) {
        return CharSequences.count(text, toSearch);
    }

    /**
     * Counts the number of occurrence of the given character in the given string. This
     * method performs the same work than {@link #count(CharSequence, char)}, but is faster.
     *
     * @param  text The text in which to count the number of occurrence.
     * @param  c The character to count, or 0 if {@code text} was null.
     * @return The number of occurrences of the given character.
     *
     * @deprecated Moved to {@link CharSequences#count(CharSequence, char)} in Apache SIS.
     */
    @Deprecated
    public static int count(final String text, final char c) {
        return CharSequences.count(text, c);
    }

    /**
     * Counts the number of occurrence of the given character in the given character sequence.
     * This method performs the same work than {@link #count(String, char)}, but on a more
     * generic interface.
     *
     * @param  text The text in which to count the number of occurrence.
     * @param  c The character to count, or 0 if {@code text} was null.
     * @return The number of occurrences of the given character.
     *
     * @deprecated Moved to {@link CharSequences#count(CharSequence, char)} in Apache SIS.
     */
    @Deprecated
    public static int count(final CharSequence text, final char c) {
        return CharSequences.count(text, c);
    }

    /**
     * Splits a string around the given character. The array returned by this method contains each
     * substring of the given string that is terminated by the given character or is terminated by
     * the end of the string. The substrings in the array are in the order in which they occur in
     * the given string. If the character is not found in the input, then the resulting array has
     * just one element, namely the given string.
     * <p>
     * This method is similar to the standard {@link String#split(String)} method except for the
     * following:
     * <p>
     * <ul>
     *   <li>It accepts a {@code null} input string, in which case an empty array is returned.</li>
     *   <li>The separator is a simple character instead than a regular expression.</li>
     *   <li>The leading and trailing spaces of each substring are {@linkplain String#trim trimmed}.</li>
     * </ul>
     *
     * @param  toSplit   The string to split, or {@code null}.
     * @param  separator The delimiting character (typically the coma).
     * @return The array of strings computed by splitting the given string around the given
     *         character, or an empty array if {@code toSplit} was null.
     *
     * @see String#split(String)
     *
     * @since 3.18
     *
     * @deprecated Moved to {@link CharSequences#split(CharSequence, char)} in Apache SIS.
     */
    @Deprecated
    public static String[] split(final String toSplit, final char separator) {
        return toString(CharSequences.split(toSplit, separator));
    }

    /**
     * {@linkplain #split(String, char) Splits} the given string around the given character,
     * then {@linkplain Double#parseDouble(String) parses} each item as a {@code double}.
     *
     * @param  values The strings containing the values to parse, or {@code null}.
     * @param  separator The delimiting character (typically the coma).
     * @return The array of numbers parsed from the given string,
     *         or an empty array if {@code values} was null.
     * @throws NumberFormatException If at least one number can not be parsed.
     *
     * @since 3.19
     *
     * @deprecated Moved to {@link CharSequences#parseDoubles(CharSequence, char)} in Apache SIS.
     */
    @Deprecated
    public static double[] parseDoubles(final String values, final char separator) throws NumberFormatException {
        return CharSequences.parseDoubles(values, separator);
    }

    /**
     * {@linkplain #split(String, char) Splits} the given string around the given character,
     * then {@linkplain Float#parseFloat(String) parses} each item as a {@code float}.
     *
     * @param  values The strings containing the values to parse, or {@code null}.
     * @param  separator The delimiting character (typically the coma).
     * @return The array of numbers parsed from the given string,
     *         or an empty array if {@code values} was null.
     * @throws NumberFormatException If at least one number can not be parsed.
     *
     * @since 3.19
     *
     * @deprecated Moved to {@link CharSequences#parseFloats(CharSequence, char)} in Apache SIS.
     */
    @Deprecated
    public static float[] parseFloats(final String values, final char separator) throws NumberFormatException {
        return CharSequences.parseFloats(values, separator);
    }

    /**
     * {@linkplain #split(String, char) Splits} the given string around the given character,
     * then {@linkplain Long#parseLong(String) parses} each item as a {@code long}.
     *
     * @param  values The strings containing the values to parse, or {@code null}.
     * @param  separator The delimiting character (typically the coma).
     * @param  radix the radix to be used for parsing. This is usually 10.
     * @return The array of numbers parsed from the given string,
     *         or an empty array if {@code values} was null.
     * @throws NumberFormatException If at least one number can not be parsed.
     *
     * @since 3.19
     *
     * @deprecated Moved to {@link CharSequences#parseLongs(CharSequence, char, int)} in Apache SIS.
     */
    @Deprecated
    public static long[] parseLongs(final String values, final char separator, final int radix) throws NumberFormatException {
        return CharSequences.parseLongs(values, separator, radix);
    }

    /**
     * {@linkplain #split(String, char) Splits} the given string around the given character,
     * then {@linkplain Integer#parseInt(String) parses} each item as an {@code int}.
     *
     * @param  values The strings containing the values to parse, or {@code null}.
     * @param  separator The delimiting character (typically the coma).
     * @param  radix the radix to be used for parsing. This is usually 10.
     * @return The array of numbers parsed from the given string,
     *         or an empty array if {@code values} was null.
     * @throws NumberFormatException If at least one number can not be parsed.
     *
     * @since 3.19
     *
     * @deprecated Moved to {@link CharSequences#parseInts(CharSequence, char, int)} in Apache SIS.
     */
    @Deprecated
    public static int[] parseInts(final String values, final char separator, final int radix) throws NumberFormatException {
        return CharSequences.parseInts(values, separator, radix);
    }

    /**
     * {@linkplain #split(String, char) Splits} the given string around the given character,
     * then {@linkplain Short#parseShort(String) parses} each item as a {@code short}.
     *
     * @param  values The strings containing the values to parse, or {@code null}.
     * @param  separator The delimiting character (typically the coma).
     * @param  radix the radix to be used for parsing. This is usually 10.
     * @return The array of numbers parsed from the given string,
     *         or an empty array if {@code values} was null.
     * @throws NumberFormatException If at least one number can not be parsed.
     *
     * @since 3.19
     *
     * @deprecated Moved to {@link CharSequences#parseShorts(CharSequence, char, int)} in Apache SIS.
     */
    @Deprecated
    public static short[] parseShorts(final String values, final char separator, final int radix) throws NumberFormatException {
        return CharSequences.parseShorts(values, separator, radix);
    }

    /**
     * {@linkplain #split(String, char) Splits} the given string around the given character,
     * then {@linkplain Byte#parseByte(String) parses} each item as a {@code byte}.
     *
     * @param  values The strings containing the values to parse, or {@code null}.
     * @param  separator The delimiting character (typically the coma).
     * @param  radix the radix to be used for parsing. This is usually 10.
     * @return The array of numbers parsed from the given string,
     *         or an empty array if {@code values} was null.
     * @throws NumberFormatException If at least one number can not be parsed.
     *
     * @since 3.19
     *
     * @deprecated Moved to {@link CharSequences#parseBytes(CharSequence, char, int)} in Apache SIS.
     */
    @Deprecated
    public static byte[] parseBytes(final String values, final char separator, final int radix) throws NumberFormatException {
        return CharSequences.parseBytes(values, separator, radix);
    }

    /**
     * Formats the given elements as a (typically) comma-separated list. This method is similar
     * to {@link java.util.AbstractCollection#toString()} or {@link Arrays#toString(Object[])}
     * except for the following:
     * <p>
     * <ul>
     *   <li>There is no leading {@code '['} and trailing {@code ']'} characters.</li>
     *   <li>Null elements are ignored instead than formatted as {@code "null"}.</li>
     *   <li>If the {@code collection} argument is null or contains only null elements,
     *       then this method returns {@code null}.</li>
     *   <li>In the common case where the collection contains a single {@link String} element,
     *       that string is returned directly (no object duplication).</li>
     * </ul>
     * <p>
     * This method is the converse of {@link #getLinesFromMultilines(String)}
     * when the separator is {@link System#lineSeparator()}.
     *
     * @param  collection The elements to format in a (typically) comma-separated list, or {@code null}.
     * @param  separator  The element separator, which is usually {@code ", "}.
     * @return The (typically) comma-separated list, or {@code null} if the given {@code collection}
     *         was null or contains only null elements.
     *
     * @since 3.20
     *
     * @deprecated Moved to {@link CharSequences#toString(Iterable, String)} in Apache SIS.
     */
    @Deprecated
    public static String formatList(final Iterable<?> collection, final String separator) {
        return CharSequences.toString(collection, separator);
    }

    /**
     * Replaces every occurrences of the given string in the given buffer.
     * This method invokes {@link StringBuilder#replace(int, int, String)}
     * for each occurrence of {@code search} found in the buffer.
     *
     * @param buffer The string in which to perform the replacements.
     * @param search The string to replace.
     * @param replacement The replacement for the target string.
     * @throws NullPointerException if any of the arguments is null.
     *
     * @see String#replace(char, char)
     * @see String#replace(CharSequence, CharSequence)
     * @see StringBuilder#replace(int, int, String)
     *
     * @deprecated Moved to {@link StringBuilders#replace(StringBuilder, String, String)} in Apache SIS.
     */
    @Deprecated
    public static void replace(final StringBuilder buffer, final String search, final String replacement) {
        StringBuilders.replace(buffer, search, replacement);
    }

    /**
     * Replaces the characters in a substring of the buffer with characters in the specified array.
     * The substring to be replaced begins at the specified {@code start} and extends to the
     * character at index {@code end - 1}.
     *
     * @param buffer The buffer in which to perform the replacement.
     * @param start  The beginning index in the {@code buffer}, inclusive.
     * @param end    The ending index in the {@code buffer}, exclusive.
     * @param chars  The array that will replace previous contents.
     * @throws NullPointerException if the {@code buffer} or {@code chars} argument is null.
     *
     * @see StringBuilder#replace(int, int, String)
     *
     * @since 3.20
     *
     * @deprecated Moved to {@link StringBuilders#replace(StringBuilder, int, int, char[])} in Apache SIS.
     */
    @Deprecated
    public static void replace(final StringBuilder buffer, int start, final int end, final char[] chars) {
        StringBuilders.replace(buffer, start, end, chars);
    }

    /**
     * Removes every occurrences of the given string in the given buffer. This method invokes
     * {@link StringBuilder#delete(int, int)} for each occurrence of {@code search} found in
     * the buffer.
     *
     * @param buffer The string in which to perform the removals.
     * @param search The string to remove.
     * @throws NullPointerException if any of the arguments is null.
     *
     * @see StringBuilder#delete(int, int)
     *
     * @deprecated Moved to {@link StringBuilders#remove(StringBuilder, String)} in Apache SIS.
     */
    @Deprecated
    public static void remove(final StringBuilder buffer, final String search) {
        StringBuilders.remove(buffer, search);
    }

    /**
     * Returns a string with leading and trailing white spaces omitted. White spaces are identified
     * by the {@link Character#isWhitespace(int)} method.
     * <p>
     * This method is similar in purpose to {@link String#trim()}, except that the later considers
     * every ASCII control codes below 32 to be a whitespace. This have the effect of removing
     * {@linkplain org.geotoolkit.io.X364 X3.64} escape sequences as well. Users should invoke
     * this {@code Strings.trim} method instead if they need to preserve X3.64 escape sequences.
     *
     * @param text The string from which to remove leading and trailing white spaces, or {@code null}.
     * @return A string with leading and trailing white spaces removed, or {@code null} is the given
     *         string was null.
     *
     * @see String#trim()
     *
     * @deprecated Moved to {@link CharSequences#trimWhitespaces(String)} in Apache SIS.
     */
    @Deprecated
    public static String trim(final String text) {
        return CharSequences.trimWhitespaces(text);
    }

    /**
     * Trims the fractional part of the given formatted number, provided that it doesn't change
     * the value. This method assumes that the number is formatted in the US locale, typically
     * by the {@link Double#toString(double)} method.
     * <p>
     * More specifically if the given string ends with a {@code '.'} character followed by a
     * sequence of {@code '0'} characters, then those characters are omitted. Otherwise this
     * method returns the string unchanged. This is a "<cite>all or nothing</cite>" method:
     * either the fractional part is completely removed, or either it is left unchanged.
     *
     * {@section Examples}
     * This method returns {@code "4"} if the given value is {@code "4."}, {@code "4.0"} or
     * {@code "4.00"}, but returns {@code "4.10"} unchanged (including the trailing {@code '0'}
     * character) if the input is {@code "4.10"}.
     *
     * {@section Use case}
     * This method is useful before to {@linkplain Integer#parseInt(String) parse a number}
     * if that number should preferably be parsed as an integer before attempting to parse
     * it as a floating point number.
     *
     * @param  value The value to trim if possible, or {@code null}.
     * @return The value without the trailing {@code ".0"} part (if any),
     *         or {@code null} if the given string was null.
     *
     * @deprecated Moved to {@link CharSequences#trimFractionalPart(CharSequence)} in Apache SIS.
     */
    @Deprecated
    public static String trimFractionalPart(final String value) {
        return toString(CharSequences.trimFractionalPart(value));
    }

    /**
     * Trims the fractional part of the given formatted number, provided that it doesn't change
     * the value. This method performs the same work than {@link #trimFractionalPart(String)}
     * except that it modifies the given buffer in-place.
     *
     * {@section Use case}
     * This method is useful after a {@linkplain StringBuilder#append(double) double value has
     * been appended to the buffer}, in order to make it appears like an integer when possible.
     *
     * @param buffer The buffer to trim if possible.
     * @throws NullPointerException if the argument is null.
     *
     * @deprecated Moved to {@link StringBuilders#trimFractionalPart(StringBuilder)} in Apache SIS.
     */
    @Deprecated
    public static void trimFractionalPart(final StringBuilder buffer) {
        StringBuilders.trimFractionalPart(buffer);
    }

    /**
     * Replaces some Unicode characters by ASCII characters on a "best effort basis".
     * For example the {@code 'é'} character is replaced by {@code 'e'} (without accent).
     * <p>
     * The current implementation replaces only the characters in the range {@code 00C0}
     * to {@code 00FF}, inclusive. Other characters are left unchanged.
     * <p>
     * Note that if the given character sequence is an instance of {@link StringBuilder},
     * then the replacement will be performed in-place.
     *
     * @param  text The text to scan for Unicode characters to replace by ASCII characters,
     *         or {@code null}.
     * @return The given text with substitution applied, or {@code text} if no replacement
     *         has been applied.
     *
     * @since 3.18
     *
     * @deprecated Moved to {@link CharSequences#toASCII(CharSequence)} in Apache SIS.
     */
    @Deprecated
    public static CharSequence toASCII(final CharSequence text) {
        if (text instanceof StringBuilder) {
            StringBuilders.toASCII((StringBuilder) text);
            return text;
        }
        return CharSequences.toASCII(text);
    }

    /**
     * Given a string in camel cases (typically a Java identifier), returns a string formatted
     * like an English sentence. This heuristic method performs the following steps:
     *
     * <ol>
     *   <li><p>Invoke {@link #camelCaseToWords(CharSequence, boolean)}, which separate the words
     *     on the basis of character case. For example {@code "transferFunctionType"} become
     *     "<cite>transfer function type</cite>". This works fine for ISO 19115 identifiers.</p></li>
     *
     *   <li><p>Next replace all occurrence of {@code '_'} by spaces in order to take in account
     *     an other common naming convention, which uses {@code '_'} as a word separator. This
     *     convention is used by NetCDF attributes like {@code "project_name"}.</p></li>
     *
     *   <li><p>Finally ensure that the first character is upper-case.</p></li>
     * </ol>
     *
     * {@section Exception to the above rules}
     * If the given identifier contains only upper-case letters, digits and the {@code '_'}
     * character, then the identifier is returned "as is" except for the {@code '_'} characters
     * which are replaced by {@code '-'}. This work well for identifiers like {@code "UTF-8"} or
     * {@code "ISO-LATIN-1"} for example.
     * <p>
     * Note that those heuristic rules may be modified in future Geotk versions,
     * depending on the practical experience gained.
     *
     * @param  identifier An identifier with no space, words begin with an upper-case character,
     *         or {@code null}.
     * @return The identifier with spaces inserted after what looks like words, or {@code null}
     *         if the given argument was null.
     *
     * @since 3.18 (derived from 3.09)
     *
     * @deprecated Moved to {@link CharSequences#camelCaseToSentence(CharSequence)} in Apache SIS.
     */
    @Deprecated
    public static String camelCaseToSentence(final CharSequence identifier) {
        return toString(CharSequences.camelCaseToSentence(identifier));
    }

    /**
     * Given a string in camel cases, returns a string with the same words separated by spaces.
     * A word begins with a upper-case character following a lower-case character. For example
     * if the given string is {@code "PixelInterleavedSampleModel"}, then this method returns
     * "<cite>Pixel Interleaved Sample Model</cite>" or "<cite>Pixel interleaved sample model</cite>"
     * depending on the value of the {@code toLowerCase} argument.
     * <p>
     * If {@code toLowerCase} is {@code false}, then this method inserts spaces but does not change
     * the case of characters. If {@code toLowerCase} is {@code true}, then this method changes
     * {@linkplain Character#toLowerCase(int) to lower case} the first character after each spaces
     * inserted by this method (note that this intentionally exclude the very first character in
     * the given string), except if the second character {@linkplain Character#isUpperCase(int)
     * is upper case}, in which case the words is assumed an acronym.
     * <p>
     * The given string is usually a programmatic identifier like a class name or a method name.
     *
     * @param  identifier An identifier with no space, words begin with an upper-case character.
     * @param  toLowerCase {@code true} for changing the first character of words to lower case,
     *         except for the first word and acronyms.
     * @return The identifier with spaces inserted after what looks like words, returned
     *         as a {@link StringBuilder} in order to allow modifications by the caller.
     * @throws NullPointerException if the {@code identifier} argument is null.
     *
     * @deprecated Moved to {@link CharSequences#camelCaseToWords(CharSequence, boolean)} in Apache SIS.
     */
    @Deprecated
    public static StringBuilder camelCaseToWords(final CharSequence identifier, final boolean toLowerCase) {
        return (StringBuilder) CharSequences.camelCaseToWords(identifier, toLowerCase);
    }

    /**
     * Creates an acronym from the given text. If every characters in the given text are upper
     * case, then the text is returned unchanged on the assumption that it is already an acronym.
     * Otherwise this method returns a string containing the first character of each word, where
     * the words are separated by the camel case convention, the {@code '_'} character, or any
     * character which is not a {@linkplain Character#isJavaIdentifierPart(int) java identifier
     * part} (including spaces).
     * <p>
     * <b>Examples:</b> given {@code "northEast"}, this method returns {@code "NE"}.
     * Given {@code "Open Geospatial Consortium"}, this method returns {@code "OGC"}.
     *
     * @param  text The text for which to create an acronym, or {@code null}.
     * @return The acronym, or {@code null} if the given text was null.
     *
     * @deprecated Moved to {@link CharSequences#camelCaseToAcronym(CharSequence)} in Apache SIS.
     */
    @Deprecated
    public static String camelCaseToAcronym(final String text) {
        return toString(CharSequences.camelCaseToAcronym(text));
    }

    /**
     * Returns {@code true} if the first string is likely to be an acronym of the second string.
     * An acronym is a sequence of {@linkplain Character#isLetterOrDigit(int) letters or digits}
     * built from at least one character of each word in the {@code words} string. More than
     * one character from the same word may appear in the acronym, but they must always
     * be the first consecutive characters. The comparison is case-insensitive.
     * <p>
     * <b>Example:</b> given the string {@code "Open Geospatial Consortium"}, the following
     * strings are recognized as acronyms: {@code "OGC"}, {@code "ogc"}, {@code "O.G.C."},
     * {@code "OpGeoCon"}.
     *
     * @param  acronym A possible acronym of the sequence of words.
     * @param  words The sequence of words.
     * @return {@code true} if the first string is an acronym of the second one.
     * @throws NullPointerException if any of the arguments is null.
     *
     * @deprecated Moved to {@link CharSequences#isAcronymForWords(CharSequence, CharSequence)} in Apache SIS.
     */
    @Deprecated
    public static boolean isAcronymForWords(final CharSequence acronym, final CharSequence words) {
        return CharSequences.isAcronymForWords(acronym, words);
    }

    /**
     * Returns {@code true} if the given identifier is a legal Java identifier.
     * This method returns {@code true} if the identifier length is greater than zero,
     * the first character is a {@linkplain Character#isJavaIdentifierStart(int) Java
     * identifier start} and all remaining characters (if any) are
     * {@linkplain Character#isJavaIdentifierPart(int) Java identifier parts}.
     *
     * @param identifier The character sequence to test.
     * @return {@code true} if the given character sequence is a legal Java identifier.
     * @throws NullPointerException if the argument is null.
     *
     * @since 3.20
     */
    public static boolean isJavaIdentifier(final CharSequence identifier) {
        final int length = identifier.length();
        if (length == 0) {
            return false;
        }
        int c = codePointAt(identifier, 0);
        if (!isJavaIdentifierStart(c)) {
            return false;
        }
        for (int i=0; (i += charCount(c)) < length;) {
            c = codePointAt(identifier, i);
            if (!isJavaIdentifierPart(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if every characters in the given character sequence are
     * {@linkplain Character#isUpperCase(int) upper-case}.
     *
     * @param  text The character sequence to test.
     * @return {@code true} if every character are upper-case.
     * @throws NullPointerException if the argument is null.
     *
     * @see String#toUpperCase()
     */
    public static boolean isUpperCase(final CharSequence text) {
        return isUpperCase(text, 0, text.length());
    }

    /**
     * Same as {@link #isUpperCase(CharSequence)}, but on a sub-sequence.
     */
    private static boolean isUpperCase(final CharSequence text, int lower, final int upper) {
        while (lower < upper) {
            final int c = codePointAt(text, lower);
            if (!isUpperCase(c)) {
                return false;
            }
            lower += charCount(c);
        }
        return true;
    }

    /**
     * Returns {@code true} if the two given strings are equal, ignoring case.
     * This method is similar to {@link String#equalsIgnoreCase(String)}, except
     * it works on arbitrary character sequences and compares <cite>code points</cite>
     * instead than characters.
     *
     * @param  s1 The first string to compare.
     * @param  s2 The second string to compare.
     * @return {@code true} if the two given strings are equal, ignoring case.
     * @throws NullPointerException if any of the arguments is null.
     *
     * @see String#equalsIgnoreCase(String)
     *
     * @deprecated Moved to {@link CharSequences#equalsIgnoreCase(CharSequence, CharSequence)} in Apache SIS.
     */
    @Deprecated
    public static boolean equalsIgnoreCase(final CharSequence s1, final CharSequence s2) {
        return CharSequences.equalsIgnoreCase(s1, s2);
    }

    /**
     * Returns {@code true} if the given string at the given offset contains the given part,
     * in a case-sensitive comparison. This method is equivalent to the following code:
     *
     * {@preformat java
     *     return string.regionMatches(offset, part, 0, part.length());
     * }
     *
     * Except that this method works on arbitrary {@link CharSequence} objects instead than
     * {@link String}s only.
     *
     * @param string The string for which to tests for the presence of {@code part}.
     * @param offset The offset in {@code string} where to test for the presence of {@code part}.
     * @param part   The part which may be present in {@code string}.
     * @return {@code true} if {@code string} contains {@code part} at the given {@code offset}.
     * @throws NullPointerException if any of the arguments is null.
     *
     * @see String#regionMatches(int, String, int, int)
     *
     * @deprecated Moved to {@link CharSequences#regionMatches(CharSequence, int, CharSequence)} in Apache SIS.
     */
    @Deprecated
    public static boolean regionMatches(final CharSequence string, final int offset, final CharSequence part) {
        return CharSequences.regionMatches(string, offset, part);
    }

    /**
     * Returns the index within the given strings of the first occurrence of the specified part,
     * starting at the specified index. This method is equivalent to the following code:
     *
     * {@preformat java
     *     return string.indexOf(part, fromIndex);
     * }
     *
     * Except that this method works on arbitrary {@link CharSequence} objects instead than
     * {@link String}s only.
     *
     * @param  string    The string in which to perform the search.
     * @param  part      The substring for which to search.
     * @param  fromIndex The index from which to start the search.
     * @return The index within the string of the first occurrence of the specified part,
     *         starting at the specified index, or -1 if none.
     * @throws NullPointerException if any of the arguments is null.
     *
     * @see String#indexOf(String, int)
     * @see StringBuilder#indexOf(String, int)
     * @see StringBuffer#indexOf(String, int)
     *
     * @since 3.16
     *
     * @deprecated Moved to {@link CharSequences#indexOf(CharSequence, CharSequence, int, int)} in Apache SIS.
     */
    @Deprecated
    public static int indexOf(final CharSequence string, final CharSequence part, int fromIndex) {
        return CharSequences.indexOf(string, part, fromIndex, string.length());
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
     *       {@linkplain Character#isJavaIdentifierStart(int) Java identifier start},
     *       then any following character that are
     *       {@linkplain Character#isJavaIdentifierPart(int) Java identifier part}.</li>
     *   <li>Otherwise any character for which {@link Character#getType(int)} returns
     *       the same value than for <var>c</var>.</li>
     * </ul>
     *
     * @param  text The text for which to get the token.
     * @param  offset Index of the fist character to consider in the given text.
     * @return A sub-sequence of {@code text} starting at the given offset, or an empty string
     *         if there is no non-blank character at or after the given offset.
     * @throws NullPointerException if the {@code text} argument is null.
     *
     * @since 3.18 (derived from 3.06)
     *
     * @deprecated Moved to {@link CharSequences#token(CharSequence, int)} in Apache SIS.
     */
    @Deprecated
    public static CharSequence token(final CharSequence text, int offset) {
        return CharSequences.token(text, offset);
    }

    /**
     * Returns the longest sequence of characters which is found at the beginning of the
     * two given strings. If one of those string is {@code null}, then the other string is
     * returned.
     *
     * @param s1 The first string, or {@code null}.
     * @param s2 The second string, or {@code null}.
     * @return The common prefix of both strings, or {@code null} if both strings are null.
     *
     * @deprecated Moved to {@link CharSequences#commonPrefix(CharSequence, CharSequence)} in Apache SIS.
     */
    @Deprecated
    public static String commonPrefix(final String s1, final String s2) {
        return toString(CharSequences.commonPrefix(s1, s2));
    }

    /**
     * Returns the longest sequence of characters which is found at the end of the two given
     * strings. If one of those string is {@code null}, then the other string is returned.
     *
     * @param s1 The first string, or {@code null}.
     * @param s2 The second string, or {@code null}.
     * @return The common suffix of both strings, or {@code null} if both strings are null.
     *
     * @deprecated Moved to {@link CharSequences#commonSuffix(CharSequence, CharSequence)} in Apache SIS.
     */
    @Deprecated
    public static String commonSuffix(final String s1, final String s2) {
        return toString(CharSequences.commonSuffix(s1, s2));
    }

    /**
     * Returns {@code true} if the given character sequence starts with the given prefix.
     *
     * @param sequence    The sequence to test.
     * @param prefix      The expected prefix.
     * @param ignoreCase  {@code true} if the case should be ignored.
     * @return {@code true} if the given sequence starts with the given prefix.
     * @throws NullPointerException if any of the arguments is null.
     *
     * @deprecated Moved to {@link CharSequences#startsWith(CharSequence, CharSequence, boolean)} in Apache SIS.
     */
    @Deprecated
    public static boolean startsWith(final CharSequence sequence, final CharSequence prefix, final boolean ignoreCase) {
        return CharSequences.startsWith(sequence, prefix, ignoreCase);
    }

    /**
     * Returns {@code true} if the given character sequence ends with the given suffix.
     *
     * @param sequence    The sequence to test.
     * @param suffix      The expected suffix.
     * @param ignoreCase  {@code true} if the case should be ignored.
     * @return {@code true} if the given sequence ends with the given suffix.
     * @throws NullPointerException if any of the arguments is null.
     *
     * @deprecated Moved to {@link CharSequences#endsWith(CharSequence, CharSequence, boolean)} in Apache SIS.
     */
    @Deprecated
    public static boolean endsWith(final CharSequence sequence, final CharSequence suffix, final boolean ignoreCase) {
        return CharSequences.endsWith(sequence, suffix, ignoreCase);
    }

    /**
     * Returns the index of the first character after the given number of lines.
     * This method counts the number of occurrence of {@code '\n'}, {@code '\r'}
     * or {@code "\r\n"} starting from the given position. When {@code numToSkip}
     * occurrences have been found, the index of the first character after the last
     * occurrence is returned.
     *
     * @param string    The string in which to skip a determined amount of lines.
     * @param numToSkip The number of lines to skip. Can be positive, zero or negative.
     * @param startAt   Index at which to start the search.
     * @return Index of the first character after the last skipped line.
     * @throws NullPointerException if the {@code string} argument is null.
     *
     * @deprecated Moved to {@link CharSequences#indexOfLineStart(CharSequence, int, int)} in Apache SIS.
     */
    @Deprecated
    public static int skipLines(final CharSequence string, int numToSkip, int startAt) {
        if (numToSkip < 0) numToSkip++;
        return CharSequences.indexOfLineStart(string, numToSkip, startAt);
    }

    /**
     * Returns a {@link String} instance for each line found in a multi-lines string. Each element
     * in the returned array will be a single line. If the given text is already a single line,
     * then this method returns a singleton containing only the given text.
     * <p>
     * The converse of this method is {@link #formatList(Iterable, String)}.
     *
     * {@note This method has been designed in a time when <code>String.substring(int,int)</code>
     * was cheap, because it shared the same internal <code>char[]</code> array than the original
     * array. However as of JDK8, the <code>String</code> implementation changed and now copies
     * the data. The pertinence of this method may need to be re-evaluated.}
     *
     * @param  text The multi-line text from which to get the individual lines.
     * @return The lines in the text, or {@code null} if the given text was null.
     *
     * @deprecated Moved to {@link CharSequences#splitOnEOL(CharSequence)} in Apache SIS.
     */
    @Deprecated
    public static String[] getLinesFromMultilines(final String text) {
        return toString(CharSequences.splitOnEOL(text));
    }

    private static String[] toString(final CharSequence[] textes) {
        if (textes == null) {
            return null;
        }
        final String[] asStrings = new String[textes.length];
        for (int i=0; i<asStrings.length; i++) {
            asStrings[i] = toString(textes[i]);
        }
        return asStrings;
    }

    private static String toString(final CharSequence text) {
        return (text != null) ? text.toString() : null;
    }
}
