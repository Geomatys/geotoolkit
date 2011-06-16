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
package org.geotoolkit.tms.map;

import java.awt.Dimension;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class OSMTMSUtilities {
    
    public static final double BASE_TILE_SIZE = 256d;
    
//    public static final CoordinateReferenceSystem GOOGLE_MERCATOR;
//    public static final Envelope MERCATOR_EXTEND;
//    
//    //scale tolerance of 10%
//    private static final double tolerance = 0.1;
//    
//    /**
//     * Resolution at zoom 0 level. in meter by pixel.
//     */
//    public static final double ZOOM_ZERO_RESOLUTION;
//    
//    static {
//        try {
//            GOOGLE_MERCATOR = CRS.decode("EPSG:3857");
//            MERCATOR_EXTEND = CRS.getEnvelope(GOOGLE_MERCATOR);
//        } catch (NoSuchAuthorityCodeException ex) {
//            throw new RuntimeException(ex);
//        } catch (FactoryException ex) {
//            throw new RuntimeException(ex);
//        }
//                
//        ZOOM_ZERO_RESOLUTION = MERCATOR_EXTEND.getSpan(0) / BASE_TILE_SIZE;
//    }
//    
    private OSMTMSUtilities(){}
//    
//    /**
//     * Calculate the most accurate resolution for the given envelope.
//     * 
//     * @param env : in google mercator projection
//     * @param dim : the image size
//     * @return int : closest resolution
//     */
//    public static int getBestZoomLevel(final Envelope env, final Dimension dim){
//                
//        //the wanted image resolution
//        final double wantedResolution = env.getSpan(0) / dim.getWidth() ;
//        
//        //we return the closes resolution above        
//        final double result = Math.log(wantedResolution/ZOOM_ZERO_RESOLUTION) / Math.log(0.5d);
//        
//        int zoom = Math.round( (float)result );
//        
//        return (zoom < 0) ? 0 : zoom;
//    }
    
    
}
