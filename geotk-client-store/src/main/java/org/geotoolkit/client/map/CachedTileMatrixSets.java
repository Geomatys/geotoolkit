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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.util.collection.Cache;
import org.geotoolkit.client.Client;
import org.geotoolkit.client.Request;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.security.DefaultClientSecurity;
import org.geotoolkit.storage.coverage.*;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class CachedTileMatrixSets extends DefaultTileMatrixSets {

    /**
     * Sentinel object used to notify the end of the queue.
     */
    private static final Object END_OF_QUEUE = new Object();

    /**
     * Boolean property used on tiled servers to force using NIO connections.
     * default value is false, rely on standard IO.
     */
    public static final String PROPERTY_NIO = "nio_query";

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.client.map");

    //NIO netty bootstrap.
    private static ClientBootstrap BOOTSTRAP;
    static synchronized ClientBootstrap getBootstrap(){
        if(BOOTSTRAP == null){
            BOOTSTRAP = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
            BOOTSTRAP.setOption("keepAlive", true);
            BOOTSTRAP.setOption("tcpNoDelay", true);
            BOOTSTRAP.setOption("reuseAddress", true);
            BOOTSTRAP.setOption("connectTimeoutMillis", 30000);
            //TODO release the bootstrap resources on application close. bootstrap.releaseExternalResources();
        }
        return BOOTSTRAP;
    }


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

        if (!cacheImages || !useURLQueries) {
            //can not optimize a non url server
            return queryUnoptimizedIO(pyramid, mosaic, locations);
        }

        final Client server = getServer();

        if (server == null) {
            return queryUnoptimizedIO(pyramid, mosaic, locations);
        }

        if (!(server.getClientSecurity() == DefaultClientSecurity.NO_SECURITY)) {
            //we can optimize only if there is no security
            return queryUnoptimizedIO(pyramid, mosaic, locations);
        }

        final boolean useNIO = Boolean.TRUE.equals(server.getUserProperty(PROPERTY_NIO));
        if(!useNIO){
            return queryUnoptimizedIO(pyramid, mosaic, locations);
        }

        final URL url = server.getURL();
        final String protocol = url.getProtocol();

        if (!"http".equalsIgnoreCase(protocol)) {
            //we can optimize only an http protocol
            return queryUnoptimizedIO(pyramid, mosaic, locations);
        }

        final CancellableQueue<Object> queue = new CancellableQueue<>(1000);

        //compose the requiered queries
        final List<ImagePack> downloadList = new ArrayList<>();
        for (long[] p : locations) {
            //check the cache if we have the image already
            final String tid = toId(pyramid, mosaic, p);
            final RenderedImage image = tileCache.get(tid);

            if (image != null) {
                //image was in cache, reuse it
                final ImagePack pack = new ImagePack(pyramid, mosaic, p);
                pack.img = image;
                queue.offer(pack.getTile());
            } else {
                //we will have to download this image
                String str;
                try {
                    str = getTileRequest(pyramid, mosaic, p, hints).getURL().toString();
                    str = str.replaceFirst("http://", "");
                    str = str.substring(str.indexOf('/'));
                    downloadList.add(new ImagePack(str, pyramid, mosaic, p));
                } catch (MalformedURLException ex) {
                    Logger.getLogger("org.geotoolkit.client.map").log(Level.SEVERE, null, ex);
                }
            }
        }

        if (!downloadList.isEmpty()) {
            queryUsingNIO(url, queue, downloadList);
        }

        return Stream.generate(new Supplier<Tile>() {
            @Override
            public Tile get() {
                Object take;
                try {
                    take = queue.take();
                    if (take == END_OF_QUEUE) return null;
                    return (Tile) take;
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex.getMessage(), ex);
                }
            }
        }).takeWhile(Objects::nonNull);
    }

    /**
     * Use Netty NIO to download tiles.
     */
    private void queryUsingNIO(final URL url, final CancellableQueue queue,
            final List<ImagePack> downloadList){

        final String host = url.getHost();
        final int port = (url.getPort() == -1) ? url.getDefaultPort() : url.getPort();


        final ChannelGroup group = new DefaultChannelGroup("group");

        final CountDownLatch latch = new CountDownLatch(downloadList.size()) {

            @Override
            public void countDown() {
                super.countDown();
                if (getCount() <= 0) {
                    try {
                        //put a custom object, this is used in the iterator
                        //to detect the end.
                        queue.put(END_OF_QUEUE);
                    } catch (InterruptedException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
            }
        };

        final Map<Integer, ImagePack> PACK_MAP = new ConcurrentHashMap<>();

        // Set up the event pipeline factory.
        final ClientBootstrap boot = getBootstrap();
        boot.setPipelineFactory(new TilePipelineFactory(queue, latch, PACK_MAP));

        for (final ImagePack pack : downloadList) {
            final ChannelFuture future = boot.connect(new InetSocketAddress(host, port));

            future.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture cf) throws Exception {

                    final Channel channel = future.getChannel();
                    group.add(channel);
                    PACK_MAP.put(channel.getId(), pack);

                    final HttpRequest request = new DefaultHttpRequest(
                            HttpVersion.HTTP_1_1, HttpMethod.GET, pack.requestPath);
                    request.setHeader(HttpHeaders.Names.HOST, host);
                    request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
                    request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.BYTES);

                    if (channel.isOpen() && channel.isWritable() && !queue.isCancelled()) {
                        channel.write(request);
                    }
                }
            });
        }

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
                final ImagePack pack = new ImagePack(tid, pyramid, mosaic, p);
                pack.img = image;
                queue.add(pack.getTile());
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

    /**
     * Used is NIO queries, act as an information container for each query.
     */
    private class ImagePack {

        private final String requestPath;
        private final TileMatrixSet pyramid;
        private final TileMatrix mosaic;
        private final long[] pt;
        private final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        private RenderedImage img;

        public ImagePack(String requestPath, TileMatrixSet pyramid, TileMatrix mosaic, long[] pt) {
            this.requestPath = requestPath;
            this.pyramid = pyramid;
            this.mosaic = mosaic;
            this.pt = pt;
        }

        public ImagePack(TileMatrixSet pyramid, TileMatrix mosaic, long[] pt) {
            this.requestPath = null;
            this.pyramid = pyramid;
            this.mosaic = mosaic;
            this.pt = pt;
        }

        public String getRequestPath() {
            return requestPath;
        }

        public Tile readNow() throws DataStoreException, IOException{
            //tile should never be null at this point
            final DefaultImageTile ref = (DefaultImageTile) mosaic.getTile(new long[]{pt[0], pt[1]}).orElse(null);
            if(ref.getInput() instanceof RenderedImage){
                return ref;
            }
            final RenderedImage img = ref.getImage();
            return new DefaultImageTile(mosaic, ref.getImageReaderSpi(), img, 0, ref.getIndices());
        }

        public Tile getTile() {
            if(img == null){
                try {
                    img = ImageIO.read(new ByteArrayInputStream(buffer.array()));
                    if(tileCache != null){
                        final String tid = toId(pyramid, mosaic, pt[0], pt[1]);
                        //store it in the cache
                        tileCache.put(tid, img);
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
            return new DefaultImageTile(mosaic, null, img, 0, pt);
        }
    }

    /**
     * Pipeline Factory.
     */
    private class TilePipelineFactory implements ChannelPipelineFactory {

        private final CancellableQueue<Object> queue;
        private final CountDownLatch latch;
        private final Map<Integer,ImagePack> packs;

        public TilePipelineFactory(final CancellableQueue<Object> queue,
                final CountDownLatch latch, final Map<Integer,ImagePack> packs) {
            this.queue = queue;
            this.latch = latch;
            this.packs = packs;
        }

        @Override
        public ChannelPipeline getPipeline() throws Exception {
            final ChannelPipeline pipeline = new DefaultChannelPipeline();
            pipeline.addLast("codec", new HttpClientCodec());
            pipeline.addLast("handler", new TileClientHandler(queue, latch, packs));
            return pipeline;
        }
    }

    /**
     * ChannelHandler that aggregate chunks and update ImagePack, queue and latch.
     */
    private class TileClientHandler extends SimpleChannelHandler {

        private final CancellableQueue<Object> queue;
        private final CountDownLatch latch;
        private final Map<Integer,ImagePack> packs;
        private boolean chunks;

        public TileClientHandler(final CancellableQueue<Object> queue,
                final CountDownLatch latch, final Map<Integer,ImagePack> packs) {
            this.queue = queue;
            this.latch = latch;
            this.packs = packs;
        }

        @Override
        public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
            final Integer channelID = e.getChannel().getId();
            final ImagePack pack = packs.get(channelID);

            if (!chunks) {
                final HttpResponse response = (HttpResponse) e.getMessage();

                if (response.isChunked()) {
                    chunks = true;

                } else {
                    final ChannelBuffer content = response.getContent();
                    if (content.readable()) {
                        pack.buffer.writeBytes(content);
                        messageCompleted(e);
                    }
                }
            } else {
                final HttpChunk chunk = (HttpChunk) e.getMessage();
                if (chunk.isLast()) {
                    chunks = false;
                    messageCompleted(e);

                } else {
                    pack.buffer.writeBytes(chunk.getContent());
                }
            }

            if(queue.isCancelled()){
                ctx.getChannel().close();
            }

        }

        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
            e.getCause().printStackTrace();
            latch.countDown();
            e.getChannel().close();
        }

        /**
         * Message completed, all chunk are aggregated into buffer attribute.
         * Create an InputStream from that buffer and update PackImage and add it tho queue.
         */
        private void messageCompleted(final MessageEvent e) {
            final Integer channelID = e.getChannel().getId();
            final ImagePack pack = packs.get(channelID);

            try {
                queue.put(pack.getTile());
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }

            latch.countDown();
            packs.remove(channelID);
            e.getChannel().close();
        }
    }
}
