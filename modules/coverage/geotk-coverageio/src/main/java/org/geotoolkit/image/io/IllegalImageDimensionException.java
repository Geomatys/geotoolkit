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
 * Thrown by {@link MultidimensionalImageStore} implementations when a slice of data in an
 * hypercube is accessed in an illegal way. For example this exception may be thrown at
 * reading or writing time if the following code:
 *
 * {@preformat java
 *     MultidimensionalImageStore reader = ...;
 *     reader.getDimensionForAPI(DimensionSlice.API.BANDS).addDimensionId(...)
 * }
 *
 * has been invoked, and the identifiers given to the {@code addDimensionId(...)} method map to a
 * dimension that can not be sliced through the {@linkplain javax.imageio.IIOParam#setSourceBands
 * source bands} attribute.
 * <p>
 * The restrictions are plugins-specific, but a typical restriction is that the
 * subregion to fetch along dimensions 0 and 1 can only be specified through the
 * {@link DimensionSlice.API#COLUMNS COLUMNS} and {@link DimensionSlice.API#ROWS ROWS}
 * API respectively, using the standard {@linkplain javax.imageio.IIOParam#setSourceRegion
 * source region} attribute.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
public class IllegalImageDimensionException extends IIOException {
    /**
     * Serial version for compatibility with different versions.
     */
    private static final long serialVersionUID = 1853327916078127235L;

    /**
     * Constructs a new exception with the specified detail message.
     * The detail message is saved for later retrieval by the {@link #getMessage()} method.
     *
     * @param message The details message.
     */
    public IllegalImageDimensionException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * The cause is saved for later retrieval by the {@link #getCause()} method.
     *
     * @param message The details message.
     * @param cause The cause.
     */
    public IllegalImageDimensionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
