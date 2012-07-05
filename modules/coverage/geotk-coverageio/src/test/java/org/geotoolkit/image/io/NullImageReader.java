/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io;

import java.util.Locale;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;

import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.DimensionAccessor;

import static org.junit.Assert.*;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * A null implementation of {@link SpatialImageReader} for testing purpose.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 2.5
 */
public strictfp class NullImageReader extends SpatialImageReader {
    /**
     * The data type to be returned by {@link #getRawDataType}.
     */
    private final int dataType;

    /**
     * The metadata to be returned by {@link #getImageMetadata}.
     */
    private final double minimum, maximum, padValue, scale, offset;

    /**
     * Creates a reader with a dummy provider.
     *
     * @param dataType The data type as one of {@link java.awt.image.DataBuffer} constants.
     * @param minimum  The minimum sample value.
     * @param maximum  The maximum sample value.
     * @param padValue The value for missing data.
     */
    public NullImageReader(final int dataType, final double minimum, final double maximum, final double padValue) {
        this(dataType, minimum, maximum, padValue, 1, 0);
    }

    /**
     * Creates a reader with a dummy provider with the given transfer function.
     *
     * @param dataType The data type as one of {@link java.awt.image.DataBuffer} constants.
     * @param minimum  The minimum sample value.
     * @param maximum  The maximum sample value.
     * @param padValue The value for missing data.
     * @param scale    The scale factory for conversion to geophysics value.
     * @param offset   The offset for conversion to geophysics value.
     */
    public NullImageReader(final int dataType, final double minimum, final double maximum,
            final double padValue, final double scale, final double offset)
    {
        super(new Spi());
        this.dataType = dataType;
        this.minimum  = minimum;
        this.maximum  = maximum;
        this.padValue = padValue;
        this.scale    = scale;
        this.offset   = offset;
        setInput("Dummy");
    }

    /**
     * Returns a dummy width.
     *
     * @param  imageIndex  The image index, numbered from 0.
     * @throws IOException Never thrown in default implementation.
     */
    @Override
    public int getWidth(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return 200;
    }

    /**
     * Returns a dummy height.
     *
     * @param  imageIndex  The image index, numbered from 0.
     * @throws IOException Never thrown in default implementation.
     */
    @Override
    public int getHeight(final int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        return 100;
    }

    /**
     * Returns the metadata specified at construction time.
     *
     * @param  imageIndex  The image index, numbered from 0.
     * @throws IOException Never thrown in default implementation.
     */
    @Override
    public SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME), this, null);
        final DimensionAccessor accessor = new DimensionAccessor(metadata);
        accessor.selectChild(accessor.appendChild());
        accessor.setValueRange(minimum * scale + offset, maximum * scale + offset);
        accessor.setValidSampleValue(minimum, maximum);
        accessor.setFillSampleValues(padValue);
        return metadata;
    }

    /**
     * Returns the data type specified at construction time.
     *
     * @param  imageIndex  The image index, numbered from 0.
     * @return The value given at construction time.
     * @throws IOException Never thrown in default implementation.
     */
    @Override
    protected int getRawDataType(final int imageIndex) throws IOException {
        // super.getRawDataType(int) basically just invoke checkImageIndex(int).
        assertEquals(DataBuffer.TYPE_FLOAT, super.getRawDataType(imageIndex));
        return dataType;
    }

    /**
     * Returns a dummy image.
     *
     * @param  imageIndex  The image index, numbered from 0.
     * @param  param Optional parameters, or {@code null} if none.
     * @return A dummy image.
     * @throws IOException Never thrown in default implementation.
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        checkImageIndex(imageIndex);
        final BufferedImage image = getDestination(imageIndex, param, 200, 100, null);
        return image;
    }

    /**
     * A dummy provider for the dummy reader.
     */
    private static final strictfp class Spi extends SpatialImageReader.Spi {
        public Spi() {
            inputTypes = new Class<?>[] {String.class};
        }

        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean canDecodeInput(final Object source) throws IOException {
            return false;
        }

        @Override
        public String getDescription(final Locale locale) {
            return "Dummy";
        }
    }
}
