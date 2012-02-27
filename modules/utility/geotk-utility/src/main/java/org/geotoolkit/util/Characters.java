/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.lang.Static;


/**
 * A set of utilities for characters handling.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public final class Characters extends Static {
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
    public static char toSuperScript(char c) {
        switch (c) {
            case '1': c = '\u00B9'; break;
            case '2': c = '\u00B2'; break;
            case '3': c = '\u00B3'; break;
            case '+': c = '\u207A'; break;
            case '-': c = '\u207B'; break;
            case '=': c = '\u207C'; break;
            case '(': c = '\u207D'; break;
            case ')': c = '\u207E'; break;
            case 'n': c = '\u207F'; break;
            default: {
                if (c >= '0' && c <= '9') {
                    c += ('\u2070' - '0');
                }
                break;
            }
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
    public static char toSubScript(char c) {
        switch (c) {
            case '+': c = '\u208A'; break;
            case '-': c = '\u208B'; break;
            case '=': c = '\u208C'; break;
            case '(': c = '\u208D'; break;
            case ')': c = '\u208E'; break;
            default: {
                if (c >= '0' && c <= '9') {
                    c += ('\u2080' - '0');
                }
                break;
            }
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
    public static char toNormalScript(char c) {
        switch (c) {
            case '\u00B9': c = '1'; break;
            case '\u00B2': c = '2'; break;
            case '\u00B3': c = '3'; break;
            case '\u2071':
            case '\u2072':
            case '\u2073':          break;
            case '\u207A': c = '+'; break;
            case '\u207B': c = '-'; break;
            case '\u207C': c = '='; break;
            case '\u207D': c = '('; break;
            case '\u207E': c = ')'; break;
            case '\u207F': c = 'n'; break;
            case '\u208A': c = '+'; break;
            case '\u208B': c = '-'; break;
            case '\u208C': c = '='; break;
            case '\u208D': c = '('; break;
            case '\u208E': c = ')'; break;
            default: {
                if (c >= '\u2070' && c <= '\u2079') {
                    c -= ('\u2070' - '0');
                } else if (c >= '\u2080' && c <= '\u2089') {
                    c -= ('\u2080'-'0');
                }
                break;
            }
        }
        return c;
    }
}
