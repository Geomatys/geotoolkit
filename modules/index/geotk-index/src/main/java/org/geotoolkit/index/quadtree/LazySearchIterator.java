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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import com.vividsolutions.jts.geom.Envelope;
import java.io.IOException;
import java.util.Arrays;
import org.geotoolkit.index.Data;

/**
 * Iterator that search the quad tree depth first. And return each node that match
 * the given bounding box.
 * 
 * @author Jesse
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LazySearchIterator implements SearchIterator<AbstractNode> {

    private static class Segment{
        private final AbstractNode node;
        int childIndex;

        private Segment(AbstractNode node, int childIndex){
            this.node = node;
            this.childIndex = childIndex;
        }

    }

    private final Envelope bounds;
    private boolean closed;

    //the current path where we are, Integer is the current visited child node index.
    private final Deque<Segment> path = new ArrayDeque<Segment>(10);

    //curent visited node
    private AbstractNode current = null;

    public LazySearchIterator(AbstractNode node, Envelope bounds, double[] minRes) {
        this.bounds = bounds;
        this.closed = false;

        if(node.intersects(bounds)){
            path.add(new Segment(node, -1));
        }
        
    }

    @Override
    public boolean hasNext() {
        findNext();
        return current != null;
    }

    @Override
    public AbstractNode next() {
        findNext();
        if (current == null){
            throw new NoSuchElementException("No more elements available");
        }
        
        final AbstractNode temp = current;
        current = null;
        return temp;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws StoreException {
        this.closed = true;
    }

    private void findNext(){
        if (closed){
            throw new IllegalStateException("Iterator has been closed!");
        }

        if(current != null){
            //we already have the next one
            return;
        }

        try {
            //search the next node that has shpIds
            findNextNode();
        } catch (StoreException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void findNextNode() throws StoreException {
        nodeLoop:
        do{
            final Segment segment = path.peekLast();
            if(segment == null){
                current = null;
                return;
            }

            if(segment.childIndex == -1){
                //prepare for next node search
                segment.childIndex = 0;

                //we have not yet explore this node ids
                //we use the node shpIds if he has some
                final int nb = segment.node.getNumShapeIds();
                if(nb>0){
                    //we have some ids in this node
                    current = segment.node;
                    return;
                }
            }

            final int nbNodes = segment.node.getNumSubNodes();
            childLoop:
            while (segment.childIndex < nbNodes) {
                final AbstractNode child = segment.node.getSubNode(segment.childIndex);

                //prepare next node search
                segment.childIndex++;

                if (!child.intersects(bounds)) {
                    //not in the area we requested
                    continue childLoop;
                }

                path.addLast(new Segment(child, -1));

                //explore the sub node
                continue nodeLoop;
            }

            //we have nothing left to explore in this node, go back to the parent
            //to explore next branch
            path.removeLast();
        }while(current == null);

    }


    /**
     * Holds a buffer of values to avoid open files to often.
     */
    public static final class Buffered<T extends Data> implements SearchIterator<T>{

        private final int bufferSize;

        private final LazySearchIterator ite;
        private final DataReader reader;

        //the last node we retrieved
        private AbstractNode node = null;
        private int[] nodeIds = null;
        private int inc = 0;

        private final int[] indices;
        private final Data[] datas;
        private int indexSize = 0;
        private int index = 0;

        private T next = null;

        public Buffered(AbstractNode node, Envelope bounds, double[] minRes, DataReader reader, int bufferSize){
            this.bufferSize = bufferSize;
            this.reader = reader;
            this.ite = new LazySearchIterator(node, bounds, minRes);
            indices = new int[bufferSize];
            datas = new Data[bufferSize];
        }

        @Override
        public boolean hasNext() {
            findNext();
            return next != null;
        }

        @Override
        public T next() {
            findNext();
            final T temp = next;
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
                next = (T) datas[index];
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
                next = (T) datas[0];
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
            if(bufferSize-indexSize > remaining){
                //we have enough space to store all remaining ids
                System.arraycopy(nodeIds, inc, indices, indexSize, remaining);
                indexSize += remaining;
                node = null;
                return false;
            }else{
                //copy part of the ids
                remaining = bufferSize-indexSize;
                System.arraycopy(nodeIds, inc, indices, indexSize, remaining);
                indexSize += remaining;
                inc += remaining;
                return true;
            }
        }

        @Override
        public void close() throws StoreException{
            ite.close();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

}
