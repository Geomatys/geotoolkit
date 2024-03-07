/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2015, Geomatys
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

package org.geotoolkit.storage.feature.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.filter.privy.FunctionNames;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.filter.visitor.DuplicatingFilterVisitor;
import org.geotoolkit.filter.visitor.SimplifyingFilterVisitor;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.storage.event.FeatureStoreContentEvent;
import org.geotoolkit.storage.event.StorageEvent;
import org.geotoolkit.storage.event.StorageListener;
import org.geotoolkit.storage.feature.DefaultSelectorFeatureCollection;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.feature.FeatureStore;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.feature.FeatureStreams;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.version.Version;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Literal;
import org.opengis.filter.ResourceId;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * This object holds a serie of alteration made against the feature store
 * but thoses are not pushed on the feature store until the commit() method has been called.
 *
 * If we had follow the WFS specification this class would have been named transaction
 * but we choose to use the name Session given by JSR-170 and JSR-283 (Java Content Repository).
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
public final class Session implements Resource, StoreListener<StoreEvent> {

    protected static final FilterFactory FF = FilterUtilities.FF;

    private final StorageListener.Weak weakListener = new StorageListener.Weak(this);
    protected final FeatureStore store;
    protected final Set<StoreListener> listeners = new HashSet<>();

    private final SessionDiff diff;
    private final boolean async;
    private final Version version;

    public Session(final FeatureStore store, final boolean async){
        this(store,async,null);
    }

