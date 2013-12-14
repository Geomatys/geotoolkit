/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.projection;

import org.apache.sis.measure.Latitude;
import org.geotoolkit.resources.Errors;
import org.opengis.referencing.operation.TransformException;


/**
 * Thrown by {@link UnitaryProjection} when a map projection failed.
 *
 * @author André Gosselin (MPO)
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 1.0
 * @module
 */
public class ProjectionException extends TransformException {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 3031350727691500915L;

    /**
     * Constructs a new exception with no detail message.
     */
    public ProjectionException() {
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param code One of the constants suitable for {@link Errors#format(int)}.
     */
    ProjectionException(final short code) {
        this(Errors.format(code));
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param code One of the constants suitable for {@link Errors#format(int)}.
     * @param value An argument value to be formatted.
     */
    ProjectionException(final short code, final Object value) {
        this(Errors.format(code, value));
    }

    /**
     * Constructs a new exception with a detail message
     * formatted for a latitude too close from a pole.
     */
    ProjectionException(final double latitude) {
        this(Errors.format(Errors.Keys.POLE_PROJECTION_1, new Latitude(Math.toDegrees(latitude))));
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message The details message, or {@code null} if none.
     */
    public ProjectionException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     * The detais message is copied from the cause.
     *
     * @param cause The cause, or {@code null} if none.
     *
     * @since 2.5
     */
    public ProjectionException(final Throwable cause) {
        super(cause.getLocalizedMessage(), cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message The details message, or {@code null} if none.
     * @param cause   The cause, or {@code null} if none.
     */
    public ProjectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
