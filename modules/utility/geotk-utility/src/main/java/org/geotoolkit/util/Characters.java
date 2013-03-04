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
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.util.Characters}.
 */
@Deprecated
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
        return org.apache.sis.util.Characters.isSuperScript(c);
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
        return org.apache.sis.util.Characters.isSubScript(c);
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
        return org.apache.sis.util.Characters.toSuperScript(c);
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
        return org.apache.sis.util.Characters.toSubScript(c);
    }

    /**
     * Converts the character argument to normal script.
     *
     * @param  c The character to convert.
     * @return The given digit as a normal digit, or {@code c} if the
     *         given character was not a superscript or a subscript.
     */
    public static char toNormalScript(char c) {
        return org.apache.sis.util.Characters.toNormalScript(c);
    }
}
