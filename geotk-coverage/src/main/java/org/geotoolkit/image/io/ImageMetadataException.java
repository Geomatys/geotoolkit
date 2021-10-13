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
 * Thrown if an error occurred while reading or writing the image metadata.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.08
 * @module
 */
public class ImageMetadataException extends IIOException {
    /**
     * Serial version for compatibility with different versions.
     */
    private static final long serialVersionUID = 298508144853920271L;

    /**
     * Constructs a new exception with the specified detail message.
     * The detail message is saved for later retrieval by the {@link #getMessage()} method.
     *
     * @param message The details message.
     */
    public ImageMetadataException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     * The cause is saved for later retrieval by the {@link #getCause()} method.
     *
     * @param cause The cause.
     *
     * @since 3.13
     */
    public ImageMetadataException(final Throwable cause) {
        super((cause != null) ? cause.getLocalizedMessage() : null, cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * The cause is saved for later retrieval by the {@link #getCause()} method.
     *
     * @param message The details message.
     * @param cause The cause.
     */
    public ImageMetadataException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
