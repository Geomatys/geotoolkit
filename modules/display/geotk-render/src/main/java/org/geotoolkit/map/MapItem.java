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
import java.util.List;
import java.util.Map;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.style.DefaultDescription;
import org.opengis.style.Description;
import org.opengis.util.InternationalString;

/**
 * Super interface for map elements. MapContext and MapLayer objects are
 * map items.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface MapItem {

    public static final String NAME_PROPERTY = "name";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String VISIBILITY_PROPERTY = "visible";

    /**
     * Placeholder for Apache SIS new MapItem API.
     */
    default String getIdentifier() {
        return getName();
    }

    /**
     * Placeholder for Apache SIS new MapItem API.
     */
    default void setIdentifier(String identifier) {
        setName(identifier);
    }

    /**
     * Placeholder for Apache SIS new MapItem API.
     */
    default CharSequence getTitle() {
        return getName();
    }

    /**
     * Placeholder for Apache SIS new MapItem API.
     */
    default void setTitle(CharSequence title) {
        InternationalString titl = (title instanceof InternationalString || title == null) ? (InternationalString)title : new SimpleInternationalString(title.toString());
        Description description = getDescription();
        InternationalString abst = null;
        if (description != null) {
            abst = description.getAbstract();
        }
        setDescription(new DefaultDescription(titl, abst));
    }

    /**
     * Placeholder for Apache SIS new MapItem API.
     */
    default CharSequence getAbstract() {
        return getName();
    }

    /**
     * Placeholder for Apache SIS new MapItem API.
     */
    default void setAbstract(CharSequence abs) {
        InternationalString abst = (abs instanceof InternationalString || abs == null) ? (InternationalString)abs : new SimpleInternationalString(abs.toString());
        Description description = getDescription();
        InternationalString title = null;
        if (description != null) {
            title = description.getTitle();
        }
        setDescription(new DefaultDescription(title, abst));
    }

    /**
     * Set the item name, this should be used as an
     * identifier. Use getdescription for UI needs.
     *
     * @deprecated to be remove, migration to apache SIS API, use identifier property instead
     */
    @Deprecated
    void setName(String name);

    /**
     * Get the layer name. Use getDescription for UI needs.
     *
     * @deprecated to be remove, migration to apache SIS API, use identifier property instead
     */
    @Deprecated
    String getName();

    /**
     * Set the item description. this holds a title and an abstract summary
     * used for user interfaces.
     *
     * @deprecated to be remove, migration to apache SIS API, use title and abstract properties instead
     */
    @Deprecated
    void setDescription(Description desc);

    /**
     * Returns the description of the item. This holds a title and an abstract summary
     * used for user interfaces.
     *
     * @deprecated to be remove, migration to apache SIS API, use title and abstract properties instead
     */
    @Deprecated
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
     * @return map of all user properties.
     *          This is the live map.
     */
    Map<String,Object> getUserProperties();

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

    /**
     * Register a property listener.
     * @param listener property listener to register
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Unregister a property listener.
     * @param listener property listener to register
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

}
