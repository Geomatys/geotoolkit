/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2018, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.apache.sis.internal.metadata.sql.ScriptRunner;
import org.apache.sis.internal.util.Constants;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.metadata.sql.MetadataSource;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.factory.sql.EPSGFactory;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.storage.WritableAggregate;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;


/**
 * Provides access to resource read from the database.
 */
public final class DatabaseStore extends DataStore implements WritableAggregate {
    /**
     * Provider of {@link DatabaseStore}.
     *
     * @see DatabaseStore#open(DataSource, Path, boolean)
     */
    @StoreMetadataExt(resourceTypes = ResourceType.GRID, canWrite = true)
    public static final class Provider extends DataStoreProvider {
        /**
         * Factory identification.
         */
        private static final String NAME = "coverage-sql";

         /**
          * Parameter for getting connections to the database.
          */
        private static final ParameterDescriptor<DataSource> DATABASE;

        /**
         * Parameter for the root directory.
         */
        private static final ParameterDescriptor<Path> ROOT_DIRECTORY;

        /**
         * Parameter for the database creation.
         */
        private static final ParameterDescriptor<Boolean> ALLOW_CREATE;

        /**
         * All parameters.
         */
        private static final ParameterDescriptorGroup PARAMETERS;
        static {
            final ParameterBuilder builder = new ParameterBuilder();
            DATABASE       = builder.addName("database").setRemarks("Connection to the database.").setRequired(true).create(DataSource.class, null);
            ROOT_DIRECTORY = builder.addName("rootDirectory").setRemarks("Root of data directory.").setRequired(true).create(Path.class, null);
            ALLOW_CREATE   = builder.addName(CREATE).setRemarks("Enable schemas creation if they do not exist.").setRequired(false).create(Boolean.class, Boolean.TRUE);
            PARAMETERS     = builder.addName(NAME).createGroup(DATABASE, ROOT_DIRECTORY, ALLOW_CREATE);
        }

        /**
         * Wraps the given parameters in a {@code ParameterValueGroup}.
         *
         * @param  database        provides connection to the SQL database.
         * @param  rootDirectory   the directory to use for resolving relative paths of data files.
         * @return the given parameters wrapped in a {@code ParameterValueGroup} object.
         */
        static ParameterValueGroup parameters(DataSource database, Path rootDirectory) {
            final ParameterValueGroup pg = PARAMETERS.createValue();
            pg.parameter("database").setValue(database);
            pg.parameter("rootDirectory").setValue(rootDirectory);
            return pg;
        }

        /**
         * Creates a new provider instance.
         */
        public Provider() {
        }

        /**
         * Get singleton instance of Coverage-SQL provider.
         *
         * <div class="note"><b>Note:</b>
         * this method is named after Java 9 service loader provider method (see {@link java.util.ServiceLoader}).
         * </div>
         *
         * @return singleton instance of CoverageSQL Provider
         */
        public static Provider provider() {
            return (Provider) DataStores.getProviderById(NAME);
        }

        /**
         * Returns {@value #NAME} for identifying this data store in warning or error messages.
         *
         * @return {@value #NAME}.
         */
        @Override
        public String getShortName() {
            return NAME;
        }

        /**
         * Returns a description of all parameters accepted by this provider for opening a data store.
         *
         * @return description of the parameters required or accepted for opening a {@link DataStore}.
         */
        @Override
        public ParameterDescriptorGroup getOpenParameters() {
            return PARAMETERS;
        }

        /**
         * Opens a data store using the given parameters.
         *
         * @param  parameters  opening parameters as defined by {@link #getOpenParameters()}.
         * @return data store associated with this provider for the given parameters.
         * @throws DataStoreException if an error occurred while opening the data store.
         *
         * @see DatabaseStore#open(DataSource, Path, boolean)
         */
        @Override
        public DatabaseStore open(final ParameterValueGroup parameters) throws DataStoreException {
            CharSequence reason = canProcess(parameters);
            if (reason == null) {
                return new DatabaseStore(this, Parameters.castOrWrap(parameters));
            }
            throw new DataStoreException(reason.toString());
        }

