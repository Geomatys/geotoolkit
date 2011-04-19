/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.geometry;

import net.jcip.annotations.Immutable;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.resources.Errors;
import static org.geotoolkit.util.ArgumentChecks.*;


/**
 * Immutable representation of an {@linkplain Envelope envelope}. This class is final in order
 * to ensure that the immutability contract can not be broken (assuming not using <cite>Java
 * Native Interface</cite> or reflections).
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@Immutable
public final class ImmutableEnvelope extends AbstractEnvelope {
    /**
     * The coordinate reference system for this envelope.
     */
    private final CoordinateReferenceSystem crs;

    /**
     * The ordinate values.
     */
    private final double[] ordinates;

    /**
     * Creates an immutable envelope with the values of the given envelope.
     *
     * @param envelope The envelope to copy.
     */
    public ImmutableEnvelope(final Envelope envelope) {
        ensureNonNull("envelope", envelope);
        crs  = envelope.getCoordinateReferenceSystem();
        final int dim = envelope.getDimension();
        ordinates = new double[2*dim];
        for (int i=0; i<dim; i++){
            ordinates[i]       = envelope.getMinimum(i);
            ordinates[i + dim] = envelope.getMaximum(i);
        }
    }

    /**
     * Builds a two-dimensional envelope with the specified bounds.
     *
     * @param crs  The coordinate reference system, or {@code null} if none.
     * @param xmin The minimal value for the first ordinate.
     * @param xmax The maximal value for the first ordinate.
     * @param ymin The minimal value for the second ordinate.
     * @param ymax The maximal value for the second ordinate.
     */
    public ImmutableEnvelope(final CoordinateReferenceSystem crs, final double xmin,
                             final double xmax, final double ymin, final double ymax)
    {
        this.crs = crs;
        if (crs != null) {
            final int dim = crs.getCoordinateSystem().getDimension();
            if (dim != 2) {
                throw new MismatchedDimensionException(Errors.format(
                        Errors.Keys.MISMATCHED_DIMENSION_$3, "crs", dim, 2));
            }
        }
        ordinates = new double[] {
            xmin, ymin, xmax, ymax
        };
    }

    /**
     * Returns the envelope coordinate reference system, or {@code null} if unknown.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    /**
     * The length of coordinate sequence (the number of entries) in this envelope.
     */
    @Override
    public int getDimension() {
        return ordinates.length / 2;
    }

    /**
     * Returns the minimal ordinate along the specified dimension.
     *
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    @Override
    public double getMinimum(final int dimension) throws IndexOutOfBoundsException {
        ensureValidIndex(ordinates.length >>> 1, dimension);
        return ordinates[dimension];
    }

    /**
     * Returns the maximal ordinate along the specified dimension.
     *
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    @Override
    public double getMaximum(final int dimension) throws IndexOutOfBoundsException {
        ensureValidIndex(ordinates.length >>> 1, dimension);
        return ordinates[dimension + (ordinates.length >>> 1)];
    }

    /**
     * Returns the median ordinate along the specified dimension.
     *
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    @Override
    public double getMedian(final int dimension) throws IndexOutOfBoundsException {
        return (ordinates[dimension] + ordinates[dimension + ordinates.length/2]) / 2 ;
    }

    /**
     * Returns the envelope span (typically width or height) along the specified dimension.
     *
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    @Override
    public double getSpan(final int dimension) throws IndexOutOfBoundsException {
        return ordinates[dimension + ordinates.length/2] - ordinates[dimension];
    }
}
