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
package org.geotoolkit.util.converter;

import java.io.Serializable;
import java.io.ObjectStreamException;
import net.jcip.annotations.Immutable;


/**
 * Handles conversions from {@link java.lang.Long} to various objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 * @module
 *
 * @deprecated Replaced by Apache SIS {@link org.apache.sis.util.ObjectConverters}.
 */
@Deprecated
@Immutable
abstract class LongConverter<T> extends SimpleConverter<Long,T> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -7313843433890738313L;

    /**
     * Returns the source class, which is always {@link Long}.
     */
    @Override
    public Class<Long> getSourceClass() {
        return Long.class;
    }

    /**
     * Converter from long integers to dates.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 2.4
     */
    @Immutable
    static final class Date extends LongConverter<java.util.Date> {
        private static final long serialVersionUID = 3999693055029959455L;
        public static final Date INSTANCE = new Date();
        private Date() {
        }

        @Override
        public Class<java.util.Date> getTargetClass() {
            return java.util.Date.class;
        }

        @Override
        public java.util.Date convert(final Long target) {
            if (target == null) {
                return null;
            }
            return new java.util.Date(target);
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }
}
