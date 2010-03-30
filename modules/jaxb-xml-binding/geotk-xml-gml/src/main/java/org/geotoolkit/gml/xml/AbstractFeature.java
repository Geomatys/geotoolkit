/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.gml.xml;

import java.util.List;
import org.geotoolkit.gml.xml.v311.BoundingShapeEntry;
import org.geotoolkit.gml.xml.v311.LocationPropertyType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public interface AbstractFeature extends AbstractGML {

    BoundingShapeEntry getBoundedBy();

    LocationPropertyType getLocation();
    
    List<String> getSrsName();
}
