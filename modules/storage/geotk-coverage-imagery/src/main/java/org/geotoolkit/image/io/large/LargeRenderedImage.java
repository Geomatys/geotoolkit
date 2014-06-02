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
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.media.jai.TileCache;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;

/**
 * <p>
 * Define "Large" {@link RenderedImage} which is an image with a large size.<br/>
 * It can contain more data than computer ram memory capacity, in cause of {@link TileCache} 
 * mechanic which store some image tiles on hard drive.
 * </p>
 * 
 * TODO : Change mecanism to get a source data as entry, not a reader ? It would allow multi-threading
 * on tile reading, by allocating a reader on the fly.
 * 
 * @author Remi Marechal (Geomatys)
 * @author Alexis Manin  (Geomatys)
 */
public class LargeRenderedImage implements RenderedImage {

    private static final Logger LOGGER = Logging.getLogger(LargeRenderedImage.class);
    
    /**
     * Mechanic to store tile on hard drive.
     */
    private final TileCache tilecache;

    /**
     * Default tile size.
     */
    private static final int DEFAULT_TILE_SIZE = 256;
    
    /**
     * Minimum required tile size.
     */
    private static final int MIN_TILE_SIZE = 64;
    
    /**
     * Maximum allowed tile size.
     */
    private static final int MAX_TILE_SIZE = 2048;
    
    /** Maximum dimension which will be allowed for {@link #getData(java.awt.Rectangle) } method. */
    private static final Dimension RASTER_MAX_SIZE = new Dimension(10000, 10000);

    /**
     * Default store memory capacity.
     */
    private static final long DEFAULT_MEMORY_CAPACITY = 64000000;

    /**
     * {@link ImageReader} where is read each image tile.
     */
    private final ImageReader imageReader;
    
    /**
     * Tile number in X direction.
     */
    private final int nbrTileX;
    
    /**
     * Tile number in Y direction.
     */
    private final int nbrTileY;

    /**
     * Define if tile will be read from {@link #imageReader} or call from {@link #tilecache}. 
     */
    private final boolean[][] isRead;

    /**
     * An array which stores a lock for each tile. The index of the tile (x, y) is retrieved as following :
     *      y * {@linkplain #nbrTileX} + x.
     */
    private final ReentrantReadWriteLock[] tileLocks;

    /**
     * Image attributs.
     */
    private final int imageIndex;
    private final int width;
    private final int height;
    private final int tileWidth;
    private final int tileHeight;
    private final int tileGridXOffset;
    private final int tileGridYOffset;
    private ColorModel cm  = null;
    private SampleModel sm = null;

    /**
     * Create a {@link LargeRenderedImage} object with a default {@link TileCache} 
     * with 64Mo memory capacity and a default tile size of 256 x 256 piels.
     * 
     * @param imageReader reader which target at image stored on disk.
     * @param imageIndex the index of the image to be retrieved.
     * @throws IOException if an error occurs during reading.
     */
    public LargeRenderedImage(ImageReader imageReader, int imageIndex) throws IOException{
        this(imageReader, imageIndex, null, null);
    }
    
