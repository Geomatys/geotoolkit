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

import org.opengis.util.LocalName;
import org.opengis.util.InternationalString;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.metadata.extent.GeographicBoundingBox;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
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
import org.geotoolkit.image.io.MultidimensionalImageStore;
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
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.ArraysExt;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;
import static org.geotoolkit.internal.image.io.DimensionAccessor.fixRoundingError;


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
 * @version 3.20
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
     * For internal usage by {@link GridCoverageEntry} only.
     */
    transient GridCoverageLoader nextInUse;

    /**
     * The coverage names to be returned by {@link #getCoverageNames()}, created when first needed.
     * This field <strong>must</strong> be cleared by {@link #clearCache()} when a new input is set.
     *
     * @since 3.20
     */
    private transient List<LocalName> coverageNames;

    /**
     * Metadata created when first needed. Those fields <strong>must</strong> be cleared
     * by {@link #clearCache()} when a new input is set.
     *
     * @since 3.15
     */
    private transient SpatialMetadata streamMetadata, imageMetadata;

    /**
     * {@code true} if the check for image index shall be temporarily disabled. This happen after
     * the {@link #read(int, GridCoverageReadParam) method replaced the user-supplied image index
     * (always 0, which is checked by {@link #ensureValidIndex(int}) by the actual image index as
     * specified in the database. This field is resets to {@code false} as soon as the reading
     * process is finished.
     */
    private transient boolean disableIndexCheck;

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
        if (index != 0 && !disableIndexCheck) {
            throw new IllegalArgumentException(Errors.getResources(getLocale())
                    .getString(Errors.Keys.ILLEGAL_ARGUMENT_2, "imageIndex", index));
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
     * given at construction time.
     * <p>
     * If the image reader is an instance of {@link NamedImageStore}, then this method sets
     * the name of NetCDF (or similar format) variable to read as the names declared in the
     * {@code SampleDimensions} table.
     * <p>
     * If the image reader is an instance of {@link MultidimensionalImageStore} (typically the
     * NetCDF reader), then this method sets the image index API to the temporal dimension. This
     * is consistent with the database schema, where the image index is specified together with
     * the date range in the {@code GridCoverages} table.
     *
     * @param The entry to use as the input.
     * @param imageIndex the image index to use
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
        clearCache();
        super.setInput(input);
        entry = e; // Set the field only on success.
        /*
         * For the NetCDF format, find the names of the variables to read. They are the names
         * declared in the SampleDimensions table. Each variable will be assigned to one band.
         * There is typically only one variable to read.
         */
        if (input != null) {
            if (imageReader instanceof NamedImageStore) {
                final List<GridSampleDimension> bands = format.sampleDimensions;
                if (bands != null) {
                    final String[] bandNames = new String[bands.size()];
                    for (int i=0; i<bandNames.length; i++) {
                        bandNames[i] = bands.get(i).getDescription().toString();
                    }
                    final NamedImageStore named = (NamedImageStore) imageReader;
                    try {
                        named.setBandNames(0, bandNames);
                    } catch (IOException ex) {
                        throw new CoverageStoreException(ex);
                    }
                }
            }
            if (imageReader instanceof MultidimensionalImageStore) {
                ((MultidimensionalImageStore) imageReader).getDimensionForAPI(DimensionSlice.API.IMAGES)
                        .addDimensionId(AxisDirection.FUTURE, AxisDirection.PAST);
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
                ArraysExt.containsIgnoreCase(provider.getFormatNames(), format.imageFormat) ||
                ArraysExt.containsIgnoreCase(provider.getMIMETypes(),   format.imageFormat) : format;
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
    public List<LocalName> getCoverageNames() throws CoverageStoreException {
        if (coverageNames == null) {
            coverageNames = Collections.singletonList(nameFactory.createLocalName(null, ensureInputSet().getName()));
        }
        return coverageNames;
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
     * Creates stream metadata for the given geographic bounding box.
     */
    static SpatialMetadata createStreamMetadata(final GeographicBoundingBox bbox) {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME));
        if (bbox != null) {
            final DiscoveryAccessor accessor = new DiscoveryAccessor(metadata) {
                @Override protected double nice(final double value) {
                    return fixRoundingError(value);
                }
            };
            accessor.setGeographicElement(bbox);
        }
        return metadata;
    }

    /**
     * Returns the metadata associated with the stream as a whole. This method fetches
     * the metadata from the database only; it does not attempt to read the image file.
     */
    @Override
    public SpatialMetadata getStreamMetadata() throws CoverageStoreException {
        SpatialMetadata metadata = streamMetadata;
        if (metadata == null) {
            streamMetadata = metadata = createStreamMetadata(entry.getGeographicBoundingBox());
        }
        return metadata;
    }

    /**
     * Creates image metadata for the given sample dimensions and grid geometry.
     *
     * @param locale   The locale to use with {@link InternationalString} attributes.
     * @param bands    The sample dimension, or {@code null} if none.
     * @param geometry The grid geometry, or {@code null} if none.
     */
    static SpatialMetadata createImageMetadata(final Locale locale,
            final List<GridSampleDimension> bands, final GeneralGridGeometry geometry)
    {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        if (bands != null) {
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
                accessor.setValueRange(fixRoundingError(band.getMinimumValue()),
                                       fixRoundingError(band.getMaximumValue()));
                accessor.setUnits(band.getUnits());
                /*
                 * Add the range of sample values to the metadata. Those values should
                 * be integers, because the type of the "lower" and "upper" columns in
                 * the database are integers.
                 */
                TransferFunction tf = null;
                band = band.geophysics(false);
                NumberRange<?> range = null;
                int[] fillValues = new int[8];
                int fillValuesCount = 0;
                for (final Category category : band.getCategories()) {
                    final NumberRange<?> r = category.getRange();
                    if (category.isQuantitative()) {
                        range = (range == null) ? r : range.unionAny(r);
                        tf = new TransferFunction(category, locale);
                    } else {
                        final int lower = (int) Math.round(r.getMinDouble(true));
                        final int upper = (int) Math.round(r.getMaxDouble(true));
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
                    accessor.setFillSampleValues(ArraysExt.resize(fillValues, fillValuesCount));
                }
                /*
                 * Add the transfer function.
                 */
                if (tf != null) {
                    accessor.setTransfertFunction(fixRoundingError(tf.scale),
                                                  fixRoundingError(tf.offset), tf.type);
                }
            }
        }
        /*
         * Add the "SpatialRepresentation" and "RectifiedGridDomain" nodes.
         */
        if (geometry != null) {
            final GridDomainAccessor accessor = new GridDomainAccessor(metadata);
            accessor.setGridGeometry(geometry, PixelInCell.CELL_CORNER, null);
        }
        return metadata;
    }

    /**
     * Returns the metadata associated with the given coverage. This method fetches the
     * metadata from the database only; it does not attempt to read the image file.
     */
    @Override
    public SpatialMetadata getCoverageMetadata(final int index) throws CoverageStoreException {
        ensureValidIndex(index);
        SpatialMetadata metadata = imageMetadata;
        if (metadata == null) {
            imageMetadata = metadata = createImageMetadata(getLocale(),
                    format.sampleDimensions, getGridGeometry(index));
        }
        return metadata;
    }

    /**
     * Returns read parameters with the z-slice initialized, if needed.
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
        final GridCoverageIdentifier identifier = ensureInputSet().getIdentifier();
        index = identifier.getImageIndex();
        final ImageReader imageReader = this.imageReader; // Protect from changes.
        try {
            final Dimension expectedSize = identifier.geometry.getImageSize();
            final int expectedWidth  = expectedSize.width;
            final int expectedHeight = expectedSize.height;
            final int imageWidth     = imageReader.getWidth (index);
            final int imageHeight    = imageReader.getHeight(index);
            if (expectedWidth != imageWidth || expectedHeight != imageHeight) {
                throw new CoverageStoreException(Errors.getResources(getLocale()).getString(Errors.Keys.MISMATCHED_IMAGE_SIZE_5,
                        org.apache.sis.internal.storage.IOUtilities.filename(getInputName()), imageWidth, imageHeight, expectedWidth, expectedHeight));
            }
        } catch (IOException e) {
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        GridCoverage2D coverage;
        disableIndexCheck = true;
        try {
            coverage = super.read(index, param);
        } finally {
            disableIndexCheck = false;
        }
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
            return org.apache.sis.internal.storage.IOUtilities.filename(input);
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
        String message = Errors.getResources(getLocale()).getString(Errors.Keys.CANT_READ_FILE_1, getInputName());
        if (cause != null && cause.indexOf(' ') > 0) { // Append only if we have a sentence.
            message = message + '\n' + cause;
        }
        return message;
    }

    /**
     * Clears the cached object. This method needs to be invoked when the input changed,
     * in order to force the calculation of new objects for the new input.
     */
    private void clearCache() {
        entry          = null;
        coverageNames  = null;
        streamMetadata = null;
        imageMetadata  = null;
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
