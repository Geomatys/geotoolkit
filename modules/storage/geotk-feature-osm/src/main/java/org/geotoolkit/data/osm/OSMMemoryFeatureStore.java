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
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import static org.geotoolkit.data.osm.model.OSMModelConstants.*;
import org.geotoolkit.data.osm.xml.OSMXMLReader;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

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
        return (DataStoreFactory) DataStores.getProviderById(OSMMemoryFeatureStoreFactory.NAME);
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
        if (!(query instanceof org.geotoolkit.data.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        final FeatureType ft = getFeatureType(gquery.getTypeName());
        FeatureReader fr = memoryStore.getFeatureReader(QueryBuilder.all(gquery.getTypeName()));
        return FeatureStreams.subset(fr, gquery);
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
