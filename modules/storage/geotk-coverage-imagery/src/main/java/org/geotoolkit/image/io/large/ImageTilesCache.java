/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2015, Geomatys
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


import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.RasterFactory;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.WeakValueHashMap;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.nio.IOUtilities;

/**
 * Stock all {@link java.awt.image.Raster} contained from define {@link java.awt.image.RenderedImage}. It's a map whose key
 * is tile location, and value is the value the tile data. We use a {@link java.util.LinkedHashMap}, so when we need to
 * remove an element, we will take the oldest one.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Alexis Manin (Geomatys).
 * @author Johann Sorel (Geomatys).
 */
final class ImageTilesCache extends PhantomReference<RenderedImage> {

    /**
     * {@link Logger} to show problem during Tile deletion.
     *
     * @see #checkMap()
     */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.image.io.large");

    private static final Path TEMPORARY_PATH = Paths.get(System.getProperty("java.io.tmpdir"));
    private static final String FORMAT = "geotiff";
    private static final ImageReaderSpi READER_SPI;
    private static final ImageWriterSpi WRITER_SPI;
    static {
        final Iterator<ImageReader> iteR = ImageIO.getImageReadersByFormatName(FORMAT);
        READER_SPI = (iteR.hasNext()) ? iteR.next().getOriginatingProvider() : null;

        final Iterator<ImageWriter> iteW = ImageIO.getImageWritersByFormatName(FORMAT);
        WRITER_SPI = (iteW.hasNext()) ? iteW.next().getOriginatingProvider() : null;
    }
    private static final Point WPOINT = new Point(0, 0);

    private final LargeCache cache;
    private ColorModel cm;
    private final int minTileX;
    private final int minTileY;
    private final int numTilesX;
    private final int numTilesY;
    private final QuadTreeDirectory qTD;
    private final int riMinX;
    private final int riMinY;
    private final int riTileWidth;
    private final int riTileHeight;
    private final int dataTypeWeight;

    private final boolean isWritableRenderedImage;

    /**
     * Tile by Tile locks.
     */
    private final WeakValueHashMap<Point,ReadWriteLock> locks = new WeakValueHashMap<>(Point.class);

    /**
     * Contains tiles of pointed image.
     * TODO : Replace LargeRaster type with simple raster ? (Raster weight will be embed in {@link org.geotoolkit.image.io.large.CachedTile}
     *
     * accesOrder = true : this ensure the must used element are at the top
     * This behavior is used by the flush to remove the oldest used tiles first
     *
     * We subclass the map to keep track of the used memory.
     */
    private final AtomicLong usedCapacity = new AtomicLong(0);
    private final Map<Point, TileRasterCache> tiles = new LinkedHashMap<Point, TileRasterCache>(16, 0.75f, true){
        @Override
        public TileRasterCache put(Point key, TileRasterCache value) {
            final TileRasterCache last = super.put(key, value);
            if(last!=null) usedCapacity.addAndGet(-last.getWeight());
            if(value!=null) usedCapacity.addAndGet(value.getWeight());
            return last;
        }
        @Override
        public void clear() {
            super.clear();
            usedCapacity.set(0);
        }
        @Override
        public TileRasterCache remove(Object key) {
            final TileRasterCache last = super.remove(key);
            if(last!=null) usedCapacity.addAndGet(-last.getWeight());
            return last;
        }
    };


    /**
     * when you use the lock keep it until release
     *
     * @param key
     * @return
     */
    private ReadWriteLock getLock(final Point key){
        ReadWriteLock lock;
        synchronized(locks){
            lock = locks.get(key);
            if (lock == null) {
                lock = new ReentrantReadWriteLock();
                locks.put(key, lock);
            }
        }
        return lock;
    }

