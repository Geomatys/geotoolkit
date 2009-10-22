/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display.canvas;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.RenderingHints;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.logging.Logging;

import org.opengis.display.primitive.Graphic;   //for javadoc
import org.opengis.display.canvas.Canvas;       //for javadoc

/**
 * The base class for {@linkplain AbstractCanvas canvas} and
 * {@linkplain org.geotools.display.primitive.AbstractGraphic graphic primitives}. This base class
 * provides support for {@linkplain PropertyChangeListener property change listeners}, and some
 * basic services particular to the Geotools implementation like {@linkplain #getLogger logging},
 * <cite>etc.</cite>
 *
 * @module pending
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 */
public class DisplayObject {
    /**
     * The logger for the display implementation module.
     */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.display.canvas");

    /**
     * Listeners to be notified about any changes in this object properties.
     */
    protected final PropertyChangeSupport propertyListeners;

    /**
     * Listeners to be notified about any changes in this object vetoable properties.
     * 
     * Property event are also called when no veto exception have been raised.
     *  
     * 
     */
    protected final VetoableChangeSupport vetoableListeners;

    /**
     * A set of rendering hints.
     */
    protected final Hints hints;

    /**
     * Creates a new instance of display object.
     */
    protected DisplayObject() {
        this(null);
    }

    /**
     * Creates a new instance of display object with a set of hints.
     */
    protected DisplayObject(Hints hints) {
        this.propertyListeners = new PropertyChangeSupport(this);
        this.vetoableListeners = new VetoableChangeSupport(this);
        this.hints = new Hints(hints);
    }

    /**
     * Returns the locale for this object. The default implementation returns the
     * {@linkplain Locale#getDefault system locale}.
     *
     * @return Locale of this object
     */
    public Locale getLocale() {
        return Locale.getDefault();
    }

    /**
     * Returns the logger for all messages to be logged by the Geotools implementation.
     *
     * @return Logger for this object
     */
    public Logger getLogger() {
        return LOGGER;
    }

    /**
     * Returns a rendering hint. The default implementation always returns {@code null}.
     * The {@link AbstractCanvas} and other subclasses override this method in order to
     * performs real work.
     *
     * @param  key The hint key.
     * @return The hint value for the specified key, or {@code null} if none.
     *
     * @see #getHint
     */
    public Object getRenderingHint(final RenderingHints.Key key) {
        return hints.get(key);
    }

    /**
     * Adds a rendering hint. The default implementation ignore the hint value and does nothing.
     * The {@link AbstractCanvas} and other subclasses override this method in order to performs
     * real work.
     *
     * @param key   The hint key.
     * @param value The hint value. A {@code null} value remove the hint.
     *
     * @see #setHint
     */
    public void setRenderingHint(RenderingHints.Key key, Object value) {
        hints.put(key, value);
    }

    /**
     * Adds a property change listener to the listener list. The listener is registered
     * for all properties. For example, {@linkplain AbstractCanvas#add adding} or
     * {@linkplain AbstractCanvas#remove removing} graphics in a canvas may fire
     * {@value #GRAPHICS_PROPERTY} change events and, indirectly, some other side-effect
     * events like {@value #ENVELOPE_PROPERTY}.
     *
     * @param listener The property change listener to be added
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        synchronized (propertyListeners) {
            propertyListeners.addPropertyChangeListener(listener);
            listenersChanged();
        }
    }

    /**
     * Adds a property change listener for a specific property.
     * The listener will be invoked only when that specific property changes.
     *
     * @param propertyName The name of the property to listen on.
     * @param listener     The property change listener to be added.
     */
    public void addPropertyChangeListener(final String propertyName,
                                          final PropertyChangeListener listener)
    {
        synchronized (propertyListeners) {
            propertyListeners.addPropertyChangeListener(propertyName, listener);
            listenersChanged();
        }
    }

    /**
     * Adds a vetoable property change listener to the listener list.
     * The listener is registered for all vetoable properties.
     *
     * @param listener The vetoable property change listener to be added
     */
    public void addVetoableChangeListener(final VetoableChangeListener listener) {
        synchronized (vetoableListeners) {
            vetoableListeners.addVetoableChangeListener(listener);
            listenersChanged();
        }
    }

