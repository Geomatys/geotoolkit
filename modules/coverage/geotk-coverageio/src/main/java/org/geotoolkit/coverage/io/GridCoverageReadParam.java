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

import java.util.Arrays;
import java.awt.geom.Rectangle2D;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.MismatchedReferenceSystemException;

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.util.converter.Classes;


/**
 * Describes how a stream is to be decoded. Instances of this class are used to supply
 * information to instances of {@link GridCoverageReader}.
 *
 * {@note This class is conceptually equivalent to the <code>ImageReadParam</code> class provided
 * in the standard Java library. The main difference is that <code>GridCoverageReadParam</code>
 * works with geodetic coordinates while <code>ImageReadParam</code> works with pixel coordinates.}
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.09
 * @module
 */
public class GridCoverageReadParam {
    /**
     * The coordinate reference system of the envelope and resolution specified in this object,
     * or {@code null} if unspecified.
     */
    private CoordinateReferenceSystem crs;

    /**
     * The region to read from the stream, or {@code null} if unspecified.
     */
    private Envelope envelope;

    /**
     * The resolution, or {@code null} if unspecified.
     */
    private double[] resolution;

    /**
     * The set of source bands to read, or {@code null} for all of them.
     */
    private int[] sourceBands;

    /**
     * The set of destination bands where data will be placed. By default, the value is
     * {@code null}, indicating that all destination bands should be written in order.
     */
    private int[] destinationBands;

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
     * Ensures that the CRS of the given envelope (if non-null) is equals, ignoring metadata,
     * to the given CRS.
     */
    private static void ensureCompatibleCRS(final Envelope envelope, final CoordinateReferenceSystem crs)
            throws MismatchedReferenceSystemException
    {
        if (crs != null && envelope != null) {
            final CoordinateReferenceSystem envelopeCRS = envelope.getCoordinateReferenceSystem();
            if (envelopeCRS != null && !CRS.equalsIgnoreMetadata(crs, envelopeCRS)) {
                throw new MismatchedReferenceSystemException(Errors.format(
                        Errors.Keys.MISMATCHED_COORDINATE_REFERENCE_SYSTEM));
            }
        }
    }

