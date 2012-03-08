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

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.collection.Cache;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractTiledGraphic extends AbstractGraphicJ2D{
        
    /**
     * Cache the last queried tiles
     */
    private final Map<String,GridCoverage2D> tileCache = new Cache<String, GridCoverage2D>(4, 10, false);
    
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
            final Collection<QueryTileReference>  queries) {
        final CanvasMonitor monitor = context.getMonitor();
        
        //bypass all if no queries
        if(queries.isEmpty()){
            return;
        }
        
        //bypass thread creation when only a single tile
        if(queries.size() == 1){
            paint(context, queries.iterator().next());            
            return;
        }
        
        final ExecutorService executor = Executors.newFixedThreadPool(6);
        
        for(final QueryTileReference entry : queries){            
            if(monitor.stopRequested()){
                return;
            }
            
            final Runnable call = new Runnable() {
                @Override
                public void run() {
                    paint(context, entry);
                }
            };
            
            executor.execute(call);            
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(2, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            Logger.getLogger(AbstractTiledGraphic.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void paint(final RenderingContext2D context, 
            final QueryTileReference tileRef){        
        final CanvasMonitor monitor = context.getMonitor();
        final CoordinateReferenceSystem objCRS2D = context.getObjectiveCRS2D();
                
        if(monitor.stopRequested()){
            return;
        }
        
        //use the cache if available
        GridCoverage2D coverage = tileCache.get(tileRef.id);
        if(coverage != null && objCRS2D == coverage.getCoordinateReferenceSystem2D()){
            //cache is still valid
            try {
                GO2Utilities.portray(context, coverage);
            } catch (PortrayalException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
            }
            return;
        }
        
        BufferedImage image;
        InputStream is = null;
        try {
            is = tileRef.query.getResponseStream();
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
                    path = tileRef.query.getURL().toString();
                } catch (MalformedURLException ex) {
                    path = "Malformed URL";
                }
                monitor.exceptionOccured(new PortrayalException("Server did not return an image for URL : \n" + path), Level.WARNING);
            }
            return;
        }

        final GridCoverageFactory gc = new GridCoverageFactory();
        
        //check the crs
        final CoordinateReferenceSystem candidate2D = tileRef.crs;
        if(!CRS.equalsIgnoreMetadata(candidate2D,objCRS2D) ){
            
            //will be reprojected, we must check that image has alpha support
            //otherwise we will have black borders after reprojection
            if(!image.getColorModel().hasAlpha()){
                final BufferedImage buffer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                buffer.createGraphics().drawRenderedImage(image, new AffineTransform());
                image = buffer;
            }
            
            coverage = gc.create("tile", image,
            tileRef.crs, tileRef.gridToCRS, null, null, null);            
            coverage = (GridCoverage2D) Operations.DEFAULT.resample(coverage.view(ViewType.NATIVE), objCRS2D);
            
        }else{
            coverage = gc.create("tile", image,
            tileRef.crs, tileRef.gridToCRS, null, null, null);
        }
        
        tileCache.put(tileRef.id, coverage); //cache the tile
        try {
            GO2Utilities.portray(context, coverage);
        } catch (PortrayalException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }
    }
         
}
