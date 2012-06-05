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
package org.geotoolkit.referencing.factory;

import org.opengis.util.NoSuchIdentifierException;


/**
 * Thrown when a coordinate operation needs an external resource, and that resource has not been
 * found. This exception may be thrown by coordinate operations that require a NADCON or NTv2 grid.
 * The resource identifiers are typically the grid filenames, but those files may be located in a
 * factory-dependent directory.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see org.geotoolkit.referencing.operation.transform.NadconTransform
 *
 * @since 3.20 (derived from 3.10)
 * @module
 */
public class NoSuchIdentifiedResource extends NoSuchIdentifierException {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7434897698526502211L;

    /**
     * Construct an exception with the specified detail message.
     *
     * @param  message The detail message. The detail message is saved
     *         for later retrieval by the {@link #getMessage()} method.
     * @param identifier The identifier of the resource which has not been found.
     */
    public NoSuchIdentifiedResource(final String message, final String identifier) {
        super(message, identifier);
    }

    /**
     * Construct an exception with the specified detail message and cause.
     * The cause is the exception thrown in the underlying data store
     * (e.g. {@link java.io.IOException} or {@link java.sql.SQLException}).
     *
     * @param  message The detail message. The detail message is saved
     *         for later retrieval by the {@link #getMessage()} method.
     * @param  identifier The identifier of the resource which has not been found.
     * @param  cause The cause for this exception. The cause is saved
     *         for later retrieval by the {@link #getCause()} method.
     */
    public NoSuchIdentifiedResource(final String message, final String identifier, final Throwable cause) {
        super(message, identifier);
        initCause(cause);
    }
}
