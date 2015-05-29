/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.io;


/**
 * Thrown when the {@linkplain GridCoverageReadParam#getEnvelope() envelope parameter}
 * given to a {@link GridCoverageReader} does not intersect the envelope of the stored
 * coverage.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public class DisjointCoverageDomainException extends CoverageStoreException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 3818685838021231218L;

    /**
     * Creates an exception with no cause and no details message.
     */
    public DisjointCoverageDomainException() {
        super();
    }

    /**
     * Creates an exception with the specified details message.
     *
     * @param message The detail message.
     */
    public DisjointCoverageDomainException(final String message) {
        super(message);
    }

    /**
     * Creates an exception with the specified cause and no details message.
     *
     * @param cause The cause for this exception.
     */
    public DisjointCoverageDomainException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates an exception with the specified details message and cause.
     *
     * @param message The detail message.
     * @param cause The cause for this exception.
     */
    public DisjointCoverageDomainException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
