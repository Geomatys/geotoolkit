/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import org.geotoolkit.internal.SetupService;
import ucar.nc2.dataset.NetcdfDataset;


/**
 * Performs initialization and shutdown of the {@code geotk-coverageio-netcdf} module.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @see org.geotoolkit.lang.Setup
 *
 * @since 3.10
 * @module
 */
public final class SetupNetcdf implements SetupService {
    /**
     * Initializes the Netcdf cache, if it was not already initialized.
     */
    @Override
    public void initialize(boolean reinit) {
        if (NetcdfDataset.getNetcdfFileCache() == null) {
            NetcdfDataset.initNetcdfFileCache(0, 10, 5*60);
        }
    }

    /**
     * Shutdowns the Netcdf cache.
     */
    @Override
    public void shutdown() {
        NetcdfDataset.shutdown();
    }
}
