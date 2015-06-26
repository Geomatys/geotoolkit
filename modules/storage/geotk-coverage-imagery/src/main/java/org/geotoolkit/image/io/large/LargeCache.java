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

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.TileCache;

import org.apache.sis.util.logging.Logging;

/**
 * Manage {@link RenderedImage} and its {@link Raster} to don't exceed JVM memory capacity.
 *
 * TODO : make memory be entirely managed by the cache, instead of allow a portion of memory to each {@link org.geotoolkit.image.io.large.ImageTilesCache}.
 * The aim is to just delegate tile manipulation to them, and get the total control over memory here.
 * Maybe a priority system would be useful to determine which tile to release first (based on the number of times a tile has been queried ?)
 *
 * @author Rémi Maréchal (Geomatys)
 * @author Alexis Manin  (Geomatys)
 */
public final class LargeCache implements TileCache {

    private static final Logger LOGGER = Logging.getLogger(LargeCache.class.getName());

    private static final BlockingQueue<Runnable> FLUSH_QUEUE = new LinkedBlockingQueue<>(64);
    static final ThreadPoolExecutor WRITER__EXECUTOR = new ThreadPoolExecutor(1, 4, 5, TimeUnit.MINUTES, FLUSH_QUEUE, new ThreadPoolExecutor.CallerRunsPolicy());
    private final ReferenceQueue<RenderedImage> phantomQueue = new ReferenceQueue<>();

    private volatile long memoryCapacity;
    private final boolean enableSwap;

    /**
     * Contains a tile manager for each cached rendered image. A tile manager job is to swap / cache image tiles as we ask it.
     *
     * Note : we can not use ReentrantReadWriteLock with WeakHashMap because
     * get and iteration methods may modify the content of the map.
     * This is because the weak references are tested when a get occurs.
     */
    private final WeakHashMap<RenderedImage, ImageTilesCache> tileManagers = new WeakHashMap<>();
    //We MUST keep hard references to the largemaps, otherwise the dispose wont be called
    //by the reference queue. check the javadoc for more details.
    private final Set<ImageTilesCache> largemaps = new HashSet<>();

    private static LargeCache INSTANCE;

