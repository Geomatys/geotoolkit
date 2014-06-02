package org.geotoolkit.image.io.large;

import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.io.IllegalImageDimensionException;
import org.geotoolkit.image.io.XImageIO;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.RasterFactory;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stock all {@link java.awt.image.Raster} contained from define {@link java.awt.image.RenderedImage}.
 * It's a map whose key is tile location, and value is the value the tile data. We use a {@link java.util.LinkedHashMap},
 * so when we need to remove an element, we will take the oldest one.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Alexis Manin  (Geomatys).
 */
public class LargeMap {

    private static final Logger LOGGER = Logging.getLogger(LargeMap.class);

    private static final String TEMPORARY_PATH = System.getProperty("java.io.tmpdir");
    private static final String FORMAT = "geotiff";
    private static final Point WPOINT = new Point(0, 0);

    private long memoryCapacity;
    private long remainingCapacity;
    private final int minTileX;
    private final int minTileY;
    private final int numTilesX;
    private final int numTilesY;
    private final QuadTreeDirectory qTD;
    private final String dirPath;
    private final ColorModel cm;
    private final int riMinX;
    private final int riMinY;
    private final int riTileWidth;
    private final int riTileHeight;
    private final int dataTypeWeight;

//    private final ImageReader imgReader;
//    private final ImageWriter imgWriter;
    private final boolean isWritableRenderedImage;

    private final Object flushLock = new Object();
    private final ExecutorService flushService = Executors.newSingleThreadExecutor();

    final ReentrantReadWriteLock tileLock = new ReentrantReadWriteLock();

    private final LinkedHashMap<Point, LargeRaster> tiles = new LinkedHashMap<>();

    /**
     * <p>List which contain {@link java.awt.image.Raster} from {@link java.awt.image.RenderedImage} owner.<br/>
     * If somme of {@link java.awt.image.Raster} weight within list exceed memory capacity, {@link java.awt.image.Raster} are stored
     * on hard disk at appropriate quad tree emplacement in temporary system directory.<br/><br/>
     *
     * Note : {@link java.awt.image.Raster} are stored in tiff format to avoid onerous, compression decompression, cost during disk writing reading.</p>
     *
     * @param ri {@link java.awt.image.RenderedImage} which contain all raster in list.
     * @param memoryCapacity storage capacity in Byte.
     * @throws java.io.IOException if impossible to create {@link javax.imageio.ImageReader} or {@link javax.imageio.ImageWriter}.
     */
    LargeMap(RenderedImage ri, long memoryCapacity) throws IOException {
        //cache properties.
        this.memoryCapacity = memoryCapacity;
        isWritableRenderedImage = ri instanceof WritableRenderedImage;
        //image owner properties.
        this.cm                = ri.getColorModel();
        this.remainingCapacity = memoryCapacity;
        this.numTilesX         = ri.getNumXTiles();
        this.numTilesY         = ri.getNumYTiles();
        this.riMinX            = ri.getMinX();
        this.riMinY            = ri.getMinY();
        this.riTileWidth       = ri.getTileWidth();
        this.riTileHeight      = ri.getTileHeight();
        this.minTileX      = ri.getMinTileX();
        this.minTileY      = ri.getMinTileY();

        //quad tree directory architecture.
        this.dirPath = TEMPORARY_PATH + "/img_"+ri.hashCode();
        this.qTD     = new QuadTreeDirectory(dirPath, numTilesX, numTilesY, FORMAT, true);

        //reader writer
//        this.imgReader = XImageIO.getReaderByFormatName(FORMAT, null, Boolean.FALSE, Boolean.TRUE);
//        this.imgWriter = XImageIO.getWriterByFormatName(FORMAT, null, null);

        final int datatype = cm.createCompatibleSampleModel(riTileWidth, riTileHeight).getDataType();
        switch (datatype) {
            case DataBuffer.TYPE_BYTE      : dataTypeWeight = 1; break;
            case DataBuffer.TYPE_DOUBLE    : dataTypeWeight = 8; break;
            case DataBuffer.TYPE_FLOAT     : dataTypeWeight = 4; break;
            case DataBuffer.TYPE_INT       : dataTypeWeight = 4; break;
            case DataBuffer.TYPE_SHORT     : dataTypeWeight = 2; break;
            case DataBuffer.TYPE_UNDEFINED : dataTypeWeight = 8; break;
            case DataBuffer.TYPE_USHORT    : dataTypeWeight = 2; break;
            default : throw new IllegalStateException("unknown raster data type");
        }

        flushService.submit(new FlushWorker());
    }


