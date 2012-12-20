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
package org.geotoolkit.internal;

import java.util.Locale;

import org.opengis.util.CodeList;
import org.opengis.annotation.UML;

import org.geotoolkit.lang.Static;
import org.apache.sis.util.iso.Types;


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
 *
 * @deprecated Moved to {@link Types}.
 */
@Deprecated
public final class CodeLists extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private CodeLists() {
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
     *
     * @deprecated Moved to {@link Types#getListName(CodeList)}.
     */
    @Deprecated
    public static String classname(final CodeList<?> code) {
        return Types.getListName(code);
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
     *
     * @deprecated Moved to {@link Types#getCodeName(CodeList)}.
     */
    @Deprecated
    public static String identifier(final CodeList<?> code) {
        return Types.getCodeName(code);
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
     *
     * @deprecated Moved to {@link Types#getCodeTitle(CodeList)}.
     */
    @Deprecated
    public static String sentence(final CodeList<?> code) {
        return Types.getCodeTitle(code);
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
     *
     * @deprecated Moved to {@link Types#getCodeTitle(CodeList, Locale)}.
     */
    @Deprecated
    public static String localize(final CodeList<?> code, final Locale locale) {
        return Types.getCodeTitle(code, locale);
    }

    /**
     * Returns the list of values for the given code list type.
     *
     * @param <T> The compile-time type given as the {@code codeType} parameter.
     * @param codeType The type of code list.
     * @return The list of values for the given code list, or an empty array if none.
     *
     * @since 3.03
     *
     * @deprecated Moved to {@link Types#getCodeValues(CodeList)}.
     */
    @Deprecated
    public static <T extends CodeList<?>> T[] values(final Class<T> codeType) {
        return Types.getCodeValues(codeType);
    }

    /**
     * Returns the code of the given type that matches the given name, or returns a new one if none
     * match it. This method performs the same work than the GeoAPI method, except that it is more
     * tolerant on string comparisons (see the <a href="#skip-navbar_top">class javadoc</a>).
     *
     * @param <T> The compile-time type given as the {@code codeType} parameter.
     * @param codeType The type of code list.
     * @param name The name of the code to obtain, or {@code null}.
     * @return A code matching the given name, or {@code null} if the name is null.
     *
     * @see CodeList#valueOf(Class, String)
     *
     * @deprecated Moved to {@link Types#forCodeName(java.lang.Class, String, boolean)}.
     */
    @Deprecated
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
     * @param name The name of the code to obtain, or {@code null}.
     * @param canCreate {@code true} if this method is allowed to create new code.
     * @return A code matching the given name, or {@code null} if the name is null
     *         or if no matching code is found and {@code canCreate} is {@code false}.
     *
     * @see CodeList#valueOf(Class, String)
     *
     * @since 3.06
     *
     * @deprecated Moved to {@link Types#forCodeName(java.lang.Class, String, boolean)}.
     */
    @Deprecated
    public static <T extends CodeList<T>> T valueOf(final Class<T> codeType, String name, final boolean canCreate) {
        return Types.forCodeName(codeType, name, canCreate);
    }
}
