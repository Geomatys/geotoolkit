/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.geotoolkit.feature.collection.AbstractFeatureCollection;
import org.geotoolkit.feature.collection.RandomAccessFeatureCollection;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Implement a FeatureCollection<SimpleFeatureType, SimpleFeature> by burning memory!
 * <p>
 * Contents are maintained in a sorted TreeMap by FID, this serves as a reference implementation
 * when exploring the FeatureCollection<SimpleFeatureType, SimpleFeature> api.
 * </p>
 * <p>
 * This is similar to DefaultFeatureCollection, although additional methods are
 * supported and test cases have been written. Unlike DefaultFeatureCollection
 * the type information must be known at construction time.
 * </p>
 *   
 * @author Jody Garnett, Refractions Research
 * @source $URL$
 */
public class MemoryFeatureCollection extends AbstractFeatureCollection implements RandomAccessFeatureCollection {

    TreeMap contents = new TreeMap();

    public MemoryFeatureCollection(SimpleFeatureType schema) {
        super(schema, null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean add(SimpleFeature o) {
        SimpleFeature feature = (SimpleFeature) o;
        contents.put(feature.getID(), feature);
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() {
        return contents.size();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Iterator openIterator() {
        return new MemoryIterator(contents.values().iterator());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void closeIterator(Iterator close) {
        if (close == null) {
            return;
        }

        MemoryIterator it = (MemoryIterator) close;
        it.close();
    }

    class MemoryIterator implements Iterator {

        Iterator it;

        MemoryIterator(Iterator iterator) {
            it = iterator;
        }

        public void close() {
            it = null;
        }

        @Override
        public boolean hasNext() {
            if (it == null) {
                throw new IllegalStateException();
            }
            return it.hasNext();
        }

        @Override
        public Object next() {
            if (it == null) {
                throw new IllegalStateException();
            }
            return it.next();
        }

        @Override
        public void remove() {
            it.remove();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeature getFeatureMember(String id) throws NoSuchElementException {
        if (contents.containsKey(id)) {
            return (SimpleFeature) contents.get(id);
        }
        throw new NoSuchElementException(id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeature removeFeatureMember(String id) {
        if (contents.containsKey(id)) {
            SimpleFeature old = (SimpleFeature) contents.get(id);
            contents.remove(id);
            return old;
        }
        return null;
    }
}
