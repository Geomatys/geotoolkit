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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.DefaultSimpleFeatureReader;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.util.Converters;

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

    /**
     * a type and it's datas.
     */
    private static class Group{
        final FeatureType type;
        final List<Object[]> datas;
        private final String base;
        private final AtomicLong inc = new AtomicLong();

        public Group(FeatureType type) {
            this.type = type;
            this.datas = new ArrayList<Object[]>();
            this.base = type.getName().getLocalPart()+".";
        }

        public ArrayPropertyRW createPropertyReader(){
            final Collection<PropertyDescriptor> props = type.getDescriptors();
            final PropertyDescriptor[] arr = props.toArray(new PropertyDescriptor[props.size()]);
            return new ArrayPropertyRW(arr, 1, datas);
        }

        public ArrayFIDRW createFIDReader(){
            return new ArrayFIDRW(0, datas , base, inc);
        }

    }

    private final QueryCapabilities capabilities = new DefaultQueryCapabilities(false);
    private final boolean singleTypeLock;
    private final Map<Name,Group> groups = new HashMap<Name, Group>();
    private Set<Name> nameCache = null;

    public MemoryDataStore(){
        singleTypeLock = false;
    }

    /**
     * Create a memory datastore with a single type.
     *
     * @param baseCollection : original collection.
     * @param singleTypeLock : true if you don't want any other types to be create or
     * this type to be deleted.
     */
    public MemoryDataStore(FeatureType type, boolean singleTypeLock){
        this.singleTypeLock = singleTypeLock;
        Name name = type.getName();
        groups.put(name, new Group(type));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized Set<Name> getNames() throws DataStoreException {
        if(nameCache == null){
            nameCache = new HashSet<Name>(groups.keySet());
        }
        return nameCache;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType(Name name) throws DataStoreException {
        final Group grp = groups.get(name);

        if(grp == null){
            throw new DataStoreException("Schema "+ name +" doesnt exist in this datastore.");
        }

        FeatureType type = grp.type;
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

        if(groups.containsKey(name)){
            throw new IllegalArgumentException("FeatureType with name : " + featureType.getName() + " already exist.");
        }

        groups.put(name, new Group(featureType));

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

        final Group grp = groups.remove(typeName);

        if(grp == null){
            throw new IllegalArgumentException("No featureType for name : " + typeName);
        }

        final FeatureType type = grp.type;
        groups.put(typeName, new Group(featureType));


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

        final Group grp = groups.remove(typeName);

        if(grp == null){
            throw new IllegalArgumentException("No featureType for name : " + typeName);
        }

        //clear name cache
        nameCache = null;

        //fire event
        fireSchemaDeleted(typeName, grp.type);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public QueryCapabilities getQueryCapabilities() {
        return capabilities;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> collection) throws DataStoreException {
        final MemoryFeatureWriter writer = (MemoryFeatureWriter)getFeatureWriterAppend(groupName);
        final PropertyDescriptor[] descs = writer.properties.getPropertyDescriptors();

        final List<FeatureId> ids = new ArrayList<FeatureId>();

        try{
            for(final Feature f : collection){
                final Feature candidate = writer.next();
                for(Property property : f.getProperties()){
                    candidate.getProperty(property.getName()).setValue(property.getValue());
                }

                //we are in append mode
                final Object[] vals = new Object[writer.properties.getPropertyCount()+1];

                //use the original id if possible
                vals[0] = f.getIdentifier().getID();
                if(vals[0] == null || vals[0].toString().isEmpty() || containId(writer.grp.datas, vals[0].toString())){
                    vals[0] = writer.modified.getIdentifier();
                }

                for(int i=0; i<descs.length; i++){
                    vals[i+1] = Converters.convert(
                            writer.modified.getProperty(descs[i].getName()).getValue(),
                            descs[i].getType().getBinding());
                }
                writer.grp.datas.add(vals);

                //fire add event
                fireFeaturesAdded(writer.grp.type.getName());

                ids.add(new DefaultFeatureId(vals[0].toString()));
            }
        }finally{
            writer.close();
        }

        return ids;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        final Group grp = groups.get(query.getTypeName());

        if(grp == null){
            throw new DataStoreException("No featureType for name : " + query.getTypeName());
        }

        final DefaultSimpleFeatureReader reader;
        try {
            reader = DefaultSimpleFeatureReader.create(grp.createPropertyReader(), grp.createFIDReader(), (SimpleFeatureType) grp.type, query.getHints());
        } catch (SchemaException ex) {
            throw new DataStoreException(ex);
        }

        //fall back on generic parameter handling.
        //todo we should handle at least spatial filter here by using a quadtree.
        return handleRemaining(reader, query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException {
        final FeatureType type = getFeatureType(typeName);
        final FeatureWriter writer;
        try {
            writer = new MemoryFeatureWriter(typeName);
        } catch (SchemaException ex) {
            throw new DataStoreException(ex);
        }
        return handleRemaining(writer, filter);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        super.dispose();
        groups.clear();
    }

    private static boolean containId(List<Object[]> datas, String id){
        for(Object[] objs : datas){
            if(objs[0].toString().equals(id)) return true;
        }
        return false;
    }

    private class MemoryFeatureWriter<T extends FeatureType, F extends Feature> implements FeatureWriter<T,F>{

        private final Group grp;
        private final DefaultSimpleFeatureReader reader;
        private final ArrayFIDRW ids;
        private final ArrayPropertyRW properties;

        private F currentFeature = null;
        private F modified = null;

        MemoryFeatureWriter(Name name) throws SchemaException{
            this.grp = groups.get(name);
            this.ids = grp.createFIDReader();
            this.properties = grp.createPropertyReader();
            reader = DefaultSimpleFeatureReader.create(
                    properties,
                    ids,
                    (SimpleFeatureType) grp.type,
                    new Hints(HintsPending.FEATURE_DETACHED, Boolean.FALSE));
        }

        @Override
        public F next() throws DataStoreRuntimeException {
            if(hasNext()){
                currentFeature = (F)reader.next();
                modified = (F)SimpleFeatureBuilder.copy((SimpleFeature) currentFeature);
            }else{
                //we are passed the end, now we create new features.
                currentFeature = null;
                try {
                    modified = (F) SimpleFeatureBuilder.template((SimpleFeatureType) grp.type, ids.next());
                } catch (DataStoreException ex) {
                    //should never happen in the memory datastore
                    throw new DataStoreRuntimeException(ex);
                }
            }
            
            return modified;
        }

        @Override
        public void write() throws DataStoreRuntimeException {
            final PropertyDescriptor[] descs = properties.getPropertyDescriptors();

            if(currentFeature == null){
                //we are in append mode
                final Object[] vals = new Object[properties.getPropertyCount()+1];
                vals[0] = modified.getIdentifier();

                for(int i=0; i<descs.length; i++){
                    vals[i+1] = Converters.convert(
                            modified.getProperty(descs[i].getName()).getValue(),
                            descs[i].getType().getBinding());
                }
                grp.datas.add(vals);
                //move the reader forward so that we wont write again on the same feature
                reader.next();

                //fire add event
                fireFeaturesAdded(grp.type.getName());
            }else{
                for(int i=0; i<descs.length; i++){
                    properties.setValue(i, modified.getProperty(descs[i].getName()).getValue());
                }
                //fire modified event
                fireFeaturesUpdated(grp.type.getName());
            }
        }

        @Override
        public T getFeatureType() {
            return (T) grp.type;
        }

        @Override
        public void remove() throws DataStoreRuntimeException {
            if(currentFeature == null){
                throw new DataStoreRuntimeException("Can not remove feature that are in creation.");
            }
            properties.remove();
            ids.remove();
        }

        @Override
        public void close() {
            //reader while close the fid and properties readers.
            reader.close();
        }

        @Override
        public boolean hasNext() {
            return reader.hasNext();
        }

    }

}
