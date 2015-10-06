 /*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2015, Geomatys
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
package org.geotoolkit.image.io.large;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.*;
import java.util.Vector;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;

/**
 * Abstract large rendered image.
 * This rendered image may be used to represent a very large image but only loading it
 * tile by tile.
 *
 * Subclasses mush implement the getTile method, the caching mechanism is leaved to
 * the implementor.
 *
 * @author Remi Marechal (Geomatys).
 * @author Johann Sorel (Geomatys).
 */
public abstract class AbstractLargeRenderedImage implements RenderedImage {


    /**
     * Default tile size.
     */
    private static final int DEFAULT_TILE_SIZE = 256;
    
    /**
     * Minimum required tile size.
     */
    private static final int MIN_TILE_SIZE = 64;
    
    /**
     * Upper left corner of currently stored {@link Raster}. 
     */
    private static final Point ptOffset = new Point();
    
    /**
     * Image attributs.
     */
    private final int minX;
    private final int minY;
    private final int width;
    private final int height;
    private final int tileWidth;
    private final int tileHeight;
    private final int tileGridXOffset;
    private final int tileGridYOffset;
    private int minTileGridX;
    private int minTileGridY;
    private final int nbrTileX;
    private final int nbrTileY;
    private final ColorModel cm;
    private final SampleModel sm;

    /**
     * Create {@link LargeRenderedImage} with default upper corner at position (0, 0),
     * a default tile size of 256 x 256 pixels and a default tile grid offset at position (0, 0).
     * 
     * @param width image width.
     * @param height image height.
     * @param colorModel {@link ColorModel} use to build {@link Raster} (image tiles).
     */
    public AbstractLargeRenderedImage(int width, int height, ColorModel colorModel) { 
        this(0, 0, width, height, null, 0, 0, colorModel);
    }
    
