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
package org.geotoolkit.wmts.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.DefaultPyramid;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.wmts.xml.v100.*;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMTSPyramid extends DefaultPyramid{

    private final TileMatrixSetLink link;
    private final TileMatrixSet matrixset;
    
    public WMTSPyramid(final WMTSPyramidSet set, final TileMatrixSetLink link){
        super(set, null);
        this.link = link;
        matrixset = set.getCapabilities().getContents().getTileMatrixSetByIdentifier(link.getTileMatrixSet());
        
        final TileMatrixSetLimits limits = link.getTileMatrixSetLimits();
        
        for(final TileMatrix matrix : matrixset.getTileMatrix()){
            
            double scale = matrix.getScaleDenominator();
            
            TileMatrixLimits limit = null;
            if(limits != null){
                for(TileMatrixLimits li : limits.getTileMatrixLimits()){
                    if(li.getTileMatrix().equals(matrix.getIdentifier().getValue())){
                        limit = li;
                        break;
                    }
                }
            }
            
            final WMTSMosaic mosaic = new WMTSMosaic(this, matrix, limit);            
            getMosaics().put(scale, mosaic);
        }
        
    }

    public TileMatrixSet getMatrixset() {
        return matrixset;
    }
    
    @Override
    public WMTSPyramidSet getPyramidSet() {
        return (WMTSPyramidSet) super.getPyramidSet();
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        final String crs = matrixset.getSupportedCRS();
        try {
            return CRS.decode(crs);
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(WMTSPyramid.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(WMTSPyramid.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
