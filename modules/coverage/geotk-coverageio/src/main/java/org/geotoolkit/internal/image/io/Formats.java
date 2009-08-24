/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.image.io;

import java.util.Iterator;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.resources.Errors;


/**
 * Utility methods about image formats.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.01
 * @module
 */
@Static
public final class Formats {
    /**
     * Do not allow instantiation of this class.
     */
    private Formats() {
    }

    /**
     * Returns the image reader provider for the given format name.
     *
     * @param  format The format name to search, or {@code null}.
     * @return The reader provider for the given format, or {@code null} if {@code format} is null.
     * @throws IllegalArgumentException If no provider is found for the given format.
     */
    public static ImageReaderSpi getReaderByFormatName(String format) throws IllegalArgumentException {
        ImageReaderSpi spi = null;
        if (format != null) {
            format = format.trim();
            final IIORegistry registry = IIORegistry.getDefaultInstance();
            final Iterator<ImageReaderSpi> it=registry.getServiceProviders(ImageReaderSpi.class, true);
            do {
                if (!it.hasNext()) {
                    throw new IllegalArgumentException(Errors.format(
                            Errors.Keys.UNKNOW_IMAGE_FORMAT_$1, format));
                }
                spi = it.next();
            } while (!XArrays.contains(spi.getFormatNames(), format));
        }
        return spi;
    }
}
