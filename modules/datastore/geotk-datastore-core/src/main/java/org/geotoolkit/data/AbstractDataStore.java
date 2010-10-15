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

package org.geotoolkit.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.memory.GenericFeatureWriter;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.data.memory.GenericMaxFeatureIterator;
import org.geotoolkit.data.memory.GenericReprojectFeatureIterator;
import org.geotoolkit.data.memory.GenericRetypeFeatureIterator;
import org.geotoolkit.data.memory.GenericSortByFeatureIterator;
import org.geotoolkit.data.memory.GenericStartIndexFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.session.DefaultSession;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractDataStore implements DataStore{

    /**
     * Static variables refering to GML model.
     */
    public static final String GML_NAMESPACE = "http://www.opengis.net/gml";
    public static final String GML_NAME = "name";
    public static final String GML_DESCRIPTION = "description";

    protected static final String NO_NAMESPACE = "no namespace";

    private final Logger Logger = Logging.getLogger(getClass().getPackage().getName());

    private final Set<StorageListener> listeners = new HashSet<StorageListener>();
    private final String defaultNamespace;

    protected AbstractDataStore(final String namespace) {
        if (namespace == null) {
            defaultNamespace = "http://geotoolkit.org";
        } else if (namespace.equals(NO_NAMESPACE)) {
            defaultNamespace = null;
        } else {
            defaultNamespace = namespace;
        }
    }

    protected String getDefaultNamespace() {
        return defaultNamespace;
    }

    protected Logger getLogger(){
        return Logger;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Session createSession(boolean async) {
        return new DefaultSession(this, async);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getTypeNames() throws DataStoreException {
        final Set<Name> names = getNames();
        final Iterator<Name> ite = names.iterator();
        final String[] locals = new String[names.size()];
        int i=0;
        while(ite.hasNext()){
            locals[i] = ite.next().getLocalPart();
            i++;
        }
        return locals;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType(String typeName) throws DataStoreException {
        for(final Name n : getNames()){
            if(n.getLocalPart().equals(typeName)){
                return getFeatureType(n);
            }
        }
        throw new DataStoreException("Schema : " + typeName + "doesnt exist in this datastore.");
    }

    /**
     * {@inheritDoc }
     *
     * This implementation will try to aquiere a writer and return true if it
     * succeed.
     */
    @Override
    public boolean isWritable(Name typeName) throws DataStoreException {
        //while raise an error if type doesnt exist
        getFeatureType(typeName);

        try{
            final FeatureWriter writer = getFeatureWriter(typeName, Filter.EXCLUDE);
            writer.close();
            return true;
        }catch(Exception ex){
            //catch anything, don't log it
            return false;
        }
    }

    /**
     * {@inheritDoc }
     *
     * This implementation will aquiere a reader and iterate to count.
     * Subclasses should override this method if they have a faster way to
     * calculate count.
     */
    @Override
    public long getCount(Query query) throws DataStoreException {
        query = addSeparateFeatureHint(query);
        final FeatureReader reader = getFeatureReader(query);
        return DataUtilities.calculateCount(reader);
    }

    /**
     * {@inheritDoc }
     *
     * This implementation will aquiere a reader and iterate to expend an envelope.
     * Subclasses should override this method if they have a faster way to
     * calculate envelope.
     * @throws DataStoreException 
     * @throws DataStoreRuntimeException
     */
    @Override
    public Envelope getEnvelope(Query query) throws DataStoreException, DataStoreRuntimeException {
        // TODO query = addSeparateFeatureHint(query);
        final FeatureReader reader = getFeatureReader(query);
        return DataUtilities.calculateEnvelope(reader);
    }

    private static Query addSeparateFeatureHint(Query query){
        //hints never null on a query
        Hints hints = query.getHints();
        hints.put(HintsPending.FEATURE_DETACHED, Boolean.FALSE);
        return query;
    }

    /**
     * {@inheritDoc }
     *
     * This implementation fallback on
     * @see  #updateFeatures(org.opengis.feature.type.Name, org.opengis.filter.Filter, java.util.Map)
     */
    @Override
    public void updateFeatures(Name groupName, Filter filter, PropertyDescriptor desc, Object value) throws DataStoreException {
        updateFeatures(groupName, filter, Collections.singletonMap(desc, value));
    }

    /**
     * {@inheritDoc }
     *
     * Generic implementation, will aquiere a featurewriter and iterate to the end of the writer.
     */
    @Override
    public FeatureWriter getFeatureWriterAppend(Name typeName) throws DataStoreException {
        final FeatureWriter writer = getFeatureWriter(typeName,Filter.INCLUDE);

        while (writer.hasNext()) {
            writer.next(); // skip to the end to switch in append mode
        }

        return writer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        synchronized (listeners) {
            listeners.clear();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // listeners methods ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public void addStorageListener(StorageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeStorageListener(StorageListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Fires a schema add event to all listeners.
     * 
     * @param name added schema name
     * @param type added feature type
     */
    protected void fireSchemaAdded(Name name, FeatureType type){
        sendEvent(StorageManagementEvent.createAddEvent(this, name, type));
    }

    /**
     * Fires a schema update event to all listeners.
     *
     * @param name updated schema name
     * @param oldType featuretype before change
     * @param newType featuretype after change
     */
    protected void fireSchemaUpdated(Name name, FeatureType oldType, FeatureType newType){
        sendEvent(StorageManagementEvent.createUpdateEvent(this, name, oldType, newType));
    }

    /**
     * Fires a schema delete event to all listeners.
     *
     * @param name deleted schema name
     * @param type feature type of the deleted schema
     */
    protected void fireSchemaDeleted(Name name, FeatureType type){
        sendEvent(StorageManagementEvent.createDeleteEvent(this, name, type));
    }

    /**
     * Fires a features add event.
     *
     * @param name of the schema where features where added.
     * @param ids modified feature ids
     */
    protected void fireFeaturesAdded(Name name, Id ids){
        sendEvent(StorageContentEvent.createAddEvent(this, name, ids));
    }

    /**
     * Fires a features update event.
     *
     * @param name of the schema where features where updated.
     * @param ids modified feature ids
     */
    protected void fireFeaturesUpdated(Name name, Id ids){
        sendEvent(StorageContentEvent.createUpdateEvent(this, name, ids));
    }

    /**
     * Fires a features delete event.
     *
     * @param name of the schema where features where deleted
     * @param ids modified feature ids
     */
    protected void fireFeaturesDeleted(Name name, Id ids){
        sendEvent(StorageContentEvent.createDeleteEvent(this, name, ids));
    }

    /**
     * Forward a schema event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(StorageManagementEvent event){
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.structureChanged(event);
        }
    }

    /**
     * Forward a features event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(StorageContentEvent event){
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.contentChanged(event);
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // useful methods for datastore that doesn't implement all query parameters/
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Convinient method to check that the given type name exist.
     * Will raise a datastore exception if the name do not exist in this datastore.
     * @param candidate Name to test.
     * @throws DataStoreException if name do not exist.
     */
    protected void typeCheck(Name candidate) throws DataStoreException{

        final Collection<Name> names = getNames();
        if(!names.contains(candidate)){
            final StringBuilder sb = new StringBuilder("Type name : ");
            sb.append(candidate);
            sb.append(" do not exist in this datastore, available names are : ");
            for(final Name n : names){
                sb.append(n).append(", ");
            }
            throw new DataStoreException(sb.toString());
        }
    }

    /**
     * Wrap a feature reader with a query.
     * This method can be use if the datastore implementation can not support all
     * filtering parameters. The returned reader will repect the remaining query
     * parameters but keep in mind that this is done in a generic way, which might
     * not be the most effective way.
     *
     * Becareful if you give a sortBy parameter in the query, this can cause
     * OutOfMemory errors since the generic implementation must iterate over all
     * feature and holds them in memory before ordering them.
     * It may be a better solution to say in the query capabilities that sortBy
     * are not handle by this datastore implementation.
     *
     * @param reader FeatureReader to wrap
     * @param remainingParameters , query holding the parameters that where not handle
     * by the datastore implementation
     * @return FeatureReader Reader wrapping the given reader with all query parameters
     * @throws IOException
     */
    protected FeatureReader handleRemaining(FeatureReader reader, Query remainingParameters) throws DataStoreException{

        final Integer start = remainingParameters.getStartIndex();
        final Integer max = remainingParameters.getMaxFeatures();
        final Filter filter = remainingParameters.getFilter();
        final Name[] properties = remainingParameters.getPropertyNames();
        final SortBy[] sorts = remainingParameters.getSortBy();
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
            reader = GenericSortByFeatureIterator.wrap(reader, sorts);
        }

        //wrap filter ----------------------------------------------------------
        //we must keep the filter first since it impacts the start index and max feature
        if(filter != null && filter != Filter.INCLUDE){
            if(filter == Filter.EXCLUDE){
                //filter that exclude everything, use optimzed reader
                reader = GenericEmptyFeatureIterator.createReader(reader.getFeatureType());
            }else{
                reader = GenericFilterFeatureIterator.wrap(reader, filter);
            }
        }

        //wrap start index -----------------------------------------------------
        if(start != null && start > 0){
            reader = GenericStartIndexFeatureIterator.wrap(reader, start);
        }
        
        //wrap max -------------------------------------------------------------
        if(max != null){
            if(max == 0){
                //use an optimized reader
                reader = GenericEmptyFeatureIterator.createReader(reader.getFeatureType());
            }else{
                reader = GenericMaxFeatureIterator.wrap(reader, max);
            }
        }

        //wrap properties, remove primary keys if necessary --------------------
        final Boolean hide = (Boolean) hints.get(HintsPending.FEATURE_HIDE_ID_PROPERTY);
        final FeatureType original = reader.getFeatureType();
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
            reader = GenericRetypeFeatureIterator.wrap(reader, mask, hints);
        }

        //wrap reprojection ----------------------------------------------------
        if(crs != null){
            try {
                reader = GenericReprojectFeatureIterator.wrap(reader, crs);
            } catch (FactoryException ex) {
                throw new DataStoreException(ex);
            } catch (SchemaException ex) {
                throw new DataStoreException(ex);
            }
        }

        return reader;
    }

    /**
     * Wrap a feature writer with a Filter.
     * This method can be used when the datastore implementation is not
     * intelligent enough to handle filtering.
     *
     * @param writer featureWriter to filter
     * @param filter filter to use for hiding feature while iterating
     * @return Filtered FeatureWriter
     * @throws DataStoreException
     */
    protected FeatureWriter handleRemaining(FeatureWriter writer, Filter filter) throws DataStoreException{

        //wrap filter ----------------------------------------------------------
        if(filter != null && filter != Filter.INCLUDE){
            if(filter == Filter.EXCLUDE){
                //filter that exclude everything, use optimzed writer
                writer.close(); //close the previous writer
                writer = GenericEmptyFeatureIterator.createWriter(writer.getFeatureType());
            }else{
                writer = GenericFilterFeatureIterator.wrap(writer, filter);
            }
        }

        return writer;
    }

    /**
     * Convinient method to handle adding features operation by using the
     * FeatureWriter.
     *
     * @param groupName
     * @param newFeatures
     * @return list of ids of the features added.
     * @throws DataStoreException
     */
    protected List<FeatureId> handleAddWithFeatureWriter(Name groupName, Collection<? extends Feature> newFeatures)
            throws DataStoreException{
        try{
            return DataUtilities.write(getFeatureWriterAppend(groupName), newFeatures);
        }catch(DataStoreRuntimeException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * Convinient method to handle adding features operation by using the
     * FeatureWriter.
     *
     * @param groupName
     * @param filter
     * @param values
     * @throws DataStoreException
     */
    protected void handleUpdateWithFeatureWriter(Name groupName, Filter filter,
            Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {

        final FeatureWriter writer = getFeatureWriter(groupName,filter);

        try{
            while(writer.hasNext()){
                final Feature f = writer.next();
                for(final Entry<? extends PropertyDescriptor,? extends Object> entry : values.entrySet()){
                    f.getProperty(entry.getKey().getName()).setValue(entry.getValue());
                }
                writer.write();
            }
        } catch(DataStoreRuntimeException ex){
            throw new DataStoreException(ex);
        } finally{
            writer.close();
        }
    }

    /**
     * Convinient method to handle adding features operation by using the
     * FeatureWriter.
     * 
     * @param groupName
     * @param filter
     * @throws DataStoreException
     */
    protected void handleRemoveWithFeatureWriter(Name groupName, Filter filter) throws DataStoreException {
        final FeatureWriter writer = getFeatureWriter(groupName,filter);

        try{
            while(writer.hasNext()){
                writer.next();
                writer.remove();
            }
        } catch(DataStoreRuntimeException ex){
            throw new DataStoreException(ex);
        } finally{
            writer.close();
        }
    }

    /**
     * Convinient method to handle modification operation by using the
     * add, remove, update methods.
     *
     * @param groupName
     * @param filter
     * @throws DataStoreException
     */
    protected FeatureWriter handleWriter(Name groupName, Filter filter) throws DataStoreException {
        return GenericFeatureWriter.wrap(this, groupName, filter);
    }

    protected FeatureWriter handleWriterAppend(Name groupName) throws DataStoreException {
        return GenericFeatureWriter.wrapAppend(this, groupName);
    }


    public static Name ensureGMLNS(String namespace, String local){
        if(local.equals(GML_NAME)){
            return new DefaultName(GML_NAMESPACE, GML_NAME);
        }else if(local.equals(GML_DESCRIPTION)){
            return new DefaultName(GML_NAMESPACE, GML_DESCRIPTION);
        }else{
            return new DefaultName(namespace, local);
        }
    }

    public static FeatureType ensureGMLNS(FeatureType type){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        ftb.setName(type.getName());

        for(PropertyDescriptor desc : type.getDescriptors()){
            adb.reset();
            adb.copy((AttributeDescriptor) desc);
            if(desc.getName().getLocalPart().equals(GML_NAME)){
                adb.setName(GML_NAMESPACE, GML_NAME);
                ftb.add(adb.buildDescriptor());
            }else if(desc.getName().getLocalPart().equals(GML_DESCRIPTION)){
                adb.setName(GML_NAMESPACE, GML_DESCRIPTION);
                ftb.add(adb.buildDescriptor());
            }else{
                ftb.add(desc);
            }
        }

        ftb.setDefaultGeometry(type.getGeometryDescriptor().getName());

        return ftb.buildFeatureType();
    }

}
