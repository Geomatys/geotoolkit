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
package org.geotoolkit.googlemaps.map;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class GoogleMapsUtilities {

    private static final double BASE_TILE_SIZE = 256d;
    
    public static final CoordinateReferenceSystem GOOGLE_MERCATOR;
    public static final Envelope MERCATOR_EXTEND;

    /**
     * Resolution at zoom 0 level. in meter by pixel.
     */
    public static final double ZOOM_ZERO_RESOLUTION;
    
    static {
        try {
            GOOGLE_MERCATOR = CRS.decode("EPSG:3857");
        } catch (NoSuchAuthorityCodeException ex) {
            throw new RuntimeException(ex);
        } catch (FactoryException ex) {
            throw new RuntimeException(ex);
        }
        
        
        //this is not an exact extent, google once again do not respect anything
        //MERCATOR_EXTEND = CRS.getEnvelope(GoogleMapsUtilities.GOOGLE_MERCATOR);
        MERCATOR_EXTEND = new GeneralEnvelope(GOOGLE_MERCATOR);
        ((GeneralEnvelope)MERCATOR_EXTEND).setRange(0, -2e7, 2e7);
        ((GeneralEnvelope)MERCATOR_EXTEND).setRange(1, -2e7, 2e7);
        
        ZOOM_ZERO_RESOLUTION = MERCATOR_EXTEND.getSpan(0) / BASE_TILE_SIZE;
    }
    
    private GoogleMapsUtilities() {
    }
    
    /**
     * Calculate the most accurate resolution for the given envelope.
     * 
     * @param env : in google mercator projection
     * @param dim : the image size
     * @return int : closest resolution
     */
    public static int getBestZoomLevel(final Envelope env, final Dimension dim){
                
        //the wanted image resolution
        final double wantedResolution = env.getSpan(0) / dim.getWidth() ;
        
        //we return the closes resolution above        
        final double result = Math.log(wantedResolution/ZOOM_ZERO_RESOLUTION) / Math.log(0.5d);
        
        int zoom = Math.round( (float)result );
        
        return (zoom < 0) ? 0 : zoom;
    }
    
    /**
     * Returns resolution at zoom given level. in meter by pixel.
     */
    public static double getZoomResolution(final int zoom){
        return ZOOM_ZERO_RESOLUTION * Math.pow(0.5d, zoom);
    }
    
    public static Envelope getEnvelope(final DirectPosition center, final Dimension dimension, final int zoom){
        
        final double imageResolution = getZoomResolution(zoom);        
        final double envWidth = dimension.width * imageResolution;
        final double envHeight = dimension.height * imageResolution; 
        
        final GeneralEnvelope env = new GeneralEnvelope(GOOGLE_MERCATOR);
        env.setRange(0, center.getOrdinate(0)-envWidth/2, center.getOrdinate(0)+envWidth/2);
        env.setRange(1, center.getOrdinate(1)-envHeight/2, center.getOrdinate(1)+envHeight/2);
        
        return env;
    }
    
    public static Collection<Point> getTileCoordinates(final int zoom, final Envelope env){
        final Collection<Point> points = new ArrayList<Point>();
        
        final double tileSize = getZoomResolution(zoom) * BASE_TILE_SIZE;
        
        final GeneralEnvelope area = new GeneralEnvelope(env);
        
        //TODO brut method, we can do better then that
        int i=0;
        int j=0;
        loopX :
        for(double x=MERCATOR_EXTEND.getMinimum(0) ; x+tileSize<=MERCATOR_EXTEND.getMaximum(0); x+=tileSize,i++){
            
            if(x+tileSize < env.getMinimum(0)){
                continue loopX;
            }else if(x > env.getMaximum(0)){
                break loopX;
            }
            
            j=0;
            loopY :
            for(double y=MERCATOR_EXTEND.getMaximum(1); y-tileSize>=MERCATOR_EXTEND.getMinimum(1); y-=tileSize,j++){
                
                if(y < env.getMinimum(1)){
                    break loopY;
                }else if(y-tileSize > env.getMaximum(1)){
                    continue loopY;
                }
                
                final Point pt = new Point(i, j);
                final DirectPosition tileCenter = getCenter(zoom, pt);
                final Envelope candidate = getEnvelope(tileCenter, new Dimension((int)BASE_TILE_SIZE, (int)BASE_TILE_SIZE), zoom);
                
                if(area.intersects(candidate,true)){
                    points.add(pt);
                }
                
            }
            
        }
        
        return points;
    }
    
    public static DirectPosition getCenter(final int zoom, final Point coordinate){        
        final GeneralDirectPosition position = new GeneralDirectPosition(GOOGLE_MERCATOR);
                
        //we look for the center, like searching for a corner if we had two times more cells
        final double zoomRes = getZoomResolution(zoom + 1);
        position.setOrdinate(0, MERCATOR_EXTEND.getMinimum(0) + zoomRes* BASE_TILE_SIZE * ( (coordinate.x)*2 + 1) );
        position.setOrdinate(1, MERCATOR_EXTEND.getMaximum(1) - zoomRes* BASE_TILE_SIZE * ( (coordinate.y)*2 + 1) );
        
        return position;
    }
    
}
