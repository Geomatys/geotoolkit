/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.image.io;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderWriterSpi;
import java.awt.image.SampleModel;

import org.geotoolkit.lang.Static;


/**
 * A set of static utilities methods related to Image I/O.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16 (derived from 3.00)
 * @module
 *
 * @deprecated Will be removed after we redesigned MosaicImageWriter.
 */
@Deprecated
public final class IIOUtilities extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private IIOUtilities() {
    }

    /**
     * Guesses the compression ratio for the given image format. This is very approximative
     * and used only in order to have some order of magnitude.
     *
     * @param  spi The image reader or writer provider.
     * @return The inverse of a guess of compression ratio (1 is uncompressed), or 0 if unknown.
     */
    public static int guessCompressionRatio(final ImageReaderWriterSpi spi) {
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
     * Guesses the number of bits per pixel for an image to be saved on the disk in the RAW format.
     * Current implementation ignores the leading or trailing bits which could exists on each lines.
     * If the information is unknown (which may happen with some JPEG readers), conservatively
     * returns the size required for storing ARGB values using bytes.
     *
     * @param  type The color model and sample model of the image to be saved.
     * @return Expected number of bits per pixel.
     */
    public static int bitsPerPixel(final ImageTypeSpecifier type) {
        if (type != null) {
            final SampleModel sm = type.getSampleModel();
            if (sm != null) {
                int size = 0;
                for (final int s : sm.getSampleSize()) {
                    size += s;
                }
                return size;
            }
        }
        return 4 * Byte.SIZE;
    }
}
