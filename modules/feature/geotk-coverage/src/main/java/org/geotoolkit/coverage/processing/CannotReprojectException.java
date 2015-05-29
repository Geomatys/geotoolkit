/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.processing;


/**
 * Throws when a {@code "Resample"} operation has been requested
 * but the specified grid coverage can't be reprojected.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
public class CannotReprojectException extends CoverageProcessingException {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8145425848361056027L;

    /**
     * Creates a new exception without detail message.
     */
    public CannotReprojectException() {
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message The detail message.
     */
    public CannotReprojectException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause The cause of this exception.
     */
    public CannotReprojectException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause The cause of this exception.
     */
    public CannotReprojectException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
