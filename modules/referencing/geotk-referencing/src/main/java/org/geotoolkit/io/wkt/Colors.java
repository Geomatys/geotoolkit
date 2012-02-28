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

import java.util.EnumMap;
import java.io.Serializable;
import org.geotoolkit.io.X364;


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
 */
public class Colors implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 256160285861027191L;

    /**
     * The immutable default set of colors.
     */
    public static final Colors DEFAULT = new Immutable();
    static {
        final EnumMap<Element,X364> map = DEFAULT.map;
        map.put(Element.NUMBER,     X364.FOREGROUND_YELLOW);
        map.put(Element.INTEGER,    X364.FOREGROUND_YELLOW);
        map.put(Element.UNIT,       X364.FOREGROUND_YELLOW);
        map.put(Element.AXIS,       X364.FOREGROUND_CYAN);
        map.put(Element.CODE_LIST,  X364.FOREGROUND_CYAN);
        map.put(Element.PARAMETER,  X364.FOREGROUND_GREEN);
        map.put(Element.METHOD,     X364.FOREGROUND_GREEN);
        map.put(Element.DATUM,      X364.FOREGROUND_GREEN);
        map.put(Element.ERROR,      X364.BACKGROUND_RED);
    }

    /**
     * Keys for syntactic elements to be colorized.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @level advanced
     * @module
     */
    public static enum Element {
        /**
         * Floating point numbers (excluding integer types).
         */
        NUMBER,

        /**
         * Integer numbers.
         */
        INTEGER,

        /**
         * {@linkplain javax.measure.unit.Unit Units of measurement}.
         * In referencing WKT, this is the text inside {@code UNIT} elements.
         */
        UNIT,

        /**
         * {@linkplain org.opengis.referencing.cs.CoordinateSystemAxis Axes}.
         * In referencing WKT, this is the text inside {@code AXIS} elements.
         */
        AXIS,

        /**
         * {@linkplain org.opengis.util.CodeList Code list} values.
         */
        CODE_LIST,

        /**
         * {@linkplain org.opengis.parameter.ParameterValue Parameter values}.
         * In referencing WKT, this is the text inside {@code PARAMETER} elements.
         */
        PARAMETER,

        /**
         * {@linkplain org.opengis.referencing.operation.OperationMethod Operation methods}.
         * In referencing WKT, this is the text inside {@code PROJECTION} elements.
         */
        METHOD,

        /**
         * {@linkplain org.opengis.referencing.datum.Datum Datum}.
         * In referencing WKT, this is the text inside {@code DATUM} elements.
         */
        DATUM,

        /**
         * Unformattable elements.
         */
        ERROR
    }

    /**
     * The map of colors.
     */
    private final EnumMap<Element,X364> map;

    /**
     * Creates a new, initially empty, set of colors.
     */
    public Colors() {
        map = new EnumMap<>(Element.class);
    }

    /**
     * Sets the color of the given syntactic element.
     *
     * @param key   The syntactic element for which to set the color.
     * @param color The color to give to the specified element.
     */
    public void set(final Element key, final X364 color) {
        map.put(key, color);
    }

    /**
     * Returns the color for the given syntactic element.
     *
     * @param key The syntactic element for which to get the color.
     * @return The color of the specified element, or {@code null} if none.
     */
    public X364 get(final Element key) {
        return map.get(key);
    }

    /**
     * An immutable flavor of {@link Colors} for the {@link Colors#DEFAULT} constant.
     */
    private static final class Immutable extends Colors {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -2349530616334766576L;

        /**
         * Do not allow color changes.
         */
        @Override
        public void set(final Element key, final X364 color) {
            throw new UnsupportedOperationException();
        }
    }
}
