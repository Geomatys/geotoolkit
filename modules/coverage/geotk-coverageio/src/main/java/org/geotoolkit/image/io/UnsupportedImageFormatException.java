/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.image.io;

import javax.imageio.IIOException;


/**
 * Thrown by {@link XImageIO} when the requested image format is not available in the
 * {@linkplain javax.imageio.spi.IIORegistry Image I/O registry}. This exception may
 * also be thrown if the file format is basically a container, and the encoding of data
 * inside the container is not supported (for example a data type or a compression algorithm).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 */
public class UnsupportedImageFormatException extends IIOException {
    /**
     * Serial version for compatibility with different versions.
     */
    private static final long serialVersionUID = 8810756579848825657L;

    /**
     * Constructs a new exception with the specified detail message.
     * The detail message is saved for later retrieval by the {@link #getMessage()} method.
     *
     * @param message The details message.
     */
    public UnsupportedImageFormatException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * The cause is saved for later retrieval by the {@link #getCause()} method.
     *
     * @param message The details message.
     * @param cause The cause.
     */
    public UnsupportedImageFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
