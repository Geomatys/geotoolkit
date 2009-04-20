/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import javax.swing.event.EventListenerList;

import org.opengis.display.canvas.RenderingState;
import org.opengis.util.InternationalString;
import org.opengis.referencing.crs.DerivedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.display.canvas.Canvas;
import org.opengis.display.canvas.CanvasEvent;
import org.opengis.display.canvas.CanvasListener;
import org.opengis.display.primitive.Graphic;

import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.canvas.control.FailOnErrorMonitor;
import org.geotoolkit.display.canvas.event.DefaultCanvasEvent;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.display.primitive.AbstractGraphic;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.converter.Classes;
import org.opengis.display.container.ContainerEvent;
import org.opengis.display.container.ContainerListener;

/**
 * Manages the display and user manipulation of {@link Graphic} instances. A newly constructed
 * {@code Canvas} is initially empty. To make something appears, {@link Graphic}s must be added
 * using the {@link #add add} method. The visual content depends of the {@code Graphic}
 * subclass. The contents are usually symbols, features or images, but some implementations
 * can also manage non-geographic elements like a map scale.
 * <p>
 * This base class and the more specialized {@link ReferencedCanvas} subclass do not assume a
 * two-dimensional rendering. Those same base classes could be used for a 3D rendering engine
 * as well. {@link ReferencedCanvas2D} is the first subclass targeting specifically 2D rendering.
 * <p>
 * In Geotools implementation, there is four {@linkplain CoordinateReferenceSystem coordinate
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
 * @since 2.3
 * @source $URL$
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCanvas extends DisplayObject implements Canvas {
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * canvas {@linkplain ReferencedCanvas2D#getDisplayBounds display bounds} changed.
     */
    public static final String DISPLAY_BOUNDS_PROPERTY = "displayBounds";

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * canvas {@linkplain ReferencedCanvas#getDisplayCRS display CRS} changed.
     */
    public static final String DISPLAY_CRS_PROPERTY = "displayCRS";

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * canvas {@linkplain ReferencedCanvas#getObjectiveCRS objective CRS} changed.
     */
    public static final String OBJECTIVE_CRS_PROPERTY = "objectiveCRS";

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * canvas {@linkplain ReferencedCanvas#getScale canvas scale} changed.
     */
    public static final String SCALE_PROPERTY = "scale";
    
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * canvas {@linkplain ReferencedCanvas#getScale canvas scale} changed.
     */
    public static final String OBJECTIVE_TO_DISPLAY_PROPERTY = "objectiveToDisplay";

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractCanvas#getTitle canvas title} changed.
     */
    public static final String TITLE_PROPERTY = "title";
    
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractCanvas#getMonitor canvas monitor} changed.
     */
    public static final String MONITOR_PROPERTY = "monitor";
    
    /**
     * Canvas listeners list.
     */
    private final EventListenerList canvasListeners;
    
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
     * A set of rendering hints.
     *
     * @see Hints#COORDINATE_OPERATION_FACTORY
     */
    protected final Hints hints;

    /**
     * Renderer used by this canvas
     */
    private AbstractContainer container = null;

    /**
     * The title assigned to this canvas, or {@code null} if none. It may be either an instance
     * of {@link String} or {@link InternationalString}.
     */
    protected InternationalString title;
    
    /**
     * The monitor assigned to this canvas, can not be null.
     */
    protected CanvasMonitor monitor = new FailOnErrorMonitor();

    /**
     * Creates an initially empty canvas.
     *
     * @param renderer the renderer for this canvas
     * @param hints   The initial set of hints, or {@code null} if none.
     */
    protected AbstractCanvas(final Hints hints) {
        this.canvasListeners = new EventListenerList();
        this.hints = new Hints(hints);
    }

    /**
     * Returns the title assigned to this {@code Canvas}, or {@code null} if none. If the title
     * was {@linkplain #setTitle(InternationalString) defined as an international string}, then
     * this method returns the title in the {@linkplain #getLocale current locale}.
     *
     * @return InternationalString of the title
     */
    public synchronized InternationalString getTitle() {
        return title;
    }

    /**
     * Sets the title of this {@code Canvas}. The title of a {@code Canvas}
     * may or may not be displayed on the titlebar of an application's window.
     * <p>
     * This method fires a {@value org.geotools.display.canvas.DisplayObject#TITLE_PROPERTY}
     * property change event.
     *
     * @param title The International String title.
     */
    public void setTitle(final InternationalString title) {
        final InternationalString old;
        synchronized (this) {
            old = this.title;
            this.title = title;
        }
        propertyListeners.firePropertyChange(TITLE_PROPERTY, old, title);
    }

    /**
     * Set the renderer for this canvas.
     */
    public void setContainer(AbstractContainer renderer){
        
        if(this.container != null){
            this.container.removeContainerListener(containerListener);
        }
        
        this.container = renderer;
        
        if(this.container != null){
            this.container.addContainerListener(containerListener);
        }
        
    }
    
    /**
     * Get the current renderer used by the canvas, or null
     * if the renderer has not be defined.
     */
    public AbstractContainer getContainer() {
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
     * This method fires a {@value org.geotools.display.canvas.DisplayObject#TITLE_PROPERTY}
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
        if(container != null) container.dispose();
        super.dispose();
    }

    /**
     * Returns a string representation of this canvas and all its {@link Graphic}s.
     * The {@linkplain BufferedCanvas2D#getOffscreenBuffered offscreen buffer type},
     * if any, appears in the right column. This method is for debugging purpose
     * only and may change in any future version.
     *
     * @return String representation of the canvas
     */
    @Override
    public synchronized String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this));
        buffer.append("[\"").append(getTitle()).append("\" ]");
        return buffer.toString();
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
    protected void fireCanvasEvent(CanvasEvent event){
        CanvasListener[] listeners = canvasListeners.getListeners(CanvasListener.class);
        for(CanvasListener listener : listeners){
            listener.canvasChanged(event);
        }
    }

    private RenderingState oldState = null;
    protected void fireRenderingStateChanged(RenderingState state){
        CanvasEvent event = new DefaultCanvasEvent(this, null, null, null, oldState, state);
        fireCanvasEvent(event);
        oldState = state;
    }

    //-----------------------CRS methods ---------------------------------------
    /**
     * Sets the objective Coordinate Reference System for this {@code Canvas}.
     *
     * @param  crs The objective coordinate reference system.
     * @throws TransformException If the data can't be transformed.
     */
    public abstract void setObjectiveCRS(final CoordinateReferenceSystem crs) throws TransformException;

    /**
     * Returns the objective Coordinate Reference System (the projection of a georeferenced CRS)
     * for this {@code Canvas}. This is the "real world" CRS used for displaying all graphics.
     * Note that underlying data in graphic primitives don't need to be in terms of this CRS.
     * Transformations will be applied on the fly as needed at rendering time.
     *
     * @return Objective CRS
     */
    public abstract CoordinateReferenceSystem getObjectiveCRS();

    /**
     * Returns the Coordinate Reference System associated with the display of this {@code Canvas}.
     * Its {@linkplain CoordinateReferenceSystem#getCoordinateSystem coordinate system} corresponds
     * to the geometry of the display device. It is usually a {@linkplain CartesianCS cartesian} one
     * for video monitor, but may also be a {@linkplain SphericalCS spherical} one for planetarium.
     * <p>
     * When rendering on a flat screen using <cite>Java2D</cite>, axis are oriented as in the
     * {@linkplain java.awt.Graphics2D Java2D space}: coordinates are in "dots" (about 1/72 of inch),
     * <var>x</var> values increasing right and <var>y</var> values increasing <strong>down</strong>.
     * <p>
     * In the Geotools implementation, the display CRS must be
     * {@linkplain DerivedCRS#getBaseCRS derived from} the
     * {@linkplain #getObjectiveCRS objective CRS}. The
     * {@linkplain DerivedCRS#getConversionFromBase conversion from base} is usually an
     * {@linkplain AffineTransform affine transform} with the scale terms proportional to the map
     * {@linkplain ReferencedCanvas#getScale scale factor}. The
     * {@linkplain AffineTransform#getScaleY y scale value} is often negative because of the
     * <var>y</var> axis oriented toward down.
     *
     * @return Display CRS
     * @see ReferencedCanvas#setDisplayCRS
     */
    public abstract DerivedCRS getDisplayCRS();

    /**
     * Returns the Coordinate Reference System associated with the device of this {@code Canvas}.
     * The device CRS is related to the {@linkplain #getDisplayCRS display CRS} in a device
     * dependent (but zoom independent) way.
     * <p>
     * When rendering on screen, device CRS and {@linkplain #getDisplayCRS display CRS} are usually
     * identical. Those CRS differ more often during printing, in which case the <cite>display to
     * device</cite> transform depends on the printer resolution. For example in the specific case
     * of <cite>Java2D</cite>, the {@linkplain #getDisplayCRS display CRS} is defined in such a way
     * that one display unit is approximatively equals to 1/72 of inch no matter what the printer
     * resolution is. The display CRS is then what <cite>Java2D</cite> calls
     * {@linkplain java.awt.Graphics2D user space}, and the <cite>display to device</cite> transform
     * is the {@linkplain java.awt.GraphicsConfiguration#getDefaultTransform transform mapping
     * display units do device units}.
     * <p>
     * The default implementation returns the {@linkplain #getDisplayCRS display CRS}, i.e. assumes
     * that the <cite>display to device</cite> transform is the identity transform. Subclasses need
     * to override this method if they can manage device-dependent transformations. In any case,
     * the device {@linkplain CoordinateReferenceSystem#getCoordinateSystem coordinate system} must
     * be the same one then the display coordinate system (not to be confused with coordinate
     * <em>reference</em> system).
     * <p>
     * Most users will deal with the {@linkplain #getDisplayCRS display CRS} rather than this
     * device CRS.
     *
     * @return
     * @see ReferencedCanvas#setDeviceCRS
     */
    public DerivedCRS getDeviceCRS() {
        return getDisplayCRS();
    }

    /**
     * Sets the {@linkplain #getObjectiveCRS objective} to {@linkplain #getDisplayCRS display}
     * transform to the specified transform. This method is typically invoked by subclasses
     * every time the zoom change.
     * <p>
     * Note that some subclasses may require that the transform is affine.
     *
     * @param  transform The {@linkplain #getObjectiveCRS objective} to
     *         {@linkplain #getDisplayCRS display} transform.
     * @throws TransformException if the transform can not be set to the specified value.
     */
    public abstract void setObjectiveToDisplayTransform(final MathTransform transform)
            throws TransformException;

    //------------------------Renderer events-----------------------------------    
    /**
     * This method is automaticly called when a event is generate by the canvas
     * renderer when a graphic object is added.
     */
    protected void graphicsAdded(ContainerEvent event) {
    }

    /**
     * This method is automaticly called when a event is generate by the canvas
     * renderer when a graphic object is removed.
     */
    protected void graphicsRemoved(ContainerEvent event) {
    }

    /**
     * This method is automaticly called when a event is generate by the canvas
     * renderer when a graphic object changes.
     */
    protected void graphicsChanged(ContainerEvent event) {
    }
    
    /**
     * This method is automaticly called when a event is generate by the canvas
     * renderer when a graphic display changes.
     */
    protected void graphicsDisplayChanged(ContainerEvent event) {
    }
    
    //--------------------------Hints ------------------------------------------
    /**
     * Returns a rendering hint.
     *
     * @param  key The hint key (e.g. {@link #FINEST_RESOLUTION}).
     * @return The hint value for the specified key, or {@code null} if none.
     */
    @Override
    public synchronized Object getRenderingHint(final RenderingHints.Key key) {
        return hints.get(key);
    }

    /**
     * Adds a rendering hint. Hints provides optional information used by some rendering code.
     *
     * @param key   The hint key (e.g. {@link #FINEST_RESOLUTION}).
     * @param value The hint value. A {@code null} value remove the hint.
     *
     * @see #FINEST_RESOLUTION
     * @see #REQUIRED_RESOLUTION
     * @see #PREFETCH
     * @see Hints#COORDINATE_OPERATION_FACTORY
     * @see RenderingHints#KEY_RENDERING
     * @see RenderingHints#KEY_COLOR_RENDERING
     * @see RenderingHints#KEY_INTERPOLATION
     */
    @Override
    public synchronized void setRenderingHint(final RenderingHints.Key key, final Object value) {
        if (value != null) {
            if (!value.equals(hints.put(key, value))) {
                clearCache(); // Invokes only if the new value is different than the previous one.
            }
        } else {
            if (hints.remove(key) != null) {
                clearCache();
            }
        }
    }

}