    private LargeCache(long memoryCapacity, boolean enableSwap) {
        this.memoryCapacity = memoryCapacity;
        this.enableSwap = enableSwap;
        final Thread phantomCleaner = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        final ImageTilesCache removed = (ImageTilesCache) phantomQueue.remove();
                        largemaps.remove(removed);
                        removed.removeTiles();
                        // Re-distribute freed memory amount between remaining caches.
                        updateLList();
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.WARNING, "Reference cleaner has been interrupted ! It could cause severe memory leaks.");
                        return;
                    } catch (Throwable t) {
                        LOGGER.log(Level.WARNING, "An image reference cannot be released. It's likely to cause memory leaks !");
                    }
                }
            }
        });
        phantomCleaner.setName("LargeCache cleaner deamon");
        phantomCleaner.setDaemon(true);
        phantomCleaner.start();
    }

    boolean isEnableSwap() {
        return enableSwap;
    }

    long getCacheSizePerImage(){
        synchronized(tileManagers){
            return memoryCapacity / (tileManagers.size() + 1);
        }
    }

    /**
     * <p>Construct tile cache mechanic.<br/>
     * Stock Raster while memory capacity does not exceed else write in temporary file.</p>
     *
     * @return TileCache
     */
    public static synchronized LargeCache getInstance() {
        if(INSTANCE==null){
            final long memoryCapacity = ImageCacheConfiguration.getCacheMemorySize();
            final boolean enableSwap = ImageCacheConfiguration.isCacheSwapEnable();
            INSTANCE = new LargeCache(memoryCapacity, enableSwap);
        }
        return INSTANCE;
    }

    /**
     * Return the cache system associated to the given rendered image. If there's no
     * such thing, it will be created / referenced then returned.
     * @param source The image we want data from.
     * @return The found or creeated cache system.
     * @throws IOException If the image did not have any cache system, and we cannot create one.
     */
    private ImageTilesCache getOrCreateLargeMap(final RenderedImage source) throws IOException {
        ImageTilesCache lL;
        synchronized (source) {
            synchronized(tileManagers){
                lL = tileManagers.get(source);
            }

            if (lL == null) {
                try {
                    lL = new ImageTilesCache(source, phantomQueue, this);
                } catch (IOException ex) {
                    throw new RuntimeException("impossible to create cache list", ex);
                }

                synchronized(tileManagers){
                    tileManagers.put(source, lL);
                    largemaps.add(lL);
                }
                
                updateLList();
            }
        }
        return lL;
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public void add(RenderedImage ri, int tileX, int tileY, Raster raster) {
        // TODO : check existing tile, flush it before replacing it, or do nothing.
        if (!(raster instanceof WritableRaster)) {
            throw new IllegalArgumentException("raster must be WritableRaster instance");
        }

        try {
            final ImageTilesCache lL = getOrCreateLargeMap(ri);
            lL.add(tileX, tileY, (WritableRaster) raster);
        } catch (IOException ex) {
            throw new RuntimeException("impossible to add raster (write raster on disk)", ex);
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void remove(RenderedImage ri, int tileX, int tileY) {
        final ImageTilesCache lL;
        synchronized(tileManagers){
            lL = tileManagers.get(ri);
        }
        
        if (lL == null){
            throw new IllegalArgumentException("renderedImage don't exist in this "+LargeCache.class.getName());
        }
        lL.remove(tileX, tileY);
    }

    /**
     * {@inheritDoc }.
     * @throws java.lang.IllegalArgumentException if TileCache is in memoryMode only and the
     * requested raster is not found on cache.
     * @throws java.lang.RuntimeException if raster can't be retrieve from cache (nested IOException).
     */
    @Override
    public Raster getTile(RenderedImage ri, int tileX, int tileY) {
        final ImageTilesCache cache;
        synchronized(tileManagers){
            cache = tileManagers.get(ri);
        }
        if (cache == null){
            throw new IllegalArgumentException("renderedImage doesn't exist in this "+LargeCache.class.getName());
        }
        try {
            return cache.getRaster(tileX, tileY);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void removeTiles(RenderedImage ri) {
        final ImageTilesCache lL;
        // De-reference image
        synchronized(tileManagers){
            lL = tileManagers.remove(ri);
        }

        // Clear cache.
        if (lL != null) {
            lL.removeTiles();
            updateLList();
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void addTiles(RenderedImage ri, Point[] points, Raster[] rasters, Object o) {
        if (points.length != rasters.length)
            throw new IllegalArgumentException("point and raster tables must have same length.");
        
        final ImageTilesCache lL;
        try {
            lL = getOrCreateLargeMap(ri);
        } catch (IOException e) {
            throw new RuntimeException("There is no cache system for the given image, and we cannot create any.", e);
        }

        for (int id = 0, l = points.length; id < l; id++) {
            if (!(rasters[id] instanceof WritableRaster))
                throw new IllegalArgumentException("raster must be WritableRaster instance");
            try {
                lL.add(points[id].x, points[id].y, (WritableRaster) rasters[id]);
            } catch (IOException ex) {
                throw new RuntimeException("impossible to add raster (write raster on disk)", ex);
            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Raster[] getTiles(RenderedImage ri, Point[] points) {
        final ImageTilesCache lL;
        synchronized(tileManagers){
            lL = tileManagers.get(ri);
        }
        
        if (lL == null)
            throw new IllegalArgumentException("renderedImage don't exist in this "+LargeCache.class.getName());
        final int l = points.length;
        final Raster[] rasters = new Raster[l];
        for (int id = 0; id < l; id++) {
            try {
                rasters[id] = lL.getRaster(points[id].x, points[id].y);
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Unreadable tile : "+points[id], ex);
            }
        }
        return rasters;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setMemoryCapacity(long l) {
        this.memoryCapacity = l;
        updateLList();
    }

    /**
     * Affect a new memory capacity and update {@link Raster} list from new memory capacity set.
     * TODO : delete this method when memory capacity will be entirely managed by {@link org.geotoolkit.image.io.large.LargeCache}.
     * @param listMemoryCapacity new memory capacity.
     */
    private void updateLList() {
        Object[] array;
        synchronized(tileManagers){
            array = tileManagers.values().toArray();
        }
        for (Object lL : array) {
            try {
                ((ImageTilesCache)lL).capacityChanged();
            } catch (IOException ex) {
                throw new RuntimeException("Raster too large for remaining memory capacity", ex);
            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public long getMemoryCapacity() {
        return memoryCapacity;
    }

    
    /*
     * UNSUPPORTED OPERATIONS
     */
    
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public Raster[] getTiles(RenderedImage ri) {
        throw new UnsupportedOperationException("Not supported yet.");
//        if (!tileManagers.containsKey(ri))
//            throw new IllegalArgumentException("renderedImage don't exist in this "+LargeCache.class.getName());
//        try {
//            return tileManagers.get(ri).getTiles();
//        } catch (IOException ex) {
//            Logger.getLogger(LargeCache.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setMemoryThreshold(float f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public float getMemoryThreshold() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setTileComparator(Comparator cmprtr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Comparator getTileComparator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void flush() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void memoryControl() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    @Deprecated
    public void setTileCapacity(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    @Deprecated
    public int getTileCapacity() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void add(RenderedImage ri, int i, int i1, Raster raster, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
