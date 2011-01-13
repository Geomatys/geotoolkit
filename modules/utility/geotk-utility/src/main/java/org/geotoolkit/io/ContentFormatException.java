/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.io;

import java.io.IOException;


/**
 * Thrown when a stream can't be parsed because some content uses an invalid format.
 * This exception typically has a {@link java.text.ParseException} has its cause.
 * It is similar in spirit to {@link java.util.InvalidPropertiesFormatException}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see java.util.InvalidPropertiesFormatException
 *
 * @since 2.2
 * @module
 */
public class ContentFormatException extends IOException {
    /**
     * Serial version for compatibility with different versions.
     */
    private static final long serialVersionUID = 6152194019351374599L;

    /**
     * Constructs a new exception with no detail message.
     *
     * @since 3.00
     */
    public ContentFormatException() {
    }

    /**
     * Constructs a new exception with the specified detail message.
     * The detail message is saved for later retrieval by the {@link #getMessage()} method.
     *
     * @param message The details message.
     */
    public ContentFormatException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * The cause is saved for later retrieval by the {@link #getCause()} method.
     *
     * @param message The details message.
     * @param cause The cause.
     */
    public ContentFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
