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
package org.geotoolkit.display.container;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;

import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotoolkit.display.canvas.ReferencedCanvas;
import org.geotoolkit.display.primitive.AbstractGraphic;
import org.geotoolkit.display.primitive.AbstractReferencedGraphic;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.resources.Errors;

import org.geotoolkit.util.logging.Logging;
import org.opengis.display.canvas.Canvas;
import org.opengis.display.container.ContainerEvent;
import org.opengis.display.container.ContainerListener;
import org.opengis.display.container.GraphicsContainer;
import org.opengis.display.primitive.Graphic;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Abstract renderer implements Renderer.
 * Provide convinient methods to handle properties, hints add methods
 * related to graphics.
 * 
 * @module pending
 * @since 2.5
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractContainer implements GraphicsContainer<Graphic>{

    /**
     * A listener to be notified when a graphic property changed.
     */
    private final PropertyChangeListener graphicListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            final Object source = event.getSource();
            graphicPropertyChanged((Graphic)source, event);            
        }
    };
    
    /**
     * The set of {@link Graphic}s to display. Keys and values are identical; values are used as
     * a way to recognize existing graphics that are equals to the {@linkplain #add added} ones.
     * <p>
     * This map must preserve the order in which the user added graphics. This order must be
     * preserved no matter how {@link #sortedGraphics} reorder graphics. This is because we
     * want to preserve to {@link #add} contract for 2D graphics whit a z-value.
     */
    protected final Map<Graphic,Graphic> graphics = new LinkedHashMap<Graphic,Graphic>();

    /**
     * The set of {@link Graphic}s given to the user. This set is act like a
     * proxy, this set delegate his methods to the graphics map.
     */
    private final AbstractSet<Graphic> userGraphics = new AbstractSet<Graphic>() {

        @Override
        public Iterator<Graphic> iterator() {
            return graphics.keySet().iterator();
        }

        @Override
        public int size() {
            return graphics.size();
        }
    };

    /**
     * Container listeners list. Thoses listeners are informed when
     * graphic objects are added, removed or changes.
     */
    protected final EventListenerList ContainerListeners = new EventListenerList();

    protected final Canvas canvas;
    
    
    /**
     * Create a Default Abstract renderer with no particular hints.
     */
    protected AbstractContainer(ReferencedCanvas canvas){
        this.canvas = canvas;
        canvas.addPropertyChangeListener(new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                AbstractContainer.this.propertyChange(evt);
            }
        });
    }
                
    /**
     * Invoked automatically when a graphic registered in this renderer changed. Subclasses can
     * override this method if they need to react to some graphic change events, but should
     * always invoke {@code super.graphicPropertyChanged(graphic, event)}.
     * <p>
     * Fire a graphic changed event if it's a graphic visibility property event.
     * </p>
     * 
     * @param graphic The graphic that changed.
     * @param event   The property change event.
     */
    protected void graphicPropertyChanged(final Graphic graphic, final PropertyChangeEvent propEvent){
        final String propertyName = propEvent.getPropertyName();
                
        if(propertyName.equals(AbstractGraphic.VISIBLE_PROPERTY)){
            ContainerEvent event = new DefaultContainerEvent(this, graphic);
            fireGraphicChanged(event);
        }else if(propertyName.equals(AbstractReferencedGraphic.ENVELOPE_PROPERTY)){
            ContainerEvent event = new DefaultContainerEvent(this, graphic);
            fireGraphicChanged(event);
        }
        
    }
        
    /**
     * {@inheritDoc}
     */
    public void clearCache() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        removeAll();
        for (final Graphic graphic : graphics()) {
            graphic.dispose();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Canvas getCanvas(){
        return canvas;
    }
    
    
    //------------ graphic methods ---------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Collection<Graphic> graphics() {
        return userGraphics;
    }
    
    /**
     * Adds the given {@code Graphic} to this {@code Renderer}. 
     * <p>
     * Most {@code Canvas} do not draw anything as long as at least one graphic hasn't be added.
     * In GeotoolKit implementation, an {@link AbstractGraphic} can be added to only one
     * {@link AbstractCanvas} object. If the specified graphic has already been added to
     * an other canvas, then this method {@linkplain AbstractGraphic#clone creates a clone}
     * before to add the graphic.
     * <p>
     * This method fires graphicsAdded method on renderer listeners.
     *
     * @param  graphic Graphic to add to this renderer. This method call will be ignored if
     *         {@code graphic} has already been added to this canvas.
     * @return The graphic added. This is usually the supplied graphic, but may also be a
     *         new one if this method cloned the graphic.
     * @throws IllegalArgumentException If {@code graphic} has already been added to an other
     *         {@code Renderer} and the graphic is not cloneable.
     *
     * @see #remove
     * @see #removeAll
     * @see #getGraphics
     *
     * @todo Current implementation has a risk of thread lock if {@code canvas1.add(graphic2)} and
     *       {@code canvas2.add(graphic1)} are invoked in same time in two concurrent threads, where
     *       {@code canvas1} and {@code canvas2} are two instances of {@code AbstractCanvas},
     *       {@code graphic1} and {@code graphic2} are two instances of {@code AbstractGraphic},
     *       {@code graphic1} was already added to {@code canvas1} and {@code graphic2} was already
     *       added to {@code canvas2} before the above-cited {@code add} method calls.
     */
    protected synchronized Graphic add(Graphic graphic) throws IllegalArgumentException {
        
        if (graphic instanceof AbstractGraphic) {
            final AbstractGraphic candidate = (AbstractGraphic) graphic;
            synchronized (candidate.getTreeLock()) {
                final Canvas canvas = candidate.getCanvas();
                if (canvas == this) {
                    // The supplied graphic is already part of this canvas.
                    assert graphics.containsKey(candidate) : candidate;
                } else {
                    assert !graphics.containsKey(candidate) : candidate;
                    if (canvas != null) {
//                        try {
                            graphic = candidate;
//                        graphic = candidate.clone();
//                        } catch (CloneNotSupportedException e) {
//                            throw new IllegalArgumentException(
//                                    Errors.format(Errors.Keys.CANVAS_NOT_OWNER_$1, candidate.getName()), e);
//                        }

                    }
                    candidate.addPropertyChangeListener(graphicListener);
                }
            }
            // The graphic lock should now be the same as the canvas lock.
//            assert Thread.holdsLock(candidate.getTreeLock());
        }
        /*
         * Add the new graphic in the 'graphics' array. The array will growth as needed and
         * 'sortedGraphics' is set to null  so that the array will be resorted when needed.
         * If an identical graphic (in the sense of Object.equals(....)) existed prior this
         * method call, then the previous graphic instance will be kept (instead of the new
         * supplied one).
         */
        final Graphic previous = graphics.put(graphic, graphic);
        if (previous != null) {
            graphic = previous;
            graphics.put(graphic, graphic);
        }
        
        // TODO will have to separate this call, to avoid having multiple event for a
        // collection of graphics
        ContainerEvent event = new DefaultContainerEvent(this, graphic);
        fireGraphicAdded(event);
        
        return graphic;
    }

    /**
     * Removes the given {@code Graphic} from this {@code Renderer}. Note that if the graphic is
     * going to be added back to the same renderer later, then it is more efficient to invoke
     * {@link Graphic#setVisible} instead.
     * <p>
     * This method fires graphicsRemoved method on renderer listeners.
     *
     * @param  graphic The graphic to remove. This method call will be ignored if {@code graphic}
     *         has already been removed from this canvas.
     * @throws IllegalArgumentException If {@code graphic} is owned by an other {@code Canvas}
     *         than {@code this}.
     *
     * @see #add
     * @see #removeAll
     * @see #getGraphics
     */
    protected synchronized void remove(final Graphic graphic) throws IllegalArgumentException {
        
        if (graphic instanceof AbstractGraphic) {
            final AbstractGraphic candidate = (AbstractGraphic) graphic;
            final Canvas canvas = candidate.getCanvas();

            if(graphics.containsKey(candidate)){
                candidate.removePropertyChangeListener(graphicListener);
                graphics.remove(candidate);
                candidate.dispose();
                return;
            }

            if (canvas == null) {
                assert !graphics.containsKey(candidate) : candidate;
                return;
            }
            
            if (canvas != this) {
                assert !graphics.containsKey(candidate) : candidate;
                throw new IllegalArgumentException(Errors.format(
                            Errors.Keys.CANVAS_NOT_OWNER_$1, candidate.getName()));
            }
//            assert Thread.holdsLock(candidate.getTreeLock());
            candidate.removePropertyChangeListener(graphicListener);
            candidate.dispose();
        } else {
            if (!graphics.containsKey(graphic)) {
                return;
            }
        }
        if (graphics.remove(graphic) != graphic) {
            throw new AssertionError(graphic); // Should never happen.
        }
        
        // TODO will have to separate this call, to avoid having multiple event for a
        // collection of graphics
        ContainerEvent event = new DefaultContainerEvent(this, graphic);
        fireGraphicRemoved(event);
    }

    /**
     * Remove all graphics from this renderer.
     * <p>
     * This method fires graphicsRemoved method on renderer listeners.
     *
     * @see #add
     * @see #remove
     * @see #getGraphics
     */
    protected synchronized void removeAll() {
        
        Set<Graphic> vals = graphics.keySet();
        
        for (final Graphic graphic : vals) {
            if (graphic instanceof AbstractGraphic) {
                final AbstractGraphic candidate = (AbstractGraphic) graphic;
                assert Thread.holdsLock(candidate.getTreeLock());
                candidate.removePropertyChangeListener(graphicListener);
                candidate.dispose();
            }
        }
                
        clearCache();
        
        // TODO will have to separate this call, to avoid having multiple event for a
        // collection of graphics
        ContainerEvent event = new DefaultContainerEvent(this, vals);
        fireGraphicRemoved(event);
    }

    /**
     * Returns an envelope that completly encloses all {@linkplain ReferencedGraphic#getEnvelope
     * graphic envelopes} managed by this canvas. Note that there is no guarantee that the returned
     * envelope is the smallest bounding box that encloses the canvas, only that the canvas lies
     * entirely within the indicated envelope.
     * <p>
     * This envelope is different from
     * {@link org.geotoolkit.display.canvas.map.DefaultMapState#getEnvelope}, since the later returns
     * an envelope that encloses only the <em>visible</em> canvas area and is scale-dependent. This
     * {@code ReferencedCanvas.getEnvelope()} method is scale-independent. Both envelopes are equal
     * if the scale is choosen in such a way that all graphics fit exactly in the canvas visible
     * area.
     *
     * @return The envelope for this canvas in terms of {@linkplain #getObjectiveCRS objective CRS}.
     *
     * @see org.geotoolkit.display.canvas.map.DefaultMapState#getEnvelope
     * @see ReferencedCanvas2D#getEnvelope2D
     */
    @Override
    public abstract GeneralEnvelope getGraphicsEnvelope();

    /**
     * Called when the canvas CRS has changed.
     * SubClasses should update there graphic objects if needed.
     */
    protected abstract void updateObjectiveCRS(final CoordinateReferenceSystem crs)
            throws TransformException;

    //--------------------renderer listeners------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void addContainerListener(ContainerListener listener) {
        ContainerListeners.add(ContainerListener.class, listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeContainerListener(ContainerListener listener) {
        ContainerListeners.remove(ContainerListener.class, listener);
    }
    
    /**
     * Convinient method to propagate added graphics on renderer listeners.
     * 
     * @param event : RendererEvent event to propagate.
     */
    protected final void fireGraphicAdded(ContainerEvent event){
        final ContainerListener[] listeners = ContainerListeners.getListeners(ContainerListener.class);
        for(final ContainerListener listener : listeners){
            listener.graphicsAdded(event);
        }
    }
    
    /**
     * Convinient method to propagate removed graphics on renderer listeners.
     * 
     * @param event : RendererEvent event to propagate.
     */
    protected final void fireGraphicRemoved(ContainerEvent event){
        final ContainerListener[] listeners = ContainerListeners.getListeners(ContainerListener.class);
        for(final ContainerListener listener : listeners){
            listener.graphicsRemoved(event);
        }
    }
        
    /**
     * Convinient method to propagate graphics changes on renderer listeners.
     * 
     * @param event : RendererEvent event to propagate.
     */
    protected final void fireGraphicChanged(ContainerEvent event){
        final ContainerListener[] listeners = ContainerListeners.getListeners(ContainerListener.class);
        for(final ContainerListener listener : listeners){
            listener.graphicsChanged(event);
        }
    }

    /**
     * Convinient method to propagate graphics display changes on renderer listeners.
     *
     * @param event : RendererEvent event to propagate.
     */
    protected final void fireGraphicDisplayChanged(ContainerEvent event){
        final ContainerListener[] listeners = ContainerListeners.getListeners(ContainerListener.class);
        for(final ContainerListener listener : listeners){
            listener.graphicsDisplayChanged(event);
        }
    }
             
    //-------------------canvas events------------------------------------------

    protected void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(AbstractCanvas.OBJECTIVE_CRS_PROPERTY)){
            try {
                updateObjectiveCRS((CoordinateReferenceSystem) evt.getNewValue());
            } catch (TransformException ex) {
                Logging.getLogger(AbstractContainer.class).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
