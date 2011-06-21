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
package org.geotoolkit.wmsc.map;

import org.geotoolkit.client.map.PyramidSet;
import org.geotoolkit.wms.map.WMSMapLayer;
import org.geotoolkit.wmsc.WebMapServerCached;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCMapLayer extends WMSMapLayer{

    private final PyramidSet pyramidset;
    
    /**
     * WMSC layer are not defined by any specification. each server implementing it
     * define it's own schema to describe tile sets. The user is responsible to provide
     * a pyramid set at construction time.
     * 
     * @param server
     * @param pyramidset
     * @param layer 
     */
    public WMSCMapLayer(final WebMapServerCached server, final PyramidSet pyramidset, final String layer) {
        super(server, layer);
        this.pyramidset = pyramidset;
        
        //replace wms graphic builder by wms-c builder
        graphicBuilders().clear();
        graphicBuilders().add(WMSCGraphicBuilder.INSTANCE);
    }

    public PyramidSet getPyramid() {
        return pyramidset;
    }
    
}