        /**
         * Returns a data store implementation associated with this provider.
         * This is currently unsupported.
         *
         * @param  connector  information about the storage.
         * @return data store associated with this provider for the given storage.
         * @throws DataStoreException if an error occurred while creating the data store instance.
         */
        @Override
        public DataStore open(final StorageConnector connector) throws DataStoreException {
            throw new DataStoreException("Not supported.");
        }

        /**
         * Indicates if the given storage appears to be supported by the {@code DataStore}s created by this provider.
         * Current implementation returns {@link ProbeResult#UNSUPPORTED_STORAGE}.
         *
         * @param  connector information about the storage.
         * @return {@link ProbeResult#UNSUPPORTED} (in current implementation).
         * @throws DataStoreException if an SQL error occurred.
         */
        @Override
        public ProbeResult probeContent(final StorageConnector connector) throws DataStoreException {
            return ProbeResult.UNSUPPORTED_STORAGE;
        }

        /**
         * Returns {@code null} if the mandatory parameters are present.
         * Otherwise returns the failure reason.
         */
        private CharSequence canProcess(final ParameterValueGroup params) {
            if (params != null) {
                final ParameterDescriptorGroup desc = getOpenParameters();
                if (desc.getName().getCode().equalsIgnoreCase(params.getDescriptor().getName().getCode())) {
                    final ConformanceResult result = org.geotoolkit.parameter.Parameters.isValid(params, desc);
                    if (result != null) {
                        if (Boolean.TRUE.equals(result.pass())) {
                            return null;
                        }
                        final CharSequence reason = result.getExplanation();
                        if (reason != null) return reason;
                    }
                }
            }
            return "Invalid parameters.";
        }
    }

    /**
     * The actual data store implementation. Contains the SQL {@link DataSource}, factories, cached tables, <i>etc.</i>
     */
    final Database database;

    /**
     * Cached result of {@link #components()}.
     */
    private List<Resource> components;

    /**
     * Creates a new data store for the given parameters. The parameters should be an instance created by
     * <code>{@linkplain Provider#getOpenParameters()}.createValue()</code>.
     *
     * @param  provider   the factory that created this {@code DataStore} instance, or {@code null} if unspecified.
     * @param  parameters  opening parameters as defined by {@link Provider#getOpenParameters()}.
     * @throws DataStoreException if an error occurred while opening the data store.
     *
     * @see #open(DataSource, Path, boolean)
     */
    protected DatabaseStore(final Provider provider, final Parameters parameters) throws DataStoreException {
        super(provider, new StorageConnector(parameters.getMandatoryValue(Provider.DATABASE)));
        final DataSource dataSource = parameters.getMandatoryValue(Provider.DATABASE);
        try {
            if (parameters.booleanValue(Provider.ALLOW_CREATE)) {
                /*
                 * Check if schemas exist.
                 */
                try (Connection cnx = dataSource.getConnection()) {
                    final DatabaseMetaData metadata = cnx.getMetaData();
                    if (!schemaExists(metadata, Constants.EPSG)) {
                        try (EPSGFactory factory = new EPSGFactory(Collections.singletonMap("dataSource", dataSource))) {
                            factory.install(cnx);
                        }
                    }
                    if (!schemaExists(metadata, "metadata")) {
                        // Following is a modified copy of org.apache.sis.metadata.sql.Installer.run().
                        try (ScriptRunner runner = new ScriptRunner(cnx, 100)) {
                            runner.run(MetadataSource.class, "Citations.sql");
                            runner.run(MetadataSource.class, "Contents.sql");
                            runner.run(MetadataSource.class, "Metadata.sql");
                            runner.run(MetadataSource.class, "Referencing.sql");
                        }
                    }
                    if (!schemaExists(metadata, "rasters")) {
                        try (ScriptRunner runner = new ScriptRunner(cnx, 100)) {
                            runner.run(DatabaseStore.class, "Create.sql");
                        }
                    }
                }
            }
            database = new Database(dataSource, parameters.getMandatoryValue(Provider.ROOT_DIRECTORY));
        } catch (SQLException | FactoryException | IOException e) {
            throw new CatalogException(e);
        }
    }

