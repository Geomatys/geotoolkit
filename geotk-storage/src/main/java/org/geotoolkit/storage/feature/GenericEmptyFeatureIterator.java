/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.storage.feature;

import java.util.NoSuchElementException;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * Basic support for an empty FeatureIterator.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
public class GenericEmptyFeatureIterator implements FeatureIterator {

    /**
     * Creates a new instance of GenericEmptyFeatureIterator
     */
    private GenericEmptyFeatureIterator() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        throw new NoSuchElementException("No such Feature exists");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws FeatureStoreRuntimeException {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        return false;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Can not remove empty feature iterator.");
    }

    /**
     * An empty FeatureReader of the given type.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericEmptyFeatureReader extends GenericEmptyFeatureIterator implements FeatureReader{

        private final FeatureType type;

        private GenericEmptyFeatureReader(final FeatureType type){
            this.type = type;
        }

        @Override
        public FeatureType getFeatureType() {
            return type;
        }

    }

    /**
     * Create an empty FeatureReader of the given type.
     * @param type FeatureType can be null
     */
    public static FeatureReader createReader(final FeatureType type){
        return new GenericEmptyFeatureReader(type);
    }

}
