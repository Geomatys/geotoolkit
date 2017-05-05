/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2016, Geomatys
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

package org.geotoolkit.data.osm;


import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.osm.xml.OSMXMLReader;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.apache.sis.storage.DataStoreException;

import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;

import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.data.osm.model.OSMModelConstants.*;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * OSM DataStore, holds 3 feature types.
 * - Node
 * - Way
 * - relation
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OSMMemoryFeatureStore extends AbstractFeatureStore{

    private final MemoryFeatureStore memoryStore;

    public OSMMemoryFeatureStore(final ParameterValueGroup params,
            final Object input) throws IOException, XMLStreamException, DataStoreException{
        super(params);
        memoryStore = new MemoryFeatureStore();
        memoryStore.createFeatureType(TYPE_NODE);
        memoryStore.createFeatureType(TYPE_WAY);
        memoryStore.createFeatureType(TYPE_RELATION);

        final OSMXMLReader reader = new OSMXMLReader();
        try{
            reader.setInput(input);
            while(reader.hasNext()){
                final Object obj = reader.next();

                if(obj instanceof Feature){
                    final Feature feature = (Feature) obj;
                    final FeatureType ft = feature.getType();

                    if(!memoryStore.getNames().contains(ft.getName())){
                        memoryStore.createFeatureType(ft);
                    }

                    memoryStore.addFeatures(ft.getName().toString(), Collections.singleton(feature));
                }

            }
        }finally{
            reader.dispose();
        }
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return (FeatureStoreFactory) DataStores.getFactoryById(OSMMemoryFeatureStoreFactory.NAME);
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        return memoryStore.getNames();
    }

    @Override
    public FeatureType getFeatureType(final String typeName) throws DataStoreException {
        return memoryStore.getFeatureType(typeName);
    }

    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final FeatureType ft = getFeatureType(query.getTypeName());
        FeatureReader fr = memoryStore.getFeatureReader(QueryBuilder.all(query.getTypeName()));
        return handleRemaining(fr, query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Not yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // FALLTHROUGHT OR NOT IMPLEMENTED /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public QueryCapabilities getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createFeatureType(final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("New schema creation not allowed on GPX files.");
    }

    @Override
    public void deleteFeatureType(final String typeName) throws DataStoreException {
        throw new DataStoreException("Delete schema not allowed on GPX files.");
    }

    @Override
    public void updateFeatureType(final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Update schema not allowed on GPX files.");
    }

    @Override
    public List<FeatureId> addFeatures(final String groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(final String groupName, final Filter filter, final Map<String, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    @Override
    public void refreshMetaModel() {
    }

}
