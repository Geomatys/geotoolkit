/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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

import java.io.IOException;
import java.util.List;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

import org.geotoolkit.data.concurrent.Transaction;
import org.geotoolkit.data.concurrent.LockingManager;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.feature.SchemaException;

/**
 * Access to Feature content from a service or file.
 * <p>
 * <h2>Description</h2>
 * The DataAccess interface provides the following information about its contents:
 * <ul>
 * <li>{@link DataAccess.getInfo()} - information about the file or server itself
 * <li>{@link DataAccess.getNames()} - list of the available contents (each is an individual resource)
 * <li>{@link DataAccess.getSchema( Name )} - FeatureType describing the information available in the named resource
 * </ul>
 * <p>
 * <h2>Contents</h2>
 * You can access the contents of a service or file using getFeatureSource( Name ). Depending the
 * abilities of your implementation and your credentials you will have access to
 * <ul>
 * <li>{@link FeatureSource}: read-only api similar to the WFS getFeature operations. Please note the reutrned
 *    FeatureCollection may be *lazy*; for many implementations no actual access will occur until you
 *    use the FetaureCollection for the first time.
 * <li>{@link FeatureStore}: read/write api similar to the WFS Transaction operation. Batch changes such as
 *    addFeatures, modifyFeatures and removeFeatures are supported.
 * <li>{@link FeatureLocking}: concurrency control; the Data Access API is thread safe; one consequence of this
 * is modifications being held up while other threads read the contents. You may wish to Lock a selection
 * of features for your exclusive use. Locks are timed; and will expire after the indicated period.
 * </ul>
 * <p>
 * Please note that all interaction occurs within the context of a Transaction, this
 * facility provides session management and is strongly advised. Please note that
 * your application is responsible for managing its own Transactions; as an example
 * they are often associated with a single Map in a desktop application; or a single
 * session in a J2EE web app.
 * <p>
 * The use of Transaction.AUTO_COMMIT is suitable for read-only access when you wish
 * to minimize the number of connections in use, when used for writing performance
 * will often be terrible.
 *
 * <h2>Lifecycle</h2>
 *
 * Normal use:
 * <ul>
 * <li>Connect using a DataAccessFactory.createDataStore using a set of connection parameters
 * <li>Application is responsible for holding a single instance to the service or file, DataAccess
 * implementations will hold onto database connections, internal caches and so on - and as such
 * should not be duplicated.
 * <li>DataAccess.dispose() is called when the application is shut down
 * </ul>
 *
 * Creation:
 * <ul>
 * <li>Created using a DataStoreFactory.createNewDataStore using a set of creation parameters
 * <li>DataAccess.createSchema( T ) is called to set up the contents
 * <li>DataAccess.getFetaureSource( Name ) is called, and FeatureStore.addFeatures( collection ) used to populate the contents
 * <li>DataAccess.dispose() is called when the application is shut down
 * </ul>
 * <p>
 * Applications are responsible for holding a single instance to the service or file, The
 * DataAccess implementations will hold onto database connections, internal caches and so on - and as such
 * should not be duplicated.
 *
 * <p>
 * Summary of our requirements:
 * </p>
 * <ul>
 * <li>
 * Provides lookup of available Feature Types
 * </li>
 * <li>
 * Provides access to low-level Readers/Writers API for a feature type
 * </li>
 * <li>
 * Provides access to high-level FeatureSource/Store/Locking API a feature type
 * </li>
 * <li>
 * Handles the conversion of filters into data source specific queries
 * </li>
 * <li>
 * Handles creation of new Feature Types
 * </li>
 * <li>
 * Provides access of Feature Type Schema information
 * </li>
 * </ul>
 *
 * @author Jody Garnett, Refractions Research
 * @param <T> Type of Feature Content, may be SimpleFeatureType
 * @param <F> Feature Content, may be SimpleFetaure
 */
public interface DataStore<T extends FeatureType, F extends Feature> {

    /**
     * Information about this service.
     * <p>
     * This method offers access to a summary of header or metadata
     * information describing the service.
     * </p>
     * Subclasses may return a specific ServiceInfo instance that has
     * additional information (such as FilterCapabilities).
     * @return SeviceInfo
     */
    ServiceInfo getInfo();

    /**
     * Retrieve a per featureID based locking service from this DataStore.
     *
     * <p>
     * It is common to return an instanceof InProcessLockingManager for
     * DataStores that do not provide native locking.
     * </p>
     *
     * <p>
     * AbstractFeatureLocking makes use of this service to provide locking
     * support. You are not limitied by this implementation and may simply
     * return <code>null</code> for this value.
     * </p>
     *
     * @return DataStores may return <code>null</code>, if the handling locking
     *         in another fashion.
     */
    LockingManager getLockingManager();

