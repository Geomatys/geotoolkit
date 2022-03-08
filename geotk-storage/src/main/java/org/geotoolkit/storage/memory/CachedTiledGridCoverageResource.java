/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.storage.memory;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.util.collection.Cache;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.internal.Threads;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.process.Monitor;
import org.geotoolkit.storage.AbstractResource;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.coverage.TileMatrixSetCoverageReader;
import org.geotoolkit.storage.event.ModelEvent;
import org.geotoolkit.storage.event.StorageListener;
import org.geotoolkit.storage.multires.AbstractTileMatrix;
import org.geotoolkit.storage.multires.AbstractTileMatrixSet;
import org.geotoolkit.storage.multires.Tile;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.TiledResource;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * This resource acts as an in memory cache for pyramid tiles.
 *
 * Note : current implementation store tiles in memory
 * Todo : add configuration to store tiles on files for offline needs
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachedTiledGridCoverageResource <T extends TiledResource & org.apache.sis.storage.GridCoverageResource> extends AbstractGridResource implements TiledResource, GridCoverageResource {

    private static final BlockingQueue IMAGEQUEUE = new PriorityBlockingQueue(Runtime.getRuntime().availableProcessors()*200);
    private static final ThreadPoolExecutor EXEC;
    static {
        final int nbLoader = Runtime.getRuntime().availableProcessors();
        EXEC = new ThreadPoolExecutor(
            nbLoader, nbLoader, 1, TimeUnit.MINUTES, IMAGEQUEUE,
            Threads.createThreadFactory("Cached pyramid tile loader thread "),
            new ThreadPoolExecutor.CallerRunsPolicy());
        EXEC.prestartAllCoreThreads();
    }


    private final T parent;
    private GenericName identifier;
    private final Map<String,CacheTileMatrixSet> cacheMap = new HashMap<>();
    private final Cache<String,CacheTile> tiles;
    private final Set<String> tilesInProcess = ConcurrentHashMap.newKeySet();
    private final boolean noblocking;

    /**
     * Listener on parent resource.
     * Any event will cause the cache to be cleared.
     */
    private final StoreListener<StoreEvent> eventListener = new StoreListener<StoreEvent>() {
        @Override
        public void eventOccured(StoreEvent event) {
            cacheMap.clear();
            tiles.clear();
            fire(new ModelEvent(CachedTiledGridCoverageResource.this), StoreEvent.class);
        }
    };
    private final StorageListener.Weak weakListener = new StorageListener.Weak(eventListener);

    /**
     * Creates a new cache using the given initial capacity and cost limit. The initial capacity
     * is the expected number of values to be stored in this cache. More values are allowed, but
     * a little bit of CPU time may be saved if the expected capacity is known before the cache
     * is created.
     *
     * <p>The <cite>cost limit</cite> is the maximal value of the <cite>total cost</cite> (the sum
     * of the {@linkplain #cost cost} of all values) before to replace eldest strong references by
     * {@linkplain Reference weak or soft references}.</p>
     *
     * @param parent           resource to cache tiles
     * @param initialCapacity  the initial tile cache capacity.
     * @param costLimit        the maximum cost of tiles to keep by strong reference.
     * @param soft             if {@code true}, use {@link SoftReference} instead of {@link WeakReference}.
     */
    public CachedTiledGridCoverageResource(T parent, int initialCapacity, final long costLimit, final boolean soft) {
        this(parent, initialCapacity, costLimit, soft, false);
    }

    /**
     * Creates a new cache using the given initial capacity and cost limit. The initial capacity
     * is the expected number of values to be stored in this cache. More values are allowed, but
     * a little bit of CPU time may be saved if the expected capacity is known before the cache
     * is created.
     *
     * <p>The <cite>cost limit</cite> is the maximal value of the <cite>total cost</cite> (the sum
     * of the {@linkplain #cost cost} of all values) before to replace eldest strong references by
     * {@linkplain Reference weak or soft references}.</p>
     *
     * @param parent           resource to cache tiles
     * @param initialCapacity  the initial tile cache capacity.
     * @param costLimit        the maximum cost of tiles to keep by strong reference.
     * @param soft             if {@code true}, use {@link SoftReference} instead of {@link WeakReference}.
     * @param noblocking       if {@code true}, only cached tiles are returned right away,
     *  if the tile is not available, loading starts and it will be available later.
     */
    public CachedTiledGridCoverageResource(T parent, int initialCapacity, final long costLimit, final boolean soft, boolean noblocking) {
        super(null);
        this.parent = parent;
        this.tiles = new Cache<String, CacheTile>(initialCapacity, costLimit, soft);
        this.tiles.setKeyCollisionAllowed(true);
        this.noblocking = noblocking;

        this.parent.addListener(StoreEvent.class, weakListener);
    }

    public void setIdentifier(GenericName identifier) {
        this.identifier = identifier;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        if (identifier != null) {
            return Optional.ofNullable(identifier);
        }
        return parent.getIdentifier();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Collection<TileMatrixSet> getTileMatrixSets() throws DataStoreException {
        final Collection<TileMatrixSet> parentPyramids = (Collection<TileMatrixSet>) parent.getTileMatrixSets();

        final List<TileMatrixSet> pyramids;
        synchronized (cacheMap) {
            //check cached pyramids, we need to do this until an event system is created

            //add missing pyramids in the cache view
            final Set<String> keys = new HashSet<>();
            for (TileMatrixSet candidate : parentPyramids) {
                if (!cacheMap.containsKey(candidate.getIdentifier())) {
                    cacheMap.put(candidate.getIdentifier(), new CacheTileMatrixSet(candidate));
                }
                keys.add(candidate.getIdentifier());
            }
            if (cacheMap.size() != parentPyramids.size()) {
                //some pyramids have been deleted from parent
                cacheMap.keySet().retainAll(keys);
            }
            pyramids = UnmodifiableArrayList.wrap(cacheMap.values().toArray(new TileMatrixSet[0]));
        }

        return pyramids;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return parent.getSampleDimensions();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return parent.getGridGeometry();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public TileMatrixSet createTileMatrixSet(TileMatrixSet template) throws DataStoreException {
        synchronized (cacheMap) {
            final TileMatrixSet newParentPyramid = parent.createTileMatrixSet(template);
            final CacheTileMatrixSet cached = new CacheTileMatrixSet(newParentPyramid);
            cacheMap.put(cached.getIdentifier(), cached);
            return cached;
        }
    }

    @Override
    public void removeTileMatrixSet(String identifier) throws DataStoreException {
        synchronized (cacheMap) {
            parent.removeTileMatrixSet(identifier);
            cacheMap.remove(identifier);
        }
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return new TileMatrixSetCoverageReader<>(this).read(domain, range);
    }

    /**
     * Erase tiles in cache.
     */
    @Override
    public void clearCache() {
        tiles.clear();
    }

    private class CacheTileMatrixSet implements TileMatrixSet {

        private final TileMatrixSet parent;
        private final Map<String,CacheTileMatrix> cacheMap = new HashMap<>();

        public CacheTileMatrixSet(TileMatrixSet parent) {
            this.parent = parent;
        }

        @Override
        public Collection<? extends TileMatrix> getTileMatrices() {
            final Collection<? extends TileMatrix> parentMosaics = parent.getTileMatrices();

            //check cached mosaics, we need to do this until an event system is created

            //add missing mosaics in the cache view
            final List<TileMatrix> mosaics;
            synchronized (cacheMap) {
                final Set<String> keys = new HashSet<>();
                for (TileMatrix m : parentMosaics) {
                    if (!cacheMap.containsKey(m.getIdentifier())) {
                        cacheMap.put(m.getIdentifier(), new CacheTileMatrix(this, parent.getIdentifier(), m));
                    }
                    keys.add(m.getIdentifier());
                }
                if (cacheMap.size() != parentMosaics.size()) {
                    //some mosaics have been deleted from parent
                    cacheMap.keySet().retainAll(keys);
                }
                mosaics = UnmodifiableArrayList.wrap(cacheMap.values().toArray(new TileMatrix[0]));
            }

            return mosaics;
        }

        @Override
        public TileMatrix createTileMatrix(TileMatrix template) throws DataStoreException {
            synchronized (cacheMap) {
                final TileMatrix newParentMosaic = parent.createTileMatrix(template);
                final CacheTileMatrix cached = new CacheTileMatrix(this, parent.getIdentifier(), newParentMosaic);
                cacheMap.put(cached.getIdentifier(), cached);
                return cached;
            }
        }

        @Override
        public void deleteTileMatrix(String mosaicId) throws DataStoreException {
            synchronized (cacheMap) {
                parent.deleteTileMatrix(mosaicId);
                cacheMap.remove(mosaicId);
            }
        }

        @Override
        public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            return parent.getCoordinateReferenceSystem();
        }

        @Override
        public Envelope getEnvelope() {
            return parent.getEnvelope();
        }

        @Override
        public String getIdentifier() {
            return parent.getIdentifier();
        }

        @Override
        public String getFormat() {
            return parent.getFormat();
        }

        @Override
        public String toString(){
            return AbstractTileMatrixSet.toString(this);
        }
    }

    private class CacheTileMatrix implements TileMatrix {

        private final CacheTileMatrixSet pyramid;
        private final String baseid;
        private final TileMatrix parent;

        public CacheTileMatrix(CacheTileMatrixSet pyramid, String pyramidId, TileMatrix parent) {
            this.pyramid = pyramid;
            this.parent = parent;
            this.baseid = pyramidId + "¤" + parent.getIdentifier() +"¤";
        }

        @Override
        public String getIdentifier() {
            return parent.getIdentifier();
        }

        @Override
        public DirectPosition getUpperLeftCorner() {
            return parent.getUpperLeftCorner();
        }

        @Override
        public Dimension getGridSize() {
            return parent.getGridSize();
        }

        @Override
        public double getScale() {
            return parent.getScale();
        }

        @Override
        public Dimension getTileSize() {
            return parent.getTileSize();
        }

        @Override
        public GridExtent getDataExtent() {
            return parent.getDataExtent();
        }

        @Override
        public void writeTiles(Stream<Tile> tiles, Monitor monitor) throws DataStoreException {
            parent.writeTiles(tiles, monitor);
        }

        private String tileId(long col, long row) {
            return new StringBuilder(baseid).append(col).append("¤").append(row).toString();
        }

        @Override
        public boolean isMissing(long col, long row) {
            CacheTile tile = tiles.peek(tileId(col, row));
            if (tile != null) return false;
            return parent.isMissing(col, row);
        }

        private boolean isInCache(long col, long row) {
            return tiles.peek(tileId(col, row)) != null;
        }

        @Override
        public CacheTile getTile(final long col, final long row, final Map hints) throws DataStoreException {
            final String key = tileId(col, row);

            CacheTile value = tiles.peek(key);
            if (value == null) {
                if (noblocking) {
                    if (tilesInProcess.add(key)) {
                        //loadUpperTile(col, row);
                        IMAGEQUEUE.add(new TileLoader(CacheTileMatrix.this, col, row, hints));
                    }
                } else {
                    value = loadTile(col, row, hints);
                }
            }
            return value;
        }

        private CacheTile loadTile(long col, long row, Map hints) throws DataStoreException {
            final String key = tileId(col, row);
            CacheTile value = null;
            final Cache.Handler<CacheTile> handler = tiles.lock(key);
            try {
                value = handler.peek();
                if (value == null || !value.finalResult) {
                    final Tile parentTile = parent.getTile(col, row, hints);
                    if (parentTile instanceof ImageTile) {
                        final ImageTile pt = (ImageTile) parentTile;
                        final RenderedImage image;
                        try {
                            image = pt.getImage();
                        } catch (IOException ex) {
                            throw new DataStoreException(ex.getMessage(), ex);
                        }
                        if (value != null) {
                            value.setImage(image, true);
                        } else {
                            final Point coord = new Point(Math.toIntExact(col), Math.toIntExact(row));
                            value = new CacheTile(image, coord, true);
                        }
                    } else {
                        throw new DataStoreException("Unsuppported tile instance "+ parentTile);
                    }
                }
            } finally {
                handler.putAndUnlock(value);
                tilesInProcess.remove(key);
            }
            return value;
        }

        /**
         * Following is an experimental tile generation from available upper level tiles.
         * Should be nice to reactive when coverage and mosaic API has moved to SIS.
         */
//        private void loadUpperTile(long col, long row) throws DataStoreException {
//
//            //find closest mosaic with a lower resolution
//            CacheMosaic candidate = null;
//            for (CacheMosaic m : (Collection<CacheMosaic>) pyramid.getMosaics()) {
//                if (m.getScale() > parent.getScale()) {
//                    if (candidate == null) {
//                        candidate = m;
//                    } else if (m.getScale() < candidate.getScale()) {
//                        candidate = m;
//                    }
//                }
//            }
//
//            if (candidate == null) return;
//
//            //compute wanted tile grid geometry
//            final Point coord = new Point(Math.toIntExact(col), Math.toIntExact(row));
//            final CoordinateReferenceSystem crs = pyramid.getCoordinateReferenceSystem();
//            final List<SampleDimension> sds = getSampleDimensions();
//            final GridGeometry tileGridGeometry = Pyramids.getTileGridGeometry2D(this, coord, crs);
//
//            //check parent tiles are available in the cache
//            final Rectangle rectangle = Pyramids.getTilesInEnvelope(candidate, tileGridGeometry.getEnvelope());
//            for (int x=0;x<rectangle.width;x++) {
//                for (int y=0;y<rectangle.height;y++) {
//                    if (!candidate.isInCache(rectangle.x+x, rectangle.y+y)) {
//                        return;
//                    }
//                }
//            }
//
//            //compute candidate mosaic
//            final MosaicImage image = new MosaicImage(candidate, rectangle, sds);
//            final GridGeometry aboveGridGeometry = Pyramids.getTileGridGeometry2D(candidate, rectangle, crs);
//            final GridCoverage coverage = new GridCoverage2D(aboveGridGeometry, sds, image);
//
//            //resample tile
//            CacheTile tile;
//            try {
//                BufferedImage img = BufferedImages.createImage(image, null, null, null, null);
//                MosaicedCoverageResource.resample(coverage, image, InterpolationCase.NEIGHBOR, tileGridGeometry, img);
//                tile = new CacheTile(img, coord, false);
//            } catch (TransformException | FactoryException ex) {
//                throw new DataStoreException(ex.getMessage(), ex);
//            }
//
//            final String key = tileId(col, row);
//            CacheTile value = null;
//            final Cache.Handler<CacheTile> handler = tiles.lock(key);
//            try {
//                value = handler.peek();
//                if (value == null) {
//                    value = tile;
//                }
//            } finally {
//                handler.putAndUnlock(value);
//            }
//        }

        @Override
        public void deleteTile(int tileX, int tileY) throws DataStoreException {
            parent.deleteTile(tileX, tileY);
            tiles.remove(tileId(tileX, tileY));
        }

        @Override
        public Tile anyTile() throws DataStoreException {
            Iterator<Map.Entry<String, CacheTile>> ite = tiles.entrySet().iterator();
            if (ite.hasNext()) {
                CacheTile value = ite.next().getValue();
                return value;
            }
            return parent.anyTile();
        }

        @Override
        public String toString() {
            return AbstractTileMatrix.toString(this);
        }
    }

    private final class CacheTile extends AbstractResource implements ImageTile{

        private RenderedImage input;
        private Point position;
        private boolean finalResult;

        public CacheTile(RenderedImage image, Point position, boolean finalResult) {
            this.input = image;
            this.position = position;
            this.finalResult = finalResult;
        }

        private synchronized void setImage(RenderedImage image, boolean finalResult) {
            if (this.finalResult) return;
            input = image;
            this.finalResult = finalResult;
        }

        @Override
        public ImageReader getImageReader() throws IOException {
            ImageReaderSpi spi = IImageReader.IISpi.INSTANCE;
            ImageReader reader = null;

            Object in = null;
            try {
                in = XImageIO.toSupportedInput(spi, input);
                reader = spi.createReaderInstance();
                reader.setInput(in, true, true);
            } catch (IOException | RuntimeException e) {
                try {
                    IOUtilities.close(in);
                } catch (IOException ex) {
                    e.addSuppressed(ex);
                }
                if (reader != null) {
                    try {
                        XImageIO.dispose(reader);
                    } catch (Exception ex) {
                        e.addSuppressed(ex);
                    }
                }
                throw e;
            }
            return reader;
        }

        @Override
        public ImageReaderSpi getImageReaderSpi() {
            return IImageReader.IISpi.INSTANCE;
        }

        @Override
        public RenderedImage getInput() {
            return input;
        }

        @Override
        public int getImageIndex() {
            return 0;
        }

        @Override
        public Point getPosition() {
            return position;
        }
    }

    private class TileLoader implements Runnable, Comparable<TileLoader> {

        private final long creationTime;
        private final CacheTileMatrix mosaic;
        private final long col;
        private final long row;
        private final Map hints;

        private TileLoader(CacheTileMatrix mosaic, long col, long row, Map hints) {
            this.mosaic = mosaic;
            this.col = col;
            this.row = row;
            this.hints = hints;
            this.creationTime = System.nanoTime();
        }

        @Override
        public void run() {
            try {
                mosaic.loadTile(col, row, hints);
            } catch (DataStoreException ex) {
                Logger.getLogger("org.geotoolkit.storage").log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        @Override
        public int compareTo(TileLoader o) {
            return Long.compare(creationTime, o.creationTime);
        }

    }
}
