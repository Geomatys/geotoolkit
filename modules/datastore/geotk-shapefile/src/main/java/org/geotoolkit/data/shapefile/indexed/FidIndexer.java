/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile.indexed;


import java.io.IOException;
import java.net.URL;

import org.geotoolkit.data.shapefile.ShpFiles;
import org.geotoolkit.data.shapefile.StorageFile;
import org.geotoolkit.data.shapefile.shp.IndexFile;

import static org.geotoolkit.data.shapefile.ShpFileType.*;
import static org.geotoolkit.data.shapefile.ShapefileDataStoreFactory.*;

/**
 * Creates a .fix file (fid index).
 * 
 * @author Jesse
 * @module pending
 */
public class FidIndexer {

    /**
     * Generates the FID index file for the shpFile
     */
    public static synchronized void generate(URL shpURL) throws IOException {
        generate(new ShpFiles(shpURL));
    }

    /**
     * Generates the FID index file for the shpFiles
     */
    public static void generate(ShpFiles shpFiles) throws IOException {
        LOGGER.fine("Generating fids for " + shpFiles.get(SHP));

        IndexFile indexFile = null;
        StorageFile file = shpFiles.getStorageFile(FIX);
        IndexedFidWriter writer = null;
        
        try {
            indexFile = new IndexFile(shpFiles, false);

            // writer closes channel for you.
            writer = new IndexedFidWriter(shpFiles, file);

            for (int i = 0, j = indexFile.getRecordCount(); i < j; i++) {
                writer.next();
            }

        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                file.replaceOriginal();
            } finally {
                if (indexFile != null) {
                    indexFile.close();
                }
            }
        }
    }

}
