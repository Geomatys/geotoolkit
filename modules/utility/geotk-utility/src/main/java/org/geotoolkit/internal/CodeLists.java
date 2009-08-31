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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import org.opengis.util.CodeList;
import org.geotoolkit.lang.Static;


/**
 * Utility methods working on {@link CodeList}. This class defines a Geotk
 * {@code CodeLists.valueOf(Class,String)} method which should be used instead
 * than the GeoAPI {@code CodeList.valueOf(Class,String)} in every cases. The
 * main difference is that the Geotk method ignore case and whitespaces,
 * and treats the {@code '_'} character like whitespace.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 * @module
 */
@Static
public final class CodeLists implements CodeList.Filter {
    /**
     * The name to compare during filtering operation.
     */
    private final String codename;

    /**
     * Creates a new filter for the specified code name.
     */
    private CodeLists(final String name) {
        codename = name.trim();
    }

    /**
     * Returns the list of UML identifiers for the given code list type.
     * If a code has no UML identifier, then the programmatic name is used as a fallback.
     *
     * @param  codeType The type of code list.
     * @return The list of UML identifiers or programmatic names for the given
     *         code list, or an empty array if none.
     *
     * @since 3.03
     */
    public static String[] identifiers(final Class<CodeList<?>> codeType) {
        final CodeList<?>[] codes = values(codeType);
        final String[] ids = new String[codes.length];
        for (int i=0; i<codes.length; i++) {
            final CodeList<?> code = codes[i];
            String id = code.identifier();
            if (id == null) {
                id = code.name();
            }
            ids[i] = id;
        }
        return ids;
    }

    /**
     * Returns the list of values for the given code list type.
     *
     * @param <T> The compile-time type given as the {@code codeType} parameter.
     * @param codeType The type of code list.
     * @return The list of values for the given code list, or an empty array if none.
     *
     * @since 3.03
     */
    @SuppressWarnings("unchecked")
    public static <T extends CodeList<?>> T[] values(final Class<T> codeType) {
        Object values;
        try {
            values = codeType.getMethod("values", (Class<?>[]) null).invoke(null, (Object[]) null);
        } catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new UndeclaredThrowableException(cause);
        } catch (Exception e) { // Multi catch: NoSuchMethodException, IllegalAccessException
            values = Array.newInstance(codeType, 0);
        }
        return (T[]) values;
    }

    /**
     * Returns the code of the given type that matches the given name, or returns a new one if none
     * match it. This method performs the same work than the GeoAPI method, except that it is more
     * tolerant on string comparisons (see the class javadoc).
     *
     * @param <T> The compile-time type given as the {@code codeType} parameter.
     * @param codeType The type of code list.
     * @param name The name of the code to obtain.
     * @return A code matching the given name, or {@code null} if the name is null.
     *
     * @see CodeList#valueOf(Class, String)
     */
    public static <T extends CodeList<T>> T valueOf(final Class<T> codeType, String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        try {
            // Forces initialization of the given class in order
            // to register its list of static final constants.
            Class.forName(codeType.getName(), true, codeType.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e); // Should never happen.
        }
        return CodeList.valueOf(codeType, new CodeLists(name));
    }

    /**
     * This method is public as an implementation side-effect.
     * Do not use directly.
     */
    @Override
    public String codename() {
        return codename;
    }

    /**
     * This method is public as an implementation side-effect.
     * Do not use directly.
     */
    @Override
    public boolean accept(final CodeList<?> code) {
        for (final String name : code.names()) {
            if (matches(name, codename)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the given strings are equal, ignoring case, whitespaces and the
     * {@code '_'} character.
     *
     * @param  name The first string to compare.
     * @param  expected The second string to compare.
     * @return {@code true} if the two strings are equal.
     */
    private static boolean matches(final String name, final String expected) {
        final int el = expected.length();
        final int length = name.length();
        int ei=0;
        for (int i=0; i<length; i++) {
            char cn = name.charAt(i);
            if (isSignificant(cn)) {
                // Fetch the next significant character from the expected string.
                char ce;
                do {
                    if (ei >= el) {
                        return false; // The name has more significant characters than expected.
                    }
                    ce = expected.charAt(ei++);
                } while (!isSignificant(ce));

                // Compare the characters in the same way than String.equalsIgnoreCase(String).
                if (cn == ce) {
                    continue;
                }
                cn = Character.toUpperCase(cn);
                ce = Character.toUpperCase(ce);
                if (cn == ce) {
                    continue;
                }
                cn = Character.toLowerCase(cn);
                ce = Character.toLowerCase(ce);
                if (cn == ce) {
                    continue;
                }
                return false;
            }
        }
        while (ei < el) {
            if (isSignificant(expected.charAt(ei++))) {
                return false; // The name has less significant characters than expected.
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the given character should be taken in account when comparing two
     * strings. Current implementation ignores only the whitespaces, so this method is merely a
     * placeholder where more elaborated conditions could be added in the future.
     */
    private static boolean isSignificant(final char c) {
        return !Character.isWhitespace(c);
    }
}
