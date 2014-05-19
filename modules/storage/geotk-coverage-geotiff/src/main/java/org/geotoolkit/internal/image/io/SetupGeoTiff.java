/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
import java.util.Properties;
//import org.geotoolkit.image.io.plugin.GeoTiffImageReader;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.image.io.plugin.GeoTiffImageWriter;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.image.io.plugin.TiffImageWriter;
import org.geotoolkit.internal.SetupService;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;


/**
 * Performs initialization and shutdown of the {@code geotk-coverageio-geotiff} module.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class SetupGeoTiff implements SetupService {
    /**
     * Give a higher priority for Geotk  reader and writer in default registry.
     */
    @Override
    public void initialize(final Properties properties, final boolean reinit) {
        initialize();
    }

    public static void initialize() {
        IIORegistry registry = IIORegistry.getDefaultInstance();

        final TiffImageReader.Spi tiffReaderSpi = registry.getServiceProviderByClass(TiffImageReader.Spi.class);
        final Iterator<ImageReaderSpi> it = registry.getServiceProviders(ImageReaderSpi.class, false);
        while (it.hasNext()) {
            final ImageReaderSpi current = it.next();
            if (current != tiffReaderSpi && ArraysExt.containsIgnoreCase(current.getFormatNames(), "tiff")) {
                registry.setOrdering(ImageReaderSpi.class, tiffReaderSpi, current);
            }
        }

        final TiffImageWriter.Spi tiffWriterSpi = registry.getServiceProviderByClass(TiffImageWriter.Spi.class);
        final Iterator<ImageWriterSpi> it2 = registry.getServiceProviders(ImageWriterSpi.class, false);
        while (it2.hasNext()) {
            final ImageWriterSpi current = it2.next();
            if (current != tiffWriterSpi && ArraysExt.containsIgnoreCase(current.getFormatNames(), "tiff")) {
                registry.setOrdering(ImageWriterSpi.class, tiffWriterSpi, current);
            }
        }
    }

    /**
     * Register geotiff reader and writer in default registry.
     */
    @Override
    public void shutdown() {
    }
}
