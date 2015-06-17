/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;

import org.geotoolkit.resources.Vocabulary;


/**
 * An image writer which doesn't write anything.
 * This is sometime useful for testing purpose.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 */
public strictfp class NullImageWriter extends SpatialImageWriter {
    /**
     * Constructs a {@code NullImageWriter}.
     *
     * @param provider The {@code ImageWriterSpi} that
     *        is constructing this object, or {@code null}.
     */
    protected NullImageWriter(final Spi provider) {
        super(provider);
    }

    /**
     * Silently ignore the given parameters.
     *
     * @param metadata Ignored.
     * @throws IOException Never thrown.
     */
    @Override
    public void write(IIOMetadata metadata, IIOImage image, ImageWriteParam param) throws IOException {
        LOGGER.info(Vocabulary.format(Vocabulary.Keys.Saving_1, output));
    }




    /**
     * Service provider interface (SPI) for {@link NullImageWriter}s.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 2.5
     */
    public static strictfp class Spi extends SpatialImageWriter.Spi {
        /**
         * Constructs a default {@code NullImageWriter.Spi}. This constructor
         * provides the following defaults:
         *
         * <ul>
         *   <li>{@link #names}           = {@code "null"}</li>
         *   <li>{@link #pluginClassName} = {@code "org.geotoolkit.image.io.NullImageWriter"}</li>
         *   <li>{@link #vendorName}      = {@code "Geotoolkit.org"}</li>
         * </ul>
         */
        public Spi() {
            names           = new String[] {"null"};
            pluginClassName = "org.geotoolkit.image.io.NullImageWriter";
        }

        /**
         * Returns a description of the image writer.
         */
        @Override
        public String getDescription(Locale locale) {
            return "Null";
        }

        /**
         * Returns {@code true} in all cases.
         */
        @Override
        public boolean canEncodeImage(ImageTypeSpecifier type) {
            return true;
        }

        /**
         * Returns a new {@link NullImageWriter} instance.
         *
         * @throws IOException If an I/O operation was required and failed.
         */
        @Override
        public ImageWriter createWriterInstance(Object extension) throws IOException {
            return new NullImageWriter(this);
        }
    }
}
