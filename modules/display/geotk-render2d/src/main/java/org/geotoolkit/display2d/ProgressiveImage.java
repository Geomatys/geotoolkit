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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.logging.Level;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.media.jai.RasterFactory;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.multires.Tile;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

/**
 * On the fly calculated image. multi-threaded.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
final class ProgressiveImage {

    private final ColorModel colorModel;
    private final SampleModel sampleModel;
    private final Dimension gridSize;
    private final Dimension tileSize;
    private final double scale;
    private final Point2D upperleft;
    private final int nbtileonwidth;

    private final CanvasDef cdef;
    private final SceneDef sdef;
    private final double[] empty;

    private J2DCanvasBuffered canvas;

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
        this.upperleft = new Point2D.Double(
                envelope.getMinimum(0),
                envelope.getMaximum(1));

        //prepare a J2DCanvas to render several tiles in the same tile
        int maxNbTile = (1024*1024) / (tileSize.width*tileSize.height);
        maxNbTile = Math.max(1, maxNbTile);

        if (maxNbTile < gridSize.width) {
            //we can not generate a full line
            nbtileonwidth = maxNbTile;
        } else {
            //we could generate more than one line
            //but this class is used in MapContextTileGenerator which makes a Stream foreach line
            nbtileonwidth = gridSize.width;
        }

        this.cdef = canvasDef;
        this.sdef = sceneDef;

        final Color backColor = canvasDef.getBackground();
        final WritableRaster raster = RasterFactory.createWritableRaster(sampleModel, new Point(0, 0));
        final BufferedImage img = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), new Hashtable());
        if (backColor != null) {
            Graphics g = img.getGraphics();
            g.setColor(backColor);
            g.fillRect(0, 0, 1, 1);
        }
        final PixelIterator ite = PixelIterator.create(img);
        ite.moveTo(0, 0);
        empty = ite.getPixel((double[])null);
    }

    public ColorModel getColorModel() {
        return colorModel;
    }

    public SampleModel getSampleModel() {
        return sampleModel;
    }

    public Stream<Tile> generate(final int minx, final int maxx, int y, boolean skipEmptyTiles) {

        final Iterator<ImageTile> ite = new Iterator<ImageTile>() {

            private final LinkedList<ImageTile> tiles = new LinkedList<>();
            private int x = minx;

            @Override
            public boolean hasNext() {
                findNext();
                return !tiles.isEmpty();
            }

            @Override
            public ImageTile next() {
                findNext();
                if (tiles.isEmpty()) throw new NoSuchElementException();
                return tiles.removeFirst();
            }

            private void findNext() {
                while (tiles.isEmpty() && x < maxx) {
                    x += renderTiles(x, y, skipEmptyTiles, tiles);
                }
            }
        };

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(ite, Spliterator.DISTINCT), false);
    }

    /**
     *
     * @param col
     * @param row
     * @param tiles
     * @return number of tiles generated on X axe
     */
    private int renderTiles(int col, int row, boolean skipEmptyTiles, Collection<ImageTile> tiles) {

        /*
         * clip generated size to grid limits otherwise this causes a large canvas
         * which increases errors in resolution and envelope computations
         */
        int nbtileonwidth = this.nbtileonwidth;
        final int nbtileonheight = 1;
        if (col + nbtileonwidth > gridSize.width) {
            nbtileonwidth = gridSize.width - col;
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

        boolean batchIsEmpty = !canvas.repaint();
        if (skipEmptyTiles && batchIsEmpty) {
            //empty rendering
            return nbtileonwidth;
        }

        //cut the canvas buffer in pieces
        final BufferedImage canvasBuffer = canvas.getSnapShot();
        for(int x=0; x<nbtileonwidth && col+x<gridSize.width; x++){
            for(int y=0; y<nbtileonheight && row+y<gridSize.height; y++){
                BufferedImage tile = canvasBuffer.getSubimage(
                        x*tileSize.width,
                        y*tileSize.height,
                        tileSize.width,
                        tileSize.height);

                //TODO temporary reset, need to be fixed
                //redefined raster corner to be at 0,0
                WritableRaster clipRaster = tile.getRaster();
                final WritableRaster raster = clipRaster.createCompatibleWritableRaster(0, 0, clipRaster.getWidth(), clipRaster.getHeight());
                raster.setRect(clipRaster);
                tile = new BufferedImage(tile.getColorModel(), raster, tile.getColorModel().isAlphaPremultiplied(), null);

                if (skipEmptyTiles && BufferedImages.isAll(tile, empty)) {
                    //empty tile
                } else {
                    tiles.add(new DefaultImageTile(tile, col+x, row+y));
                }
            }
        }

        return nbtileonwidth;
    }

}
