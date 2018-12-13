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

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;


/**
 * Provides access to resource read from the database.
 */
public final class DatabaseStore extends DataStore implements Aggregate {
    /**
     * Provider of {@link DatabaseStore}.
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
         * All parameters.
         */
        private static final ParameterDescriptorGroup PARAMETERS;
        static {
            final ParameterBuilder builder = new ParameterBuilder();
            DATABASE       = builder.addName("database").setRequired(true).create(DataSource.class, null);
            ROOT_DIRECTORY = builder.addName("rootDirectory").setRemarks("local data directory root").setRequired(true).create(Path.class, null);
            PARAMETERS     = builder.addName(NAME).createGroup(DATABASE, ROOT_DIRECTORY);
        }

        @Override
        public String getShortName() {
            return NAME;
        }

        @Override
        public ParameterDescriptorGroup getOpenParameters() {
            return PARAMETERS;
        }

        @Override
        public DatabaseStore open(final ParameterValueGroup params) throws DataStoreException {
            if (canProcess(params)) {
                return new DatabaseStore(this, Parameters.castOrWrap(params));
            }
            throw new DataStoreException("Parameter values not supported by this factory.");
        }

        @Override
        public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
            return new ProbeResult(false, null, null);
        }

        @Override
        public DataStore open(StorageConnector connector) throws DataStoreException {
            throw new DataStoreException("Not supported.");
        }

        private boolean canProcess(final ParameterValueGroup params) {
            if (params != null) {
                final ParameterDescriptorGroup desc = getOpenParameters();
                if (desc.getName().getCode().equalsIgnoreCase(params.getDescriptor().getName().getCode())) {
                    final ConformanceResult result = org.geotoolkit.parameter.Parameters.isValid(params, desc);
                    if (result != null) {
                        return Boolean.TRUE.equals(result.pass());
                    }
                }
            }
            return false;
        }
    }

    private final Parameters parameters;

    final Database database;

    private List<GridCoverageResource> components;

    public DatabaseStore(final Provider provider, final Parameters parameters) throws DataStoreException {
        super(provider, new StorageConnector(parameters.getMandatoryValue(Provider.DATABASE)));
        try {
            database = new Database(parameters.getMandatoryValue(Provider.DATABASE),
                                    parameters.getMandatoryValue(Provider.ROOT_DIRECTORY));
        } catch (FactoryException e) {
            throw new CatalogException(e);
        }
        this.parameters = Parameters.unmodifiable(parameters);
    }

    @Override
    public GenericName getIdentifier() {
        return null;
    }

    @Override
    public ParameterValueGroup getOpenParameters() {
        return parameters;
    }

    public synchronized void addRaster(final String product, final AddOption option, final Path... files) throws DataStoreException {
        final List<NewRaster> rasters = NewRaster.list(files);
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

    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public synchronized Collection<GridCoverageResource> components() throws DataStoreException {
        if (components == null) {
            final List<ProductEntry> products;
            try (Transaction transaction = database.transaction();
                 ProductTable table = new ProductTable(transaction))
            {
                products = table.list();
            } catch (SQLException e) {
                throw new CatalogException(e);
            }
            final ProductGeotk[] resources = new ProductGeotk[products.size()];
            for (int i=0; i<resources.length; i++) {
                resources[i] = new ProductGeotk(this, products.get(i));
            }
            components = UnmodifiableArrayList.wrap(resources);
        }
        return components;
    }

    @Override
    public synchronized GridCoverageResource findResource(final String productName) throws DataStoreException {
        final ProductEntry product;
        try (Transaction transaction = database.transaction();
             ProductTable table = new ProductTable(transaction))
        {
            product = table.getEntry(productName);
        } catch (SQLException e) {
            throw new CatalogException(e);
        }
        return new ProductGeotk(this, product);
    }

    @Override
    public void close() throws DataStoreException {
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return null;
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }
}
