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
package org.geotoolkit.display2d.service;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.Deque;
import java.util.EventListener;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.RasterFactory;
import javax.swing.event.EventListenerList;

import org.geotoolkit.coverage.CoverageUtilities;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.apache.sis.geometry.GeneralEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Implementation of {@link RenderedImage} that is computed on the fly using portrayal rendering.
 * This implementation is mostly used to tile a portrayal context in a @{org.geotoolkit.coverage.GridMosaic}.
 *
 * @author Johann Sorel (Geomatys)
 * @see org.geotoolkit.process.coverage.pyramid.MapcontextPyramidProcess
 */
public class PortrayalRenderedImage implements RenderedImage{

    /** store pregenerated tiles */
//    private final Map<Integer,Raster> tileCache = new HashMap<Integer, Raster>();
    private final ColorModel colorModel;
    private final SampleModel sampleModel;
    private final Dimension gridSize;
    private final Dimension tileSize;
    private final double scale;

    /**
     * UpperLeft 2D from ViewDef Envelope.
     */
    private final Point2D upperleft;
//    private final J2DCanvasBuffered canvas;
    private final int nbtileonwidth;
    private final int nbtileonheight;

    /**
     * Index of first geographical axis
     */
    private final int minOrdi0;

    /**
     * Index of first geographical axis
     */
    private final int minOrdi1;

    /**
     * ViewDef envelope.
     * Used to fill non geographic range in CanvasDef request Envelope.
     */
    private final Envelope viewEnvelope;

    /**
     * ViewDef envelope CRS.
     */
    private final CoordinateReferenceSystem crs;

    /**
     * Portrayal CanvasDef.
     */
    private final CanvasDef canvasDef;

    /**
     * Portrayal SceneDef that contain Layers.
     */
    private final SceneDef sceneDef;

    /**
     * Portrayal ViewDef with requested envelope.
     */
    private final ViewDef viewDef;

    /** listener support */
    private final EventListenerList listeners = new EventListenerList();

    private final Deque<J2DCanvasBuffered> canvas = new ConcurrentLinkedDeque<J2DCanvasBuffered>();

    /**
     *
     * @param canvasDef : canvas size will be ignored.
     * @param sceneDef
     * @param viewDef
     * @param gridSize
     * @param tileSize
     */
    public PortrayalRenderedImage(final CanvasDef canvasDef, final SceneDef sceneDef, final ViewDef viewDef,
            final Dimension gridSize, final Dimension tileSize, final double scale) throws PortrayalException{
        this.gridSize = gridSize;
        this.tileSize = tileSize;
        this.scale = scale;
        this.colorModel = ColorModel.getRGBdefault();
        this.sampleModel = colorModel.createCompatibleSampleModel(1, 1);
        this.canvasDef = canvasDef;
        this.sceneDef = sceneDef;
        this.viewDef = viewDef;


        this.viewEnvelope = viewDef.getEnvelope();
        crs = viewEnvelope.getCoordinateReferenceSystem();
        this.upperleft = new Point2D.Double(
                viewEnvelope.getMinimum(0),
                viewEnvelope.getMaximum(1));

        this.minOrdi0 = CoverageUtilities.getMinOrdinate(crs);
        this.minOrdi1 = minOrdi0 + 1;

        nbtileonheight = 1;
        nbtileonwidth = 1;
    }

    /**
     * Tiles are generated on the fly, so we have information on their generation
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

    @Override
    public Raster getTile(int col, int row) {

        final double tilespanX = scale*tileSize.width;
        final double tilespanY = scale*tileSize.height;

        final GeneralEnvelope canvasEnv = new GeneralEnvelope(crs);
        int nbDim = canvasEnv.getDimension();

        for (int d = 0; d < nbDim; d++) {

            if (d == minOrdi0) {
                double minX = upperleft.getX() + (col) * tilespanX;
                double maxX = upperleft.getX() + (col+nbtileonwidth) * tilespanX;
                canvasEnv.setRange(d, minX, maxX);
            } else if (d == minOrdi1) {
                double minY = upperleft.getY() - (row+nbtileonheight) * tilespanY;
                double maxY = upperleft.getY() - (row) * tilespanY;
                canvasEnv.setRange(d, minY, maxY);
            } else {
                //other dimensions
                canvasEnv.setRange(d, viewEnvelope.getMinimum(d), viewEnvelope.getMaximum(d));
            }
        }

        J2DCanvasBuffered cvs = canvas.poll();

        try {
            if(cvs == null){
                cvs = new J2DCanvasBuffered(
                    crs, new Dimension(tileSize.width,tileSize.height));
                cvs.setRenderingHint(GO2Hints.KEY_COLOR_MODEL, colorModel);
                DefaultPortrayalService.prepareCanvas(cvs, canvasDef, sceneDef, viewDef);
            }

            cvs.setVisibleArea(canvasEnv);
        } catch (NoninvertibleTransformException | TransformException | PortrayalException ex) {
            Logger.getLogger(PortrayalRenderedImage.class.getName()).log(Level.SEVERE, null, ex);
        }

        //cut the canvas buffer in pieces
        cvs.repaint();
        final BufferedImage canvasBuffer = cvs.getSnapShot();
        final Raster data = canvasBuffer.getData(); // make a copy since we will reuse canvas
        fireTileCreated(col,row);
        canvas.push(cvs);
        return data;
    }

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
        final Rectangle bounds = getBounds();	// image's bounds

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
    private int getTileIndex(int col, int row){
        return row*getNumXTiles() + col;
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

    public static interface ProgressListener extends EventListener{

        void tileCreated(int x, int y);

    }
}
