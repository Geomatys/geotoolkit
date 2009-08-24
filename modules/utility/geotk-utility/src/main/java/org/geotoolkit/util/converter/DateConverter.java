/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.converter;

import java.util.Date;
import java.io.Serializable;
import java.io.ObjectStreamException;


/**
 * Handles conversions from {@link java.util.Date} to various objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
abstract class DateConverter<T> extends SimpleConverter<Date,T> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -7770401534710581917L;

    /**
     * Returns the source class, which is always {@link Date}.
     */
    @Override
    public final Class<Date> getSourceClass() {
        return Date.class;
    }

    /**
     * Converter from dates to long integers.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 2.4
     */
    static final class Long extends DateConverter<java.lang.Long> {
        private static final long serialVersionUID = 3163928356094316134L;
        public static final Long INSTANCE = new Long();
        private Long() {
        }

        @Override
        public Class<java.lang.Long> getTargetClass() {
            return java.lang.Long.class;
        }

        @Override
        public java.lang.Long convert(final Date source) {
            if (source == null) {
                return null;
            }
            return source.getTime();
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }

    /**
     * Converter from dates to timestamp.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     */
    static final class Timestamp extends DateConverter<java.sql.Timestamp> {
        private static final long serialVersionUID = 3798633184562706892L;
        public static final Timestamp INSTANCE = new Timestamp();
        private Timestamp() {
        }

        @Override
        public Class<java.sql.Timestamp> getTargetClass() {
            return java.sql.Timestamp.class;
        }

        @Override
        public java.sql.Timestamp convert(final Date source) {
            if (source == null) {
                return null;
            }
            return new java.sql.Timestamp(source.getTime());
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }
}
