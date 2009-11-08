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
package org.geotoolkit.internal.jaxb.referencing;

import java.lang.reflect.Field;

import org.geotoolkit.lang.Static;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;


/**
 * Accessors for settings the value of final fields. This should be used for JAXB unmarshalling
 * only - any other usage may produce unexpected results.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @param <C> The class type.
 * @param <V> The field type.
 *
 * @since 3.06
 * @module
 */
@Static
public final class Accessors<C,V> {
    /**
     * Accessors to the ellipsoid axes length.
     */
    public static final Accessors<DefaultEllipsoid,Double> SEMI_MAJOR, SEMI_MINOR, IVF;

    /**
     * Sets the constant values.
     */
    static {
        final Class<DefaultEllipsoid> type = DefaultEllipsoid.class;
        SEMI_MAJOR = new Accessors<DefaultEllipsoid,Double>(type, "semiMajorAxis");
        SEMI_MINOR = new Accessors<DefaultEllipsoid,Double>(type, "semiMinorAxis");
        IVF        = new Accessors<DefaultEllipsoid,Double>(type, "inverseFlattening");
    }

    /**
     * The field to be acceed.
     */
    private final Field field;

    /**
     * Creates a new accessor for the given field.
     */
    private Accessors(final Class<C> type, final String name) {
        try {
            field = type.getField(name);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(name, e);
        }
        field.setAccessible(true);
    }

    /**
     * Sets the value for the field of the given class.
     *
     * @param type  The class on which to set the value.
     * @param value The value to set.
     */
    public void set(final C type, final V value) {
        try {
            field.set(type, value);
        } catch (IllegalAccessException e) {
            // Should never happen because we have set the field to accessible state.
            throw new AssertionError(e);
        }
    }
}