    /**
     * Create {@link LargeRenderedImage} object.
     * 
     * @param minX image upper left corner min X values.
     * @param minY image upper left corner min Y values. 
     * @param width image width.
     * @param height image height. 
     * @param tileSize size of tile or raster within this image.
     * @param tileGridXOffset tile grid offset in X direction.
     * @param tileGridYOffset tile grid offset in Y direction.
     * @param colorModel {@link ColorModel} use to build {@link WritableRaster} (image tiles). 
     */
    public AbstractLargeRenderedImage(int minX, int minY, int width, int height,
            Dimension tileSize, int tileGridXOffset, int tileGridYOffset, ColorModel colorModel) {
        ArgumentChecks.ensureNonNull("ColorModel", colorModel);
        ArgumentChecks.ensureStrictlyPositive("image width", width);
        ArgumentChecks.ensureStrictlyPositive("image height", height);
        this.minX      = minX;
        this.minY      = minY;
        this.width     = width;
        this.height    = height;

        if (tileSize == null) {
            this.tileWidth = this.tileHeight = DEFAULT_TILE_SIZE;
        } else {
            tileWidth  = Math.min(Math.max(MIN_TILE_SIZE, tileSize.width),  DEFAULT_TILE_SIZE);
            tileHeight = Math.min(Math.max(MIN_TILE_SIZE, tileSize.height), DEFAULT_TILE_SIZE);
        }

        this.tileGridXOffset = tileGridXOffset;
        this.tileGridYOffset = tileGridYOffset;
        this.nbrTileX = (width  + tileWidth - 1)  / tileWidth;
        this.nbrTileY = (height + tileHeight - 1) / tileHeight;
        this.cm = colorModel;
        this.sm = colorModel.createCompatibleSampleModel(tileWidth, tileHeight);
        this.minTileGridX = (minX - tileGridXOffset) / tileWidth;
        this.minTileGridY = (minY - tileGridYOffset) / tileHeight;
        if (tileGridXOffset < minX) minTileGridX--;
        if (tileGridYOffset < minY) minTileGridY--;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Vector<RenderedImage> getSources() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Object getProperty(String name) {
        return Image.UndefinedProperty;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public String[] getPropertyNames() {
        return null;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public ColorModel getColorModel() {
        return cm;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public SampleModel getSampleModel() {
        return sm;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getMinX() {
        return minX;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getMinY() {
        return minY;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getNumXTiles() {
        return nbrTileX;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getNumYTiles() {
        return nbrTileY;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getMinTileX() {
        return minTileGridX;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getMinTileY() {
        return minTileGridY;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getTileGridXOffset() {
        return tileGridXOffset;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getTileGridYOffset() {
        return tileGridYOffset;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public abstract Raster getTile(int tileX, int tileY);

    /**
     * {@inheritDoc }.
     */
    @Override
    public Raster getData() {
        // in contradiction with this class aim.
        // in attempt to replace JAI dependencies.
        if (width <= 5000 && height <= 5000) {
            ptOffset.setLocation(minX, minY);
            final WritableRaster wr = Raster.createWritableRaster(cm.createCompatibleSampleModel(width, height), ptOffset);
            final Rectangle rect = new Rectangle();
            int my = minY;
            for (int ty = minTileGridY, tmy = minTileGridY + nbrTileY; ty < tmy; ty++) {
                int mx = minX;
                for (int tx = minTileGridX, tmx = minTileGridX + nbrTileX; tx < tmx; tx++) {
                    final Raster r = getTile(tx, ty);
                    rect.setBounds(mx, my, tileWidth, tileHeight);
                    //recopie
                    final PixelIterator copix = PixelIteratorFactory.createDefaultWriteableIterator(wr, wr, rect);
                    final PixelIterator pix = PixelIteratorFactory.createDefaultIterator(r, rect);
                    while (copix.next()) {
                        pix.next();
                        copix.setSampleDouble(pix.getSampleDouble());
                    }
                    mx += tileWidth;
                }
                my += tileHeight;
            }
            return wr;
        }
        throw new UnsupportedOperationException("Not supported yet. Raster weight too expensive.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Raster getData(Rectangle rect) {
        // in contradiction with this class aim.
        // in attempt to replace JAI dependencies.
        final int rx = Math.max(rect.x, minX);
        final int ry = Math.max(rect.y, minY);
        final int rw = Math.min(rect.x+rect.width, minX+width)-rx;
        final int rh = Math.min(rect.y+rect.height, minY+height)-ry;
        if (rw <= 5000 && rh <= 5000) {
            ptOffset.setLocation(rx, ry);
            final WritableRaster wr = Raster.createWritableRaster(cm.createCompatibleSampleModel(rw, rh), ptOffset);
            final Rectangle area = new Rectangle();

            int ty = minTileGridY  + (ry - minY) / tileHeight;
            int tbx = minTileGridX + (rx - minX) / tileWidth;
            int tmaxY = (ry+rh-minY+tileHeight-1)/tileHeight;
            int tmaxX = (rx+rw-minX+tileWidth-1)/tileWidth;
            for (; ty < tmaxY; ty++) {
                for (int tx = tbx; tx < tmaxX; tx++) {
                    final Raster r = getTile(tx, ty);
                    final int ix = Math.max(rx, minX + (tx-minTileGridX) * tileWidth);
                    final int iy = Math.max(ry, minY + (ty-minTileGridY) * tileHeight);
                    final int imx = Math.min(rx + rw, minX + (tx + 1 - minTileGridX) * tileWidth);
                    final int imy = Math.min(ry + rh, minY + (ty + 1 - minTileGridY) * tileHeight);
                    area.setBounds(ix, iy, imx-ix, imy-iy);
                    //recopie
                    final PixelIterator copix = PixelIteratorFactory.createDefaultWriteableIterator(wr, wr, area);
                    final PixelIterator pix = PixelIteratorFactory.createDefaultIterator(r, area);
                    while (copix.next()) {
                        pix.next();
                            copix.setSampleDouble(pix.getSampleDouble());
                    }
                }
            }
            return wr;
        }
        throw new UnsupportedOperationException("Not supported yet.Raster weight too expensive.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public WritableRaster copyData(WritableRaster raster) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
