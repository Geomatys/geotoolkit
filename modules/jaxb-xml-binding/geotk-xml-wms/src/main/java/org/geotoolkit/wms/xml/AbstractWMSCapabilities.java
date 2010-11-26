/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wms.xml;

import javax.xml.bind.annotation.XmlSeeAlso;
import org.geotoolkit.wms.xml.v111.WMT_MS_Capabilities;
import org.geotoolkit.wms.xml.v130.WMSCapabilities;

/**
 * Abstract main class which describe a capabilities document  for all version of WMS.
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlSeeAlso({WMT_MS_Capabilities.class, WMSCapabilities.class})
public abstract class AbstractWMSCapabilities implements WMSResponse {

     /**
     * return the Service part of the capabilities document.
     * 
     */
    public abstract AbstractService getService(); 
    
    /**
     *return the capability part of the capabilities document.
     * 
     */
    public abstract AbstractCapability getCapability();

    /**
     * get the version number.
     * 
     */
    public abstract String getVersion(); 

    /**
     * Gets the value of the updateSequence property.
     * 
     */
    public abstract String getUpdateSequence();
    
    /**
     * Get a specific layer from the capabilities document.
     * 
     */
    public abstract AbstractLayer getLayerFromName(String name);

    /**
     * Get a specific layer from the capabilities document.
     * The Layer may be contain in other layers, so this is a stack which last
     * element is the wanted layer.
     */
    public abstract AbstractLayer[] getLayerStackFromName(String name);

}
