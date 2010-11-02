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

package org.geotoolkit.map;

import java.beans.PropertyChangeListener;
import org.opengis.style.Description;

/**
 * Super interface for map elements. MapContext and MapLayer objects are
 * map items.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface MapItem {

    public static final String NAME_PROPERTY = "name";
    public static final String DESCRIPTION_PROPERTY = "description";
    
    /**
     * Set the item name, this should be used as an
     * identifier. Use getdescription for UI needs.
     */
    void setName(String name);

    /**
     * Get the layer name. Use getDescription for UI needs.
     */
    String getName();

    /**
     * Set the item description. this holds a title and an abstract summary
     * used for user interfaces.
     */
    void setDescription(Description desc);

    /**
     * Returns the description of the item. This holds a title and an abstract summary
     * used for user interfaces.
     */
    Description getDescription();

    /**
     * Store a value for this maplayer in a hashmap using the given key.
     */
    void setUserPropertie(String key,Object value);

    /**
     * Get a stored value knowing the key.
     */
    Object getUserPropertie(String key);

    /**
     * Register a property change listener.
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Unregister a property change listener.
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

}
