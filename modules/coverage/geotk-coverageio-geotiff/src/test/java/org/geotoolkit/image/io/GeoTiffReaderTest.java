/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
 *
 */
package org.geotoolkit.image.io;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.test.TestData;
import org.geotoolkit.util.logging.Logging;

import org.junit.Test;

import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 * Testing {@link GeoTiffReader} as well as {@link IIOMetadataDumper}.
 * 
 * @author Simone Giannecchini
 */
public class GeoTiffReaderTest extends Assert {

    private final static Logger LOGGER = Logging.getLogger(GeoTiffReaderTest.class);

    /**
     * Test for reading geotiff files
     * 
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws NoSuchAuthorityCodeException
     */
    @Test
    public void testReader() throws IllegalArgumentException, IOException,
            NoSuchAuthorityCodeException {
    
        final File file = TestData.file(GeoTiffReaderTest.class, "");
        final File files[] = file.listFiles();
        final int numFiles = files.length;

        for (int i = 0; i < numFiles; i++) {
            final String path = files[i].getAbsolutePath().toLowerCase();
            if (!path.endsWith("tif") && !path.endsWith("tiff") || path.contains("no_crs")) {
                continue;
            }

            final Object o;
            if (i % 2 == 0){
                // testing file
                o = files[i];
            }else{
                // testing url
                o = files[i].toURI().toURL();
            }

            // getting a reader

//            GeoTiffReader reader = new GeoTiffReader(o, new Hints(
//                    Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
//
//            if (reader != null) {
//                // reading the coverage
//                GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
//
//                // showing it
//                coverage.show();
//            }
        }
    }

}
