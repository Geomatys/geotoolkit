/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.io;

import java.util.Set;
import java.util.EnumSet;
import javax.imageio.ImageReadParam;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.resources.Errors;


/**
 * Describes how a stream is to be decoded. Instances of this class are used to supply
 * information to instances of {@link GridCoverageReader}. The relationship between this
 * class and {@code GridCoverageReader} is similar to the relationship that exists in the
 * standard Java library between {@link ImageReadParam} and {@link javax.imageio.ImageReader}.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.09
 * @module
 */
public class GridCoverageReadParam {
    /**
     * The coordinate reference system of the coverage to return, or {@code null} if unspecified.
     * If the {@linkplain #envelope} is non-null, then it shall be the same CRS than the envelope
     * CRS.
     */
    private CoordinateReferenceSystem crs;

    /**
     * The region to read from the stream, or {@code null} if unspecified.
     */
    private GeneralEnvelope envelope;

    /**
     * The resolution, or {@code null} if unspecified.
     */
    private double[] resolution;

    /**
     * The set of parameters which should be used strictly as defined. By default, no parameter
     * is strict (i.e. this set is empty), which allow the {@link GridCoverageReader} to use more
     * efficient parameter values when possible. However users can add elements to this set for
     * forcing the {@code GridCoverageReader} to return a coverage matching exactly the parameters.
     * <p>
     * For example if the {@linkplain #getEnvelope() envelope} is not strict, then the reader
     * may use the intersection of the coverage envelope (as available in the store) with the
     * requested envelope. If the {@linkplain #getResolution() resolution} is not strict, then
     * the reader may use a finer resolution if it avoid the need for a resampling operation.
     * <p>
     * See {@link ParameterType} for a list of parameters that can be set to strict or not-strict
     * mode, and and explanation of what "non-strict" means for each parameter.
     */
    // TODO: Not yet public because not yet implemented.
    final Set<ParameterType> strictParameters = EnumSet.noneOf(ParameterType.class);

    /**
     * Creates a new {@code GridCoverageReadParam} instance. All properties are
     * initialized to {@code null}. Callers must invoke setter methods in order
     * to provide information about the way to decode the stream.
     */
    public GridCoverageReadParam() {
    }

    /**
     * Resets all parameters to their {@code null} value.
     */
    public void clear() {
        crs = null;
        envelope = null;
        resolution = null;
    }

