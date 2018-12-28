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
package org.geotoolkit.display2d;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.EventListener;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import javax.media.jai.RasterFactory;
import javax.swing.event.EventListenerList;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.factory.Hints;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * On the fly calculated image. multi-threaded.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ProgressiveImage implements RenderedImage{

//    private static class TileSet{
//        private final Map<String,Raster> tiles = new ConcurrentHashMap<String, Raster>();
//    }


    /** store pregenerated tiles */
    private final Map<String,Raster> tiles = new ConcurrentHashMap<>();
//    private final ArrayBlockingQueue<TileSet> tiles;
    private final ColorModel colorModel;
    private final SampleModel sampleModel;
    private final Dimension gridSize;
    private final Dimension tileSize;
    private final double scale;
    private final Point2D upperleft;
    private final int nbtileonwidth;
    private final int nbtileonheight;
    /**
     * hold information of the last generated image batch.
     * if true, the images do not contain any datas.
     */
    private boolean batchIsEmpty = false;

    private final CanvasDef cdef;
    private final SceneDef sdef;
    private final ViewDef vdef;

    private J2DCanvasBuffered canvas;

    /** listener support */
    private final EventListenerList listeners = new EventListenerList();

    /** painter threads */
//    private final ExecutorService executor;

    /**
     *
     * @param canvasDef : canvas size will be ignored
     * @param sceneDef
     * @param viewDef
     * @param gridSize
     * @param tileSize
     */
    public ProgressiveImage(final CanvasDef canvasDef, final SceneDef sceneDef, final ViewDef viewDef,
            final Dimension gridSize, final Dimension tileSize, final double scale, int nbPainter) throws PortrayalException{
        this.gridSize = gridSize;
        this.tileSize = tileSize;
        this.scale = scale;

        ColorModel cm = ColorModel.getRGBdefault();
        if (sceneDef.getHints() != null) {
            cm = (ColorModel) sceneDef.getHints().get(GO2Hints.KEY_COLOR_MODEL);
        }
        if (cm == null) cm = ColorModel.getRGBdefault();
        this.colorModel = cm;
        this.sampleModel = colorModel.createCompatibleSampleModel(1, 1);


        final Envelope envelope = viewDef.getEnvelope();
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
        this.upperleft = new Point2D.Double(
                envelope.getMinimum(0),
                envelope.getMaximum(1));

        //prepare a J2DCanvas to render several tiles in the same tile
        //we consider a 2000*2000 size to be the maximum, which is 16Mb in memory
        //we expect the user to access tile lines by lines.
        final int maxNbTile = (2000*2000) / (tileSize.width*tileSize.height);

        if(maxNbTile < gridSize.width){
            //we can not generate a full line
            nbtileonwidth = maxNbTile;
            nbtileonheight = 1;
        }else{
            //we can generate more than one line
            nbtileonwidth = gridSize.width;
            nbtileonheight = maxNbTile / gridSize.width;
        }

        this.cdef = canvasDef;
        this.sdef = sceneDef;
        this.vdef = viewDef;

//        executor = Executors.newFixedThreadPool(nbPainter, new ThreadFactory() {
//            private volatile int inc = 0;
//            @Override
//            public Thread newThread(Runnable r) {
//                final Thread t = new Thread(r);
//                t.setName("TilePainter " + inc++);
//                return t;
//            }
//        });
//
//        tiles = new ArrayBlockingQueue<TileSet>(nbPainter);
//
//        for(int y=0;y<gridSize.height;y+=nbtileonheight){
//            for(int x=0;x<gridSize.width;x+=nbtileonwidth){
//                executor.execute(new TileGenerator(new Point(x, y)));
//            }
//        }
    }

    /**
     * Tiles are generated on the fly, so we have informations on their generation
     * process but we don't have the tiles themselves.
     *
     * @return empty vector
     */
    @Override
    public Vector<RenderedImage> getSources() {
        return new Vector<RenderedImage>();
    }

    /**
     * A PortrayalRenderedImage does not have any properties
     *
     * @param name
     * @return always Image.UndefinedProperty
     */
    @Override
    public Object getProperty(String name) {
        return Image.UndefinedProperty;
    }

    /**
     * A PortrayalRenderedImage does not have any properties
     *
     * @return always null
     */
    @Override
    public String[] getPropertyNames() {
        return null;
    }

    /**
     * Fallback on the mosaic definition.
     *
     * @return mosaic grid size width * mosaic tile size width.
     */
    @Override
    public int getWidth() {
        return gridSize.width * tileSize.height;
    }

    /**
     * Fallback on the mosaic definition.
     *
     * @return mosaic grid size width * mosaic tile size width.
     */
    @Override
    public int getHeight() {
        return gridSize.height * tileSize.width;
    }

    /**
     * Generated tiles start at zero.
     *
     * @return 0
     */
    @Override
    public int getMinX() {
        return 0;
    }

    /**
     * Generated tiles start at zero.
     *
     * @return 0
     */
    @Override
    public int getMinY() {
        return 0;
    }

    /**
     * Fallback on the mosaic definition.
     *
     * @return mosaic grid size width.
     */
    @Override
    public int getNumXTiles() {
        return gridSize.width;
    }

    /**
     * Fallback on the mosaic definition.
     *
     * @return mosaic grid size height.
     */
    @Override
    public int getNumYTiles() {
        return gridSize.height;
    }

    /**
     * Generated tiles start at zero.
     *
     * @return 0
     */
    @Override
    public int getMinTileX() {
        return 0;
    }

    /**
     * Generated tiles start at zero.
     *
     * @return 0
     */
    @Override
    public int getMinTileY() {
        return 0;
    }

    /**
     * Fallback on the mosaic definition.
     *
     * @return mosaic tile size width.
     */
    @Override
    public int getTileWidth() {
        return tileSize.width;
    }

    /**
     * Fallback on the mosaic definition.
     *
     * @return mosaic tile size height.
     */
    @Override
    public int getTileHeight() {
        return tileSize.height;
    }

    /**
     * Generated tiles start at zero.
     *
     * @return 0
     */
    @Override
    public int getTileGridXOffset() {
        return 0;
    }

    /**
     * Generated tiles start at zero.
     *
     * @return 0
     */
    @Override
    public int getTileGridYOffset() {
        return 0;
    }

    /**
     * Returns the image's bounds as a <code>Rectangle</code>.
     *
     * <p> The image's bounds are defined by the values returned by
     * <code>getMinX()</code>, <code>getMinY()</code>,
     * <code>getWidth()</code>, and <code>getHeight()</code>.
     * A <code>Rectangle</code> is created based on these four methods.
     *
     * @return Rectangle
     */
    public Rectangle getBounds() {
    return new Rectangle(getMinX(), getMinY(), getWidth(), getHeight());
    }

    @Override
    public ColorModel getColorModel() {
        return colorModel;
    }

    @Override
    public SampleModel getSampleModel() {
        return sampleModel;
    }

    public boolean isBatchEmpty() {
        return batchIsEmpty;
    }

    @Override
    public Raster getTile(int col, int row) {
        final Raster raster = tiles.remove(getTileIndex(col, row));
        if(raster!=null) return raster;
        tiles.clear();
        renderTiles(col, row);
        return tiles.remove(getTileIndex(col, row));
    }



//    @Override
//    public Raster getTile(int col, int row) {
//        final String index = getTileIndex(col, row);
//
//        Raster raster = null;
//        do{
//            final TileSet[] generates = tiles.toArray(new TileSet[0]);
//            for(TileSet ts : generates){
//                raster = ts.tiles.remove(index);
//                if(raster != null){
//                    if(ts.tiles.isEmpty()){
//                        tiles.remove(ts);
//                    }
//                    break;
//                }
//            }
//
//            if(raster == null){
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException ex) {
//                    Logging.getLogger("org.geotoolkit.display2d.process.pyramid").log(Level.SEVERE, null, ex);
//                }
//            }
//
//        }while(raster == null);
//
//        return raster;
//    }

    @Override
    public Raster getData() {
        return getData(null);
    }

    @Override
    public WritableRaster getData(Rectangle region) {
        return copyData(region, null);
    }

    @Override
    public WritableRaster copyData(WritableRaster raster) {
        final Rectangle bounds = (raster!=null) ? raster.getBounds() : null;
        return copyData(bounds, raster);
    }

    public WritableRaster copyData(Rectangle region, WritableRaster dstRaster) {
        final Rectangle bounds = getBounds();   // image's bounds

        if (region == null) {
            region = bounds;
        } else if (!region.intersects(bounds)) {
            throw new IllegalArgumentException("Rectangle does not intersect datas.");
        }

        // Get the intersection of the region and the image bounds.
        final Rectangle xsect = (region == bounds) ? region : region.intersection(bounds);

        //create a raster of this size
        if(dstRaster == null){
            SampleModel sampleModel = getSampleModel();
            sampleModel = sampleModel.createCompatibleSampleModel(xsect.width, xsect.height);
            dstRaster = RasterFactory.createWritableRaster(sampleModel, new Point(0, 0));
        }

        //calculate the first and last tiles index we will need
        final int startTileX = xsect.x / getTileWidth();
        final int startTileY = xsect.y / getTileHeight();
        final int endTileX = (xsect.x+xsect.width) / getTileWidth();
        final int endTileY = (xsect.y+xsect.height) / getTileHeight();

        //loop on each tile
        for (int j = startTileY; j <= endTileY; j++) {
            for (int i = startTileX; i <= endTileX; i++) {
                final Raster tile = getTile(i, j);
                dstRaster.setRect(
                        i*getTileWidth(),
                        j*getTileHeight(),
                        tile);
            }
        }

        return dstRaster;
    }

    /**
     * @return unique index for this tile coordinate
     */
    private String getTileIndex(int col, int row){
        return row+" "+ col;
    }

    protected void fireTileCreated(int x, int y){
        for(ProgressListener l : listeners.getListeners(ProgressListener.class)){
            l.tileCreated(x, y);
        }
    }

    public void addProgressListener(ProgressListener listener){
        listeners.add(ProgressListener.class, listener);
    }

    public void removeProgressListener(ProgressListener listener){
        listeners.remove(ProgressListener.class, listener);
    }

//    @Override
//    protected void finalize() throws Throwable {
//        if(!executor.isShutdown()){
//            executor.shutdownNow();
//        }
//        super.finalize();
//    }

    public static interface ProgressListener extends EventListener{

        void tileCreated(int x, int y);

    }

    private synchronized void renderTiles(int col, int row) {

        if (canvas == null) {
            final Dimension canvasSize = new Dimension(
                    nbtileonwidth*tileSize.width,
                    nbtileonheight*tileSize.height);

            final Hints hints = new Hints();
            hints.put(GO2Hints.KEY_COLOR_MODEL, colorModel);
            canvas = new J2DCanvasBuffered(vdef.getEnvelope().getCoordinateReferenceSystem(), canvasSize, hints);
        }

        try {
            DefaultPortrayalService.prepareCanvas(canvas, cdef, sdef, vdef);
        } catch (PortrayalException ex) {
            ex.printStackTrace();
        }

        final double tilespanX = scale * tileSize.width;
        final double tilespanY = scale * tileSize.height;

        final GeneralEnvelope canvasEnv = new GeneralEnvelope(canvas.getObjectiveCRS());
        canvasEnv.setRange(0,
                upperleft.getX() + (col) * tilespanX,
                upperleft.getX() + (col + nbtileonwidth) * tilespanX);
        canvasEnv.setRange(1,
                upperleft.getY() - (row + nbtileonheight) * tilespanY,
                upperleft.getY() - (row) * tilespanY);

        try {
            canvas.setVisibleArea(canvasEnv);
        } catch (NoninvertibleTransformException | TransformException ex) {
            Logging.getLogger("org.geotoolkit.display2d.process.pyramid").log(Level.SEVERE, null, ex);
        }

        //cut the canvas buffer in pieces
        batchIsEmpty = !canvas.repaint();

        final BufferedImage canvasBuffer = canvas.getSnapShot();
        for(int x=0; x<nbtileonwidth && col+x<gridSize.width; x++){
            for(int y=0; y<nbtileonheight && row+y<gridSize.height; y++){
                final String idx = getTileIndex(col+x, row+y);
                final BufferedImage tile = canvasBuffer.getSubimage(
                        x*tileSize.width,
                        y*tileSize.height,
                        tileSize.width,
                        tileSize.height);
                tiles.put(idx, tile.getRaster());
                fireTileCreated(col+x,row+y);
            }
        }
    }

//    private class TileGenerator implements Runnable{
//
//        private final Point topleft;
//
//        public TileGenerator(final Point start) {
//            topleft = start;
//
//        }
//
//        @Override
//        public void run() {
//
//            final TileSet ts = new TileSet();
//            try {
//                tiles.put(ts);
//            } catch (InterruptedException ex) {
//                Logging.getLogger("org.geotoolkit.display2d.process.pyramid").log(Level.SEVERE, null, ex);
//            }
//
//            final Dimension canvasSize = new Dimension(
//                nbtileonwidth*tileSize.width,
//                nbtileonheight*tileSize.height);
//
//            final J2DCanvasBuffered canvas = new J2DCanvasBuffered(vdef.getEnvelope().getCoordinateReferenceSystem(), canvasSize);
//            canvas.setRenderingHint(GO2Hints.KEY_COLOR_MODEL, colorModel);
//            try {
//                DefaultPortrayalService.prepareCanvas(canvas, cdef, sdef, vdef);
//            } catch (PortrayalException ex) {
//                ex.printStackTrace();
//            }
//
//
//            final int col = topleft.x;
//            final int row = topleft.y;
//
//            final double tilespanX = scale*tileSize.width;
//            final double tilespanY = scale*tileSize.height;
//
//            final GeneralEnvelope canvasEnv = new GeneralEnvelope(canvas.getObjectiveCRS());
//            canvasEnv.setRange(0,
//                    upperleft.getX() + (col)*tilespanX,
//                    upperleft.getX() + (col+nbtileonwidth)*tilespanX
//                    );
//            canvasEnv.setRange(1,
//                    upperleft.getY() - (row+nbtileonheight)*tilespanY,
//                    upperleft.getY() - (row)*tilespanY
//                    );
//
//            try {
//                canvas.getController().setVisibleArea(canvasEnv);
//            } catch (NoninvertibleTransformException ex) {
//                Logging.getLogger("org.geotoolkit.display2d.process.pyramid").log(Level.SEVERE, null, ex);
//            } catch (TransformException ex) {
//                Logging.getLogger("org.geotoolkit.display2d.process.pyramid").log(Level.SEVERE, null, ex);
//            }
//
//            //cut the canvas buffer in pieces
//            canvas.repaint();
//
//
//
//            final BufferedImage canvasBuffer = canvas.getSnapShot();
//            for(int x=0; x<nbtileonwidth && col+x<gridSize.width; x++){
//                for(int y=0; y<nbtileonheight && row+y<gridSize.height; y++){
//                    final String idx = getTileIndex(col+x, row+y);
//                    final BufferedImage tile = canvasBuffer.getSubimage(
//                            x*tileSize.width,
//                            y*tileSize.height,
//                            tileSize.width,
//                            tileSize.height);
//                    ts.tiles.put(idx, tile.getRaster());
//                    fireTileCreated(col+x,row+y);
//                }
//            }
//
//        }
//
//    }

}