    /**
     * Adds a vetoable property change listener for a specific property.
     * The listener will be invoked only when that specific property changes.
     *
     * @param propertyName The name of the property to listen on.
     * @param listener     The vetoable property change listener to be added.
     */
    public void addVetoableChangeListener(final String propertyName,
                                          final VetoableChangeListener listener)
    {
        synchronized (vetoableListeners) {
            vetoableListeners.addVetoableChangeListener(propertyName, listener);
            listenersChanged();
        }
    }

    /**
     * Removes a property change listener from the listener list. This removes a listener
     * that was registered for all properties.
     *
     * @param listener The property change listener to be removed
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        synchronized (propertyListeners) {
            propertyListeners.removePropertyChangeListener(listener);
            listenersChanged();
        }
    }

    /**
     * Remove a property change listener for a specific property.
     *
     * @param propertyName The name of the property that was listened on.
     * @param listener     The property change listener to be removed.
     */
    public void removePropertyChangeListener(final String propertyName,
                                             final PropertyChangeListener listener)
    {
        synchronized (propertyListeners) {
            propertyListeners.removePropertyChangeListener(propertyName, listener);
            listenersChanged();
        }
    }

    /**
     * Removes a vetoable property change listener from the listener list.
     * This removes a listener that was registered for all properties.
     *
     * @param listener The vetoable property change listener to be removed
     */
    public void removeVetoableChangeListener(final VetoableChangeListener listener) {
        synchronized(vetoableListeners){
            vetoableListeners.removeVetoableChangeListener(listener);
            listenersChanged();
        }
    }

    /**
     * Remove a vetoable property change listener for a specific property.
     *
     * @param propertyName The name of the property that was listened on.
     * @param listener     The vetoable property change listener to be removed.
     */
    public void removeVetoableChangeListener(final String propertyName,
                                             final VetoableChangeListener listener)
    {
        synchronized(vetoableListeners){
            vetoableListeners.removeVetoableChangeListener(propertyName,listener);
            listenersChanged();
        }
    }

    /**
     * Invoked when a property change listener has been {@linkplain #addPropertyChangeListener
     * added} or {@linkplain #removePropertyChangeListener removed}. Some subclasses may be
     * interrested to know if there is any registered listener of a particular kind. Such
     * subclasses can override this method in order to perform their check only once.
     */
    protected void listenersChanged() {
    }

    /**
     * Invoked when an unexpected exception occured. This exception may happen while a rendering
     * is in process, so this method should not popup any dialog box and returns fast. The default
     * implementation sends a record to the {@linkplain #getLogger() logger} with the
     * {@link Level#WARNING WARNING} level.
     *
     * @param  sourceClassName  The caller's class name, for logging purpose.
     * @param  sourceMethodName The caller's method name, for logging purpose.
     * @param  exception        The exception.
     */
    protected void handleException(final Class<?> sourceClassName,
                                   final String  sourceMethodName,
                                   final Exception exception)
    {
        Logging.unexpectedException(getLogger(),
                sourceClassName, sourceMethodName, exception);
    }

    /**
     * Method that can be called when an object is no longer needed. Implementations may use
     * this method to release resources, if needed. Implementations may also implement this
     * method to return an object to an object pool. It is an error to reference a
     * {@link Graphic}, {@link Canvas} or {@link Renderer} in any way after its
     * dispose method has been called.
     */
    public void dispose() {
        synchronized (propertyListeners) {
            final PropertyChangeListener[] list = propertyListeners.getPropertyChangeListeners();
            for (int i=list.length; --i>=0;) {
                propertyListeners.removePropertyChangeListener(list[i]);
            }
            listenersChanged();
        }
        synchronized (vetoableListeners) {
            final VetoableChangeListener[] list = vetoableListeners.getVetoableChangeListeners();
            for (int i=list.length; --i>=0;) {
                vetoableListeners.removeVetoableChangeListener(list[i]);
            }
            listenersChanged();
        }
    }
}
