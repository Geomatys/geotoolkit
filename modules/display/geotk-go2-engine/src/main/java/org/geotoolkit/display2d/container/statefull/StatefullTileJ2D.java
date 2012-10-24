/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.display2d.container.statefull;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.vecmath.Point3d;
import org.geotoolkit.coverage.AbstractGridMosaic;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.TileReference;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.util.logging.Logging;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class StatefullTileJ2D extends StatefullMapItemJ2D<MapItem> {

    private static final Logger LOGGER = Logging.getLogger(StatefullTileJ2D.class);
    
    private final GridMosaic mosaic;
    private final Point3d coordinate;
    
    protected volatile GridCoverage2D buffer = null;
    protected AffineTransform2D trs = null;
    protected RenderedImage img = null;
    private volatile Updater updater = null;    
    private final AtomicBoolean needUpdate = new AtomicBoolean();
    private Envelope env2d;
    private volatile boolean obsoleted = false;
    private boolean loaded = false;

    public StatefullTileJ2D(GridMosaic mosaic, Point3d coordinate, J2DCanvas canvas, StatefullMapItemJ2D parent, MapItem item) {
        super(canvas, parent, item);
        this.mosaic = mosaic;
        this.coordinate = coordinate;
    }

    public GridMosaic getMosaic() {
        return mosaic;
    }

    public Point3d getCoordinate() {
        return coordinate;
    }
    
    public boolean isLoaded(){
        return loaded;
    }

    public void setObsoleted(boolean obsoleted) {
        this.obsoleted = obsoleted;
    }
    
    @Override
    public void paint(RenderingContext2D renderingContext) {
        
        GridCoverage2D coverage = this.buffer;        
        if(coverage == null){
            final Envelope env2d = renderingContext.getCanvasObjectiveBounds2D();
            updateRequest(env2d);
            return;
        }
                
        //we must switch to objectiveCRS for grid coverage
        renderingContext.switchToDisplayCRS();
        
        final AffineTransform objToDisp = renderingContext.getObjectiveToDisplay();
        final AffineTransform comp = new AffineTransform(objToDisp);
        comp.concatenate(trs);
          
        final Graphics2D g = renderingContext.getGraphics();
        g.drawRenderedImage(img, comp);
    }
        
    private void updateRequest(Envelope env2d){
        if(obsoleted) return;
        
        boolean mustUpdate = false;
        if(this.env2d == null || !this.env2d.equals(env2d)){
            mustUpdate = true;
            loaded = false;
        }
        this.env2d = env2d;
        if(mustUpdate){
            needUpdate.set(true);
            checkUpdater();
        }
    }
        
    private synchronized void checkUpdater(){
        if(obsoleted) return;
        
        if(needUpdate.get() && updater == null){
            needUpdate.set(false);
            if(env2d != null){
                updater = new Updater(env2d);
                getExecutor().execute(updater);
            }
        }
    }
    
    
    protected final class Updater implements Runnable{
        
        private Envelope env2d;
        
        private Updater(Envelope env2d){
            this.env2d = env2d;
        }
                
        @Override
        public void run() {
            if(obsoleted || loaded){
                updater = null;
                return;
            }    
            try{
                final MathTransform trs = AbstractGridMosaic.getTileGridToCRS(
                        mosaic, new Point((int)coordinate.x, (int)coordinate.y));
                final TileReference tr = mosaic.getTile((int)coordinate.x, (int)coordinate.y, null);
                final GridCoverage2D coverage = prepareTile(env2d.getCoordinateReferenceSystem(), 
                        mosaic.getPyramid().getCoordinateReferenceSystem(), tr, trs);
                if(coverage != null){
                    
                    //we copy the image in a buffered image with a well knowed data typeand color model
                    //this can significantly improve performances.
                    final RenderedImage ri = coverage.getRenderedImage();
                    final BufferedImage img = new BufferedImage(ri.getWidth(), ri.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    img.createGraphics().drawRenderedImage(ri, new AffineTransform());
                    StatefullTileJ2D.this.img = img;
                    
                    StatefullTileJ2D.this.trs = (AffineTransform2D) coverage.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);
                    StatefullTileJ2D.this.buffer = coverage;
                }
                
                getCanvas().repaint();
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                loaded = true;
            }
            
            updater = null;
            checkUpdater();
        }
    }
    
    private static GridCoverage2D prepareTile(final CoordinateReferenceSystem objCRS2D, 
            final CoordinateReferenceSystem tileCRS ,final TileReference tile, MathTransform trs) {
        
        Object input = tile.getInput();
        RenderedImage image = null;
        if(input instanceof RenderedImage){
            image = (RenderedImage) input;
        }else{
            ImageReader reader = null;
            try {
                reader = tile.getImageReader();
                if(reader == null) return null;
                image = reader.read(tile.getImageIndex());
            } catch (IOException ex) {
                LOGGER.log(Level.INFO, ex.getMessage());
                return null;
            } finally {
                //dispose reader and substream
                if(reader != null){
                    Object readerinput = reader.getInput();
                    if(readerinput instanceof InputStream){
                        try {
                            ((InputStream)readerinput).close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            return null;
                        }
                    }else if(readerinput instanceof ImageInputStream){
                        try {
                            ((ImageInputStream)readerinput).close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            return null;
                        }
                    }
                    reader.dispose();
                }
            }
        }
                
        final GridCoverageFactory gc = new GridCoverageFactory();
        GridCoverage2D coverage;
        
        //check the crs
        if(!CRS.equalsIgnoreMetadata(tileCRS,objCRS2D) ){
            
            //will be reprojected, we must check that image has alpha support
            //otherwise we will have black borders after reprojection
            if(!image.getColorModel().hasAlpha()){
                final BufferedImage buffer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                buffer.createGraphics().drawRenderedImage(image, new AffineTransform());
                image = buffer;
            }
            
            coverage = gc.create("tile", image, tileCRS, trs, null, null, null);            
            coverage = (GridCoverage2D) Operations.DEFAULT.resample(coverage.view(ViewType.NATIVE), objCRS2D);
            
        }else{
            coverage = gc.create("tile", image, tileCRS, trs, null, null, null);
        }
        
        return coverage;        
    }
}
