/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
import java.util.List;
import java.util.Arrays;
import java.util.Locale;
import java.util.Collections;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.spi.ImageReaderSpi;

import org.opengis.util.InternationalString;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.metadata.spatial.PixelOrientation;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageStorePool;
import org.geotoolkit.image.io.mosaic.MosaicImageReader;
import org.geotoolkit.image.io.SpatialImageReadParam;
import org.geotoolkit.image.io.DimensionSlice;
import org.geotoolkit.image.io.NamedImageStore;
import org.geotoolkit.image.io.SampleConversionType;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.coverage.TransferFunction;
import org.geotoolkit.internal.image.io.DiscoveryAccessor;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.math.XMath;
import org.geotoolkit.referencing.operation.transform.LinearTransform;


/**
 * An implementation of {@link ImageCoverageReader} when the {@link GridGeometry2D} and the
 * {@link GridSampleDimension}s are obtained from the database instead than from the file.
 * <p>
 * The values given to the {@link #setInput(Object)} method must be instances
 * of {@link GridCoverageEntry}. The caller shall {@linkplain #reset() reset}
 * or {@linkplain #dispose() dispose} the reader as soon as the reading is
 * finished, in order to close the underlying input stream.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.10
 * @module
 */
final class GridCoverageLoader extends ImageCoverageReader {
    /**
     * A description of the image format.
     */
    final FormatEntry format;

    /**
     * The entry for the grid coverage to be read.
     */
    private GridCoverageEntry entry;

    /**
     * The index of the image to be read.
     * It must be defined by the caller before to read an image.
     */
    int imageIndex;

    /**
     * The expected image size. It must be defined by the caller before to read an image.
     */
    Dimension expectedSize;

    /**
     * For internal usage by {@link GridCoverageEntry} only.
     */
    transient GridCoverageLoader nextInUse;

    /**
     * Creates a new reader. This constructor sets {@link #ignoreMetadata} to
     * {@code true} because the required metadata are provided by the database.
     */
    public GridCoverageLoader(final FormatEntry format) {
        this.format     = format;
        seekForwardOnly = Boolean.TRUE;
        ignoreMetadata  = Boolean.TRUE;
    }

    /**
     * Returns that the given image index is zero.
     */
    private void ensureValidIndex(final int index) {
        if (index != 0) {
            throw new IllegalArgumentException(Errors.getResources(getLocale())
                    .getString(Errors.Keys.ILLEGAL_ARGUMENT_$2, "imageIndex", index));
        }
    }

    /**
     * Ensures that the input is set, and returns it for convenience.
     */
    private GridCoverageEntry ensureInputSet() {
        final GridCoverageEntry entry = this.entry;
        if (entry == null) {
            throw new IllegalArgumentException(Errors.getResources(getLocale())
                    .getString(Errors.Keys.NO_IMAGE_INPUT));
        }
        return entry;
    }

    /**
     * Sets the input, which must be a {@link GridCoverageEntry} using the image format
     * given at construction time. In addition, if the image reader is an instance of
     * {@link NamedImageStore}, then this method sets the name of NetCDF (or similar format)
     * variable to read as the names declared in the {@code SampleDimensions} table.
     */
    @Override
    public void setInput(Object input) throws CoverageStoreException {
        while (input instanceof GridCoverageDecorator) {
            input = ((GridCoverageDecorator) input).reference;
        }
        if (input == entry) {
            return;
        }
        final GridCoverageEntry e = (GridCoverageEntry) input;
        if (e != null) {
            assert format.equals(e.getIdentifier().series.format) : e;
            try {
                input = e.getInput();
            } catch (URISyntaxException ex) {
                throw new CoverageStoreException(ex);
            }
        }
        super.setInput(input);
        entry = e; // Set the field only on success.
        /*
         * For the NetCDF format, set the names of the variables to read.
         * The names declared in the SampleDimensions table.
         */
        if (input != null && imageReader instanceof NamedImageStore) {
            String[] names = null;
            String imageName = null;
            final NamedImageStore store = (NamedImageStore) imageReader;
            final List<GridSampleDimension> bands = format.sampleDimensions;
            if (bands != null) {
                names = new String[bands.size()];
                for (int i=0; i<names.length; i++) {
                    names[i] = bands.get(i).getDescription().toString();
                }
                imageName = names[0];
            }
            try {
                store.setImageNames(imageName);
                store.setBandNames(0, names);
            } catch (IOException ex) {
                throw new CoverageStoreException(ex);
            }
        }
    }

