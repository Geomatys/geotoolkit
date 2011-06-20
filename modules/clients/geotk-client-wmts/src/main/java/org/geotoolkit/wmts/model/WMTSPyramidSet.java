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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.geotoolkit.client.map.Pyramid;
import org.geotoolkit.client.map.PyramidSet;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.wmts.xml.v100.Capabilities;
import org.geotoolkit.wmts.xml.v100.ContentsType;
import org.geotoolkit.wmts.xml.v100.LayerType;
import org.geotoolkit.wmts.xml.v100.TileMatrixSetLink;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMTSPyramidSet implements PyramidSet{

    private final Capabilities capabilities;
    private final String layerName;
    
    public WMTSPyramidSet(final Capabilities capabilities, final String layerName){
        ArgumentChecks.ensureNonNull("capabilities", capabilities);
        ArgumentChecks.ensureNonNull("layer name", layerName);
        this.capabilities = capabilities;
        this.layerName = layerName;
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    public String getLayerName() {
        return layerName;
    }
        
    @Override
    public Collection<Pyramid> getPyramids() {        
        final List<Pyramid> pyramids = new ArrayList<Pyramid>();
        
        final ContentsType contents = capabilities.getContents();
        
        //first find the layer
        LayerType layer = null;
        for(LayerType candidate : contents.getLayers()){            
            if(layerName.equalsIgnoreCase(candidate.getIdentifier().getValue())){
                layer = candidate;
                break;
            }            
        }
        
        if(layer == null){
            //layer not found
            return pyramids;
        }
        
        for(TileMatrixSetLink lk : layer.getTileMatrixSetLink()){
            pyramids.add(new WMTSPyramid(this,lk));
        }
        
        return pyramids;
    }
    
}
