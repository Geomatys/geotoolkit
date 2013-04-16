/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.util.List;
import java.util.Collections;
import java.util.concurrent.Future;
import java.sql.SQLException;
import java.awt.geom.Dimension2D;

import org.opengis.util.LocalName;
import org.opengis.util.FactoryException;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.referencing.CRS;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.display.shape.DoubleDimension2D;
import org.geotoolkit.util.collection.FrequencySortedSet;
import org.geotoolkit.image.io.IIOListeners;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.resources.Errors;


/**
 * A grid coverage reader for a layer. This class provides a way to read the data using only the
 * {@link GridCoverageReader} API, with {@linkplain #getInput() input} of kind {@link Layer}.
 * <p>
 * The {@link #read read} method actually reads two-dimensional slices selected according
 * the spatio-temporal envelope given to the {@link GridCoverageReadParam} argument.
 * <p>
 * <b>Usage example:</b>
 * {@preformat java
 *     CoverageDatabase      db     = new CoverageDatabase(...);
 *     LayerCoverageReader   reader = db.createGridCoverageReader("My layer");
 *     GridCoverageReadParam param  = new GridCoverageGridParam();
 *     param.setEnvelope(...);
 *     param.setResolution(...);
 *     GridCoverage coverage = reader.read(0, param);
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see CoverageDatabase#createGridCoverageReader(String)
 * @see CoverageDatabase#readSlice(String, CoverageEnvelope, IIOListeners)
 * @see GridCoverageReference#read(CoverageEnvelope, IIOListeners)
 *
 * @since 3.10
 * @module
 */
public class LayerCoverageReader extends GridCoverageReader {
    /**
     * The coverage database which created this {@code LayerCoverageReader}.
     */
    protected final CoverageDatabase database;

    /**
     * A temporary object used for computing the value to be given to the
     * {@link Layer#getCoverageReference(CoverageEnvelope)} method. Subclasses can use
     * this field for computation purpose, but its content shall not be presumed stable.
     */
    protected final CoverageEnvelope temporaryEnvelope;

    /**
     * The list of coverage names, computed when first needed.
     */
    private List<LocalName> names;

    /**
     * The most frequently used grid geometry.
     * This is computed when first needed.
     */
    private transient GeneralGridGeometry gridGeometry;

    /**
     * The most commonly used sample dimensions, or {@code null} if none.
     * This is computed when first needed.
     */
    private transient List<GridSampleDimension> sampleDimensions;

    /**
     * Metadata created when first needed.
     *
     * @since 3.16
     */
    private transient SpatialMetadata streamMetadata, imageMetadata;

    /**
     * Creates a new reader for the given database. The {@link #setInput(Object)}
     * method must be invoked before this reader can be used.
     *
     * @param database The database to used with this reader.
     */
    protected LayerCoverageReader(final CoverageDatabase database) {
        this.database = database;
        temporaryEnvelope = new CoverageEnvelope(database.database);
    }

    /**
     * Creates a new reader for the given database and initializes its layer to the given value.
     *
     * @throws CoverageStoreException Declared for compilation raison, but should never happen.
     */
    LayerCoverageReader(final CoverageDatabase database, final Future<Layer> layer)
            throws CoverageStoreException
    {
        this(database);
        super.setInput(layer);
    }

    /**
     * Returns the object to use for formatting error messages.
     */
    private Errors errors() {
        return Errors.getResources(getLocale());
    }

    /**
     * Ensures that the input is set.
     *
     * @throws CoverageStoreException Declared for compilation raison, but should never happen.
     */
    private void ensureInputSet() throws CoverageStoreException, IllegalStateException {
        if (super.getInput() == null) { // Use 'super' because we don't want to wait for Future.
            throw new IllegalStateException(errors().getString(Errors.Keys.NO_IMAGE_INPUT));
        }
    }

