/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.geotoolkit.index.Data;

import com.vividsolutions.jts.geom.Envelope;
import java.util.Arrays;

/**
 * Iterator that search the quad tree depth first. 32000 indices are cached at a
 * time and each time a node is visited the indices are removed from the node so
 * that the memory footprint is kept small. Note that if other iterators operate
 * on the same tree then they can interfere with each other.
 * 
 * @author Jesse
 * @module pending
 */
public class LazySearchIterator implements Iterator<Data> {

    private static final int MAX_INDICES = 32768;

    private final DataReader dataReader;
    private final Envelope bounds;

    private Data next = null;
    private Node current;
    private int idIndex = 0;
    private boolean closed;
    private Iterator<Data> data;

    public LazySearchIterator(Node root, DataReader dataReader, Envelope bounds) {
        this.dataReader = dataReader;
        this.current = root;
        this.bounds = bounds;
        this.closed = false;
        this.next = null;
    }

    @Override
    public boolean hasNext() {
        findNext();
        return next != null;
    }

    @Override
    public Data next() {
        findNext();
        if (next == null){
            throw new NoSuchElementException("No more elements available");
        }
        
        final Data temp = next;
        next = null;
        return temp;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void close() throws StoreException {
        this.closed = true;
    }

    private void findNext(){
        if (closed){
            throw new IllegalStateException("Iterator has been closed!");
        }

        if(next != null){
            return;
        }

        //search for the next value.
        if (data != null && data.hasNext()) {
            next = data.next();
        } else {
            fillCache();
            if (data != null && data.hasNext())
                next = data.next();
        }
    }


    private void fillCache() {
        int indexSize = 0;
        final List<Integer> indices = new ArrayList<Integer>();
        final Data[] dataList;

        try {
            while (indexSize < MAX_INDICES && current != null) {

                if (idIndex < current.getNumShapeIds() && !current.isVisited()
                        && current.getBounds().intersects(bounds)) {
                    indices.add(current.getShapeId(idIndex));
                    indexSize++;
                    idIndex++;
                } else {
                    current.setShapesId(new int[0]);
                    idIndex = 0;

                    boolean foundUnvisited = false;
                    for (int i=0,n=current.getNumSubNodes(); i<n ; i++) {
                        final Node node = current.getSubNode(i);
                        if (!node.isVisited() && node.getBounds().intersects(bounds)) {
                            foundUnvisited = true;
                            current = node;
                            break;
                        }
                    }

                    if (!foundUnvisited) {
                        current.setVisited(true);
                        current = current.getParent();
                    }
                }
            }

            dataList = new Data[indexSize];

            // sort so offset lookup is faster
            Collections.sort(indices);
            for (int i=0; i<indexSize; i++) {
                dataList[i] = dataReader.create(indices.get(i));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (StoreException e) {
            throw new RuntimeException(e);
        }

        data = Arrays.asList(dataList).iterator();
    }

}
