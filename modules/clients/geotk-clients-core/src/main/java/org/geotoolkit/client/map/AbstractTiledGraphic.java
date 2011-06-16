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

    public AbstractTiledGraphic(final J2DCanvas canvas, final CoordinateReferenceSystem crs){
        super(canvas, crs);
    }
    
    /**
     * Render each query response as a coverage with the given gridToCRS and CRS.
     */
    protected void paint(final RenderingContext2D context, 
            final Map<Entry<CoordinateReferenceSystem,MathTransform>,? extends Request>  queries) {
        final CanvasMonitor monitor = context.getMonitor();
                
        final Collection<Thread> threads = new ArrayList<Thread>();
        
        for(final Entry<Entry<CoordinateReferenceSystem,MathTransform>,? extends Request> entry : queries.entrySet()){
            
            //check if rendering stopped requested
            if(monitor.stopRequested()){
                return;
            }
            
            final Thread thread = new Thread(){
                @Override
                public void run() {
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

                    String urlpath;
                    try {
                        urlpath = request.getURL().toString();
                        System.out.println(urlpath);
                    } catch (MalformedURLException ex) {
                        urlpath = "Malformed URL";
                    }

                    if (image == null) {
                        String path;
                        try {
                            path = request.getURL().toString();
                        } catch (MalformedURLException ex) {
                            path = "Malformed URL";
                        }
                        monitor.exceptionOccured(new PortrayalException("Server did not return an image for URL : \n" + path), Level.WARNING);
                        return;
                    }

                    final GridCoverageFactory gc = new GridCoverageFactory();

                    final GridCoverage2D coverage = gc.create("tile", image,
                        tileCRS, gridToCRS, null, null, null);
                    System.out.println(coverage.getEnvelope2D());
                    paint(context, coverage);
                }
            };
            threads.add(thread);
            thread.start();
        }
        
        //wait for all thread to end
        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(AbstractTiledGraphic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private synchronized void paint(final RenderingContext2D context, final GridCoverage2D coverage){
        final CanvasMonitor monitor = context.getMonitor();
        try {
            GO2Utilities.portray(context, coverage);
        } catch (PortrayalException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }
    }
        
}
