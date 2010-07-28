/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.index.quadtree;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.logging.Level;

import org.geotoolkit.index.CloseableCollection;
import org.geotoolkit.index.Data;

import com.vividsolutions.jts.geom.Envelope;
import java.util.Arrays;

/**
 * A collection that will open and close the QuadTree and find the next id in
 * the index.
 * 
 * @author Jesse
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LazySearchCollection extends AbstractCollection<Data> implements
        CloseableCollection<Data> {

    private final QuadTree tree;
    private final Envelope bounds;

    public LazySearchCollection(QuadTree tree, Envelope bounds) {
        this.tree = tree;
        this.bounds = bounds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#iterator()
     */
    @Override
    public Iterator<Data> iterator() {
        final BufferIterator iterator = new BufferIterator(
                new LazySearchIterator(tree.getRoot(), bounds), tree.getDataReader());
        tree.registerIterator(iterator);
        return iterator;
    }

    @Override
    public int size() {
        final Iterator iter = iterator();
        try {
            int count = 0;
            while (iter.hasNext()) {
                iter.next();
                count++;
            }
            return count;
        } finally {
            try {
                tree.close(iter);
            } catch (StoreException e) {
                QuadTree.LOGGER.severe("Couldn't close iterator");
            }
        }
    }

    @Override
    public boolean isEmpty() {
        final Iterator iter = iterator();
        try {
            return !iter.hasNext();
        } finally {
            try {
                tree.close(iter);
            } catch (StoreException e) {
                QuadTree.LOGGER.severe("Couldn't close iterator");
            }
        }
    }

    @Override
    public void close() {
        try {
            tree.close();
        } catch (StoreException e) {
            QuadTree.LOGGER.log(Level.WARNING, "Error closing QuadTree", e);
        }
    }

    @Override
    public void closeIterator( Iterator<Data> iter ) throws IOException {
        try {
            tree.close(iter);
        } catch (StoreException e) {
            throw new IOException(e);
        }
    }

    /**
     * Holds a buffer of values to avoid open files to often.
     */
    public static final class BufferIterator implements Iterator<Data>{

        private static final int MAX_INDICES = 32768;

        private final LazySearchIterator ite;
        private final DataReader reader;
        
        //the last node we retrieved
        private AbstractNode node = null;
        private int[] nodeIds = null;
        private int inc = 0;

        private final int[] indices = new int[MAX_INDICES];
        private final Data[] datas = new Data[MAX_INDICES];
        private int indexSize = 0;
        private int index = 0;

        private Data next = null;

        public BufferIterator(LazySearchIterator ite, DataReader reader){
            this.reader = reader;
            this.ite = ite;
        }

        @Override
        public boolean hasNext() {
            findNext();
            return next != null;
        }

        @Override
        public Data next() {
            findNext();
            final Data temp = next;
            next = null;
            return temp;
        }

        public void findNext(){

            //next one already prepared
            if(next!= null){
                return;
            }

            //next one in the cache
            if(index<indexSize){
                next = datas[index];
                //prepare next one
                index++;
                return;
            }

            index = 0;
            fillIndices();

            // sort so offset lookup is faster
            Arrays.sort(indices,0,indexSize);
            try{
                reader.read(indices, datas, indexSize);
            }catch(IOException ex) {
                throw new RuntimeException(ex);
            }

            if(indexSize>0){
                next = datas[0];
                index++;
            }
        }

        private void fillIndices(){
            indexSize=0;

            //copy the remaining ids from the last node
            if(node != null){
                if(fillWithNode()){
                    return;
                }
            }

            while(ite.hasNext()){
                node = ite.next();
                nodeIds = node.getShapesId();
                inc=0;

                if(fillWithNode()){
                    //we reach buffer limit
                    return;
                }
            }
        }

        /**
         * @return true if the buffer is at max capacity
         */
        private boolean fillWithNode(){
            int remaining = nodeIds.length-inc;
            if(MAX_INDICES-indexSize > remaining){
                //we have enough space to store all remaining ids
                System.arraycopy(nodeIds, inc, indices, indexSize, remaining);
                indexSize += remaining;
                node = null;
                return false;
            }else{
                //copy part of the ids
                remaining = MAX_INDICES-indexSize;
                System.arraycopy(nodeIds, inc, indices, indexSize, remaining);
                indexSize += remaining;
                inc += remaining;
                return true;
            }
        }

        public void close() throws StoreException{
            ite.close();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

}