    /**
     * <p>List which contain {@link java.awt.image.Raster} from {@link java.awt.image.RenderedImage} owner.<br/>
     * If some of {@link java.awt.image.Raster} weight within list exceed memory capacity, {@link java.awt.image.Raster} are stored
     * on hard disk at appropriate quad tree emplacement in temporary system directory.<br/><br/>
     *
     * Note : {@link java.awt.image.Raster} are stored in tiff format to avoid onerous, compression decompression, cost during disk writing reading.</p>
     *
     * @param ri {@link java.awt.image.RenderedImage} which contain all raster in list.
     * @param memoryCapacity storage capacity in Byte.
     * @param enableSwap flag that enable memory swapping on filesystem.
     * @throws java.io.IOException if impossible to create {@link javax.imageio.ImageReader} or {@link javax.imageio.ImageWriter}.
     */
    ImageTilesCache(RenderedImage ri, ReferenceQueue queue, LargeCache cache) throws IOException {
        super(ri, queue);
        //cache properties.
        this.cache = cache;
        this.isWritableRenderedImage = ri instanceof WritableRenderedImage;

        if (ri instanceof WritableLargeRenderedImage ) {
            if (!cache.isEnableSwap())
                throw new IllegalArgumentException("With WritableRenderedImage LargeCache must swap.");
        }
        //image owner properties.
        this.cm            = ri.getColorModel();
        this.numTilesX     = ri.getNumXTiles();
        this.numTilesY     = ri.getNumYTiles();
        this.riMinX        = ri.getMinX();
        this.riMinY        = ri.getMinY();
        this.riTileWidth   = ri.getTileWidth();
        this.riTileHeight  = ri.getTileHeight();
        this.minTileX      = ri.getMinTileX();
        this.minTileY      = ri.getMinTileY();

        //quad tree directory architecture.
        if (cache.isEnableSwap()) {
            ArgumentChecks.ensureNonNull("READER_SPI", READER_SPI);
            ArgumentChecks.ensureNonNull("WRITER_SPI", WRITER_SPI);
            final Path dirPath = Files.createTempDirectory(TEMPORARY_PATH, "img");
            this.qTD = new QuadTreeDirectory(dirPath, numTilesX, numTilesY, FORMAT, true);
        } else {
            this.qTD = null;
        }

        final int datatype = ri.getSampleModel().getDataType();
        switch (datatype) {
            case DataBuffer.TYPE_BYTE      : dataTypeWeight = 1; break;
            case DataBuffer.TYPE_SHORT     : dataTypeWeight = 2; break;
            case DataBuffer.TYPE_USHORT    : dataTypeWeight = 2; break;
            case DataBuffer.TYPE_INT       : dataTypeWeight = 4; break;
            case DataBuffer.TYPE_FLOAT     : dataTypeWeight = 4; break;
            case DataBuffer.TYPE_DOUBLE    : dataTypeWeight = 8; break;
            case DataBuffer.TYPE_UNDEFINED : dataTypeWeight = 8; break;
            default : throw new IllegalStateException("unknown raster data type");
        }
    }

    /**
     * Get currently used amount of memory, this is just an estimation
     * @return memory used.
     */
    public long getUsedCapacity() {
        return usedCapacity.get();
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
        if (rasterWeight > cache.getCacheSizePerImage()) throw new IOException("Raster too large : " + rasterWeight
                + " bytes, but maximum cache capacity is "+ cache.getCacheSizePerImage() +" bytes");

        final ReadWriteLock tileLock = getLock(tileCorner);
        tileLock.writeLock().lock();
        try {
            synchronized(tiles){
                tiles.put(tileCorner, new TileRasterCache(tileCorner.x, tileCorner.y, rasterWeight, raster));
            }
        } finally {
            tileLock.writeLock().unlock();
        }
        //remove or cache on disk oldest raster
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

        final ReadWriteLock tileLock = getLock(tileCorner);
        tileLock.writeLock().lock();
        try {
            synchronized(tiles){
                tiles.remove(tileCorner);
            }

            if (qTD != null) {
                //quad tree
                final Path removeFile = Paths.get(qTD.getPath(tileCorner.x, tileCorner.y));
                //delete on hard disk if exist.
                try {
                    Files.deleteIfExists(removeFile);
                } catch (IOException e) {
                    //delete failed try to delete it when JVM shutdown
                    LOGGER.log(Level.FINE,"Tile delete failed : "+ e.getLocalizedMessage(), e);
                    IOUtilities.deleteOnExit(removeFile);
                }
            }

        } finally {
            tileLock.writeLock().unlock();
        }

    }

