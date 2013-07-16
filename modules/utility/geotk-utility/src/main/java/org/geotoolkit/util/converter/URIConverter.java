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
import java.net.MalformedURLException;
import java.net.URI;
import net.jcip.annotations.Immutable;


/**
 * Handles conversions from {@link java.net.URI} to various objects.
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
abstract class URIConverter<T> extends SimpleConverter<URI,T> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 5419481828621160876L;

    /**
     * Returns the source class, which is always {@link URI}.
     */
    @Override
    public final Class<URI> getSourceClass() {
        return URI.class;
    }

    /**
     * Returns {@code true} since subclasses do not preserve order.
     */
    @Override
    public boolean isOrderPreserving() {
        return false;
    }


    /**
     * Converter from {@link java.net.URI} to {@link java.lang.String}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.01
     *
     * @since 3.01
     */
    @Immutable
    static final class String extends URIConverter<java.lang.String> {
        private static final long serialVersionUID = -1745990349642467147L;
        public static final String INSTANCE = new String();
        private String() {
        }

        @Override
        public Class<java.lang.String> getTargetClass() {
            return java.lang.String.class;
        }

        @Override
        public java.lang.String convert(final URI source) throws NonconvertibleObjectException {
            return (source != null) ? source.toASCIIString() : null;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.net.URI} to {@link java.io.File}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.12
     *
     * @since 3.01
     */
    @Immutable
    static final class File extends URIConverter<java.io.File> {
        private static final long serialVersionUID = 5289256237146366469L;
        public static final File INSTANCE = new File();
        private File() {
        }

        @Override
        public Class<java.io.File> getTargetClass() {
            return java.io.File.class;
        }

        @Override
        public java.io.File convert(final URI source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            try {
                return new java.io.File(source);
            } catch (IllegalArgumentException e) {
                throw new NonconvertibleObjectException(formatErrorMessage("URI", source, e), e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.net.URI} to {@link java.net.URL}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.12
     *
     * @since 3.01
     */
    @Immutable
    static final class URL extends URIConverter<java.net.URL> {
        private static final long serialVersionUID = -7866572007304228474L;
        public static final URL INSTANCE = new URL();
        private URL() {
        }

        @Override
        public Class<java.net.URL> getTargetClass() {
            return java.net.URL.class;
        }

        @Override
        public java.net.URL convert(final URI source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            try {
                return source.toURL();
            } catch (MalformedURLException e) {
                throw new NonconvertibleObjectException(formatErrorMessage("URI", source, e), e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }
}
