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

import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.metadata.sql.ScriptRunner;
import org.apache.sis.internal.util.Constants;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.measure.Units;
import org.apache.sis.metadata.sql.MetadataSource;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.factory.sql.EPSGFactory;
import org.apache.sis.storage.*;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.geotoolkit.coverage.sql.UpgradableLock.Stamp;

/**
 * Provides access to resource read from the database.
 *
 * @implNote Concurrency management is rather strict. You can only launch one insertion at a time, due to information
 * merging policies (We try to factorize redondant information across products). Otherwise, reading stays accessible
 * while inserting, except on a short period of time in which we commit all modifications to database. To enforce that
 * mechanism, we use two different types of lock:
 * <ul>
 * <li>An {@link #insertionTicket insertion ticket}. It's simply a {@link Semaphore} with a single permit, to ensure
 * only one insertion is performed at any time.</li>
 * <li>An {@link #accessLock access lock} to manage concurrent reads and write over the catalog. ATTENTION ! It's NOT
 * a reentrant lock, so be careful when using it.</li>
 * </ul>
 */
public final class DatabaseStore extends DataStore implements WritableAggregate {

    private static final long DEFAULT_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(5);

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
         * Parameter to setup timeout on lock acquisitions (insertions, read, etc.)
         */
        private static final ParameterDescriptor<Long> LOCK_TIMEOUT;