    /**
     * Disposes of this data store and releases any resource that it is using.
     * <p>
     * A <code>DataStore</code> cannot be used after <code>dispose</code> has
     * been called, neither can any data access object it helped create, such
     * as {@link FeatureReader}, {@link FeatureSource} or {@link FeatureCollection}.
     * <p>
     * This operation can be called more than once without side effects.
     * <p>
     * There is no thread safety assurance associated with this method. For example,
     * client code will have to make sure this method is not called while retrieving/saving data
     * from/to the storage, or be prepared for the consequences.
     */
    void dispose();

    /**
     * Retrieves a list of of the available FeatureTypes.
     *
     * <p>
     * This is simply a list of the FeatureType names as aquiring the actual
     * FeatureType schemas may be expensive.
     * </p>
     *
     * <p>
     * Warning: this list may not be unique - the types may be
     * in separate namespaces.
     * </p>
     *
     * <p>
     * If you need to worry about such things please consider the use of
     * the Catalog and CatalogEntry interface - many DataStores support this.
     * getTypeNames is really a convience method for a Catalog.iterator() where
     * the name of each entry is returned.
     * </p>
     *
     * @return typeNames for available FeatureTypes.
     */
    String[] getTypeNames() throws IOException;

    /**
     * Names of the available Resources.
     * <p>
     * For additional information please see getInfo( Name ) and getSchema( Name ).
     * </p>
     * @return Names of the available contents.
     * @throws IOException
     */
    List<Name> getNames() throws IOException;

    /**
     * Creates storage for a new <code>featureType</code>.
     *
     * <p>
     * The provided <code>featureType</code> we be accessable by the typeName
     * provided by featureType.getTypeName().
     * </p>
     *
     * @param featureType FetureType to add to DataStore
     *
     * @throws IOException If featureType cannot be created
     */
    void createSchema(T featureType) throws IOException;

    /**
     * Used to force namespace and CS info into a persistent change.
     * <p>
     * The provided featureType should completely cover the existing schema.
     * All attributes should be accounted for and the typeName should match.
     * </p>
     * <p>
     * Suggestions:
     * </p>
     * <ul>
     * <li>Sean - don't do this</li>
     * <li>Jody - Just allow changes to metadata: CS, namespace, and others</li>
     * <li>James - Allow change/addition of attribtues</li>
     * </ul>
     * @param typeName
     * @throws IOException
     */
    void updateSchema(String typeName, T featureType) throws IOException;

    /**
     * Used to update a schema in place.
     * <p>
     * This functionality is similar to an "alter table" statement in SQL. Implementation
     * is optional; it may not be supported by all servers or files.
     * @param typeName
     * @param featureType
     * @throws IOException if the operation failed
     * @throws UnsupportedOperation if functionality is not available
     */
    void updateSchema(Name typeName, T featureType) throws IOException;

    /**
     * Retrieve FeatureType metadata by <code>typeName</code>.
     *
     * <p>
     * Retrieves the Schema information as a FeatureType object.
     * </p>
     *
     * @param typeName typeName of requested FeatureType
     *
     * @return FeatureType for the provided typeName
     *
     * @throws IOException If typeName cannot be found
     */
    T getSchema(String typeName) throws IOException;

    /**
     * Description of the named resource.
     * <p>
     * The FeatureType returned describes the contents being published. For
     * additional metadata please review getInfo( Name ).
     *
     * @param name Type name a the resource from getNames()
     * @return Description of the FeatureType being made avaialble
     * @throws IOException
     */
    T getSchema(Name name) throws IOException;

    /**
     * Access a FeatureSource<SimpleFeatureType, SimpleFeature> for Query providing a high-level API.
     * <p>
     * The provided Query does not need to completely cover the existing
     * schema for Query.getTypeName(). The result will mostly likely only be
     * a FeatureSource<SimpleFeatureType, SimpleFeature> and probably wont' allow write access by the
     * FeatureStore method.
     * </p>
     * <p>
     * By using Query we allow support for reprojection, in addition
     * to overriding the CoordinateSystem used by the native FeatureType.
     * </p>
     * <p>
     * We may wish to limit this method to only support Queries using
     * Filter.EXCLUDE.
     * </p>
     * <p>
     * Update - GeoServer has an elegatent implementation of this functionality
     * that we could steal. GeoServerFeatureSource, GeoServerFeatureStore and
     * GeoServerFeatureLocking serve as a working prototype.
     * </p>
     * @param query Query.getTypeName() locates FeatureType being viewed
     *
     * @return FeatureSource<SimpleFeatureType, SimpleFeature> providing operations for featureType
     * @throws IOException If FeatureSource<SimpleFeatureType, SimpleFeature> is not available
     * @throws SchemaException If fetureType is not covered by existing schema
     */
    FeatureSource<T, F> getView(Query query) throws IOException,SchemaException;

