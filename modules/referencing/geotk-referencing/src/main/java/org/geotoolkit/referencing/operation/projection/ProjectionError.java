/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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


/**
 * Thrown when an assertion failed during a projection. Assertion failures are usually thrown
 * as {@link AssertionError}, but failures during projections are treated differently because
 * we can't be really sure that they will not happen (they are sensible to threshold values in
 * floating point comparisons). Declaring those assertion failures as a subclass of
 * {@link ProjectionException} allow applications - including our test suite - to handle those
 * exceptions in the way they usually do for normal projection exceptions.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
final class ProjectionError extends ProjectionException {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -6489823740587786542L;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message The details message, or {@code null} if none.
     */
    public ProjectionError(final String message) {
        super(message);
    }
}
