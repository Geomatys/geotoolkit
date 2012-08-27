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
import java.util.logging.Level;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
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
        
    private final Object LOCK = new Object();
    
    private DataStoreRuntimeException subException = null;
    private boolean closed = false;
    protected final R iterator;
    protected final int cacheSize;
    private final ArrayBlockingQueue queue;
    private Feature[] buffer = null;
    private int bufferIndex = 0;
    protected F next = null;
    private final Thread collector;

    /**
     * Creates a new instance of GenericCacheFeatureIterator
     *
     * @param iterator FeatureReader to limit
     * @param cacheSize cacheSize
     */
    private GenericCachedFeatureIterator(final R iterator, final int cacheSize) {
        this.iterator = iterator;
        this.cacheSize = cacheSize;
        this.queue = new ArrayBlockingQueue(2);
        
        collector = new Thread(){

            @Override
            public void run() {
                
                boolean finish = false;
                try{
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
                        }catch(DataStoreRuntimeException ex){
                            subException = ex;
                        }

                        boolean success;
                        do{
                            success = queue.offer(buffer);
                            if(!success){
                                try {
                                    //no space left, go to sleep until iterator wake it up
                                    synchronized(LOCK){
                                        LOCK.wait();
                                    }
                                } catch (InterruptedException ex) {
                                    Logging.getLogger(GenericCachedFeatureIterator.class).log(Level.WARNING, ex.getMessage(), ex);
                                }
                            }

                        }while(!success || closed);
                    }
                }finally{
                    iterator.close();
                }
                
            }
        };
        collector.start();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws DataStoreRuntimeException {
        if(subException != null){
            //forward sub exception
            final DataStoreRuntimeException d = subException;
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
    public void close() throws DataStoreRuntimeException {
        closed = true;
        //notify collector thread, may be waiting
        synchronized(LOCK){
            LOCK.notify();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws DataStoreRuntimeException {
        if(subException != null){
            //forward sub exception
            final DataStoreRuntimeException d = subException;
            subException = null;
            throw d;
        }
        findNext();
        return next != null;
    }

    private void findNext() throws DataStoreRuntimeException {
        if(next != null || closed) return;
        
        if(buffer == null){
            try {
                buffer = (Feature[]) queue.take();
            } catch (InterruptedException ex) {
                Logging.getLogger(GenericCachedFeatureIterator.class).log(Level.WARNING, ex.getMessage(), ex);
            }
            bufferIndex = 0;
            
            //notify collector thread some space is available
            synchronized(LOCK){
                LOCK.notify();
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
        iterator.remove();
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

        private GenericCachedFeatureReader(final R reader, final int cacheSize){
            super(reader,cacheSize);
        }
        
        @Override
        public T getFeatureType() {
            return iterator.getFeatureType();
        }

    }

    private static final class GenericCachedFeatureCollection extends WrapFeatureCollection{

        private final int cacheSize;

        private GenericCachedFeatureCollection(final FeatureCollection original, final int cacheSize){
            super(original);
            this.cacheSize = cacheSize;
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws DataStoreRuntimeException {
            return wrap(getOriginalFeatureCollection().iterator(hints), cacheSize);
        }

        @Override
        protected Feature modify(Feature original) throws DataStoreRuntimeException {
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
    
}