    /**
     * Return {@link java.awt.image.Raster} at tileX tileY mosaic coordinates.
     *
     * @param tileX mosaic index in X direction.
     * @param tileY mosaic index in Y direction.
     * @return Raster at tileX tileY mosaic coordinates.
     * @throws java.io.IOException if an error occurs during reading..
     * @throws IllegalArgumentException if raster not found in memory mode.
     */
    Raster getRaster(int tileX, int tileY) throws IOException, IllegalArgumentException {
        final Point tileCorner = new Point(tileX - minTileX, tileY - minTileY);
        // Check if queried raster is cached.
        final ReadWriteLock tileLock = getLock(tileCorner);
        tileLock.readLock().lock();
        try {
            final TileRasterCache lRaster;
            synchronized(tiles){
                lRaster= tiles.get(tileCorner);
            }
            if (lRaster != null) {
                return lRaster.getRaster();
            }
        } finally {
            tileLock.readLock().unlock();
        }

        if (qTD == null) {
            // raster not found in memory
            throw new IllegalArgumentException("Tile (" + tileX + ", " + tileY + ") not found in memory.");
        } else {

            //-- lock in writing
            tileLock.writeLock().lock();
            try {

                //-- asked again getRaster() in case another thread already enter
                //-- into this scope and has loaded tile from file system.
                final TileRasterCache lRaster;
                synchronized (tiles) {
                    lRaster= tiles.get(tileCorner);
                }
                if (lRaster != null) {
                    return lRaster.getRaster();
                }

                // If not, we must take it from input quad-tree.
                final Path tileFile = Paths.get(qTD.getPath(tileCorner.x, tileCorner.y));
                if (Files.exists(tileFile)) {
                    // TODO : Use a "pool" of readers, instead of creating one each time ?
                    final ImageReader imgReader = READER_SPI.createReaderInstance();
                    final BufferedImage buff;
                    try {
                        imgReader.setInput(tileFile);
                        buff = imgReader.read(0);
                    }catch (Exception ex){
                        throw ex;
                    }finally {
                        imgReader.dispose();
                    }
                    //add in cache list.
                    final WritableRaster checkedRaster = checkRaster(buff.getRaster(), tileCorner);
                    add(tileCorner, checkedRaster);
                    return checkedRaster;
                }
            } finally {
               tileLock.writeLock().unlock();
            }
        }
        throw new IOException("Tile (" + tileX + ", " + tileY + ") unknown. Cannot get raster.");
    }

    /**
     * Remove all file and directory relevant to this cached image.
     */
    void removeTiles() throws IOException {
        //rendered image won't be used after this
        synchronized(tiles){
            tiles.clear();
            if (qTD != null) {
                qTD.cleanDirectory();
            }
        }
    }

    /**
     * Affect a new memory capacity and update {@link java.awt.image.Raster} list from new memory capacity set.
     *
     * @param memoryCapacity new memory capacity.
     * @throws java.io.IOException if capacity is too low from raster weight.
     * @throws java.io.IOException if cache capacity is too low from raster weight, or if impossible to write raster on disk.
     */
    void capacityChanged() throws IOException {
        checkMap();
    }

