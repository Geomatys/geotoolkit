/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.data.shapefile;

import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FileDataStoreFactory;
import org.geotoolkit.data.folder.AbstractFolderDataStoreFactory;

/**
 * DataStore for a folder of Shapefiles.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ShapefileFolderDataStoreFactory extends AbstractFolderDataStoreFactory{
    
    @Override
    public FileDataStoreFactory getSingleFileFactory() {
        return DataStoreFinder.getAllDataStores(ShapefileDataStoreFactory.class).next();
    }

    @Override
    public String getDescription() {
        return "Folder of Shapefiles";
    }
    
}
