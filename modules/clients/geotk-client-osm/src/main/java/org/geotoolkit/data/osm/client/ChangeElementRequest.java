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
import org.geotoolkit.data.osm.model.IdentifiedElement;

/**
 * Request to create a new element.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface ChangeElementRequest extends Request{

    public static enum Type{
        CREATE,
        UPDATE,
        DELETE
    };

    /**
     * 
     * @return the request type : Create/Update/Delete
     */
    Type getType();

    /**
     * @param element : Node/Way/Relation to create
     */
    void setElement(IdentifiedElement element);

    /**
     * @return IdentifiedElement : Node/Way/Relation to create
     */
    IdentifiedElement getElement();

}
