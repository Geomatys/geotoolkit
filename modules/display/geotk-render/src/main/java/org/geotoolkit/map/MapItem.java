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

import java.util.List;
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
    public static final String VISIBILITY_PROPERTY = "visibility";
    
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
     * Determine whether this item is visible on a map pane or whether the
     * item is hidden.
     *
     * @return <code>true</code> if the item is visible, or <code>false</code>
     *         if the item is hidden.
     */
    boolean isVisible();

    /**
     * Specify whether this item is visible on a map pane or whether the item
     * is hidden. A {@link PropertyChangeEvent} is fired if the visibility changed.
     *
     * @param visible Show the item if <code>true</code>, or hide the item if
     *        <code>false</code>
     */
    void setVisible(boolean visible);

    /**
     * Returns the living list of all items. You may add, remove or change items
     * of this list. In case this object is a map layer object, this list will be empty
     * and immutable.
     * @return the live list
     */
    List<MapItem> items();

    /**
     * Store a value for this maplayer in a hashmap using the given key.
     */
    void setUserPropertie(String key,Object value);

    /**
     * Get a stored value knowing the key.
     */
    Object getUserPropertie(String key);

    /**
     * Register an item listener.
     * @param listener item listener to register
     */
    void addItemListener(ItemListener listener);

    /**
     * Unregister an item listener.
     * @param listener item listener to unregister.
     */
    void removeItemListener(ItemListener listener);

}
