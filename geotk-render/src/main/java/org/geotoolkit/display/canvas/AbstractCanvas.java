/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2013, Geomatys
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

import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.canvas.control.FailOnErrorMonitor;
import org.geotoolkit.factory.Hints;

import static org.apache.sis.util.ArgumentChecks.*;

/**
 * Manages the display and user manipulation of {@link Graphic} instances. A newly constructed
 * {@code Canvas} is initial empty. To make something appears, {@link Graphic}s must be added
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
 * @module
 * @since 2.3
 * @version $Id$
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCanvas<T extends GraphicContainer> extends Canvas {

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractCanvas#getMonitor canvas monitor} changed.
     */
    public static final String MONITOR_KEY = "monitor";
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractCanvas#getRenderState canvas state} changed.
     */
    public static final String RENDERSTATE_KEY = "renderstate";
    public static final Boolean ON_HOLD = Boolean.FALSE;
    public static final Boolean RENDERING = Boolean.TRUE;

    /**
     * Small number for floating point compare.
     */
    protected static final double EPS = 1E-12;

    /**
     * Container used by this canvas
     */
    protected T container;

    /**
     * The monitor assigned to this canvas, can not be null.
     */
    protected CanvasMonitor monitor = new FailOnErrorMonitor();

    /**
     * Creates an initial empty canvas.
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
        this.container = container;
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
        ensureNonNull("canvas monitor", monitor);
        final CanvasMonitor old;
        synchronized (this) {
            old = this.monitor;
            this.monitor = monitor;
        }
        firePropertyChange(MONITOR_KEY, old, monitor);
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

    protected void fireRenderingStateChanged(final boolean newState){
        firePropertyChange(RENDERSTATE_KEY, !newState, newState);
    }

}