    /**
     * Add a {@link java.awt.image.Raster} in list and check list to don't exceed memory capacity.
     *
     * @param tileX mosaic index in X direction of raster will be stocked.
     * @param tileY mosaic index in Y direction of raster will be stocked.
     * @param raster raster will be stocked in list.
     * @throws java.io.IOException if an error occurs during writing.
     */
    void add(int tileX, int tileY, WritableRaster raster) throws IOException {
        final Point tileCorner = new Point(tileX - minTileX, tileY - minTileY);
        add(tileCorner, checkRaster(raster, tileCorner));
    }

    private void add(Point tileCorner, WritableRaster raster) throws IOException {
        final long rasterWeight = getRasterWeight(raster);
        if (rasterWeight > memoryCapacity) throw new IllegalImageDimensionException("raster too large");

        try {
            tileLock.writeLock().lock();
            final LargeRaster lr = tiles.remove(tileCorner);
            if (lr != null) {
                remainingCapacity += lr.getWeight();
            }
            remainingCapacity -= rasterWeight;
            tiles.put(tileCorner, new LargeRaster(tileCorner.x, tileCorner.y, rasterWeight, raster));
        } finally {
            tileLock.writeLock().unlock();
        }
        checkMap();
    }

    /**
     * Remove {@link java.awt.image.Raster} at tileX tileY mosaic coordinates.
     *
     * @param tileX mosaic index in X direction.
     * @param tileY mosaic index in Y direction.
     */
    void remove(int tileX, int tileY) {
        final Point tileCorner = new Point(tileX - minTileX, tileY - minTileY);

        tileLock.writeLock().lock();
        try {
            tiles.remove(tileCorner);
        } finally {
            tileLock.writeLock().unlock();
        }

        //quad tree
        final File removeFile = new File(qTD.getPath(tileCorner.x, tileCorner.y));
        //delete on hard disk if exist.
        if (removeFile.exists()) removeFile.delete();
    }

    /**
     * Return {@link java.awt.image.Raster} at tileX tileY mosaic coordinates.
     *
     * @param tileX mosaic index in X direction.
     * @param tileY mosaic index in Y direction.
     * @return Raster at tileX tileY mosaic coordinates.
     * @throws java.io.IOException if an error occurs during reading..
     */
    Raster getRaster(int tileX, int tileY) throws IOException {
        final Point tileCorner = new Point(tileX - minTileX, tileY - minTileY);
        // Check if queried raster is cached.
        try {
            tileLock.readLock().lock();
            final LargeRaster lRaster = tiles.get(tileCorner);
            if (lRaster != null) {
                return lRaster.getRaster();
            }
        } finally {
            tileLock.readLock().unlock();
        }
        
        // If not, we must take it from input quad-tree.
        final File getFile = new File(qTD.getPath(tileCorner.x, tileCorner.y));
        if (getFile.exists()) {
            // TODO : Use a "pool" of readers, instead of creating one each time ?
            final ImageReader imgReader = XImageIO.getReaderByFormatName(FORMAT, null, Boolean.FALSE, Boolean.TRUE);
            imgReader.setInput(getFile);
            final BufferedImage buff = imgReader.read(0);
            imgReader.setInput(null);
            imgReader.dispose();
            //add in cache list.
            final WritableRaster checkedRaster = checkRaster(buff.getRaster(), tileCorner);
            add(tileCorner, checkedRaster);
            return checkedRaster;
        }

        throw new IOException("Tile (" + tileX + ", " + tileY + ") unknown. Cannot get raster.");
    }

