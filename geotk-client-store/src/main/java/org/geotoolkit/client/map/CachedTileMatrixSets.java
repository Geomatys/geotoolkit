/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.client.map;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.TileMatrixSet;
import org.apache.sis.util.collection.Cache;
import org.geotoolkit.client.Client;
import org.geotoolkit.client.Request;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.storage.coverage.*;
import org.geotoolkit.storage.multires.TileMatrices;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class CachedTileMatrixSets extends DefaultTileMatrixSets {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.client.map");

    /**
     * Cache the last queried tiles
     */
    private final Cache<String, RenderedImage> tileCache;
    protected final Client server;
    protected final boolean useURLQueries;
    protected final boolean cacheImages;

    public CachedTileMatrixSets(Client server, boolean useURLQueries, boolean cacheImages) {
        this.server = server;
        this.useURLQueries = useURLQueries;
        this.cacheImages = cacheImages;
        if (cacheImages) {
            tileCache = new Cache<String, RenderedImage>(30, 30, false);
        } else {
            tileCache = null;
        }
    }

    protected Client getServer() {
        return server;
    }

    public abstract Request getTileRequest(TileMatrixSet pyramid, TileMatrix mosaic, long[] indices, Map hints) throws DataStoreException;

    public Optional<Tile> getTile(TileMatrixSet pyramid, TileMatrix mosaic, long[] indices, Map hints) throws DataStoreException {
        final String formatmime = (hints == null) ? null : (String) hints.get(TileMatrices.HINT_FORMAT);
        ImageReaderSpi spi = null;
        if (formatmime != null) {
            try {
                spi = XImageIO.getReaderByMIMEType(formatmime, null, false, false).getOriginatingProvider();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        if (cacheImages) {
            return Optional.of(new DefaultImageTile(mosaic, spi, getTileImage(pyramid, mosaic, indices, hints), 0, indices));
        } else {
            return Optional.of(new RequestImageTile(mosaic, spi, getTileRequest(pyramid, mosaic, indices, hints), 0, indices));
        }
    }

    private static String toId(TileMatrixSet pyramid, TileMatrix mosaic, long... indices) {
        final String pyramidId = pyramid.getIdentifier().toString();
        final String mosaicId = mosaic.getIdentifier().toString();

        final StringBuilder sb = new StringBuilder(pyramidId).append('_').append(mosaicId).append('_').append(indices[0]).append('_').append(indices[1]);

        return sb.toString();
    }

    private RenderedImage getTileImage(TileMatrixSet pyramid, TileMatrix mosaic, long[] indices, Map hints) throws DataStoreException {

        final String tileId = toId(pyramid, mosaic, indices);

        //use the cache if available
        RenderedImage value = tileCache.peek(tileId);
        if (value == null) {
            Cache.Handler<RenderedImage> handler = tileCache.lock(tileId);
            try {
                value = handler.peek();
                if (value == null) {
                    final Request request = getTileRequest(pyramid, mosaic, indices, hints);
                    InputStream stream = null;
                    ImageInputStream iis = null;
                    try {
                        stream = request.getResponseStream();
                        iis = new MemoryCacheImageInputStream(stream);
                        value = ImageIO.read(iis);
                    } catch (IOException ex) {
                        LOGGER.log(Level.INFO, ex.getMessage());
                    } finally {
                        if(iis != null && value == null){
                            try {
                                iis.close();
                            } catch (IOException ex) {
                                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                            }
                        }
                        if(stream != null){
                            try {
                                stream.close();
                            } catch (IOException ex) {
                                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                            }
                        }
                    }
                }
            } finally {
                handler.putAndUnlock(value);
            }
        }
        return value;
    }

    public Stream<Tile> getTiles(TileMatrixSet pyramid, TileMatrix mosaic, Collection<long[]> locations, Map hints) throws DataStoreException {

        return queryUnoptimizedIO(pyramid, mosaic, locations);
    }

    /**
     * Use standard java IO with a thread pool.
     */
    private Stream<Tile> queryUsingIO(final List<ImagePack> downloadList){
        return downloadList.stream().parallel().map((ImagePack pack) -> {
            final Tile tr;
            try {
                tr = pack.readNow();
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(),ex);
                return null;
            }
            return tr;
        }).filter(Objects::nonNull);
    }

    /**
     * When service is secured or has other constraints we can only use standard IO.
     */
    private Stream<Tile> queryUnoptimizedIO(TileMatrixSet pyramid, TileMatrix mosaic, Collection<long[]> locations){

        final List<Tile> queue = new ArrayList<>();

        //compose the requiered queries
        final List<ImagePack> downloadList = new ArrayList<>();
        for (long[] p : locations) {
            //check the cache if we have the image already
            final String tid = toId(pyramid,mosaic, p);
            RenderedImage image = null;
            if (tileCache != null) {
                image = tileCache.get(tid);
            }

            if (image != null) {
                //image was in cache, reuse it
                DefaultImageTile tile = new DefaultImageTile(mosaic, null, image, 0, p);
                queue.add(tile);
            } else {
                //we will have to download this image
                downloadList.add(new ImagePack(pyramid, mosaic, p));
            }
        }

        //nothing to download, everything was in cache.
        if (downloadList.isEmpty()) {
            return queue.stream();
        }
        return Stream.concat(
                queue.stream(),
                queryUsingIO(downloadList));
    }

    private final class ImagePack {

        private final String requestPath;
        private final TileMatrixSet matrixSet;
        private final TileMatrix matrix;
        private final long[] pt;

        public ImagePack(String requestPath, TileMatrixSet matrixSet, TileMatrix matrix, long[] pt) {
            this.requestPath = requestPath;
            this.matrixSet = matrixSet;
            this.matrix = matrix;
            this.pt = pt;
        }

        public ImagePack(TileMatrixSet matrixSet, TileMatrix matrix, long[] pt) {
            this.requestPath = null;
            this.matrixSet = matrixSet;
            this.matrix = matrix;
            this.pt = pt;
        }

        public String getRequestPath() {
            return requestPath;
        }

        public Tile readNow() throws DataStoreException, IOException{
            //tile should never be null at this point
            final DefaultImageTile ref = (DefaultImageTile) matrix.getTile(new long[]{pt[0], pt[1]}).orElse(null);
            if (ref.getInput() instanceof RenderedImage) {
                return ref;
            }
            final RenderedImage img = ref.getImage();
            return new DefaultImageTile(matrix, ref.getImageReaderSpi(), img, 0, ref.getIndices());
        }

    }

}
