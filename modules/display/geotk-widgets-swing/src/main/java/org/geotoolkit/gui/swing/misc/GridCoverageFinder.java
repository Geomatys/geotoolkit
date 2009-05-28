/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.misc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotools.data.DataSourceException;
import org.geotools.factory.Hints;
//import org.geotools.gce.geotiff.GeoTiffReader;

import org.opengis.coverage.grid.GridCoverage;

/**
 * Static class to build GridCoverage
 * 
 * @author Johann Sorel
 */
public class GridCoverageFinder {

    
    private static final String GEOTIFF = ".tif";
    private static final String BMP = ".bmp";
    private static final String JPG = ".jpg";
    private static final String JPEG = ".jpeg";
    private static final String PNG = ".png";

    /**
     * return a gridcoverage for Raster file. Use a Map containing key "url"
     * @param params 
     * @return GridCoverage
     */
    public static GridCoverage getGridCoverage(Map params){

        GridCoverage cover = null;

        URL url = (URL) params.get("url");

        if (url != null) {
            String name = url.getFile().toLowerCase();
            File file = null;

            try {
                file = new File(url.toURI());
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }

            if (file != null) {
                // try a geotiff gridcoverage
//                if (name.endsWith(GEOTIFF)) {
//
//                    try {
//                        GeoTiffReader reader = new GeoTiffReader(file, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
//                        cover = (GridCoverage2D) reader.read(null);
//                    } catch (DataSourceException ex) {
//                        cover = null;
//                        ex.printStackTrace();
//                    }catch (IOException ex){
//                        cover = null;
//                        ex.printStackTrace();
//                    }
//                } 
                // try a world image file
                if (name.endsWith(BMP) || name.endsWith(JPG) || name.endsWith(JPEG) || name.endsWith(PNG)) {

                    //TODO fix this
//                    try {
//                        WorldImageFactory factory = new WorldImageFactory();
////                        cover = factory.createSimpleReader(file);
//                    } catch (DataSourceException ex) {
//                        cover = null;
//                        ex.printStackTrace();
//                    }catch (IOException ex){
//                        cover = null;
//                        ex.printStackTrace();
//                    }
                }
            }

        }

        return cover;
    }
    
    
}
