/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.converter;


/**
 * A filter for {@link Class} instances. This is used in iterations over a set of classes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public interface ClassFilter {
    /**
     * Returns a filter accepting only classes {@linkplain Class#isAssignableFrom assignable to}
     * {@link Number}.
     */
    ClassFilter NUMBER = new BaseClassFilter(Number.class);

    /**
     * Returns a filter accepting only classes {@linkplain Class#isAssignableFrom assignable to}
     * {@link CharSequence}.
     */
    ClassFilter CHAR_SEQUENCE = new BaseClassFilter(CharSequence.class);

    /**
     * Returns {@code true} if the given class can be accepted.
     *
     * @param type The class to test.
     * @return {@code true} if the given class can be accepted.
     */
    boolean accepts(Class<?> type);

    /**
     * Returns a filter which is the negation of this filter.
     * For any type <var>t</var>, the following condition shall hold:
     *
     * {@preformat java
     *     negate().accepts(t) == !accepts(t)
     * }
     *
     * @return The negation of this filter.
     */
    ClassFilter negate();
}
