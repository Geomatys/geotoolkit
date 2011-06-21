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

import org.geotoolkit.client.map.DefaultPyramidSet;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCPyramidSet extends DefaultPyramidSet{

    private final String layer;
    
    public WMSCPyramidSet(final AbstractWMSCapabilities capa, final String layer) {
        this.layer = layer;
        
    }

    public String getLayer() {
        return layer;
    }
        
}
