/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Geomatys
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

import org.geotoolkit.factory.Hints;
import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.geometry.jts.JTS;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 * @todo : make this concurrent
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MemoryDataStore extends AbstractDataStore{
    
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private static class Group{
        final AtomicLong incId = new AtomicLong(-1);//first one will be 0
        final FeatureType type;
        final Map<String,Feature> features;

        Group(final FeatureType type){
            this.type = type;
            this.features = new ConcurrentHashMap<String, Feature>();
        }

        public String generateId(){
            while(true){
                final long c = incId.incrementAndGet();
                final String strc = new StringBuilder(getFeatureType().getName()
                        .getLocalPart()).append('.').append(c).toString();
                if(!features.containsKey(strc)){
                    return strc;
                }
            }
        }

        public FeatureType getFeatureType() {
            return type;
        }

        public Iterator<? extends Feature> createIterator(final Id ids) {

            if(ids == null){
                return features.values().iterator();
            }

            final Set<Identifier> fids = ids.getIdentifiers();
            final Iterator<Identifier> iteIds = fids.iterator();

            return new Iterator<Feature>(){

                Feature next = null;

                @Override
                public boolean hasNext() {
                    findNext();
                    return next != null;
                }

                @Override
                public Feature next() {
                    if(next == null){
                        throw new DataStoreRuntimeException("No more features.");
                    }
                    final Feature candidate = next;
                    next = null;
                    return candidate;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported.");
                }

                private void findNext(){
                    if(next != null) return;

                    while(next == null && iteIds.hasNext()){
                        final String strid = iteIds.next().getID().toString();
                        next = features.get(strid);
                    }

                }

            };
        }
    }

    private final QueryCapabilities capabilities = new DefaultQueryCapabilities(false);
    private final boolean singleTypeLock;
    private final Map<Name,Group> groups = new HashMap<Name, Group>();
    private Set<Name> nameCache = null;

    public MemoryDataStore(){
        super(null);
        singleTypeLock = false;
    }

    /**
     * Create a memory datastore with a single type.
     *
     * @param baseCollection : original collection.
     * @param singleTypeLock : true if you don't want any other types to be create or
     * this type to be deleted.
     */
    public MemoryDataStore(final FeatureType type, final boolean singleTypeLock){
        super(null);
        this.singleTypeLock = singleTypeLock;
        final Name name = type.getName();
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
    public FeatureType getFeatureType(final Name name) throws DataStoreException {
        final Group grp = groups.get(name);

        if(grp == null){
            throw new DataStoreException("Schema "+ name +" doesnt exist in this datastore.");
        }

        return grp.getFeatureType();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void createSchema(final Name name, final FeatureType featureType) throws DataStoreException {
        if(singleTypeLock) throw new DataStoreException(
                "Memory datastore is in single type mode. Schema modification are not allowed.");

        ensureNonNull("feature type", featureType);
        ensureNonNull("name", name);

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
    public synchronized void updateSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        if(singleTypeLock) throw new DataStoreException(
                "Memory datastore is in single type mode. Schema modification are not allowed.");

        //todo must do it a way to avoid destroying all features.

        ensureNonNull("feature type", featureType);
        ensureNonNull("name", typeName);

        final Group grp = groups.remove(typeName);

        if(grp == null){
            throw new IllegalArgumentException("No featureType for name : " + typeName);
        }

        final FeatureType type = grp.getFeatureType();
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
    public synchronized void deleteSchema(final Name typeName) throws DataStoreException {
        if(singleTypeLock) throw new DataStoreException(
                "Memory datastore is in single type mode. Schema modification are not allowed.");

        final Group grp = groups.remove(typeName);

        if(grp == null){
            throw new IllegalArgumentException("No featureType for name : " + typeName);
        }

        //clear name cache
        nameCache = null;

        //fire event
        fireSchemaDeleted(typeName, grp.getFeatureType());
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
    public List<FeatureId> addFeatures(final Name groupName, final Collection<? extends Feature> collection, 
            final Hints hints) throws DataStoreException {
        typeCheck(groupName);
        final Group grp = groups.get(groupName);

        final List<FeatureId> addedIds = new ArrayList<FeatureId>();
        for(final Feature f : collection){
            String candidateId = f.getIdentifier().getID();
            if("".equals(candidateId)){
                //feature does not have an id, create one
                candidateId = grp.generateId();
            }else{
                Long test = null;
                try{
                    test = Long.parseLong(candidateId);
                }catch(NumberFormatException ex){
                    //do nothing
                }

                if(test != null && test < 0){
                    //it's a decremented id value, we replace it
                    candidateId = grp.generateId();
                }else if(grp.features.containsKey(candidateId)){
                    //key already used, replace it
                    candidateId = grp.generateId();
                }
            }

            //copy the feature
            final Feature copy = FeatureUtilities.copy(f,candidateId);

            grp.features.put(candidateId, copy);
            addedIds.add(new DefaultFeatureId(candidateId));
        }

        //fire add event
        final Id eventIds = FF.id(new HashSet<Identifier>(addedIds));
        fireFeaturesAdded(groupName,eventIds);
        return addedIds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final Name groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        typeCheck(groupName);

        final Group grp = groups.get(groupName);
        final Collection<Identifier> toUpdate = getAffectedFeatures(groupName, filter);

        final Set<Identifier> ups = new HashSet<Identifier>();
        for(final Identifier itd : toUpdate){
            final Feature candidate = grp.features.get(itd.getID());
            if(candidate == null) continue;

            ups.add(itd);
            for(Map.Entry<? extends PropertyDescriptor, ? extends Object> entry : values.entrySet()){
                final PropertyDescriptor desc = entry.getKey();
                final Property prop = candidate.getProperty(desc.getName());
                if(prop != null){
                    final Object value = entry.getValue();
                    if(value instanceof Geometry){
                        JTS.setCRS((Geometry)value, ((GeometryDescriptor)desc).getCoordinateReferenceSystem() );
                    }                    
                    prop.setValue(value);
                }
            }
        }

        //fire update event
        final Id eventIds = FF.id(new HashSet<Identifier>(ups));
        fireFeaturesUpdated(groupName,eventIds);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final Name groupName, final Filter filter) throws DataStoreException {
        typeCheck(groupName);

        final Group grp = groups.get(groupName);
        final Collection<Identifier> toRemove = getAffectedFeatures(groupName, filter);

        final Set<Identifier> rems = new HashSet<Identifier>();
        for(final Identifier itd : toRemove){
            final Feature candidate = grp.features.remove(itd.getID());
            if(candidate == null) continue;

            rems.add(itd);
        }

        //fire remove event
        final Id eventIds = FF.id(new HashSet<Identifier>(rems));
        fireFeaturesDeleted(groupName,eventIds);
    }

    private Collection<Identifier> getAffectedFeatures(final Name groupName, final Filter filter) throws DataStoreException{
        final Group grp = groups.get(groupName);

        final Collection<Identifier> affected;
        if(filter instanceof Id){
            final Id ids = (Id) filter;
            affected = ids.getIdentifiers();
        }else{
            affected = new ArrayList<Identifier>();

            final QueryBuilder qb = new QueryBuilder(groupName);
            qb.setFilter(filter);
            qb.setProperties(new Name[0]); //no properties, only ids
            final FeatureReader reader = getFeatureReader(qb.buildQuery());
            try{
                while(reader.hasNext()){
                    affected.add(reader.next().getIdentifier());
                }
            }finally{
                reader.close();
            }
        }
        return affected;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final Group grp = groups.get(query.getTypeName());

        if(grp == null){
            throw new DataStoreException("No featureType for name : " + query.getTypeName());
        }

        //we can handle id filter
        final Filter filter = query.getFilter();
        final QueryBuilder remaining = new QueryBuilder(query);

        final Iterator<? extends Feature> ite;
        if(filter instanceof Id){
            ite = grp.createIterator((Id)filter);
            if(ite != null){
                remaining.setFilter(Filter.INCLUDE);
            }
        }else{
            ite = grp.createIterator(null);
        }

        final FeatureReader reader;
        
        reader = GenericWrapFeatureIterator.wrapToReader(ite, grp.getFeatureType()); 

        //fall back on generic parameter handling.
        //todo we should handle at least spatial filter here by using a quadtree.
        return handleRemaining(reader, remaining.buildQuery());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(final Name typeName, final Filter filter, final Hints hints) throws DataStoreException {
        return handleWriter(typeName, filter, hints);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        super.dispose();
        groups.clear();
    }

}
