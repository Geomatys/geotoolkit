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

import java.util.Arrays;
import java.io.IOException;
import org.geotoolkit.index.Data;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import com.vividsolutions.jts.geom.Envelope;
import static org.geotoolkit.index.quadtree.AbstractNode.*;

/**
 * Iterator that search the quad tree depth first. And return each node that match
 * the given bounding box. It stores an addition information to know if each node
 * is contained or just intersect, which mean a more accurate call to intersect
 * on the geometry will be needed.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LazyTyleSearchIterator implements SearchIterator<AbstractNode> {

    private static class Segment{
        private final AbstractNode node;
        int childIndex;
        int relation;

        private Segment(AbstractNode node, int childIndex, int relation){
            this.node = node;
            this.childIndex = childIndex;
            this.relation = relation;
        }

    }

    private final Envelope bounds;
    private final double[] minRes;
    private boolean closed;

    //the current path where we are, Integer is the current visited child node index.
    private final Deque<Segment> path = new ArrayDeque<Segment>(10);

    //curent visited node
    private AbstractNode current = null;
    private boolean safe = false;

    public LazyTyleSearchIterator(AbstractNode node, Envelope bounds, double[] minRes) {
        this.bounds = bounds;
        this.minRes = minRes;
        this.closed = false;

        final int relation = node.relation(bounds);
        if(relation != NONE){
            path.add(new Segment(node, -1, relation));
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
        if(current != null){
            safe = path.getLast().relation == CONTAINED;
        }else{
            safe = false;
        }
        current = null;
        return temp;
    }

    /**
     * @return true if we are sure that the node in contained in the bbox.
     */
    public boolean isSafe(){
        return safe;
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

                //check the node size
                if(!child.isBigger(minRes)){
                    continue childLoop;
                }

                if(segment.relation == CONTAINED){
                    //safe to add all child without test
                    path.addLast(new Segment(child, -1, CONTAINED));
                }else{
                    final int relation = child.relation(bounds);
                    if (relation == NONE) {
                        //not in the area we requested
                        continue childLoop;
                    }else{
                        path.addLast(new Segment(child, -1,relation));
                    }
                }

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
    public static final class Buffered<T extends Data>implements SearchIterator<T>{

        private final int bufferSize;

        private final LazyTyleSearchIterator ite;
        private final DataReader reader;

        //the last node we retrieved
        private AbstractNode node = null;
        private int[] nodeIds = null;
        private int inc = 0;

        private final int[] indices;
        private final Data[] datas;
        private final boolean[] safes;
        private int indexSize = 0;
        private int index = 0;

        private T next = null;
        private boolean safe = false;

        public Buffered(AbstractNode node, Envelope bounds, double[] minRes, DataReader reader, int bufferSize){
            this.ite = new LazyTyleSearchIterator(node, bounds, minRes);
            this.bufferSize = bufferSize;
            this.reader = reader;
            indices = new int[bufferSize];
            datas = new Data[bufferSize];
            safes = new boolean[bufferSize];
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

        public boolean isSafe() {
            return safe;
        }

        public void findNext(){

            //next one already prepared
            if(next!= null){
                return;
            }

            //next one in the cache
            if(index<indexSize){
                next = (T) datas[index];
                safe = safes[index];
                //prepare next one
                index++;
                return;
            }

            index = 0;
            fillIndices();

            // sort so offset lookup is faster
            sort1(indices,safes,0,indexSize);
            try{
                reader.read(indices, datas, indexSize);
            }catch(IOException ex) {
                throw new RuntimeException(ex);
            }

            if(indexSize>0){
                next = (T) datas[0];
                safe = safes[0];
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
                Arrays.fill(safes, indexSize, indexSize+remaining, ite.isSafe());
                indexSize += remaining;
                node = null;
                return false;
            }else{
                //copy part of the ids
                remaining = bufferSize-indexSize;
                System.arraycopy(nodeIds, inc, indices, indexSize, remaining);
                Arrays.fill(safes, indexSize, indexSize+remaining, ite.isSafe());
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




    /**
     * Sorts the specified sub-array of integers into ascending order.
     * Apply the same sorting on the boolean array.
     */
    private static void sort1(int[] x,boolean[] safes, int off, int len) {
	// Insertion sort on smallest arrays
	if (len < 7) {
	    for (int i=off; i<len+off; i++)
		for (int j=i; j>off && x[j-1]>x[j]; j--)
		    swap(x,safes, j, j-1);
	    return;
	}

	// Choose a partition element, v
	int m = off + (len >> 1);       // Small arrays, middle element
	if (len > 7) {
	    int l = off;
	    int n = off + len - 1;
	    if (len > 40) {        // Big arrays, pseudomedian of 9
		int s = len/8;
		l = med3(x, l,     l+s, l+2*s);
		m = med3(x, m-s,   m,   m+s);
		n = med3(x, n-2*s, n-s, n);
	    }
	    m = med3(x, l, m, n); // Mid-size, med of 3
	}
	int v = x[m];

	// Establish Invariant: v* (<v)* (>v)* v*
	int a = off, b = a, c = off + len - 1, d = c;
	while(true) {
	    while (b <= c && x[b] <= v) {
		if (x[b] == v)
		    swap(x,safes, a++, b);
		b++;
	    }
	    while (c >= b && x[c] >= v) {
		if (x[c] == v)
		    swap(x,safes, c, d--);
		c--;
	    }
	    if (b > c)
		break;
	    swap(x,safes, b++, c--);
	}

	// Swap partition elements back to middle
	int s, n = off + len;
	s = Math.min(a-off, b-a  );  vecswap(x,safes, off, b-s, s);
	s = Math.min(d-c,   n-d-1);  vecswap(x,safes, b,   n-s, s);

	// Recursively sort non-partition-elements
	if ((s = b-a) > 1)
	    sort1(x,safes, off, s);
	if ((s = d-c) > 1)
	    sort1(x,safes, n-s, s);
    }

    /**
     * Swaps x[a] with x[b].
     */
    private static void swap(int x[],boolean[] safes, int a, int b) {
	int t = x[a];
	x[a] = x[b];
	x[b] = t;

        boolean k = safes[a];
	safes[a] = safes[b];
	safes[b] = k;
    }

    /**
     * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].
     */
    private static void vecswap(int x[],boolean[] safes, int a, int b, int n) {
	for (int i=0; i<n; i++, a++, b++)
	    swap(x, safes,a, b);
    }

    /**
     * Returns the index of the median of the three indexed integers.
     */
    private static int med3(int x[], int a, int b, int c) {
	return (x[a] < x[b] ?
		(x[b] < x[c] ? b : x[a] < x[c] ? c : a) :
		(x[b] > x[c] ? b : x[a] > x[c] ? c : a));
    }


}
