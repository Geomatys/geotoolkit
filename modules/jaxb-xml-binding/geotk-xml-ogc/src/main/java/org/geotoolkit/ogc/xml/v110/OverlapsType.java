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

import javax.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.opengis.filter.spatial.Overlaps;

/**
 *
 * @author Guilhem Legal
 * @module pending
 */
@XmlRootElement(name = "Overlaps")
public class OverlapsType extends BinarySpatialOpType implements Overlaps {

    /**
     * An empty constructor used by JAXB
     */
    public OverlapsType() {
        
    }
    
    /**
     * Build a new Overlaps Type
     */
    public OverlapsType(String propertyName, AbstractGeometryType geometry) {
        super(propertyName, geometry);
    }
    
    /**
     * Build a new Overlaps Type
     */
    public OverlapsType(PropertyNameType propertyName, Object geometry) {
        super(propertyName, geometry);
    }
}
