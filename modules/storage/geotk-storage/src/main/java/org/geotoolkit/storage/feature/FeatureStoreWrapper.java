/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.storage.feature;

import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Query;
import org.geotoolkit.storage.feature.query.QueryCapabilities;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.storage.feature.query.DefaultQueryCapabilities;

/**
 *
 * @author guilhem
 */
public class FeatureStoreWrapper extends AbstractFeatureStore implements FeatureStore {

    private final FeatureSet featureSet;

    public FeatureStoreWrapper(ParameterValueGroup params, FeatureSet featureSet) {
        super(params);
        this.featureSet = featureSet;
    }

    @Override
    public FeatureType getFeatureType(String typeName) throws DataStoreException {
        FeatureType type = featureSet.getType();
        if (typeName != null && typeName.equals(type.getName().toString())) {
            return featureSet.getType();
        }
        return null;
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return new DefaultQueryCapabilities(false, false);
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        return FeatureStreams.asReader(featureSet.subset(query).features(false).iterator(), featureSet.getType());
    }

}
