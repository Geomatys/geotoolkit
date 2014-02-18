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

import java.util.Properties;
import org.geotoolkit.image.io.plugin.DimapImageReader;
import org.geotoolkit.image.io.plugin.DimapImageWriter;
import org.geotoolkit.internal.SetupService;


/**
 * Performs initialization and shutdown of the {@code geotk-coverageio-dimap} module.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class SetupDimap implements SetupService {
    
    /**
     * Register dimap reader and writer in default registry.
     */
    @Override
    public void initialize(final Properties properties, final boolean reinit) {
        DimapImageReader.Spi.registerDefaults(null);
        DimapImageWriter.Spi.registerDefaults(null);
    }

    /**
     * Unregister dimap reader and writer in default registry.
     */
    @Override
    public void shutdown() {
        DimapImageReader.Spi.unregisterDefaults(null);
        DimapImageWriter.Spi.unregisterDefaults(null);
    }
}
