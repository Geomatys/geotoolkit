/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.storage.coverage;

import org.apache.sis.geometry.GeneralEnvelope;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractPyramidSet implements PyramidSet {
    
    @Override
    public Pyramid getPyramid(String pyramidId) {
        for(Pyramid p : getPyramids()){
            if(p.getId().equals(pyramidId)){
                return p;
            }
        }
        
        return null;
    }

    @Override
    public GridMosaic getMosaic(String pyramidId, String mosaicId) {
        final Pyramid p = getPyramid(pyramidId);
        if(p == null){ return null; }
        
        for(GridMosaic m : p.getMosaics()){
            if(m.getId().equals(mosaicId)){
                return m;
            }
        }
        return null;
    }
    
    @Override
    public Envelope getEnvelope() {
        for(Pyramid pyramid : getPyramids()){
            //we consider the first pyramid to be in the main data crs
            GeneralEnvelope env = null;
            for(GridMosaic mosaic : pyramid.getMosaics()){
                if(env==null){
                    env = new GeneralEnvelope(mosaic.getEnvelope());
                }else{
                    env.add(mosaic.getEnvelope());
                }
            }
            return env;
        }
        return null;
    }
    
}
