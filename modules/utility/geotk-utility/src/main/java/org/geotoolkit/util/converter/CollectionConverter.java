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
package org.geotoolkit.util.converter;

import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.io.Serializable;
import java.io.ObjectStreamException;
import net.jcip.annotations.Immutable;


/**
 * Handles conversions from {@link java.util.Collection} to various objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 * @module
 *
 * @deprecated Replaced by Apache SIS {@link org.apache.sis.util.ObjectConverters}.
 */
@Deprecated
@Immutable
abstract class CollectionConverter<T> extends SimpleConverter<Collection<?>,T> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -4515250904953131514L;

    /**
     * Returns the source class, which is always {@link String}.
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public final Class<Collection<?>> getSourceClass() {
        return (Class) Collection.class;
    }


    /**
     * Converter from {@link java.util.Collection} to {@link java.util.List}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.02
     */
    @Immutable
    static final class List extends CollectionConverter<java.util.List<?>> {
        private static final long serialVersionUID = 5492247760609833586L;
        public static final List INSTANCE = new List();
        private List() {
        }

        @Override
        @SuppressWarnings({"unchecked","rawtypes"})
        public Class<java.util.List<?>> getTargetClass() {
            return (Class) java.util.List.class;
        }

        @Override
        public java.util.List<?> convert(final Collection<?> source) {
            if (source == null) {
                return null;
            }
            if (source instanceof java.util.List<?>) {
                return (java.util.List<?>) source;
            }
            return new ArrayList<>(source);
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.util.Collection} to {@link java.util.Set}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.02
     */
    @Immutable
    static final class Set extends CollectionConverter<java.util.Set<?>> {
        private static final long serialVersionUID = -4200659837453206164L;
        public static final Set INSTANCE = new Set();
        private Set() {
        }

        @Override
        @SuppressWarnings({"unchecked","rawtypes"})
        public Class<java.util.Set<?>> getTargetClass() {
            return (Class) java.util.Set.class;
        }

        @Override
        public java.util.Set<?> convert(final Collection<?> source) {
            if (source == null) {
                return null;
            }
            if (source instanceof java.util.Set<?>) {
                return (java.util.Set<?>) source;
            }
            return new LinkedHashSet<>(source);
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }
}