    /**
     * Returns {@code true} if the given schema exists.
     *
     * @param  metadata  value of {@link Connection#getMetaData()}.
     * @param  schema    the schema to check for existence. Shall not contain {@code '%'} or {@code '_'} wildcards.
     */
    private static boolean schemaExists(final DatabaseMetaData metadata, final String schema) throws SQLException {
        try (ResultSet rs = metadata.getSchemas(null, schema)) {
            while (rs.next()) {
                if (schema.equals(rs.getString("TABLE_SCHEM"))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Opens a data store using the given data source. This is a convenience method building
     * the parameters for a call to {@link Provider#open(ParameterValueGroup)}.
     *
     * @param  database       provides connection to the SQL database.
     * @param  rootDirectory  the directory to use for resolving relative paths of data files.
     * @param  create         whether to create the required schemas if they do not exist.
     * @return data store associated with this provider for the given parameters.
     * @throws DataStoreException if an error occurred while opening the data store.
     */
    public static DatabaseStore open(final DataSource database, final Path rootDirectory, final boolean create)
            throws DataStoreException
    {
        ArgumentChecks.ensureNonNull("database", database);
        ArgumentChecks.ensureNonNull("rootDirectory", rootDirectory);
        final ParameterValueGroup pg = Provider.parameters(database, rootDirectory);
        pg.parameter(Provider.CREATE).setValue(create);
        return Provider.provider().open(pg);
    }

    /**
     * Returns the parameters used to open this data store.
     *
     * @return parameters used for opening this {@code DataStore}.
     */
    @Override
    public ParameterValueGroup getOpenParameters() {
        return Provider.parameters(database.source, database.root);
    }

    /**
     * Returns an identifier for the root resource of this data store, or {@code null} if none.
     *
     * @return {@code null} in current implementation.
     * @throws DataStoreException if an error occurred while reading data.
     */
    @Override
    public GenericName getIdentifier() throws DataStoreException {
        return null;
    }

    /**
     * Returns information about the data store as a whole, or {@code null} if none.
     *
     * @return {@code null} in current implementation.
     * @throws DataStoreException if an error occurred while reading data.
     */
    @Override
    public Metadata getMetadata() throws DataStoreException {
        return null;
    }

    /**
     * Returns all products in the database. Products are entries in the {@code "Products"} table.
     * Each product instance may be a {@link org.apache.sis.storage.GridCoverageResource} or an
     * {@link org.apache.sis.storage.Aggregate} of other products.
     *
     * @return all products in the database.
     * @throws DataStoreException if an error occurred while reading data.
     */
    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public synchronized Collection<Resource> components() throws DataStoreException {
        if (components == null) {
            final List<ProductEntry> products;
            try (Transaction transaction = database.transaction();
                 ProductTable table = new ProductTable(transaction))
            {
                products = table.list();
            } catch (SQLException e) {
                throw new CatalogException(e);
            }
            final Resource[] resources = new Resource[products.size()];
            for (int i=0; i<resources.length; i++) {
                // No need to call prefetch(table) for products obtained by ProductTable.list().
                resources[i] = createResource(products.get(i));
            }
            components = UnmodifiableArrayList.wrap(resources);
        }
        return components;
    }

    /**
     * Searches for a product identified by the given identifier.
     *
     * @param  productName  identifier of the product to fetch. Must be non-null.
     * @return product associated to the given identifier (never {@code null}).
     * @throws DataStoreException if an error occurred while reading data.
     */
    @Override
    public synchronized Resource findResource(final String productName) throws DataStoreException {
        ArgumentChecks.ensureNonNull("productName", productName);
        final ProductEntry product;
        try (Transaction transaction = database.transaction();
             ProductTable table = new ProductTable(transaction))
        {
            product = table.getEntry(productName);
            product.prefetch(table);
        } catch (SQLException e) {
            throw new CatalogException(e);
        }
        return createResource(product);
    }

    /**
     * Wraps the given product entry in a resource, which may be a grid resource or an aggregate.
     * It is recommended to have product components prefetched before to invoke this method.
     */
    final Resource createResource(final ProductEntry product) throws DataStoreException {
        if (product.components().isEmpty()) {
            return new ProductResource(this, product);
        } else if (product.isGrid()) {
            return new ProductAggregate.AlsoGrid(this, product);
        } else {
            return new ProductAggregate(this, product);
        }
    }

    /**
     * Not yet supported.
     *
     * @param  resource  the resource to copy in this {@code Aggregate}.
     * @return the effectively added resource. May be {@code resource} itself if it has been added verbatim.
     * @throws DataStoreException if the given resource can not be stored in this {@code Aggregate}.
     */
    @Override
    public Resource add(final Resource resource) throws DataStoreException {
        throw new CatalogException("Not supported yet.");
    }

    /**
     * Registers the given grid coverages to the database. The coverages are specified as files.
     * The format will be detected automatically and the files added to the given product.
     * If this method fails to add the given grid coverage files, then the database if left unchanged
     * (i.e. this method is a "all or nothing" operation).
     *
     * @param  product  name of the product for which to add grid coverage files.
     * @param  option   specifies if non-existing product should be created.
     * @param  files    the files to add to the specified product.
     * @throws DataStoreException if an error occurred while reading the grid coverages or adding them to the database.
     */
    public synchronized void addRaster(final String product, final AddOption option, final Path... files) throws DataStoreException {
        final Map<String,List<NewRaster>> rasters = NewRaster.list(product, option, files);
        if (!rasters.isEmpty()) {
            try (Transaction transaction = database.transaction()) {
                transaction.writeStart();
                try (ProductTable table = new ProductTable(transaction)) {
                    table.addCoverageReferences(product, option, rasters);
                }
                transaction.writeEnd();
            } catch (SQLException e) {
                throw new CatalogException(e);
            }
            components = null;
        }
    }

    /**
     * Removes the given grid coverages from the database. The grid coverages are specified by the path to their file.
     * The path may be relative or absolute; this method will resolve them for verifying if they are the same that the
     * paths in the database. If the file appears in more than one product, the corresponding grid coverage entries will
     * be removed for all products.
     *
     * <p>Note that this method does <strong>not</strong> delete the given files.
     * It assumes that the files will be deleted or moved by the caller.</p>
     *
     * @param  files  the files to remove from any product.
     * @throws DataStoreException if an error occurred while resolving a path of removing entries from the database.
     */
    public synchronized void removeRaster(final Path... files) throws DataStoreException {
        if (files.length != 0) {
            try (Transaction transaction = database.transaction()) {
                transaction.writeStart();
                try (GridCoverageTable table = new GridCoverageTable(transaction)) {
                    for (final Path file : files) {
                        table.remove(file);
                    }
                }
                transaction.writeEnd();
            } catch (DataStoreException e) {
                throw e;
            } catch (Exception e) {
                throw new CatalogException(e);
            }
            components = null;
        }
    }

    /**
     * Removes the given resource.
     *
     * @param  resource  the resource to remove.
     * @throws DataStoreException  if the given resource can not be removed from this datastore.
     */
    @Override
    public void remove(Resource resource) throws DataStoreException {
        final ProductEntry product;
        if (resource instanceof ProductResource) {
            product = ((ProductResource) resource).product;
        } else if (resource instanceof ProductAggregate) {
            product = ((ProductAggregate) resource).product;
        } else {
            throw new CatalogException("Not a resource from this data store.");
        }
        product.remove();
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

    /**
     * Closes this data store and releases any underlying resources.
     *
     * @throws DataStoreException if an error occurred while closing this data store.
     */
    @Override
    public void close() throws DataStoreException {
    }
}
