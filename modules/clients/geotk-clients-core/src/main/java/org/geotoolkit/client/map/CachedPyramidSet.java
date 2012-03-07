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
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
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
import org.geotoolkit.coverage.AbstractGridMosaic;
import org.geotoolkit.coverage.DefaultPyramidSet;
import org.geotoolkit.coverage.DefaultTileIterator;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.image.io.mosaic.Tile;
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
            
        }
        
        
    }
    
    
    protected static final Logger LOGGER = Logging.getLogger(CachedPyramidSet.class);
    
    /**
     * Cache the last queried tiles
     */
    private final Cache<String,RenderedImage> tileCache = new Cache<String, RenderedImage>(4, 10, false);
    
    protected final Server server;
    protected final boolean useURLQueries;

    public CachedPyramidSet(Server server, boolean useURLQueries) {
        this.server = server;
        this.useURLQueries = useURLQueries;
    }
    
    protected Server getServer(){
        return server;
    }
      
    public abstract Request getTileRequest(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException ;
    
    public Tile getTile(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException{
        final RenderedImage image = getTileImage(mosaic, col, row, hints);
        final AffineTransform gridToCRS = AbstractGridMosaic.getTileGridToCRS(mosaic, new Point(col, row));
        final Tile tile = new Tile(image, gridToCRS);
        return tile;
    }
    
    private InputStream download(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException {
        final Request request = getTileRequest(mosaic, col, row, hints);
        try {
            return request.getResponseStream();
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }
    
    private String toId(GridMosaic mosaic, int col, int row, Map hints){
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
                    final InputStream stream = download(mosaic, col, row, hints);
                    if(stream != null){
                        try{
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
                }
            } finally {
                handler.putAndUnlock(value);
            }
        }
        return value;
    }
    
    
    public Iterator<Tile> getTiles(GridMosaic mosaic, Collection<? extends Point> locations, Map hints) throws DataStoreException{
        
        if(!useURLQueries){
            //can not optimize a non url server
            return new DefaultTileIterator(mosaic, locations.iterator(), hints);
        }
        
        final Server server = getServer();
        
        if(server == null){
            return new DefaultTileIterator(mosaic, locations.iterator(), hints);
        }
        
        if(!(server.getClientSecurity() == DefaultClientSecurity.NO_SECURITY)){
            //we can optimize only if there is no security
            return new DefaultTileIterator(mosaic, locations.iterator(), hints);
        }
        
        final URL url = server.getURL();
        final String protocol = url.getProtocol();
        
        if(!"http".equalsIgnoreCase(protocol)){
            //we can optimize only an http protocol
            return new DefaultTileIterator(mosaic, locations.iterator(), hints);
        }
        
        //compose the requiered queries, TODO check the cache if some images already exist
        final List<ImagePack> packs = new ArrayList<ImagePack>();
        for(Point p : locations){
            String str;
            try {
                str = getTileRequest(mosaic, p.x, p.y, hints).getURL().toString();
                str = str.replaceAll(url.toString(), "");
                packs.add(new ImagePack(str, mosaic, p));
            } catch (MalformedURLException ex) {
                Logger.getLogger(CachedPyramidSet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        
        ////////////////////////////////////////////////////////////////////////
        // USE NIO TO QUERY EVERYTHING IN PARALLAL /////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        
        final String host = url.getHost();
        final int port = (url.getPort() == -1) ? url.getDefaultPort() : url.getPort();
        
        final BlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(10);
                    
        final CountDownLatch latch = new CountDownLatch(packs.size()){
            @Override
            public void countDown() {
                super.countDown();
                if(getCount() <= 0){
                    try {
                        //put a custom object, this is used in the iterator 
                        //to detect the end.
                        queue.put(new Object());
                    } catch (InterruptedException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
            }
        };

        for (final ImagePack pack : packs) {
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
                            try {
                                pack.setBuffer(response.getEntity().getContent());
                                queue.put(pack);
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
        
        
        return new Iterator<Tile>() {
            
            Tile next = null;
            
            @Override
            public boolean hasNext() {
                checkNext();
                return next != null;
            }
            @Override
            public Tile next() {
                checkNext();
                if(next == null){
                    throw new NoSuchElementException("No more tiles");
                }
                
                Tile c = next;
                next = null;
                return c;
            }
            
            private void checkNext() {
                if(next != null){
                    return;
                }
                
                Object obj = null;
                try {
                    obj = queue.take();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CachedPyramidSet.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(obj instanceof ImagePack){
                    final ImagePack pack = (ImagePack) obj;
                    final RenderedImage img = pack.img;
                    final AffineTransform trs = AbstractGridMosaic.getTileGridToCRS(pack.mosaic, pack.pt);
                    next = new Tile(img, trs);
                }
                //we have finish
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }
    
    /**
     * Used is NIO queries, act as an information container for each query.
     */
    private static class ImagePack{
        
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
            }catch(Exception ex){
                ex.printStackTrace();
            }
            
        }
        
    }
    
}
