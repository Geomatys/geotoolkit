/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage;

import java.util.Date;

import org.opengis.geometry.Envelope;
import org.opengis.coverage.PointOutsideCoverageException;

import org.geotoolkit.resources.Errors;


/**
 * Thrown when an {@code evaluate(...)} method method is invoked with a point outside coverage.
 * This subclass of {@code PointOutsideCoverage} exception is used when the dimension of the
 * out-of-bounds ordinate is known.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
public class OrdinateOutsideCoverageException extends PointOutsideCoverageException {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4718948524305632185L;

    /**
     * The dimension of the out-of-bounds ordinate.
     */
    private final int dimension;

    /**
     * The coverage envelope, or {@code null} if unknown.
     */
    private final Envelope envelope;

    /**
     * Creates an exception with the specified message.
     *
     * @param  message The detail message. The detail message is saved for
     *         later retrieval by the {@link #getMessage()} method.
     * @param  dimension The dimension of the out-of-bounds ordinate.
     */
    public OrdinateOutsideCoverageException(final String message, final int dimension) {
        super(message);
        this.dimension = dimension;
        this.envelope  = null;
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param  message The detail message. The detail message is saved for
     *         later retrieval by the {@link #getMessage()} method.
     * @param  dimension The dimension of the out-of-bounds ordinate.
     * @param  envelope The coverage envelope, or {@code null} if unknown.
     *
     * @since 2.3
     */
    public OrdinateOutsideCoverageException(final String message, final int dimension, final Envelope envelope) {
        super(message);
        this.dimension = dimension;
        this.envelope  = envelope;
    }

    /**
     * Creates an exception with the specified cause and an automaticaly formatted message. This
     * constructor assumes that the out-of-bounds value was the temporal ordinate (i.e. the date).
     * This condition should be verified before to invoke this constructor. A localized error
     * message including the specified date is then formatted.
     * <p>
     * This constructor is for internal use by {@code evaluate(Point2D, Date, ...)} methods in
     * {@link SpatioTemporalCoverage3D}, in order to replace dates as numerical values by a more
     * explicit string. Users can still get the numerical value if they looks at the cause of this
     * exception.
     */
    OrdinateOutsideCoverageException(final OrdinateOutsideCoverageException cause, final Date date) {
        super(Errors.format(Errors.Keys.DateOutsideCoverage_1, date));
        dimension = cause.dimension;
        envelope  = cause.envelope;
        initCause(cause);
    }

    /**
     * Returns the dimension of the out-of-bounds ordinate.
     *
     * @return The dimension of the out-of-bounds ordinate.
     */
    public int getOutOfBoundsDimension() {
        return dimension;
    }

    /**
     * Returns the coverage envelope, or {@code null} if unknown.
     *
     * @return the coverage envelope, or {@code null}.
     *
     * @since 2.3
     */
    public Envelope getCoverageEnvelope() {
        return envelope;
    }
}
