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

import java.io.File;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.net.MalformedURLException;
import net.jcip.annotations.Immutable;


/**
 * Handles conversions from {@link java.lang.File} to various objects.
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
abstract class FileConverter<T> extends SimpleConverter<File,T> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -2150865427977735620L;

    /**
     * Returns the source class, which is always {@link File}.
     */
    @Override
    public final Class<File> getSourceClass() {
        return File.class;
    }

    /**
     * Returns {@code true} since subclasses do not preserve order.
     */
    @Override
    public boolean isOrderPreserving() {
        return false;
    }


    /**
     * Converter from {@link java.lang.File} to {@link java.lang.String}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.01
     *
     * @since 3.01
     */
    @Immutable
    static final class String extends FileConverter<java.lang.String> {
        private static final long serialVersionUID = -6811286687809954151L;
        public static final String INSTANCE = new String();
        private String() {
        }

        @Override
        public Class<java.lang.String> getTargetClass() {
            return java.lang.String.class;
        }

        @Override
        public java.lang.String convert(final File source) throws NonconvertibleObjectException {
            return (source != null) ? source.getAbsolutePath() : null;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.File} to {@link java.net.URI}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.01
     *
     * @since 3.01
     */
    @Immutable
    static final class URI extends FileConverter<java.net.URI> {
        private static final long serialVersionUID = 1032598133849975567L;
        public static final URI INSTANCE = new URI();
        private URI() {
        }

        @Override
        public Class<java.net.URI> getTargetClass() {
            return java.net.URI.class;
        }

        @Override
        public java.net.URI convert(final File source) throws NonconvertibleObjectException {
            return (source != null) ? source.toURI() : null;
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }


    /**
     * Converter from {@link java.lang.File} to {@link java.net.URL}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.01
     *
     * @since 3.01
     */
    @Immutable
    static final class URL extends FileConverter<java.net.URL> {
        private static final long serialVersionUID = 621496099287330756L;
        public static final URL INSTANCE = new URL();
        private URL() {
        }

        @Override
        public Class<java.net.URL> getTargetClass() {
            return java.net.URL.class;
        }

        @Override
        public java.net.URL convert(final File source) throws NonconvertibleObjectException {
            if (source == null) {
                return null;
            }
            try {
                return source.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new NonconvertibleObjectException(e);
            }
        }

        /** Returns the singleton instance on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }
}
