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

import java.util.NoSuchElementException;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Represents an Empty, Typed, FeatureReader.
 *
 * @author Jody Garnett, Refractions Research
 * @source $URL$
 * @module pending
 */
public class EmptyFeatureReader<T extends FeatureType, F extends Feature> implements FeatureReader<T, F> {

    private final T featureType;

    /**
     * An Empty  FeatureReader<SimpleFeatureType, SimpleFeature> of the provided <code>featureType</code>.
     *
     * @param featureType
     */
    public EmptyFeatureReader(final T featureType) {
        this.featureType = featureType;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T getFeatureType() {
        return featureType;
    }

    /**
     * Throws NoSuchElementException as this is an Empty FeatureReader.
     *
     * @return Does not return
     *
     * @throws NoSuchElementException
     *
     * @see org.geotoolkit.data.FeatureReader#next()
     */
    @Override
    public F next() throws NoSuchElementException {
        throw new NoSuchElementException("FeatureReader is empty");
    }

    /**
     * There is no next Feature.
     *
     * @return <code>false</code>
     *
     * @see org.geotoolkit.data.FeatureReader#hasNext()
     */
    @Override
    public boolean hasNext() {
        return false;
    }

    /**
     * Cleans up after Empty FeatureReader.
     *
     * @see org.geotoolkit.data.FeatureReader#close()
     */
    @Override
    public void close() {
    }
}