    /**
     * Returns the CRS in which to resample the coverage, or {@code null} for
     * returning the coverage in its native CRS.
     *
     * @return The CRS in which to resample the coverage, or {@code null}.
     *
     * @see ParameterType#CRS
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    /**
     * Sets the CRS in which to resample the coverage. A {@code null} value means that
     * the coverage shall be returned in its native CRS.
     *
     * @param crs The new CRS in which to resample the coverage, or {@code null}.
     * @throws MismatchedDimensionException If the dimension of the given CRS is
     *         greater than the {@linkplain #getEnvelope() envelope} dimension
     *         or the {@linkplain #getResolution() resolution} dimension.
     *
     * @see ParameterType#CRS
     */
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs)
            throws MismatchedDimensionException
    {
        if (crs != null) {
            final int dimension = crs.getCoordinateSystem().getDimension();
            final int expected;
            if (envelope != null) {
                expected = envelope.getDimension();
            } else if (resolution != null) {
                expected = resolution.length;
            } else {
                expected = dimension;
            }
            if (dimension > expected) {
                throw dimensionMismatch("crs", dimension, expected);
            }
        }
        this.crs = crs;
    }

    /**
     * Ensures that the given dimension is compatible with the CRS dimension, if any.
     */
    private void ensureCompatibleDimension(final String name, final int dimension) {
        if (crs != null) {
            final int expected = crs.getCoordinateSystem().getDimension();
            if (dimension < expected) {
                throw dimensionMismatch(name, dimension, expected);
            }
        }
    }

    /**
     * Returns the exception to throw for a dimension mismatch.
     */
    private static MismatchedDimensionException dimensionMismatch(
            final String name, final int dimension, final int expected)
    {
        return new MismatchedDimensionException(Errors.format(
                Errors.Keys.MISMATCHED_DIMENSION_$3, name, dimension, expected));
    }

    /**
     * Returns the region to read from the stream, or {@code null} if unspecified.
     * If the {@linkplain Envelope#getCoordinateReferenceSystem() envelope CRS} is
     * not the same one than the CRS returned by {@link #getCoordinateReferenceSystem()},
     * then the envelope will be transformed to the later CRS at reading time.
     *
     * @return The region to read from the stream, or {@code null} if unspecified.
     *
     * @see ParameterType#ENVELOPE
     */
    public Envelope getEnvelope() {
        return (envelope != null) ? envelope.clone() : null;
    }

    /**
     * Sets the region to read from the stream. The envelope can be specified in any
     * {@linkplain CoordinateReferenceSystem Coordinate Reference System}; the envelope will
     * be transformed to the {@linkplain #getCoordinateReferenceSystem() requested CRS}
     * at reading time.
     * <p>
     * If the envelope is set to {@code null}, then {@link GridCoverageReader} will read
     * the full coverage extent in its native CRS.
     *
     * @param envelope The region to read from the stream, or {@code null}.
     * @throws MismatchedDimensionException If the dimension of the given envelope is smaller
     *         than the {@linkplain #getCoordinateReferenceSystem() CRS} dimension, or not
     *         equal to the {@linkplain #getResolution() resolution} dimension.
     *
     * @see ParameterType#ENVELOPE
     */
    public void setEnvelope(final Envelope envelope) throws MismatchedDimensionException {
        if (envelope != null) {
            final int dimension = envelope.getDimension();
            ensureCompatibleDimension("envelope", dimension);
            if (resolution != null) {
                final int expected = resolution.length;
                if (dimension != expected) {
                    throw dimensionMismatch("envelope", dimension, expected);
                }
            }
            this.envelope = new GeneralEnvelope(envelope);
        } else {
            this.envelope = null;
        }
    }

    /**
     * Returns the resolution to read from the stream, or {@code null} if unspecified.
     * The resolution shall be specified in the same {@linkplain CoordinateReferenceSystem
     * Coordinate Reference System} than the {@linkplain #getEnvelope() envelope} CRS.
     * This implies that the length of the returned array must match the
     * {@linkplain Envelope#getDimension() envelope dimension}.
     *
     * @return The resolution to read from the stream, or {@code null} if unspecified.
     *
     * @see ParameterType#RESOLUTION
     */
    public double[] getResolution() {
        return (resolution != null) ? resolution.clone() : null;
    }

    /**
     * Sets the resolution to read from the stream. The resolution shall be specified in the
     * same {@linkplain CoordinateReferenceSystem Coordinate Reference System} than the
     * {@linkplain #getEnvelope() envelope} CRS.
     * <p>
     * If the dimension is set to {@code null}, then {@link GridCoverageReader} will read
     * the coverage with the best resolution available.
     *
     * @param resolution The new resolution to read from the stream, or {@code null}.
     * @throws MismatchedDimensionException If the dimension of the given resolution is smaller
     *         than the {@linkplain #getCoordinateReferenceSystem() CRS} dimension, or not
     *         equal to the {@linkplain #getEnvelope() envelope} dimension.
     *
     * @see ParameterType#RESOLUTION
     */
    public void setResolution(double[] resolution) throws MismatchedDimensionException {
        if (resolution != null) {
            final int dimension = resolution.length;
            ensureCompatibleDimension("resolution", dimension);
            if (envelope != null) {
                final int expected = envelope.getDimension();
                if (dimension != expected) {
                    throw dimensionMismatch("resolution", dimension, expected);
                }
            }
            resolution = resolution.clone();
        }
        this.resolution = resolution;
    }
}
