/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2014, Geomatys
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.memory.GenericFeatureWriter;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.Selector;
import org.geotoolkit.data.query.Source;
import org.geotoolkit.data.session.DefaultSession;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.type.NamesExt;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.parameter.Parameters;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.memory.GenericQueryFeatureIterator;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Uncomplete implementation of a feature store that handle most methods
 * by fallbacking on others. It also offer some generic methods to
 * handle query parameters and events.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractFeatureStore extends FeatureStore {

    /**
     * Static variables refering to GML model.
     */
    public static final String GML_311_NAMESPACE = "http://www.opengis.net/gml";
    public static final String GML_32_NAMESPACE = "http://www.opengis.net/gml/3.2";
    public static final String GML_NAME = "name";
    public static final String GML_DESCRIPTION = "description";

    protected static final String NO_NAMESPACE = "no namespace";

    private final Logger Logger = Logging.getLogger("org.geotoolkit.data");

    protected final ParameterValueGroup parameters;
    protected String defaultNamespace;
    protected final Set<StorageListener> listeners = new HashSet<>();

    protected AbstractFeatureStore(final ParameterValueGroup params) {

        this.parameters = params;
        String namespace = null;
        if(params != null){
            try{
                namespace = (String)Parameters.getOrCreate(AbstractFeatureStoreFactory.NAMESPACE, params).getValue();
            }catch(ParameterNotFoundException ex){
                //ignore this error, factory might not necessarily have a namespace parameter
                //example : gpx
            }
        }

        if (namespace == null) {
            defaultNamespace = "http://geotoolkit.org";
        } else if (namespace.equals(NO_NAMESPACE)) {
            defaultNamespace = null;
        } else {
            defaultNamespace = namespace;
        }
    }

    @Override
    public ParameterValueGroup getConfiguration() {
        return parameters;
    }

    protected String getDefaultNamespace() {
        return defaultNamespace;
    }

    protected Logger getLogger(){
        return Logger;
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return null;
    }

    @Override
    public VersionControl getVersioning(String typeName) throws VersioningException{
        final GenericName n = NamesExt.valueOf(typeName);
        return getVersioning(n);
    }

    /**
     * Overwrite to enable versioning.
     * @param version
     */
    @Override
    public VersionControl getVersioning(GenericName typeName) throws VersioningException{
        throw new VersioningException("Versioning not supported");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final Session createSession(final boolean async) {
        return createSession(async, null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Session createSession(final boolean async, Version version) {
        return new DefaultSession(this, async,version);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getTypeNames() throws DataStoreException {
        final Set<GenericName> names = getNames();
        final Iterator<GenericName> ite = names.iterator();
        final String[] locals = new String[names.size()];
        int i=0;
        while(ite.hasNext()){
            locals[i] = ite.next().tip().toString();
            i++;
        }
        return locals;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType(final String typeName) throws DataStoreException {
        for(final GenericName n : getNames()){
            if(n.tip().toString().equals(typeName)){
                return getFeatureType(n);
            }
        }
        throw new DataStoreException("Schema : " + typeName + "doesn't exist in this feature store.");
    }

    @Override
    public FeatureType getFeatureType(final Query query) throws DataStoreException, MismatchedFeatureException {

        final Source source = query.getSource();

        if(Query.GEOTK_QOM.equalsIgnoreCase(query.getLanguage()) && source instanceof Selector){
            final Selector selector = (Selector) source;
            FeatureType ft = selector.getSession().getFeatureStore().getFeatureType(query.getTypeName());
            ft = FeatureTypeUtilities.createSubType(ft, query.getPropertyNames(), query.getCoordinateSystemReproject());

            final Boolean hide = (Boolean) query.getHints().get(HintsPending.FEATURE_HIDE_ID_PROPERTY);
            if(hide != null && hide){
                ft = FeatureTypeUtilities.excludePrimaryKeyFields(ft);
            }

            return ft;
        }

        throw new DataStoreException("Can not deduce feature type of query : " + query);
    }

    /**
     * Default implementation, will return a list with the single feature tpe from method
     * {@link #getFeatureType(org.geotoolkit.feature.type.Name) }
     *
     * @param typeName
     * @return
     * @throws DataStoreException
     */
    @Override
    public List<ComplexType> getFeatureTypeHierarchy(GenericName typeName) throws DataStoreException {
        return Collections.singletonList((ComplexType)getFeatureType(typeName));
    }

    /**
     * {@inheritDoc }
     *
     * This implementation will try to aquire a writer and return true if it
     * succeed.
     */
    @Override
    public boolean isWritable(final GenericName typeName) throws DataStoreException {
        //while raise an error if type doesnt exist
        getFeatureType(typeName);

        FeatureWriter writer = null;
        try{
            writer = getFeatureWriter(typeName, Filter.EXCLUDE);
            return true;
        }catch(Exception ex){
            //catch anything, log it
            getLogger().log(Level.WARNING, "Type not writable : {0}", ex.getMessage());
            return false;
        }finally{
            if(writer != null){
                writer.close();
            }
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
        return FeatureStoreUtilities.calculateCount(reader);
    }

    /**
     * {@inheritDoc }
     *
     * This implementation will aquiere a reader and iterate to expend an envelope.
     * Subclasses should override this method if they have a faster way to
     * calculate envelope.
     * @throws DataStoreException
     * @throws FeatureStoreRuntimeException
     */
    @Override
    public Envelope getEnvelope(Query query) throws DataStoreException, FeatureStoreRuntimeException {
        // TODO query = addSeparateFeatureHint(query);

        if(query.retrieveAllProperties()){
            //we simplify it, get only geometry attributes + sort attribute
            final FeatureType ft = getFeatureType(query.getTypeName());
            final List<GenericName> names = new ArrayList<GenericName>();
            for(PropertyDescriptor desc : ft.getDescriptors()){
                if(desc instanceof GeometryDescriptor){
                    names.add(desc.getName());
                } else if (query.getSortBy() != null) {
                    for (SortBy sortBy : query.getSortBy()) {
                        final String propName = sortBy.getPropertyName().getPropertyName();
                        if (desc.getName().toString().equals(propName) ||
                            desc.getName().tip().toString().equals(propName)) {
                            names.add(desc.getName());
                        }
                    }
                }
            }

            if(names.isEmpty()){
                //no geometry field
                return null;
            }

            final QueryBuilder qb = new QueryBuilder(query);
            qb.setProperties(names.toArray(new GenericName[names.size()]));
            query = qb.buildQuery();
        }


        final GenericName[] wantedProp = query.getPropertyNames();
        if(wantedProp.length==0){
            return null;
        }

        final FeatureReader reader = getFeatureReader(query);
        return FeatureStoreUtilities.calculateEnvelope(reader);
    }

    private static Query addSeparateFeatureHint(final Query query){
        //hints never null on a query
        Hints hints = query.getHints();
        hints.put(HintsPending.FEATURE_DETACHED, Boolean.FALSE);
        return query;
    }

    @Override
    public final List<FeatureId> addFeatures(GenericName groupName, Collection<? extends Feature> newFeatures) throws DataStoreException {
        return addFeatures(groupName,newFeatures,new Hints());
    }

    /**
     * {@inheritDoc }
     *
     * This implementation fallback on
     * @see  #updateFeatures(org.opengis.feature.type.Name, org.opengis.filter.Filter, java.util.Map)
     */
    @Override
    public void updateFeatures(final GenericName groupName, final Filter filter, final PropertyDescriptor desc, final Object value) throws DataStoreException {
        updateFeatures(groupName, filter, Collections.singletonMap(desc, value));
    }

    @Override
    public final FeatureWriter getFeatureWriter(GenericName typeName, Filter filter) throws DataStoreException {
        return getFeatureWriter(typeName, filter, null);
    }

    /**
     * {@inheritDoc }
     *
     * Generic implementation, will aquiere a featurewriter and iterate to the end of the writer.
     */
    @Override
    public final FeatureWriter getFeatureWriterAppend(final GenericName typeName) throws DataStoreException {
        return getFeatureWriterAppend(typeName,null);
    }

    /**
     * {@inheritDoc }
     *
     * Generic implementation, will aquiere a featurewriter and iterate to the end of the writer.
     */
    @Override
    public FeatureWriter getFeatureWriterAppend(final GenericName typeName,final Hints hints) throws DataStoreException {
        final FeatureWriter writer = getFeatureWriter(typeName,Filter.INCLUDE, hints);

        while (writer.hasNext()) {
            writer.next(); // skip to the end to switch in append mode
        }

        return writer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws DataStoreException {
        synchronized (listeners) {
            listeners.clear();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // listeners methods ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Fires a schema add event to all listeners.
     *
     * @param name added schema name
     * @param type added feature type
     */
    protected void fireSchemaAdded(final GenericName name, final FeatureType type){
        sendStructureEvent(FeatureStoreManagementEvent.createAddEvent(this, name, type));
    }

    /**
     * Fires a schema update event to all listeners.
     *
     * @param name updated schema name
     * @param oldType featuretype before change
     * @param newType featuretype after change
     */
    protected void fireSchemaUpdated(final GenericName name, final FeatureType oldType, final FeatureType newType){
        sendStructureEvent(FeatureStoreManagementEvent.createUpdateEvent(this, name, oldType, newType));
    }

    /**
     * Fires a schema delete event to all listeners.
     *
     * @param name deleted schema name
     * @param type feature type of the deleted schema
     */
    protected void fireSchemaDeleted(final GenericName name, final FeatureType type){
        sendStructureEvent(FeatureStoreManagementEvent.createDeleteEvent(this, name, type));
    }

    /**
     * Fires a features add event.
     *
     * @param name of the schema where features where added.
     * @param ids modified feature ids
     */
    protected void fireFeaturesAdded(final GenericName name, final Id ids){
        sendContentEvent(FeatureStoreContentEvent.createAddEvent(this, name, ids));
    }

    /**
     * Fires a features update event.
     *
     * @param name of the schema where features where updated.
     * @param ids modified feature ids
     */
    protected void fireFeaturesUpdated(final GenericName name, final Id ids){
        sendContentEvent(FeatureStoreContentEvent.createUpdateEvent(this, name, ids));
    }

    /**
     * Fires a features delete event.
     *
     * @param name of the schema where features where deleted
     * @param ids modified feature ids
     */
    protected void fireFeaturesDeleted(final GenericName name, final Id ids){
        sendContentEvent(FeatureStoreContentEvent.createDeleteEvent(this, name, ids));
    }

    ////////////////////////////////////////////////////////////////////////////
    // useful methods for feature store that doesn't implement all query parameters/
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Convinient method to check that the given type name exist.
     * Will raise a datastore exception if the name do not exist in this FeatureStore.
     * @param candidate Name to test.
     * @throws DataStoreException if name do not exist.
     */
    protected void typeCheck(final GenericName candidate) throws DataStoreException{

        final Collection<GenericName> names = getNames();
        if(!names.contains(candidate)){
            final StringBuilder sb = new StringBuilder("Type name : ");
            sb.append(candidate);
            sb.append(" do not exist in this feature store, available names are : ");
            for(final GenericName n : names){
                sb.append(n).append(", ");
            }
            throw new DataStoreException(sb.toString());
        }
    }

    /**
     * Wrap a feature reader with a query.
     * This method can be use if the FeatureStore implementation can not support all
     * filtering parameters. The returned reader will repect the remaining query
     * parameters but keep in mind that this is done in a generic way, which might
     * not be the most effective way.
     *
     * Becareful if you give a sortBy parameter in the query, this can cause
     * OutOfMemory errors since the generic implementation must iterate over all
     * feature and holds them in memory before ordering them.
     * It may be a better solution to say in the query capabilities that sortBy
     * are not handle by this FeatureStore implementation.
     *
     * @param reader FeatureReader to wrap
     * @param remainingParameters , query holding the parameters that where not handle
     * by the FeatureStore implementation
     * @return FeatureReader Reader wrapping the given reader with all query parameters
     * @throws org.apache.sis.storage.DataStoreException
     */
    protected FeatureReader handleRemaining(FeatureReader reader, final Query remainingParameters) throws DataStoreException{
        return GenericQueryFeatureIterator.wrap(reader, remainingParameters);
    }

    /**
     * Wrap a feature writer with a Filter.
     * This method can be used when the featurestore implementation is not
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
    protected List<FeatureId> handleAddWithFeatureWriter(final GenericName groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException{
        try{
            return FeatureStoreUtilities.write(getFeatureWriterAppend(groupName,hints), newFeatures);
        }catch(FeatureStoreRuntimeException ex){
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
    protected void handleUpdateWithFeatureWriter(final GenericName groupName, final Filter filter,
            final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {

        final FeatureWriter writer = getFeatureWriter(groupName,filter);

        try{
            while(writer.hasNext()){
                final Feature f = writer.next();
                for(final Entry<? extends PropertyDescriptor,? extends Object> entry : values.entrySet()){
                    f.getProperty(entry.getKey().getName()).setValue(entry.getValue());
                }
                writer.write();
            }
        } catch(FeatureStoreRuntimeException ex){
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
    protected void handleRemoveWithFeatureWriter(final GenericName groupName, final Filter filter) throws DataStoreException {
        final FeatureWriter writer = getFeatureWriter(groupName,filter);

        try{
            while(writer.hasNext()){
                writer.next();
                writer.remove();
            }
        } catch(FeatureStoreRuntimeException ex){
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
    protected FeatureWriter handleWriter(final GenericName groupName, final Filter filter, final Hints hints) throws DataStoreException {
        return GenericFeatureWriter.wrap(this, groupName, filter);
    }

    protected FeatureWriter handleWriterAppend(final GenericName groupName, final Hints hints) throws DataStoreException {
        return GenericFeatureWriter.wrapAppend(this, groupName);
    }


    public static GenericName ensureGMLNS(final String namespace, final String local){
        if(local.equals(GML_NAME)){
            return NamesExt.create(GML_311_NAMESPACE, GML_NAME);
        }else if(local.equals(GML_DESCRIPTION)){
            return NamesExt.create(GML_311_NAMESPACE, GML_DESCRIPTION);
        }else{
            return NamesExt.create(namespace, local);
        }
    }

    public static FeatureType ensureGMLNS(final FeatureType type){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        ftb.setName(type.getName());

        for(PropertyDescriptor desc : type.getDescriptors()){
            adb.reset();
            adb.copy((AttributeDescriptor) desc);
            if(desc.getName().tip().toString().equals(GML_NAME)){
                adb.setName(GML_311_NAMESPACE, GML_NAME);
                ftb.add(adb.buildDescriptor());
            }else if(desc.getName().tip().toString().equals(GML_DESCRIPTION)){
                adb.setName(GML_311_NAMESPACE, GML_DESCRIPTION);
                ftb.add(adb.buildDescriptor());
            }else{
                ftb.add(desc);
            }
        }

        ftb.setDefaultGeometry(type.getGeometryDescriptor().getName());

        return ftb.buildFeatureType();
    }

    @Override
    public void addStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Forward a structure event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendStructureEvent(final StorageEvent event){
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.structureChanged(event);
        }
    }

    /**
     * Forward a data event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendContentEvent(final StorageEvent event){
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.contentChanged(event);
        }
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardStructureEvent(StorageEvent event){
        sendStructureEvent(event.copy(this));
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardContentEvent(StorageEvent event){
        sendContentEvent(event.copy(this));
    }

}
