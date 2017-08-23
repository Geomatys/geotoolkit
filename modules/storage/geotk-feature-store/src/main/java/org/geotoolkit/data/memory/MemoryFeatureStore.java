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

import org.geotoolkit.data.FeatureStreams;
import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.geometry.jts.JTS;
import org.apache.sis.util.Utilities;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.IllegalNameException;

import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.internal.data.GenericNameIndex;
import org.opengis.feature.PropertyNotFoundException;


/**
 * @todo : make this concurrent
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MemoryFeatureStore extends AbstractFeatureStore{

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private static abstract class Group {
        final FeatureType type;
        final boolean hasIds;

        Group(final FeatureType type){
            this.type = type;
            hasIds = hasIdentifier(type);
        }

        public FeatureType getFeatureType() {
            return type;
        }

    }

    private static class GroupWithId extends Group {
        final AtomicLong incId = new AtomicLong(-1);//first one will be 0
        final Map<Object,Feature> features;

        GroupWithId(final FeatureType type){
            super(type);
            this.features = new ConcurrentHashMap<>();
        }

        public String generateId(){
            while(true){
                final long c = incId.incrementAndGet();
                final String strc = new StringBuilder(getFeatureType().getName()
                        .tip().toString()).append('.').append(c).toString();
                if(!features.containsKey(strc)){
                    return strc;
                }
            }
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
                        throw new FeatureStoreRuntimeException("No more features.");
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

    private static class GroupNoId extends Group {
        final List<Feature> features = new CopyOnWriteArrayList<>();

        GroupNoId(final FeatureType type){
            super(type);
        }

        public Iterator<? extends Feature> createIterator() {
            return features.iterator();
        }
    }

    private final QueryCapabilities capabilities = new DefaultQueryCapabilities(false);
    private final boolean singleTypeLock;
    private final GenericNameIndex<Group> groups = new GenericNameIndex<>();
    private Set<GenericName> nameCache = null;

    public MemoryFeatureStore(){
        super(null);
        singleTypeLock = false;
    }

    /**
     * Memory feature store has no factory
     * @return null
     */
    @Override
    public FeatureStoreFactory getFactory() {
        return null;
    }

    /**
     * Create a memory feature store with a single type.
     *
     * @param type
     * @param singleTypeLock : true if you don't want any other types to be create or
     * this type to be deleted.
     */
    public MemoryFeatureStore(FeatureType type, final boolean singleTypeLock){
        super(null);
        this.singleTypeLock = singleTypeLock;
        final GenericName name = type.getName();
        try {
            groups.add(this, name, hasIdentifier(type) ? new GroupWithId(type) : new GroupNoId(type));
        } catch (IllegalNameException ex) {
            //wont happen
            getLogger().log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized Set<GenericName> getNames() throws DataStoreException {
        if(nameCache == null){
            nameCache = groups.getNames();
        }
        return nameCache;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType(final String name) throws DataStoreException {
        final Group grp = groups.get(this, name);

        if(grp == null){
            throw new DataStoreException("Schema "+ name +" doesnt exist in this feature store.");
        }

        return grp.getFeatureType();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void createFeatureType(final FeatureType type) throws DataStoreException {
        if(singleTypeLock) throw new DataStoreException(
                "Memory feature store is in single type mode. Schema modification are not allowed.");

        ensureNonNull("feature type", type);
        final GenericName name = type.getName();

        if(groups.contains(name.toString())){
            throw new IllegalArgumentException("FeatureType with name : " + type.getName() + " already exist.");
        }

        groups.add(this, name, hasIdentifier(type) ? new GroupWithId(type) : new GroupNoId(type));

        //clear name cache
        nameCache = null;

        //fire event
        fireSchemaAdded(name, type);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void updateFeatureType(final FeatureType newType) throws DataStoreException {
        if(singleTypeLock) throw new DataStoreException(
                "Memory feature store is in single type mode. Schema modification are not allowed.");

        final GenericName typeName = newType.getName();
        //todo must do it a way to avoid destroying all features.

        ensureNonNull("feature type", newType);
        ensureNonNull("name", typeName);

        final Group grp = groups.get(this, typeName.toString());

        groups.remove(this, typeName);

        final FeatureType type = grp.getFeatureType();
        groups.add(this, typeName, hasIdentifier(newType) ? new GroupWithId(newType) : new GroupNoId(newType));

        //clear name cache
        nameCache = null;

        //fire update event
        fireSchemaUpdated(typeName, type, newType);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void deleteFeatureType(final String typeName) throws DataStoreException {
        if(singleTypeLock) throw new DataStoreException(
                "Memory feature store is in single type mode. Schema modification are not allowed.");

        final Group grp = groups.get(this, typeName);

        groups.remove(this, grp.type.getName());

        //clear name cache
        nameCache = null;

        //fire event
        fireSchemaDeleted(grp.getFeatureType().getName(), grp.getFeatureType());
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
    public List<FeatureId> addFeatures(final String groupName, final Collection<? extends Feature> collection,
            final Hints hints) throws DataStoreException {
        typeCheck(groupName);
        final Group grp = groups.get(this, groupName);

        final List<FeatureId> addedIds = new ArrayList<>();
        for(final Feature f : collection){
            Object candidateId = null;
            if(grp instanceof GroupWithId){
                final GroupWithId grpwid = (GroupWithId) grp;
                candidateId = f.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString());
                if(candidateId == null || "".equals(candidateId)){
                    //feature does not have an id, create one
                    candidateId = grpwid.generateId();
                }else{
                    Long test = null;
                    if (candidateId instanceof Number) {
                        test = ((Number) candidateId).longValue();
                    }

                    if(test != null && test < 0){
                        //it's a decremented id value, we replace it
                        candidateId = grpwid.generateId();
                    }else if(grpwid.features.containsKey(candidateId)){
                        //key already used, replace it
                        candidateId = grpwid.generateId();
                    }
                }
                f.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), candidateId);
                addedIds.add(new DefaultFeatureId(String.valueOf(candidateId)));
            }

            //copy the feature
            final Feature copy = FeatureExt.copy(f);

            //force crs definition on each geometry
            for(PropertyType pt : copy.getType().getProperties(true)){
                if(AttributeConvention.isGeometryAttribute(pt)){
                    CoordinateReferenceSystem crs = FeatureExt.getCRS(pt);
                    if(crs==null) continue;
                    Object value = copy.getPropertyValue(pt.getName().toString());
                    if(value instanceof Geometry){
                        try {
                            CoordinateReferenceSystem geomCrs = JTS.findCoordinateReferenceSystem((Geometry) value);
                            if(geomCrs!=null){
                                if(!Utilities.equalsIgnoreMetadata(geomCrs, crs)){
                                    throw new DataStoreException("Geometry "+pt.getName().tip()+" CRS do not match FeatureType CRS");
                                }
                            }else{
                                JTS.setCRS((Geometry)value, geomCrs);
                            }
                        } catch (FactoryException ex) {
                            throw new DataStoreException(ex.getMessage(), ex);
                        }
                    }
                }
            }

            if(grp instanceof GroupWithId){
                ((GroupWithId)grp).features.put(candidateId, copy);
            }else{
                ((GroupNoId)grp).features.add(copy);
            }
        }

        //fire add event
        final Id eventIds = FF.id(new HashSet<Identifier>(addedIds));
        fireFeaturesAdded(grp.type.getName(),eventIds);
        return addedIds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final String groupName, final Filter filter, final Map<String, ?> values) throws DataStoreException {
        typeCheck(groupName);

        //get features which will be modified
        final Group grp = groups.get(this, groupName);

        //ensure crs is set on geometric values
        for(Map.Entry<String, ?> entry : values.entrySet()){
            final String name = entry.getKey();
            final Object value = entry.getValue();

            //ensure the crs is set on the geometry
            if(value instanceof Geometry){
                final PropertyType property = grp.getFeatureType().getProperty(name);
                final CoordinateReferenceSystem crs = FeatureExt.getCRS(property);
                if(crs==null) continue;
                final CoordinateReferenceSystem geomCrs;
                try {
                    geomCrs = JTS.findCoordinateReferenceSystem((Geometry) value);
                    if(geomCrs!=null){
                        if(!Utilities.equalsIgnoreMetadata(geomCrs, crs)){
                            throw new DataStoreException("Geometry "+property.getName().tip()+" CRS do not match FeatureType CRS");
                        }
                    }else{
                        JTS.setCRS((Geometry)value, geomCrs);
                    }
                } catch (FactoryException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            }
        }

        if(grp instanceof GroupWithId){
            final Collection<Identifier> toUpdate = getAffectedFeatures(groupName, filter);
            if(toUpdate.isEmpty()) return;

            final Set<Identifier> ups = new HashSet<>();
            for(final Identifier itd : toUpdate){
                final Feature candidate = ((GroupWithId)grp).features.get(itd.getID());
                if(candidate == null) continue;

                ups.add(itd);
                for(Map.Entry<String, ?> entry : values.entrySet()){
                    final String name = entry.getKey();
                    final Object value = entry.getValue();
                    candidate.setPropertyValue(name, value);
                }
            }

            //fire update event
            final Id eventIds = FF.id(new HashSet<>(ups));
            fireFeaturesUpdated(((GroupWithId) grp).type.getName(),eventIds);
        }else{
            final GroupWithId grpnoid = (GroupWithId) grp;
            for (int i=grpnoid.features.size()-1;i>=0;i--) {
                Feature candidate = grpnoid.features.get(i);
                if (filter.evaluate(candidate)) {
                    for(Map.Entry<String, ?> entry : values.entrySet()){
                        final String name = entry.getKey();
                        final Object value = entry.getValue();
                        candidate.setPropertyValue(name, value);
                    }
                }
            }

            fireFeaturesUpdated(grpnoid.type.getName(),FF.id(new HashSet(Collections.EMPTY_SET)));
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {
        typeCheck(groupName);

        final Group grp = groups.get(this, groupName);
        if(grp instanceof GroupWithId){
            final GroupWithId grpwithid = (GroupWithId) grp;
            final Collection<Identifier> toRemove = getAffectedFeatures(groupName, filter);
            final Set<Identifier> rems = new HashSet<>();
            for(final Identifier itd : toRemove){
                final Feature candidate = grpwithid.features.remove(String.valueOf(itd.getID()));
                if(candidate == null) continue;
                rems.add(itd);
            }
            //fire remove event
            final Id eventIds = FF.id(new HashSet<>(rems));
            fireFeaturesDeleted(grpwithid.type.getName(),eventIds);
        }else{
            final GroupNoId grpnoid = (GroupNoId) grp;
            for (int i=grpnoid.features.size()-1;i>=0;i--) {
                Feature f = grpnoid.features.get(i);
                if (filter.evaluate(f)) {
                    grpnoid.features.remove(i);
                }
            }

            fireFeaturesDeleted(grpnoid.type.getName(),FF.id(new HashSet(Collections.EMPTY_SET)));
        }

    }

    private Collection<Identifier> getAffectedFeatures(final String groupName, final Filter filter) throws DataStoreException{
        final Group grp = groups.get(this, groupName);

        final Collection<Identifier> affected;
        if(filter instanceof Id){
            final Id ids = (Id) filter;
            affected = ids.getIdentifiers();
        }else{
            affected = new ArrayList<>();

            final QueryBuilder qb = new QueryBuilder(groupName);
            qb.setFilter(filter);
            qb.setProperties(new String[]{AttributeConvention.IDENTIFIER_PROPERTY.toString()}); //no properties, only ids
            final FeatureReader reader = getFeatureReader(qb.buildQuery());
            try{
                while(reader.hasNext()){
                    affected.add(FeatureExt.getId(reader.next()));
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
        final Group grp = groups.get(this, query.getTypeName());

        if(grp == null){
            throw new DataStoreException("No featureType for name : " + query.getTypeName());
        }

        //we can handle id filter
        final Filter filter = query.getFilter();
        final QueryBuilder remaining = new QueryBuilder(query);

        final Iterator<? extends Feature> ite;
        if(grp instanceof GroupWithId){
            if(filter instanceof Id){
                ite = ((GroupWithId)grp).createIterator((Id)filter);
                if(ite != null){
                    remaining.setFilter(Filter.INCLUDE);
                }
            }else{
                ite = ((GroupWithId)grp).createIterator(null);
            }
        }else{
            ite = ((GroupNoId)grp).createIterator();
        }

        final FeatureReader reader;

        reader = FeatureStreams.asReader(ite, grp.getFeatureType());

        //fall back on generic parameter handling.
        //todo we should handle at least spatial filter here by using a quadtree.
        return FeatureStreams.subset(reader, remaining.buildQuery());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        return handleWriter(query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws DataStoreException{
        super.close();
        groups.clear();
    }

    @Override
    public void refreshMetaModel() {
    }

    private static boolean hasIdentifier(FeatureType type) {
        try{
            type.getProperty(AttributeConvention.IDENTIFIER_PROPERTY.toString());
            return true;
        }catch(PropertyNotFoundException ex){
            return false;
        }
    }

}
