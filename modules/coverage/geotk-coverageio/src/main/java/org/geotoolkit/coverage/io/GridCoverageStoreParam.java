/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.io;

import java.util.Arrays;
import java.io.Serializable;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.MismatchedReferenceSystemException;

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.cs.AxisRangeType;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.internal.referencing.CRSUtilities;

import static org.geotoolkit.util.ArgumentChecks.ensurePositive;


/**
 * Base class for {@link GridCoverageReadParam} and {@link GridCoverageWriteParam}. This class
 * defines which part of the source (a stream when reading, or a coverage when writing) shall
 * be transfered to the destination (a coverage when reading, or a stream when writing).
 *
 * {@note This class is conceptually equivalent to the <code>IIOParam</code> class provided
 * in the standard Java library. The main difference is that <code>GridCoverageStoreParam</code>
 * works with geodetic coordinates while <code>IIOParam</code> works with pixel coordinates.}
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @see javax.imageio.IIOParam
 *
 * @since 3.14 (derived from 3.09)
 * @module
 */
public abstract class GridCoverageStoreParam implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 5654080292972651645L;

    /**
     * The coordinate reference system of the envelope and resolution specified in this object,
     * or {@code null} if unspecified.
     */
    private CoordinateReferenceSystem crs;

    /**
     * The region to read from the source, or {@code null} if unspecified.
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
     * Creates a new {@code GridCoverageStoreParam} instance. All properties are
     * initialized to {@code null}. Callers must invoke setter methods in order
     * to provide information about the way to decode or encode the stream.
     */
    protected GridCoverageStoreParam() {
    }

    /**
     * Creates a new {@code GridCoverageStoreParam} instance initialized to the same
     * values than the given parameters.
     *
     * @param param The parameters to copy, or {@code null} if none.
     *
     * @since 3.15
     */
    protected GridCoverageStoreParam(final GridCoverageStoreParam param) {
        if (param != null) {
            crs         = param.crs;
            envelope    = param.envelope;
            resolution  = param.resolution;
            sourceBands = param.sourceBands;
        }
    }

    /**
     * Resets all parameters to their {@code null} value.
     */
    public void clear() {
        crs         = null;
        envelope    = null;
        resolution  = null;
        sourceBands = null;
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
     * Returns the maximal extent of the region to read from the source, or {@code null} if
     * unspecified. If the {@linkplain Envelope#getCoordinateReferenceSystem() envelope CRS}
     * is not equals to the native CRS of the grid coverage to be read or written, then the
     * envelope will be transformed to the later CRS at reading or writing time.
     *
     * @return The region to read from the stream, or {@code null} if unspecified.
     *
     * @see javax.imageio.IIOParam#getSourceRegion()
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
     * Returns the {@linkplain #getEnvelope() envelope}, ensuring that it is contained
     * inside the coordinate system domain of validity.  This method also ensures that
     * the returned envelope is not a direct reference to the {@link #envelope} field,
     * so it is safe for modification.
     *
     * @param needsLongitudeShift {@code true} if the grid geometry needs longitude
     *        values in the [0…360]° range instead than the default [-180 … +180]°.
     */
    final GeneralEnvelope getValidEnvelope(final boolean needsLongitudeShift) {
        final Envelope env = getEnvelope();
        if (env == null) {
            return null;
        }
        final GeneralEnvelope ge;
        if (env instanceof GeneralEnvelope && env != envelope) {
            ge = (GeneralEnvelope) env;
        } else {
            ge = new GeneralEnvelope(env);
        }
        if (needsLongitudeShift) {
            ge.setCoordinateReferenceSystem(CRSUtilities.shiftAxisRange(
                    ge.getCoordinateReferenceSystem(), AxisRangeType.POSITIVE_LONGITUDE));
        }
        if (ge.reduceToDomain(false)) {
            ge.reorderCorners();
        }
        return ge;
    }

    /**
     * Sets the maximal extent of the region to read from the source. The actual envelope of
     * the destination (the coverage returned by {@link GridCoverageReader}, or the coverage
     * in the file written by {@link GridCoverageWriter}) may be smaller if the coverage
     * available in the source does not fill completely the given envelope.
     * <p>
     * The envelope can be specified in any {@linkplain Envelope#getCoordinateReferenceSystem()
     * Coordinate Reference System}, unless the CRS has been restricted by a call to the
     * {@link #setCoordinateReferenceSystem setCoordinateReferenceSystem} method.
     * The envelope CRS is honored as below:
     *
     * <ul>
     *   <li><p><b>At reading time</b>: {@link GridCoverageReader} may return a coverage in that CRS
     *       if it can do that cheaply (for example if the backing store already contains the same
     *       coverage in different CRS), or return the coverage in its native CRS otherwise, at
     *       implementation choice. Callers should check the CRS of the returned coverage.</p></li>
     *
     *   <li><p><b>At writing time</b>: {@link GridCoverageWriter} will reproject the coverage
     *       to that CRS, if needed. If the file format does not support that CRS, then an
     *       exception will be thrown.</p></li>
     * </ul>
     *
     * If the envelope is set to {@code null}, then {@code GridCoverageStore} will read
     * the full coverage extent in its native CRS.
     *
     * @param  envelope The region to read from the source, or {@code null}.
     * @throws MismatchedReferenceSystemException If the given CRS is not equal
     *         (ignoring metadata) to the CRS defined by the last call to
     *         {@link #setCoordinateReferenceSystem setCoordinateReferenceSystem}.
     * @throws MismatchedDimensionException If the dimension of the given envelope is not
     *         equal to the {@linkplain #getResolution() resolution} dimension.
     * @throws IllegalArgumentException If the given envelope is illegal for an other reason.
     *
     * @see javax.imageio.IIOParam#setSourceRegion(Rectangle)
     */
    public void setEnvelope(Envelope envelope) throws IllegalArgumentException {
        if (envelope != null) {
            ensureCompatibleCRS(envelope, crs);
            ensureCompatibleDimension(resolution, envelope);
            /*
             * Ensures that the envelope is non-empty.  The two dimensions to be read should
             * have a span greater than zero, while the extra dimension may have a span of 0.
             * We can not determine easily which dimensions will map to the rows and columns
             * (we would need to transform the envelope for that purpose, but we don't want
             * do do that here). As a compromise, we will just check that at least two
             * dimensions have a non-null span.
             */
            int dimension = 0;
            for (int i=envelope.getDimension(); --i>=0;) {
                if (envelope.getSpan(i) > 0) {
                    dimension++;
                }
            }
            if (dimension < 2) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.EMPTY_ENVELOPE_2D));
            }
            if (envelope instanceof Cloneable) {
                envelope = (Envelope) ((Cloneable) envelope).clone();
            }
        }
        this.envelope = envelope;
    }

    /**
     * Sets the maximal extent of the region to read from the source. This convenience method
     * performs the same work than {@link #setEnvelope(Envelope)}, except that the envelope is
     * created from the given {@linkplain Rectangle rectangle} and two-dimensional coordinate
     * reference system.
     *
     * @param  bounds The region to read from the source, or {@code null}.
     * @param  crs The two-dimensional coordinate reference system of the region.
     * @throws MismatchedReferenceSystemException If the given CRS is not equal
     *         (ignoring metadata) to the CRS defined by the last call to
     *         {@link #setCoordinateReferenceSystem setCoordinateReferenceSystem}.
     * @throws MismatchedDimensionException If dimension of the current
     *         {@linkplain #getResolution() resolution} is different than 2.
     *
     * @see javax.imageio.IIOParam#setSourceRegion(Rectangle)
     *
     * @since 3.10
     */
    public void setEnvelope(final Rectangle2D bounds, final CoordinateReferenceSystem crs)
            throws MismatchedReferenceSystemException, MismatchedDimensionException
    {
        setEnvelope(bounds != null ? new Envelope2D(crs, bounds) : null);
    }

    /**
     * Returns the resolution to read from the source, or {@code null} if unspecified.
     * The resolution shall be specified in the same {@linkplain CoordinateReferenceSystem
     * Coordinate Reference System} than the {@linkplain #getEnvelope() envelope} CRS.
     * This implies that the length of the returned array must match the
     * {@linkplain Envelope#getDimension() envelope dimension}.
     *
     * @return The resolution to read from the stream, or {@code null} if unspecified.
     *
     * @see javax.imageio.IIOParam#getSourceXSubsampling()
     * @see javax.imageio.IIOParam#getSourceYSubsampling()
     */
    public double[] getResolution() {
        return (resolution != null) ? resolution.clone() : null;
    }

    /**
     * Sets the resolution to read from the source. The resolution shall be specified in the
     * same {@linkplain CoordinateReferenceSystem Coordinate Reference System} than the
     * {@linkplain #getEnvelope() envelope} CRS.
     * <p>
     * The resolution is honored as below:
     *
     * <ul>
     *   <li><p><b>At reading time:</b> If the given resolution does not match a resolution that
     *       {@link GridCoverageReader} can read, then {@code GridCoverageReader} while use the
     *       largest {@code resolution} values which are equal or smaller (finer) than the given
     *       arguments. If no available resolutions are equal or finer than the given ones, then
     *       {@code GridCoverageReader} will use the finest resolution available.</p></li>
     *
     *   <li><p><b>At writing time:</b> {@link GridCoverageWriter} will resample the coverage to
     *       that resolution, if needed. If the file format does not support that resolution,
     *       then an exception will be thrown.</p></li>
     * </ul>
     *
     * If the dimension is set to {@code null}, then {@link GridCoverageStore} will read
     * the coverage with the best resolution available.
     *
     * @param resolution The new resolution to read from the source, or {@code null}.
     * @throws MismatchedDimensionException If the dimension of the given resolution is not
     *         equal to the {@linkplain #getEnvelope() envelope} dimension.
     *
     * @see javax.imageio.IIOParam#setSourceSubsampling(int, int, int, int)
     */
    public void setResolution(double... resolution) throws MismatchedDimensionException {
        ensureCompatibleDimension(resolution, crs);
        ensureCompatibleDimension(resolution, envelope);
        if (resolution != null) {
            resolution = resolution.clone();
            for (final double r : resolution) {
                // Accept 0 as well, meaning "best resolution available".
                ensurePositive("resolution", r);
            }
        }
        this.resolution = resolution;
    }

    /**
     * Returns the set of source bands to read, or {@code null} for all of them.
     *
     * @return The set of source bands to read, or {@code null} for all of them.
     *
     * @see javax.imageio.IIOParam#getSourceBands()
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
     * At the time of reading or writing, an {@link IllegalArgumentException} will be thrown by
     * the reader or writer if a value larger than the largest available source band index has
     * been specified or if the number of source bands and destination bands to be used differ.
     *
     * @param  bands The source bands to read, or {@code null}.
     * @throws IllegalArgumentException If the given array is empty,
     *         or if it contains duplicated or negative values.
     *
     * @see javax.imageio.IIOParam#setSourceBands(int[])
     *
     * @since 3.10
     */
    public void setSourceBands(final int... bands) throws IllegalArgumentException {
        sourceBands = checkAndClone(bands);
    }

    /**
     * Clones the given array and ensures that all numbers are positive and non-duplicated.
     *
     * @param  bands The array to check and clone, or {@code null}.
     * @return A clone of the given array, or {@code null}.
     * @throws IllegalArgumentException If the given array is empty,
     *         or if it contains duplicated or negative values.
     */
    static int[] checkAndClone(int[] bands) throws IllegalArgumentException {
        if (bands != null) {
            if (bands.length == 0) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.EMPTY_ARRAY));
            }
            bands = bands.clone();
            for (int i=0; i<bands.length; i++) {
                final int band = bands[i];
                if (band < 0) {
                    throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_BAND_NUMBER_$1, band));
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
     * The content of the returned string may change in any future version.
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
        toString(buffer, separator);
        return buffer.append(']').toString();
    }

    /**
     * Overloaded by the subclasses in order to complete the string built by {@link #toString()}.
     *
     * @param buffer    The buffer where to write the string.
     * @param separator The separator to put before to write anything in the buffer,
     *                  or {@code null} if none.
     */
    void toString(final StringBuilder buffer, String separator) {
    }
}
