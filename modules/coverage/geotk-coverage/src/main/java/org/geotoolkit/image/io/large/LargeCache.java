/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.image.io.large;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.TileCache;
import org.geotoolkit.image.io.IllegalImageDimensionException;

/**
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class LargeCache implements TileCache {

    private long memoryCapacity;
    private HashMap<RenderedImage, LargeList> map  = new HashMap<RenderedImage, LargeList>();

    private static LargeCache INSTANCE;

    private LargeCache(long memoryCapacity) {
        this.memoryCapacity = memoryCapacity;
    }

    /**<p>Construct tile cache mechanic.<br/>
     * Stock Raster while memory capacity does not exceed else write in temporary file.</p>
     *
     * @param memoryCapacity memory allocation authorized for all stocked image.
     * @return TileCache
     */
    public static LargeCache getInstance(long memoryCapacity) {
        if (INSTANCE == null) INSTANCE = new LargeCache(memoryCapacity);
        return INSTANCE;
    }

    @Override
    public void add(RenderedImage ri, int i, int i1, Raster raster) {
        if (!(raster instanceof WritableRaster))
            throw new IllegalArgumentException("raster must be WritableRaster instance");
        final WritableRaster wRaster = (WritableRaster) raster;
        LargeList lL;
        if (map.containsKey(ri)) {
            lL = map.get(ri);
        } else {
            final long mC = memoryCapacity/(map.size()+1);
            updateLList(mC);
            lL = new LargeList("img_"+ri.hashCode(), mC, ri.getNumXTiles(), ri.getNumYTiles(), ri.getColorModel());
            map.put(ri, lL);
        }
        try {
            lL.add(i, i1, wRaster);
        } catch (IOException ex) {
            Logger.getLogger(LargeCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void add(RenderedImage ri, int i, int i1, Raster raster, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(RenderedImage ri, int i, int i1) {
        if (!map.containsKey(ri))
            throw new IllegalArgumentException("renderedImage don't exist in this "+LargeCache.class.getName());
        final LargeList lL = map.get(ri);
        lL.remove(i, i1);
    }

    @Override
    public Raster getTile(RenderedImage ri, int i, int i1) {
        if (!map.containsKey(ri))
            throw new IllegalArgumentException("renderedImage don't exist in this "+LargeCache.class.getName());
        try {
            return map.get(ri).getRaster(i, i1);
        } catch (IOException ex) {
            Logger.getLogger(LargeCache.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Raster[] getTiles(RenderedImage ri) {
        if (!map.containsKey(ri))
            throw new IllegalArgumentException("renderedImage don't exist in this "+LargeCache.class.getName());
        try {
            return map.get(ri).getTiles();
        } catch (IOException ex) {
            Logger.getLogger(LargeCache.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void removeTiles(RenderedImage ri) {
        if (map.containsKey(ri)) {
            LargeList lL = map.get(ri);
            lL.removeTiles();
            map.remove(ri);
        }
    }

    @Override
    public void addTiles(RenderedImage ri, Point[] points, Raster[] rasters, Object o) {
        if (points.length != rasters.length)
            throw new IllegalArgumentException("points and rasters tables must have same length.");
        LargeList lL;
        if (map.containsKey(ri)) {
            lL = map.get(ri);
        } else {
            lL = new LargeList("img_"+ri.hashCode(), memoryCapacity/(map.size()+1), ri.getNumXTiles(), ri.getNumYTiles(), ri.getColorModel());
            map.put(ri, lL);
        }
        try {
            for (int id = 0, l = points.length; id < l; id++) {
                if (!(rasters[id] instanceof WritableRaster))
                    throw new IllegalArgumentException("raster must be WritableRaster instance");
                lL.add(points[id].x, points[id].y, (WritableRaster) rasters[id]);
            }
        } catch (IOException ex) {
            Logger.getLogger(LargeCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Raster[] getTiles(RenderedImage ri, Point[] points) {
        if (!map.containsKey(ri))
            throw new IllegalArgumentException("renderedImage don't exist in this "+LargeCache.class.getName());
        final LargeList lL = map.get(ri);
        final int l        = points.length;
        final Raster[] rasters = new Raster[l];
        try {
            for (int id = 0; id < l; id++) {
                rasters[id] = lL.getRaster(points[id].x, points[id].y);
            }
        } catch (IOException ex) {
            Logger.getLogger(LargeCache.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rasters;
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void memoryControl() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Deprecated
    public void setTileCapacity(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Deprecated
    public int getTileCapacity() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMemoryCapacity(long l) {
        this.memoryCapacity = l;
        updateLList(memoryCapacity / map.size());
    }

    private void updateLList(long listMemoryCapacity) {
        try {
            for (RenderedImage r : map.keySet()) map.get(r).setCapacity(listMemoryCapacity);
        } catch (IllegalImageDimensionException ex) {
            Logger.getLogger(LargeCache.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LargeCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public long getMemoryCapacity() {
        return memoryCapacity;
    }

    @Override
    public void setMemoryThreshold(float f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float getMemoryThreshold() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTileComparator(Comparator cmprtr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Comparator getTileComparator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
