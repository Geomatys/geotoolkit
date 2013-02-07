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

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.geotoolkit.client.Request;
import org.geotoolkit.client.Server;
import org.geotoolkit.coverage.*;
import org.geotoolkit.security.DefaultClientSecurity;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ImageIOUtilities;
import org.geotoolkit.util.collection.Cache;
import org.geotoolkit.util.logging.Logging;
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
 * @author Johann Sorel (Geomatys) @module pending
 */
public abstract class CachedPyramidSet extends DefaultPyramidSet {

    /**
     * Boolean property used on tiled servers to force using NIO connections.
     * default value is false, rely on standard IO.
     */
    public static final String PROPERTY_NIO = "nio_query";

    protected static final Logger LOGGER = Logging.getLogger(CachedPyramidSet.class);

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
    protected final Server server;
    protected final boolean useURLQueries;
    protected final boolean cacheImages;

    public CachedPyramidSet(Server server, boolean useURLQueries, boolean cacheImages) {
        this.server = server;
        this.useURLQueries = useURLQueries;
        this.cacheImages = cacheImages;
        if (cacheImages) {
            tileCache = new Cache<String, RenderedImage>(30, 30, false);
        } else {
            tileCache = null;
        }
    }

    protected Server getServer() {
        return server;
    }

    public abstract Request getTileRequest(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException;

    public TileReference getTile(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException {

        if (cacheImages) {
            return new DefaultTileReference(null, getTileImage(mosaic, col, row, hints), 0, new Point(col, row));
        } else {
            return new RequestTileReference(null, getTileRequest(mosaic, col, row, hints), 0, new Point(col, row));
        }
    }

    private static String toId(GridMosaic mosaic, int col, int row, Map hints) {
        final String pyramidId = mosaic.getPyramid().getId();
        final String mosaicId = mosaic.getId();

        final StringBuilder sb = new StringBuilder(pyramidId).append('_').append(mosaicId).append('_').append(col).append('_').append(row);

        return sb.toString();
    }

    private RenderedImage getTileImage(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException {

        final String tileId = toId(mosaic, col, row, hints);

        //use the cache if available
        RenderedImage value = tileCache.peek(tileId);
        if (value == null) {
            Cache.Handler<RenderedImage> handler = tileCache.lock(tileId);
            try {
                value = handler.peek();
                if (value == null) {
                    final Request request = getTileRequest(mosaic, col, row, hints);
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

    public BlockingQueue<Object> getTiles(GridMosaic mosaic, Collection<? extends Point> locations, Map hints) throws DataStoreException {

        if (!cacheImages || !useURLQueries) {
            //can not optimize a non url server
            return queryUnoptimizedIO(mosaic, locations, hints);
        }

        final Server server = getServer();

        if (server == null) {
            return queryUnoptimizedIO(mosaic, locations, hints);
        }

        if (!(server.getClientSecurity() == DefaultClientSecurity.NO_SECURITY)) {
            //we can optimize only if there is no security
            return queryUnoptimizedIO(mosaic, locations, hints);
        }

        final boolean useNIO = Boolean.TRUE.equals(server.getUserProperty(PROPERTY_NIO));
        if(!useNIO){
            return queryUnoptimizedIO(mosaic, locations, hints);
        }

        final URL url = server.getURL();
        final String protocol = url.getProtocol();

        if (!"http".equalsIgnoreCase(protocol)) {
            //we can optimize only an http protocol
            return queryUnoptimizedIO(mosaic, locations, hints);
        }


        final CancellableQueue<Object> queue = new CancellableQueue<Object>(1000);

        //compose the requiered queries
        final List<ImagePack> downloadList = new ArrayList<ImagePack>();
        for (Point p : locations) {
            //check the cache if we have the image already
            final String tid = toId(mosaic, p.x, p.y, hints);
            final RenderedImage image = tileCache.get(tid);

            if (queue.isCancelled()) {
                queue.offer(GridMosaic.END_OF_QUEUE); //end sentinel
                return queue;
            }

            if (image != null) {
                //image was in cache, reuse it
                final ImagePack pack = new ImagePack(mosaic, p);
                pack.img = image;
                queue.offer(pack.getTile());
            } else {
                //we will have to download this image
                String str;
                try {
                    str = getTileRequest(mosaic, p.x, p.y, hints).getURL().toString();
                    str = str.replaceFirst("http://", "");
                    str = str.substring(str.indexOf('/'));
                    downloadList.add(new ImagePack(str, mosaic, p));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(CachedPyramidSet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        //nothing to download, everything was in cache.
        if (downloadList.isEmpty()) {
            queue.offer(GridMosaic.END_OF_QUEUE); //end sentinel
            return queue;
        }

        queryUsingNIO(url, queue, downloadList);

        return queue;
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
                        queue.put(GridMosaic.END_OF_QUEUE);
                    } catch (InterruptedException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
            }
        };

        final Map<Integer, ImagePack> PACK_MAP = new ConcurrentHashMap<Integer, ImagePack>();

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
    private void queryUsingIO(final CancellableQueue queue,
            final List<ImagePack> downloadList){

        final int processors = Runtime.getRuntime().availableProcessors();
        final ExecutorService es = Executors.newFixedThreadPool(processors*2);

        queue.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        es.shutdownNow();
                    }
                });

        final CountDownLatch latch = new CountDownLatch(downloadList.size()) {
            @Override
            public void countDown() {
                super.countDown();
                if (getCount() <= 0 && !queue.isCancelled()) {
                    try {
                        //put a custom object, this is used in the iterator
                        //to detect the end.
                        queue.put(GridMosaic.END_OF_QUEUE);
                    } catch (InterruptedException ex) {
                        LOGGER.log(Level.INFO, ex.getMessage(), ex);
                    }
                    es.shutdown();
                }
            }
        };

        for(final ImagePack pack : downloadList){
            es.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        final TileReference tr;
                        try {
                            tr = pack.readNow();
                        } catch (Exception ex) {
                            LOGGER.log(Level.WARNING, ex.getMessage(),ex);
                            return;
                        }

                        boolean added = false;
                        while(!added && !queue.isCancelled()){
                            try {
                                added = queue.offer(tr,200, TimeUnit.MILLISECONDS);
                            } catch (InterruptedException ex) {
                                LOGGER.log(Level.FINE, ex.getMessage());
                            }
                        }
                    }finally{
                        latch.countDown();
                    }
                }
            });
        }

    }

    /**
     * When service is secured or has other constraints we can only use standard IO.
     */
    private CancellableQueue queryUnoptimizedIO(GridMosaic mosaic, Collection<? extends Point> locations, Map hints){

        final CancellableQueue<Object> queue = new CancellableQueue<Object>(1000);

        //compose the requiered queries
        final List<ImagePack> downloadList = new ArrayList<ImagePack>();
        for (Point p : locations) {
            //check the cache if we have the image already
            final String tid = toId(mosaic, p.x, p.y, hints);
            RenderedImage image = null;
            if(tileCache != null){
                image = tileCache.get(tid);
            }

            if (queue.isCancelled()) {
                queue.offer(GridMosaic.END_OF_QUEUE); //end sentinel
                return queue;
            }

            if (image != null) {
                //image was in cache, reuse it
                final ImagePack pack = new ImagePack(tid, mosaic, p);
                pack.img = image;
                queue.offer(pack.getTile());
            } else {
                //we will have to download this image
                downloadList.add(new ImagePack(mosaic, p));
            }
        }

        //nothing to download, everything was in cache.
        if (downloadList.isEmpty()) {
            queue.offer(GridMosaic.END_OF_QUEUE); //end sentinel
            return queue;
        }

        queryUsingIO(queue, downloadList);
        return queue;
    }

    /**
     * Used is NIO queries, act as an information container for each query.
     */
    private class ImagePack {

        private final String requestPath;
        private final GridMosaic mosaic;
        private final Point pt;
        private final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        private RenderedImage img;

        public ImagePack(String requestPath, GridMosaic mosaic, Point pt) {
            this.requestPath = requestPath;
            this.mosaic = mosaic;
            this.pt = pt;
        }

        public ImagePack(GridMosaic mosaic, Point pt) {
            this.requestPath = null;
            this.mosaic = mosaic;
            this.pt = pt;
        }

        public String getRequestPath() {
            return requestPath;
        }

        public TileReference readNow() throws DataStoreException, IOException{
            final TileReference ref = mosaic.getTile(pt.x, pt.y, null);
            if(ref.getInput() instanceof RenderedImage){
                return ref;
            }
            final BufferedImage img;
            final ImageReader reader = ref.getImageReader();
            try{
                img = reader.read(ref.getImageIndex());
            }finally{
                ImageIOUtilities.releaseReader(reader);
            }

            final TileReference tr = new DefaultTileReference(ref.getImageReaderSpi(), img, 0, ref.getPosition());
            return tr;
        }

        public TileReference getTile() {
            if(img == null){
                try {
                    img = ImageIO.read(new ByteArrayInputStream(buffer.array()));
                    if(tileCache != null){
                        final String tid = toId(mosaic, pt.x, pt.y, null);
                        //store it in the cache
                        tileCache.put(tid, img);
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
            return new DefaultTileReference(null, img, 0, pt);
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
         *
         * @param e
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
