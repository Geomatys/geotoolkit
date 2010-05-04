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
package org.geotoolkit.display2d.container;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;


/**
 * Multithread rendering process used by GraphicContextJ2D.
 * this class handle several threads and buffer to speed up
 * rendering when distant layers exist in the mapcontext.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MultiThreadedRendering{

    private final ReferencedCanvas2D canvas;
    private final MapContext context;
    private final Map<MapLayer, GraphicJ2D> layerGraphics;
    private final RenderingContext2D renderingContext;
    private final SortedMap<Integer, BufferedImage> buffers = new TreeMap<Integer, BufferedImage>();


    public MultiThreadedRendering(final ReferencedCanvas2D canvas, final MapContext context,
            final Map<MapLayer, GraphicJ2D> layerGraphics,
            final RenderingContext2D renderingContext){
        this.canvas = canvas;
        this.context = context;
        this.layerGraphics = layerGraphics;
        this.renderingContext = renderingContext;
    }

    /**
     * Clear the buffer cache.
     */
    public void dispose() {
        buffers.clear();
    }

    /**
     * Pack the buffers, merge the different buffer when possible.
     * @return true if there is no more buffer to pack. false
     * if some buffers are not ready yet.
     */
    private boolean pack() {

        synchronized(buffers){
            final Set<Integer> keySet = buffers.keySet();
            final Integer[] keys = keySet.toArray(new Integer[keySet.size()]);

            boolean valid = true;

            //we go throw the buffer and merge (with the renderingContext or between them)
            //when possible to reduce used memory as much as possible
            BufferedImage buffer = null;
            BufferedImage previousBuffer = null;
            Integer index = null;
            Integer previousIndex = null;
            for(int i=keys.length-1 ; i>=0 ; i--){
                index = keys[i];
                buffer = buffers.get(index);

                if(buffer == null){
                    //a buffer is not ready
                    valid = false;
                }else{
                    if(previousBuffer != null){
                        //check if the buffer has a valid ARGB type
                        buffer = validateBuffer(buffer);
                        buffers.put(index, buffer);
                        //we merge with previous buffer to reduce memory use
                        buffer.getGraphics().drawImage(previousBuffer, 0, 0, null);
                        //remove previous buffer from cache
                        buffers.remove(previousIndex);
                    }

                    if(i==0){
                        renderingContext.switchToDisplayCRS();
                        renderingContext.getGraphics().drawImage(buffer, 0,0,null);
                        //remove buffer from cache
                        buffers.remove(index);
                    }
                }

                previousIndex = index;
                previousBuffer = buffer;
            }
            return valid;
        }
    }

    /**
     * Wake the dispatching thread.
     */
    private synchronized void wake() {
        notifyAll();
    }

    /**
     * Make sleep the dispatch thread.
     */
    private synchronized void block() {
        try {
            wait();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Build a temporary bufferImage of the canvas size.
     */
    private BufferedImage buildBuffer(){
        final Rectangle rect = renderingContext.getCanvasDisplayBounds();
        return new BufferedImage( rect.width, rect.height, BufferedImage.TYPE_INT_ARGB );
    }

    /**
     * Check the type of the buffer,
     * @return if the type is correct the original buffer is returned otherwise
     * a new buffer is created and the content of the old buffer is painted on the
     * new one.
     */
    private BufferedImage validateBuffer(final BufferedImage buffer){

        if(buffer.getType() != BufferedImage.TYPE_INT_ARGB){
            final BufferedImage newBuffer = buildBuffer();
            newBuffer.getGraphics().drawImage(buffer, 0, 0, null);
            return newBuffer;
        }

        return buffer;
    }

    public void render(){
        final List<MapLayer> layers = context.layers();

        for (final MapLayer layer : layers) {

            //we ignore invisible layers
            if (!layer.isVisible()) {
                continue;
            }

            final int zOrder = layers.indexOf(layer);

            synchronized(buffers){
                buffers.put(zOrder, null);
            }

            new Thread() {
                @Override
                public void run() {
                    final BufferedImage img = buildBuffer();
                    final RenderingContext2D tc = renderingContext.create(img.createGraphics());
                    final GraphicJ2D gra = layerGraphics.get(layer);

                    gra.paint(tc);

                    synchronized(buffers){
                        buffers.put(zOrder, img);
                    }

                    //we wake the dispatch thread that may be waiting for it
                    wake();
                }
            }.start();
        }
        

        //we now wait for every rendering to finish
        synchronized(this){
            while (!pack()) {
                block();
            }
        }

    }


//    /**
//     * {@inheritDoc }
//     */
//    public void render() {
//
//        final List<MapLayer> layers = context.layers();
//
//        final Rectangle rect = renderingContext.getCanvasDisplayBounds();
//        final int width = rect.width;
//        final int height = rect.height;
//
//        //first pass to start threads for dynamic layers
//        for (final MapLayer layer : layers) {
//
//            //we ignore invisible layers
//            if (!layer.isVisible()) {
//                continue;
//            }
//
//            if (layer instanceof DynamicMapLayer) {
//                final StatelessDynamicLayerJ2D gra = (StatelessDynamicLayerJ2D) layerGraphics.get(layer);
//                final int zOrder = layers.indexOf(layer);
//                buffers.put(zOrder, null);
//
//                //start a separate thread that handle the distant serveur
//                new Thread() {
//
//                    @Override
//                    public void run() {
//                        BufferedImage img = null;
//                        Object obj = null;
//                        try {
//                            obj = gra.query(renderingContext);
//                        } catch (PortrayalException ex) {
//                            img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
//                            Logging.getLogger(MultiThreadedRendering.class).log(Level.WARNING, null, ex);
//                        }
//
//                        if(obj instanceof BufferedImage){
//                            img = (BufferedImage) obj;
//                        }else if(obj instanceof Image){
//                            final BufferedImage buffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
//                            final Graphics2D g2 = buffer.createGraphics();
//                            g2.drawImage((Image)obj, 0,0,null);
//                            g2.dispose();
//                            img = buffer;
//                        }else if(obj instanceof RenderedImage){
//                            final BufferedImage buffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
//                            final Graphics2D g2 = buffer.createGraphics();
//                            g2.drawRenderedImage( (RenderedImage)obj, new AffineTransform());
//                            g2.dispose();
//                            img = buffer;
//                        }else if(obj instanceof RenderableImage){
//                            final BufferedImage buffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
//                            final Graphics2D g2 = buffer.createGraphics();
//                            g2.drawRenderableImage( (RenderableImage)obj, new AffineTransform());
//                            g2.dispose();
//                            img = buffer;
//                        }else if(obj instanceof URL){
//                            BufferedImage buffer = null;
//                            try {
//                                buffer = ImageIO.read((URL) obj);
//                            } catch (IOException ex) {
//                                Logging.getLogger(MultiThreadedRendering.class).log(Level.WARNING, null, ex);
//                            }
//                            img = buffer;
//                        }else{
//                            final BufferedImage buffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
//                            //TODO DRAW a message to show that we couldn't paint this layer
//                            img = buffer;
//                        }
//
//                        synchronized(buffers){
//                            buffers.put(zOrder, img);
//                        }
//
//                        //we wake the dispatch thread that may be waiting for it
//                        wake();
//                    }
//                }.start();
//            }
//        }
//
//        //flag used to know if the previous layer is done
//        boolean previousIsNotReady = false;
//        RenderingContext2D validContext = null;
//
//        for (final MapLayer layer : layers) {
//
//            //we ignore invisible layers
//            if (!layer.isVisible()) {
//                continue;
//            }
//
//            if (layer instanceof DynamicMapLayer) {
//                // A dynamic layer
//                final int zOrder = layers.indexOf(layer);
//
//                //see if the distant layer has finished rendering
//                final Image layerImage = buffers.get(new Integer(zOrder));
//                if (layerImage != null) {
//                    //the distant layer has finished rendering
//                    //we can directly paint the result image
//
//                    if (previousIsNotReady) {
//                        //if the previous laye was distant and not ready we have to create a temp buffer.
//                        final BufferedImage buffer = buildBuffer();
//                        //append the buffer in the buffers set
//                        buffers.put(layers.indexOf(layer), buffer);
//
//                        if(validContext != null && validContext != renderingContext){
//                            //the context is not the main rendering context, we dispose it
////                            validContext.gdisposeGraphics();
////                            validContext.setGraphics(null, null);
//                        }
//
//                        //create a new rendering context using the bufferedimage
//                        validContext = renderingContext.create(buffer.createGraphics());
//                    } else {
//                        //we use the last valid rendering context or the default one.
//                        if (validContext == null) {
//                            validContext = renderingContext;
//                        }
//                    }
//
//                    validContext.switchToDisplayCRS();
//                    validContext.getGraphics().drawImage(layerImage, 0, 0, null);
//
//                    previousIsNotReady = false;
//                } else {
//                    //Distant layer is not ready, we will have to wait
//                    previousIsNotReady = true;
//                }
//
//            } else {
//                // A normal feature layer
//                final GraphicJ2D gra = layerGraphics.get(layer);
//
//                if (previousIsNotReady) {
//                    //if the previous laye was distant and not ready we have to create a temp buffer.
//                    final BufferedImage buffer = buildBuffer();
//                    //append the buffer in the buffers set
//                    buffers.put(layers.indexOf(layer), buffer);
//
//                    if(validContext != null && validContext != renderingContext){
//                        //the context is not the main rendering context, we dispose it
////                        validContext.disposeGraphics();
////                        validContext.setGraphics(null, null);
//                    }
//
//                    //create a new rendering context using the bufferedimage
//                    validContext = renderingContext.create(buffer.createGraphics());
//                } else {
//                    //we use the last valid rendering context or the default one.
//                    if (validContext == null) {
//                        validContext = renderingContext;
//                    }
//                }
//
//                gra.paint(validContext);
//                previousIsNotReady = false;
//            }
//
//        }
//
//        //we now wait for every rendering to finish
//        synchronized(this){
//            while (!pack()) {
//                block();
//            }
//        }
//
//    }

}
