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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.display.canvas.DefaultController2D;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.display.shape.XRectangle2D;

import org.opengis.display.canvas.RenderingState;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Canvas based on a BufferedImage.
 *
 * @author Johann Sorel (Geomatys)
 */
public class J2DCanvasBuffered extends J2DCanvas{

    private final CanvasController2D controller = new DefaultController2D(this);
    private final DefaultRenderingContext2D context2D = new DefaultRenderingContext2D(this);
    private BufferedImage buffer;
    private Color background = null;
    private Dimension dim;


    public J2DCanvasBuffered(CoordinateReferenceSystem crs, final Dimension dim){
        this(crs,dim,null);
    }

    public J2DCanvasBuffered(CoordinateReferenceSystem crs, final Dimension dim, final Hints hints){
        super(crs,hints);
        if(dim == null){
            throw new NullPointerException("Dimension can not be null");
        }else if(dim.getHeight() <=0 || dim.getWidth() <=0){
            throw new IllegalArgumentException("Dimension is not valid, width and height must be > 0");
        }
        setDisplayBounds(new Rectangle(dim));
        this.dim = dim;
    }

    public void setBackground(final Color color){
        this.background = color;
    }

    public void setSize(final Dimension dim){
        if(dim == null){
            throw new NullPointerException("Dimension can't be null");
        }
        if(dim.width != this.dim.width || dim.height != this.dim.height){
            setDisplayBounds(new Rectangle(dim));
            this.dim = dim;
            buffer = null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clearCache() {
        super.clearCache();
        buffer = null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose(){
        super.dispose();
        context2D.dispose();
        buffer = null;
        dim = null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CanvasController2D getController() {
        return controller;
    }

    /**
     * Returns the display bounds in terms of {@linkplain #getDisplayCRS display CRS}.
     * If no bounds were {@linkplain #setDisplayBounds explicitly set}, then this method
     * returns the {@linkplain Component#getBounds() widget bounds}.
     */
    @Override
    public synchronized Shape getDisplayBounds() {
        Shape bounds = super.getDisplayBounds();
        if (bounds.equals(XRectangle2D.INFINITY)) {
            bounds = new Rectangle(dim);
        }
        return bounds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void repaint() {

        if(buffer == null){
            //create the buffer at the last possible moment
            buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        }else{
            //we clear the buffer if it exists
            final Graphics2D g2D = (Graphics2D) buffer.getGraphics();
            g2D.setComposite( AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
            g2D.fillRect(0,0,dim.width,dim.height);
        }

        monitor.renderingStarted();
        fireRenderingStateChanged(RenderingState.RENDERING);

        final Graphics2D output = (Graphics2D) buffer.getGraphics();

        //paint background if there is one.
        if(background != null){
            output.setColor(background);
            output.fillRect(0,0,dim.width,dim.height);
        }

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
        final Rectangle displayBounds = new Rectangle(dim);
        Rectangle2D dirtyArea = XRectangle2D.INFINITY;
        if (clipBounds == null) {
            clipBounds = displayBounds;
        } else if (displayBounds.contains(clipBounds)) {
            dirtyArea = clipBounds;
        }
        output.setClip(clipBounds);
        output.addRenderingHints(hints);

        final DefaultRenderingContext2D context = prepareContext(context2D, output,null);
        final AbstractContainer renderer = getContainer();
        if(renderer != null && renderer instanceof AbstractContainer2D){
            final AbstractContainer2D renderer2D = (AbstractContainer2D) renderer;
            render(context, renderer2D.getSortedGraphics());
        }

        /**
         * End painting, erase dirtyArea
         */
        output.dispose();
        fireRenderingStateChanged(RenderingState.ON_HOLD);
        monitor.renderingFinished();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage getSnapShot(){
        return buffer;
    }

    @Override
    protected RenderingContext getRenderingContext() {
        return context2D;
    }

    @Override
    public void repaint(Shape displayArea) {

    }

}
