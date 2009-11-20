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
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.logging.Level;

import org.geotoolkit.data.shapefile.ShapefileDataStoreFactory;
import org.geotoolkit.index.CloseableCollection;
import org.geotoolkit.index.Data;

import com.vividsolutions.jts.geom.Envelope;

/**
 * A collection that will open and close the QuadTree and find the next id in
 * the index.
 * 
 * @author Jesse
 * 
 * @module pending
 */
public class LazySearchCollection extends AbstractCollection<Data> implements
        CloseableCollection<Data> {

    private QuadTree tree;

    private Envelope bounds;

    public LazySearchCollection(QuadTree tree, Envelope bounds) {
        this.tree = tree;
        this.bounds = bounds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#iterator()
     */
    public Iterator<Data> iterator() {
        LazySearchIterator object;
        try {
            object = new LazySearchIterator(tree.getRoot().copy(), tree
                    .getIndexfile(), bounds);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tree.registerIterator(object);
        return object;
    }

    public int size() {
        Iterator iter = iterator();
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
                org.geotoolkit.util.logging.Logging.getLogger(
                        "org.geotoolkit.index.quadtree").severe(
                        "Couldn't close iterator");
            }
        }
    }

    public boolean isEmpty() {
        Iterator iter = iterator();
        boolean isEmtpy = true;
        try {
            isEmtpy = !iter.hasNext();
        } finally {
            try {
                tree.close(iter);
            } catch (StoreException e) {
                org.geotoolkit.util.logging.Logging.getLogger(
                        "org.geotoolkit.index.quadtree").severe(
                        "Couldn't close iterator");
            }
        }
        return isEmtpy;
    }

    public void close() {
        try {
            tree.close();
        } catch (StoreException e) {
            ShapefileDataStoreFactory.LOGGER.log(Level.SEVERE, "Error closing QuadTree", e);
        }
    }

    public void closeIterator( Iterator<Data> iter ) throws IOException {
        try {
            tree.close(iter);
        } catch (StoreException e) {
            throw (IOException) new IOException(e.getLocalizedMessage()).initCause(e);
        }
    }

}
