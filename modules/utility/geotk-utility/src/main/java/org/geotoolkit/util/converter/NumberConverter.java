/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.io.Serializable;
import java.io.ObjectStreamException;


/**
 * Handles conversions from {@link java.lang.Number} to various objects.
 *
 * @author Justin Deoliveira (TOPP)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 2.4
 * @module
 */
abstract class NumberConverter<T> extends SimpleConverter<Number,T> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8715054480508622025L;

    /**
     * Returns the source class, which is always {@link Number}.
     */
    @Override
    public final Class<Number> getSourceClass() {
        return Number.class;
    }

    /**
     * Returns {@code false} since subclasses do not preserve order.
     */
    @Override
    public boolean isOrderPreserving() {
        return false;
    }

    /**
     * Converter from numbers to strings.
     *
     * @author Justin Deoliveira (TOPP)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.0
     *
     * @since 2.4
     */
    static final class String extends NumberConverter<java.lang.String> {
        private static final long serialVersionUID = 1460382215827540172L;
        public static final String INSTANCE = new String();
        private String() {
        }

        @Override
        public Class<java.lang.String> getTargetClass() {
            return java.lang.String.class;
        }

        @Override
        public java.lang.String convert(final Number source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            return source.toString();
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }

    /**
     * Converter from numbers to booleans. Values in the range (-1..1) exclusive
     * and NaN values are understood as "false", and all other values as "true".
     *
     * @author Justin Deoliveira (TOPP)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.0
     *
     * @since 2.4
     */
    static final class Boolean extends NumberConverter<java.lang.Boolean> {
        private static final long serialVersionUID = -7522980351031833731L;
        public static final Boolean INSTANCE = new Boolean();
        private Boolean() {
        }

        @Override
        public Class<java.lang.Boolean> getTargetClass() {
            return java.lang.Boolean.class;
        }

        @Override
        public java.lang.Boolean convert(final Number source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            return java.lang.Boolean.valueOf(source.intValue() != 0);
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }

    /**
     * Converter from numbers to colors.
     *
     * @author Justin Deoliveira (TOPP)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.0
     *
     * @since 2.4
     */
    static final class Color extends NumberConverter<java.awt.Color> {
        private static final long serialVersionUID = 8866612442279600953L;
        public static final Color INSTANCE = new Color();
        private Color() {
        }

        @Override
        public Class<java.awt.Color> getTargetClass() {
            return java.awt.Color.class;
        }

        @Override
        public java.awt.Color convert(final Number source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            final int rgba = source.intValue();
            final int alpha = rgba & 0xFF000000;
            return new java.awt.Color(rgba, alpha != 0);
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }
}
