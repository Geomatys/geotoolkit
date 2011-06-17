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
package org.geotoolkit.googlemaps.model;

import java.awt.geom.Point2D;
import org.geotoolkit.client.map.DefaultGridMosaic;
import org.geotoolkit.client.map.DefaultPyramid;
import org.geotoolkit.client.map.DefaultPyramidSet;
import org.geotoolkit.client.map.GridMosaic;
import org.geotoolkit.client.map.PyramidSet;
import org.geotoolkit.googlemaps.GetMapRequest;
import org.geotoolkit.googlemaps.map.GoogleMapsUtilities;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GoogleMapsPyramidSet extends DefaultPyramidSet{

    private GoogleMapsPyramidSet(final int maxScale) {
        
        final DefaultPyramid pyramid = new DefaultPyramid(this, GoogleMapsUtilities.GOOGLE_MERCATOR);
        
        final int tileWidth = (int) GoogleMapsUtilities.BASE_TILE_SIZE;        
        final int tileHeight = (int) GoogleMapsUtilities.BASE_TILE_SIZE;        
        final Envelope extent = CRS.getEnvelope(GoogleMapsUtilities.GOOGLE_MERCATOR);
        final Point2D upperLeft = new Point2D.Double(extent.getMinimum(0), extent.getMaximum(1));
        
        final double scale0Resolution = extent.getSpan(0) / GoogleMapsUtilities.BASE_TILE_SIZE;
        
        for(int i=0; i<=maxScale; i++){
            
            final int size = (int) Math.pow(2, i);
            final double scale = scale0Resolution / size;
            
            final GoogleMapsMosaic mosaic = new GoogleMapsMosaic(
                    pyramid, upperLeft, 
                    size, size, 
                    tileHeight, tileWidth, 
                    tileWidth*scale, tileHeight*scale,
                    i);
            
            pyramid.getMosaics().put(scale, mosaic);
        }
        
        getPyramids().add(pyramid);    
    }
    
    public static PyramidSet getPyramidSet(final String name){
        
        PyramidSet set = null;
        
        if (GetMapRequest.TYPE_HYBRID.equalsIgnoreCase(name)) {
            set = new GoogleMapsPyramidSet(18);
        } else if (GetMapRequest.TYPE_ROADMAP.equalsIgnoreCase(name)) {
            set = new GoogleMapsPyramidSet(18);
        } else if (GetMapRequest.TYPE_SATELLITE.equalsIgnoreCase(name)) {
            set = new GoogleMapsPyramidSet(18);
        } else if (GetMapRequest.TYPE_TERRAIN.equalsIgnoreCase(name)) {
            set = new GoogleMapsPyramidSet(18);
        } else {
            throw new IllegalArgumentException("Unknowned google maps layer : " + name);
        }
        
        return set;        
    }
    
    
}
