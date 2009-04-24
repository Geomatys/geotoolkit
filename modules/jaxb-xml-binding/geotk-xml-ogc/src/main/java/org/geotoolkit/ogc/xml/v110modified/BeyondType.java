/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogc.xml.v110modified;

import org.geotoolkit.gml.xml.v311modified.AbstractGeometryType;
import org.opengis.filter.spatial.Beyond;

/**
 *
 * @author Guilhem Legal
 */
public class BeyondType extends DistanceBufferType implements Beyond {

    /**
     * An empty constructor used by JAXB
     */
    public BeyondType() {
        
    }
    
    /**
     * Build a new Beyond Type
     */
    public BeyondType(String propertyName, AbstractGeometryType geometry, double distance, String unit) {
        super(propertyName, geometry, distance, unit);
    }
    

}
