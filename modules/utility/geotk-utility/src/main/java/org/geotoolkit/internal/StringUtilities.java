/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
 * @version 3.03
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
     * Returns the leading part which is common to the two given string.
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
     * Returns the trailing part which is common to the two given string.
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
