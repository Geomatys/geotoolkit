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
import org.geotoolkit.image.io.plugin.BILImageReader;
import org.geotoolkit.internal.SetupService;


/**
 * Performs initialization and shutdown of the {@code geotk-coverageio-bil} module.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class SetupBIL implements SetupService {
    
    /**
     * Register bil reader in default registry.
     */
    @Override
    public void initialize(final Properties properties, final boolean reinit) {
        BILImageReader.Spi.registerDefaults(null);
    }

    /**
     * Unregister bil reader in default registry.
     */
    @Override
    public void shutdown() {
        BILImageReader.Spi.unregisterDefaults(null);
    }
}
