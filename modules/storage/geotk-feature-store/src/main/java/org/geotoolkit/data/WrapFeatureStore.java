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
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple wrapper of a FeatureStore.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class WrapFeatureStore extends AbstractFeatureStore {

    protected FeatureStore featureStore;

    public WrapFeatureStore (final FeatureStore featureStore) {
        super(featureStore.getConfiguration());
        this.featureStore = featureStore;
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return featureStore.getFactory();
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        return featureStore.getNames();
    }

    @Override
    public void createFeatureType(Name typeName, FeatureType featureType) throws DataStoreException {
        featureStore.createFeatureType(typeName, featureType);
    }

    @Override
    public void updateFeatureType(Name typeName, FeatureType featureType) throws DataStoreException {
        featureStore.updateFeatureType(typeName, featureType);
    }

    @Override
    public void deleteFeatureType(Name typeName) throws DataStoreException {
        featureStore.deleteFeatureType(typeName);
    }

    @Override
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
        return featureStore.getFeatureType(typeName);
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return getQueryCapabilities();
    }

    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        return addFeatures(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        featureStore.updateFeatures(groupName, filter, values);
    }

    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        featureStore.removeFeatures(groupName, filter);
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        return featureStore.getFeatureReader(query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter, Hints hints) throws DataStoreException {
        return featureStore.getFeatureWriter(typeName, filter, hints);
    }

    @Override
    public void refreshMetaModel() {
        featureStore.refreshMetaModel();
    }
}
