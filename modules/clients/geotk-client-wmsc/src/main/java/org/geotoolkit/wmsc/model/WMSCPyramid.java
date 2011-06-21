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
package org.geotoolkit.wmsc.model;

import java.awt.geom.Point2D;
import java.util.List;
import org.geotoolkit.client.map.DefaultPyramid;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.wmsc.xml.v111.TileSet;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCPyramid extends DefaultPyramid{
        
    private final TileSet tileset;
    private final Point2D upperleft;
    
    public WMSCPyramid(final WMSCPyramidSet set, final TileSet tileset) throws NoSuchAuthorityCodeException, FactoryException{
        super(set,CRS.decode(tileset.getSRS()));        
        this.tileset = tileset;
        
        this.upperleft = new Point2D.Double(
                tileset.getBoundingBox().getMinx(), 
                tileset.getBoundingBox().getMaxy());
        
        final List<Double> ress = tileset.getResolutions();
        if(ress == null){
            return;
        }
        
        for(Double res : tileset.getResolutions()){
            getMosaics().put(res, new WMSCMosaic(this, res));
        }
        
    }

    public TileSet getTileset() {
        return tileset;
    }
     
    public Point2D getUpperLeftCorner(){
        return upperleft;
    }
    
}