    /**
     * Return all {@link java.awt.image.Raster} within this cache system.
     *
     * @return all raster within this cache system.
     * @throws java.io.IOException if impossible to read raster from disk.
     */
    Raster[] getTiles() throws IOException {
        int id = 0;
        final Raster[] rasters = new Raster[numTilesX * numTilesY];
        for (int ty = minTileY, tmy = minTileY+ numTilesY; ty < tmy; ty++) {
            for (int tx = minTileX, tmx = minTileX+ numTilesX; tx < tmx; tx++) {
                rasters[id++] = getRaster(tx, ty);
            }
        }
        return rasters;
    }

    /**
     * Remove all file and directory relevant to this cached image.
     */
    void removeTiles() {
        flushService.shutdownNow();
        try {
            tileLock.writeLock().lock();
            remainingCapacity = memoryCapacity;
            tiles.clear();
        } finally {
            tileLock.writeLock().unlock();
        }
    }

    /**
     * Affect a new memory capacity and update {@link java.awt.image.Raster} list from new memory capacity set.
     *
     * @param memoryCapacity new memory capacity.
     * @throws org.geotoolkit.image.io.IllegalImageDimensionException if capacity is too low from raster weight.
     * @throws java.io.IOException if impossible to write raster on disk.
     */
    void setCapacity(long memoryCapacity) throws IOException {
        ArgumentChecks.ensurePositive("LargeMap : memory capacity", memoryCapacity);
        try {
            tileLock.writeLock().lock();
            final long diff = this.memoryCapacity - memoryCapacity;
            remainingCapacity -= diff;
            this.memoryCapacity = memoryCapacity;
            checkMap();
        } finally {
            tileLock.writeLock().unlock();
        }
    }

    /**
     * Define the weight of a {@link java.awt.image.Raster}.
     *
     * @param raster raster which will be weigh.
     * @return raster weight.
     */
    private long getRasterWeight(Raster raster) {
        final SampleModel rsm = raster.getSampleModel();
        final int width = (rsm instanceof ComponentSampleModel) ? ((ComponentSampleModel) rsm).getScanlineStride() : raster.getWidth()*rsm.getNumDataElements();
        return width * raster.getHeight() * dataTypeWeight;
    }

    /**
     * Write {@link java.awt.image.Raster} within {@link org.geotoolkit.image.io.large.LargeRaster} object on hard disk at appropriate quad tree emplacement.
     *
     * @param lRaster object which contain raster.
     * @throws java.io.IOException if impossible to write raster on disk.
     */
    private void writeRaster(LargeRaster lRaster) throws IOException {
        final File tileFile = new File(qTD.getPath(lRaster.getGridX(), lRaster.getGridY()));
        if (!tileFile.exists() || isWritableRenderedImage) {
            final BufferedImage toWrite = new BufferedImage(
                    cm, RasterFactory.createWritableRaster(lRaster.getRaster().getSampleModel(), lRaster.getRaster().getDataBuffer(), WPOINT), true, null);
            // TODO : Optimize using a "writer pool" instead of creating one each time ?
            final ImageWriter imgWriter = XImageIO.getWriterByFormatName(FORMAT, null, null);
            imgWriter.setOutput(tileFile);
            imgWriter.write(toWrite);
            imgWriter.setOutput(null);
            imgWriter.dispose();
        }
    }

    /**
     * Clean all subDirectory of given folder.
     *
     * @param parentDirectory directory which will be cleaned.
     */
    public static void cleanDirectory(File parentDirectory) {
        for (File file : parentDirectory.listFiles()) {
            if (file.isDirectory()) cleanDirectory(file);
            file.delete();
        }
    }

