/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.report.graphic.map;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRenderable;

import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.display.canvas.DefaultController2D;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.DefaultRenderingContext2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.util.logging.Logging;

import org.opengis.display.canvas.RenderingState;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

/**
 * Special canvas used to render maps in JasperReport templates.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CanvasRenderer extends J2DCanvas implements JRRenderable{

    private final String id = System.currentTimeMillis() + "-" + Math.random();

    private Envelope area = null;

    private final CanvasController2D controller = new DefaultController2D(this){
        @Override
        public void setVisibleArea(Envelope env) throws NoninvertibleTransformException, TransformException {
            super.setVisibleArea(env);
            area = env;
        }
    };
    private final DefaultRenderingContext2D context2D = new DefaultRenderingContext2D(this);
    private Graphics2D g2d = null;
    private Color background = null;
    private Dimension dim = new Dimension(1,1);
    
    public CanvasRenderer(MapContext context){
        super(context.getCoordinateReferenceSystem(),null);
    }
    
    private CanvasRenderer( final Hints hints){
        super(DefaultGeographicCRS.WGS84,hints);
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
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void clearCache() {
        super.clearCache();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose(){
        super.dispose();
        context2D.dispose();
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
        monitor.renderingStarted();
        fireRenderingStateChanged(RenderingState.RENDERING);

        final Graphics2D output = g2d;

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
        if (clipBounds == null) {
            clipBounds = new Rectangle(dim);
        }
        output.setClip(clipBounds);
        output.addRenderingHints(hints);

        final DefaultRenderingContext2D context = prepareContext(context2D, output,null);

        //paint background if there is one.
        if(painter != null){
            painter.paint(context2D);
        }

        final AbstractContainer2D renderer2D = getContainer();
        if(renderer2D != null){
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
        throw new UnsupportedOperationException("JasperCanvas doesnt support getSnapshot");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public byte getType() {
        return TYPE_SVG;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public byte getImageType() {
        return IMAGE_TYPE_PNG;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Dimension2D getDimension() throws JRException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public byte[] getImageData() throws JRException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void render(Graphics2D g, Rectangle2D rect) throws JRException {
        double rotation = getController().getRotation();

        setSize(new Dimension((int)rect.getWidth(), (int)rect.getHeight()));
        try {
            getController().setVisibleArea(area);
            getController().setRotation(rotation);
        } catch (NoninvertibleTransformException ex) {
            Logging.getLogger(CanvasRenderer.class).log(Level.WARNING, null, ex);
        } catch (TransformException ex) {
            Logging.getLogger(CanvasRenderer.class).log(Level.WARNING, null, ex);
        }
        
        g2d = (Graphics2D) g.create();
        g2d.clip(rect);
        g2d.translate(rect.getMinX(), rect.getMinY());
        repaint();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected RenderingContext getRenderingContext() {
        return context2D;
    }

    @Override
    public void repaint(Shape displayArea) {
    }
    
}
