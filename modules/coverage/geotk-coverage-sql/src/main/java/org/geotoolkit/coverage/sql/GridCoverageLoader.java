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
import java.util.Collections;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;

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
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.XArrays;


/**
 * An implementation of {@link ImageCoverageReader} when the {@link GridGeometry2D} and the
 * {@link GridSampleDimension}s are obtained from the database instead than from the file.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
final class GridCoverageLoader extends ImageCoverageReader {
    /**
     * A description of the image format.
     */
    private final FormatEntry format;

    /**
     * The entry for the grid coverage to be read.
     */
    private GridCoverageEntry entry;

    /**
     * The expected image size. It must be defined by the caller before to read an image.
     */
    Dimension expectedSize;

    /**
     * If {@code true}, the input is not allowed to be changed.
     */
    boolean inputIsFinal;

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
     * Sets the input, which can be an {@link GridCoverageEntry} or a file.
     */
    @Override
    public void setInput(Object input) throws CoverageStoreException {
        if (inputIsFinal) {
            throw new CoverageStoreException(Errors.getResources(getLocale())
                    .getString(Errors.Keys.UNSUPPORTED_OPERATION_$1, "setInput"));
        }
        entry = null;
        if (input instanceof GridCoverageEntry) try {
            entry = (GridCoverageEntry) input;
            assert format.equals(entry.getIdentifier().series.format) : entry;
            input = entry.getInput();
        } catch (URISyntaxException e) {
            throw new CoverageStoreException(e);
        }
        super.setInput(input);
    }

    /**
     * Returns {@code true} if the given provider is suitable for the image format
     * expected by the current entry. This implementation returns {@code true} in
     * all cases, since we are supposed to recycle the same reader.
     */
    @Override
    protected boolean canReuseImageReader(final ImageReaderSpi provider, final Object input) throws IOException {
        assert XArrays.containsIgnoreCase(provider.getFormatNames(), format.imageFormat);
        return true;
    }

    /**
     * Creates an {@link ImageReader} that claim to be able to decode the given input.
     * This method is invoked automatically by {@link #setInput(Object)} for creating
     * a new {@linkplain #imageReader image reader}.
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
        if (entry != null) {
            return Collections.singletonList(entry.getName());
        }
        return super.getCoverageNames();
    }

    /**
     * Returns the grid geometry which is declared in the database.
     */
    @Override
    public GridGeometry2D getGridGeometry(int index) throws CoverageStoreException {
        if (index == 0 && entry != null) {
            return entry.getGridGeometry();
        }
        // Should not happen. But if it happen anyway, the
        // super-class work should be a raisonable fallback.
        return super.getGridGeometry(index);
    }

    /**
     * Returns the sample dimensions for each band of the {@code GridCoverage} to be read.
     * This method returns the sample dimensions declared in the database rather then inferring
     * them from the image metadata.
     */
    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException {
        if (index == 0) {
            return format.sampleDimensions;
        }
        // Should not happen. But if it happen anyway, the
        // super-class work should be a raisonable fallback.
        return super.getSampleDimensions(index);
    }

    /**
     * Reads the grid coverage. This method checks that the size of the image is the same as
     * the size declared in the database. This check is only used to catch possible errors that
     * would otherwise slip into the database or during the copy of the image to the disk.
     */
    @Override
    public GridCoverage2D read(final int index, final GridCoverageReadParam param)
            throws CoverageStoreException
    {
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
        return IOUtilities.name(getInput());
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
