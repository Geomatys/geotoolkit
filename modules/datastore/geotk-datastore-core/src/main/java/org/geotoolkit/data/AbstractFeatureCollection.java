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

package org.geotoolkit.data;

import java.util.Iterator;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


import org.geotoolkit.data.memory.GenericReprojectFeatureIterator;
import org.geotoolkit.data.memory.GenericTransformFeatureIterator;
import org.geotoolkit.data.memory.GenericRetypeFeatureIterator;
import org.geotoolkit.data.memory.GenericMaxFeatureIterator;
import org.geotoolkit.data.memory.GenericStartIndexFeatureIterator;
import org.geotoolkit.geometry.jts.transform.GeometryScaleTransformer;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.memory.GenericSortByFeatureIterator;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.data.query.Selector;
import org.geotoolkit.data.query.Source;
import org.geotoolkit.data.query.TextStatement;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.storage.DataStoreException;

import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractFeatureCollection<F extends Feature> extends AbstractCollection<F>
        implements FeatureCollection<F>, StorageListener{

    private final Set<StorageListener> listeners = new HashSet<StorageListener>();
    private final StorageListener.Weak weakListener = new Weak(this);

    protected String id;
    protected final Source source;

    public AbstractFeatureCollection(final String id, final Source source){
        ensureNonNull("feature collection id", id);
        ensureNonNull("feature collection source", source);

        this.id = id;
        this.source = source;

        final Collection<Session> sessions = QueryUtilities.getSessions(source, null);
        for (Session s : sessions) {
            weakListener.registerSource(s);
        }

    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Session getSession() {
        if(source instanceof Selector){
            return ((Selector)source).getSession();
        }else if(source instanceof TextStatement){
            return ((TextStatement)source).getSession();
        }else{
            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Source getSource() {
        return source;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isWritable() throws DataStoreRuntimeException {
        try {
            return QueryUtilities.isWritable(source);
        } catch (DataStoreException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final FeatureIterator<F> iterator(){
        return iterator(null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope() throws DataStoreException{
        try{
            return DataUtilities.calculateEnvelope(iterator());
        }catch(DataStoreRuntimeException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() throws DataStoreRuntimeException{
        return (int) DataUtilities.calculateCount(iterator());
    }

    @Override
    public boolean contains(Object o) {
        final FeatureIterator<F> e = iterator();
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
	final FeatureIterator<F> e = iterator();
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
        FeatureIterator<F> e = iterator();
        try{
            while (e.hasNext()) {
                e.next();
                e.remove();
            }
        }finally{
            e.close();
        }
            
    }
    
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void update(final Filter filter, final AttributeDescriptor desc, final Object value) throws DataStoreException {
        update(filter, Collections.singletonMap(desc, value));
    }

    @Override
    public FeatureCollection<F> subCollection(final Query remainingParameters) throws DataStoreException {

        FeatureCollection result = this;

        final Integer start = remainingParameters.getStartIndex();
        final Integer max = remainingParameters.getMaxFeatures();
        final Filter filter = remainingParameters.getFilter();
        final Name[] properties = remainingParameters.getPropertyNames();
        final SortBy[] sorts = remainingParameters.getSortBy();
        final double[] resampling = remainingParameters.getResolution();
        final CoordinateReferenceSystem crs = remainingParameters.getCoordinateSystemReproject();
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
            result = GenericSortByFeatureIterator.wrap(result, sorts);
        }

        //wrap filter ----------------------------------------------------------
        //we must keep the filter first since it impacts the start index and max feature
        if(filter != null && filter != Filter.INCLUDE){
            if(filter == Filter.EXCLUDE){
                //filter that exclude everything, use optimzed reader
                result = GenericEmptyFeatureIterator.wrap(result);
            }else{
                result = GenericFilterFeatureIterator.wrap(result, filter);
            }
        }

        //wrap start index -----------------------------------------------------
        if(start != null && start > 0){
            result = GenericStartIndexFeatureIterator.wrap(result, start);
        }

        //wrap max -------------------------------------------------------------
        if(max != null){
            if(max == 0){
                //use an optimized reader
                result = GenericEmptyFeatureIterator.wrap(result);
            }else{
                result = GenericMaxFeatureIterator.wrap(result, max);
            }
        }

        //wrap properties, remove primary keys if necessary --------------------
        final Boolean hide = (Boolean) hints.get(HintsPending.FEATURE_HIDE_ID_PROPERTY);
        final FeatureType original = result.getFeatureType();
        FeatureType mask = original;
        if(properties != null){
            try {
                mask = FeatureTypeUtilities.createSubType(mask, properties);
            } catch (SchemaException ex) {
                throw new DataStoreException(ex);
            }
        }
        if(hide != null && hide){
            try {
                //remove primary key properties
                mask = FeatureTypeUtilities.excludePrimaryKeyFields(mask);
            } catch (SchemaException ex) {
                throw new DataStoreException(ex);
            }
        }
        if(mask != original){
            result = GenericRetypeFeatureIterator.wrap(result, mask);
        }

        //wrap resampling ------------------------------------------------------
        if(resampling != null){
            result = GenericTransformFeatureIterator.wrap(result,
                    new GeometryScaleTransformer(resampling[0], resampling[1]));
        }

        //wrap reprojection ----------------------------------------------------
        if(crs != null){
            result = GenericReprojectFeatureIterator.wrap(result, crs);
        }

        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // listeners methods ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void structureChanged(StorageManagementEvent event){
        final FeatureType currentType = getFeatureType();

        //forward events only if the collection is typed and match the type name
        if(currentType != null && currentType.getName().equals(event.getFeatureTypeName())){
            event = StorageManagementEvent.resetSource(this, event);
            final StorageListener[] lst;
            synchronized (listeners) {
                lst = listeners.toArray(new StorageListener[listeners.size()]);
            }
            for (final StorageListener listener : lst) {
                listener.structureChanged(event);
            }
        }
    }

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void contentChanged(final StorageContentEvent event){
        final FeatureType currentType = getFeatureType();

        //forward events only if the collection is typed and match the type name
        if(currentType != null && currentType.getName().equals(event.getFeatureTypeName())){
            sendEvent(StorageContentEvent.resetSource(this, event));
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeStorageListener(final StorageListener listener) {
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
    protected void fireFeaturesAdded(final Name name, final Id ids){
        sendEvent(StorageContentEvent.createAddEvent(this, name,ids));
    }

    /**
     * Fires a features update event.
     *
     * @param name of the schema where features where updated.
     * @param ids modified feature ids.
     */
    protected void fireFeaturesUpdated(final Name name, final Id ids){
        sendEvent(StorageContentEvent.createUpdateEvent(this, name, ids));
    }

    /**
     * Fires a features delete event.
     *
     * @param name of the schema where features where deleted
     * @param ids modified feature ids.
     */
    protected void fireFeaturesDeleted(final Name name, final Id ids){
        sendEvent(StorageContentEvent.createDeleteEvent(this, name, ids));
    }

    /**
     * Forward a features event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(final StorageContentEvent event) {
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for (final StorageListener listener : lst) {
            listener.contentChanged(event);
        }
    }

}
