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
package org.geotoolkit.osmtms.model;

import java.awt.geom.Point2D;

import org.geotoolkit.client.map.DefaultPyramid;
import org.geotoolkit.client.map.DefaultPyramidSet;
import org.geotoolkit.osmtms.map.OSMTMSUtilities;
import org.geotoolkit.referencing.CRS;

import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMTMSPyramidSet extends DefaultPyramidSet{

    public OSMTMSPyramidSet(final int maxScale) {
        
        final DefaultPyramid pyramid = new DefaultPyramid(this,OSMTMSUtilities.GOOGLE_MERCATOR);
        
        final int tileWidth = (int) OSMTMSUtilities.BASE_TILE_SIZE;        
        final int tileHeight = (int) OSMTMSUtilities.BASE_TILE_SIZE;        
        final Envelope extent = CRS.getEnvelope(OSMTMSUtilities.GOOGLE_MERCATOR);
                
        final Point2D upperLeft = new Point2D.Double(
                OSMTMSUtilities.UPPER_LEFT_CORNER.getOrdinate(0), 
                OSMTMSUtilities.UPPER_LEFT_CORNER.getOrdinate(1));
        
        final double scale0Resolution = extent.getSpan(0) / OSMTMSUtilities.BASE_TILE_SIZE;
        
        for(int i=0; i<=maxScale; i++){
            
            final int size = (int) Math.pow(2, i);
            final double scale = scale0Resolution / size;
            
            final OSMTMSMosaic mosaic = new OSMTMSMosaic(
                    pyramid, upperLeft, 
                    size, size, 
                    tileHeight, tileWidth, 
                    tileWidth*scale, tileHeight*scale,
                    i);
            
            pyramid.getMosaics().put(scale, mosaic);
        }
        
        getPyramids().add(pyramid);        
    }
    
}
