/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.processing.util.converter;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.util.converter.SimpleConverter;
import org.geotoolkit.storage.DataStores;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of ObjectConverter to convert a path to a file in a String to a
 * FeatureCollection.
 * @author Quentin Boileau
 * @module
 */
public class StringToFeatureCollectionConverter extends SimpleConverter<String, FeatureCollection> {

    private static StringToFeatureCollectionConverter INSTANCE;

    private StringToFeatureCollectionConverter(){
    }

    public static StringToFeatureCollectionConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StringToFeatureCollectionConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<FeatureCollection> getTargetClass() {
        return FeatureCollection.class;
    }

    @Override
    public FeatureCollection apply(final String s) throws UnconvertibleObjectException {
        if(s == null) throw new UnconvertibleObjectException("Empty FeatureCollection");
        try {
            String url;
            if(s.startsWith("file:")){
                url = s;
            }else{
                url = "file:"+s;
            }

            final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
            parameters.put(AbstractFileFeatureStoreFactory.PATH.getName().getCode(), URI.create(url));

            final FeatureStore store = (FeatureStore) DataStores.open(parameters);

            if(store == null){
                throw new UnconvertibleObjectException("Invalid URL");
            }

            if(store.getNames().size() != 1){
                throw new UnconvertibleObjectException("More than one FeatureCollection in the file");
            }

            final FeatureCollection collection = store.createSession(true).getFeatureCollection(QueryBuilder.all(store.getNames().iterator().next()));

            if(collection != null){
                return collection;
            }else{
                throw new UnconvertibleObjectException("Collection not found");
            }

        } catch (DataStoreException ex) {
            throw new UnconvertibleObjectException(ex);
        }

    }
}


