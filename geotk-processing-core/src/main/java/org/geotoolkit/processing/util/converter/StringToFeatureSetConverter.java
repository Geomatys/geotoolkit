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

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.feature.util.converter.SimpleConverter;
import org.geotoolkit.storage.DataStores;

/**
 * Implementation of ObjectConverter to convert a path to a file in a String to a
 * FeatureCollection.
 * @author Quentin Boileau
 * @module
 */
public class StringToFeatureSetConverter extends SimpleConverter<String, FeatureSet> {

    private static StringToFeatureSetConverter INSTANCE;

    private StringToFeatureSetConverter(){
    }

    public static StringToFeatureSetConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new StringToFeatureSetConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<FeatureSet> getTargetClass() {
        return FeatureSet.class;
    }

    @Override
    public FeatureSet apply(final String s) throws UnconvertibleObjectException {
        if(s == null) throw new UnconvertibleObjectException("Empty FeatureCollection");
        try {
            String url;
            if(s.startsWith("file:")){
                url = s;
            }else{
                url = "file:"+s;
            }

            final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
            parameters.put(DataStoreProvider.LOCATION, URI.create(url));

            final DataStore store = DataStores.open(parameters);

            if(store == null){
                throw new UnconvertibleObjectException("Invalid URL");
            }

            Collection<FeatureSet> flatten = DataStores.flatten(store, true, FeatureSet.class);

            if(flatten.size() != 1){
                throw new UnconvertibleObjectException("More than one FeatureCollection in the file");
            }

            final FeatureSet collection = flatten.iterator().next();

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


