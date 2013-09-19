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

import org.geotoolkit.lang.Static;
import static java.lang.Character.*;


/**
 * Utility methods working on {@link String} or {@link CharSequence} instances.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 4.00
 *
 * @see org.apache.sis.util.CharSequences
 *
 * @since 3.09 (derived from 3.00)
 * @module
 */
public final class Strings extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private Strings() {
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
            if (!Character.isUpperCase(c)) {
                return false;
            }
            lower += charCount(c);
        }
        return true;
    }
}
