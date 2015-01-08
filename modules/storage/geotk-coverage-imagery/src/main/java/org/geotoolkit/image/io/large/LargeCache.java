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

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.TileCache;

import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;

/**
 * Manage {@link RenderedImage} and its {@link Raster} to don't exceed JVM memory capacity.
 *
 * TODO : make memory be entirely managed by the cache, instead of allow a portion of memory to each {@link org.geotoolkit.image.io.large.LargeMap}.
 * The aim is to just delegate tile manipulation to them, and get the total control over memory here.
 * Maybe a priority system would be useful to determine which tile to release first (based on the number of times a tile has been queried ?)
 *
 * @author Rémi Maréchal (Geomatys)
 * @author Alexis Manin  (Geomatys)
 */
public class LargeCache implements TileCache {

    private static final Logger LOGGER = Logging.getLogger(LargeCache.class.getName());

    private final ReferenceQueue<RenderedImage> phantomQueue = new ReferenceQueue<RenderedImage>();

    private long memoryCapacity;
    private long remainingCapacity;

    /**
     * References tiles cached over time. Used when we need to free space, we browse it to remove the oldest tiles.
     */
    private final LinkedHashSet<Map.Entry<RenderedImage, Point>> cachedTiles = new LinkedHashSet<>();

    /*
     * Contains a tile manager for each cached rendered image. A tile manager job is to swap / cache image tiles as we ask it.
     */
    private final WeakHashMap<RenderedImage, LargeMap> tileManagers = new WeakHashMap<>();

    private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private static LargeCache INSTANCE;

    private LargeCache(long memoryCapacity) {
        this.memoryCapacity = memoryCapacity;
        final Thread phantomCleaner = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        final LargeMap removed = (LargeMap) phantomQueue.remove();
                        removed.removeTiles();
                        // Re-distribute freed memory amount between remaining caches.
                        final long mC = LargeCache.this.memoryCapacity / (tileManagers.size());
                        updateLList(mC);
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.WARNING, "Reference cleaner has been interrupted ! It could cause severe memory leaks.");
                        return;
                    } catch (Throwable t) {
                        LOGGER.log(Level.WARNING, "An image reference cannot be released. It's likely to cause memory leaks !");
                    }
                }
            }
        });
        phantomCleaner.setDaemon(true);
        phantomCleaner.start();
    }

    /**<p>Construct tile cache mechanic.<br/>
     * Stock Raster while memory capacity does not exceed else write in temporary file.</p>
     *
     * @return TileCache
     */
    public static LargeCache getInstance() {
        long memoryCapacity = ImageCacheConfiguration.getCacheMemorySize();
        if (INSTANCE == null) INSTANCE = new LargeCache(memoryCapacity);
        return INSTANCE;
    }

    /**
     * Return the cache system associated to the given rendered image. If there's no
     * such thing, it will be created / referenced then returned.
     * @param source The image we want data from.
     * @return The found or creeated cache system.
     * @throws IOException If the image did not have any cache system, and we cannot create one.
     */
    private LargeMap getOrCreateLargeMap(RenderedImage source) throws IOException {
        LargeMap lL;
        synchronized (source) {
            cacheLock.readLock().lock();
            try {
                lL = tileManagers.get(source);
            } finally {
                cacheLock.readLock().unlock();
            }
            if (lL == null) {
                // To delete when memory will be managed by tile cache directly.
                final long mC = memoryCapacity / (tileManagers.size() + 1);
                updateLList(mC);
                try {
                    lL = new LargeMap(source, phantomQueue, mC);
                    cacheLock.writeLock().lock();
                    tileManagers.put(source, lL);

                } catch (IOException ex) {
                    throw new RuntimeException("impossible to create cache list", ex);
                } finally {
                    if (cacheLock.isWriteLockedByCurrentThread()) {
                        cacheLock.writeLock().unlock();
                    }
                }
            }
        }
        return lL;
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public void add(RenderedImage ri, int i, int i1, Raster raster) {
        // TODO : check existing tile, flush it before replacing it, or do nothing.
        if (!(raster instanceof WritableRaster)) {
            throw new IllegalArgumentException("raster must be WritableRaster instance");
        }
        final WritableRaster wRaster = (WritableRaster) raster;

        try {
            final LargeMap lL = getOrCreateLargeMap(ri);
            lL.add(i, i1, wRaster);
        } catch (IOException ex) {
            throw new RuntimeException("impossible to add raster (write raster on disk)", ex);
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void remove(RenderedImage ri, int i, int i1) {
        final LargeMap lL;
        cacheLock.readLock().lock();
        try {
            lL = tileManagers.get(ri);
        } finally {
            cacheLock.readLock().unlock();
        }
        if (lL == null)
            throw new IllegalArgumentException("renderedImage don't exist in this "+LargeCache.class.getName());
        lL.remove(i, i1);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Raster getTile(RenderedImage ri, int i, int i1) {
        final LargeMap cache;
        cacheLock.readLock().lock();
        try {
            cache = tileManagers.get(ri);
        } finally {
            cacheLock.readLock().unlock();
        }
        if (cache == null)
            throw new IllegalArgumentException("renderedImage doesn't exist in this "+LargeCache.class.getName());
        try {
            return cache.getRaster(i, i1);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void removeTiles(RenderedImage ri) {
        final LargeMap lL;
        // De-reference image
        cacheLock.writeLock().lock();
        try {
            lL = tileManagers.remove(ri);
            final long mC = memoryCapacity / (tileManagers.size());
            updateLList(mC);
        } finally {
            cacheLock.writeLock().unlock();
        }

        // Clear cache.
        if (lL != null) {
            lL.removeTiles();
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void addTiles(RenderedImage ri, Point[] points, Raster[] rasters, Object o) {
        if (points.length != rasters.length)
            throw new IllegalArgumentException("point and raster tables must have same length.");
        
        final LargeMap lL;
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
        final LargeMap lL;
        cacheLock.readLock().lock();
        try {
            lL = tileManagers.get(ri);
        } finally {
            cacheLock.readLock().unlock();
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
    public synchronized void setMemoryCapacity(long l) {
        cacheLock.writeLock().lock();
        try {
            this.memoryCapacity = l;
            updateLList(memoryCapacity / tileManagers.size());
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    /**
     * Affect a new memory capacity and update {@link Raster} list from new memory capacity set.
     * TODO : delete this method when memory capacity will be entirely managed by {@link org.geotoolkit.image.io.large.LargeCache}.
     * @param listMemoryCapacity new memory capacity.
     */
    private void updateLList(long listMemoryCapacity) {
        for (RenderedImage r : tileManagers.keySet()) {
            try {
                tileManagers.get(r).setCapacity(listMemoryCapacity);
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
