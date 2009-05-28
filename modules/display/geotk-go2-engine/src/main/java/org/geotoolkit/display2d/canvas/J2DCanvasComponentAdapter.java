/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display2d.canvas;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.display.canvas.DefaultController2D;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.referencing.operation.matrix.AffineMatrix3;
import org.geotools.resources.GraphicsUtilities;
import org.geotoolkit.display.shape.XRectangle2D;

import org.opengis.display.canvas.RenderingState;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Adapter object to link a ReferencedCanvas2D to a Component.
 *
 * @author Johann Sorel (Geomatys)
 */
public class J2DCanvasComponentAdapter extends J2DCanvas{

    /**
     * The component owner, or {@code null} if none. This is used for managing
     * repaint request (see {@link GraphicPrimitive2D#refresh}) or mouse events.
     */
    private final Component owner;

    /**
     * Updates the enclosing canvas according various AWT events.
     */
    private final ComponentListener listener = new ComponentAdapter(){
        
        /** Invoked when the component's size changes. */
        @Override public void componentResized(final ComponentEvent event) {
            synchronized (J2DCanvasComponentAdapter.this) {
                //cache bounds
                Rectangle old = null;
                if(cachedBounds != null){
                    old = new Rectangle(cachedBounds);
                }
                owner.getBounds(cachedBounds);
                setDisplayBounds(cachedBounds);
                displayBoundsChanged(old,cachedBounds);

                zoomChanged(null);
            }
        }

        /** Invoked when the component's position changes. */
        @Override public void componentMoved(final ComponentEvent event) {
            synchronized (J2DCanvasComponentAdapter.this) {
                //cache bounds
                Shape old = null;
                if(cachedBounds != null){
                    old = new Rectangle(cachedBounds);
                }
                event.getComponent().getBounds(cachedBounds);
                setDisplayBounds(cachedBounds);
                displayBoundsChanged(old,cachedBounds);

                zoomChanged(null); // Translation term has changed.
            }
        }

        /** Invoked when the component has been made invisible. */
        @Override public void componentHidden(final ComponentEvent event) {
            synchronized (J2DCanvasComponentAdapter.this) {
                cachedBounds.x = 0;
                cachedBounds.y = 0;
                cachedBounds.width = 0;
                cachedBounds.height = 0;
                setDisplayBounds(cachedBounds);

                clearCache();
            }
            // As a symetrical approach,  it would be nice to invoke 'prefetch(...)' inside
            // 'componentShown(...)' too. But we don't know for sure what the widget bounds
            // and the zoom will be. We are better to wait until 'paint(...)' is invoked.
        }
        
    };
    
    private final CanvasController2D controller = new DefaultController2D(this);
    private final DefaultRenderingContext2D context2D = new DefaultRenderingContext2D(this);

    /**
     * Rectangle in which to place the coordinates returned by {@link #getDisplayBounds}. This
     * object is defined in order to avoid allocating objects too often {@link Rectangle}.
     */
    private final transient Rectangle cachedBounds = new Rectangle();

    public J2DCanvasComponentAdapter(CoordinateReferenceSystem crs,final Component owner){
        super(crs,null);
        this.owner = owner;
        if (owner != null) {
            owner.addComponentListener(listener);
        }
    }

    /**
     * Returns the display bounds in terms of {@linkplain #getDisplayCRS display CRS}.
     * If no bounds were {@linkplain #setDisplayBounds explicitly set}, then this method
     * returns the {@linkplain Component#getBounds() widget bounds}.
     */
    @Override
    public synchronized Shape getDisplayBounds() {
        Shape bounds = super.getDisplayBounds();
        if (bounds.equals(XRectangle2D.INFINITY) && owner!=null) {
            bounds = owner.getBounds();
        }
        return bounds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CanvasController2D getController() {
        return controller;
    }
        
    
    //----------------------AWT Paint methods ----------------------------------
    public void paint(Graphics2D output){
        monitor.renderingStarted();
        fireRenderingStateChanged(RenderingState.RENDERING);
        output.addRenderingHints(hints);
        Rectangle clipBounds = output.getClipBounds();
        
        /*
         * Sets a flag for avoiding some "refresh()" events while we are actually painting.
         * For example some implementation of the GraphicPrimitive2D.paint(...) method may
         * detects changes since the last rendering and invokes some kind of invalidate(...)
         * methods before the graphic rendering begin. Invoking those methods may cause in some
         * indirect way a call to GraphicPrimitive2D.refresh(), which will trig an other widget
         * repaint. This second repaint is usually not needed, since Graphics usually managed
         * to update their informations before they start their rendering. Consequently,
         * disabling repaint events while we are painting help to reduces duplicated rendering.
         */
        final Rectangle displayBounds = getDisplayBounds().getBounds();
        Rectangle2D dirtyArea = XRectangle2D.INFINITY;
        if (clipBounds == null) {
            clipBounds = displayBounds;
        } else if (displayBounds.contains(clipBounds)) {
            dirtyArea = clipBounds;
        }
                
        //correct the displayToDevice transform
        final AffineTransform normalize = output.getDeviceConfiguration().getNormalizingTransform();
        displayToDevice = new AffineMatrix3(normalize);

        
       
        AffineMatrix3 old =  previousObjectiveToDisplay.clone();
        AffineMatrix3 objToDisp = null;

        //retrieve an affineTransform that will not be modify
        // while rendering
        try{
            objToDisp = setObjectiveToDisplayTransform(clipBounds);
        }catch(TransformException exception){
            exception.printStackTrace();
            GraphicsUtilities.paintStackTrace(output, owner.getBounds(), exception);
            return;
        }
        
        //notify graphics that the affine changed
        if( !old.equals(objToDisp) ){            
            propertyListeners.firePropertyChange(AbstractCanvas.OBJECTIVE_TO_DISPLAY_PROPERTY, old, objToDisp);
        }
        
        final DefaultRenderingContext2D context = prepareContext(context2D, output,null);
        final AbstractContainer renderer         = getContainer();
        if(renderer != null && renderer instanceof AbstractContainer2D){
            final AbstractContainer2D renderer2D = (AbstractContainer2D) renderer;
            render(context,renderer2D.getSortedGraphics());
        }
        
        /**
         * End painting, erase dirtyArea
         */
//        paintFinished(true);
        fireRenderingStateChanged(RenderingState.ON_HOLD);
        monitor.renderingFinished();
    }
    
    /**
     * Declares that the {@link Component} need to be repainted. This method can be invoked
     * from any thread (it doesn't need to be the <cite>Swing</cite> thread). Note that this
     * method doesn't invoke any {@link #flushOffscreenBuffer} method; this is up to the caller
     * to invokes the appropriate method.
     */
    @Override
    public void repaint() {
        if (owner != null) {
            owner.repaint();
        }
    }

    @Override
    public Image getSnapShot() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected RenderingContext getRenderingContext() {
        return context2D;
    }

    @Override
    public void repaint(Shape displayArea) {
        repaint();
    }

}