    public Session(final FeatureStore store, final boolean async, final Version version){
        ensureNonNull("feature store", store);
        this.store = store;
        this.weakListener.registerSource(store);
        this.diff = createDiff();
        this.async = async;
        this.version = version;
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        return Optional.empty();
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the feature store attached to this session.
     * @return FeatureStore, never null
     */
    public FeatureStore getFeatureStore() {
        return store;
    }

    protected SessionDiff createDiff(){
        return new SessionDiff();
    }

    protected AddDelta createAddDelta(Session session, String typeName, Collection<? extends Feature> features){
        return new AddDelta(this, typeName, features);
    }

    protected ModifyDelta createModifyDelta(Session session, String typeName,
            Filter filter , final Map<String,?> values){
        return new ModifyDelta(this, typeName, filter, values);
    }

    protected RemoveDelta createRemoveDelta(Session session, String typeName, Filter filter){
        return new RemoveDelta(session, typeName, filter);
    }

    /**
     * Check if the session is asynchrone.
     * If it is asynchrone then a call to commit is necessary to push all
     * changes to the feature store.
     *
     * @return true if this session is asynchrone
     */
    public boolean isAsynchrone() {
        return async;
    }

    /**
     * Get session version.
     * This version will be used on all queries passing through this session.
     * If a session is set, writing operations will systematicaly raise an exception.
     *
     * @return Version, can be null
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Request a collection of features that match the given query.
     *
     * @param query collections query
     * @return FeatureCollection , never null
     */
    public FeatureCollection getFeatureCollection(final Query query) {
        final NamedIdentifier ident = new NamedIdentifier(NamesExt.create("id"));
        return new DefaultSelectorFeatureCollection(ident, query, this);
    }

    /**
     * Get a feature iterator that can be used only for reading.
     * Use add, update and remove methods for other purposes.
     */
    public FeatureIterator getFeatureIterator(Query original) throws DataStoreException {

        //quick check to bypass deltas
        if(!hasPendingChanges()){
            return store.getFeatureReader(original);
        }

        original = forceCRS(original,false);
        final List<Delta> deltas = diff.getDeltas();

        //we must store the modified queries to iterate on them in reverse order.
        final List<Query> modifieds = new ArrayList<>(deltas.size());
        Query modified = original;
        for(int i=0,n=deltas.size(); i<n; i++){
            final Delta delta = deltas.get(i);
            modifieds.add(modified); //store before modification
            modified = delta.modify(modified);
        }

        FeatureIterator reader = store.getFeatureReader(modified);

        for(int i=0,n=deltas.size(); i<n; i++){
            final Delta delta = deltas.get(i);
            reader = delta.modify(modifieds.get(i),reader);
        }

        //we must preserve the original filter after all thoses modifications
        Filter originalFilter = original.getSelection();
        reader = FeatureStreams.filter(reader, originalFilter);

        return reader;
    }

    /**
     * Same behavior as @see FeatureStore#updateFeatures(org.opengis.feature.type.Name, java.util.Collection)
     * but makes modification in the session diff if this one is asynchrone.
     */
    public void addFeatures(final String groupName, final Collection newFeatures) throws DataStoreException {
        checkVersion();
        //will raise an error if the name doesnt exist
        store.getFeatureType(groupName);

        if(async){
            diff.add(createAddDelta(this, groupName, newFeatures));
            fireSessionChanged();
        }else{
            store.addFeatures(groupName.toString(), newFeatures);
        }
    }

    /**
     * Same behavior as @see FeatureStore#updateFeatures(org.opengis.feature.type.Name, org.opengis.filter.Filter, java.util.Map)
     * but makes modification in the session diff if this one is asynchrone.
     */
    public void updateFeatures(final String groupName, Filter filter, final Map<String,?> values) throws DataStoreException {
        checkVersion();
        //will raise an error if the name doesnt exist
        store.getFeatureType(groupName);

        if(values == null || values.isEmpty()){
            //no modifications, no need to create a modify delta
            //todo should we raise an error ?
            return;
        }

        if(async){
            final Filter modified;

            final SimplifyingFilterVisitor simplifier = SimplifyingFilterVisitor.INSTANCE;
            filter = (Filter) simplifier.visit(filter);

            if(filter instanceof ResourceId){
                modified = filter;
            }else{
                final Set<Filter<Object>> identifiers = new HashSet<>();
                Query qb = new Query(groupName);
                qb.setSelection(filter);
                final FeatureIterator ite = getFeatureIterator(qb);
                try{
                    while(ite.hasNext()){
                        identifiers.add(FeatureExt.getId(ite.next()));
                    }
                }finally{
                    ite.close();
                }
                switch (identifiers.size()) {
                    case 0:  return;     // no feature match this filter, no need to create a modify delta
                    case 1:  modified = identifiers.iterator().next(); break;
                    default: modified = FF.or(identifiers); break;
                }
            }
            diff.add(createModifyDelta(this, groupName, modified, values));
            fireSessionChanged();
        }else{
            store.updateFeatures(groupName, filter, values);
        }
    }

    /**
     * Same behavior as @see FeatureStore#removeFeatures(org.opengis.feature.type.Name, org.opengis.filter.Filter)
     * but makes modification in the session diff if this one is asynchrone.
     */
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {
        checkVersion();
        //will raise an error if the name doesnt exist
        store.getFeatureType(groupName);

        if(async){
            final Filter removed;

            if(filter instanceof ResourceId){
                removed = (ResourceId)filter;
            }else{
                final Set<Filter<Object>> identifiers = new HashSet<>();
                Query qb = new Query(groupName);
                qb.setSelection(filter);
                final FeatureIterator ite = getFeatureIterator(qb);
                try{
                    while(ite.hasNext()){
                        identifiers.add(FeatureExt.getId(ite.next()));
                    }
                }finally{
                    ite.close();
                }
                switch (identifiers.size()) {
                    case 0:  return;     // no feature match this filter, no need to create to remove delta
                    case 1:  removed = identifiers.iterator().next(); break;
                    default: removed = FF.or(identifiers); break;
                }
            }
            diff.add(createRemoveDelta(this, groupName, removed));
            fireSessionChanged();
        }else{
            store.removeFeatures(groupName, filter);
        }
    }

    /**
     * Returns true if this session holds pending (that is, unsaved) changes; otherwise returns false.
     */
    public boolean hasPendingChanges() {
        return !diff.getDeltas().isEmpty();
    }

    /**
     * Apply all the changes made in this session on the featurestore.
     */
    public void commit() throws DataStoreException {
        diff.commit(store);
        fireSessionChanged();
    }

    /**
     * Revert all changes made in this session.
     */
    public void rollback() {
        diff.rollback();
        fireSessionChanged();
    }

    /**
     * Same behavior as @see FeatureStore#getCount(org.geotoolkit.data.query.Query)
     * but take in consideration the session modifications.
     */
    public long getCount(final Query original) throws DataStoreException {
        if(hasPendingChanges()){
            return FeatureStoreUtilities.calculateCount(getFeatureIterator(original));
        }else{
            return store.getCount(original);
        }
    }

    /**
     * Same behavior as @see FeatureStore#getEnvelope(org.geotoolkit.data.query.Query)
     * but take in consideration the session modifications.
     */
    public Envelope getEnvelope(Query original) throws DataStoreException {
        if(hasPendingChanges()){
            return FeatureStoreUtilities.calculateEnvelope(getFeatureIterator(original));
        }else{
            return store.getEnvelope(original);
        }
    }

    protected SessionDiff getDiff() {
        return diff;
    }

    /**
     * Test if a version is set, raise an error if it's the case.
     * Version must not be set when doing writing operations
     */
    protected void checkVersion() throws DataStoreException {
        if(version!=null){
            throw new DataStoreException("Session is opened on version : "+version+". "
                    + "Writing operations are not allowed, open a session without version to support writing.");
        }
    }

    private Query forceCRS(final Query query, boolean replace) throws DataStoreException{
        final FeatureType ft = store.getFeatureType(query.getTypeName());
        final CoordinateReferenceSystem crs = FeatureExt.getCRS(ft);

        if(crs == null){
            return query;
        }

        final Query qb = new Query();
        qb.copy(query);
        qb.setSelection(forceCRS(qb.getSelection(), crs,replace));
        return qb;
    }

    private static Filter forceCRS(final Filter filter, final CoordinateReferenceSystem crs, final boolean replace){

        if (crs == null) return filter;

        final DuplicatingFilterVisitor visitor = new DuplicatingFilterVisitor() {
            {
                setExpressionHandler(FunctionNames.Literal, (e) -> {
                    final Literal expression = (Literal) e;
                    final Object obj = expression.getValue();
                    if (obj instanceof Envelope bb) {
                        if (bb.getCoordinateReferenceSystem() == null) {
                            //force crs definition
                            GeneralEnvelope env = new GeneralEnvelope(bb);
                            env.setCoordinateReferenceSystem(crs);
                            return FF.literal(bb);
                        } else if (replace) {
                            try {
                                //reproject bbox
                                final Envelope env = Envelopes.transform(bb, crs);
                                return FF.literal(env);
                            } catch (TransformException ex) {
                                Logger.getLogger("org.geotoolkit.data.session").log(Level.SEVERE, null, ex);
                            }
                        }
                        return expression;
                    } else if (obj instanceof Geometry) {
                        Geometry geom = (Geometry) obj;
                        try {
                            CoordinateReferenceSystem cdtcrs = JTS.findCoordinateReferenceSystem(geom);
                            if (cdtcrs == null) {
                                geom = (Geometry) geom.clone();
                                JTS.setCRS(geom, crs);
                                return FF.literal(geom);
                            } else if (replace) {
                                //reproject geometry
                                final MathTransform trs = CRS.findOperation(cdtcrs, crs, null).getMathTransform();
                                geom = org.apache.sis.geometry.wrapper.jts.JTS.transform(geom, trs);
                                JTS.setCRS(geom, crs);
                                return FF.literal(geom);
                            }
                        } catch (Exception ex) {
                            Logger.getLogger("org.geotoolkit.data.session").log(Level.WARNING, ex.getLocalizedMessage(), ex);
                            geom = (Geometry) geom.clone();
                            JTS.setCRS(geom, crs);
                            return FF.literal(geom);
                        }
                        return expression;
                    } else {
                        return super.visit(expression);
                    }
                });
            }
        };
        return (Filter) visitor.visit(filter);
    }

    ////////////////////////////////////////////////////////////////////////////
    // listeners methods ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Add a storage listener which will be notified when schema are added, modified or deleted
     * and when features are added, modified or deleted.
     *
     * This includes events from the feature store and events from the session.
     *
     * @param listener to add
     */
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a storage listener
     * @param listener to remove
     */
    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Forward an event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(final StoreEvent event){
        final StoreListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StoreListener[listeners.size()]);
        }
        for (final StoreListener listener : lst) {
            listener.eventOccured(event);
        }
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardEvent(StorageEvent event){
        sendEvent(event.copy((Resource) this));
    }

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void eventOccured(StoreEvent event) {
        if (event instanceof StorageEvent) event = ((StorageEvent)event).copy(this);
        sendEvent(event);
    }
    /**
     * Fires a features add event.
     *
     * @param name of the schema where features where added.
     * @param ids modified feature ids.
     */
    protected void fireFeaturesAdded(final GenericName name, final ResourceId ids){
        sendEvent(FeatureStoreContentEvent.createAddEvent(this, name, ids));
    }

    /**
     * Fires a features update event.
     *
     * @param name of the schema where features where updated.
     * @param ids modified feature ids.
     */
    protected void fireFeaturesUpdated(final GenericName name, final ResourceId ids){
        sendEvent(FeatureStoreContentEvent.createUpdateEvent(this, name, ids));
    }

    /**
     * Fires a features delete event.
     *
     * @param name of the schema where features where deleted
     * @param ids modified feature ids.
     */
    protected void fireFeaturesDeleted(final GenericName name, final ResourceId ids){
        sendEvent(FeatureStoreContentEvent.createDeleteEvent(this, name, ids));
    }

    /**
     * Fires a session event. when new pending changes are added.
     */
    protected void fireSessionChanged(){
        sendEvent(FeatureStoreContentEvent.createSessionEvent(this));
    }
}
