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
 * A FeatureReader is a feature iterator supporting only reading.
 * <br/>
 * The feature reader is the result of a query on a Featurestore, the expected
 * FeatureType can be retrieved with method getFeatureType.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface FeatureReader<T extends FeatureType, F extends Feature> extends FeatureIterator<F>{

    /**
     * Get the reader FeatureType.
     * This type may be abstract to reflect possible variations in the returned 
     * features.
     * 
     * @return the featuretype of all feature returned by this iterator.
     */
    T getFeatureType();

}
