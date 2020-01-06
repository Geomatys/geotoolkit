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
import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.EventListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.swing.event.EventListenerList;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PlanarImage;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
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
final class ProgressiveImage extends PlanarImage {

    /** store pregenerated tiles */
    private final Map<String,Raster> tiles = new ConcurrentHashMap<>();
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

    private J2DCanvasBuffered canvas;

    /** listener support */
    private final EventListenerList listeners = new EventListenerList();

    /**
     *
     * @param canvasDef : canvas size will be ignored
     */
    public ProgressiveImage(final CanvasDef canvasDef, final SceneDef sceneDef,
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


        final Envelope envelope = canvasDef.getEnvelope();
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
        this.upperleft = new Point2D.Double(
                envelope.getMinimum(0),
                envelope.getMaximum(1));

        //prepare a J2DCanvas to render several tiles in the same tile
        int maxNbTile = (1024*1024) / (tileSize.width*tileSize.height);
        maxNbTile = Math.max(1, maxNbTile);

        if (maxNbTile < gridSize.width) {
            //we can not generate a full line
            nbtileonwidth = maxNbTile;
            nbtileonheight = 1;
        } else {
            //we could generate more than one line
            //but this class is used in MapContextTileGenerator which makes a Stream foreach line
            nbtileonwidth = gridSize.width;
            nbtileonheight = 1;
        }

        this.cdef = canvasDef;
        this.sdef = sceneDef;
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

    private synchronized void renderTiles(int col, int row) {

        /*
         * clip generated size to grid limits otherwise this causes a large canvas
         * which increases errors in resolution and envelope computations
         */
        int nbtileonwidth = this.nbtileonwidth;
        int nbtileonheight = this.nbtileonheight;
        if (col + nbtileonwidth > gridSize.width) {
            nbtileonwidth = gridSize.width - col;
        }
        if (row + nbtileonheight > gridSize.height) {
            nbtileonheight = gridSize.height - row;
        }

        if (canvas == null) {
            final Dimension canvasSize = new Dimension(
                    nbtileonwidth*tileSize.width,
                    nbtileonheight*tileSize.height);

            final Hints hints = new Hints();
            hints.put(GO2Hints.KEY_COLOR_MODEL, colorModel);
            canvas = new J2DCanvasBuffered(cdef.getEnvelope().getCoordinateReferenceSystem(), canvasSize, hints);
        }

        try {
            DefaultPortrayalService.prepareCanvas(canvas, cdef, sdef);
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

    public static interface ProgressListener extends EventListener{
        void tileCreated(int x, int y);
    }
}