        /**
         * All parameters.
         */
        private static final ParameterDescriptorGroup PARAMETERS;
        static {
            final ParameterBuilder builder = new ParameterBuilder();
            ALLOW_CREATE = builder.addName(CREATE).setRemarks("Enable schemas creation if they do not exist.")
                    .create(Boolean.class, Boolean.TRUE);
            LOCK_TIMEOUT = builder.addName("lock-timeout")
                    .setDescription("Maximum number of milliseconds to wait for a read/write lock on the store")
                    .createBounded(
                            new MeasurementRange<>(Long.class, 1l, true, Long.MAX_VALUE, true, Units.MILLISECOND),
                            DEFAULT_TIMEOUT_MS
                    );

            builder.setRequired(true);
            DATABASE       = builder.addName("database").setRemarks("Connection to the database.")
                    .create(DataSource.class, null);
            ROOT_DIRECTORY = builder.addName("rootDirectory").setRemarks("Root of data directory.")
                    .create(Path.class, null);
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
    private volatile List<Resource> components;

    private final UpgradableLock accessLock;
    private final Semaphore insertionTicket;

    /**
     * TODO: Add in provider parameters.
     */
    private final long lockTimeout;
    private final TimeUnit lockTimeoutUnit;

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
        final Long lockParam = parameters.getValue(Provider.LOCK_TIMEOUT);
        lockTimeout = lockParam == null? DEFAULT_TIMEOUT_MS : lockParam;
        lockTimeoutUnit = TimeUnit.MILLISECONDS;
        accessLock = new UpgradableLock(lockTimeout, lockTimeoutUnit);
        insertionTicket = new Semaphore(1);
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
    public Collection<Resource> components() throws DataStoreException {
        if (components == null) {
            accessLock.doLocked(this::createComponentsIfNull, true);
        }
        return components;
    }

    private void createComponentsIfNull(final Stamp stamp) throws DataStoreException {
        if (components != null) return;

        final List<ProductEntry> products;
        try (Transaction transaction = database.transaction();
             ProductTable table = new ProductTable(transaction)) {
            products = table.list();
        } catch (SQLException e) {
            throw new CatalogException(e);
        }
        final Resource[] resources = new Resource[products.size()];
        for (int i = 0; i < resources.length; i++) {
            // No need to call prefetch(table) for products obtained by ProductTable.list().
            resources[i] = createResource(products.get(i));
        }

        components = UnmodifiableArrayList.wrap(resources);
    }

    /**
     * Searches for a product identified by the given identifier.
     *
     * @param  productName  identifier of the product to fetch. Must be non-null.
     * @return product associated to the given identifier (never {@code null}).
     * @throws DataStoreException if an error occurred while reading data.
     */
    @Override
    public Resource findResource(final String productName) throws DataStoreException {
        ArgumentChecks.ensureNonNull("productName", productName);
        // TODO : check if components are not null, in which case we could get the resource from there.
        return accessLock.doLocked(stamp -> {
            try (Transaction transaction = database.transaction();
                 ProductTable table = new ProductTable(transaction))
            {
                final ProductEntry product = table.getEntry(productName);
                product.components(table);                                  // Force prefetching components.

                return createResource(product);
            } catch (SQLException e) {
                throw new CatalogException(e);
            }
        }, false);
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
     * <p>The {@code exportedGrid} parameter specifies the grid geometry of the datacube containing all data
     * for this product. It may be useful to specify this parameter if the coverage files have heterogynous
     * grid geometries. This parameter is used only if the product does not already exists in the database.
     * If unspecified ({@code null}), an arbitrary grid geometry will be selected.</p>
     *
     * @param  product       name of the product for which to add grid coverage files.
     * @param  exportedGrid  a grid encompassing all files that may be added for this product, or {@code null}.
     * @param  option        specifies if non-existing product should be created.
     * @param  files         the files to add to the specified product.
     * @throws DataStoreException if an error occurred while reading the grid coverages or adding them to the database.
     */
    public void addRaster(final String product, final GridGeometry exportedGrid, final AddOption option,
            final Path... files) throws DataStoreException
    {
        addRaster(product, exportedGrid, option, null, null, files);
    }

    /**
     * Registers the given grid coverages to the database. The coverages are specified as files.
     * The format will be detected automatically and the files added to the given product.
     * If this method fails to add the given grid coverage files, then the database if left unchanged
     * (i.e. this method is a "all or nothing" operation).
     *
     * <p>The {@code exportedGrid} parameter specifies the grid geometry of the datacube containing all data
     * for this product. It may be useful to specify this parameter if the coverage files have heterogynous
     * grid geometries. This parameter is used only if the product does not already exists in the database.
     * If unspecified ({@code null}), an arbitrary grid geometry will be selected.</p>
     *
     * @param  product       name of the product for which to add grid coverage files.
     * @param  exportedGrid  a grid encompassing all files that may be added for this product, or {@code null}.
     * @param  option        specifies if non-existing product should be created.
     * @param  datasets      array of datasets to insert, null for all
     * @param  provider      provider to use for opening files, or {@code null} for auto-detection.
     * @param  files         the files to add to the specified product.
     * @throws DataStoreException if an error occurred while reading the grid coverages or adding them to the database.
     */
    public void addRaster(final String product, final GridGeometry exportedGrid, final AddOption option,
            final Set<String> datasets, final DataStoreProvider provider, final Path... files) throws DataStoreException
    {
        final Map<String,List<NewRaster>> rasters = NewRaster.list(product, option, provider, files);
        if (datasets != null) {
            for (String ds : rasters.keySet().toArray(new String[0])) {
                if (!datasets.contains(ds)) {
                    rasters.remove(ds);
                }
            }
        }
        if (!rasters.isEmpty()) {
            try {
                // TODO: retry policy ?
                if (!insertionTicket.tryAcquire(lockTimeout, lockTimeoutUnit)) {
                    throw new CatalogException("Cannot lock insertion code. Another process is running an insertion for too long.");
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new CatalogException("Thread interrupted while waiting for a lock for insertion", ex);
            }
            try {
                accessLock.doLocked((Stamp stamp) -> insert(product, exportedGrid, option, rasters, stamp), false);
            } finally {
                insertionTicket.release();
            }
        }
    }

    private void insert(String product, GridGeometry exportedGrid, AddOption option, Map<String, List<NewRaster>> rasters, final Stamp stamp) throws DataStoreException {
        try (Transaction transaction = database.transaction()) {
            transaction.writeStart();
            try (ProductTable table = new ProductTable(transaction)) {
                table.addCoverageReferences(product, exportedGrid, option, rasters);
            }

            stamp.tryUpgrade();

            transaction.writeEnd();
        } catch (SQLException e) {
            throw new CatalogException(e);
        }
        components = null;
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
    public void removeRaster(final Path... files) throws DataStoreException {
        if (files.length != 0) {
            accessLock.doLocked(stamp -> {
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
            }, true);
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

    /**
     * Removes the coverages in the resource which intersect the given envelope.
     *
     * @param resource The product to remove a part of.
     * @param areaOfInterest A sub-area to de-reference in input resource.
     * @return list of files which won't be used anymore after given area removal.
     * @throws DataStoreException If given resource is not a product from this data store, or there's an issue accessing
     * inner ledger.
     */
    public List<Path> remove(Resource resource, Envelope areaOfInterest) throws DataStoreException {
        ArgumentChecks.ensureNonNull("areaOfInterest", areaOfInterest);

        if(resource instanceof ProductResource) {
            final ProductResource pr = (ProductResource) resource;
            return accessLock.doLocked((Stamp stamp) ->  remove(areaOfInterest, pr, stamp), false);
        }

        throw new CatalogException("Illegal input resource. Expected a Coverage-SQL product, but got "+resource == null? "null" : resource.getClass().getCanonicalName());
    }

    private List<Path> remove(Envelope areaOfInterest, ProductResource pr, final Stamp readLock) throws DataStoreException {
        areaOfInterest = pr.getGridGeometry().derive().subgrid(areaOfInterest).build().getEnvelope();

        try (Transaction transaction = database.transaction();
             GridCoverageTable table = new GridCoverageTable(transaction)) {

            final List<Path> removed = table.find(pr.toString(), areaOfInterest)
                    .stream()
                    .map(GridCoverageEntry::getDataPath)
                    .collect(Collectors.toList());

            readLock.tryUpgrade();

            transaction.writeStart();
            table.remove(pr.toString(), areaOfInterest);
            transaction.writeEnd();

            components = null;

            return removed;
        } catch (DataStoreException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CatalogException(exception);
        }
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
