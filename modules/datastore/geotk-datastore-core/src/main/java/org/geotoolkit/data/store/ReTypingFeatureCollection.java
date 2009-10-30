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
package org.geotoolkit.data.store;

import java.io.IOException;
import java.util.Iterator;

import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.collection.DecoratingFeatureCollection;
import org.geotoolkit.data.collection.DelegateFeatureIterator;
import org.geotoolkit.data.DelegateFeatureReader;

import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.collection.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * FeatureCollection<SimpleFeatureType, SimpleFeature> decorator which decorates a feature collection "re-typing" 
 * its schema based on attributes specified in a query.
 * 
 * @author Justin Deoliveira, The Open Planning Project
 * @module pending
 */
public class ReTypingFeatureCollection extends DecoratingFeatureCollection<SimpleFeatureType, SimpleFeature> {

    private final SimpleFeatureType featureType;

    public ReTypingFeatureCollection(FeatureCollection<SimpleFeatureType, SimpleFeature> delegate, SimpleFeatureType featureType) {
        super(delegate);
        this.featureType = featureType;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType getSchema() {
        return featureType;
    }

    public FeatureReader<SimpleFeatureType, SimpleFeature> reader() throws IOException {
        return new DelegateFeatureReader<SimpleFeatureType, SimpleFeature>(getSchema(), features());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator<SimpleFeature> features() {
        return new DelegateFeatureIterator<SimpleFeature>(this, iterator());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close(FeatureIterator<SimpleFeature> close) {
        close.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Iterator<SimpleFeature> iterator() {
        return new ReTypingIterator(delegate.iterator(), delegate.getSchema(), featureType);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close(Iterator close) {
        ReTypingIterator reType = (ReTypingIterator) close;
        delegate.close(reType.getDelegate());
    }
}
