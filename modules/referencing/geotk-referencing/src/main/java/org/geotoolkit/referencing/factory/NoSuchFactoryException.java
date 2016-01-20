/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory;

import org.opengis.util.FactoryException;


/**
 * Thrown when a requested factory has not been found. This exception may be thrown by
 * {@link ThreadedAuthorityFactory#createBackingStore}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 * @module
 *
 * @deprecated Moved to {@link org.apache.sis.referencing.factory.UnavailableFactoryException} in Apache SIS.
 */
@Deprecated
public class NoSuchFactoryException extends FactoryException {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -661925454228937249L;

    /**
     * Construct an exception with no detail message.
     */
    public NoSuchFactoryException() {
    }

    /**
     * Construct an exception with the specified detail message.
     *
     * @param  message The detail message. The detail message is saved
     *         for later retrieval by the {@link #getMessage()} method.
     */
    public NoSuchFactoryException(String message) {
        super(message);
    }

    /**
     * Construct an exception with the specified cause.
     *
     * @param  cause The cause for this exception. The cause is saved
     *         for later retrieval by the {@link #getCause()} method.
     */
    public NoSuchFactoryException(Throwable cause) {
        super(cause.getLocalizedMessage(), cause);
    }

    /**
     * Construct an exception with the specified detail message and cause.
     * The cause is the exception thrown in the underlying database
     * (e.g. {@link java.io.IOException} or {@link java.sql.SQLException}).
     *
     * @param  message The detail message. The detail message is saved
     *         for later retrieval by the {@link #getMessage()} method.
     * @param  cause The cause for this exception. The cause is saved
     *         for later retrieval by the {@link #getCause()} method.
     */
    public NoSuchFactoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
