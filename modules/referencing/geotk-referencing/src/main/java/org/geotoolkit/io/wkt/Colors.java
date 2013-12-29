/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.io.wkt;

import org.geotoolkit.io.X364;
import org.apache.sis.io.wkt.ElementKind;


/**
 * The colors to use for formatting <cite>Well Known Text</cite> (WKT) objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @see X364
 *
 * @since 3.00
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
public class Colors extends org.apache.sis.io.wkt.Colors {
    /**
     * Keys for syntactic elements to be colorized.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @level advanced
     * @module
     *
     * @deprecated Moved to Apache SIS.
     */
    @Deprecated
    public static final class Element {
        /**
         * Floating point numbers (excluding integer types).
         */
        public static final ElementKind NUMBER = ElementKind.NUMBER;

        /**
         * Integer numbers.
         */
        public static final ElementKind INTEGER = ElementKind.INTEGER;

        /**
         * {@linkplain javax.measure.unit.Unit Units of measurement}.
         * In referencing WKT, this is the text inside {@code UNIT} elements.
         */
        public static final ElementKind UNIT = ElementKind.UNIT;

        /**
         * {@linkplain org.opengis.referencing.cs.CoordinateSystemAxis Axes}.
         * In referencing WKT, this is the text inside {@code AXIS} elements.
         */
        public static final ElementKind AXIS = ElementKind.AXIS;

        /**
         * {@linkplain org.opengis.util.CodeList Code list} values.
         */
        public static final ElementKind CODE_LIST = ElementKind.CODE_LIST;

        /**
         * {@linkplain org.opengis.parameter.ParameterValue Parameter values}.
         * In referencing WKT, this is the text inside {@code PARAMETER} elements.
         */
        public static final ElementKind PARAMETER = ElementKind.PARAMETER;

        /**
         * {@linkplain org.opengis.referencing.operation.OperationMethod Operation methods}.
         * In referencing WKT, this is the text inside {@code PROJECTION} elements.
         */
        public static final ElementKind METHOD = ElementKind.METHOD;

        /**
         * {@linkplain org.opengis.referencing.datum.Datum Datum}.
         * In referencing WKT, this is the text inside {@code DATUM} elements.
         */
        public static final ElementKind DATUM = ElementKind.DATUM;

        /**
         * Unformattable elements.
         */
        public static final ElementKind ERROR = ElementKind.ERROR;

        private Element() {
        }
    }

    /**
     * Creates a new, initially empty, set of colors.
     */
    public Colors() {
    }

    /**
     * Sets the color of the given syntactic element.
     *
     * @param key   The syntactic element for which to set the color.
     * @param color The color to give to the specified element.
     */
    public void set(final ElementKind key, final X364 color) {
        super.setName(key, org.apache.sis.internal.util.X364.valueOf(color.name()).color);
    }

    /**
     * Returns the color for the given syntactic element.
     *
     * @param key The syntactic element for which to get the color.
     * @return The color of the specified element, or {@code null} if none.
     */
    public X364 get(final ElementKind key) {
        final String name = super.getName(key);
        if (name == null) return null;
        return X364.valueOf(org.apache.sis.internal.util.X364.forColorName(name).name());
    }
}
