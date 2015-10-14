/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.SetupService;
import ucar.nc2.dataset.NetcdfDataset;


/**
 * Performs initialization and shutdown of the {@code geotk-coverageio-netcdf} module.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @see org.geotoolkit.lang.Setup
 *
 * @since 3.10
 * @module
 */
public final class SetupNetcdf implements SetupService {
    /**
     * Initializes the NetCDF cache, if it was not already initialized.
     */
    @Override
    public void initialize(final Properties properties, final boolean reinit) {
        if (properties != null) {
            final int n;
            try {
                n = Integer.parseInt(properties.getProperty("netcdfCacheLimit", "0"));
            } catch (NumberFormatException e) {
                Logging.unexpectedException(null, org.geotoolkit.lang.Setup.class, "initialize", e);
                return;
            }
            if (n != 0) {
                if (NetcdfDataset.getNetcdfFileCache() == null) {
                    NetcdfDataset.initNetcdfFileCache(0, 10, 5*60);
                }
            }
        }
    }

    /**
     * Shutdowns the NetCDF cache.
     */
    @Override
    public void shutdown() {
        NetcdfDataset.shutdown();
    }
}