    /**
     * Ensures that the given coverage index is valid. Invoking
     * this method implies a call to {@link #ensureInputSet()}.
     *
     * @param index The coverage index.
     * @throws CoverageStoreException Declared for compilation raison, but should never happen.
     */
    private void ensureValidIndex(final int index) throws CoverageStoreException, IndexOutOfBoundsException {
        ensureInputSet();
        if (index != 0) {
            throw new IndexOutOfBoundsException(errors().getString(
                    Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, index));
        }
    }

    /**
     * Returns the current layer which is used as input, or {@code null} if none.
     */
    @Override
    public final Layer getInput() throws CoverageStoreException {
        Object input = super.getInput();
        if (input instanceof Future<?>) {
            input = ((FutureQuery<?>) input).result();
            super.setInput(input);
        }
        return (Layer) input;
    }

    /**
     * Sets a new layer as input. The given input can be either a {@link Layer} instance,
     * or the name of a layer as a {@link CharSequence}.
     *
     * @param input The new input as a {@link Layer} instance or a {@link CharSequence},
     *        or {@code null} for removing any input previously set.
     * @throws IllegalArgumentException If the given input is not of a legal type.
     */
    @Override
    public void setInput(Object input) throws CoverageStoreException {
        if (input != null) {
            if (input instanceof CharSequence) {
                input = database.getLayer(input.toString());
            } else if (!(input instanceof Layer)) {
                throw new IllegalArgumentException(errors().getString(Errors.Keys.ILLEGAL_CLASS_$2,
                        input.getClass(), Layer.class));
            }
        }
        clearCache();
        super.setInput(input);
    }

    /**
     * Returns the layer name.
     */
    @Override
    public List<LocalName> getCoverageNames() throws CoverageStoreException {
        ensureInputSet();
        if (names == null) {
            names = Collections.singletonList(FactoryFinder.getNameFactory(null)
                            .createLocalName(null, getInput().getName()));
        }
        return names;
    }

    /**
     * Returns the most commonly used grid geometry. If no grid geometry can be found
     * (for example because the layer doesn't contain any coverage), then this method
     * returns {@code null}.
     */
    @Override
    public GeneralGridGeometry getGridGeometry(final int index) throws CoverageStoreException {
        ensureValidIndex(index);
        if (gridGeometry == null) {
            for (final GeneralGridGeometry geometry : getInput().getGridGeometries()) {
                if (geometry != null) {
                    gridGeometry = geometry;
                    break;
                }
            }
        }
        return gridGeometry;
    }