    /**
     * Access a FeatureSource<SimpleFeatureType, SimpleFeature> for typeName providing a high-level API.
     *
     * <p>
     * The resulting FeatureSource<SimpleFeatureType, SimpleFeature> may implment more functionality:
     * </p>
     * <pre><code>
     *
     * FeatureSource<SimpleFeatureType, SimpleFeature> fsource = dataStore.getFeatureSource( "roads" );
     * FeatureStore fstore = null;
     * if( fsource instanceof FeatureLocking ){
     *     fstore = (FeatureStore<SimpleFeatureType, SimpleFeature>) fs;
     * }
     * else {
     *     System.out.println("We do not have write access to roads");
     * }
     * </code>
     * </pre>
     *
     * @param typeName
     *
     * @return FeatureSource<SimpleFeatureType, SimpleFeature> (or subclass) providing operations for typeName
     */
    FeatureSource<T, F> getFeatureSource(String typeName) throws IOException;

    /**
     * Access to the named resource.
     * <p>
     * The level of access is represented by the instance of the FeatureSource
     * being returned.
     * <p>
     * Formally:
     * <ul>
     * <li>FeatureSource - read-only access
     * <li>FeatureStore - read-write access
     * <li>FetureLocking - concurrency control
     * <ul>
     * Additional interfaces may be supported by the implementation you are using.
     * @param typeName
     * @return Access to the named resource being made available
     * @throws IOException
     */
    FeatureSource<T, F> getFeatureSource(Name typeName) throws IOException;

    /**
     * Access a FeatureReader providing access to Feature information.
     *
     * <p>
     * <b>Filter</b> is used as a low-level indication of constraints.
     * (Implementations may resort to using a FilteredFeatureReader, or
     * provide their own optimizations)
     * </p>
     *
     * <p>
     * <b>FeatureType</b> provides a template for the returned FeatureReader
     * </p>
     *
     * <ul>
     * <li>
     * featureType.getTypeName(): used by JDBC as the table reference to query
     * against. Shapefile reader may need to store a lookup to the required
     * filename.
     * </li>
     * <li>
     * featureType.getAttributeTypes(): describes the requested content. This
     * may be a subset of the complete FeatureType defined by the DataStore.
     * </li>
     * <li>
     * getType.getNamespace(): describes the requested namespace for the
     * results (may be different then the one used internally)
     * </li>
     * </ul>
     *
     * <p>
     * <b>Transaction</b> to externalize DataStore state on a per Transaction
     * basis. The most common example is a JDBC datastore saving a Connection
     * for use across several FeatureReader requests. Similarly a Shapefile
     * reader may wish to redirect FeatureReader requests to a alternate
     * filename over the course of a Transaction.
     * </p>
     *
     * <p>
     * <b>Notes For Implementing DataStore</b>
     * </p>
     *
     * <p>
     * Subclasses may need to retrieve additional attributes, beyond those
     * requested by featureType.getAttributeTypes(), in order to correctly
     * apply the <code>filter</code>.<br>
     * These Additional <b>attribtues</b> should be not be returned by
     * FeatureReader. Subclasses may use ReTypeFeatureReader to aid in
     * acomplishing this.
     * </p>
     * <p>
     * Helper classes for implementing a FeatureReader (in order):
     * </p>
     * <ul>
     * <li>
     * DefaultFeatureReader
     * - basic support for creating a FeatureReader for an AttributeReader
     * </li>
     * <li>
     * FilteringFeatureReader
     * - filtering support
     * </li>
     * <li>
     * DiffFeatureReader
     * - In-Process Transaction Support (see TransactionStateDiff)
     * </li>
     * <li>
     * ReTypeFeatureReader
     * - Feature Type schema manipulation of namesspace and attribute type subsets
     * </li>
     * <li>
     * EmptyFeatureReader
     * - provides no content for Filter.EXCLUDE optimizations
     * </li>
     * </ul>
     * <p>
     * Sample use (not optimized):
     * </p>
     * <pre><code>
     * if (filter == Filter.EXCLUDE) {
     *      return new EmptyFeatureReader(featureType);
     *  }
     *
     *  String typeName = featureType.getTypeName();
     *  FeatureType schema = getSchema( typeName );
     *  FeatureReader reader = new DefaultFeatureReader( getAttributeReaders(), schema );
     *
     *  if (filter != Filter.INCLUDE) {
     *      reader = new FilteringFeatureReader<SimpleFeatureType, SimpleFeature>(reader, filter);
     *  }
     *
     *  if (transaction != Transaction.AUTO_COMMIT) {
     *      Map diff = state(transaction).diff(typeName);
     *      reader = new DiffFeatureReader(reader, diff);
     *  }
     *
     *  if (!featureType.equals(reader.getFeatureType())) {
     *      reader = new ReTypeFeatureReader(reader, featureType);
     *  }
     * return reader
     * </code></pre>
     * <p>
     * Locking support does not need to be provided for FeatureReaders.
     * </p>
     *
     * @param query Requested form of the returned Features and the filter used
     *              to constraints the results
     * @param transaction Transaction this query operates against
     *
     * @return FeatureReader Allows Sequential Processing of featureType
     */
    FeatureReader<T, F> getFeatureReader(Query query, Transaction transaction) throws IOException;

