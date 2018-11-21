/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.VolatileImage;
import java.util.logging.Level;
import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.internal.Threads;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Canvas based on a VolatileImage.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class J2DCanvasVolatile extends J2DCanvas{

    private final GraphicsConfiguration GC;
    private final DrawTask drawtask = new DrawTask();
    private VolatileImage buffer0;
    private Dimension dim;
    private boolean mustupdate = false;
    private Envelope wishedEnvelope = null;

    private final Object LOCK = new Object();

    private final Area dirtyArea = new Area();

    public J2DCanvasVolatile(final CoordinateReferenceSystem crs, final Dimension dim){
        this(crs,dim,null);
    }

    public J2DCanvasVolatile(final CoordinateReferenceSystem crs, final Dimension dim, final Hints hints){
        super(crs,hints);

        //we might not know our dimension until it is painted by a swing component for exemple.
        if(dim != null){
            setDisplayBounds(new Rectangle(dim));
        }
        this.dim = dim;

        GC = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        painter = new SolidColorPainter(Color.WHITE);
    }

    @Override
    public void dispose(){
        super.dispose();
        buffer0 = null;
        dim = null;
    }

    /**
     * Resize the volatile image, this will set to null the buffer.
     * A new one will be created when repaint is called.
     */
    public synchronized void resize(Dimension dim){
        if(this.dim == null){
            //first time we affect the size
            this.dim = dim;
            setDisplayBounds(new Rectangle(dim));
            if(wishedEnvelope!=null){
                try {
                    setVisibleArea(wishedEnvelope);
                } catch (NoninvertibleTransformException ex) {
                    getLogger().log(Level.SEVERE, null, ex);
                } catch (TransformException ex) {
                    getLogger().log(Level.SEVERE, null, ex);
                }
                wishedEnvelope = null;
                return;
            }
        }else if(this.dim.equals(dim)){
            //same size, nothing to do
            return;
        }

        //ensure dimension is valid
        if(dim.width<=0 || dim.height<=0){
            dim = new Dimension(dim);
            if(dim.width<=0)dim.width=1;
            if(dim.height<=0)dim.height=1;
        }

        this.dim = dim;
        setDisplayBounds(new Rectangle(dim));
        buffer0 = null;

        if(isAutoRepaint()){
            repaint();
        }

    }

    private synchronized VolatileImage createBackBuffer() {
        if(dim == null){
            return null;
        }
        return GC.createCompatibleVolatileImage(dim.width, dim.height, VolatileImage.OPAQUE);
    }

    private void render(Shape paintingDisplayShape){

        if(paintingDisplayShape == null) paintingDisplayShape = getDisplayBounds();

        final Graphics2D output;

        synchronized(LOCK){
            VolatileImage buffer;

            if(buffer0 == null){
                //create the buffer at the last possible moment
                //or create a new one if we are already rendering
                //TODO : find a way to stop previous thread
                buffer = createBackBuffer();
                if(buffer == null){
                    //size may not be knowned yet
                    return;
                }

                buffer.setAccelerationPriority(1);
                output = (Graphics2D) buffer.getGraphics();
                output.setComposite(GO2Utilities.ALPHA_COMPOSITE_0F);
                output.fillRect(0,0,dim.width,dim.height);
            }else{
                buffer = buffer0;
                //we clear the buffer part if it exists
                output = (Graphics2D) buffer0.getGraphics();
                output.setComposite(GO2Utilities.ALPHA_COMPOSITE_0F);
                output.fill(paintingDisplayShape);
            }

            buffer0 = buffer;
            output.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);


    //        Rectangle clipBounds = output.getClipBounds();
    //        /*
    //         * Sets a flag for avoiding some "refresh()" events while we are actually painting.
    //         * For example some implementation of the GraphicPrimitive2D.paint(...) method may
    //         * detects changes since the last rendering and invokes some kind of invalidate(...)
    //         * methods before the graphic rendering begin. Invoking those methods may cause in some
    //         * indirect way a call to GraphicPrimitive2D.refresh(), which will trig an other widget
    //         * repaint. This second repaint is usually not needed, since Graphics usually managed
    //         * to update their informations before they start their rendering. Consequently,
    //         * disabling repaint events while we are painting help to reduces duplicated rendering.
    //         */
    //        final Rectangle displayBounds = getDisplayBounds().getBounds();
    //        Rectangle2D dirtyArea = XRectangle2D.INFINITY;
    //        if (clipBounds == null) {
    //            clipBounds = displayBounds;
    //        } else if (displayBounds.contains(clipBounds)) {
    //            dirtyArea = clipBounds;
    //        }
    //        output.setClip(clipBounds);
    //        paintStarted(dirtyArea);
            output.setClip(paintingDisplayShape);

            //must be called outside of the lock or it may provoque a deadlock
            prepareContext(context2D, output, paintingDisplayShape);

            //paint background if there is one.
            if(painter != null){
                painter.paint(context2D);
            }
        }

        monitor.renderingStarted();
        fireRenderingStateChanged(RENDERING);

        try{
            final GraphicContainer container = getContainer();
            if(container != null){
                render(context2D, container.flatten(true));
            }
        }catch(Exception ex){
            //volatile canvas must never lock itself.
            monitor.exceptionOccured(ex, Level.WARNING);
        }

        //End painting
        output.dispose();
        fireRenderingStateChanged(ON_HOLD);
        monitor.renderingFinished();
    }

    @Override
    public Image getSnapShot(){
        if(buffer0 != null){
            return buffer0.getSnapshot();
        }
        return null;
    }

    public VolatileImage getVolatile(){
        synchronized(LOCK){
            return buffer0;
        }
    }

    @Override
    public synchronized boolean repaint(final Shape displayArea) {
        //finish any previous painting
        getMonitor().stopRendering();

        this.dirtyArea.add(new Area(displayArea));
        mustupdate = true;
        Threads.executeWork(drawtask);
        return true;
    }

    private class DrawTask implements Runnable {

        @Override
        public synchronized void run() {

            if (!mustupdate) {
                return;
            }
            mustupdate = false;

            final Shape copy;
            synchronized (dirtyArea) {
                copy = (dirtyArea.isEmpty()) ? null : new Area(dirtyArea);
                dirtyArea.reset();
            }

            render(copy);
        }
    }

    @Override
    public void setVisibleArea(final Envelope env) throws NoninvertibleTransformException, TransformException {
        if(dim == null){
            //we don't know our size yet, store the information for later
            wishedEnvelope = env;
        }else{
            super.setVisibleArea(env);
        }
    }

}
