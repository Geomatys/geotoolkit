/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2012, Geomatys
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
package org.geotoolkit.storage.coverage;

import org.geotoolkit.storage.StorageListener;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CoverageStoreListener extends StorageListener<CoverageStoreManagementEvent, CoverageStoreContentEvent> {
    
    /**
     * Fired when a new pyramid/mosaic has been created, modified or deleted.
     * @param event
     */
    @Override
    void structureChanged(CoverageStoreManagementEvent event);

    /**
     * Fired when some coverage data/tile has been added, modified or deleted.
     * @param event
     */
    @Override
    void contentChanged(CoverageStoreContentEvent event);
    
}
