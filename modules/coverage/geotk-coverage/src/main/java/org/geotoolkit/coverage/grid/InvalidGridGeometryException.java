/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.grid;

import org.geotoolkit.resources.Errors;


/**
 * Thrown by {@link GeneralGridGeometry} when a grid geometry is in an invalid state. For example
 * this exception is thrown when {@link GeneralGridGeometry#getExtent()} is invoked while the grid
 * geometry were built with a null {@link org.opengis.coverage.grid.GridEnvelope}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public class InvalidGridGeometryException extends IllegalStateException {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -7386283388753448743L;

    /**
     * Constructs an exception with no detail message.
     */
    public InvalidGridGeometryException() {
    }

    /**
     * Constructs an exception with a detail message from the specified error code.
     * Should not be public because the Geotk I18N framework is not a committed one.
     */
    InvalidGridGeometryException(final short code) {
        super(Errors.format(code));
    }

    /**
     * Constructs an exception with the specified detail message.
     *
     * @param message The detail message.
     */
    public InvalidGridGeometryException(final String message) {
        super(message);
    }

    /**
     * Constructs an exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause The cause for this exception.
     */
    public InvalidGridGeometryException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
