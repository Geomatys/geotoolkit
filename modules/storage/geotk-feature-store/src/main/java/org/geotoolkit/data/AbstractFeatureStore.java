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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.storage.MetadataBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.storage.WritableAggregate;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.memory.GenericFeatureWriter;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.DefaultSession;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.FeatureTypeExt;
import org.geotoolkit.feature.ReprojectMapper;
import org.geotoolkit.feature.ViewMapper;
import org.geotoolkit.storage.DataStore;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;
import org.opengis.util.ScopedName;


/**
 * Uncomplete implementation of a feature store that handle most methods
 * by fallbacking on others. It also offer some generic methods to
 * handle query parameters and events.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractFeatureStore extends DataStore implements FeatureStore, WritableAggregate {

    /**
     * Static variables refering to GML model.
     */
    public static final String GML_311_NAMESPACE = "http://www.opengis.net/gml";
    public static final String GML_32_NAMESPACE = "http://www.opengis.net/gml/3.2";
    public static final String GML_NAME = "name";
    public static final String GML_DESCRIPTION = "description";

    protected static final String NO_NAMESPACE = "no namespace";

    private static final Logger logger = Logging.getLogger("org.geotoolkit.data");

    protected final Parameters parameters;
    protected final Set<ChangeListener> listeners = new HashSet<>();

    protected AbstractFeatureStore(final ParameterValueGroup params) {
        this.parameters = Parameters.castOrWrap(params);
    }

    @Override
    public Parameters getOpenParameters() {
        return parameters;
    }

    protected Logger getLogger(){
        return logger;
    }

    @Override
    public GenericName getIdentifier() throws DataStoreException {
        return null;
    }

    @Override
    protected Metadata createMetadata() throws DataStoreException {
        final MetadataBuilder builder = new MetadataBuilder();
        String name = "";
        if (getProvider() != null) {
            name = getProvider().getOpenParameters().getName().getCode();
        }
        builder.addIdentifier(null, name, MetadataBuilder.Scope.ALL);

        try {
            for (org.apache.sis.storage.Resource r : components()) {
                if (!(r instanceof FeatureSet)) continue;
                final FeatureSet fs = (FeatureSet) r;
                final FeatureType type = fs.getType();
                builder.addFeatureType(type, null);
                final CoordinateReferenceSystem crs = FeatureExt.getCRS(type);
                if (crs!=null) builder.addReferenceSystem(crs);
            }
        } catch (BackingStoreException e) {
            throw e.unwrapOrRethrow(DataStoreException.class);
        }

        return builder.build(false);
    }

    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        final List<org.apache.sis.storage.Resource> resources = new ArrayList<>();
        for (GenericName name : getNames()) {
            resources.add(create(name));
        }
        return resources;
    }

    /**
     * Create a feature set to allow browsing of a particular identified type
     * collection from this store.
     *
     * @implNote Default implementation only return a {@link DefaultFeatureResource},
     * so you can override it to return a more optimized object.
     *
     * @param resourceName Name of the data type we want to browse.
     * @return A new feature set, should never be null.
     * @throws DataStoreException If we cannot create a connector to the queried
     * data type.
     */
    protected FeatureSet create(final GenericName resourceName) throws DataStoreException {
        return new DefaultFeatureResource(this, resourceName);
    }

    /**
     * Get a collection of all available names.
     * @return {@literal Set<Name>}, never null, but can be empty.
     * @throws org.apache.sis.storage.DataStoreException
     */
    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        final Set<GenericName> names = new HashSet<>();
        listNames(this, names);
        return names;
    }

    private static void listNames(Resource resource, Set<GenericName> names) throws DataStoreException {
        names.add(resource.getIdentifier());
        if (resource instanceof Aggregate) {
            final Aggregate ds = (Aggregate) resource;
            for (Resource rs : ds.components()) {
                listNames(rs, names);
            }
        }
    }

    /**
     * Overwrite to enable versioning.
     */
    @Override
    public VersionControl getVersioning(String typeName) throws VersioningException{
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

    @Override
    public FeatureType getFeatureType(final Query query) throws DataStoreException, MismatchedFeatureException {
        if (!(query instanceof org.geotoolkit.data.query.Query))  throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;

        FeatureType ft = getFeatureType(gquery.getTypeName());
        final String[] properties = gquery.getPropertyNames();
        if (properties!=null && FeatureTypeExt.isAllProperties(ft, properties)) {
            ft = new ViewMapper(ft, properties).getMappedType();
        }
        if(gquery.getCoordinateSystemReproject()!=null){
            ft = new ReprojectMapper(ft, gquery.getCoordinateSystemReproject()).getMappedType();
        }

        return ft;
    }

    /**
     * Default implementation, will return a list with the single feature tpe from method
     * {@link #getFeatureType(org.geotoolkit.feature.type.Name) }
     */
    @Override
    public List<FeatureType> getFeatureTypeHierarchy(String typeName) throws DataStoreException {
        return Collections.singletonList((FeatureType)getFeatureType(typeName));
    }

    /**
     * {@inheritDoc }
     *
     * This implementation will try to aquire a writer and return true if it
     * succeed.
     */
    @Override
    public boolean isWritable(final String typeName) throws DataStoreException {
        //while raise an error if type doesnt exist
        getFeatureType(typeName);

        FeatureWriter writer = null;
        try{
            writer = getFeatureWriter(QueryBuilder.filtered(typeName, Filter.EXCLUDE));
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
        if (!(query instanceof org.geotoolkit.data.query.Query))  throw new UnsupportedQueryException();

        org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        final FeatureReader reader = getFeatureReader(gquery);
        return FeatureStoreUtilities.calculateCount(reader);
    }

    /**
     * {@inheritDoc }
     *
     * This implementation will aquiere a reader and iterate to expend an envelope.
     * Subclasses should override this method if they have a faster way to
     * calculate envelope.
     */
    @Override
    public Envelope getEnvelope(Query query) throws DataStoreException, FeatureStoreRuntimeException {
        if (!(query instanceof org.geotoolkit.data.query.Query))  throw new UnsupportedQueryException();

        org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;

        if(gquery.retrieveAllProperties()){
            //we simplify it, get only geometry attributes + sort attribute
            final FeatureType ft = getFeatureType(gquery.getTypeName());
            final List<String> names = new ArrayList<>();
            for(PropertyType desc : ft.getProperties(true)){
                if(AttributeConvention.isGeometryAttribute(desc)){
                    names.add(desc.getName().toString());
                } else if (gquery.getSortBy() != null) {
                    for (SortBy sortBy : gquery.getSortBy()) {
                        final String propName = sortBy.getPropertyName().getPropertyName();
                        if (desc.getName().toString().equals(propName) ||
                            desc.getName().tip().toString().equals(propName)) {
                            names.add(desc.getName().toString());
                        }
                    }
                }
            }

            if(names.isEmpty()){
                //no geometry field
                return null;
            }

            final QueryBuilder qb = new QueryBuilder(gquery);
            qb.setProperties(names.toArray(new String[names.size()]));
            gquery = qb.buildQuery();
        }


        final String[] wantedProp = gquery.getPropertyNames();
        if(wantedProp.length==0){
            return null;
        }

        final FeatureReader reader = getFeatureReader(gquery);
        return FeatureStoreUtilities.calculateEnvelope(reader);
    }

    @Override
    public final List<FeatureId> addFeatures(String groupName, Collection<? extends Feature> newFeatures) throws DataStoreException {
        return addFeatures(groupName,newFeatures,new Hints());
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
    // Writable aggregate ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public Resource add(Resource resource) throws DataStoreException {
        if (resource instanceof FeatureSet) {
            final FeatureSet fs = (FeatureSet) resource;
            FeatureType type = fs.getType();
            createFeatureType(type);
            final String name = type.getName().tip().toString();
            final List<Feature> features = fs.features(false).collect(Collectors.toList());
            addFeatures(name, features);
            return findResource(name);
        } else if (resource instanceof DefiningFeatureSet) {
            final DefiningFeatureSet fs = (DefiningFeatureSet) resource;
            final FeatureType type = fs.getType();
            createFeatureType(type);
            final String name = type.getName().tip().toString();
            return findResource(name);
        } else {
            throw new DataStoreException("Unsupported resource "+ resource);
        }
    }

    @Override
    public void remove(Resource resource) throws DataStoreException {
        if (resource instanceof FeatureSet) {
            final FeatureSet fs = (FeatureSet) resource;
            FeatureType type = fs.getType();
            final String name = type.getName().tip().toString();
            deleteFeatureType(name);
        } else if (resource instanceof DefiningFeatureSet) {
            final DefiningFeatureSet fs = (DefiningFeatureSet) resource;
            final FeatureType type = fs.getType();
            final String name = type.getName().tip().toString();
            deleteFeatureType(name);
        } else {
            throw new DataStoreException("Unsupported resource "+ resource);
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
        sendEvent(FeatureStoreManagementEvent.createAddEvent(this, name, type));
    }

    /**
     * Fires a schema update event to all listeners.
     *
     * @param name updated schema name
     * @param oldType featuretype before change
     * @param newType featuretype after change
     */
    protected void fireSchemaUpdated(final GenericName name, final FeatureType oldType, final FeatureType newType){
        sendEvent(FeatureStoreManagementEvent.createUpdateEvent(this, name, oldType, newType));
    }

    /**
     * Fires a schema delete event to all listeners.
     *
     * @param name deleted schema name
     * @param type feature type of the deleted schema
     */
    protected void fireSchemaDeleted(final GenericName name, final FeatureType type){
        sendEvent(FeatureStoreManagementEvent.createDeleteEvent(this, name, type));
    }

    /**
     * Fires a features add event.
     *
     * @param name of the schema where features where added.
     * @param ids modified feature ids
     */
    protected void fireFeaturesAdded(final GenericName name, final Id ids){
        sendEvent(FeatureStoreContentEvent.createAddEvent(this, name, ids));
    }

    /**
     * Fires a features update event.
     *
     * @param name of the schema where features where updated.
     * @param ids modified feature ids
     */
    protected void fireFeaturesUpdated(final GenericName name, final Id ids){
        sendEvent(FeatureStoreContentEvent.createUpdateEvent(this, name, ids));
    }

    /**
     * Fires a features delete event.
     *
     * @param name of the schema where features where deleted
     * @param ids modified feature ids
     */
    protected void fireFeaturesDeleted(final GenericName name, final Id ids){
        sendEvent(FeatureStoreContentEvent.createDeleteEvent(this, name, ids));
    }

    ////////////////////////////////////////////////////////////////////////////
    // useful methods for feature store that doesn't implement all query parameters/
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Convenient method to check that the given type name exist.
     * Will raise a datastore exception if the name does not exist in this FeatureStore.
     *
     * @param candidate Name to test.
     * @throws DataStoreException if name does not exist, or if it'ambiguous.
     * Ambiguity is raised in case there's not any exact match, but input name
     * is equal to the local part of multiple names in the store.
     */
    protected void typeCheck(final String candidate) throws DataStoreException{
        ArgumentChecks.ensureNonNull("type name", candidate);
        int count = 0;
        boolean exactMatching = false;
        final Collection<GenericName> names = getNames();
        for (GenericName name : names) {
            if (candidate.equals(name.toString())) {
                exactMatching = true;
                break;
            }

            while (name instanceof ScopedName) {
                name = ((ScopedName)name).tail();
                if (candidate.equals(name.toString())) {
                    count++;
                    if (count>1) break;
                }
            }
        }

        if (exactMatching) return;

        if (count>1) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Multiple match for name ");
            sb.append(candidate);
            sb.append(", available names are : ");
            for(final GenericName n : names){
                sb.append(n).append(", ");
            }
            throw new DataStoreException(sb.toString());
        } else if (count==0) {
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
     * Convinient method to handle adding features operation by using the
     * FeatureWriter.
     *
     * @return list of ids of the features added.
     */
    protected List<FeatureId> handleAddWithFeatureWriter(final String groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException{

        try(FeatureWriter featureWriter = getFeatureWriter(QueryBuilder.filtered(groupName, Filter.EXCLUDE))) {
            while (featureWriter.hasNext()) {
                featureWriter.next();
            }
            return FeatureStoreUtilities.write(featureWriter, newFeatures);
        }catch(FeatureStoreRuntimeException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * Convinient method to handle adding features operation by using the
     * FeatureWriter.
     */
    protected void handleUpdateWithFeatureWriter(final String groupName, final Filter filter,
            final Map<String, ?> values) throws DataStoreException {

        try(FeatureWriter writer = getFeatureWriter(QueryBuilder.filtered(groupName, filter))) {
            while(writer.hasNext()){
                final Feature f = writer.next();
                for(final Entry<String,?> entry : values.entrySet()){
                    f.setPropertyValue(entry.getKey(), entry.getValue());
                }
                writer.write();
            }
        } catch(FeatureStoreRuntimeException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * Convinient method to handle adding features operation by using the
     * FeatureWriter.
     */
    protected void handleRemoveWithFeatureWriter(final String groupName, final Filter filter) throws DataStoreException {
        try(FeatureWriter writer = getFeatureWriter(QueryBuilder.filtered(groupName, filter))) {
            while(writer.hasNext()){
                writer.next();
                writer.remove();
            }
        } catch(FeatureStoreRuntimeException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * Convenient method to handle modification operation by using the
     * add, remove, update methods.
     */
    protected FeatureWriter handleWriter(Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.data.query.Query))  throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        final Filter filter = gquery.getFilter();
        final String groupName = gquery.getTypeName();
        if (Filter.EXCLUDE.equals(filter) ) {
            return GenericFeatureWriter.wrapAppend(this, groupName);
        } else {
            return GenericFeatureWriter.wrap(this, groupName, filter);
        }
    }

    public static GenericName ensureGMLNS(final String local){
        if(local.equals(GML_NAME)){
            return NamesExt.create(GML_311_NAMESPACE, GML_NAME);
        }else if(local.equals(GML_DESCRIPTION)){
            return NamesExt.create(GML_311_NAMESPACE, GML_DESCRIPTION);
        }else{
            return NamesExt.create(local);
        }
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Forward a structure event to all listeners.
     * @param event event to send to listeners.
     */
    protected void sendEvent(final ChangeEvent event){
        final ChangeListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new ChangeListener[listeners.size()]);
        }
        for(final ChangeListener listener : lst){
            listener.changeOccured(event);
        }
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     */
    public void forwardEvent(StorageEvent event){
        sendEvent(event.copy(this));
    }

}
