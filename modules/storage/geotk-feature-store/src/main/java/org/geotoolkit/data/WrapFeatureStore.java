/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.apache.sis.storage.DataStoreException;
import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.geotoolkit.storage.DataStoreFactory;

/**
 * Simple wrapper of a FeatureStore.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class WrapFeatureStore extends AbstractFeatureStore {

    protected FeatureStore featureStore;

    public WrapFeatureStore (final FeatureStore featureStore) {
        super(featureStore.getOpenParameters());
        this.featureStore = featureStore;
    }

    @Override
    public DataStoreFactory getProvider() {
        return featureStore.getProvider();
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        return featureStore.getNames();
    }

    @Override
    public void createFeatureType(FeatureType featureType) throws DataStoreException {
        featureStore.createFeatureType(featureType);
    }

    @Override
    public void updateFeatureType(FeatureType featureType) throws DataStoreException {
        featureStore.updateFeatureType(featureType);
    }

    @Override
    public void deleteFeatureType(String typeName) throws DataStoreException {
        featureStore.deleteFeatureType(typeName);
    }

    @Override
    public FeatureType getFeatureType(String typeName) throws DataStoreException {
        return featureStore.getFeatureType(typeName);
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return getQueryCapabilities();
    }

    @Override
    public List<FeatureId> addFeatures(String groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        return addFeatures(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(String groupName, Filter filter, Map<String, ?> values) throws DataStoreException {
        featureStore.updateFeatures(groupName, filter, values);
    }

    @Override
    public void removeFeatures(String groupName, Filter filter) throws DataStoreException {
        featureStore.removeFeatures(groupName, filter);
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        return featureStore.getFeatureReader(query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        return featureStore.getFeatureWriter(query);
    }

    @Override
    public void refreshMetaModel() throws DataStoreException {
        featureStore.refreshMetaModel();
    }
}