    /**
     * <p>Verify that {@link java.awt.image.Raster} coordinate is agree from {@link java.awt.image.RenderedImage} location.<br/>
     * If location is correct return {@link java.awt.image.Raster} else return new {@link java.awt.image.Raster} with correct<br/>
     * location but with same internal value from {@link java.awt.image.Raster}.</p>
     *
     * @param raster raster will be checked.
     * @param tileCorner tile location within renderedImage owner.
     * @return raster with correct coordinate from its image owner.
     */
    private WritableRaster checkRaster(WritableRaster raster, Point tileCorner) {
        final int mx = riTileWidth  * tileCorner.x + riMinX;
        final int my = riTileHeight * tileCorner.y + riMinY;
        if (raster.getMinX() != mx || raster.getMinY() != my) {
            return Raster.createWritableRaster(raster.getSampleModel(), raster.getDataBuffer(), new Point(mx, my));
        }
        return raster;
    }

    /**
     * <p>Check that cache weight do not exceed memory capacity.<br/>
     * If memory capacity is exceeded, write as many {@link java.awt.image.Raster} objects needed to not exceed memory capacity anymore.</p>
     *
     * @throws org.geotoolkit.image.io.IllegalImageDimensionException if raster too large for this Tilecache.
     * @throws java.io.IOException                                    if impossible to write raster.
     */
    private void checkMap() {
        synchronized (flushLock) {
            flushLock.notifyAll();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        removeTiles();
        super.finalize();
    }


    /**
     * A thread which will be in charge of memory cleaning. To do so, it will flush old tiles in a temporary quad-tree on file-system.
     */
    private class FlushWorker implements Runnable {

        @Override
        public void run() {
            final Thread currentThread = Thread.currentThread();
            final LinkedList<LargeRaster> toFlush = new LinkedList<>();
            
            while (!currentThread.isInterrupted()) {
                // If nothing to do, just wait.
                synchronized (flushLock) {
                    while (remainingCapacity > 0) {
                        try {
                            flushLock.wait();
                        } catch (InterruptedException e) {
                            LOGGER.log(Level.WARNING, "Flushing process has been canceled !", e);
                            Thread.currentThread().interrupt();
                        }
                    }
                }

                // While the cache size is exceeded, we flush tiles, beginning with the oldest one.
                tileLock.writeLock().lock();
                tileLock.readLock().lock();
                try {
                    final Iterator<Point> tileIterator = tiles.keySet().iterator();
                    while (remainingCapacity < 0 && tileIterator.hasNext() && !currentThread.isInterrupted()) {
                        final Point tileCorner = tileIterator.next();
                        final LargeRaster largeRaster = tiles.get(tileCorner);
                        if (largeRaster != null) {
                                remainingCapacity += largeRaster.getWeight();
                                tileIterator.remove();
                                toFlush.add(largeRaster);
                        }
                    }
                    
                    // We've de-referenced tiles, now we can flush them without blocking other threads from reading this cache.
                    tileLock.writeLock().unlock();
                    while (!toFlush.isEmpty()) {
                        try {
                            writeRaster(toFlush.poll());
                        } catch (IOException e) {
                            // If flush operation fails, it's not a severe error, cache will miss the tile, so source image will need to reload it.
                            LOGGER.log(Level.WARNING, "Tile cannot be flushed, it will be lost !", e);
                        }
                    }
                    toFlush.clear();
                    
                    if (remainingCapacity < 0) {
                        LOGGER.log(Level.WARNING, "Error raised !");
                        throw new IllegalStateException("No tile available for flushing, but cache size has been exceeded.");
                    }
                } finally {
                    if (tileLock.isWriteLockedByCurrentThread()) {
                        tileLock.writeLock().unlock();
                    }
                    tileLock.readLock().unlock();
                }
            }
            LOGGER.log(Level.INFO, "Flush worker finished.");
        }
    }
}
