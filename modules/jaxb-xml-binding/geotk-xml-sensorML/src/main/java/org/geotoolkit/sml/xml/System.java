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
package org.geotoolkit.sml.xml;

import org.geotoolkit.gml.xml.v311.BoundingShapeEntry;
import org.geotoolkit.gml.xml.v311.LocationPropertyType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public interface System extends AbstractComponent {

    AbstractComponents getComponents();

    AbstractPositions getPositions();

    BoundingShapeEntry getBoundedBy();

    LocationPropertyType getLocation();

    AbstractConnections getConnections();
}
