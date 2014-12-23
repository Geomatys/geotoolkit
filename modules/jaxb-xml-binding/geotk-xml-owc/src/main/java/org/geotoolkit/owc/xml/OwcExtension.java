/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.owc.xml;

import org.geotoolkit.map.MapLayer;
import org.geotoolkit.owc.xml.v10.OfferingType;

/**
 * OWC Specification is made in a way extensions can be added.
 * 
 * @author Samuel Andrés (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public interface OwcExtension {
    
    /**
     * Extension code.
     * @return extension code, must be unique for each extension
     */
    String getCode();
    
    boolean canHandle(MapLayer layer);
    
    MapLayer createLayer(OfferingType offering);
    
    OfferingType createOffering(MapLayer mapLayer);
    
}
