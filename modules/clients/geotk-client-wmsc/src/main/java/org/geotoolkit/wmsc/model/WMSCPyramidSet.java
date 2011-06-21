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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.client.map.DefaultPyramidSet;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.v111.Capability;
import org.geotoolkit.wms.xml.v111.VendorSpecificCapabilities;
import org.geotoolkit.wmsc.xml.v111.TileSet;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCPyramidSet extends DefaultPyramidSet{

    private static final Logger LOGGER = Logging.getLogger(WMSCPyramidSet.class);
    
    private final String layer;
    
    public WMSCPyramidSet(final AbstractWMSCapabilities capa, final String layer) {
        this.layer = layer;
        
        //WMSC is a WMS 1.1.1
        final Capability capas = (Capability) capa.getCapability();        
        final VendorSpecificCapabilities vendor = capas.getVendorSpecificCapabilities();
        
        if(vendor == null){
            return;
        }
        
        final List<TileSet> sets = vendor.getTileSet();
        
        if(sets == null){
            return;
        }
        
        //find tileset definition for this layer
        for(final TileSet set : sets){
            for(String layerName : set.getLayers()){
                if(!layer.equals(layerName)){
                    continue;
                }
                            
                try {
                    final WMSCPyramid pyramid = new WMSCPyramid(this, set);
                    getPyramids().add(pyramid);
                } catch (NoSuchAuthorityCodeException ex) {
                    LOGGER.log(Level.INFO, ex.getMessage(),ex);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.INFO, ex.getMessage(),ex);
                }
                
            }
        }        
    }

    public String getLayer() {
        return layer;
    }
        
}
