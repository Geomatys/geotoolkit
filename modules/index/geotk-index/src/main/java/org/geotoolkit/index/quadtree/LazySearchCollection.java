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

    private static final int MAX_INDICES = Short.MAX_VALUE;

    private final QuadTree tree;
    private final Envelope bounds;
    private final double[] minRes;

    public LazySearchCollection(QuadTree tree, Envelope bounds) {
        this(tree,bounds,null);
    }

    public LazySearchCollection(QuadTree tree, Envelope bounds, double[] minRes) {
        this.tree = tree;
        this.bounds = bounds;
        this.minRes = minRes;
    }

    /**
     * More accurate iterator when plenty of nodes, also hase a isSafe methode
     * to test if the value is safe to be used untested.
     */
    public SearchIterator<Data> bboxIterator() {
        final SearchIterator<Data> iterator = new LazyTyleSearchIterator.Buffered(
                tree.getRoot(), bounds,minRes,tree.getDataReader(),MAX_INDICES);
        tree.registerIterator(iterator);
        return iterator;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#iterator()
     */
    @Override
    public SearchIterator<Data> iterator() {
        final SearchIterator<Data> iterator = new LazySearchIterator.Buffered(
                tree.getRoot(), bounds,minRes, tree.getDataReader(),MAX_INDICES);
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

}