    /**
     * Define the weight of a {@link java.awt.image.Raster}.
     *
     * @param raster raster which will be weigh.
     * @return raster weight.
     */
    private long getRasterWeight(final Raster raster) {
        final SampleModel rsm = raster.getSampleModel();
        final int width = (rsm instanceof ComponentSampleModel) ? ((ComponentSampleModel) rsm).getScanlineStride() : raster.getWidth()*rsm.getNumDataElements();
        return width * raster.getHeight() * dataTypeWeight;
    }

    /**
     * Write {@link java.awt.image.Raster} within {@link org.geotoolkit.image.io.large.TileRasterCache} object on hard disk at appropriate quad tree emplacement.
     *
     * @param lRaster object which contain raster.
     * @throws java.io.IOException if impossible to write raster on disk.
     */
    private void writeRaster(final TileRasterCache lRaster) throws IOException {
        final Path tileFile = Paths.get(qTD.getPath(lRaster.getGridX(), lRaster.getGridY()));
        if (isWritableRenderedImage || !Files.exists(tileFile)) {
            final BufferedImage toWrite = new BufferedImage(
                    cm, RasterFactory.createWritableRaster(lRaster.getRaster().getSampleModel(), lRaster.getRaster().getDataBuffer(), WPOINT),
                    cm.isAlphaPremultiplied(), null);
            // TODO : Optimize using a "writer pool" instead of creating one each time ?
            final ImageWriter imgWriter = WRITER_SPI.createWriterInstance();
            try {
                imgWriter.setOutput(tileFile);
                imgWriter.write(toWrite);
                imgWriter.dispose();
            } finally {
                releaseWriter(imgWriter);
            }
        }
    }

    /**
     * Release ImageReader and his input.
     * TODO replace with XImageIO utility methods
     * @param imageReader
     */
    private void releaseReader(ImageReader imageReader) {
        if(imageReader != null) {
            Object writerOutput = imageReader.getInput();
            if(writerOutput instanceof OutputStream){
                try {
                    ((OutputStream)writerOutput).close();
                } catch (IOException ex) {
                    LOGGER.log(Level.INFO, ex.getMessage(),ex);
                }
            }else if(writerOutput instanceof ImageOutputStream){
                try {
                    ((ImageOutputStream)writerOutput).close();
                } catch (IOException ex) {
                    LOGGER.log(Level.INFO, ex.getMessage(),ex);
                }
            }
            imageReader.dispose();
        }
    }

    /**
     * Release ImageWriter and his output.
     * TODO replace with XImageIO utility methods
     * @param imgWriter
     */
    private void releaseWriter(ImageWriter imgWriter) {
        if(imgWriter != null) {
            Object writerOutput = imgWriter.getOutput();
            if(writerOutput instanceof OutputStream){
                try {
                    ((OutputStream)writerOutput).close();
                } catch (IOException ex) {
                    LOGGER.log(Level.INFO, ex.getMessage(),ex);
                }
            }else if(writerOutput instanceof ImageOutputStream){
                try {
                    ((ImageOutputStream)writerOutput).close();
                } catch (IOException ex) {
                    LOGGER.log(Level.INFO, ex.getMessage(),ex);
                }
            }
            imgWriter.dispose();
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
     */
    private void checkMap() throws IOException {
        final long maxCacheSize = cache.getCacheSizePerImage();

        final boolean swap = cache.isEnableSwap();

        for(long currentCapacity = usedCapacity.get(); currentCapacity>maxCacheSize; currentCapacity = usedCapacity.get()){

            Point key = null;
            synchronized(tiles){
                //get oldest key
                Iterator<Point> ite = tiles.keySet().iterator();
                if(ite.hasNext()){
                    key = ite.next();
                }
            }
            if(key==null) continue;

            final ReadWriteLock rwl = getLock(key);
            rwl.writeLock().lock();
            try {
                final TileRasterCache tr;
                synchronized (tiles) {
                    tr = tiles.remove(key);
                }

                if (tr != null && swap) {
                    writeRaster(tr);
                }
            } finally {
                rwl.writeLock().unlock();
            }
        }

    }
}
