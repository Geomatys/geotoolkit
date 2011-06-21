/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.client.map;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import org.geotoolkit.client.Request;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractTiledGraphic extends AbstractGraphicJ2D{

    private boolean silentErrors = false;
    
    public AbstractTiledGraphic(final J2DCanvas canvas, final CoordinateReferenceSystem crs){
        super(canvas, crs);
    }

    public void setSilentErrors(boolean silentErrors) {
        this.silentErrors = silentErrors;
    }

    public boolean isSilentErrors() {
        return silentErrors;
    }
    
    /**
     * Render each query response as a coverage with the given gridToCRS and CRS.
     */
    protected void paint(final RenderingContext2D context, 
            final Map<Entry<CoordinateReferenceSystem,MathTransform>,? extends Request>  queries) {
        final CanvasMonitor monitor = context.getMonitor();
        
        //bypass all if no queries
        if(queries.isEmpty()){
            return;
        }
        
        //bypass thread creation when only a single tile
        if(queries.size() == 1){
            paint(context, queries.entrySet().iterator().next());            
            return;
        }
                  
        final ThreadPoolExecutor exec = new ThreadPoolExecutor(0, 8, 100, TimeUnit.MILLISECONDS, 
                new ArrayBlockingQueue<Runnable>(queries.size()));
            
        
        final Collection<FutureTask> tasks = new ArrayList<FutureTask>();
        
        for(final Entry<Entry<CoordinateReferenceSystem,MathTransform>,? extends Request> entry : queries.entrySet()){
                        
            if(monitor.stopRequested()){
                return;
            }
            
            final FutureTask task = new FutureTask(new Callable() {
                @Override
                public Object call() throws Exception {
                    paint(context, entry);
                    return null;
                }
            });
            
            tasks.add(task);
            exec.execute(task);            
        }
        
        //wait for all thread to end
        for(FutureTask t : tasks){
            if(monitor.stopRequested()){
                return;
            }
            
            try {
                t.get(2000,TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Logger.getLogger(AbstractTiledGraphic.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(AbstractTiledGraphic.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TimeoutException ex) {
                Logger.getLogger(AbstractTiledGraphic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private void paint(final RenderingContext2D context, 
            final Entry<Entry<CoordinateReferenceSystem,MathTransform>,? extends Request> entry){
        final CanvasMonitor monitor = context.getMonitor();
        
        final CoordinateReferenceSystem tileCRS = entry.getKey().getKey();
        final MathTransform gridToCRS = entry.getKey().getValue();
        final Request request = entry.getValue();

        final BufferedImage image;
        InputStream is = null;
        try {
            is = request.getResponseStream();
            image = ImageIO.read(is);
        } catch (IOException io) {
            monitor.exceptionOccured(io, Level.WARNING);
            return;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    monitor.exceptionOccured(ex, Level.WARNING);
                }
            }
        }

        if (image == null) {
            if(!silentErrors){
                String path;
                try {
                    path = request.getURL().toString();
                } catch (MalformedURLException ex) {
                    path = "Malformed URL";
                }
                monitor.exceptionOccured(new PortrayalException("Server did not return an image for URL : \n" + path), Level.WARNING);
            }
            return;
        }

        final GridCoverageFactory gc = new GridCoverageFactory();

        final GridCoverage2D coverage = gc.create("tile", image,
            tileCRS, gridToCRS, null, null, null);
        try {
            GO2Utilities.portray(context, coverage);
        } catch (PortrayalException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }
    }
         
}
