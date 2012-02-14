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
package org.geotoolkit.process.converters;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.SimpleConverter;

/**
 * Implementation of ObjectConverter to convert a path to a file in a String to a
 * FeatureCollection.
 * @author Quentin Boileau
 * @module pending
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
    public Class<? super String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<? extends FeatureCollection> getTargetClass() {
        return FeatureCollection.class;
    }

    @Override
    public FeatureCollection convert(final String s) throws NonconvertibleObjectException {
        if(s == null) throw new NonconvertibleObjectException("Empty FeatureCollection");
        try {
            String url = new String();
            if(s.startsWith("file:")){
                url = s;
            }else{
                url = "file:"+s;
            }
            
            

            final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
            parameters.put("url", new URL(url));

            final DataStore store = DataStoreFinder.get(parameters);
            
            if(store == null){
                throw new NonconvertibleObjectException("Invalid URL");
            }
            
            if(store.getNames().size() != 1){
                throw new NonconvertibleObjectException("More than one FeatureCollection in the file");
            }
            
            final FeatureCollection collection = store.createSession(true).getFeatureCollection(QueryBuilder.all(store.getNames().iterator().next()));

            if(collection != null){
                return collection;
            }else{
                throw new NonconvertibleObjectException("Collection not found");
            }

        } catch (DataStoreException ex) {
            throw new NonconvertibleObjectException(ex);
        } catch (MalformedURLException ex) {
            throw new NonconvertibleObjectException(ex);
        }

    }
}


