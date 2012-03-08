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
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.nio.DefaultHttpClientIODispatch;
import org.apache.http.impl.nio.pool.BasicNIOConnPool;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.*;
import org.geotoolkit.client.Request;
import org.geotoolkit.client.Server;
import org.geotoolkit.coverage.*;
import org.geotoolkit.security.DefaultClientSecurity;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.collection.Cache;
import org.geotoolkit.util.logging.Logging;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class CachedPyramidSet extends DefaultPyramidSet{
    
    protected static final Logger LOGGER = Logging.getLogger(CachedPyramidSet.class);
    
    // NIO Reactor for queries
    private static ConnectingIOReactor ioReactor = null;
    private static BasicNIOConnPool pool = null;
    private static HttpAsyncRequester requester;
    
    static{
        
        try{
            // HTTP parameters for the client
            final HttpParams params = new SyncBasicHttpParams();
                params
                    .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 30000)
                    .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000)
                    .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 30 * 1024)
                    .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                    .setBooleanParameter(CoreConnectionPNames.SO_REUSEADDR, true)
                    .setBooleanParameter(CoreConnectionPNames.SO_KEEPALIVE, true)
                    .setParameter(CoreProtocolPNames.USER_AGENT, "Test/1.1");
                
            // Create HTTP protocol processing chain
            final HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                    // Use standard client-side protocol interceptors
                    new RequestContent(),
                    new RequestTargetHost(),
                    new RequestConnControl(),
                    new RequestUserAgent(),
                    new RequestExpectContinue()});
            
            // Create client-side HTTP protocol handler
            final HttpAsyncRequestExecutor protocolHandler = new HttpAsyncRequestExecutor();
            // Create client-side I/O event dispatch
            final IOEventDispatch ioEventDispatch = new DefaultHttpClientIODispatch(protocolHandler, params);
            // Create client-side I/O reactor