    /**
     * Access FeatureWriter for modification of the DataStore typeName.
     *
     * <p>
     * FeatureWriters will need to be limited to the FeatureTypes defined by
     * the DataStore, the easiest way to express this limitation is to the
     * FeatureType by a provided typeName.
     * </p>
     *
     * <p>
     * The returned FeatureWriter will return <code>false</code> for getNext()
     * when it reaches the end of the Query.
     * </p>
     *
     * @param typeName Indicates featureType to be modified
     * @param transaction Transaction to operates against
     *
     * @return FeatureReader Allows Sequential Processing of featureType
     */
    FeatureWriter<T, F> getFeatureWriter(String typeName, Transaction transaction) throws IOException;

    /**
     * Access FeatureWriter for modification of existing DataStore contents.
     *
     * <p>
     * To limit FeatureWriter to the FeatureTypes defined by this DataStore,
     * typeName is used to indicate FeatureType. The resulting
     * feature writer will allow modifications against the
     * same FeatureType provided by getSchema( typeName )
     * </p>
     *
     * <p>
     * The FeatureWriter will provide access to the existing contents of the
     * FeatureType referenced by typeName. The provided filter will be used
     * to skip over Features as required.
     * </p>
     *
     * <b>Notes For Implementing DataStore</b>
     * </p>
     *
     * <p>
     * The returned FeatureWriter <b>does not</b> support the addition of new
     * Features to FeatureType (it would need to police your modifications to
     * agree with <code>filer</code>).  As such it will return
     * <code>false</code> for getNext() when it reaches the end of the Query
     * and NoSuchElementException when next() is called.
     * </p>
     *
     * <p>
     * Helper classes for implementing a FeatureWriter (in order):
     * </p>
     * <li>
     * InProcessLockingManager.checkedWriter( writer )
     * - provides a check against locks before allowing modification
     *
     * <li>
     * FilteringFeatureWriter
     * - filtering support for FeatureWriter (does not allow new content)
     * </li>
     * <li>
     * DiffFeatureWriter
     * - In-Process Transaction Support (see TransactionStateDiff)
     * </li>
     * <li>
     * EmptyFeatureWriter
     * - provides no content for Filter.EXCLUDE optimizations
     * </li>
     * </ul>
     *
     * @param typeName Indicates featureType to be modified
     * @param filter constraints used to limit the modification
     * @param transaction Transaction this query operates against
     *
     * @return FeatureWriter Allows Sequential Modification of featureType
     */
    FeatureWriter<T, F> getFeatureWriter(String typeName, Filter filter, Transaction transaction) throws IOException;

    /**
     * Aquire a FeatureWriter for adding new content to a FeatureType.
     *
     * <p>
     * This FeatureWriter will return <code>false</code> for hasNext(), however
     * next() may be used to aquire new Features that may be writen out to add
     * new content.
     * </p>
     *
     * @param typeName Indicates featureType to be modified
     * @param transaction Transaction to operates against
     *
     * @return FeatureWriter that may only be used to append new content
     *
     * @throws IOException
     */
    FeatureWriter<T, F> getFeatureWriterAppend(String typeName, Transaction transaction) throws IOException;
    
}
