/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

package org.geotoolkit.storage.feature;

import org.geotoolkit.storage.event.FeatureStoreContentEvent;
import org.geotoolkit.storage.event.FeatureStoreManagementEvent;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.IllegalFeatureTypeException;
import org.apache.sis.storage.ReadOnlyStorageException;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.storage.feature.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.FeatureTypeExt;
import org.geotoolkit.feature.ReprojectMapper;
import org.geotoolkit.feature.TransformMapper;
import org.geotoolkit.feature.ViewMapper;
import org.geotoolkit.geometry.jts.transform.GeometryScaleTransformer;
import org.geotoolkit.storage.event.StorageListener;
import org.geotoolkit.storage.event.StorageListener.Weak;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.ResourceId;
import org.opengis.filter.SortProperty;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractFeatureCollection extends AbstractCollection<Feature>
        implements FeatureCollection, StoreListener<StoreEvent> {

    private final Set<StoreListener> listeners = new HashSet<>();
    private final StorageListener.Weak weakListener = new Weak(this);

    protected NamedIdentifier identifier;
    protected Session session;

    public AbstractFeatureCollection(final String id, Session session){
        this(new NamedIdentifier(NamesExt.create(id)),session);
    }

    public AbstractFeatureCollection(final NamedIdentifier id, Session session){
        ensureNonNull("feature collection id", id);

        this.identifier = id;
        this.session = session;

        if (session != null) {
            weakListener.registerSource(session);
        }

    }

    public void setIdentifier(final NamedIdentifier id) {
        this.identifier = id;
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        return Optional.ofNullable(identifier);
    }

    @Override
    public Session getSession() {
        return session;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final FeatureIterator iterator(){
        return iterator(null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException{
        try {
            return Optional.ofNullable(FeatureStoreUtilities.calculateEnvelope(iterator()));
        } catch (FeatureStoreRuntimeException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() throws FeatureStoreRuntimeException{
        return (int) FeatureStoreUtilities.calculateCount(iterator());
    }

    @Override
    public boolean contains(Object o) {
        final FeatureIterator e = iterator();
        try{
            if (o==null) {
                while (e.hasNext())
                    if (e.next()==null)
                        return true;
            } else {
                while (e.hasNext())
                    if (o.equals(e.next()))
                        return true;
            }
            return false;
        }finally{
            e.close();
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        final Iterator<?> e = c.iterator();
        try{
            while (e.hasNext())
                if (!contains(e.next()))
                    return false;
            return true;
        }finally{
            if(e instanceof CloseableIterator){
                ((CloseableIterator)e).close();
            }
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
    final FeatureIterator e = iterator();
        try{
            while (e.hasNext()) {
                if (!c.contains(e.next())) {
                    e.remove();
                    modified = true;
                }
            }
            return modified;
        }finally{
            e.close();
        }
    }

    @Override
    public void clear() {
        FeatureIterator e = iterator();
        try{
            while (e.hasNext()) {
                e.next();
                e.remove();
            }
        }finally{
            e.close();
        }

    }

    @Override
    public String toString() {
        return "FeatureCollection\n"+String.valueOf(getType());
    }

    @Override
    public void update(Feature feature) throws DataStoreException {
        if(feature == null) return;
        ResourceId filter = FeatureExt.getId(feature);
        final Map<String,Object> map = new HashMap<>();
        for(PropertyType pt : feature.getType().getProperties(true)){
            if(pt instanceof AttributeType){
                map.put(pt.getName().toString(), feature.getPropertyValue(pt.getName().toString()));
            }
        }
        update(filter, map);
    }

    @Override
    public FeatureCollection subset(final Query remainingParameters) throws DataStoreException {

        FeatureCollection result = this;

        final long start = remainingParameters.getOffset();
        final long max = remainingParameters.getLimit();
        final Filter filter = remainingParameters.getSelection();
        final String[] properties = remainingParameters.getPropertyNames();
        final SortProperty[] sorts = remainingParameters.getSortBy();
        final double[] resampling = remainingParameters.getResolution();
        final Hints hints = remainingParameters.getHints();

        //we should take care of wrapping the reader in a correct order to avoid
        //unnecessary calculations. fast and reducing number wrapper should be placed first.
        //but we must not take misunderstanding assumptions neither.
        //exemple : filter is slow than startIndex and MaxFeature but must be placed before
        //          otherwise the result will be illogic.


        //wrap sort by ---------------------------------------------------------
        //This can be really expensive, and force the us to read the full iterator.
        //that may cause out of memory errors.
        if(sorts != null && sorts.length != 0){
            result = FeatureStreams.sort(result, sorts);
        }

        //wrap filter ----------------------------------------------------------
        //we must keep the filter first since it impacts the start index and max feature
        if(filter != null && filter != Filter.include()){
            if(filter == Filter.exclude()){
                //filter that exclude everything, use optimzed reader
                result = FeatureStreams.emptyCollection(result);
            }else{
                result = FeatureStreams.filter(result, filter);
            }
        }

        //wrap start index -----------------------------------------------------
        if(start > 0){
            result = FeatureStreams.skip(result, (int) start);
        }

        //wrap max -------------------------------------------------------------
        if(max != -1){
            if(max == 0){
                //use an optimized reader
                result = FeatureStreams.emptyCollection(result);
            }else{
                result = FeatureStreams.limit(result, (int) max);
            }
        }

        //wrap properties --------------------
        final FeatureType original = result.getType();
        FeatureType mask = original;
        if(properties!=null && FeatureTypeExt.isAllProperties(original, properties)) {
            try {
                result = FeatureStreams.decorate(result,  new ViewMapper(mask, properties));
            } catch (MismatchedFeatureException | IllegalStateException ex) {
                throw new DataStoreException(ex);
            }
        }

        //wrap resampling ------------------------------------------------------
        if(resampling != null){
            final GeometryScaleTransformer trs = new GeometryScaleTransformer(resampling[0], resampling[1]);
            final TransformMapper ttype = new TransformMapper(result.getType(), trs);
            result = FeatureStreams.decorate(result, ttype);
        }

        return result;
    }

    @Override
    public void updateType(FeatureType newType) throws IllegalFeatureTypeException, DataStoreException {
        throw new ReadOnlyStorageException();
    }

    @Override
    public void add(Iterator<? extends Feature> features) throws DataStoreException {
        throw new ReadOnlyStorageException();
    }

    @Override
    public void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) throws DataStoreException {
        throw new ReadOnlyStorageException();
    }

    // fix toArray methods to forced separate features
    @Override
    public Object[] toArray() {
        final List<Object> datas = new ArrayList<>();

        final Hints hints = new Hints();
        final FeatureIterator ite = iterator(hints);
        try{
            while(ite.hasNext()){
                datas.add(ite.next());
            }
        }finally{
            ite.close();
        }

        return datas.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        final List<Object> datas = new ArrayList<>();

        final Hints hints = new Hints();
        final FeatureIterator ite = iterator(hints);
        try{
            while(ite.hasNext()){
                datas.add(ite.next());
            }
        }finally{
            ite.close();
        }

        return datas.toArray(a);
    }

    ////////////////////////////////////////////////////////////////////////////
    // listeners methods ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void eventOccured(StoreEvent event) {
        if (event instanceof FeatureStoreManagementEvent) {
            FeatureStoreManagementEvent fevent = (FeatureStoreManagementEvent) event;
            final FeatureType currentType = getType();

            //forward events only if the collection is typed and match the type name
            if (currentType != null && currentType.getName().equals(fevent.getFeatureTypeName())) {
                fevent = fevent.copy(this);
                final StoreListener[] lst;
                synchronized (listeners) {
                    lst = listeners.toArray(new StoreListener[listeners.size()]);
                }
                for (final StoreListener listener : lst) {
                    listener.eventOccured(fevent);
                }
            }
        } else if(event instanceof FeatureStoreContentEvent) {
            final FeatureStoreContentEvent fevent = (FeatureStoreContentEvent) event;
            final FeatureType currentType = getType();

            //forward events only if the collection is typed and match the type name
            if (currentType != null && currentType.getName().equals(fevent.getFeatureTypeName())) {
                sendEvent(fevent.copy(this));
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Fires a features add event.
     *
     * @param name of the schema where features where added.
     * @param ids modified feature ids.
     */
    protected void fireFeaturesAdded(final GenericName name, final ResourceId ids){
        sendEvent(FeatureStoreContentEvent.createAddEvent(this, name,ids));
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
     * Forward a features event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(final StoreEvent event) {
        final StoreListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StoreListener[listeners.size()]);
        }
        for (final StoreListener listener : lst) {
            listener.eventOccured(event);
        }
    }
}