//            final IOReactorConfig config = new IOReactorConfig();
//            config.setIoThreadCount(2);
            ioReactor = new DefaultConnectingIOReactor();
            // Create HTTP connection pool
            pool = new BasicNIOConnPool(ioReactor, params);
            // Limit total number of connections to just two
            pool.setDefaultMaxPerRoute(2);
            pool.setMaxTotal(2);
            // Run the I/O reactor in a separate thread
            final Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Ready to go!
                        ioReactor.execute(ioEventDispatch);
                    } catch (InterruptedIOException ex) {
                        System.err.println("Interrupted");
                    } catch (IOException e) {
                        System.err.println("I/O error: " + e.getMessage());
                    }
                    System.out.println("Shutdown");
                }
            });
            // Start the client thread
            t.start();
            
            // Create HTTP requester
            requester = new HttpAsyncRequester(
                    httpproc, new DefaultConnectionReuseStrategy(), params);
            
        }catch(Exception ex){
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        
    }
    
    /**
     * Cache the last queried tiles
     */
    private final Cache<String,RenderedImage> tileCache;
    
    protected final Server server;
    protected final boolean useURLQueries;
    protected final boolean cacheImages;

    public CachedPyramidSet(Server server, boolean useURLQueries, boolean cacheImages) {
        this.server = server;
        this.useURLQueries = useURLQueries;
        this.cacheImages = cacheImages;
        if(cacheImages){
            tileCache = new Cache<String, RenderedImage>(30, 30, false);
        }else{
            tileCache = null;
        }
    }
        
    protected Server getServer(){
        return server;
    }
      
    public abstract Request getTileRequest(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException ;
    
    public TileReference getTile(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException{
        
        final Object input;
        if(cacheImages){
            input = getTileImage(mosaic, col, row, hints);
        }else{
            try {
                input = getTileRequest(mosaic, col, row, hints).getURL();
            } catch (MalformedURLException ex) {
                throw new DataStoreException(ex.getMessage(),ex);
            }
        }
        
        return new DefaultTileReference(null, input, 0, new Point(col, row));
    }
        
    private static String toId(GridMosaic mosaic, int col, int row, Map hints){
        final String pyramidId = mosaic.getPyramid().getId();
        final String mosaicId = mosaic.getId();
        
        final StringBuilder sb = new StringBuilder(pyramidId).append('_').append(mosaicId)
                .append('_').append(col).append('_').append(row);
        
        return sb.toString();
    }
    
    private RenderedImage getTileImage(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException{
        
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
                    try{
                        stream = request.getResponseStream();
                        value = ImageIO.read(stream);
                    }catch (IOException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    }finally{
                        try {
                            stream.close();
                        } catch (IOException ex) {
                            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                        }
                    }
                }
            } finally {
                handler.putAndUnlock(value);
            }
        }
        return value;
    }
    
    
    public BlockingQueue<Object> getTiles(GridMosaic mosaic, Collection<? extends Point> locations, Map hints) throws DataStoreException{
                
        
        
        if(!cacheImages || !useURLQueries){
            //can not optimize a non url server
            return AbstractGridMosaic.getTiles(mosaic, locations, hints);
        }
        
        final Server server = getServer();
        
        if(server == null){
            return AbstractGridMosaic.getTiles(mosaic, locations, hints);
        }
        
        if(!(server.getClientSecurity() == DefaultClientSecurity.NO_SECURITY)){
            //we can optimize only if there is no security
            return AbstractGridMosaic.getTiles(mosaic, locations, hints);
        }
        
        final URL url = server.getURL();
        final String protocol = url.getProtocol();
        
        if(!"http".equalsIgnoreCase(protocol)){
            //we can optimize only an http protocol
            return AbstractGridMosaic.getTiles(mosaic, locations, hints);
        }
        
        
        final CancellableQueue<Object> queue = new CancellableQueue<Object>(1000);
        
        //compose the requiered queries
        final List<ImagePack> downloadList = new ArrayList<ImagePack>();
        for(Point p : locations){
            //check the cache if we have the image already
            final String tid = toId(mosaic, p.x, p.y, hints);
            final RenderedImage image = tileCache.get(tid);
            
            if (queue.isCancelled()) {
                return queue;
            }
            
            if(image != null){
                //image was in cache, reuse it
                final ImagePack pack = new ImagePack(tid, mosaic, p);
                pack.img = image;
                queue.offer(pack.getTile());
            }else{
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
        if(downloadList.isEmpty()){
            queue.offer(GridMosaic.END_OF_QUEUE); //end sentinel
            return queue;
        }
        
        
        ////////////////////////////////////////////////////////////////////////
        // USE NIO TO QUERY EVERYTHING IN PARALLAL /////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        
        final String host = url.getHost();
        final int port = (url.getPort() == -1) ? url.getDefaultPort() : url.getPort();
        
                    
        final CountDownLatch latch = new CountDownLatch(downloadList.size()){
            @Override
            public void countDown() {
                super.countDown();
                if(getCount() <= 0){
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

        for (final ImagePack pack : downloadList) {
            final HttpHost httphost = new HttpHost(host, port, protocol);
            final BasicHttpRequest request = new BasicHttpRequest("GET", pack.getRequestPath());
            requester.execute(
                    new BasicAsyncRequestProducer(httphost, request),
                    new BasicAsyncResponseConsumer(),
                    pool,
                    new BasicHttpContext(),
                    // Handle HTTP response from a callback
                    new FutureCallback<HttpResponse>() {

                        @Override
                        public void completed(final HttpResponse response) {
                            if (queue.isCancelled()) {
                                return;
                            }
                            
                            try {
                                pack.setBuffer(response.getEntity().getContent());
                                queue.put(pack.getTile());
                            } catch (IOException ex) {
                                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                            } catch (Exception ex) {
                                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                            }
                            latch.countDown();
                        }

                        @Override
                        public void failed(final Exception ex) {
                            latch.countDown();
                        }

                        @Override
                        public void cancelled() {
                            latch.countDown();
                        }

            });
        }   
                
        return queue;
    }
    
    /**
     * Used is NIO queries, act as an information container for each query.
     */
    private class ImagePack{
        
        private final String requestPath;
        private final GridMosaic mosaic;
        private final Point pt;
        private RenderedImage img;

        public ImagePack(String requestPath, GridMosaic mosaic, Point pt) {
            this.requestPath = requestPath;
            this.mosaic = mosaic;
            this.pt = pt;
        }
        
        public String getRequestPath(){
            return requestPath;
        }
        
        public void setBuffer(InputStream buffer) {
            try{
                img = ImageIO.read(buffer);
                final String tid = toId(mosaic, pt.x, pt.y, null);
                //store it in the cache
                tileCache.put(tid, img);
                
            }catch(Exception ex){
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
            
        }
        
        public TileReference getTile(){
            final TileReference t = new DefaultTileReference(null, img, 0, pt);
            return t;
        }
        
    }
    
}
