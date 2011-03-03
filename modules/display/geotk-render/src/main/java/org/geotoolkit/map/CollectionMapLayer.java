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

package org.geotoolkit.map;

import java.util.Collection;
import org.opengis.filter.Id;

/**
 * A maplayer that holds a collection of object.
 * Any kind of object can be used here as long as there is a property accessor
 * registered that can handle them.
 *
 * If geometry attributes are available on objects, the CRS must by set in
 * the userMap or the SRID must be handle by the SRIDGenerator.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface CollectionMapLayer extends MapLayer{

    /**
     * Get the collection of object for this layer.
     *
     * @return The objects for this layer, can not be null.
     */
    Collection<?> getCollection();

    /**
     * A separate filter for datas that are selected on this layer.
     * @return Filter, can be null or empty.
     */
    Id getSelectionFilter();

    /**
     * Set the selection filter.
     * @param filter Id
     */
    void setSelectionFilter(Id filter);

}
