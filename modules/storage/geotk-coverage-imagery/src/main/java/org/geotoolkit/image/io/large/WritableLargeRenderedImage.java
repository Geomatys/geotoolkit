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
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;

/**
 *
 * @author Alexis Manin (Geomatys).s
 * @author Remi Marechal (Geomatys).
 */
public class WritableLargeRenderedImage implements WritableRenderedImage {

    private final LargeCache tilecache;

    /**
     * Default tile size.
     */
    private static final int DEFAULT_TILE_SIZE = 256;

    /**
     * Minimum required tile size.
     */
    private static final int MIN_TILE_SIZE = 64;

    /**
     * Upper left corner of all image tiles.
     */
    private Point[] tileIndices = null;

    /**
     * Defaine if tile is allready writen.
     */
    private final boolean[][] isWrite;

    /**
     * An array which stores a lock for each tile. The index of the tile (x, y) is retrieved as following :
     *      y * {@linkplain #nbrTileX} + x.
     */
    private final ReentrantReadWriteLock[] tileLocks;

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
     * Create {@link WritableLargeRenderedImage} with default upper corner at position (0, 0),
     * a default tile size of 256 x 256 pixels and a default tile grid offset at position (0, 0).
     *
     * @param width image width.
     * @param height image height.
     * @param colorModel {@link ColorModel} use to build {@link WritableRaster} (image tiles).
     */
    public WritableLargeRenderedImage(int width, int height, ColorModel colorModel, SampleModel sampleModel) {
        this(0, 0, width, height, null, 0, 0, colorModel, sampleModel);
    }

    /**
     * Create {@link WritableLargeRenderedImage} object.
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
    public WritableLargeRenderedImage(int minX, int minY, int width, int height,
            Dimension tileSize, int tileGridXOffset, int tileGridYOffset,
            ColorModel colorModel, SampleModel sm) {
        ArgumentChecks.ensureNonNull("ColorModel", colorModel);
        ArgumentChecks.ensureStrictlyPositive("image width", width);
        ArgumentChecks.ensureStrictlyPositive("image height", height);
        this.tilecache = LargeCache.getInstance();
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
        this.sm = sm;
        this.minTileGridX = (minX - tileGridXOffset) / tileWidth;
        this.minTileGridY = (minY - tileGridYOffset) / tileHeight;
        if (tileGridXOffset < minX) minTileGridX--;
        if (tileGridYOffset < minY) minTileGridY--;
        isWrite = new boolean[nbrTileY][nbrTileX];
        for (boolean[] bool : isWrite) Arrays.fill(bool, false);

        //-- tile lock initialize
        tileLocks = new ReentrantReadWriteLock[nbrTileX * nbrTileY];
        for (int i = 0; i < tileLocks.length; i++) {
            tileLocks[i] = new ReentrantReadWriteLock();
        }
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
        return (WritableRaster) getTile(tileX, tileY);
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
        return (getTile(tileX, tileY) instanceof WritableRaster);
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

        final ReentrantReadWriteLock lock = tileLocks[ty * nbrTileX + tx];
        lock.writeLock().lock(); //-- ask reading token

        try {
            if (isWrite[isRow][isCol]) tilecache.remove(this, tx, ty);
            tilecache.add(this, tx, ty, r);
            isWrite[isRow][isCol] = true;
        } finally {
            lock.writeLock().unlock();
        }
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

        final ReentrantReadWriteLock lock = tileLocks[tileY * nbrTileX + tileX];
        lock.readLock().lock(); //-- ask reading token
        final boolean iswrite;
        try {
            iswrite = isWrite[tileY - minTileGridY][tileX - minTileGridX];
        } finally {
            lock.readLock().unlock();
        }

        if (!iswrite) fillWritableImage(tileX, tileY);//-- inside this method : writing lock

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
            final WritableRaster wr = Raster.createWritableRaster(cm.createCompatibleSampleModel(width, height), new Point(minX, minY));
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
            final WritableRaster wr = Raster.createWritableRaster(cm.createCompatibleSampleModel(rw, rh), new Point(rx, ry));
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
     * Create an original {@link Raster} adapted for image properties.
     *
     * @param tileX image tile index in X direction.
     * @param tileY image tile index in Y direction.
     */
    private void fillWritableImage(final int tileX, final int tileY) {
        final ReentrantReadWriteLock lock = tileLocks[tileY * nbrTileX + tileX];
        lock.writeLock().lock(); //-- ask reading token

        try {
            if (!isWrite[tileY - minTileGridY][tileX - minTileGridX]) {

                //-- upper left raster corner.
                final int ptOffx = minX + (tileX - minTileGridX) * tileWidth;
                final int ptOffy = minY + (tileY - minTileGridY) * tileHeight;

                //-- raster width and height at least.
                final int rw = StrictMath.min(ptOffx + tileWidth,  minX + width)  - ptOffx;
                final int rh = StrictMath.min(ptOffy + tileHeight, minY + height) - ptOffy;

                this.tilecache.add(this, tileX, tileY, Raster.createWritableRaster(cm.createCompatibleSampleModel(rw, rh), new Point(ptOffx, ptOffy)));
                isWrite[tileY - minTileGridY][tileX - minTileGridX] = true;
            }
        } finally {
            lock.writeLock().unlock();
        }




////        //remplissage de raster vide
////        final int mx = minX + width;
////        final int my = minY + height;
////        int ry = minY;
////        for (int ty = minTileGridY, tmy = minTileGridY+nbrTileY; ty < tmy; ty++){
////            int rx = minX;
////            int row = ty - minTileGridY;
////            for (int tx = minTileGridX, tmx = minTileGridX+nbrTileX; tx < tmx; tx++) {
////                int col = tx - minTileGridX;
////                if (!isWrite[row][col]) {
////                    final int rw = Math.min(rx + tileWidth, mx)  - rx;
////                    final int rh = Math.min(ry + tileHeight, my) - ry;
////                    ptOffset.setLocation(rx, ry);
////                    this.tilecache.add(this, tx, ty, Raster.createWritableRaster(cm.createCompatibleSampleModel(rw, rh), ptOffset));
////                    isWrite[row][col] = true;
////                }
////                rx += tileWidth;
////            }
////            ry += tileHeight;
////        }
    }
}
