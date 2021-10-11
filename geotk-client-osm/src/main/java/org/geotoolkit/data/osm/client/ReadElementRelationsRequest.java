/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.osm.client;

import org.geotoolkit.client.Request;

/**
 * Request to get affected relations for the given element
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface ReadElementRelationsRequest extends Request{

    OSMType getElementType();

    /**
     * Set the type of element to read.
     * @param clazz : Node, Way or Relation
     */
    void setElementType(OSMType clazz);

    /**
     * @param id of the requested element
     */
    void setId(long id);

    /**
     * @return id of the requested element
     */
    long getId();

}
