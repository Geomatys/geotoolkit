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
package org.geotoolkit.display.primitive;

import java.util.Locale;
import java.util.logging.Logger;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.opengis.display.canvas.Canvas;
import org.opengis.display.primitive.Graphic;

import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.display.canvas.DisplayObject;

/**
 * The root abstraction of a graphic object taxonomy, specifying the methods common to a
 * lightweight set of graphic objects.
 *
 * @since 2.3
 * @source $URL$
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractGraphic extends DisplayObject implements Graphic, PropertyChangeListener {
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractGraphic#getName graphic name} changed.
     */
    public static final String NAME_PROPERTY = "name";

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractGraphic#getParent graphic parent} changed.
     */
    public static final String PARENT_PROPERTY = "parent";

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractGraphic#getVisible graphic visibility} changed.
     */
    public static final String VISIBLE_PROPERTY = "visible";
    
    /**
     * The canvas that own this graphic, or {@code null} if none.
     */
    protected final Canvas canvas;

    /**
     * The name assigned to this graphic.
     */
    protected String name;

    /**
     * The parent of this graphic, or {@code null} if none.
     */
    protected Graphic parent;

    /**
     * Tells if this graphic is visible.
     *
     * @see #setVisible
     */
    protected boolean visible = true;

    /**
     * Creates a new graphic. The {@linkplain #getZOrderHint z-order} default to positive infinity
     * (i.e. this graphic is drawn on top of everything else). Subclasses should invokes setters
     * methods in order to define properly this graphic properties.
     */
    protected AbstractGraphic(final Canvas canvas) {
        if(canvas == null) throw new NullPointerException("Canvas can not be null");
        this.canvas = canvas;

        if(this.canvas instanceof DisplayObject)
            ((DisplayObject)this.canvas).addPropertyChangeListener(this);

    }

    /**
     * If this display object is contained in a canvas, returns the canvas that own it.
     * Otherwise, returns {@code null}.
     *
     * @return Canvas, The canvas that this graphic listen to.
     */
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Returns the name assigned to this {@code Graphic}.
     */
    public String getName() {
        final String name = this.name;  // Avoid the need for synchronization.
        return name;
    }

    /**
     * Sets the name of this {@code Graphic} to the given value.
     * <p>
     * This method fires a {@value org.geotools.display.canvas.DisplayObject#NAME_PROPERTY}
     * property change event.
     */
    public void setName(final String name) {
        final String old;
        synchronized (getTreeLock()) {
            old = this.name;
            this.name = name;
        }
        propertyListeners.firePropertyChange(NAME_PROPERTY, old, name);
    }

    /**
     * Returns the parent of this {@code Graphic}, or {@code null} if none. Usually, only
     * {@link org.opengis.go.display.primitive.AggregateGraphic}s have {@code Graphic} children.
     */
    public Graphic getParent() {
        return parent;
    }

    /**
     * Sets the parent of this {@code Graphic}.
     * <p>
     * This method fires a {@value org.geotools.display.canvas.DisplayObject#PARENT_PROPERTY}
     * property change event.
     */
    public void setParent(final Graphic parent) {
        final Graphic old;
        synchronized (getTreeLock()) {
            old = this.parent;
            this.parent = parent;
        }
        propertyListeners.firePropertyChange(PARENT_PROPERTY, old, parent);
    }

    /**
     * Determines whether this graphic should be visible when its {@linkplain #getCanvas canvas}
     * is visible. The default value is {@code true}.
     *
     * @return {@code true} if the graphic is visible, {@code false} otherwise.
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visible value. This method may be invoked when the user wants to hide momentarily
     * this graphic.
     * <p>
     * This method fires a {@value org.geotools.display.canvas.DisplayObject#VISIBLE_PROPERTY}
     * property change event.
     */
    @Override
    public void setVisible(final boolean visible) {
        synchronized (getTreeLock()) {
            if (visible == this.visible) {
                return;
            }
            this.visible = visible;
        }
        propertyListeners.firePropertyChange(VISIBLE_PROPERTY, !visible, visible);
    }

    /**
     * Property change event generated by the canvas.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt){}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        synchronized (getTreeLock()) {
            super.dispose();
            if(this.canvas instanceof DisplayObject)
                ((DisplayObject)this.canvas).addPropertyChangeListener(this);
        }
    }

    /**
     * Returns the locale for this object. If this graphic is contained in a
     * {@linkplain AbstractCanvas canvas}, then the default implementation returns the canvas
     * locale. Otherwise, this method returns the {@linkplain Locale#getDefault system locale}.
     */
    @Override
    public Locale getLocale() {
        final Canvas candidate = getCanvas();
        if (candidate instanceof DisplayObject) {
            return ((DisplayObject) candidate).getLocale();
        }
        return super.getLocale();
    }

    /**
     * Returns the logger for all messages to be logged by the Geotools implementation of GO-1. If
     * this object is a {@linkplain Graphic graphic} which is contained in a {@linkplain Canvas
     * canvas}, then the default implementation returns the canvas logger. Otherwise, this method
     * returns a default one.
     */
    @Override
    public Logger getLogger() {
        final Canvas candidate = getCanvas();
        if (candidate instanceof DisplayObject) {
            return ((DisplayObject) candidate).getLogger();
        }
        return super.getLogger();
    }

    /**
     * Returns the lock for synchronisation. If this object is contained in a canvas,
     * then this method returns the same lock than the canvas.
     */
    public final Object getTreeLock() {
        final Canvas candidate = this.canvas;
        return (candidate != null) ? (Object) candidate : (Object) this;
    }

    /**
     * Returns a string representation of this graphic. This method is for debugging purpose
     * only and may changes in any future version.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + '[' + getName() + ']';
    }
}
