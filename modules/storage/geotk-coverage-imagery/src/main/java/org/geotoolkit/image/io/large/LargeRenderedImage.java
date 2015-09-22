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
import javax.imageio.spi.ImageReaderSpi;
import org.apache.sis.util.Disposable;

/**
 * Define "Large" {@link RenderedImage} which is an image with a large size.<br/>
 * It can contain more data than computer ram memory capacity, in cause of {@link TileCache}
 * mechanic which store some image tiles on hard drive.
 *
 * TODO : Change mecanism to get a source data as entry, not a reader ? It would allow multi-threading
 * on tile reading, by allocating a reader on the fly.
 *
 * @author Remi Marechal (Geomatys)
 * @author Alexis Manin  (Geomatys)
 */
public class LargeRenderedImage implements RenderedImage, Disposable {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.image.io.large");

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
     * The provider for {@link #imageReader}, or {@code null} if none.
     *
     * @see #imageReader
     * @see #input
     */
    private final ImageReaderSpi spi;

    /**
     * {@link ImageReader} where is read each image tile, or {@code null} if not yet created.
     */
    private ImageReader imageReader;

    /**
     * {@link javax.imageio.ImageReadParam} which specify how to read the source image (subsampling, cropping).
     */
    private final ImageReadParam sourceReadParam;

    /**
     * The input to give to {@link #imageReader}, or {@code null} is unspecified.
     * This is non-null only if {@link #spi} is non-null.
     */
    private final Object input;

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
     * Image attributes.
     */
    private final int imageIndex;
    private final int width;
    private final int height;
    private final int tileWidth;
    private final int tileHeight;
    private final int tileGridXOffset;
    private final int tileGridYOffset;
    private final ColorModel cm;
    private final SampleModel sm;

    /**
     * Create a {@link LargeRenderedImage} object with a default {@link TileCache}
     * with 64 Mb memory capacity and a default tile size of 256 x 256 pixels.
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
     *                  is define with a default memory capacity of 64 Mb.
     * @param tileSize internal {@link Raster} (tile) dimension. if {@code null}
     *                 a default tile size is chosen (256x256 pixels).
     * @throws IOException if an error occurs during reading.
     */
    public LargeRenderedImage(ImageReader imageReader, int imageIndex, TileCache tilecache, Dimension tileSize) throws IOException {
        this(imageReader, null, imageIndex, tilecache, tileSize);
    }

    public LargeRenderedImage(ImageReader imageReader, ImageReadParam readParam, int imageIndex,
            TileCache tilecache, Dimension tileSize) throws IOException
    {
        this(null, imageReader, readParam, null, imageIndex, tilecache, tileSize);
    }

    public LargeRenderedImage(ImageReaderSpi spi, ImageReadParam readParam, final Object input, int imageIndex,
            TileCache tilecache, Dimension tileSize) throws IOException
    {
        this(spi, null, readParam, input, imageIndex, tilecache, tileSize);
    }

    private LargeRenderedImage(ImageReaderSpi spi, ImageReader imageReader, ImageReadParam readParam,
            final Object input, int imageIndex, TileCache tilecache, Dimension tileSize) throws IOException
    {
        ArgumentChecks.ensurePositive("image index", imageIndex);
        this.spi         = spi;
        this.imageReader = imageReader;
        this.input       = input;
        this.imageIndex  = imageIndex;

        /* To initialize the color model, we must read a little piece of the source image.
         * First, we check we have an initialized reader (containing an input), or an input along with an SPI or a reader.
         */
        if ((spi == null || input == null) && (imageReader == null || (imageReader.getInput() == null && input == null))) {
            throw new IllegalArgumentException("Either a valid image reader or an SPI along with an input object must " +
                    "be given at built.");
        }

        if (this.imageReader == null) {
            this.imageReader = imageReader = spi.createReaderInstance();
        }

        if (this.imageReader.getInput() == null) {
            this.imageReader.setInput(input, false, false);
        }

        final ImageReadParam tmpReadParam = imageReader.getDefaultReadParam();
        tmpReadParam.setSourceRegion(new Rectangle(0, 0, 1, 1));
        final BufferedImage tmpImage = imageReader.read(imageIndex, tmpReadParam);
        cm = tmpImage.getColorModel();
        sm = tmpImage.getSampleModel();

        if (readParam != null) {
            if (readParam.getSourceRenderSize() != null) {
                width = readParam.getSourceRenderSize().width;
                height = readParam.getSourceRenderSize().height;
            } else {
                Rectangle sourceRegion = readParam.getSourceRegion();
                if (sourceRegion == null) {
                    sourceRegion = new Rectangle(0, 0, imageReader.getWidth(imageIndex), imageReader.getHeight(imageIndex));
                }
                Point destOffset = readParam.getDestinationOffset();
                int subsampledZoneWidth = (int) Math.ceil((double)(sourceRegion.width - readParam.getSubsamplingXOffset())/readParam.getSourceXSubsampling());
                width = destOffset.x + readParam.getSubsamplingXOffset() + subsampledZoneWidth;
                int subsampledZoneHeight = (int) Math.ceil((double)(sourceRegion.height - readParam.getSubsamplingYOffset())/readParam.getSourceYSubsampling());
                height = destOffset.y + readParam.getSubsamplingYOffset() + subsampledZoneHeight;
            }
            sourceReadParam = readParam;
        } else {
            this.width       = imageReader.getWidth(imageIndex);
            this.height      = imageReader.getHeight(imageIndex);
            sourceReadParam = null;
        }

        this.tilecache = (tilecache != null) ? tilecache : LargeCache.getInstance();
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
//                Logger.getLogger("org.geotoolkit.image.io.large").log(Level.SEVERE, null, ex);
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
            if (isRead[tileY][tileX]) {
                return tilecache.getTile(this, tileX, tileY);
            }
        } catch (IllegalArgumentException e) {
            /*
             * Should occurs if LargeCache is used in memory mode only and the requested tile
             * is not anymore in cache.
             */
            LOGGER.log(Level.FINER, "Tile not found in cache system.", e);
            isRead[tileY][tileX] = false;
        } catch (Exception e) {
            /* This block is because of possible runtime exception if there's a cache problem,
             * we don't throw error, just reload the tile.
             */
            LOGGER.log(Level.FINE, "Cannot get tile from cache system, but it should be here !", e);
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
            int tileWidth = Math.min(minRx + this.tileWidth, width) - minRx;
            int tileHeight = Math.min(minRy + this.tileHeight, height) - minRy;
            final ImageReadParam imgParam = imageReader.getDefaultReadParam();

            final BufferedImage result;
            // no subsampling nor offset, read directly the specified region.
            if (sourceReadParam == null) {
                imgParam.setSourceRegion(new Rectangle(minRx, minRy, tileWidth, tileHeight));
                // TODO : Modify reading mechanism to allow multi-threading ?
                synchronized (imageReader) {
                    result = imageReader.read(imageIndex, imgParam);
                }
            } else {
                /* If an offset has been specified, we must fill result only from this point. First, we check if the given tile
                 * is completely before the destination offset, in which case we just have to return a black filled image.
                 * Otherwise, we compute the source region to read which intersects the asked tile rectangle.
                 */
                final Point destOffset = sourceReadParam.getDestinationOffset();
                if (minRx + tileWidth < destOffset.x || minRy + tileHeight < destOffset.y) {
                    result = new BufferedImage(cm, cm.createCompatibleWritableRaster(tileWidth, tileHeight), cm.isAlphaPremultiplied(), null);
                } else {
                    if (minRx < destOffset.x || minRy < destOffset.y) {
                        imgParam.setDestination(new BufferedImage(cm, cm.createCompatibleWritableRaster(tileWidth, tileHeight), cm.isAlphaPremultiplied(), null));
                        imgParam.setDestinationOffset(new Point(Math.max(0, destOffset.x - minRx), Math.max(0, destOffset.y - minRy)));
                    }

                    final Rectangle srcRegion = sourceReadParam.getSourceRegion();
                    final int ssX = sourceReadParam.getSourceXSubsampling();
                    final int ssY = sourceReadParam.getSourceYSubsampling();

                    final int readOffsetX = minRx-destOffset.x;
                    final int readOffsetY = minRy-destOffset.y;
                    // Put subsampling offset only on left and upper border tiles.
                    imgParam.setSourceRegion(new Rectangle(
                            srcRegion.x + (readOffsetX > 0? readOffsetX * ssX : sourceReadParam.getSubsamplingXOffset()),
                            srcRegion.y + (readOffsetY > 0? readOffsetY * ssY : sourceReadParam.getSubsamplingYOffset()),
                            (tileWidth + Math.min(0, readOffsetX)) * ssX,
                            (tileHeight + Math.min(0, readOffsetY)) * ssY));
                    imgParam.setSourceSubsampling(ssX, ssY, 0, 0);

                    // TODO : Modify reading mechanism to allow multi-threading ?
                    synchronized (imageReader) {
                        result = imageReader.read(imageIndex, imgParam);
                    }
                }
            }

            final WritableRaster wRaster = Raster.createWritableRaster(result.getSampleModel(), result.getRaster().getDataBuffer(), new Point(minRx, minRy));
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
        throw new UnsupportedOperationException(String.format("Image width/height exceed max size (%d/%d).",
                RASTER_MAX_SIZE.width, RASTER_MAX_SIZE.height));
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
        throw new UnsupportedOperationException(String.format("Image width/height exceed max size (%d/%d).",
                RASTER_MAX_SIZE.width, RASTER_MAX_SIZE.height));
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public WritableRaster copyData(WritableRaster raster) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose() {
        tilecache.removeTiles(this);
        if (spi != null && imageReader != null) {
            imageReader.dispose();
            imageReader = null;
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
}
