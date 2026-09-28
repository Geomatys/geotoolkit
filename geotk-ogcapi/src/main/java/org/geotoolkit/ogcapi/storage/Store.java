/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.storage;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.ogcapi.client.common.CoreApi;
import org.geotoolkit.ogcapi.dto.Conformance;
import org.geotoolkit.ogcapi.dto.common.ConfClasses;
import org.geotoolkit.ogcapi.request.common.GetConformance;
import org.geotoolkit.process.ProcessingRegistry;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Store extends DataStore implements Aggregate {

    private final OpenApiConfiguration configuration;
    private final URI uri;
    private List<Resource> roots;
    private ConfClasses conformance;
    private OgcApiProcessingRegistry processingRegistry;
    private final String id;

    Store(Provider provider, StorageConnector connector) throws DataStoreException {
        super(provider, connector);
        uri = connector.getStorageAs(URI.class);
        connector.closeAllExcept(null);

        configuration = OpenApiConfiguration.builder()
            .updateBaseUri(uri.toString())
            .build();

        String str = configuration.getBaseUri();
        id = str.replace("http://", "").replace("https://", "");

    }

    @Override
    public String getDisplayName() {
        return id;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(Names.createLocalName(null, null, id));
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        final ParameterValueGroup parameters = Provider.PARAMETERS_DESCRIPTOR.createValue();
        try {
            parameters.parameter(Provider.URL.getName().getCode()).setValue(uri.toURL());
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
        return Optional.of(parameters);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }

    /**
     * @return root resource
     * @throws DataStoreException
     */
    @Override
    public synchronized Collection<? extends Resource> components() throws DataStoreException {
        if (roots == null) {
            final ConfClasses conformance = getConformance();
            final List<Resource> newRoots = new ArrayList<>();
            if (conformance.isConformTo(Conformance.COLLECTIONS_v0)
               || conformance.isConformTo(Conformance.COLLECTIONS_v1)
               || conformance.isConformTo(Conformance.DGGS_COLLECTION)
               || conformance.isConformTo(Conformance.RECORD_CRAWLABLE_CATALOG)
               || conformance.isConformTo(Conformance.RECORD_SEARCHABLE_CATALOG)
               || conformance.isConformTo(Conformance.RECORD_LOCAL_RESOURCES_CATALOG)) {
                newRoots.add(new CollectionResource(configuration));
            }
            if (conformance.isConformTo(Conformance.ROUTES_CORE)) {
                newRoots.add(new RoutesResource(configuration));
            }
            if (newRoots.isEmpty()) {
                newRoots.add(new UndefinedResource(configuration));
            }
            roots = newRoots;
        }

        return Collections.unmodifiableList(roots);
    }

    /**
     * Get the processing registry of this service.
     *
     * @return processing registry, empty if the service does not declare OGC API - Processes conformance
     * @throws DataStoreException if the service conformance can not be retrieved
     */
    public synchronized Optional<ProcessingRegistry> getProcessingRegistry() throws DataStoreException {
        if (processingRegistry == null) {
            if (!getConformance().isConformTo(Conformance.PROCESSES_CORE)) {
                return Optional.empty();
            }
            processingRegistry = new OgcApiProcessingRegistry(configuration);
        }
        return Optional.of(processingRegistry);
    }

    private synchronized ConfClasses getConformance() throws DataStoreException {
        if (conformance == null) {
            try (CoreApi core = new CoreApi(configuration)) {
                conformance = core.getConformance(new GetConformance().format("json")).send().body().get();
            } catch (ServiceException ex) {
                throw new DataStoreException(ex);
            } catch (Exception ex) {
                throw new DataStoreException(ex);
            }
        }
        return conformance;
    }

    @Override
    public synchronized void close() throws DataStoreException {
        if (processingRegistry != null) {
            try {
                processingRegistry.close();
            } catch (Exception ex) {
                throw new DataStoreException(ex);
            } finally {
                processingRegistry = null;
            }
        }
    }

}
