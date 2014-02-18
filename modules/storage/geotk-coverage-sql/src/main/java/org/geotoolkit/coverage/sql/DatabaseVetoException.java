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
package org.geotoolkit.coverage.sql;

import org.geotoolkit.coverage.io.CoverageStoreException;


/**
 * Thrown when a proposed change in the content of a {@linkplain CoverageDatabase Coverage Database}
 * is not allowed. This exception can be thrown by {@linkplain CoverageDatabaseListener listeners}
 * associated with any {@code CoverageDatabase} instance.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.12
 * @module
 */
public class DatabaseVetoException extends CoverageStoreException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8529860269259521319L;

    /**
     * Creates an exception with no cause and no details message.
     */
    public DatabaseVetoException() {
        super();
    }

    /**
     * Creates an exception with the specified details message.
     *
     * @param message The detail message.
     */
    public DatabaseVetoException(final String message) {
        super(message);
    }

    /**
     * Creates an exception with the specified cause and no details message.
     *
     * @param cause The cause for this exception.
     */
    public DatabaseVetoException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates an exception with the specified details message and cause.
     *
     * @param message The detail message.
     * @param cause The cause for this exception.
     */
    public DatabaseVetoException(final String message, final Exception cause) {
        super(message, cause);
    }
}
