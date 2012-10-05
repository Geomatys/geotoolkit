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

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
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
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class StatefullTileJ2D extends StatefullMapItemJ2D<MapItem> {

    private final GridMosaic mosaic;
    final Coordinate coordinate;
    
    protected volatile GridCoverage2D buffer = null;
    private volatile Updater updater = null;

    public StatefullTileJ2D(GridMosaic mosaic, Coordinate coordinate, J2DCanvas canvas, StatefullMapItemJ2D parent, MapItem item) {
        super(canvas, parent, item);
        this.mosaic = mosaic;
        this.coordinate = coordinate;
    }

    @Override
    public void paint(RenderingContext2D renderingContext) {
        super.paint(renderingContext);
        
        
        
        GridCoverage2D coverage = this.buffer;        
        if(coverage == null){
            final Envelope env = renderingContext.getCanvasObjectiveBounds();
            final Envelope env2d = renderingContext.getCanvasObjectiveBounds2D();
            final Dimension rect = renderingContext.getCanvasDisplayBounds().getSize();
            updateRequest(env, env2d, rect);
            return;
        }
                
        //we must switch to objectiveCRS for grid coverage
        renderingContext.switchToObjectiveCRS();
        
        final RenderedImage img = coverage.getRenderedImage();
        final AffineTransform2D trs = (AffineTransform2D) coverage.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);
                
        final Graphics2D g = renderingContext.getGraphics();
        g.drawRenderedImage(img, trs);
        
    }

    
    private final AtomicBoolean needUpdate = new AtomicBoolean();
    private Envelope env;
    private Envelope env2d;
    private Dimension dim;
    
    private synchronized void updateRequest(Envelope env, Envelope env2d, Dimension dim){
        boolean mustUpdate = false;
        if(this.env == null || !this.env.equals(env) ||
           this.env2d == null || !this.env2d.equals(env2d) ||
           this.dim == null || !this.dim.equals(dim)){
            mustUpdate = true;
        }
        this.env = env;
        this.env2d = env2d;
        this.dim = new Dimension(dim);
        if(mustUpdate){
            update();
        }
    }
    
    protected synchronized void update(){
        needUpdate.set(true);
        checkUpdater();
    }
    
    private synchronized void checkUpdater(){
        if(needUpdate.get() && updater == null){
            needUpdate.set(false);
            if(env != null){
                updater = new Updater(env, env2d, dim);
                getExecutor().execute(updater);
            }
        }
    }
    
    
    protected final class Updater implements Runnable{
        
        private Envelope env;
        private Envelope env2d;
        private Dimension dim;
        
        private Updater(Envelope env, Envelope env2d, Dimension dim){
            this.env = env;
            this.env2d = env2d;
            this.dim = dim;
        }
                
        @Override
        public void run() {            
            try{
                final MathTransform trs = AbstractGridMosaic.getTileGridToCRS(
                        mosaic, new Point((int)coordinate.x, (int)coordinate.y));
                final TileReference tr = mosaic.getTile((int)coordinate.x, (int)coordinate.y, null);
                buffer = prepareTile(env2d.getCoordinateReferenceSystem(), 
                        mosaic.getPyramid().getCoordinateReferenceSystem(), tr, trs);
            }catch(Exception ex){
                ex.printStackTrace();
            }    
            getCanvas().repaint();
            
            updater = null;
            checkUpdater();
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StatefullTileJ2D){
            StatefullTileJ2D t = (StatefullTileJ2D) obj;
            return t.coordinate.equals3D(coordinate);
        }else if(obj instanceof Coordinate){
            Coordinate t = (Coordinate) obj;
            return t.equals3D(coordinate);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return coordinate.hashCode();
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
                image = reader.read(tile.getImageIndex());
            } catch (IOException ex) {
                ex.printStackTrace();
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
