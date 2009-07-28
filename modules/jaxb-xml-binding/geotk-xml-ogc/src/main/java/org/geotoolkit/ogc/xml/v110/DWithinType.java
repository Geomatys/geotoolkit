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
package org.geotoolkit.ogc.xml.v110;

import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.opengis.filter.spatial.DWithin;

/**
 *
 * @author Guilhem Legal
 */
public class DWithinType extends DistanceBufferType implements DWithin {
     /**
     * An empty constructor used by JAXB
     */
    public DWithinType() {
        
    }
    
    /**
     * Build a new DWithin Filter
     */
    public DWithinType(String propertyName, AbstractGeometryType geometry, double distance, String unit) {
        super(propertyName, geometry, distance, unit);
    }

}