    /**
     * Returns the most commonly used sample dimensions for each band of the {@link GridCoverage}
     * to be read. If sample dimensions are not known, then this method returns {@code null}.
     */
    @Override
    public List<GridSampleDimension> getSampleDimensions(final int index) throws CoverageStoreException {
        ensureValidIndex(index);
        if (sampleDimensions == null) {
            final FrequencySortedSet<SeriesEntry> series;
            try {
                series = ((LayerEntry) getInput()).getCountBySeries();
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
            final FrequencySortedSet<List<GridSampleDimension>> sd = new FrequencySortedSet<>(true);
            final int[] count = series.frequencies();
            int i = 0;
            for (final SeriesEntry entry : series) {
                sd.add(entry.format.sampleDimensions, count[i++]);
            }
            for (final List<GridSampleDimension> result : sd) {
                if (result != null) {
                    sampleDimensions = result;
                    break;
                }
            }
        }
        return sampleDimensions;
    }

    /**
     * Returns the metadata associated with the stream as a whole. This method fetches
     * the metadata from the database only; it does not attempt to read the image file.
     *
     * @since 3.16
     */
    @Override
    public SpatialMetadata getStreamMetadata() throws CoverageStoreException {
        ensureInputSet();
        SpatialMetadata metadata = streamMetadata;
        if (metadata == null) {
            streamMetadata = metadata = GridCoverageLoader.createStreamMetadata(
                    getInput().getGeographicBoundingBox());
        }
        return metadata;
    }

    /**
     * Returns the metadata associated with the given coverage. This method fetches the
     * metadata from the database only; it does not attempt to read the image file.
     *
     * @since 3.16
     */
    @Override
    public SpatialMetadata getCoverageMetadata(final int index) throws CoverageStoreException {
        ensureValidIndex(index);
        SpatialMetadata metadata = imageMetadata;
        if (metadata == null) {
            imageMetadata = metadata = GridCoverageLoader.createImageMetadata(getLocale(),
                    getSampleDimensions(index), getGridGeometry(index));
        }
        return metadata;
    }

    /**
     * Reads the data and return them as a coverage. The current implementation delegates to
     * {@link #readSlice(int, GridCoverageReadParam)}. A future implementation may return a
     * three-dimensional coverage.
     *
     * @param  index The image index (usually 0).
     * @param  param Optional read parameters (including the envelope and resolution to request),
     *         or {@code null} if none.
     * @return The coverage, or {@code null} if none.
     * @throws CoverageStoreException If an error occurred while querying the database or reading
     *         the image.
     */
    @Override
    public GridCoverage read(final int index, final GridCoverageReadParam param) throws CoverageStoreException {
        return readSlice(index, param);
    }

    /**
     * Reads the data of a two-dimensional slice and returns them as a coverage. Note that the
     * returned two-dimensional slice is not guaranteed to have exactly the requested envelope.
     * Callers may need to check the geometry of the returned envelope and perform an additional
     * resampling if needed.
     *
     * @param  index The image index (usually 0).
     * @param  param Optional read parameters (including the envelope and resolution to request),
     *         or {@code null} if none.
     * @return The coverage, or {@code null} if none.
     * @throws CoverageStoreException If an error occurred while querying the database or reading
     *         the image.
     *
     * @see CoverageDatabase#readSlice(String, CoverageEnvelope, IIOListeners)
     */
    public GridCoverage2D readSlice(final int index, final GridCoverageReadParam param) throws CoverageStoreException {
        ensureValidIndex(index);
        final Layer layer = getInput();
        CoverageEnvelope envelope = null;
        if (param != null) {
            envelope = temporaryEnvelope;
            Dimension2D hr = null;
            /*
             * Transforms the envelope and the resolution (if any) from the user envelope CRS
             * to the database CRS. In the particular case of the resolution, we will transform
             * an offset vector in the center of the intersection between the user envelope and
             * the layer envelope.
             */
            try {
                envelope.setEnvelope(param.getEnvelope()); // Null allowed.
                double[] resolution = param.getResolution();
                if (resolution != null && resolution.length >= 2) {
                    final CoordinateReferenceSystem crs = param.getCoordinateReferenceSystem();
                    if (crs != null) {
                        envelope.intersect(layer.getEnvelope(null, null));
                        final GeneralDirectPosition center = new GeneralDirectPosition(crs);
                        for (int i=center.getDimension(); --i>=0;) {
                            center.setOrdinate(i, envelope.getMedian(i));
                        }
                        resolution = CRS.deltaTransform(CRS.findMathTransform(crs,
                                envelope.database.horizontalCRS, true), center, resolution);
                    }
                    hr = new DoubleDimension2D(resolution[0], resolution[1]);
                }
            } catch (TransformException e) {
                throw new CoverageStoreException(errors().getString(
                        Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), e);
            } catch (FactoryException e) {
                throw new CoverageStoreException(e);
            }
            envelope.setPreferredResolution(hr);
        }
        /*
         * Now process to the image reading.
         */
        final GridCoverageEntry ref = (GridCoverageEntry) layer.getCoverageReference(envelope);
        if (ref != null) {
            final IIOListeners listeners = null; // TODO
            return ref.read(param, listeners);
        }
        return null;
    }

    /**
     * Clears the cached object. This method needs to be invoked when the input changed,
     * in order to force the calculation of new objects for the new input.
     */
    private void clearCache() {
        names            = null;
        gridGeometry     = null;
        sampleDimensions = null;
        streamMetadata   = null;
        imageMetadata    = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() throws CoverageStoreException {
        clearCache();
        super.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() throws CoverageStoreException {
        clearCache();
        super.dispose();
    }
}
