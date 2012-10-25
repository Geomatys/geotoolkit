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
package org.geotoolkit.data.memory;

import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Wrap a feature iterator and precache the given number of values.
 * A separate thread is created to load the buffer.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class GenericCachedFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
        implements FeatureIterator<F> {
        
    //TODO : wait for martin, there should already be a thread pool for global tasks somewhere.
    public static final Executor POOL = Executors.newCachedThreadPool();
    
    private final Object QUEUELOCK = new Object();
    private final Object FINISHLOCK = new Object();
    
    // used by the main thread to notify collector thread to stop retrieving features.
    private volatile boolean closed = false;
    // used by the collector thread to notify main thread that sub iterator can be closed.
    private volatile boolean canCloseSub = false;
    
    private final ArrayBlockingQueue queue = new ArrayBlockingQueue(2);
    
    protected R iterator;
    protected final int cacheSize;
    private Feature[] buffer = null;
    private int bufferIndex = 0;
    protected F next = null;
    private FeatureStoreRuntimeException subException = null;

    /**
     * Creates a new instance of GenericCacheFeatureIterator
     *
     * @param iterator FeatureReader to limit
     * @param cacheSize cacheSize
     */
    private GenericCachedFeatureIterator(final R iterator, final int cacheSize) {
        this.iterator = iterator;
        this.cacheSize = cacheSize;
        
        POOL.execute(new Collector());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws FeatureStoreRuntimeException {
        if(subException != null){
            //forward sub exception
            final FeatureStoreRuntimeException d = subException;
            subException = null;
            throw d;
        }
        
        findNext();
        final F c = next;
        next = null;
        if(c == null){
            throw new NoSuchElementException("No such Feature exists");
        }
        return c;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws FeatureStoreRuntimeException {
        closed = true;
        //notify collector thread, may be waiting
        synchronized(QUEUELOCK){
            QUEUELOCK.notify();
        }
        
        //sub iterator might already be closed
        if(iterator != null){
            synchronized(FINISHLOCK){
                iterator.close();
                iterator = null;
            }
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        if(subException != null){
            //forward sub exception
            final FeatureStoreRuntimeException d = subException;
            subException = null;
            throw d;
        }
        findNext();
        return next != null;
    }

    private void findNext() throws FeatureStoreRuntimeException {
        if(next != null || closed) return;
        
        if(buffer == null){
            //collector thread might have finish reading before iteration
            //is over. we can release the sub iterator sooner to release resources.
            if(iterator!=null && canCloseSub){
                iterator.close();
                iterator = null;
            }
            
            try {
                buffer = (Feature[]) queue.take();
            } catch (InterruptedException ex) {
                Logging.getLogger(GenericCachedFeatureIterator.class).log(Level.WARNING, ex.getMessage(), ex);
            }
            bufferIndex = 0;
            
            //notify collector thread some space is available
            synchronized(QUEUELOCK){
                QUEUELOCK.notify();
            }
        }
        
        next = (F) buffer[bufferIndex];
        bufferIndex++;
        
        if(bufferIndex >= cacheSize){
            //we have finish reading this buffer.
            //next iteration will get a new one.
            buffer = null;
        }
        
        if(next == null){
            //no more records
            closed = true;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        throw new FeatureStoreRuntimeException("Cached iterator does not support remove operation.");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append("[CacheSize=").append(cacheSize).append("]\n");
        String subIterator = "\u2514\u2500\u2500" + iterator.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
    }

    /**
     * Wrap a FeatureReader with a cache size.
     * 
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericCachedFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericCachedFeatureIterator<F,R> implements FeatureReader<T,F>{

        private final T ft;
        
        private GenericCachedFeatureReader(final R reader, final int cacheSize){
            super(reader,cacheSize);
            ft = iterator.getFeatureType();
        }
        
        @Override
        public T getFeatureType() {
            return ft;
        }

    }

    private static final class GenericCachedFeatureCollection extends WrapFeatureCollection{

        private final int cacheSize;

        private GenericCachedFeatureCollection(final FeatureCollection original, final int cacheSize){
            super(original);
            this.cacheSize = cacheSize;
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
            return wrap(getOriginalFeatureCollection().iterator(hints), cacheSize);
        }

        @Override
        protected Feature modify(Feature original) throws FeatureStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }

    /**
     * Wrap a FeatureIterator with a cache size.
     */
    public static <F extends Feature> FeatureIterator<F> wrap(final FeatureIterator<F> reader, final int cacheSize){
        if(reader instanceof FeatureReader){
            return wrap((FeatureReader)reader,cacheSize);
        }else if(reader instanceof FeatureWriter){
            return wrap((FeatureWriter)reader,cacheSize);
        }else{
            return new GenericCachedFeatureIterator(reader, cacheSize);
        }
    }

    /**
     * Wrap a FeatureReader with a cache size.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrap(
            final FeatureReader<T,F> reader, final int cacheSize){
        return new GenericCachedFeatureIterator.GenericCachedFeatureReader(reader, cacheSize);
    }

    /**
     * Create an caching FeatureCollection wrapping the given collection.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final int cacheSize){
        return new GenericCachedFeatureIterator.GenericCachedFeatureCollection(original, cacheSize);
    }
    
    
    private final class Collector implements Runnable{

        @Override
        public void run() {
            boolean finish = false;
            
            synchronized(FINISHLOCK){
                mainLoop:
                while(!closed && !finish){
                    //create and fill buffer
                    final Feature[] buffer = new Feature[cacheSize];
                    try{
                        for(int i=0;i<buffer.length;i++){
                            if(iterator.hasNext()){
                                buffer[i] = iterator.next();
                            }else{
                                finish = true;
                                break;
                            }
                        }
                    }catch(FeatureStoreRuntimeException ex){
                        subException = ex;
                    }

                    boolean success;
                    do{
                        success = queue.offer(buffer);
                        if(!success){
                            try {
                                //no space left, go to sleep until iterator wake it up
                                synchronized(QUEUELOCK){
                                    if(closed) break mainLoop;
                                    QUEUELOCK.wait();
                                }
                            } catch (InterruptedException ex) {
                                Logging.getLogger(GenericCachedFeatureIterator.class).log(Level.WARNING, ex.getMessage(), ex);
                            }
                        }

                    }while(!success);
                }
            }
            canCloseSub = true;
        }
    
    }
    
}
