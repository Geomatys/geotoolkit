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
package org.geotoolkit.data.collection;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.feature.collection.FeatureIterator;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;


/**
 * A  FeatureReader that wraps up a normal FeatureIterator.
 * <p>
 * This class is useful for faking (and testing) the Resource based
 * API against in memory datastructures. You are warned that to
 * complete the illusion that Resource based IO is occuring content
 * will be duplicated.
 * </p>
 * @author Jody Garnett, Refractions Research, Inc.
 * @source $URL$
 */
public class DelegateFeatureReader<T extends FeatureType, F extends Feature> implements FeatureReader<T, F> {

    private final FeatureIterator<F> delegate;
    private final T schema;

    public DelegateFeatureReader(final T featureType, final FeatureIterator<F> features) {
        if(featureType == null) throw new NullPointerException("Feature type can not be null");
        if(features == null) throw new NullPointerException("Feature iterator can not be null");
        this.schema = featureType;
        this.delegate = features;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T getFeatureType() {
        return schema;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws IOException, IllegalAttributeException, NoSuchElementException {
        return delegate.next();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws IOException {
        return delegate.hasNext();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
