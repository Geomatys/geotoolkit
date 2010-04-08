/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.feature.Feature;

/**
 *
 * Default implementation of a FeatureCollectionRow.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultFeatureCollectionRow implements FeatureCollectionRow{

    private final Map<String,Feature> selectorFeatures = new HashMap<String, Feature>();

    public DefaultFeatureCollectionRow(){
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Feature getFeature() throws DataStoreException {
        if(selectorFeatures.size() == 1){
            return selectorFeatures.values().iterator().next();
        }else{
            throw new DataStoreException("Row has several selectors, use getFeature(String selector) to get a feature.");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Feature getFeature(String selector) throws DataStoreException {
        return selectorFeatures.get(selector);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, Feature> getFeatures() throws DataStoreException {
        return selectorFeatures;
    }

}
