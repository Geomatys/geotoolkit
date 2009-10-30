/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Basic support for a  FeatureReader<SimpleFeatureType, SimpleFeature> that limits itself to the number of
 * features passed in.
 *
 * @author Chris Holmes
 * @version $Id$
 * @module pending
 */
public class MaxFeatureReader<T extends FeatureType, F extends Feature> implements DelegatingFeatureReader<T, F> {

    private final FeatureReader<T, F> featureReader;
    private final int maxFeatures;
    private int counter = 0;

    /**
     * Creates a new instance of MaxFeatureReader
     *
     * @param featureReader FeatureReader being maxed
     * @param maxFeatures DOCUMENT ME!
     */
    public MaxFeatureReader(final FeatureReader<T, F> featureReader, final int maxFeatures) {
        this.featureReader = featureReader;
        this.maxFeatures = maxFeatures;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader<T, F> getDelegate() {
        return featureReader;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws IOException, IllegalAttributeException, NoSuchElementException {
        if (hasNext()) {
            counter++;

            return featureReader.next();
        } else {
            throw new NoSuchElementException("No such Feature exists");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws IOException {
        featureReader.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T getFeatureType() {
        return featureReader.getFeatureType();
    }

    /**
     * <p></p>
     *
     * @return <code>true</code> if the featureReader has not passed the max
     *         and still has more features.
     *
     * @throws IOException If the reader we are filtering encounters a problem
     */
    @Override
    public boolean hasNext() throws IOException {
        return (featureReader.hasNext() && (counter < maxFeatures));
    }
}