    /**
     * Returns {@code true} if the given provider is suitable for the image format
     * expected by the current entry. This implementation returns {@code true} in
     * all cases, since we are supposed to recycle the same reader.
     */
    @Override
    protected boolean canReuseImageReader(final ImageReaderSpi provider, final Object input) throws IOException {
        assert (provider instanceof MosaicImageReader.Spi) || // The format name of this provider is "mosaic".
                XArrays.containsIgnoreCase(provider.getFormatNames(), format.imageFormat) ||
                XArrays.containsIgnoreCase(provider.getMIMETypes(),   format.imageFormat) : format;
        return true;
    }

    /**
     * Creates an {@link ImageReader} that claim to be able to decode the given input.
     * This method is invoked automatically by {@link #setInput(Object)} for assigning
     * a value to the {@link #imageReader} field.
     */
    @Override
    protected ImageReader createImageReader(final Object input) throws IOException {
        if (MosaicImageReader.Spi.DEFAULT.canDecodeInput(input)) {
            return MosaicImageReader.Spi.DEFAULT.createReaderInstance();
        }
        final String imageFormat = format.imageFormat;
        final boolean isMIME = imageFormat.indexOf('/') >= 0;
        if (isMIME) {
            return XImageIO.getReaderByMIMEType(imageFormat, input, seekForwardOnly, ignoreMetadata);
        } else {
            return XImageIO.getReaderByFormatName(imageFormat, input, seekForwardOnly, ignoreMetadata);
        }
    }

    /**
     * Returns the name of the coverages to be read. This implementations
     * assumes that there is exactly one coverage per entry.
     */
    @Override
    public List<String> getCoverageNames() throws CoverageStoreException {
        return Collections.singletonList(ensureInputSet().getName());
    }

    /**
     * Returns the grid geometry which is declared in the database.
     */
    @Override
    public GridGeometry2D getGridGeometry(int index) throws CoverageStoreException {
        ensureValidIndex(index);
        return ensureInputSet().getGridGeometry();
    }

    /**
     * Returns the sample dimensions for each band of the {@code GridCoverage} to be read.
     * This method returns the sample dimensions declared in the database rather then inferring
     * them from the image metadata.
     * <p>
     * If the sample dimensions are not known, then this method returns {@code null}.
     */
    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException {
        ensureValidIndex(index);
        return format.sampleDimensions;
    }

