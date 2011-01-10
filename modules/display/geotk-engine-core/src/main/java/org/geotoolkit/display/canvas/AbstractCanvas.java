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

import javax.swing.event.EventListenerList;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.canvas.control.FailOnErrorMonitor;
import org.geotoolkit.display.canvas.event.DefaultCanvasEvent;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.factory.Hints;

import org.opengis.display.canvas.Canvas;
import org.opengis.display.canvas.CanvasEvent;
import org.opengis.display.canvas.CanvasListener;
import org.opengis.display.canvas.CanvasState;
import org.opengis.display.canvas.RenderingState;
import org.opengis.display.container.ContainerEvent;
import org.opengis.display.container.ContainerListener;
import org.opengis.geometry.DirectPosition;

/**
 * Manages the display and user manipulation of {@link Graphic} instances. A newly constructed
 * {@code Canvas} is initially empty. To make something appears, {@link Graphic}s must be added
 * using the {@link AbstractContainer#add(org.opengis.display.primitive.Graphic) } method.
 * The visual content depends of the {@code Graphic}
 * subclass. The contents are usually symbols, features or images, but some implementations
 * can also manage non-geographic elements like a map scale.
 * <p>
 * This base class and the more specialized {@link ReferencedCanvas} subclass do not assume a
 * two-dimensional rendering. Those same base classes could be used for a 3D rendering engine
 * as well. {@link ReferencedCanvas2D} is the first subclass targeting specifically 2D rendering.
 * <p>
 * In GeotoolKit implementation, there is four {@linkplain CoordinateReferenceSystem coordinate
 * reference systems} involved in rendering. {@code AbstractCanvas} declares abstract methods
 * for three of them, but the actual CRS management is performed in the {@link ReferencedCanvas}
 * subclass. The CRS are enumerated below (arrows are {@linkplain MathTransform transforms}):
 *
 * <p align="center">                       data CRS  &nbsp; <img src="doc-files/right.png">
 * &nbsp; {@linkplain #getObjectiveCRS objective CRS} &nbsp; <img src="doc-files/right.png">
 * &nbsp; {@linkplain #getDisplayCRS     display CRS} &nbsp; <img src="doc-files/right.png">
 * &nbsp; {@linkplain #getDeviceCRS       device CRS}
 * </p>
 *
 * @module pending
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCanvas<T extends AbstractContainer> extends DisplayObject implements Canvas{

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractCanvas#getMonitor canvas monitor} changed.
     */
    public static final String MONITOR_PROPERTY = "monitor";

    /**
     * Small number for floating point comparaisons.
     */
    protected static final double EPS = 1E-12;

    /**
     * Listener on the renderer to be notified when graphics change.
     */
    private final ContainerListener containerListener = new ContainerListener() {

        @Override
        public void graphicsAdded(ContainerEvent event) {
            AbstractCanvas.this.graphicsAdded(event);
        }

        @Override
        public void graphicsRemoved(ContainerEvent event) {
            AbstractCanvas.this.graphicsRemoved(event);
        }

        @Override
        public void graphicsChanged(ContainerEvent event) {
            AbstractCanvas.this.graphicsChanged(event);
        }

        @Override
        public void graphicsDisplayChanged(ContainerEvent event){
            AbstractCanvas.this.graphicsDisplayChanged(event);
        }

    };

    /**
     * Container used by this canvas
     */
    protected T container;

    /**
     * Canvas listeners list.
     */
    private final EventListenerList canvasListeners = new EventListenerList();

    /**
     * The monitor assigned to this canvas, can not be null.
     */
    protected CanvasMonitor monitor = new FailOnErrorMonitor();

    /**
     * Creates an initially empty canvas.
     *
     * @param hints The initial set of hints, or {@code null} if none.
     */
    protected AbstractCanvas(final Hints hints) {
        super(hints);
    }

    

    /**
     * Set the graphics container for this canvas.
     */
    public void setContainer(final T container){
        if(this.container != null){
            this.container.removeContainerListener(containerListener);
        }

        this.container = container;

        if(this.container != null){
            this.container.addContainerListener(containerListener);
        }
        
    }

    /**
     * Get the current graphics container used by the canvas, or null
     * if the container has not be defined.
     */
    public T getContainer(){
        return container;
    }

    /**
     * Returns the monitor assigned to this {@code Canvas}, it can not be null.
     *
     * @return CanvasMonitor monitor of this canvas
     */
    public synchronized CanvasMonitor getMonitor() {
        return monitor;
    }

    /**
     * Sets the monitor of this {@code Canvas}. The monitor can not be null.
     * <p>
     * This method fires a {@value #TITLE_PROPERTY}
     * property change event.
     *
     * @param monitor The canvas monitor.
     */
    public void setMonitor(final CanvasMonitor monitor) {
        if(monitor == null){
            throw new NullPointerException("Canvas monitor can not be null");
        }

        final CanvasMonitor old;
        synchronized (this) {
            old = this.monitor;
            this.monitor = monitor;
        }
        propertyListeners.firePropertyChange(MONITOR_PROPERTY, old, monitor);
    }

    /**
     * Clears all cached data. Invoking this method may help to release some resources for other
     * applications. It should be invoked when we know that the map is not going to be rendered
     * for a while. For example it may be invoked from {@link java.applet.Applet#stop}. Note
     * that this method doesn't changes the renderer setting; it will just slow down the first
     * rendering after this method call.
     *
     * @see #dispose
     */
    public void clearCache() {
        if(container != null) container.clearCache();
    }

    /**
     * Method that may be called when a {@code Canvas} is no longer needed. {@code AbstractCanvas}
     * defines this method to invoke {@link Graphic#dispose} for all graphics. The results
     * of referencing a canvas or any of its graphics after a call to {@code dispose()} are
     * undefined.
     * <p>
     * Subclasses may use this method to release resources or to return the object to an object
     * pool. It is an error to reference a {@code Canvas} after its dispose method has been called.
     *
     * @see AbstractGraphic#dispose
     * @see javax.media.jai.PlanarImage#dispose
     */
    @Override
    public synchronized void dispose() {
        clearCache();
        if(container != null) container.dispose();
        super.dispose();
    }


    //--------------Canvas Listeners convinient methods-------------------------
    /**
     * Adds the given listener that will be notified when the state of this
     * {@code Canvas} has changed.
     */
    @Override
    public void addCanvasListener(final CanvasListener listener) {
        canvasListeners.add(CanvasListener.class, listener);
    }

    /**
     * Removes the given listener.
     */
    @Override
    public void removeCanvasListener(final CanvasListener listener) {
        canvasListeners.remove(CanvasListener.class, listener);
    }

    /**
     * Fire a canvas event to all canvas listeners.
     *
     * @param event CanvasEvent
     */
    protected void fireCanvasEvent(final CanvasEvent event){
        final CanvasListener[] listeners = canvasListeners.getListeners(CanvasListener.class);
        for(final CanvasListener listener : listeners){
            listener.canvasChanged(event);
        }
    }

    private RenderingState oldState = null;
    protected void fireRenderingStateChanged(final RenderingState state){
        final CanvasEvent event = new DefaultCanvasEvent(this, null, null, null, oldState, state);
        fireCanvasEvent(event);
        oldState = state;
    }

    //------------------------container events-----------------------------------
    /**
     * This method is automaticly called when a event is generate by the canvas
     * container when a graphic object is added.
     */
    protected void graphicsAdded(final ContainerEvent event) {
    }

    /**
     * This method is automaticly called when a event is generate by the canvas
     * container when a graphic object is removed.
     */
    protected void graphicsRemoved(final ContainerEvent event) {
    }

    /**
     * This method is automaticly called when a event is generate by the canvas
     * container when a graphic object changes.
     */
    protected void graphicsChanged(final ContainerEvent event) {
    }

    /**
     * This method is automaticly called when a event is generate by the canvas
     * container when a graphic display changes.
     */
    protected void graphicsDisplayChanged(final ContainerEvent event) {
    }


    //////////////////////////////////////////////////////////////////////////
    // Obsolete methods, TODO should be removed from geoapi //////////////////
    //////////////////////////////////////////////////////////////////////////

    @Override
    @Deprecated
    public CanvasState getState() {
        throw new UnsupportedOperationException("Not supported. Obsolete.");
    }

    @Override
    @Deprecated
    public boolean isVisible(final DirectPosition coordinate) {
        throw new UnsupportedOperationException("Not supported. Obsolete.");
    }
    
}
