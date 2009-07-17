/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.geotoolkit.data.DataAccess;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DefaultServiceInfo;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureSource;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.InProcessLockingManager;
import org.geotoolkit.data.concurrent.LockingManager;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.ServiceInfo;
import org.geotoolkit.data.concurrent.Transaction;
import org.geotoolkit.feature.collection.FeatureCollection;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.SchemaException;

import org.opengis.feature.FeatureFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotoolkit.util.logging.Logging;


/**
 * Abstract base class for data stores.
 * <p>
 * A datastore contains a set of entries ({@link ContentEntry}). Each entry
 * corresponds to a "real world dataset". For instance, a shapefile datastore
 * would contain a single entry which would represent the shapefile on disk. A
 * postgis datastore could contain many entries, one for each table in the database.
 * </p>
 * <p>
 * Each entry is identified by a name ({@link Name}). The name can be qualified
 * with a namespace uri, or unqualified (in which the namespace uri is null). An
 * example of a datastore that might use qualified names is WFS, where in each
 * entry corresponds to a WFS "Feature Type", which have namespace qualified name.
 * Other datastores (such as databases) use unqualified names.
 * </p>
 * <p>
 * When entry names of a datastore are unqualified, a default namespace uri can
 * be set "globally" on the datastore itself, see {@link #setNamespaceURI(String)}.
 * When this value is set, unqualified entry names are implicitly qualified with
 * the global namespace uri.
 * </p>
 * <h3>Subclasses</h3>
 * <p>
 * At a minimum subclasses must implement the following methods:
 * <ul>
 *   <li>{@link #createTypeNames()}
 *   <li>{@link #createFeatureSource(ContentEntry)}
 * </ul>
 * The following methods may also be overriden:
 * <ul>
 *   <li>{@link #createContentState(ContentEntry)}
 * </ul>
 * The following methods may be overriden but <b>only</b> to narrow the return
 * type to a specific subclass of {@link ContentFeatureSource}.
 * <ul>
 *   <li>{@link #getFeatureSource(String)}
 *   <li>{@link #getFeatureSource(String, Transaction)}
 *   <li>{@link #getFeatureSource(Name, Transaction)}
 * </ul>
 * </p>
 * @author Jody Garnett, Refractions Research Inc.
 * @author Justin Deoliveira, The Open Planning Project
 */
public abstract class ContentDataStore implements DataStore {

    /**
     * writer flags
     */
    protected final static int WRITER_ADD = 0x01<<0;
    protected final static int WRITER_UPDATE = 0x01<<1;

    /**
     * name, entry map
     */
    protected final Map<Name,ContentEntry> entries = new HashMap<Name,ContentEntry>();

    /**
     * locking manager
     */
    protected final LockingManager lockingManager = new InProcessLockingManager();

    /**
     * logger
     */
    protected final Logger Logger = Logging.getLogger(getClass().getPackage().getName());

    /**
     * Factory used to create feature types
     */
    protected FeatureTypeFactory typeFactory;

    /**
     * Factory used to create features
     */
    protected FeatureFactory featureFactory;

    /**
     * Factory used to create filters
     */
    protected FilterFactory filterFactory;

    /**
     * Factory used to create geometries
     */
    protected GeometryFactory geometryFactory;

    /**
     * namespace uri of the datastore itself, or default namespace
     */
    protected String namespaceURI;

    public ContentDataStore() {
    }

    //
    // Property accessors
    //

    /**
     * The factory used to create feature types.
     */
    public FeatureTypeFactory getFeatureTypeFactory() {
        return typeFactory;
    }

