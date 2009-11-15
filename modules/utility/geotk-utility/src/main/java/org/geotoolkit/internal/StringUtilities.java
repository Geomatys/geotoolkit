/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.util.Arrays;
import org.geotoolkit.lang.Static;
import org.geotoolkit.util.XArrays;


/**
 * Utility methods working on {@link String} or {@link CharSequence}. Some methods duplicate
 * functionalities already provided in {@code String}, but working on {@code CharSequence}.
 * This avoid the need to convert a {@code CharSequence} to a {@code String} for some simple
 * tasks that do not really need such conversion.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
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
     */
    public static int count(final CharSequence text, final char c) {
        int n = 0;
        for (int i=text.length(); --i>=0;) {
            if (text.charAt(i) == c) {
                n++;
            }
        }
        return n;
    }

    /**
     * Replaces every occurences of the given string in the given buffer.
     *
     * @param buffer The string in which to perform the replacements.
     * @param target The string to replace.
     * @param replacement The replacement for the target string.
     */
    public static void replace(final StringBuilder buffer, final String target, final String replacement) {
        final int length = target.length();
        int i = buffer.length();
        while ((i = buffer.lastIndexOf(target, i)) >= 0) {
            buffer.replace(i, i+length, replacement);
            i -= length;
        }
    }

    /**
     * Removes every occurences of the given string in the given buffer.
     *
     * @param buffer The string in which to perform the removals.
     * @param toRemove The string to remove.
     *
     * @since 3.06
     */
    public static void remove(final StringBuilder buffer, final String toRemove) {
        final int length = toRemove.length();
        for (int i=buffer.lastIndexOf(toRemove); i>=0; i=buffer.lastIndexOf(toRemove, i)) {
            buffer.delete(i, i + length);
        }
    }

    /**
     * Removes every occurences of line feeds in the given buffer, together with the spaces
     * around the line feeds. If the last character of a line and the first character of the
     * next line are both {@linkplain Character#isLetterOrDigit(char) letter or digit}, then
     * a space will be inserted between them. Otherwise they will be no space.
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
     * Returns the given identifier (e.g. a class name) with spaces inserted after words.
     * A word begin with a upper-case character following a lower-case character. For
     * example if the given identifier is {@code "PixelInterleavedSampleModel"}, then this
     * method returns {@code "Pixel interleaved sample model"}.
     *
     * @param  identifier An identifier with no space, words beging with an upper-case character.
     * @return The identifier with spaces inserted after what looks like words.
     *
     * @since 3.03
     */
    public static StringBuilder separateWords(final CharSequence identifier) {
        final int length = identifier.length();
        final StringBuilder buffer = new StringBuilder(length + 8);
        int last = 0;
        for (int i=1; i<=length; i++) {
            if (i == length ||
                (Character.isUpperCase(identifier.charAt(i)) &&
                 Character.isLowerCase(identifier.charAt(i-1))))
            {
                final int pos = buffer.length();
                buffer.append(identifier, last, i).append(' ');
                if (pos!=0 && last<length-1 && Character.isLowerCase(identifier.charAt(last+1))) {
                    buffer.setCharAt(pos, Character.toLowerCase(buffer.charAt(pos)));
                }
                last = i;
            }
        }
        /*
         * Removes the trailing space, if any.
         */
        int lg = buffer.length();
        if (lg != 0 && Character.isSpaceChar(buffer.charAt(--lg))) {
            buffer.setLength(lg);
        }
        return buffer;
    }

    /**
     * Returns the token starting at the given offset in the given text. For the purpose of this
     * method, a "token" is any sequence of consecutive characters of the same type, as defined
     * below.
     * <p>
     * Lets define <var>c</var> as the first non-blank character located at an index equals or
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
     */
    public static boolean equalsAcronym(final CharSequence complete, final CharSequence acronym) {
        final int lgc = complete.length();
        final int lga = acronym .length();
        int ic=0, ia=0;
        char ca, cc;
        do if (ia >= lga) return false;
        while (!Character.isLetterOrDigit(ca = acronym.charAt(ia++)));
        do if (ic >= lgc) return false;
        while (!Character.isLetterOrDigit(cc = complete.charAt(ic++)));
        if (Character.toUpperCase(ca) != Character.toUpperCase(cc)) {
            // The first letter must match.
            return false;
        }
cmp:    while (ia < lga) {
            if (ic >= lgc) {
                // There is more letters in the acronym than in the complete name.
                return false;
            }
            ca = acronym .charAt(ia++);
            cc = complete.charAt(ic++);
            if (Character.isLetterOrDigit(ca)) {
                if (Character.toUpperCase(ca) == Character.toUpperCase(cc)) {
                    // Acronym letter matches the letter from the complete name.
                    // Continue the comparison with next letter of both strings.
                    continue;
                }
                // Will search for the next word after the 'else' block.
            } else do {
                if (ia >= lga) break cmp;
                ca = acronym.charAt(ia++);
            } while (!Character.isLetterOrDigit(ca));
            /*
             * At this point, 'ca' is the next acronym letter to compare and we
             * need to search for the next word in the complete name. We first
             * skip remaining letters, then we skip non-letter characters.
             */
            boolean skipLetters = true;
            do while (Character.isLetterOrDigit(cc) == skipLetters) {
                if (ic >= lgc) {
                    return false;
                }
                cc = complete.charAt(ic++);
            } while ((skipLetters = !skipLetters) == false);
            // Now that we are aligned on a new word, the first letter must match.
            if (Character.toUpperCase(ca) != Character.toUpperCase(cc)) {
                return false;
            }
        }
        /*
         * Now that we have processed all acronym letters, the complete name can not have
         * any additional word. We can only finish the current word and skip trailing non-
         * letter characters.
         */
        boolean skipLetters = true;
        do {
            do {
                if (ic >= lgc) return true;
                cc = complete.charAt(ic++);
            } while (Character.isLetterOrDigit(cc) == skipLetters);
        } while ((skipLetters = !skipLetters) == false);
        return false;
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
     */
    public static boolean equalsIgnoreCase(final CharSequence s1, final CharSequence s2) {
        final int length = s1.length();
        if (s2.length() != length) {
            return false;
        }
        for (int i=0; i<length; i++) {
            if (Character.toUpperCase(s1.charAt(i)) != Character.toUpperCase(s2.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the given string at the given offset contains the given part.
     *
     * @param string The string for which to tests for the presense of {@code part}.
     * @param offset The offset at which {@code part} is to be tested.
     * @param part   The part which may be present in {@code string}.
     * @return {@code true} if {@code string} contains {@code part} at the given {@code offset}.
     */
    public static boolean regionMatches(final CharSequence string, final int offset, final CharSequence part) {
        final int length = part.length();
        if (offset + length > string.length()) {
            return false;
        }
        for (int i=0; i<length; i++) {
            if (string.charAt(offset + i) != part.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the leading part which is common in to the two given strings.
     * If one of those string is {@code null}, then the other string is returned.
     *
     * @param s1 The first string, or {@code null}.
     * @param s2 The second string, or {@code null}.
     * @return The common prefix of both strings, or {@code null} if both strings are null.
     */
    public static String commonPrefix(final String s1, final String s2) {
        if (s1 == null) return s2;
        if (s2 == null) return s1;
        final String shortest;
        final int lg1 = s1.length();
        final int lg2 = s2.length();
        final int length;
        if (lg1 <= lg2) {
            shortest = s1;
            length = lg1;
        } else {
            shortest = s2;
            length = lg2;
        }
        int i = 0;
        while (i < length) {
            if (s1.charAt(i) != s2.charAt(i)) {
                break;
            }
            i++;
        }
        return shortest.substring(0, i);
    }

    /**
     * Returns the trailing part which is common in to the two given strings.
     * If one of those string is {@code null}, then the other string is returned.
     *
     * @param s1 The first string, or {@code null}.
     * @param s2 The second string, or {@code null}.
     * @return The common suffix of both strings, or {@code null} if both strings are null.
     */
    public static String commonSuffix(final String s1, final String s2) {
        if (s1 == null) return s2;
        if (s2 == null) return s1;
        final String shortest;
        final int lg1 = s1.length();
        final int lg2 = s2.length();
        final int length;
        if (lg1 <= lg2) {
            shortest = s1;
            length = lg1;
        } else {
            shortest = s2;
            length = lg2;
        }
        int i = 0;
        while (++i <= length) {
            if (s1.charAt(lg1 - i) != s2.charAt(lg2 - i)) {
                break;
            }
        }
        i--;
        return shortest.substring(length - i);
    }

    /**
     * Returns {@code true} if the given character sequence starts with the given prefix.
     *
     * @param sequence    The sequence to test.
     * @param prefix      The expected prefix.
     * @param ignoreCase  {@code true} if the case should be ignored.
     * @return {@code true} if the given sequence starts with the given prefix.
     */
    public static boolean startsWith(final CharSequence sequence, final CharSequence prefix, final boolean ignoreCase) {
        final int length = prefix.length();
        if (length > sequence.length()) {
            return false;
        }
        for (int i=0; i<length; i++) {
            char c1 = sequence.charAt(i);
            char c2 = prefix.charAt(i);
            if (ignoreCase) {
                c1 = Character.toLowerCase(c1);
                c2 = Character.toLowerCase(c2);
            }
            if (c1 != c2) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the given character sequence ends with the given suffix.
     *
     * @param sequence    The sequence to test.
     * @param suffix      The expected suffix.
     * @param ignoreCase  {@code true} if the case should be ignored.
     * @return {@code true} if the given sequence ends with the given suffix.
     */
    public static boolean endsWith(final CharSequence sequence, final CharSequence suffix, final boolean ignoreCase) {
        int j = suffix.length();
        int i = sequence.length();
        if (j > i) {
            return false;
        }

        while (--j >= 0) {
            char c1 = sequence.charAt(--i);
            char c2 = suffix.charAt(j);
            if (ignoreCase) {
                c1 = Character.toLowerCase(c1);
                c2 = Character.toLowerCase(c2);
            }
            if (c1 != c2) {
                return false;
            }
        }
        return true;
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
     */
    public static int skipLines(final CharSequence string, int numToSkip, int startAt) {
        final int length = string.length();
        /*
         * Go backward if the number of lines is negative.
         */
        if (numToSkip < 0) {
            do {
                char c;
                do {
                    if (startAt == 0) {
                        return startAt;
                    }
                    c = string.charAt(--startAt);
                    if (c == '\n') {
                        if (startAt != 0 && string.charAt(startAt - 1) == '\r') {
                            --startAt;
                        }
                        break;
                    }
                } while (c != '\r');
            } while (++numToSkip != 0);
            numToSkip = 1; // For skipping the "end of line" characters.
        }
        /*
         * Skips forward the given amount of lines.
         */
        while (--numToSkip >= 0) {
            char c;
            do {
                if (startAt >= length) {
                    return startAt;
                }
                c = string.charAt(startAt++);
                if (c == '\r') {
                    if (startAt != length && string.charAt(startAt) == '\n') {
                        startAt++;
                    }
                    break;
                }
            } while (c != '\n');
        }
        return startAt;
    }

    /**
     * Splits a multi-lines string. Each element in the returned array will be a single line.
     * If the given text is already a single line, then this method returns a singleton which
     * contain the given text
     *
     * @param  text The text to split.
     * @return The lines in the text, or {@code null} if the given text was null.
     */
    public static String[] splitLines(final String text) {
        if (text == null) {
            return null;
        }
        /*
         * This method is implemented on top of String.indexOf(int,int), which is the
         * fatest method available while taking care of the complexity of code points.
         */
        int lf = text.indexOf('\n');
        int cr = text.indexOf('\r');
        if (lf < 0 && cr < 0) {
            return new String[] {
                text
            };
        }
        int count = 0;
        String[] splitted = new String[8];
        int last = 0;
        boolean hasMore;
        do {
            int skip = 1;
            final int splitAt;
            if (cr < 0) {
                // There is no "\r" character in the whole text, only "\n".
                splitAt = lf;
                hasMore = (lf = text.indexOf('\n', lf+1)) >= 0;
            } else if (lf < 0) {
                // There is no "\n" character in the whole text, only "\r".
                splitAt = cr;
                hasMore = (cr = text.indexOf('\r', cr+1)) >= 0;
            } else if (lf < cr) {
                // There is both "\n" and "\r" characters with "\n" first.
                splitAt = lf;
                hasMore = true;
                lf = text.indexOf('\n', lf+1);
            } else {
                // There is both "\r" and "\n" characters with "\r" first.
                // We need special care for the "\r\n" sequence.
                splitAt = cr;
                if (lf == ++cr) {
                    cr = text.indexOf('\r', cr+1);
                    lf = text.indexOf('\n', lf+1);
                    hasMore = (cr >= 0 || lf >= 0);
                    skip = 2;
                } else {
                    cr = text.indexOf('\r', cr+1);
                    hasMore = true; // Because there is lf.
                }
            }
            if (count >= splitted.length) {
                splitted = Arrays.copyOf(splitted, count*2);
            }
            splitted[count++] = text.substring(last, splitAt);
            last = splitAt + skip;
        } while (hasMore);
        /*
         * Add the remaining string and we are done.
         */
        if (count >= splitted.length) {
            splitted = Arrays.copyOf(splitted, count+1);
        }
        splitted[count++] = text.substring(last);
        return XArrays.resize(splitted, count);
    }
}
