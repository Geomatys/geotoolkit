/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.metadata.IIOMetadata;

import org.geotoolkit.image.io.metadata.Band;
import org.geotoolkit.image.io.metadata.GeographicMetadata;


/**
 * A null implementation of {@link GeographicImageReader} for testing purpose.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 */
public class NullImageReader extends GeographicImageReader {
    /**
     * The data type to be returned by {@link #getRawDataType}.
     */
    private final int dataType;

    /**
     * The metadata to be returned by {@link #getImageMetadata}.
     */
    private final double minimum, maximum, padValue;

    /**
     * Creates a reader with a dummy provider.
     *
     * @param dataType The data type as one of {@link java.awt.image.DataBuffer} constants.
     * @param minimum  The minimum sample value.
     * @param maximum  The maximum sample value.
     * @param padValue The value for missing data.
     */
    public NullImageReader(final int dataType, final double minimum, final double maximum, final double padValue) {
        super(new Spi());
        this.dataType = dataType;
        this.minimum  = minimum;
        this.maximum  = maximum;
        this.padValue = padValue;
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
    public IIOMetadata getImageMetadata(final int imageIndex) throws IOException {
        final GeographicMetadata metadata = new GeographicMetadata(this);
        final Band band = metadata.addBand("Dummy");
        band.setValidRange(minimum, maximum);
        band.setNoDataValues(new double[] {padValue});
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
        super.getRawDataType(imageIndex);
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
    private static final class Spi extends ImageReaderSpi {
        public Spi() {
            inputTypes = new Class[] {String.class};
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
