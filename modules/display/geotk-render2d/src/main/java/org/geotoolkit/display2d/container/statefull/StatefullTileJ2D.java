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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Point3d;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Pyramids;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.storage.coverage.ImageTile;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.style.RasterSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class StatefullTileJ2D extends StatefullMapItemJ2D<MapItem> {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.display2d.container.statefull");

    private final Mosaic mosaic;
    private final Point3d coordinate;
    private final CachedRule[] rules;

    protected volatile GridCoverage2D buffer = null;
    protected AffineTransform2D trs = null;
    protected RenderedImage img = null;
    private volatile Updater updater = null;
    private final AtomicBoolean needUpdate = new AtomicBoolean();
    private Envelope env2d;
    private volatile boolean obsoleted = false;
    private boolean loaded = false;

    public StatefullTileJ2D(Mosaic mosaic, Point3d coordinate, J2DCanvas canvas,
            MapLayer item, CachedRule[] rules) {
        super(canvas, item, false);
        this.mosaic = mosaic;
        this.coordinate = coordinate;
        this.rules = rules;
    }

    public Mosaic getMosaic() {
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
    public boolean paint(RenderingContext2D renderingContext) {

        GridCoverage2D coverage = this.buffer;
        if(coverage == null){
            final Envelope env2d = renderingContext.getCanvasObjectiveBounds2D();
            updateRequest(env2d);
            return false;
        }

        //we must switch to objectiveCRS for grid coverage
        renderingContext.switchToDisplayCRS();

        final AffineTransform objToDisp = renderingContext.getObjectiveToDisplay();
        final AffineTransform comp = new AffineTransform(objToDisp);
        comp.concatenate(trs);

        final Graphics2D g = renderingContext.getGraphics();
        g.drawRenderedImage(img, comp);
        return true;
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


    protected class Updater implements Runnable{

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
                final MathTransform trs = Pyramids.getTileGridToCRS(
                        mosaic, new Point((int)coordinate.x, (int)coordinate.y));
                final ImageTile tr = (ImageTile) mosaic.getTile((int)coordinate.x, (int)coordinate.y);
                final CoordinateReferenceSystem pyramidCRS2D = CRSUtilities.getCRS2D(mosaic.getEnvelope().getCoordinateReferenceSystem());
                final GridCoverage2D coverage = (GridCoverage2D) prepareTile(env2d.getCoordinateReferenceSystem(), pyramidCRS2D, tr, trs);
                if (coverage != null) {

                    //we copy the image in a buffered image with a well knowed data typeand color model
                    //this can significantly improve performances.
                    RenderedImage ri = null;

                    for(final CachedRule rule : rules){
                        for(final CachedSymbolizer symbol : rule.symbolizers()){
                            if(symbol.getSource() instanceof RasterSymbolizer){
                                final MapLayer layer = (MapLayer) getUserObject();
                                ri = DefaultRasterSymbolizerRenderer.applyStyle(null,coverage, null, (RasterSymbolizer)symbol.getSource());
                                break;
                            }
                        }
                    }

                    if(ri == null){
                        //should not happen
                        ri = coverage.getRenderedImage();
                    }


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

    private static GridCoverage prepareTile(final CoordinateReferenceSystem objCRS2D,
            final CoordinateReferenceSystem tileCRS ,final ImageTile tile, MathTransform trs) {

        RenderedImage image;
        try {
            image = tile.getImage();
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
            return null;
        }

        final boolean needReproject = !Utilities.equalsIgnoreMetadata(tileCRS,objCRS2D);

        if (needReproject) {
            //will be reprojected, we must check that image has alpha support
            //otherwise we will have black borders after reprojection
            if (!image.getColorModel().hasAlpha()) {
                final BufferedImage buffer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                buffer.createGraphics().drawRenderedImage(image, new AffineTransform());
                image = buffer;
            }
        }

        //build the coverage
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        final GridExtent ge = new GridExtent(image.getWidth(), image.getHeight());
        final GridGeometry2D gridgeo = new GridGeometry2D(ge, PixelOrientation.UPPER_LEFT, trs, tileCRS);
        gcb.setName("tile");
        gcb.setGridGeometry(gridgeo);
        gcb.setRenderedImage(image);
        GridCoverage coverage = gcb.build();

        if (needReproject) {
            try {
                coverage = GO2Utilities.resample(coverage.forConvertedValues(false), objCRS2D);
            } catch (ProcessException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        return coverage;
    }
}
