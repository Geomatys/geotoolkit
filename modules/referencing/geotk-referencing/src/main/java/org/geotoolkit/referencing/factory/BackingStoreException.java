/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.io.IOException;
import java.sql.SQLException;
import org.opengis.referencing.FactoryException;


/**
 * Thrown to indicate that an {@link IdentifiedObjectSet} operation could not complete because of a
 * failure in the backing store, or a failure to contact the backing store. This exception usually
 * has an {@link IOException} or a {@link SQLException} as its {@linkplain #getCause cause}.
 * <p>
 * This exception may be throw by collection implementations (especially {@link IdentifiedObjectSet})
 * that are not allowed to throw checked exception.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 * @module
 *
 * @deprecated Moved to the {@link org.geotoolkit.util.collection} package.
 */
@Deprecated
public class BackingStoreException extends org.geotoolkit.util.collection.BackingStoreException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 4257200758051575441L;

    /**
     * Constructs a new exception with no detail message.
     */
    public BackingStoreException() {
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message, saved for later retrieval by the {@link #getMessage} method.
     */
    public BackingStoreException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause, saved for later retrieval by the {@link #getCause} method.
     */
    public BackingStoreException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message, saved for later retrieval by the {@link #getMessage} method.
     * @param cause the cause, saved for later retrieval by the {@link #getCause} method.
     */
    public BackingStoreException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
