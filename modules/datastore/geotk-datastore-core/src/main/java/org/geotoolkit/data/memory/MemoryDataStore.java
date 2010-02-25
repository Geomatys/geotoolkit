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

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.geotoolkit.data.DefaultFeatureIDReader;
import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.AbstractFeatureWriterAppend;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIDReader;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;

/**
 * @todo : make this concurrent
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MemoryDataStore extends AbstractDataStore{

    private final boolean singleTypeLock;
    private final Map<Name,FeatureType> types = new HashMap<Name, FeatureType>();
    private final Map<Name,Collection<Feature>> features = new HashMap<Name, Collection<Feature>>();
    private final Map<Name,FeatureIDReader> idGenerators = new HashMap<Name, FeatureIDReader>();
    private Set<Name> nameCache = null;

    public MemoryDataStore(){
        singleTypeLock = false;
    }

    /**
     * Create a memory datastore using the given collection.
     * Usefull when you want to benefit the datastore structure from a simple list.
     *
     * @param baseCollection : original collection.
     * @param singleTypeLock : true if you don't want any other types to be create or
     * the collection type to be deleted.
     */
    public MemoryDataStore(FeatureCollection<? extends Feature> baseCollection, boolean singleTypeLock){
        this.singleTypeLock = singleTypeLock;

        if(baseCollection == null){
            throw new NullPointerException("Collection is null.");
        }

        final FeatureType ft = baseCollection.getFeatureType();
        if(ft == null){
            throw new IllegalArgumentException("Can not create memory datastore from untyped feature collection.");
        }

        final Name n = ft.getName();
        types.put(n, ft);
        features.put(n, (Collection<Feature>) baseCollection);

        //todo not 100% safe, we should rely on the basecollection to handle this more properly.
        idGenerators.put(n, new DefaultFeatureIDReader(n.getLocalPart()));

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized Set<Name> getNames() throws DataStoreException {
        if(nameCache == null){
            nameCache = new HashSet<Name>(types.keySet());
        }
        return nameCache;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType(Name name) throws DataStoreException {
        FeatureType type = types.get(name);

        if(type == null){
            throw new DataStoreException("Schema "+ name +" doesnt exist in this datastore.");
        }

        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void createSchema(Name name, FeatureType featureType) throws DataStoreException {
        if(singleTypeLock) throw new DataStoreException(
                "Memory datastore is in single type mode. Schema modification are not allowed.");

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
        features.put(name, new CopyOnWriteArrayList<Feature>());
        idGenerators.put(name, new DefaultFeatureIDReader(name.getLocalPart()));

        //clear name cache
        nameCache = null;

        //fire event
        fireSchemaAdded(name, featureType);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void updateSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        if(singleTypeLock) throw new DataStoreException(
                "Memory datastore is in single type mode. Schema modification are not allowed.");

        //todo must do it a way to avoid destroying all features.

        if(featureType == null){
            throw new NullPointerException("Feature type can not be null.");
        }
        if(typeName == null){
            throw new NullPointerException("Name can not be null.");
        }

        final FeatureType type = types.remove(typeName);

        if(type == null){
            throw new IllegalArgumentException("No featureType for name : " + typeName);
        }

        features.remove(typeName);
        idGenerators.remove(typeName);

        types.put(typeName, featureType);
        features.put(typeName, new ArrayList<Feature>());
        idGenerators.put(typeName, new DefaultFeatureIDReader(typeName.getLocalPart()));

        //clear name cache
        nameCache = null;

        //fire update event
        fireSchemaUpdated(typeName, type, featureType);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void deleteSchema(Name typeName) throws DataStoreException {
        if(singleTypeLock) throw new DataStoreException(
                "Memory datastore is in single type mode. Schema modification are not allowed.");

        final FeatureType type = types.remove(typeName);

        if(type == null){
            throw new IllegalArgumentException("No featureType for name : " + typeName);
        }

        features.remove(typeName);
        idGenerators.remove(typeName);

        //clear name cache
        nameCache = null;

        //fire event
        fireSchemaDeleted(typeName, type);
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
    public long getCount(Query query) throws DataStoreException {
        final Collection<Feature> lst = features.get(query.getTypeName());

        if(lst == null){
            throw new IllegalArgumentException("No featureType for name : " + query.getTypeName());
        }

        final Integer max = query.getMaxFeatures();
        final int startIndex = query.getStartIndex();
        final Filter filter = query.getFilter();
        long size = 0;

        //filter should never be null in the query
        if(filter == Filter.INCLUDE){
            if(max != null){
                size = Math.min(lst.size(), max);
            }else{
                size = lst.size();
            }
        }else if(filter == Filter.EXCLUDE){
            return 0;
        }else{
            long count = 0;

            if(max != null){
                int index=0;
                final Iterator<Feature> ite = lst.iterator();
                while(ite.hasNext() && index <= max){
                    final Feature f = ite.next();
                    if(filter.evaluate(f)) count++;
                    index++;
                }
            }else{
                for(final Feature f :lst){
                    if(filter.evaluate(f)) count++;
                }
            }
            
            size = count;
        }

        //reduce by startIndex, ensure we dont get under 0
        size = Math.max(0, size - startIndex);

        return size;
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures) throws DataStoreException {
        final FeatureType type = getFeatureType(groupName);
        final Collection<Feature> group = this.features.get(groupName);


        final List<FeatureId> ids = new ArrayList<FeatureId>();
        int numAdded = 0;

        if(newFeatures instanceof FeatureCollection && type.equals( ((FeatureCollection)newFeatures).getFeatureType()) ){
            //safe to use the addAll method
            numAdded++;
            group.addAll(newFeatures);
        }else{
            //safe iteration add
            for(Feature f : newFeatures){
                if(f.getType().equals(type)){
                    numAdded++;
                    group.add(f);
                }else{
                    throw new DataStoreException("Trying to add a feature with wrong type : " + f.getType() +" should be : " + type);
                }
            }
        }

        //fire event if needed
        if(numAdded > 0){
            fireFeaturesAdded(groupName);
        }

        return ids;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        final FeatureType type = getFeatureType(groupName);
        final Collection<Feature> group = this.features.get(groupName);

        int numUpdated = 0;

        for(Feature f : group){
            if(filter.evaluate(f)){
                numUpdated++;
                for(Map.Entry<? extends PropertyDescriptor,? extends Object> entry : values.entrySet()){
                    f.getProperty(entry.getKey().getName()).setValue(entry.getValue());
                }
            }
        }

        //fire event if needed
        if(numUpdated > 0){
            fireFeaturesUpdated(groupName);
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        final Collection<Feature> group = this.features.get(groupName);
        
        if(group == null){
            throw new DataStoreException("Type : " + groupName +" do not exist.");
        }
        
        int numRemoved = 0;
        for(Feature f : group){
            if(filter.evaluate(f)){
                numRemoved++;
                group.remove(f);
            }
        }

        //fire event if needed
        if(numRemoved > 0){
            fireFeaturesDeleted(groupName);
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        final FeatureType type = getFeatureType(query.getTypeName());
        final Collection<Feature> lst = features.get(query.getTypeName());

        if(lst == null){
            throw new IllegalArgumentException("No featureType for name : " + query.getTypeName());
        }

        //fall back on generic parameter handling.
        //todo we should handle at least spatial filter here by using a quadtree.
        return handleRemaining(GenericWrapFeatureIterator.wrapToReader(lst.iterator(), type), query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException {
        final FeatureType type = getFeatureType(typeName);
        final FeatureWriter writer = new MemoryFeatureWriter(typeName, type);
        return handleRemaining(writer, filter);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriterAppend(final Name typeName) throws DataStoreException {
        final FeatureType type = getFeatureType(typeName);
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
        private final FeatureIDReader idGenerator;
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
            try {
                currentFeature = (F) builder.buildFeature(idGenerator.next());
            } catch (DataStoreException ex) {
                throw new DataStoreRuntimeException(ex);
            }
            return currentFeature;
        }

        @Override
        public void write() throws DataStoreRuntimeException {
            if(currentFeature != null){
                features.get(name).add(currentFeature);
                //fire event
                fireFeaturesAdded(builder.getFeatureType().getName());
            }
        }

    }

    private class MemoryFeatureWriter<T extends FeatureType, F extends Feature> implements FeatureWriter<T,F>{

        private final T type;
        private final List<F> list;
        private final ListIterator<F> iterator;
        private F currentFeature = null;
        private F modified = null;

        MemoryFeatureWriter(Name name, T type){
            this.type = type;
            this.list =  (List<F>) features.get(name);
            this.iterator = list.listIterator();
        }

        @Override
        public F next() throws DataStoreRuntimeException {
            currentFeature = iterator.next();
            modified = (F)SimpleFeatureBuilder.copy((SimpleFeature) currentFeature);
            return modified;
        }

        @Override
        public void write() throws DataStoreRuntimeException {
            for(Property prop : modified.getProperties()){
                currentFeature.getProperty(prop.getName()).setValue(prop.getValue());
            }
            //fire event
            fireFeaturesUpdated(type.getName());
        }

        @Override
        public T getFeatureType() {
            return type;
        }

        @Override
        public void remove() throws DataStoreRuntimeException {
            list.remove(currentFeature);
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
