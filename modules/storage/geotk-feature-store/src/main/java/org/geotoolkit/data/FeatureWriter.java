/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2009-2012, Geomatys
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

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;

/**
 * A FeatureWriter is a feature iterator allowing both modification and insertion
 * of features.
 * <br/>
 * A FeatureWriter can be created on any FeatureStore which support writing operations.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface FeatureWriter<T extends FeatureType, F extends Feature> extends FeatureIterator<F>{

    /**
     * Get the writer FeatureType.
     * This type may be abstract to reflect possible variations in the returned 
     * features.
     * 
     * @return the featuretype of all feature returned by this iterator.
     */
    T getFeatureType();

    /**
     * Read the next feature, the returned feature can be modify.
     * <br/>
     * If no more feature are available the writer switches to append mode and a new record is created.
     * Behavior is similar to JDBC modifiable ResultSet.
     * <br/>
     * To save the modified feature call the 'write' method.
     * 
     * @return Feature
     * @throws FeatureStoreRuntimeException if error occured while reading the next feature.
     */
    @Override
    F next() throws FeatureStoreRuntimeException;

    /**
     * Delete the current feature.
     * 
     * @throws FeatureStoreRuntimeException if error occured while removing feature.
     */
    @Override
    void remove() throws FeatureStoreRuntimeException;

    /**
     * Save the current modified feature or create a new record if in append mode.
     * 
     * @throws FeatureStoreRuntimeException if error occured while saving feature.
     */
    void write() throws FeatureStoreRuntimeException;

}
