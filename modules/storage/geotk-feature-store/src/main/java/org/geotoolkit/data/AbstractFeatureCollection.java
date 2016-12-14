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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.feature.FeatureTypeExt;
import org.apache.sis.feature.ReprojectFeatureType;
import org.apache.sis.feature.TransformFeatureType;
import org.apache.sis.feature.ViewFeatureType;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.data.memory.GenericMaxFeatureIterator;
import org.geotoolkit.data.memory.GenericSortByFeatureIterator;
import org.geotoolkit.data.memory.GenericStartIndexFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.data.query.Selector;
import org.geotoolkit.data.query.Source;
import org.geotoolkit.data.query.TextStatement;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.geometry.jts.transform.GeometryScaleTransformer;
import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.data.memory.GenericDecoratedFeatureIterator;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractFeatureCollection extends AbstractCollection<Feature>
        implements FeatureCollection, FeatureStoreListener{

    private final Set<StorageListener> listeners = new HashSet<StorageListener>();
    private final FeatureStoreListener.Weak weakListener = new Weak(this);

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
    public boolean isWritable() throws FeatureStoreRuntimeException {
        try {
            return QueryUtilities.isWritable(source);
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
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
    public Envelope getEnvelope() throws DataStoreException{
        try{
            return FeatureStoreUtilities.calculateEnvelope(iterator());
        }catch(FeatureStoreRuntimeException ex){
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
        return "FeatureCollection\n"+String.valueOf(getFeatureType());
    }

    @Override
    public void update(Feature feature) throws DataStoreException {
        if(feature == null) return;
        FeatureId fid = FeatureExt.getId(feature);
        final Filter filter = FactoryFinder.getFilterFactory(null).id(Collections.singleton(fid));

        final Map<String,Object> map = new HashMap<>();
        for(PropertyType pt : feature.getType().getProperties(true)){
            if(pt instanceof AttributeType){
                map.put(pt.getName().toString(), feature.getPropertyValue(pt.getName().toString()));
            }
        }
        update(filter, map);
    }

    @Override
    public FeatureCollection subCollection(final Query remainingParameters) throws DataStoreException {

        FeatureCollection result = this;

        final Integer start = remainingParameters.getStartIndex();
        final Integer max = remainingParameters.getMaxFeatures();
        final Filter filter = remainingParameters.getFilter();
        final String[] properties = remainingParameters.getPropertyNames();
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

        //wrap properties --------------------
        final FeatureType original = result.getFeatureType();
        FeatureType mask = original;
        if(properties!=null && FeatureTypeExt.isAllProperties(original, properties)) {
            try {
                result = GenericDecoratedFeatureIterator.wrap(result,  new ViewFeatureType(mask, properties));
            } catch (MismatchedFeatureException | IllegalStateException ex) {
                throw new DataStoreException(ex);
            }
        }

        //wrap resampling ------------------------------------------------------
        if(resampling != null){
            final GeometryScaleTransformer trs = new GeometryScaleTransformer(resampling[0], resampling[1]);
            final TransformFeatureType ttype = new TransformFeatureType(result.getFeatureType(), trs);
            result = GenericDecoratedFeatureIterator.wrap(result, ttype);
        }

        //wrap reprojection ----------------------------------------------------
        if(crs != null){
            result = GenericDecoratedFeatureIterator.wrap(result, new ReprojectFeatureType(result.getFeatureType(), crs));
        }

        return result;
    }

    // fix toArray methods to forced separate features
    @Override
    public Object[] toArray() {
        final List<Object> datas = new ArrayList<>();

        final Hints hints = new Hints();
        hints.put(HintsPending.FEATURE_DETACHED, Boolean.TRUE);
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
        hints.put(HintsPending.FEATURE_DETACHED, Boolean.TRUE);
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

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void structureChanged(FeatureStoreManagementEvent event){
        final FeatureType currentType = getFeatureType();

        //forward events only if the collection is typed and match the type name
        if(currentType != null && currentType.getName().equals(event.getFeatureTypeName())){
            event = event.copy(this);
            final FeatureStoreListener[] lst;
            synchronized (listeners) {
                lst = listeners.toArray(new FeatureStoreListener[listeners.size()]);
            }
            for (final FeatureStoreListener listener : lst) {
                listener.structureChanged(event);
            }
        }
    }

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void contentChanged(final FeatureStoreContentEvent event){
        final FeatureType currentType = getFeatureType();

        //forward events only if the collection is typed and match the type name
        if(currentType != null && currentType.getName().equals(event.getFeatureTypeName())){
            sendEvent(event.copy(this));
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
    protected void fireFeaturesAdded(final GenericName name, final Id ids){
        sendEvent(FeatureStoreContentEvent.createAddEvent(this, name,ids));
    }

    /**
     * Fires a features update event.
     *
     * @param name of the schema where features where updated.
     * @param ids modified feature ids.
     */
    protected void fireFeaturesUpdated(final GenericName name, final Id ids){
        sendEvent(FeatureStoreContentEvent.createUpdateEvent(this, name, ids));
    }

    /**
     * Fires a features delete event.
     *
     * @param name of the schema where features where deleted
     * @param ids modified feature ids.
     */
    protected void fireFeaturesDeleted(final GenericName name, final Id ids){
        sendEvent(FeatureStoreContentEvent.createDeleteEvent(this, name, ids));
    }

    /**
     * Forward a features event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(final FeatureStoreContentEvent event) {
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for (final StorageListener listener : lst) {
            listener.contentChanged(event);
        }
    }

}
