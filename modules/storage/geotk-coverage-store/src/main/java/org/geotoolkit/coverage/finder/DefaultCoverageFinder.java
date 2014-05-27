/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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
package org.geotoolkit.coverage.finder;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;

/**
 * <p>Define a default CoverageFinder adapted for projects.<br/>
 * In attempt to replace this class by {@link strictlyCoverageFinder}.</p>
 *
 * @author Remi Marechal (Geomatys).
 * @author Johann Sorel (Geomatys).
 */
@Deprecated
class DefaultCoverageFinder extends CoverageFinder {
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public GridMosaic findMosaic(Pyramid pyramid, double resolution, double tolerance, Envelope env, int maxTileNumber) throws FactoryException {
        final List<GridMosaic> mosaics = new ArrayList<GridMosaic>(pyramid.getMosaics());
        Collections.sort(mosaics, SCALE_COMPARATOR);
        Collections.reverse(mosaics);
        GridMosaic result = null;
        mosaicLoop:
        for(GridMosaic candidate : mosaics){
            //check the mosaic intersect the searched envelope
            final GeneralEnvelope clip = new GeneralEnvelope(candidate.getEnvelope());
            if(!clip.intersects(env)) continue;
            //calculate the intersection, will be used to determinate the number of tiles used.
            clip.intersect(env);
            
            final DirectPosition ul = candidate.getUpperLeftCorner();
            final double scale = candidate.getScale();
            
            if(result == null){
                //set the highest mosaic as base
                result = candidate;
            }else{
                //check additional axis
                for(int i=2,n=ul.getDimension();i<n;i++){
                    final double median = env.getMedian(i);
                    final double currentDistance = Math.abs(
                            candidate.getUpperLeftCorner().getOrdinate(i) - median);
                    final double candidateDistance = Math.abs(
                            ul.getOrdinate(i) - median);
                    
                    if(candidateDistance < currentDistance){
                        //better mosaic
                        break;
                    }else if(candidateDistance > currentDistance){
                        //less accurate
                        continue mosaicLoop;
                    }
                    //continue on other axes
                }
            }
            
            //check if it will not requiere too much tiles
            final Dimension tileSize = candidate.getTileSize();
            double nbtileX = clip.getSpan(0) / (tileSize.width*scale);
            double nbtileY = clip.getSpan(1) / (tileSize.height*scale);
            
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
