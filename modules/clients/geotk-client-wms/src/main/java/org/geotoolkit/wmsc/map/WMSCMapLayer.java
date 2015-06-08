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

import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.wms.map.WMSGraphicBuilder;
import org.geotoolkit.wms.map.WMSMapLayer;
import org.geotoolkit.wmsc.WMSCCoverageReference;
import org.geotoolkit.wmsc.WebMapClientCached;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCMapLayer extends WMSMapLayer{
    
    private static WMSCCoverageReference toReference(final WebMapClientCached server, final GenericName name) throws CapabilitiesException{
        return new WMSCCoverageReference(server, name);
    }
    
    /**
     * @param server
     * @param layer : only one layer possible in wms-c
     */
    public WMSCMapLayer(final WebMapClientCached server, final GenericName name) throws CapabilitiesException {
        super(toReference(server, name));
        graphicBuilders().add(WMSGraphicBuilder.INSTANCE);
    }
    
}
