/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.net.URISyntaxException;
import java.net.URL;
import net.jcip.annotations.Immutable;


/**
 * Handles conversions from {@link java.net.URL} to various objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.01
 * @module
 *
 * @deprecated Replaced by Apache SIS {@link org.apache.sis.util.ObjectConverters}.
 */
@Deprecated
@Immutable
abstract class URLConverter<T> extends SimpleConverter<URL,T> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 4843540356265851861L;

    /**
     * Returns the source class, which is always {@link URL}.
     */
    @Override
    public final Class<URL> getSourceClass() {
        return URL.class;
    }

    /**
     * Returns {@code true} since subclasses do not preserve order.
     */
    @Override
    public boolean isOrderPreserving() {
        return false;
    }


    /**
     * Converter from {@link java.net.URL} to {@link java.lang.String}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.01
     *
     * @since 3.01
     */
    @Immutable
    static final class String extends URLConverter<java.lang.String> {
        private static final long serialVersionUID = 8091677760312351740L;
        public static final String INSTANCE = new String();
        private String() {
        }

        @Override
        public Class<java.lang.String> getTargetClass() {
            return java.lang.String.class;
        }

        @Override
        public java.lang.String convert(final URL source) throws NonconvertibleObjectException {
            return (source != null) ? source.toExternalForm() : null;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.net.URL} to {@link java.io.File}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.12
     *
     * @since 3.01
     */
    @Immutable
    static final class File extends URLConverter<java.io.File> {
        private static final long serialVersionUID = 1228852836485762335L;
        public static final File INSTANCE = new File();
        private File() {
        }

        @Override
        public Class<java.io.File> getTargetClass() {
            return java.io.File.class;
        }

        @Override
        public java.io.File convert(final URL source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            try {
                return new java.io.File(source.toURI());
            } catch (URISyntaxException | IllegalArgumentException e) {
                throw new NonconvertibleObjectException(formatErrorMessage("URL", source, e), e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.net.URL} to {@link java.net.URI}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.12
     *
     * @since 3.01
     */
    @Immutable
    static final class URI extends URLConverter<java.net.URI> {
        private static final long serialVersionUID = -1653233667050600894L;
        public static final URI INSTANCE = new URI();
        private URI() {
        }

        @Override
        public Class<java.net.URI> getTargetClass() {
            return java.net.URI.class;
        }

        @Override
        public java.net.URI convert(final URL source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            try {
                return source.toURI();
            } catch (URISyntaxException e) {
                throw new NonconvertibleObjectException(formatErrorMessage("URL", source, e), e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }
}
