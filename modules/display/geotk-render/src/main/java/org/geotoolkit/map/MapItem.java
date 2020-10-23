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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.Description;

/**
 * Super class for map elements. MapContext and MapLayer objects are
 * map items.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class MapItem {

    public static final String IDENTIFIER_PROPERTY = org.apache.sis.portrayal.MapItem.IDENTIFIER_PROPERTY;
    public static final String TITLE_PROPERTY = org.apache.sis.portrayal.MapItem.TITLE_PROPERTY;
    public static final String ABSTRACT_PROPERTY = org.apache.sis.portrayal.MapItem.ABSTRACT_PROPERTY;
    public static final String VISIBLE_PROPERTY = org.apache.sis.portrayal.MapItem.VISIBLE_PROPERTY;

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.map");

    protected final EventListenerList listeners = new EventListenerList();
    private final Map<String,Object> parameters = new HashMap<>();
    protected String identifier = null;
    protected CharSequence title = null;
    protected CharSequence abstrat = null;
    protected Description desc = null;
    protected boolean visible = true;

    /**
     * Constructor that can used by subclass only.
     */
    protected MapItem(){
        this.desc = StyleConstants.DEFAULT_DESCRIPTION;
    }

    /**
     * Placeholder for Apache SIS new MapItem API.
     */
    public final String getIdentifier() {
        return identifier;
    }

    /**
     * Placeholder for Apache SIS new MapItem API.
     */
    public final void setIdentifier(String identifier) {
        final String oldIdentifier;
        synchronized (this) {
            oldIdentifier = this.identifier;
            if (Objects.equals(oldIdentifier, identifier)) {
                return;
            }
            this.identifier = identifier;
        }
        firePropertyChange(IDENTIFIER_PROPERTY, oldIdentifier, this.identifier);
    }

    /**
     * Placeholder for Apache SIS new MapItem API.
     */
    public final CharSequence getTitle() {
        return title;
    }

    /**
     * Placeholder for Apache SIS new MapItem API.
     */
    public final void setTitle(CharSequence title) {
        final CharSequence oldtitle;
        synchronized (this) {
            oldtitle = this.title;
            if (Objects.equals(oldtitle, title)) {
                return;
            }
            this.title = title;
        }
        firePropertyChange(TITLE_PROPERTY, oldtitle, this.title);
    }

    /**
     * Placeholder for Apache SIS new MapItem API.
     */
    public final CharSequence getAbstract() {
        return abstrat;
    }

    /**
     * Placeholder for Apache SIS new MapItem API.
     */
    public final void setAbstract(CharSequence abs) {
        final CharSequence oldabs;
        synchronized (this) {
            oldabs = this.abstrat;
            if (Objects.equals(oldabs, abs)) {
                return;
            }
            this.abstrat = abs;
        }
        firePropertyChange(ABSTRACT_PROPERTY, oldabs, this.abstrat);
    }

    /**
     * Determine whether this item is visible on a map pane or whether the
     * item is hidden.
     *
     * @return <code>true</code> if the item is visible, or <code>false</code>
     *         if the item is hidden.
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Specify whether this item is visible on a map pane or whether the item
     * is hidden. A {@link PropertyChangeEvent} is fired if the visibility changed.
     *
     * @param visible Show the item if <code>true</code>, or hide the item if
     *        <code>false</code>
     */
    public void setVisible(final boolean visible) {
        final boolean oldVisible;
        synchronized (this) {
            oldVisible = this.visible;
            if(oldVisible == visible){
                return;
            }
            this.visible = visible;
        }
        firePropertyChange(VISIBLE_PROPERTY, oldVisible, this.visible);
    }

    /**
     * @return map of all user properties.
     *          This is the live map.
     */
    public Map<String, Object> getUserProperties() {
        return parameters;
    }

    /**
     * Register a property listener.
     * @param listener property listener to register
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(PropertyChangeListener.class, listener);
    }

    /**
     * Unregister a property listener.
     * @param listener property listener to register
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(PropertyChangeListener.class, listener);
    }

    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue){
        //TODO make fire property change thread safe, preserve fire order

        final ItemListener[] listIs = listeners.getListeners(ItemListener.class);
        final PropertyChangeListener[] listPs = listeners.getListeners(PropertyChangeListener.class);
        if(listIs.length==0 && listPs.length==0) return;


        final PropertyChangeEvent event = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
        for (PropertyChangeListener listener : listIs) {
            listener.propertyChange(event);
        }
        for (PropertyChangeListener listener : listPs) {
            listener.propertyChange(event);
        }
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(Classes.getShortClassName(this));
        if(identifier != null){
            buf.append(" (");
            buf.append(identifier);
            buf.append(") ");
        }
        if(desc != null){
            buf.append(desc);
        }
        buf.append(" Visible = ");
        buf.append(visible);
        return buf.toString();
    }

    /**
     * Try to affect this object {@link #identifier} property with given resource {@link Resource#getIdentifier() identifier}.
     * Note that if an error occurs while accessing the resource identifier, a log is triggered, and we fail silently
     * (name not affected, no error thrown).
     *
     * @param input The resource to get name from. Must not be null.
     */
    protected final void trySetName(final Resource input) {
        try {
            input.getIdentifier().ifPresent((id) -> setIdentifier(id.toString()));
        } catch (DataStoreException | RuntimeException e) {
            LOGGER.log(Level.WARNING, "Cannot extract identifier from a resource", e);
        }
    }
}
