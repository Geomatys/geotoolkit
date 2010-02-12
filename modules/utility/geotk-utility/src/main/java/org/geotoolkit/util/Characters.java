/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util;

import org.geotoolkit.lang.Static;


/**
 * A set of utilities for characters handling.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 2.1
 * @module
 */
@Static
public final class Characters {
    /**
     * Brackets recognized by {@link #matchingBracket(char)}, declared by pairs.
     * The opening bracket must be at the even index and the closing bracket at
     * the odd index.
     *
     * @since 3.09
     */
    private static final char[] BRACKETS = {'(',')' , '[',']' , '{','}' , '<','>'};

    /**
     * Do not allow instantiation of this class.
     */
    private Characters() {
    }

    /**
     * Determines whether the character is a superscript. Most superscripts have
     * unicode values from \\u2070 to \\u207F inclusive. Superscripts are the
     * following symbols:
     *
     * <blockquote><code>
     *     \u2070 \u00B9 \u00B2 \u00B3 \u2074 \u2075 \u2076 \u2077
     *     \u2078 \u2079 \u207A \u207B \u207C \u207D \u207E \u207F
     * </code></blockquote>
     *
     * @param  c The character to test.
     * @return {@code true} if the given character is a superscript.
     */
    public static boolean isSuperScript(final char c) {
        switch (c) {
            /*1*/case '\u2071':
            /*2*/case '\u2072':
            /*3*/case '\u2073': return false;
            /*1*/case '\u00B9':
            /*2*/case '\u00B2':
            /*3*/case '\u00B3': return true;
        }
        return (c>='\u2070' && c<='\u207F');
    }

    /**
     * Determines whether the character is a subscript. Most subscripts have
     * unicode values from \\u2080 to \\u208E inclusive. Subscripts are the
     * following symbols:
     *
     * <blockquote><code>
     *     \u2080 \u2081 \u2082 \u2083 \u2084 \u2085 \u2086 \u2087
     *     \u2088 \u2089 \u208A \u208B \u208C \u208D \u208E
     * </code></blockquote>
     *
     * @param  c The character to test.
     * @return {@code true} if the given character is a subscript.
     */
    public static boolean isSubScript(final char c) {
        return (c>='\u2080' && c<='\u208E');
    }

    /**
     * Converts the character argument to superscript.
     * Only the following characters can be converted
     * (other characters are left unchanged):
     *
     * {@preformat text
     *     0 1 2 3 4 5 6 7 8 9 + - = ( ) n
     * }
     *
     * @param  c The character to convert.
     * @return The given digit as a superscript, or {@code c} if the
     *         given character was not a digit.
     */
    public static char toSuperScript(final char c) {
        switch (c) {
            case '1': return '\u00B9';
            case '2': return '\u00B2';
            case '3': return '\u00B3';
            case '+': return '\u207A';
            case '-': return '\u207B';
            case '=': return '\u207C';
            case '(': return '\u207D';
            case ')': return '\u207E';
            case 'n': return '\u207F';
        }
        if (c>='0' && c<='9') {
            return (char) (c+('\u2070'-'0'));
        }
        return c;
    }

    /**
     * Converts the character argument to subscript.
     * Only the following characters can be converted
     * (other characters are left unchanged):
     *
     * {@preformat text
     *     0 1 2 3 4 5 6 7 8 9 + - = ( ) n
     * }
     *
     * @param  c The character to convert.
     * @return The given digit as a subscript, or {@code c} if the
     *         given character was not a digit.
     */
    public static char toSubScript(final char c) {
        switch (c) {
            case '+': return '\u208A';
            case '-': return '\u208B';
            case '=': return '\u208C';
            case '(': return '\u208D';
            case ')': return '\u208E';
        }
        if (c>='0' && c<='9') {
            return (char) (c+('\u2080'-'0'));
        }
        return c;
    }

    /**
     * Converts the character argument to normal script.
     *
     * @param  c The character to convert.
     * @return The given digit as a normal digit, or {@code c} if the
     *         given character was not a superscript or a subscript.
     */
    public static char toNormalScript(final char c) {
        switch (c) {
            case '\u00B9': return '1';
            case '\u00B2': return '2';
            case '\u00B3': return '3';
            case '\u2071': return c;
            case '\u2072': return c;
            case '\u2073': return c;
            case '\u207A': return '+';
            case '\u207B': return '-';
            case '\u207C': return '=';
            case '\u207D': return '(';
            case '\u207E': return ')';
            case '\u207F': return 'n';
            case '\u208A': return '+';
            case '\u208B': return '-';
            case '\u208C': return '=';
            case '\u208D': return '(';
            case '\u208E': return ')';
        }
        if (c>='\u2070' && c<='\u2079') return (char) (c-('\u2070'-'0'));
        if (c>='\u2080' && c<='\u2089') return (char) (c-('\u2080'-'0'));
        return c;
    }

    /**
     * Returns the matching brace, bracket or parenthesis for the given character.
     * If the given character is not a bracket, returns it unchanged.
     * <p>
     * In this method, the word "<cite>bracket</cite>" refers to any kind of bracket
     * (round, square, curly or angle). For example this method returns {@code ')'}
     * if it is given the {@code '('} character, and conversely. The same rule applies
     * for other kind of brackets.
     *
     * @param  c The character which may be a bracket.
     * @return The matching character for the given bracket, or {@code c} unchanged
     *         if it is not a bracket.
     *
     * @see <a href="http://en.wikipedia.org/wiki/Bracket">Bracket on Wikipedia</a>
     *
     * @since 3.09
     */
    public static char matchingBracket(char c) {
        for (int i=0; i<BRACKETS.length; i++) {
            if (c == BRACKETS[i]) {
                c = BRACKETS[i ^ 1];
                break;
            }
        }
        return c;
    }
}
