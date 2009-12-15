/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.util.NoSuchElementException;

import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.session.ContentException;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Represents an Empty, Typed, FeatureWriter.
 *
 * @author Jody Garnett, Refractions Research
 * @author Johann sorel (Geomatys)
 * @module pending
 */
public class EmptyFeatureWriter<T extends FeatureType, F extends Feature> implements FeatureWriter<T,F> {

    private final T featureType;

    /**
     * An Empty FeatureWriter of the provided <code>featureType</code>.
     *
     * @param featureType
     */
    public EmptyFeatureWriter(final T featureType) {
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
     * {@inheritDoc }
     *
     * This implementation Throws NoSuchElementException as this is an Empty FeatureWrtiter.
     */
    @Override
    public F next() throws NoSuchElementException {
        throw new NoSuchElementException("FeatureWriter is empty");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() throws ContentException {
        throw new ContentException("FeatureWriter is empty and does not support remove()");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write() throws ContentException {
        throw new ContentException("FeatureWriter is empty and does not support write()");
    }

    /**
     * {@inheritDoc }
     *
     * This implementation return always false since it's an empty writer.
     */
    @Override
    public boolean hasNext() {
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() {
    }
    
}
