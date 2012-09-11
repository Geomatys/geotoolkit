/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.coverage;

import java.awt.Dimension;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Utility functions for coverage and mosaic.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class CoverageUtilities {
    
    private CoverageUtilities(){}
    
    /**
     * Find the most appropriate pyramid in given pyramid set and given crs.
     * Returned Pyramid may not have the given crs.
     * 
     * @param set : pyramid set to search in
     * @param crs searched crs
     * @return Pyramid, never null exept if the pyramid set is empty
     */
    public static Pyramid findPyramid(final PyramidSet set, final CoordinateReferenceSystem crs){
        
        Pyramid result = null;
        for(Pyramid pyramid : set.getPyramids()){
            
            if(result == null){
                result = pyramid;
            }
            
            if(CRS.equalsApproximatively(pyramid.getCoordinateReferenceSystem(),crs)){
                //we found a pyramid for this crs
                result = pyramid;
                break;
            }
            
        }
        
        return result;
    }
    
    /**
     * Find the most appropriate mosaic in the pyramid with the given informations.
     * 
     * @param pyramid
     * @param resolution
     * @param tolerance
     * @param env
     * @return GridMosaic
     */
    public static GridMosaic findMosaic(final Pyramid pyramid, final double resolution, 
            final double tolerance, final Envelope env, int maxTileNumber){
        
        GridMosaic result = null;        
        final double[] scales = pyramid.getScales();
        
        for(int i=0;i<scales.length;i++){
            final double scale = scales[i];            
                        
            final GridMosaic candidate = pyramid.getMosaic(i);            
            if(result == null){
                result = candidate;
            }
            
            //check if it will not requiere too much tiles
            final Dimension tileSize = candidate.getTileSize();
            double nbtileX = env.getSpan(0) / (tileSize.width*scale);
            double nbtileY = env.getSpan(1) / (tileSize.height*scale);
            
            //if the envelope has some NaN, we presume it's a square
            if(Double.isNaN(nbtileX) || Double.isInfinite(nbtileX)){
                nbtileX = nbtileY;
            }else if(Double.isNaN(nbtileY) || Double.isInfinite(nbtileY)){
                nbtileY = nbtileX;
            }
            
            if(maxTileNumber > 0 && nbtileX*nbtileY > maxTileNumber){
                //we haven't reach the best resolution, it would requiere
                //too much tiles, we use the previous scale level
                break;
            }
            
            result = candidate;
            
            if( (scale * (1-tolerance)) < resolution){                      
                //we found the most accurate resolution
                break;
            }           
        }
                
        return result;
    }
    
}
