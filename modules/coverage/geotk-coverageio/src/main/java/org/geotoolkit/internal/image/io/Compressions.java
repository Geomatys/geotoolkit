/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.image.io;

import java.awt.image.SampleModel;
import javax.imageio.spi.ImageReaderWriterSpi;

import org.geotoolkit.lang.Static;


/**
 * Utilities methods related to umage compression.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@Static
public final class Compressions {
    /**
     * Do not allow instantiation of this class.
     */
    private Compressions() {
    }

    /**
     * Guess the compression ratio for the given image format. This is very approximative
     * and user only in order to have some order of magnitude.
     *
     * @param  spi The image reader or writer provider.
     * @return The inverse of a guess of compression ratio (1 is uncompressed), or 0 if unknown.
     */
    public static int guessForFormat(final ImageReaderWriterSpi spi) {
        if (spi != null) {
            for (final String format : spi.getFormatNames()) {
                if (format.equalsIgnoreCase("png")) {
                    return 4;
                }
                if (format.equalsIgnoreCase("jpeg")) {
                    return 8;
                }
                if (format.equalsIgnoreCase("tiff")) {
                    // TIFF are uncompressed unless explicitly specified.
                    return 1;
                }
                if (format.equalsIgnoreCase("bmp") || format.equalsIgnoreCase("raw")) {
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * Guess the number of bits per pixel for an image to be saved on the disk in the RAW format.
     * Current implementation ignores the leading or trailing bits which could exists on each
     * lines.
     *
     * @param  sm The sample model of the image to be saved.
     * @return Expected number of bits per pixel.
     */
    public static int bitsPerPixel(final SampleModel sm) {
        int size = 0;
        for (final int s : sm.getSampleSize()) {
            size += s;
        }
        return size;
    }
}