    /**
     * Returns the metadata associated with the stream as a whole.
     * This method fetches the metadata from the database only, ignoring the file metadata.
     */
    @Override
    public SpatialMetadata getStreamMetadata() throws CoverageStoreException {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.STREAM);
        if (entry != null) {
            final DiscoveryAccessor accessor = new DiscoveryAccessor(metadata) {
                @Override protected double nice(final double value) {
                    return GridCoverageLoader.nice(value);
                }
            };
            accessor.setGeographicElement(entry.getGeographicBoundingBox());
        }
        return metadata;
    }

    /**
     * Returns the metadata associated with the given coverage.
     * This method fetches the metadata from the database only, ignoring the file metadata.
     */
    @Override
    public SpatialMetadata getCoverageMetadata(final int index) throws CoverageStoreException {
        final List<GridSampleDimension> bands = getSampleDimensions(index);
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        if (bands != null) {
            final Locale locale = getLocale();
            final DimensionAccessor accessor = new DimensionAccessor(metadata);
            for (GridSampleDimension band : bands) {
                accessor.selectChild(accessor.appendChild());
                final InternationalString title = band.getDescription();
                if (title != null) {
                    accessor.setDescriptor(title.toString(locale));
                }
                /*
                 * Add the range of geophysics values to the metadata.
                 */
                band = band.geophysics(true);
                accessor.setValueRange(nice(band.getMinimumValue()), nice(band.getMaximumValue()));
                accessor.setUnits(band.getUnits());
                /*
                 * Add the range of sample values to the metadata. Those values should
                 * be integers, because the type of the "lower" and "upper" columns in
                 * the database are integers.
                 */
                TransferFunction tf = null;
                band = band.geophysics(false);
                NumberRange<?> range = null;
                int fillValues[] = new int[8];
                int fillValuesCount = 0;
                for (final Category category : band.getCategories()) {
                    final NumberRange<?> r = category.getRange();
                    if (category.isQuantitative()) {
                        range = (range == null) ? r : range.union(r);
                        tf = new TransferFunction(category, locale);
                    } else {
                        final int lower = (int) Math.round(r.getMinimum(true));
                        final int upper = (int) Math.round(r.getMaximum(true));
                        for (int i=lower; i<=upper; i++) {
                            if (fillValuesCount >= fillValues.length) {
                                fillValues = Arrays.copyOf(fillValues, fillValuesCount*2);
                            }
                            fillValues[fillValuesCount++] = i;
                        }
                    }
                }
                accessor.setValidSampleValue(range);
                if (fillValuesCount != 0) {
                    accessor.setFillSampleValues(XArrays.resize(fillValues, fillValuesCount));
                }
                /*
                 * Add the transfert function.
                 */
                if (tf != null) {
                    accessor.setTransfertFunction(nice(tf.scale), nice(tf.offset), tf.type);
                }
            }
        }
        /*
         * Add the "SpatialRepresentation" and "RectifiedGridDomain" nodes.
         */
        final GridGeometry2D geometry = getGridGeometry(index);
        if (geometry != null) {
            final MathTransform      gridToCRS    = geometry.getGridToCRS(PixelInCell.CELL_CORNER);
            final GridDomainAccessor accessor     = new GridDomainAccessor(metadata);
            final GridEnvelope       gridEnvelope = geometry.getGridRange();
            final int                dimension    = gridToCRS.getTargetDimensions();
            final double[]           ordinates    = new double[dimension];
            for (int i=gridEnvelope.getDimension(); --i>=0;) {
                ordinates[i] = 0.5 * (gridEnvelope.getLow(i) + gridEnvelope.getHigh(i));
            }
            try {
                gridToCRS.transform(ordinates, 0, ordinates, 0, 1);
                accessor.setSpatialRepresentation(nice(ordinates), null, PixelOrientation.UPPER_LEFT);
            } catch (TransformException e) {
                // Should not happen. If it happen anyway, this is not a fatal error.
                // The above metadata will be missing from the IIOMetadata object, but
                // they were mostly for information purpose anyway.
                Logging.unexpectedException(GridCoverageLoader.class, "getCoverageMetadata", e);
            }
            if (gridToCRS instanceof LinearTransform) {
                final Matrix matrix = ((LinearTransform) gridToCRS).getMatrix();
                final int lastColumn = matrix.getNumCol() - 1;
                for (int j=0; j<dimension; j++) {
                    ordinates[j] = matrix.getElement(j, lastColumn);
                }
                accessor.setOrigin(nice(ordinates));
                for (int j=0; j<dimension; j++) {
                    for (int i=0; i<lastColumn; i++) {
                        ordinates[i] = matrix.getElement(j, i);
                    }
                    accessor.addOffsetVector(nice(ordinates));
                }
            }
        }
        return metadata;
    }

    /**
     * Returns read parameters with the z-slice initialized, if needed. In addition, if the image
     * reader is an instance of {@link NamedImageStore} (typically the NetCDF reader), sets the
     * image index API to the temporal dimension. The later is consistent with:
     * <p>
     * <ul>
     *   <li>The database schema, where the image index is specified together with the
     *       date range in the {@code GridCoverages} table.</li>
     *   <li>The work done by {@link #setInput(Object)} for the {@link NamedImageStore}
     *       case, which sets the image reader has if it had only one image (before we
     *       map the image index API to the temporal dimension).</li>
     * </ul>
     */
    @Override
    protected ImageReadParam createImageReadParam(final int index) throws IOException {
        final ImageReadParam param = super.createImageReadParam(index);
        final int zIndex = ensureInputSet().getIdentifier().zIndex;
        if (zIndex != 0) {
            if (param instanceof SpatialImageReadParam) {
                final DimensionSlice slice = ((SpatialImageReadParam) param).newDimensionSlice();
                slice.addDimensionId(AxisDirection.UP, AxisDirection.DOWN);
                slice.setSliceIndex(zIndex - 1);
            }
        }
        if (imageReader instanceof NamedImageStore) {
            if (param instanceof SpatialImageReadParam) {
                final DimensionSlice slice = ((SpatialImageReadParam) param).newDimensionSlice();
                slice.addDimensionId(AxisDirection.FUTURE, AxisDirection.PAST);
                slice.setAPI(DimensionSlice.API.IMAGES);
            }
        }
        /*
         * Sets the palette name as a safety, but this is actually not used by geotk-coverage-sql
         * because SampleDimensionPalette.createImageTypeSpecifier() will use the information
         * declared in the GridSampleDimensions.
         *
         * If the sample values are already geophysics, enable the conversion from integer type
         * to floating point type in order to allow the image reader to replace fill values by
         * NaN during the read process.
         *
         * Otherwise, if the format is declared "native" (i.e. it should describe precisely
         * how the sample values are stored on disk, without offset of negative values), then
         * overwrite the image metadata with the database values. This allow correct results
         * when the image metadata are incomplete or inaccurate.
         */
        if (param instanceof SpatialImageReadParam) {
            final SpatialImageReadParam sp = (SpatialImageReadParam) param;
            sp.setPaletteName(format.paletteName);
            switch (format.viewType) {
                case GEOPHYSICS: {
                    sp.setSampleConversionAllowed(SampleConversionType.STORE_AS_FLOATS, true);
                    break;
                }
                case NATIVE: {
                    sp.setSampleDomains(format.sampleDomains);
                    break;
                }
            }
        }
        return param;
    }

    /**
     * Reads the grid coverage. This method checks that the size of the image is the same as
     * the size declared in the database. This check is only used to catch possible errors that
     * would otherwise slip into the database or during the copy of the image to the disk.
     */
    @Override
    public GridCoverage2D read(int index, final GridCoverageReadParam param)
            throws CoverageStoreException
    {
        ensureValidIndex(index);
        index = imageIndex;
        final int expectedWidth  = expectedSize.width;
        final int expectedHeight = expectedSize.height;
        final int imageWidth, imageHeight;
        final ImageReader imageReader = this.imageReader; // Protect from changes.
        try {
            imageWidth  = imageReader.getWidth (index);
            imageHeight = imageReader.getHeight(index);
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        if (expectedWidth != imageWidth || expectedHeight != imageHeight) {
            throw new CoverageStoreException(Errors.getResources(getLocale()).getString(Errors.Keys.IMAGE_SIZE_MISMATCH_$5,
                    IOUtilities.name(getInputName()), imageWidth, imageHeight, expectedWidth, expectedHeight));
        }
        GridCoverage2D coverage = super.read(index, param);
        /*
         * The GridCoverageReference.read(...) contract requires that we return
         * always the geophysics view, when available.
         */
        if (coverage != null) {
            coverage = coverage.view(ViewType.GEOPHYSICS);
        }
        return coverage;
    }

    /**
     * Returns the name of the input.
     */
    private String getInputName() throws CoverageStoreException {
        final Object input = getInput();
        if (IOUtilities.canProcessAsPath(input)) {
            return IOUtilities.name(input);
        } else {
            return entry.toString();
        }
    }

    /**
     * Returns an error message for the given exception. If the {@linkplain #input input} is
     * known, this method returns "<cite>Can't read 'the name'</cite>" followed by the cause
     * message. Otherwise it returns the localized message of the given exception.
     */
    private String formatErrorMessage(final Exception e) throws CoverageStoreException {
        final String cause = e.getLocalizedMessage();
        String message = Errors.getResources(getLocale()).getString(Errors.Keys.CANT_READ_$1, getInputName());
        if (cause != null && cause.indexOf(' ') > 0) { // Append only if we have a sentence.
            message = message + '\n' + cause;
        }
        return message;
    }

    /**
     * Converts the given number into something nicer to display. This is used only for
     * formatting attributes in {@link IIOMetadata} and is not used for computation purpose.
     */
    private static double nice(final double value) {
        final double t1 = value * 3600000;
        final double t2 = XMath.roundIfAlmostInteger(t1, 12);
        return (t1 != t2) ? t2 / 3600000 : value;
    }

    /**
     * Invokes {@link #nice(double)} for all elements in the given array.
     * Values in the given array will be modified in-place.
     */
    private static double[] nice(final double[] values) {
        for (int i=0; i<values.length; i++) {
            values[i] = nice(values[i]);
        }
        return values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() throws CoverageStoreException {
        entry = null;
        super.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() throws CoverageStoreException {
        entry = null;
        super.dispose();
    }

    /**
     * The pool of {@link GridCoverageLoader}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.10
     *
     * @since 3.10
     * @module
     */
    static final class Pool extends GridCoverageStorePool {
        /**
         * A description of the image format.
         */
        private final FormatEntry format;

        /**
         * Creates a new {@code Pool} instance. The maximal number of readers is intentionally
         * small, given that we are going to create one pool for each format.
         */
        Pool(final FormatEntry format) {
            super(4);
            this.format = format;
        }

        /**
         * Creates a new {@link GridCoverageLoader}.
         */
        @Override
        protected GridCoverageReader createReader() throws CoverageStoreException {
            return new GridCoverageLoader(format);
        }
    }
}
