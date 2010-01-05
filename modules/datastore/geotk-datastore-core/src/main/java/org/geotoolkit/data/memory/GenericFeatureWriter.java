/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.memory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 * Basic support for a  FeatureWriter that redicts it's calls to
 * the more casual methods : addFeatures, removeFeatures and updateFeatures
 * of the datastore.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GenericFeatureWriter<T extends FeatureType, F extends Feature> implements FeatureWriter<T,F> {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    protected final DataStore store;
    protected final Name typeName;
    protected final FeatureReader<T,F> reader;
    protected final T type;
    protected F currentFeature = null;
    protected F modified = null;
    private boolean remove = false;

    private GenericFeatureWriter(final DataStore store, final Name typeName, Filter filter) throws DataStoreException {
        this.store = store;
        this.typeName = typeName;
        reader = store.getFeatureReader(QueryBuilder.filtered(typeName, filter));
        type = (T) store.getFeatureType(typeName);
    }


    @Override
    public T getFeatureType() throws DataStoreRuntimeException{
        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws DataStoreRuntimeException {
        remove = false;
        if(hasNext()){
            currentFeature = reader.next();
            modified = (F) SimpleFeatureBuilder.deep((SimpleFeature) currentFeature);
        }else{
            currentFeature = null;
            modified = (F) SimpleFeatureBuilder.template((SimpleFeatureType) type, "no-id");
        }

        return modified;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws DataStoreRuntimeException {
        reader.close();
        write();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws DataStoreRuntimeException {
        return reader.hasNext();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        remove = true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write() throws DataStoreRuntimeException {
        if(currentFeature != null){
            if(remove){
                //it's a remove operation
            }else{
                //it's a modify operation
                final Filter filter = FF.id(Collections.singleton(currentFeature.getIdentifier()));
                final Map<PropertyDescriptor,Object> values = new HashMap<PropertyDescriptor, Object>();

                for(PropertyDescriptor desc : type.getDescriptors()){
                    final Object original = currentFeature.getProperty(desc.getName()).getValue();
                    final Object mod = modified.getProperty(desc.getName()).getValue();
                    //check if the values was modified
                    if(!safeEqual(original, mod)){
                        //value has changed
                        values.put(desc, mod);
                    }
                }

                if(!values.isEmpty()){
                    try {
                        store.updateFeatures(typeName, filter, values);
                    } catch (DataStoreException ex) {
                        throw new DataStoreRuntimeException(ex);
                    }
                }
            }

        }else{
            if(modified != null){
                //it's an add operation
                try {
                    store.addFeatures(typeName, Collections.singleton(modified));
                } catch (DataStoreException ex) {
                    throw new DataStoreRuntimeException(ex);
                }
            }
        }

        remove = false;
    }

    private boolean safeEqual(Object o1, Object o2){
        if(o1 == null && o2 == null){
            return true;
        }else if(o1 != null){
            return o1.equals(o2);
        }else{
            return o2.equals(o1);
        }
    }

    public static <T extends FeatureType, F extends Feature> FeatureWriter<T,F> wrap(
            DataStore store, Name typeName, Filter filter) throws DataStoreException{
        return new GenericFeatureWriter<T, F>(store, typeName, filter);
    }

    public static <T extends FeatureType, F extends Feature> FeatureWriter<T,F> wrapAppend(
            DataStore store, Name typeName) throws DataStoreException{
        return wrap(store,typeName,Filter.EXCLUDE);
    }

}
