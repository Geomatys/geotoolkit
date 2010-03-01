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

import java.util.Set;
import java.util.List;
import java.util.TreeSet;
import java.util.Iterator;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.resources.Errors;


/**
 * An implementation of {@link ImageCoverageReader} when the {@link GridGeometry2D} and the
 * {@link GridSampleDimension}s are obtained from the database instead than from the file.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class ImageCoverageReaderSQL extends ImageCoverageReader {
    /**
     * A description of the image format.
     */
    private final FormatEntry format;

    /**
     * Creates a new reader for the given format.
     */
    ImageCoverageReaderSQL(final FormatEntry format) {
        this.format     = format;
        seekForwardOnly = Boolean.TRUE;
        ignoreMetadata  = Boolean.TRUE;
    }

    /**
     * Creates an {@link ImageReader} that claim to be able to decode the given input.
     * This method is invoked automatically by {@link #setInput(Object)} for creating
     * a new {@linkplain #imageReader image reader}. The image reader input must be set.
     */
    @Override
    protected ImageReader createImageReader(final Object input) throws IOException {
        final String imageFormat = format.imageFormat;
        final boolean isMIME = imageFormat.indexOf('/') >= 0;
        try {
            if (isMIME) {
                return XImageIO.getReaderByMIMEType(imageFormat, input, seekForwardOnly, ignoreMetadata);
            } else {
                return XImageIO.getReaderByFormatName(imageFormat, input, seekForwardOnly, ignoreMetadata);
            }
        } catch (UnsupportedImageFormatException e) {
            /*
             * No decoder found. Gets the list of decoders. This list will be
             * inserted in the error message as an attempt to help debugging.
             * We will take only the first format name of each SPI since the
             * other are only synonymous and we want to keep the message short.
             */
            final Set<String> formats = new TreeSet<String>();
            final Iterator<ImageReaderSpi> spi = IIORegistry.getDefaultInstance()
                    .getServiceProviders(ImageReaderSpi.class, false);
            while (spi.hasNext()) {
                final ImageReaderSpi p = spi.next();
                final String[] f = isMIME ? p.getMIMETypes() : p.getFormatNames();
                if (f != null && f.length != 0 && f[0].length() != 0) {
                    formats.add(f[0]);
                }
            }
            final StringBuilder buffer = new StringBuilder();
            for (final String format : formats) {
                if (buffer.length() != 0) {
                    buffer.append(", ");
                }
                buffer.append(format);
            }
            throw new UnsupportedImageFormatException(Errors.format(
                    Errors.Keys.NO_IMAGE_FORMAT_$2, imageFormat, buffer), e);
        }
    }

    /**
     * Returns the sample dimensions for each band of the {@code GridCoverage} to be read.
     * This method returns the sample dimensions declared in the database rather then inferring
     * them from the image metadata.
     */
    @Override
    public List<GridSampleDimension> getSampleDimensions(final int index) throws CatalogException {
        try {
            return format.getSampleDimensions();
        } catch (SQLException e) {
            throw new CatalogException(e);
        }
    }
}