    /**
     * Sets the factory used to create feature types.
     */
    public void setFeatureTypeFactory(final FeatureTypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    /**
     * Sets the factory used to create features.
     */
    public void setFeatureFactory(final FeatureFactory featureFactory) {
        this.featureFactory = featureFactory;
    }

    /**
     * The factory used to create filters.
     */
    public FilterFactory getFilterFactory() {
        return filterFactory;
    }

    /**
     * The factory used to create features.
     */
    public FeatureFactory getFeatureFactory() {
        return featureFactory;
    }

    /**
     * Sets the factory used to create filters.
     */
    public void setFilterFactory(final FilterFactory filterFactory) {
        this.filterFactory = filterFactory;
    }


    /**
     * The factory used to create geometries.
     */
    public GeometryFactory getGeometryFactory() {
        return geometryFactory;
    }

    /**
     * Sets the factory used to create geometries.
     *
     */
    public void setGeometryFactory(final GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    /**
     * The namespace uri of the datastore.
     *
     * @return The namespace uri, may be <code>null</code>.
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * Sets the namespace uri of the datastore.
     * <p>
     * This will be used to qualify the entries or types of the datastore.
     * </p>
     * @param namespaceURI The namespace uri, may be <code>null</code>.
     */
    public void setNamespaceURI(final String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    /**
     * The logger for the datastore.
     */
    public Logger getLogger() {
        return Logger;
    }

    //
    // DataStore API
    //

    @Override
    public ServiceInfo getInfo() {
        final DefaultServiceInfo info = new DefaultServiceInfo();
        info.setDescription("Features from "+getClass().getSimpleName() );
        info.setSchema( FeatureTypeUtilities.DEFAULT_NAMESPACE );
        return info;
    }

    /**
     * Returns the names of all entries or types provided by the datastore.
     * <p>
     * This method is marked final and delegates to {@link #createTypeNames()},
     * which subclasses are intended to implement.
     * </p>
     *
     * @see DataStore#getTypeNames()
     */
    @Override
    public final String[] getTypeNames() throws IOException {
        final List<Name> typeNames = createTypeNames();
        final String[] names = new String[typeNames.size()];

        for (int i = 0; i < typeNames.size(); i++) {
            Name typeName = typeNames.get(i);
            names[i] = typeName.getLocalPart();
        }

        return names;
    }

    /**
     * Creates a new schema in the datastore.
     * <p>
     * This implementation throws a{@link UnsupportedOperationException}. Subclasses
     * should override to support schema creation.
     * </p>
     *
     *
     * @see DataStore#createSchema(FeatureType)
     */
    @Override
    public void createSchema(final SimpleFeatureType featureType) throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the feature type or schema matching the specified name.
     * <p>
     * This method calls through to <code>getFeatureSource(typeName).getSchema()</code>
     * </p>
     *
     * @see DataStore#getSchema(String)
     */
    @Override
    public final SimpleFeatureType getSchema(final String typeName) throws IOException {
        return getFeatureSource(typeName).getSchema();
    }

    /**
     * Returns the feature source matching the specified name.
     * <p>
     * Subclasses should not implement this method. However overriding in order
     * to perform a type narrowing to a subclasses of {@link ContentFeatureSource}
     * is acceptable.
     * </p>
     *
     * @see DataStore#getFeatureSource(String)
     */
    @Override
    public ContentFeatureSource getFeatureSource(final String typeName) throws IOException {
        return getFeatureSource(new DefaultName(namespaceURI,typeName), Transaction.AUTO_COMMIT);
    }

    /**
     * Returns the feature source matching the specified name and explicitly
     * specifies a transaction.
     * <p>
     * Subclasses should not implement this method. However overriding in order
     * to perform a type narrowing to a subclasses of {@link ContentFeatureSource}
     * is acceptable.
     * </p>
     *
     * @see DataStore#getFeatureSource(String)
     */
    public ContentFeatureSource getFeatureSource(final String typeName, final Transaction tx)
                                                 throws IOException{
        return getFeatureSource(name(typeName), tx);
    }

    /**
     * Returns the feature source matching the specified name and explicitly
     * specifies a transaction.
     * <p>
     * Subclasses should not implement this method. However overriding in order
     * to perform a type narrowing to a subclasses of {@link ContentFeatureSource}
     * is acceptable.
     * </p>
     *
     * @see DataStore#getFeatureSource(String)
     */
    public ContentFeatureSource getFeatureSource(final Name typeName, final Transaction tx)
                                                 throws IOException{
        final ContentEntry entry = ensureEntry(typeName);

        final ContentFeatureSource featureSource = createFeatureSource(entry);
        featureSource.setTransaction(tx);

        return featureSource;
    }

    /**
     * Returns a feature reader for the specified query and transaction.
     * <p>
     * This method is not intended to be overridden and is marked final. This
     * implementation delegates to {@link FeatureCollection} and wraps an iterator
     * in a {@link FeatureReader}.
     * </p>
     */
    @Override
    public FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(final Query query,
            final Transaction tx) throws IOException{
        final String typeName = query.getTypeName();
        if (typeName == null) {
            throw new IllegalArgumentException("Query does not specify type.");
        }

        return getFeatureSource(typeName, tx).getReader(query);
    }

    /**
     * Returns a feature writer for the specified query and transaction.
     * <p>
     * This method is not intended to be overridden and is marked final. This
     * implementation delegates to {@link FeatureCollection} and wraps an iterator
     * in a {@link FeatureWriter}.
     * </p>
     */
    @Override
    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter(final String typeName,
            final Filter filter, final Transaction tx) throws IOException{

        final ContentFeatureStore featureStore = ensureFeatureStore(typeName,tx);
        return featureStore.getWriter( filter , WRITER_UPDATE | WRITER_ADD );

    }

    /**
     * Helper method which gets a feature source ensuring that it is a feature
     * store as well. If not it throws an IOException.
     *
     * @param typeName The name of the feature source.
     * @param tx A transaction handle.
     *
     * @throws IOException If the feature source is not a store.
     *
     */
    protected final ContentFeatureStore ensureFeatureStore(final String typeName, final Transaction tx)
                                                           throws IOException{

        final ContentFeatureSource featureSource = getFeatureSource(typeName,tx);
        if (!(featureSource instanceof ContentFeatureStore)) {
            throw new IOException(typeName + " is read only" );
        }

        return (ContentFeatureStore) featureSource;
    }

    /**
     * Returns a feature writer for the specified type name and transaction.
     * <p>
     * This method is convenience for <code>getFeatureWriter(typeName,Filter.INCLUDE,tx)</code>.
     * </p>
     */
    @Override
    public final FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter(final String typeName,
            final Transaction tx) throws IOException{

        return getFeatureWriter(typeName, Filter.INCLUDE, tx);
    }

    /**
     * Returns an appending feature writer for the specified type name and
     * transaction.
     * <p>
     * This method is not intended to be overridden and is marked final. This
     * implementation delegates to {@link FeatureCollection} and wraps an iterator
     * in a {@link FeatureWriter}.
     * </p>
     */
    @Override
    public final FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriterAppend(final String typeName,
            final Transaction tx) throws IOException{

        final ContentFeatureStore featureStore = ensureFeatureStore(typeName,tx);
        return featureStore.getWriter(Filter.INCLUDE , WRITER_ADD);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final LockingManager getLockingManager() {
        return lockingManager;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final ContentFeatureSource getView(final Query query) throws IOException, SchemaException {
        final ContentFeatureSource origional = getFeatureSource( query.getTypeName() );
        return origional.getView(query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final void updateSchema(final String typeName, final SimpleFeatureType featureType)
            throws IOException{
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        for (ContentEntry entry : entries.values()) {
            entry.dispose();
        }
    }

    /**
     * Returns the entry for a specified name, or <code>null</code> if no such
     * entry exists.
     */
    public ContentEntry getEntry(final Name name) {
        return (ContentEntry) entries.get(name);
    }

    //
    // Internal API
    //

    /**
     * Instantiates a new content state for the entry.
     * <p>
     * Subclasses may override this method to return a specific subclass of
     * {@link ContentState}.
     * </p>
     * @param entry The entry.
     *
     * @return A new instance of {@link ContentState} for the entry.
     *
     */
    protected ContentState createContentState(final ContentEntry entry) {
    	return new ContentState(entry);
    }

    /**
     * Helper method to wrap a non-qualified name.
     */
    protected final Name name(final String typeName) {
        return new DefaultName(namespaceURI,typeName);
    }

    /**
     * Helper method to look up an entry in the datastore.
     * <p>
     * This method will create a new instance of {@link ContentEntry} if one
     * does not exist.
     * </p>
     * <p>
     * In the event that the name does not map to an entry
     * and one cannot be created <code>null</code> will be returned. Note that
     * {@link #ensureEntry(TypeName)} will throw an exception in this case.
     * </p>
     *
     * @param The name of the entry.
     *
     * @return The entry, or <code>null</code> if it does not exist.
     */
    protected final ContentEntry entry(final Name name) throws IOException {
        ContentEntry entry = null;

        //do we already know about the entry
        if (!entries.containsKey(name)) {
            //is this type available?
            final List<Name> typeNames = createTypeNames();

            if (typeNames.contains(name)) {
                //yes, create an entry for it
                synchronized (this) {
                    if (!entries.containsKey(name)) {
                        entry = new ContentEntry(this, name);
                        entries.put(name, entry);
                    }
                }

                entry = (ContentEntry) entries.get(name);
            }
        }

        return (ContentEntry) entries.get(name);
    }

    /**
     * Helper method to look up an entry in the datastore which throws an
     * {@link IOException} in the event that the entry does not exist.
     *
     * @param name The name of the entry.
     *
     * @return The entry.
     *
     * @throws IOException If the entry does not exist, or if there was an error
     * looking it up.
     */
    protected final ContentEntry ensureEntry(final Name name) throws IOException {
        final ContentEntry entry = entry(name);

        if (entry == null) {
            throw new IOException("Schema '" + name + "' does not exist.");
        }

        return entry;
    }

    /**
     * Creates a set of qualified names corresponding to the types that the
     * datastore provides.
     * <p>
     * Namespaces may be left <code>null</code> for data stores which do not
     * support namespace qualified type names.
     * </p>
     *
     * @return A list of {@link Name}.
     *
     * @throws IOException Any errors occuring connecting to data.
     */
    protected abstract List<Name> createTypeNames() throws IOException;

    /**
     * Instantiates new feature source for the entry.
     * <p>
     * Subclasses should override this method to return a specific subclass of
     * {@link ContentFeatureSource}.
     * </p>
     * @param entry The entry.
     *
     * @return An new instance of {@link ContentFeatureSource} for the entry.
     */
    protected abstract ContentFeatureSource createFeatureSource(final ContentEntry entry) throws IOException;

    /**
     * Instantiates a new transaction state object.
     * <p>
     * Subclasses should override method to return a specific instance of
     * {@link Transaction.State}.
     * </p>
     * @param FeatureSource<SimpleFeatureType, SimpleFeature> The feature source / store for the new transaction
     * state.
     */
//    protected abstract Transaction.State createTransactionState(ContentFeatureSource<SimpleFeatureType, SimpleFeature> featureSource)
//        throws IOException;


    /**
     * Delegates to {@link #getFeatureSource(String)} with
     * {@code name.getLocalPart()}
     *
     * @since 2.5
     * @see DataAccess#getFeatureSource(Name)
     */
    @Override
    public FeatureSource<SimpleFeatureType, SimpleFeature> getFeatureSource(Name typeName)
            throws IOException
    {
        return getFeatureSource(typeName.getLocalPart());
    }

    /**
     * Returns the same list of names than {@link #getTypeNames()} meaning the
     * returned Names have no namespace set.
     *
     * @since 2.5
     * @see DataAccess#getNames()
     */
    @Override
    public List<Name> getNames() throws IOException {
        final String[] typeNames = getTypeNames();
        final List<Name> names = new ArrayList<Name>(typeNames.length);
        for (String typeName : typeNames) {
            names.add(new DefaultName(typeName));
        }
        return names;
    }

    /**
     * Delegates to {@link #getSchema(String)} with {@code name.getLocalPart()}
     *
     * @since 2.5
     * @see DataAccess#getSchema(Name)
     */
    @Override
    public SimpleFeatureType getSchema(final Name name) throws IOException {
        return getSchema(name.getLocalPart());
    }

    /**
     * Delegates to {@link #updateSchema(String, SimpleFeatureType)} with
     * {@code name.getLocalPart()}
     *
     * @since 2.5
     * @see DataAccess#getFeatureSource(Name)
     */
    @Override
    public void updateSchema(final Name typeName, final SimpleFeatureType featureType) throws IOException {
        updateSchema(typeName.getLocalPart(), featureType);
    }
}
