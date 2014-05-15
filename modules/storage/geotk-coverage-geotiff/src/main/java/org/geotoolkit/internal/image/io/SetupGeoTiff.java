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
import org.geotoolkit.internal.SetupService;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;


/**
 * Performs initialization and shutdown of the {@code geotk-coverageio-geotiff} module.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class SetupGeoTiff implements SetupService {
    /**
     * Register geotiff reader and writer in default registry.
     */
    @Override
    public void initialize(final Properties properties, final boolean reinit) {
        IIORegistry registery = IIORegistry.getDefaultInstance();
        final TiffImageReader.Spi tiffSpi = registery.getServiceProviderByClass(TiffImageReader.Spi.class);
        final Iterator<ImageReaderSpi> it = registery.getServiceProviders(ImageReaderSpi.class, false);
        while (it.hasNext()) {
            final ImageReaderSpi current = it.next();
            if (current != tiffSpi && ArraysExt.containsIgnoreCase(current.getFormatNames(), "tiff")) {
                registery.setOrdering(ImageReaderSpi.class, tiffSpi, current);
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
