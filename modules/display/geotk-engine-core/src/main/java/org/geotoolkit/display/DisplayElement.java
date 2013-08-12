/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display;

import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.factory.Hints;

/**
 * Convenient class for rendering canvas and scene graphics.
 * Provides :
 * - default logger 
 * - event system
 * - hints map
 * 
 * @author Johann Sorel (Geomatys)
 */
public class DisplayElement {
    
    /**
     * Default logger for the display module.
     */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.display");
    
    /**
     * A set of rendering hints.
     */
    private Hints hints;

    /**
     * Listeners list.
     */
    private EventListenerList listeners;
    
    /**
     * No rendering hints.
     */
    public DisplayElement() {
        this(null);
    }

    /**
     * 
     * @param hints rendering hints
     */
    public DisplayElement(final Hints hints) {
        this.hints = hints;
    }
    
    /**
     * Returns the logger for all messages to be logged by this object.
     *
     * @return Logger for this object
     */
    public Logger getLogger() {
        return LOGGER;
    }
    
    /**
     * Get the EventListener list.
     * 
     * @param create, set to true to force list creation if null.
     * @return Hints, can be null
     */
    protected synchronized EventListenerList getListenerList(boolean create){
        if(create && listeners == null) listeners = new EventListenerList();
        return listeners;
    }
    
    /**
     * Adds a property change listener to the listener list. 
     * The listener is registered for all properties.
     *
     * @param listener The property change listener to be added
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        final EventListenerList lst = getListenerList(true);
        lst.add(PropertyChangeListener.class, listener);
    }

    /**
     * Removes a property change listener from the listener list. This removes a listener
     * that was registered for all properties.
     *
     * @param listener The property change listener to be removed
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        final EventListenerList lst = getListenerList(true);
        lst.remove(PropertyChangeListener.class, listener);
    }

    /**
     * Fire a property change event.
     * 
     * @param propName property name
     * @param oldValue old property value
     * @param newValue new property value
     */
    protected void firePropertyChange(final String propName, final Object oldValue, final Object newValue) {
        final EventListenerList lst = getListenerList(false);
        if (lst == null) {
            return;
        }

        final PropertyChangeListener[] listeners = lst.getListeners(PropertyChangeListener.class);
        if (listeners.length == 0) {
            return;
        }

        final PropertyChangeEvent event = new PropertyChangeEvent(this, propName, oldValue, newValue);
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }

    /**
     * Get the Hints map.
     * 
     * @param create, set to true to force map creation if null.
     * @return Hints, can be null
     */
    protected synchronized Hints getHints(boolean create){
        if(create && hints == null) hints = new Hints();
        return hints;
    }
    
    /**
     * Returns a rendering hint. The default implementation always returns {@code null}.
     * The {@link AbstractCanvas} and other subclasses override this method in order to
     * performs real work.
     *
     * @param  key The hint key.
     * @return The hint value for the specified key, or {@code null} if none.
     *
     * @see #getRenderingHint(java.awt.RenderingHints.Key)
     */
    public Object getRenderingHint(final RenderingHints.Key key) {
        final Hints hints = getHints(false);
        return (hints==null) ? null : hints.get(key);
    }

    /**
     * Adds a rendering hint. The default implementation ignore the hint value and does nothing.
     * The {@link AbstractCanvas} and other subclasses override this method in order to performs
     * real work.
     *
     * @param key   The hint key.
     * @param value The hint value. A {@code null} value remove the hint.
     *
     * @see #setRenderingHint(java.awt.RenderingHints.Key, java.lang.Object)
     */
    public void setRenderingHint(final RenderingHints.Key key, final Object value) {
        final Hints hints = getHints(true);
        hints.put(key, value);
    }
    
    /**
     * Method that can be called when an object is no longer needed. Implementations may use
     * this method to release resources, if needed. Implementations may also implement this
     * method to return an object to an object pool. It is an error to reference a
     * {@link DisplayElement} in any way after its dispose method has been called.
     */
    public void dispose() {
    }
    
    /**
     * Returns a string representation of this element. 
     * This method is for debugging purpose only and may changes in any future version.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this);
    }
}
