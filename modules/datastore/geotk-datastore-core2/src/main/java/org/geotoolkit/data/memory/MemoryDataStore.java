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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.AbstractFeatureWriterAppend;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureIDGenerator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

/**
 * @todo : make this concurrent
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MemoryDataStore extends AbstractDataStore{

    private final Map<Name,FeatureType> types = new HashMap<Name, FeatureType>();
    private final Map<Name,List<Feature>> features = new HashMap<Name, List<Feature>>();
    private final Map<Name,FeatureIDGenerator> idGenerators = new HashMap<Name, FeatureIDGenerator>();
    private Set<Name> nameCache = null;

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized Set<Name> getNames() throws IOException {
        if(nameCache == null){
            nameCache = new HashSet<Name>(types.keySet());
        }
        return nameCache;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getSchema(Name name) throws IOException {
        FeatureType type = types.get(name);

        if(type == null){
            throw new IOException("Schema "+ name +" doesnt exist in this datastore.");
        }

        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void createSchema(Name name, FeatureType featureType) throws IOException {
        if(featureType == null){
            throw new NullPointerException("Feature type can not be null.");
        }
        if(name == null){
            throw new NullPointerException("Name can not be null.");
        }

        if(types.containsKey(name)){
            throw new IllegalArgumentException("FeatureType with name : " + featureType.getName() + " already exist.");
        }

        types.put(name, featureType);
        features.put(name, new ArrayList<Feature>());
        idGenerators.put(name, new MemoryFeatureIDGenerator(name.getLocalPart()));

        //clear name cache
        nameCache = null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void updateSchema(Name typeName, FeatureType featureType) throws IOException {
        //todo must do it a way to avoid destroying all features.
        deleteSchema(typeName);
        createSchema(typeName,featureType);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void deleteSchema(Name typeName) throws IOException {
        final FeatureType type = types.remove(typeName);

        if(type == null){
            throw new IllegalArgumentException("No featureType for name : " + typeName);
        }

        features.remove(typeName);
        idGenerators.remove(typeName);

        //clear name cache
        nameCache = null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long getCount(Query query) throws IOException {
        final List<Feature> lst = features.get(query.getTypeName());

        if(lst == null){
            throw new IllegalArgumentException("No featureType for name : " + query.getTypeName());
        }

        final Integer max = query.getMaxFeatures();
        final Filter filter = query.getFilter();

        //filter should never be null in the query
        if(filter == Filter.INCLUDE){
            if(max != null){
                return Math.max(lst.size(), max);
            }else{
                return lst.size();
            }
        }else if(filter == Filter.EXCLUDE){
            return 0;
        }else{
            int count = 0;

            if(max != null){
                for(int index=0; index <= max; index++){
                    if(filter.evaluate(lst.get(index))) count++;
                }
            }else{
                for(final Feature f :lst){
                    if(filter.evaluate(f)) count++;
                }
            }
            
            return count;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader getFeatureReader(Query query) throws IOException {
        final FeatureType type = getSchema(query.getTypeName());
        final List<Feature> lst = features.get(query.getTypeName());

        if(lst == null){
            throw new IllegalArgumentException("No featureType for name : " + query.getTypeName());
        }

        //fall back on generic parameter handling.
        //todo we should handle at least spatial filter here by using a quadtree.
        return handleRemaining(DataUtilities.reader(lst.iterator(), type), query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws IOException {
        final FeatureType type = getSchema(typeName);
        final FeatureWriter writer = new MemoryFeatureWriter(typeName, type);
        return handleRemaining(writer, filter);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriterAppend(final Name typeName) throws IOException {
        final FeatureType type = getSchema(typeName);
        return new MemoryFeatureWriterAppend(typeName, type);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        super.dispose();
        types.clear();
        features.clear();
        idGenerators.clear();
    }

    private class MemoryFeatureWriterAppend<T extends FeatureType, F extends Feature> extends AbstractFeatureWriterAppend<T,F>{

        private final SimpleFeatureBuilder builder;
        private final FeatureIDGenerator idGenerator;
        private final Name name;
        private F currentFeature = null;

        MemoryFeatureWriterAppend(Name name, T type){
            super(type);
            this.name = name;
            this.builder = new SimpleFeatureBuilder( (SimpleFeatureType)type );
            this.idGenerator = idGenerators.get(name);
        }

        @Override
        public F next() throws DataStoreRuntimeException {
            currentFeature = (F) builder.buildFeature(idGenerator.next().getID());
            return currentFeature;
        }

        @Override
        public void write() throws DataStoreRuntimeException {
            if(currentFeature != null){
                features.get(name).add(currentFeature);
            }
        }

    }

    private class MemoryFeatureWriter<T extends FeatureType, F extends Feature> implements FeatureWriter<T,F>{

        private final T type;
        private final ListIterator<F> iterator;
        private F currentFeature = null;
        private F modified = null;

        MemoryFeatureWriter(Name name, T type){
            this.type = type;
            this.iterator = (ListIterator<F>) features.get(name).listIterator();
        }

        @Override
        public F next() throws DataStoreRuntimeException {
            currentFeature = iterator.next();
            modified = (F)SimpleFeatureBuilder.copy((SimpleFeature) currentFeature);
            return modified;
        }

        @Override
        public void write() throws DataStoreRuntimeException {
            iterator.set(modified);
        }

        @Override
        public T getFeatureType() {
            return type;
        }

        @Override
        public void remove() throws DataStoreRuntimeException {
            iterator.remove();
        }

        @Override
        public void close() {
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

    }

}
