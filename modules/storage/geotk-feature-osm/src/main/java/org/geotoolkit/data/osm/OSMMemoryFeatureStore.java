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
import java.util.Collections;
import java.util.Set;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.osm.xml.OSMXMLReader;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryCapabilities;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStreams;

import org.opengis.util.GenericName;

import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.data.osm.model.OSMModelConstants.*;
import org.geotoolkit.storage.DataStoreFactory;
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
    public DataStoreFactory getProvider() {
        return DataStores.getFactoryById(OSMMemoryFeatureStoreFactory.NAME);
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
        return FeatureStreams.subset(fr, query);
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
