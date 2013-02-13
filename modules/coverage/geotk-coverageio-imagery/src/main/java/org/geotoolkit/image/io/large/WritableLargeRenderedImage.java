/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.image.io.large;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.*;
import java.util.Arrays;
import java.util.Vector;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;

/**
 *
 * @author Remi Marechal (Geomatys).
 */
public class WritableLargeRenderedImage implements WritableRenderedImage {

    private final LargeCache tilecache;

    private static final int DEFAULT_TILE_SIZE = 256;
    private static final int MIN_TILE_SIZE = 64;
    private static final Point ptOffset = new Point();
    private static final long DEFAULT_MEMORY_CAPACITY = 64000000;
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
    private Vector<RenderedImage> vector = null;

    private final int nbrTileX;
    private final int nbrTileY;
    private final ColorModel cm;
    private final SampleModel sm;
    private Point[] tileIndices = null;
    private final boolean[][] isWrite;

    public WritableLargeRenderedImage(int minX, int minY, int width, int height,
            Dimension tileSize, int tileGridXOffset, int tileGridYOffset, ColorModel colorModel) {
        this.tilecache = LargeCache.getInstance(DEFAULT_MEMORY_CAPACITY);
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
        
        isWrite = new boolean[nbrTileY][nbrTileX];
        for (boolean[] bool : isWrite) Arrays.fill(bool, false);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void addTileObserver(TileObserver to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void removeTileObserver(TileObserver to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public WritableRaster getWritableTile(int tileX, int tileY) {
        if (!isWrite[tileY - minTileGridY][tileX - minTileGridX]) fillWritableImage();
        return (WritableRaster) tilecache.getTile(this, tileX, tileY);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void releaseWritableTile(int tileX, int tileY) {
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean isTileWritable(int tileX, int tileY) {
        if (!isWrite[tileY - minTileGridY][tileX - minTileGridX]) fillWritableImage();
        return (tilecache.getTile(this, tileX, tileY) instanceof WritableRaster);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Point[] getWritableTileIndices() {
        if (tileIndices == null) {
            tileIndices = new Point[nbrTileX*nbrTileY];
            int idTab = 0;
            for (int ty = minTileGridY, tmy = minTileGridY+nbrTileY; ty < tmy; ty++) {
                for (int tx = minTileGridX, tmx = minTileGridX+nbrTileX; tx < tmx; tx++) {
                    tileIndices[idTab++] = new Point(tx, ty);
                }
            }
        }
        return tileIndices;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean hasTileWriters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setData(Raster r) {
        final int rminX = r.getMinX();
        final int rminY = r.getMinY();
        final int rw    = r.getWidth();
        final int rh    = r.getHeight();
        final int rmaxX = rminX+rw;
        final int rmaxY = rminY+rh;
        if (Math.abs(minX-rminX) % tileWidth != 0) {
            throw new IllegalArgumentException("raster minX value don't tie in tile coordinate");
        }
        if (Math.abs(minY-rminY) % tileHeight != 0) {
            throw new IllegalArgumentException("raster minY value don't tie in tile coordinate");
        }
        final int ix  = Math.max(rminX, minX);
        final int imx = Math.min(rmaxX, minX+width);
        final int iy  = Math.max(rminY, minY);
        final int imy = Math.min(rmaxY, minY+height);
        if (imx <= ix || imy <= iy) {
            throw new IllegalArgumentException("raster is not within image boundary");
        }
        if ((imx-ix) != rw || (imy-iy) != rh) {
            throw new IllegalArgumentException("raster boundary don't tie in tile coordinate");
        }
        if (r.getSampleModel().getDataType() != sm.getDataType()) {
            throw new IllegalArgumentException("raster datatype don't tie with image datatype");
        }
        final int tx = minTileGridX + (rminX-minX) / tileWidth;
        final int ty = minTileGridY + (rminY-minY) / tileHeight;
        final int isRow = ty - minTileGridY;
        final int isCol = tx - minTileGridX;
        if (isWrite[isRow][isCol]) tilecache.remove(this, tx, ty);
        tilecache.add(this, tx, ty, r);
        isWrite[isRow][isCol] = true;
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
    public Raster getTile(int tileX, int tileY) {
        if (!isWrite[tileY - minTileGridY][tileX - minTileGridX]) fillWritableImage();
        return tilecache.getTile(this, tileX, tileY);
    }

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
                    final Raster r = tilecache.getTile(this, tx, ty);
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
                    final Raster r = tilecache.getTile(this, tx, ty);
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

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void finalize() throws Throwable {
        tilecache.removeTiles(this);
        super.finalize();
    }
    
    /**
     * Fill image by writable raster.
     */
    private void fillWritableImage() {
        //remplissage de raster vide
        final int mx = minX + width;
        final int my = minY + height;
        int ry = minY;
        for (int ty = minTileGridY, tmy = minTileGridY+nbrTileY; ty < tmy; ty++){
            int rx = minX;
            int row = ty - minTileGridY;
            for (int tx = minTileGridX, tmx = minTileGridX+nbrTileX; tx < tmx; tx++) {
                int col = tx - minTileGridX;
                if (!isWrite[row][col]) {
                    final int rw = Math.min(rx + tileWidth, mx)  - rx;
                    final int rh = Math.min(ry + tileHeight, my) - ry;
                    ptOffset.setLocation(rx, ry);
                    this.tilecache.add(this, tx, ty, Raster.createWritableRaster(cm.createCompatibleSampleModel(rw, rh), ptOffset));
                    isWrite[row][col] = true;
                }
                rx += tileWidth;
            }
            ry += tileHeight;
        }
    }
}
