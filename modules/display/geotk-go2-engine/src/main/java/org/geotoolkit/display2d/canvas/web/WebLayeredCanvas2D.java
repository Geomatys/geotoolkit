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
package org.geotoolkit.display2d.canvas.web;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RecyclingTileFactory;

import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.display.canvas.DefaultController2D;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.canvas.DefaultRenderingContext2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.display.shape.XRectangle2D;

import org.opengis.display.canvas.RenderingState;
import org.opengis.display.primitive.Graphic;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Canvas used for web client. requiere a special listener to be notified of the renderer images.
 * it return one image by z order.
 *
 * @author Johann Sorel (Geomatys)
 */
public class WebLayeredCanvas2D extends J2DCanvas{

    private static final RecyclingTileFactory RECYCLER = new RecyclingTileFactory();    
    
    private final CanvasController2D controller = new DefaultController2D(this);
    private Dimension dim;

    public WebLayeredCanvas2D(CoordinateReferenceSystem crs, final Dimension dim){
        this(crs,dim,null);
    }

    public WebLayeredCanvas2D(CoordinateReferenceSystem crs, final Dimension dim, final Hints hints){
        super(crs,hints);
        if(dim == null){
            throw new NullPointerException("Dimension can not be null");
        }else if(dim.getHeight() <=0 || dim.getWidth() <=0){
            throw new IllegalArgumentException("Dimension is not valid, width and height must be > 0");
        }
        setDisplayBounds(new Rectangle(dim));
        this.dim = dim;
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
        
        final AbstractContainer renderer = getContainer();

        if(renderer == null || !(renderer instanceof AbstractContainer2D)) return;

        final AbstractContainer2D renderer2D = (AbstractContainer2D) renderer;
        final List<Graphic> graphics        = renderer2D.getSortedGraphics();

        double lastZ = -1;
        final List<Graphic> Zgraphics = new ArrayList<Graphic>();
        for(final Graphic graphic : graphics){

            if( !(graphic instanceof GraphicJ2D)) continue;

            final GraphicJ2D j2d  = (GraphicJ2D) graphic;
            final double graphicZ = j2d.getZOrderHint();

            if(graphicZ == lastZ){
                Zgraphics.add(graphic);
            }else{
                if(!Zgraphics.isEmpty()){
                    render(Zgraphics, lastZ);
                    Zgraphics.clear();
                }
                lastZ = graphicZ;
                Zgraphics.add(graphic);
            }

        }

        if(!Zgraphics.isEmpty()){
            render(Zgraphics, lastZ);
            Zgraphics.clear();
        }
        
        // End painting
        fireRenderingStateChanged(RenderingState.ON_HOLD);
        monitor.renderingFinished();
    }

    private void render(final List<Graphic> graphics, final double z){
        final List<Graphic> copy = new ArrayList<Graphic>(graphics);
        
        new Thread(){
            
            @Override
            public void run(){
                
                //Grab a bufferImage from the recycling factory
                final RenderingHints rh = new RenderingHints(null);
                rh.put(JAI.KEY_TILE_RECYCLER, RECYCLER);
                rh.put(JAI.KEY_TILE_FACTORY, RECYCLER);
                rh.put(JAI.KEY_IMAGE_LAYOUT,  new ImageLayout().setTileWidth(dim.width).setTileHeight(dim.height));
                final Byte[] bandValues = new Byte[]{0,0,0,0};
                final ParameterBlock pb = new ParameterBlock();
                pb.add((float)dim.width);   // The width
                pb.add((float)dim.height);  // The height
                pb.add(bandValues);       // The band values
                // Create the constant operation.
                final PlanarImage pi = (PlanarImage)JAI.create("constant", pb,rh);
                final BufferedImage buffer = pi.getAsBufferedImage();
                
//                final BufferedImage buffer    = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
                final Rectangle displayBounds = new Rectangle(dim);
                final Graphics2D output       = (Graphics2D) buffer.getGraphics();                
                output.setClip(displayBounds);
                output.addRenderingHints(hints);
                final DefaultRenderingContext2D context = prepareContext(new DefaultRenderingContext2D(WebLayeredCanvas2D.this), output,null);
                render(context, copy);

                if(monitor instanceof WebLayeredCanvasMonitor){
                    WebLayeredCanvasMonitor moni = (WebLayeredCanvasMonitor) monitor;
                    moni.imageCreated(buffer, z);
                }
                
                //recycle le raster to optimize further needs.
                RECYCLER.recycleTile(buffer.getRaster());
                
            }
            
        }.start();
        
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage getSnapShot(){
        return null;
    }

    @Override
    protected RenderingContext getRenderingContext() {
        return null;
    }

    @Override
    public void repaint(Shape displayArea) {
        repaint();
    }

}
