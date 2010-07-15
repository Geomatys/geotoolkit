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

import org.geotoolkit.data.shapefile.shp.IndexFile;
import org.geotoolkit.index.Data;
import org.geotoolkit.index.DataDefinition;

import com.vividsolutions.jts.geom.Envelope;

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

    private static final DataDefinition DATA_DEFINITION = new DataDefinition("US-ASCII");

    private static final int MAX_INDICES = 32768;
    static {
        DATA_DEFINITION.addField(Integer.class);
        DATA_DEFINITION.addField(Long.class);
    }

    private final IndexFile indexfile;
    private final Envelope bounds;

    private Data next = null;
    private Node current;
    private int idIndex = 0;
    private boolean closed;
    private Iterator data;

    public LazySearchIterator(Node root, IndexFile indexfile, Envelope bounds) {
        super();
        this.current = root;
        this.bounds = bounds;
        this.closed = false;
        this.next = null;
        this.indexfile = indexfile;
    }

    @Override
    public boolean hasNext() {
        if (closed){
            throw new IllegalStateException("Iterator has been closed!");
        }

        if (next != null){
            return true;
        }

        if (data != null && data.hasNext()) {
            next = (Data) data.next();
        } else {
            fillCache();
            if (data != null && data.hasNext()){
                next = (Data) data.next();
            }
        }
        return next != null;
    }

    private void fillCache() {
        final List<Integer> indices = new ArrayList<Integer>();

        try {
            while (indices.size() < MAX_INDICES && current != null) {
                if (idIndex < current.getNumShapeIds() && !current.isVisited()
                        && current.getBounds().intersects(bounds)) {
                    indices.add(current.getShapeId(idIndex));
                    idIndex++;
                } else {
                    current.setShapesId(new int[0]);
                    idIndex = 0;
                    boolean foundUnvisited = false;
                    for (int i=0,n=current.getNumSubNodes(); i<n; i++) {
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

            // sort so offset lookup is faster
            Collections.sort(indices);
            final List<Data> dataList = new ArrayList<Data>(indices.size());
            for (Integer recno : indices) {
                final Data data = new Data(DATA_DEFINITION);
                data.addValue(recno.intValue() + 1);
                data.addValue(Long.valueOf(indexfile.getOffsetInBytes(recno)));
                dataList.add(data);
            }
            data = dataList.iterator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (StoreException e) {
            throw new RuntimeException(e);
        }
        
    }

    @Override
    public Data next() {
        if (!hasNext()){
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

}