    /**
     * Create {@link LargeRenderedImage} object.
     * 
     * @param imageReader reader which target at image stored on disk.
     * @param imageIndex the index of the image to be retrieved.
     * @param tilecache cache mechanic class. if {@code null} a default {@link TileCache} 
     *                  is define with a default memory capacity of 64 Mo.
     * @param tileSize internal {@link Raster} (tile) dimension. if {@code null} 
     *                 a default tile size is chosen (256x256 pixels).
     * @throws IOException if an error occurs during reading.
     */
    public LargeRenderedImage(ImageReader imageReader, int imageIndex, TileCache tilecache, Dimension tileSize) throws IOException {
        ArgumentChecks.ensureNonNull("imageReader", imageReader);
        ArgumentChecks.ensurePositive("image index", imageIndex);
        this.imageReader = imageReader;
        this.imageIndex  = imageIndex;
        this.width       = imageReader.getWidth(imageIndex);
        this.height      = imageReader.getHeight(imageIndex);
        this.tilecache = (tilecache != null) ? tilecache : LargeCache.getInstance(DEFAULT_MEMORY_CAPACITY);
        this.tileGridXOffset = 0;
        this.tileGridYOffset = 0;
        if (tileSize != null) {
            tileWidth  = Math.min(Math.max(MIN_TILE_SIZE, tileSize.width),  MAX_TILE_SIZE);
            tileHeight = Math.min(Math.max(MIN_TILE_SIZE, tileSize.height), MAX_TILE_SIZE);
        } else {
            tileWidth = tileHeight = DEFAULT_TILE_SIZE;
        }
        this.nbrTileX = (width + tileWidth - 1)   / tileWidth;
        this.nbrTileY = (height + tileHeight - 1) / tileHeight;
        isRead = new boolean[nbrTileY][nbrTileX];
        for (boolean[] bool : isRead) Arrays.fill(bool, false);

        tileLocks = new ReentrantReadWriteLock[nbrTileX * nbrTileY];
        for (int i = 0; i < tileLocks.length; i++) {
            tileLocks[i] = new ReentrantReadWriteLock();
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Vector<RenderedImage> getSources() {
//        if (vector != null) return vector;
//        vector = new Vector<RenderedImage>(numImages);
//        for (int id = minIndex; id < numImages; id++) {
//            try {
//                vector.add(new LargeRenderedImage(imageReader, id, tilecache, new Dimension(tileWidth, tileHeight)));
//            } catch (IOException ex) {
//                Logger.getLogger(LargeRenderedImage.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return vector;
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
        if (cm == null) getTile(0, 0);
        return cm;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public SampleModel getSampleModel() {
        if (sm == null) sm = getColorModel().createCompatibleSampleModel(tileWidth, tileHeight);
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
        return 0;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getMinY() {
        return 0;
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
        return (int) - (tileGridXOffset + (tileWidth - 1) * Math.signum(tileGridXOffset)) / tileWidth;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getMinTileY() {
        return (int) - (tileGridYOffset + (tileHeight - 1) * Math.signum(tileGridYOffset)) / tileHeight;
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
        
        final ReadWriteLock tileLock = tileLocks[tileY * nbrTileX + tileX];
        tileLock.readLock().lock();
        try {
            if (isRead[tileY][tileX]) return tilecache.getTile(this, tileX, tileY);
        } catch (Exception e) {
            /* This block is because of possible runtime exception if there's a cache problem, 
             * we don't throw error, just reload the tile.
             */
            LOGGER.log(Level.WARNING, "Cannot get tile from cache system, but it should be here !", e);
        } finally {
            tileLock.readLock().unlock();
        }

        // Prepare for tile loading
        tileLock.writeLock().lock();
        try {
            try {
                if (isRead[tileY][tileX]) {
                    return tilecache.getTile(this, tileX, tileY);
                }
            } catch (Exception e) {
                 // Do not log again, it must have been done above.
            }
            
            // Compute tile position in source image
            final int minRx = tileX * tileWidth;
            final int minRy = tileY * tileHeight;
            int wRx = Math.min(minRx + tileWidth, width) - minRx;
            int hRy = Math.min(minRy + tileHeight, height) - minRy;
            final ImageReadParam imgParam = imageReader.getDefaultReadParam();
            imgParam.setSourceRegion(new Rectangle(minRx, minRy, wRx, hRy));

            // Load tile and give it to cache.
            // TODO : Modify reading mecanism to allow multi-threading ?
            BufferedImage buff;
            synchronized (imageReader) {
                buff = imageReader.read(imageIndex, imgParam);
            }
            if (cm == null) cm = buff.getColorModel();
            final WritableRaster wRaster = Raster.createWritableRaster(buff.getSampleModel(), buff.getRaster().getDataBuffer(), new Point(minRx, minRy));
            tilecache.add(this, tileX, tileY, wRaster);
            isRead[tileY][tileX] = true;

            return wRaster;
        } catch (IOException e) {
            throw new IllegalStateException("Impossible to read tile from image reader.", e);
        } finally {
            tileLock.writeLock().unlock();
        }
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public Raster getData() {
        // in contradiction with this class aim.
        // in attempt to replace JAI dependencies.
        if (width <= RASTER_MAX_SIZE.width && height <= RASTER_MAX_SIZE.height) {
            final WritableRaster wr = Raster.createWritableRaster(cm.createCompatibleSampleModel(width, height), new Point(0, 0));
            final Rectangle rect = new Rectangle();
            int my = 0;
            for (int ty = 0, tmy = 0 + nbrTileY; ty < tmy; ty++) {
                int mx = 0;
                for (int tx = 0, tmx = 0 + nbrTileX; tx < tmx; tx++) {
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Raster getData(Rectangle rect) {
        // in contradiction with this class aim.
        // in attempt to replace JAI dependencies.
        final int minX = 0;
        final int minY = 0;
        final int minTileGridY = 0;
        final int minTileGridX = 0;
        final int rx = Math.max(rect.x, minX);
        final int ry = Math.max(rect.y, minY);
        final int rw = Math.min(rect.x+rect.width, minX+width)-rx;
        final int rh = Math.min(rect.y+rect.height, minY+height)-ry;
        if (rw <= RASTER_MAX_SIZE.width && rh <= RASTER_MAX_SIZE.height) {
            final WritableRaster wr = Raster.createWritableRaster(cm.createCompatibleSampleModel(rw, rh), new Point(rx, ry));
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
        throw new UnsupportedOperationException("Not supported yet.");
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
}
