/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import static java.lang.Double.NaN;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.image.io.IIOListeners;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageStorePool;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.util.DateRange;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.sql.table.DefaultEntry;
import org.geotoolkit.internal.sql.table.IllegalRecordException;
import org.apache.sis.internal.referencing.AxisDirections;
import org.geotoolkit.resources.Errors;


/**
 * Implementation of {@linkplain GridCoverageReference coverage reference}.
 * This implementation is immutable and thread-safe.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Sam Hiatt
 * @version 3.15
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class GridCoverageEntry extends DefaultEntry implements GridCoverageReference {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -5725249398707248625L;

    /**
     * The grid geometry as a {@link GridGeometry2D} object.
     * Will be created only when first needed.
     */
    private transient GridGeometry2D geometry2D;

    /**
     * Image start time, inclusive.
     */
    private final long startTime;

    /**
     * Image end time, exclusive.
     */
    private final long endTime;

    /**
     * If the image is tiled, the tiles. Otherwise {@code null}.
     */
    private final TileManager[] tiles;

    /**
     * The value returned by {@link #getCoverage}, cached for reuse.
     */
    private transient Reference<GridCoverage2D> cached;

    /**
     * The loader currently in use, or {@code null} if none. Note that more than one loader can be
     * in use concurrently. The other loaders are {@linkplain GridCoverageLoader#nextInUse chained
     * to the current loader}.
     * <p>
     * Access to this chained list shall be synchronized on {@code this}.
     */
    private transient GridCoverageLoader currentReader;

    /**
     * Creates an entry containing coverage information (but not yet the coverage itself).
     *
     * @param  identifier The identifier of this grid geometry.
     * @param  startTime  The coverage start time, or {@code null} if none.
     * @param  endTime    The coverage end time, or {@code null} if none.
     * @param  tiles      If the image is tiled, the tiles. Otherwise {@code null}.
     * @param  comments   Optional remarks, or {@code null} if none.
     */
    protected GridCoverageEntry(final GridCoverageIdentifier identifier,
            final Date startTime, final Date endTime,
            final TileManager[] tiles, final String comments) throws SQLException
    {
        super(identifier, comments);
        this.startTime = (startTime != null) ? startTime.getTime() : Long.MIN_VALUE;
        this.  endTime = (  endTime != null) ?   endTime.getTime() : Long.MAX_VALUE;
        if (identifier.geometry.isEmpty() || this.startTime > this.endTime) {
            throw new IllegalRecordException(Errors.format(Errors.Keys.EMPTY_ENVELOPE_2D));
        }
        this.tiles = tiles;
    }

    /**
     * Returns the identifier of this {@code GridCoverageReference}.
     */
    @Override
    public final GridCoverageIdentifier getIdentifier() {
        return (GridCoverageIdentifier) identifier;
    }

    /**
     * Returns a name for the coverage, for use in graphical user interfaces.
     */
    @Override
    public String getName() {
        final GridCoverageIdentifier identifier = getIdentifier();
        final StringBuilder buffer = new StringBuilder(identifier.filename);
        final int index = identifier.imageIndex;
        if (index != 0) {
            buffer.append(':').append(index);
        }
        return buffer.toString();
    }

    /**
     * Returns the path to the image file as an object of the given type.
     */
    @Override
    public <T> T getFile(final Class<T> type) throws IOException {
        final Object input;
        final GridCoverageIdentifier identifier = getIdentifier();
        if (type.isAssignableFrom(File.class)) {
            input = identifier.file();
        } else {
            final boolean isURL = type.isAssignableFrom(URL.class);
            if (isURL || type.isAssignableFrom(URI.class)) try {
                final URI uri = identifier.uri();
                input = isURL ? uri.toURL() : uri;
            } catch (URISyntaxException e) {
                throw new IOException(e);
            } else {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOWN_TYPE_1, type));
            }
        }
        return type.cast(input);
    }

    /**
     * Returns the source as a {@link File} or an {@link URI}, in this preference order.
     * This method never returns {@code null}; if the URI can not be created, then an
     * exception is thrown.
     */
    final Object getInput() throws URISyntaxException {
        if (tiles != null) {
            return tiles;
        }
        final GridCoverageIdentifier identifier = getIdentifier();
        final File file = identifier.file();
        if (file.isAbsolute()) {
            return file;
        }
        return identifier.uri();
    }

    /**
     * Returns the image format.
     */
    @Override
    public String getImageFormat() {
        return getIdentifier().series.format.imageFormat;
    }

    /**
     * Returns the native Coordinate Reference System of the coverage.
     * The returned CRS may be up to 4-dimensional.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem(final boolean includeTime) {
        final GridGeometryEntry geometry = getIdentifier().geometry;
        return geometry.getSpatioTemporalCRS(includeTime);
    }

    /**
     * Returns the geographic bounding box of the {@linkplain #getEnvelope coverage envelope}.
     */
    @Override
    public GeographicBoundingBox getGeographicBoundingBox() {
        final GridGeometryEntry geometry = getIdentifier().geometry;
        try {
            return geometry.getGeographicBoundingBox();
        } catch (TransformException e) {
            // Returning 'null' is allowed by the method contract.
            Logging.recoverableException(GridCoverageReference.class, "getGeographicBoundingBox", e);
            return null;
        }
    }

    /**
     * Returns the spatio-temporal envelope of the coverage. The CRS of the returned envelope
     * is the {@linkplain #getCoordinateReferenceSystem(boolean) spatio-temporal CRS} of this
     * entry, which may vary on a coverage-by-coverage basis.
     */
    @Override
    public Envelope getEnvelope() {
        return getGridGeometry().getEnvelope();
    }

    /**
     * Returns the range of values in the two first dimensions, which are horizontal.
     */
    @Override
    public Rectangle2D getXYRange() {
        return getIdentifier().geometry.standardEnvelope.getBounds2D();
    }

    @Override
    public Number getZCenter() throws IOException {
        final NumberRange<?> range = getZRange();
        if (range != null) {
            final Number lower = range.getMinValue();
            final Number upper = range.getMaxValue();
            if (lower != null) {
                if (upper != null) {
                    return 0.5 * (lower.doubleValue() + upper.doubleValue());
                } else {
                    return lower.doubleValue();
                }
            } else if (upper != null) {
                return upper.doubleValue();
            }else{
                return NaN;
            }
        }else{
            return NaN;
        }
    }

    /**
     * Returns the range of values in the third dimension, which may be vertical or temporal.
     * This method returns the range in units of the database vertical or temporal CRS, which
     * may not be the same than the vertical or temporal CRS of the coverage.
     */
    @Override
    public NumberRange<Double> getZRange() {
        final GridGeometryEntry geometry = getIdentifier().geometry;
        double min = geometry.standardMinZ;
        double max = geometry.standardMaxZ;
        if (!(min <= max)) { // Use '!' for catching NaN values.
            min = Double.NEGATIVE_INFINITY;
            max = Double.POSITIVE_INFINITY;
            final DefaultTemporalCRS temporalCRS = geometry.getTemporalCRS();
            if (temporalCRS != null) {
                if (startTime != Long.MIN_VALUE) min = temporalCRS.toValue(new Date(startTime));
                if (  endTime != Long.MAX_VALUE) max = temporalCRS.toValue(new Date(  endTime));
            }
        }
        return NumberRange.create(min, true, max, false);
    }

    /**
     * Returns the temporal part of the {@linkplain #getEnvelope coverage envelope}.
     */
    @Override
    public DateRange getTimeRange() {
        return new DateRange((startTime != Long.MIN_VALUE) ? new Date(startTime) : null, true,
                               (endTime != Long.MAX_VALUE) ? new Date(  endTime) : null, false);
    }

    /**
     * Returns the coverage grid geometry.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public synchronized GridGeometry2D getGridGeometry() {
        if (geometry2D == null) {
            /*
             * If the grid coverage has a temporal dimension, we need to set the scale and offset
             * coefficients for it. Those coefficients need to be set on a coverage-by-coverage
             * basis since they are typically different for each coverage even if they share the
             * same GridGeometryEntry.
             */
            double min = Double.NEGATIVE_INFINITY;
            double max = Double.POSITIVE_INFINITY;
            final GridCoverageIdentifier identifier = getIdentifier();
            final GridGeometryEntry geometry = identifier.geometry;
            final DefaultTemporalCRS temporalCRS = geometry.getTemporalCRS();
            if (temporalCRS != null) {
                if (startTime != Long.MIN_VALUE) min = temporalCRS.toValue(new Date(startTime));
                if (  endTime != Long.MAX_VALUE) max = temporalCRS.toValue(new Date(  endTime));
            }
            final boolean hasTime = !Double.isInfinite(min) || !Double.isInfinite(max);
            final CoordinateReferenceSystem crs = geometry.getSpatioTemporalCRS(hasTime);
            final int dimension = crs.getCoordinateSystem().getDimension();
            final Matrix gridToCRS = geometry.getGridToCRS(dimension, identifier.zIndex);
            if (hasTime) {
                /*
                 * The code below makes the following assumptions,
                 * which are checked by the assert statements below:
                 *
                 *   1) The temporal dimension is the last dimension.
                 *   2) The temporal dimension is at the same index in
                 *      both the grid CRS and the "real world" CRS.
                 */
                assert AxisDirections.indexOfColinear(crs.getCoordinateSystem(),
                        temporalCRS.getCoordinateSystem()) == dimension-1 : crs;
                assert gridToCRS.getElement(dimension-1, dimension-1) != 0 : gridToCRS;
                gridToCRS.setElement(dimension-1, dimension-1, max - min);
                gridToCRS.setElement(dimension-1, dimension, min);
            }
            /*
             * At this point, the 'gridToCRS' matrix has been built.
             * Now, compute the GridEnvelope.
             */
            final Dimension size = geometry.getImageSize();
            final int[] lower = new int[dimension];
            final int[] upper = new int[dimension];
            switch (dimension) {
                default: Arrays.fill(upper, 2, dimension, 1); // Fall through for every cases.
                case 2:  upper[1] = size.height;
                case 1:  upper[0] = size.width;
                case 0:  break;
            }
            geometry2D = new GridGeometry2D(new GeneralGridEnvelope(lower, upper, false),
                    geometry.getPixelInCell(), MathTransforms.linear(gridToCRS), crs, null);
        }
        return geometry2D;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GridSampleDimension[] getSampleDimensions() {
        final List<GridSampleDimension> sd = getIdentifier().series.format.sampleDimensions;
        if (sd == null) {
            return null;
        }
        final GridSampleDimension[] bands = sd.toArray(new GridSampleDimension[sd.size()]);
        for (int i=0; i<bands.length; i++) {
            bands[i] = bands[i].geophysics(true);
        }
        return bands;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GridCoverageReader getCoverageReader(final GridCoverageReader recycle)
            throws CoverageStoreException
    {
        final GridCoverageIdentifier identifier = getIdentifier();
        final FormatEntry format = identifier.series.format;
        GridCoverageLoader reader;
        if (!(recycle instanceof GridCoverageLoader) ||
            !(reader = (GridCoverageLoader) recycle).format.equals(format))
        {
            reader = new GridCoverageLoader(format);
        }
        reader.setInput(this);
        return reader;
    }

    /**
     * Loads the data if needed and returns the coverage.
     * Note that the coverage is cached by the read method, since the envelope is null.
     */
    @Override
    public GridCoverage2D getCoverage(final IIOListeners listeners)
            throws IOException, CancellationException
    {
        try {
            return read((CoverageEnvelope) null, listeners);
        } catch (CoverageStoreException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw new IIOException(Errors.format(Errors.Keys.CANT_READ_FILE_1, getName()), e);
        }
    }

    /**
     * Reads the data in the given envelope and returns them as a coverage.
     * If the given envelope is {@code null}, then the whole coverage is loaded and cached.
     */
    @Override
    public GridCoverage2D read(final CoverageEnvelope envelope, final IIOListeners listeners)
            throws CoverageStoreException, CancellationException
    {
        GridCoverageReadParam param = null;
        if (envelope != null) {
            final Rectangle2D bounds = envelope.getHorizontalRange();
            if (!Double.isInfinite(bounds.getWidth()) || !Double.isInfinite(bounds.getHeight())) {
                param = new GridCoverageReadParam();
                param.setEnvelope(bounds, envelope.database.horizontalCRS);
            }
            final Dimension2D resolution = envelope.getPreferredResolution();
            if (resolution != null) {
                if (param == null) {
                    param = new GridCoverageReadParam();
                    param.setCoordinateReferenceSystem(envelope.database.horizontalCRS);
                }
                param.setResolution(resolution.getWidth(), resolution.getHeight());
            }
        }
        GridCoverage2D coverage;
        if (param != null) {
            // Do not use cache.
            coverage = read(param, listeners);
        } else {
            /*
             * This block is synchronized on identifier, which is a totally arbitrary lock. We
             * use that lock because we need something different than the lock used by abort().
             */
            synchronized (identifier) {
                if (cached != null) {
                    coverage = cached.get();
                    if (coverage != null) {
                        return coverage;
                    }
                    cached = null;
                }
                coverage = read(param, listeners);
                if (coverage != null) {
                    cached = new SoftReference<>(coverage);
                }
            }
        }
        return coverage;
    }

    /**
     * Reads the data using the given parameters and returns them as a coverage.
     */
    final GridCoverage2D read(final GridCoverageReadParam param, final IIOListeners listeners)
            throws CoverageStoreException, CancellationException
    {
        final GridCoverageIdentifier identifier = getIdentifier();
        final GridCoverageStorePool pool = identifier.series.format.getCoverageLoaders();
        final GridCoverageLoader reader = (GridCoverageLoader) pool.acquireReader();
        /*
         * Adds the reader to the list of readers currently in use.
         * This list will be used by 'abort()' if needed.
         */
        synchronized (this) {
            reader.nextInUse = currentReader;
            currentReader = reader;
        }
        GridCoverage2D coverage;
        try {
            reader.setInput(this);
            coverage = reader.read(0, param);
        } finally {
            /*
             * Removes the reader from the list of readers currently in use. Note that our
             * reader may not be anymore the head of the chained list, since new readers
             * could have been added concurrently to that list.
             */
            synchronized (this) {
                GridCoverageLoader p = currentReader;
                if (p == reader) {
                    currentReader = reader.nextInUse;
                } else {
                    while (p.nextInUse != reader) {
                        p = p.nextInUse; // A NullPointerException here would be a bug in our algorithm.
                    }
                    p.nextInUse = reader.nextInUse;
                }
            }
            reader.setInput(null); // Close the image input stream.
        }
        pool.release(reader);
        return coverage;
    }

    /**
     * Aborts all image reading which are in progress.
     */
    @Override
    public synchronized void abort() {
        for (GridCoverageLoader reader=currentReader; reader!=null; reader=reader.nextInUse) {
            reader.abort();
        }
    }

    /**
     * Returns {@code true} if the coverage represented by this entry has enough resolution
     * compared to the requested one. If this method doesn't have sufficient information,
     * then it conservatively returns {@code true}.
     *
     * @param requested The requested resolution in units of the database horizontal CRS.
     */
    final boolean hasEnoughResolution(final Dimension2D requested) {
        if (requested != null) try {
            final GridGeometryEntry geometry = getIdentifier().geometry;
            final Dimension2D resolution = geometry.getStandardResolution();
            if (resolution != null) {
                return resolution.getWidth()  <= requested.getWidth()  + SpatialRefSysEntry.EPS &&
                       resolution.getHeight() <= requested.getHeight() + SpatialRefSysEntry.EPS;
            }
        } catch (TransformException e) {
            Logging.recoverableException(GridCoverageEntry.class, "hasEnoughResolution", e);
        }
        return true;
    }

    /**
     * If two grid coverages have the same spatio-temporal envelope, return the one having the
     * coarsest resolution. If this method can not select an entry, it returns {@code null}.
     */
    final GridCoverageEntry selectCoarseResolution(final GridCoverageEntry that) {
        if (startTime == that.startTime && endTime == that.endTime) {
            final GridGeometryEntry geom1 = this.getIdentifier().geometry;
            final GridGeometryEntry geom2 = that.getIdentifier().geometry;
            if (geom1.sameEnvelope(geom2)) {
                final Dimension size1 = geom1.getImageSize();
                final Dimension size2 = geom2.getImageSize();
                if (size1.width <= size2.width && size1.height <= size2.height) return this;
                if (size1.width >= size2.width && size1.height >= size2.height) return that;
            }
        }
        return null;
    }

    /**
     * Compares two entries on the same criterion than the one used in the SQL {@code "ORDER BY"}
     * statement of {@link GridCoverageTable}). Entries without date are treated as unordered.
     */
    final boolean equalsAsSQL(final GridCoverageEntry other) {
        if (startTime == Long.MIN_VALUE && endTime == Long.MAX_VALUE) {
            return false;
        }
        return endTime == other.endTime;
    }

    /**
     * Compares this entry with the given object for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final GridCoverageEntry that = (GridCoverageEntry) object;
            if (startTime == that.startTime && endTime == that.endTime) {
                final GridGeometryEntry geom1 = this.getIdentifier().geometry;
                final GridGeometryEntry geom2 = that.getIdentifier().geometry;
                return Objects.equals(geom1, geom2);
            }
        }
        return false;
    }
}