    /**
     * Ensures that the dimension of the given resolution array (if non-null)
     * is compatible with the CRS dimension, if any.
     */
    private static void ensureCompatibleDimension(final double[] resolution,
            final CoordinateReferenceSystem crs) throws MismatchedDimensionException
    {
        if (crs != null && resolution != null) {
            final int dimension = resolution.length;
            final int expected = crs.getCoordinateSystem().getDimension();
            if (dimension != expected) {
                throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_$2, dimension, expected));
            }
        }
    }

    /**
     * Ensures that the dimension of the given resolution array (if non-null)
     * is compatible with the dimension of the given envelope.
     */
    private static void ensureCompatibleDimension(final double[] resolution, final Envelope envelope)
            throws MismatchedDimensionException
    {
        if (resolution != null && envelope != null) {
            final int dimension = resolution.length;
            final int expected = envelope.getDimension();
            if (dimension != expected) {
                throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_$2, dimension, expected));
            }
        }
    }

    /**
     * Returns the CRS of the {@linkplain #getEnvelope() envelope} and {@link #getResolution()
     * resolution} parameters, or {@code null} if unspecified.
     *
     * @return The CRS of the envelope and resolution parameters, or {@code null}.
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        if (crs == null && envelope != null) {
            return envelope.getCoordinateReferenceSystem();
        }
        return crs;
    }

    /**
     * Sets the CRS of the {@linkplain #getEnvelope() envelope} and {@linkplain #getResolution()
     * resolution} parameters. If the envelope parameter is already defined with a different
     * CRS, then this method throws a {@link MismatchedReferenceSystemException}.
     *
     * @param  crs The new CRS for the envelope and resolution parameters.
     * @throws MismatchedReferenceSystemException If the {@linkplain #getEnvelope() envelope}
     *         parameter is already defined with a different CRS.
     * @throws MismatchedDimensionException If the {@linkplain #getResolution() resolution}
     *         parameter is already defined with a different dimension.
     */
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs)
            throws MismatchedReferenceSystemException, MismatchedDimensionException
    {
        ensureCompatibleCRS(envelope, crs);
        ensureCompatibleDimension(resolution, crs);
        this.crs = crs;
    }

    /**
     * Returns the maximal extent of the region to read from the stream, or {@code null} if
     * unspecified. If the {@linkplain Envelope#getCoordinateReferenceSystem() envelope CRS}
     * is not equals to the native CRS of the grid coverage to be read, then the envelope
     * will be transformed to the later CRS at reading time.
     *
     * @return The region to read from the stream, or {@code null} if unspecified.
     */
    public Envelope getEnvelope() {
        Envelope env = envelope;
        if (env != null) {
            if (crs != null && env.getCoordinateReferenceSystem() == null) {
                final GeneralEnvelope ge = new GeneralEnvelope(env);
                ge.setCoordinateReferenceSystem(crs);
                env = ge;
            } else if (env instanceof Cloneable) {
                env = (Envelope) ((Cloneable) env).clone();
            }
        }
        return env;
    }

    /**
     * Sets the maximal extent of the region to read from the stream. The actual envelope of
     * the coverage returned by {@link GridCoverageReader} may be smaller if the coverage
     * available in the stream does not fill completly the given envelope.
     * <p>
     * The envelope can be specified in any {@linkplain CoordinateReferenceSystem Coordinate
     * Reference System}. The {@code GridCoverageReader} may return a coverage in that CRS if
     * it can do that cheaply (for example if the backing store already contains the same
     * coverage in different CRS), or return the coverage in its native CRS otherwise.
     * <p>
     * If the envelope is set to {@code null}, then {@code GridCoverageReader} will read
     * the full coverage extent in its native CRS.
     *
     * @param  envelope The region to read from the stream, or {@code null}.
     * @throws MismatchedReferenceSystemException If the given CRS is not equal
     *         (ignoring metadata) to the CRS defined by the last call to
     *         {@link #setCoordinateReferenceSystem setCoordinateReferenceSystem}.
     * @throws MismatchedDimensionException If the dimension of the given envelope is not
     *         equal to the {@linkplain #getResolution() resolution} dimension.
     */
    public void setEnvelope(Envelope envelope)
            throws MismatchedReferenceSystemException, MismatchedDimensionException
    {
        ensureCompatibleCRS(envelope, crs);
        ensureCompatibleDimension(resolution, envelope);
        if (envelope instanceof Cloneable) {
            envelope = (Envelope) ((Cloneable) envelope).clone();
        }
        this.envelope = envelope;
    }

    /**
     * Sets the maximal extent of the region to read from the stream. This convenience method
     * performs the same work than {@link #setEnvelope(Envelope)}, except that the envelope is
     * created from the given rectangle and two-dimensional coordinate reference system.
     *
     * @param  bounds The region to read from the stream, or {@code null}.
     * @param  crs The two-dimensional coordinate reference system of the region.
     * @throws MismatchedReferenceSystemException If the given CRS is not equal
     *         (ignoring metadata) to the CRS defined by the last call to
     *         {@link #setCoordinateReferenceSystem setCoordinateReferenceSystem}.
     * @throws MismatchedDimensionException If dimension of the current
     *         {@linkplain #getResolution() resolution} is different than 2.
     *
     * @since 3.10
     */
    public void setEnvelope(final Rectangle2D bounds, final CoordinateReferenceSystem crs)
            throws MismatchedReferenceSystemException, MismatchedDimensionException
    {
        setEnvelope(bounds != null ? new Envelope2D(crs, bounds) : null);
    }

    /**
     * Returns the resolution to read from the stream, or {@code null} if unspecified.
     * The resolution shall be specified in the same {@linkplain CoordinateReferenceSystem
     * Coordinate Reference System} than the {@linkplain #getEnvelope() envelope} CRS.
     * This implies that the length of the returned array must match the
     * {@linkplain Envelope#getDimension() envelope dimension}.
     *
     * @return The resolution to read from the stream, or {@code null} if unspecified.
     */
    public double[] getResolution() {
        return (resolution != null) ? resolution.clone() : null;
    }

    /**
     * Sets the resolution to read from the stream. The resolution shall be specified in the
     * same {@linkplain CoordinateReferenceSystem Coordinate Reference System} than the
     * {@linkplain #getEnvelope() envelope} CRS.
     * <p>
     * If the given resolution does not match a resolution that {@link GridCoverageReader}
     * can read, then {@code GridCoverageReader} while use the largest {@code resolution}
     * values which are equal or smaller (finer) than the given arguments. If no available
     * resolutions are equal or finer than the given ones, then {@code GridCoverageReader}
     * will use the finest resolution available.
     * <p>
     * If the dimension is set to {@code null}, then {@link GridCoverageReader} will read
     * the coverage with the best resolution available.
     *
     * @param resolution The new resolution to read from the stream, or {@code null}.
     * @throws MismatchedDimensionException If the dimension of the given resolution is not
     *         equal to the {@linkplain #getEnvelope() envelope} dimension.
     */
    public void setResolution(double... resolution) throws MismatchedDimensionException {
        ensureCompatibleDimension(resolution, crs);
        ensureCompatibleDimension(resolution, envelope);
        if (resolution != null) {
            resolution = resolution.clone();
            for (final double r : resolution) {
                if (!(r >= 0)) { // Accept 0 as well, meaning "best resolution available".
                    throw new IllegalArgumentException(Errors.format(
                            Errors.Keys.NOT_GREATER_THAN_ZERO_$1, r));
                }
            }
        }
        this.resolution = resolution;
    }

    /**
     * Returns the set of source bands to read, or {@code null} for all of them.
     *
     * @return The set of source bands to read, or {@code null} for all of them.
     *
     * @since 3.10
     */
    public int[] getSourceBands() {
        final int[] bands = sourceBands;
        return (bands != null) ? bands.clone() : null;
    }

    /**
     * Sets the indices of the source bands to read. A {@code null} value indicates
     * that all source bands will be read.
     * <p>
     * At the time of reading, an {@link IllegalArgumentException} will be thrown by the reader
     * if a value larger than the largest available source band index has been specified or if
     * the number of source bands and destination bands to be used differ.
     *
     * @param  bands The source bands to read, or {@code null}.
     * @throws IllegalArgumentException If the given array is empty,
     *         or if it contains duplicated or negative values.
     *
     * @since 3.10
     */
    public void setSourceBands(final int... bands) throws IllegalArgumentException {
        sourceBands = checkAndClone(bands);
    }

    /**
     * Returns the set of destination bands where data will be placed. By default, the value
     * is {@code null}, indicating that all destination bands should be written in order.
     *
     * @return The set of destination bands where data will be placed, or {@code null}.
     *
     * @since 3.10
     */
    public int[] getDestinationBands() {
        final int[] bands = destinationBands;
        return (bands != null) ? bands.clone() : null;
    }

    /**
     * Sets the indices of the destination bands where data will be placed. A null value
     * indicates that all destination bands will be used.
     * <p>
     * At the time of reading, an {@link IllegalArgumentException} will be thrown by the reader
     * if a value larger than the largest destination band index has been specified or if the
     * number of source bands and destination bands to be used differ.
     *
     * @param  bands The destination bands, or {@code null}.
     * @throws IllegalArgumentException If the given array is empty,
     *         or if it contains duplicated or negative values.
     *
     * @since 3.10
     */
    public void setDestinationBands(final int... bands) throws IllegalArgumentException {
        destinationBands = checkAndClone(bands);
    }

    /**
     * Clones the given array and ensures that all numbers are positive and non-duplicated.
     *
     * @param  bands The array to check and clone, or {@code null}.
     * @return A clone of the given array, or {@code null}.
     * @throws IllegalArgumentException If the given array is empty,
     *         or if it contains duplicated or negative values.
     */
    private static int[] checkAndClone(int[] bands) throws IllegalArgumentException {
        if (bands != null) {
            if (bands.length == 0) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.EMPTY_ARRAY));
            }
            bands = bands.clone();
            for (int i=0; i<bands.length; i++) {
                final int band = bands[i];
                if (band < 0) {
                    throw new IllegalArgumentException(Errors.format(Errors.Keys.BAD_BAND_NUMBER_$1, band));
                }
                for (int j=i; --j>=0;) {
                    if (band == bands[j]) {
                        throw new IllegalArgumentException(Errors.format(
                                Errors.Keys.DUPLICATED_VALUE_$1, band));
                    }
                }
            }
        }
        return bands;
    }

    /**
     * Returns a string representation of this object for debugging purpose.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this)).append('[');
        String separator = "";
        if (envelope != null) {
            buffer.append("envelope=").append(envelope);
            separator = ", ";
        }
        if (resolution != null) {
            buffer.append(separator).append("resolution=").append(Arrays.toString(resolution));
            separator = ", ";
        }
        final CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
        if (crs != null) {
            buffer.append(separator).append("crs=\"").append(crs.getName().getCode()).append('"');
            separator = ", ";
        }
        if (sourceBands != null) {
            buffer.append(separator).append("sourceBands=").append(Arrays.toString(sourceBands));
            separator = ", ";
        }
        if (destinationBands != null) {
            buffer.append(separator).append("destinationBands=").append(Arrays.toString(destinationBands));
        }
        return buffer.append(']').toString();
    }
}
