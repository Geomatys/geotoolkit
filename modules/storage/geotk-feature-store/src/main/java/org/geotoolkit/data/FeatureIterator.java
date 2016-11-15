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
package org.geotoolkit.data;

import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.Feature;

/**
 * Extent the Standard Iterator, limited to Feature class
 * and add a close method from interface Closeable that is needed by the feature store
 * to release potential resources.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface FeatureIterator extends CloseableIterator<Feature>{

    /**
     * Reduce possibilities to Feature only.
     * @return Feature
     * @throws FeatureStoreRuntimeException if error occured when reading.
     */
    @Override
    Feature next() throws FeatureStoreRuntimeException;
    
    /**
     * {@inheritDoc }
     * 
     * @return true if there are more features.
     * @throws FeatureStoreRuntimeException 
     */
    @Override
    boolean hasNext() throws FeatureStoreRuntimeException;

}
