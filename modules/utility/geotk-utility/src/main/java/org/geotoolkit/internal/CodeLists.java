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

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import org.opengis.util.CodeList;
import org.opengis.annotation.UML;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.logging.Logging;


/**
 * Utility methods working on {@link CodeList}. This class defines a Geotk
 * {@code CodeLists.valueOf(Class,String)} method which should be used instead
 * than the GeoAPI {@code CodeList.valueOf(Class,String)} in every cases. The
 * main difference is that the Geotk method ignores case and whitespaces,
 * and treats the {@code '_'} character like whitespace.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.02
 * @module
 */
public final class CodeLists extends Static implements CodeList.Filter {
    /**
     * The name to compare during filtering operation.
     */
    private final String codename;

    /**
     * {@code true} if {@link #valueOf} is allowed to create new code lists.
     */
    private final boolean canCreate;

    /**
     * Creates a new filter for the specified code name.
     */
    private CodeLists(final String codename, final boolean canCreate) {
        this.codename  = codename;
        this.canCreate = canCreate;
    }

    /**
     * Returns the classname of the given code list. This method use the {@link UML} annotation
     * if it exists, or fallback on the {@linkplain Class#getSimpleName() simple class name}
     * otherwise.
     *
     * @param  code The code from which to get the class name, or {@code null}.
     * @return The class name, or {@code null} if the given code is null.
     *
     * @since 3.18
     */
    public static String classname(final CodeList<?> code) {
        if (code == null) {
            return null;
        }
        final Class<?> type = code.getClass();
        final UML uml = type.getAnnotation(UML.class);
        if (uml != null) {
            String id = uml.identifier();
            if (id != null && !(id = id.trim()).isEmpty()) {
                return id;
            }
        }
        return type.getSimpleName();
    }

    /**
     * Returns the UML identifier for the given code. If the code has no UML identifier,
     * then the programmatic name is used as a fallback.
     *
     * @param  code The code for which to get the UML identifier, or {@code null}.
     * @return The UML identifiers or programmatic name for the given code,
     *         or {@code null} if the given code is null.
     *
     * @since 3.06
     */
    public static String identifier(final CodeList<?> code) {
        if (code == null) {
            return null;
        }
        String id = code.identifier();
        if (id == null || (id = id.trim()).isEmpty()) {
            // Fallback if no UML identifier were found.
            id = code.name();
        }
        return id;
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
    public static String[] identifiers(final Class<? extends CodeList<?>> codeType) {
        final CodeList<?>[] codes = values(codeType);
        final String[] ids = new String[codes.length];
        for (int i=0; i<codes.length; i++) {
            ids[i] = identifier(codes[i]);
        }
        return ids;
    }

    /**
     * Returns the most descriptive sentence of the given code, excluding the field name. This is
     * usually the UML name except for {@link org.opengis.metadata.identification.CharacterSet}
     * in which case it is a string like {@code "UTF-8"}.
     *
     * @param  code The code from which to construct a sentence, or {@code null}.
     * @return A unlocalized sentence for the given code, or {@code null} if the given code is null.
     *
     * @since 3.18
     */
    public static String sentence(final CodeList<?> code) {
        if (code == null) {
            return null;
        }
        String id = code.identifier();
        final String name = code.name();
        if (id == null) {
            id = name;
        }
        for (final String candidate : code.names()) {
            if (!candidate.equals(name) && candidate.length() >= id.length()) {
                id = candidate;
            }
        }
        return StringUtilities.makeSentence(id);
    }

    /**
     * Returns the localized name of the given code, if possible.
     * <p>
     * <b>Note:</b> This code is partially duplicated by
     * {@link org.geotoolkit.internal.jaxb.code.CodeListProxy#CodeListProxy(CodeList)}.
     *
     * @param  code   The code for which to get the localized name, or {@code null}.
     * @param  locale The local, or {@code null} if none.
     * @return The localized (if possible) sentence, or {@code null} if the given code is null.
     *
     * @since 3.18
     */
    public static String localize(final CodeList<?> code, final Locale locale) {
        if (code == null) {
            return null;
        }
        if (locale != null) {
            final String key = classname(code) + '.' + identifier(code);
            try {
                return ResourceBundle.getBundle("org.opengis.metadata.CodeLists", locale).getString(key);
            } catch (MissingResourceException e) {
                Logging.recoverableException(CodeLists.class, "localize", e);
            }
        }
        return sentence(code);
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
     * tolerant on string comparisons (see the <a href="#skip-navbar_top">class javadoc</a>).
     *
     * @param <T> The compile-time type given as the {@code codeType} parameter.
     * @param codeType The type of code list.
     * @param name The name of the code to obtain.
     * @return A code matching the given name, or {@code null} if the name is null.
     *
     * @see CodeList#valueOf(Class, String)
     */
    public static <T extends CodeList<T>> T valueOf(final Class<T> codeType, final String name) {
        return valueOf(codeType, name, true);
    }

    /**
     * Returns the code of the given type that matches the given name, as described in the
     * {@link #valueOf(Class, String)} method. If no existing code matches, then this method
     * creates a new code if {@code canCreate} is {@code true}, or returns {@code false} otherwise.
     *
     * @param <T> The compile-time type given as the {@code codeType} parameter.
     * @param codeType The type of code list.
     * @param name The name of the code to obtain.
     * @param canCreate {@code true} if this method is allowed to create new code.
     * @return A code matching the given name, or {@code null} if the name is null
     *         or if no matching code is found and {@code canCreate} is {@code false}.
     *
     * @see CodeList#valueOf(Class, String)
     *
     * @since 3.06
     */
    public static <T extends CodeList<T>> T valueOf(final Class<T> codeType, String name, final boolean canCreate) {
        if (name == null || (name = name.trim()).isEmpty()) {
            return null;
        }
        final String typeName = codeType.getName();
        try {
            // Forces initialization of the given class in order
            // to register its list of static final constants.
            Class.forName(typeName, true, codeType.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new TypeNotPresentException(typeName, e); // Should never happen.
        }
        return CodeList.valueOf(codeType, new CodeLists(name, canCreate));
    }

    /**
     * This method is public as an implementation side-effect.
     * Do not use directly.
     */
    @Override
    public String codename() {
        return canCreate ? codename : null;
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
