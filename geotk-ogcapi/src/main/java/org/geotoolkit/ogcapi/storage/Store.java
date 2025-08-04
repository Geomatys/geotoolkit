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

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.ogcapi.client.common.CoreApi;
import org.geotoolkit.ogcapi.model.Conformance;
import org.geotoolkit.ogcapi.model.common.ConfClasses;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Store extends DataStore implements Aggregate {

    private final OpenApiConfiguration configuration;
    private final URI uri;
    private Resource root;

    Store(Provider provider, StorageConnector connector) throws DataStoreException {
        super(provider, connector);
        uri = connector.getStorageAs(URI.class);
        connector.closeAllExcept(null);

        configuration = OpenApiConfiguration.builder()
            .updateBaseUri(uri.toString())
            .build();
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        final ParameterValueGroup parameters = Provider.PARAMETERS.createValue();
        parameters.parameter(Provider.LOCATION).setValue(uri);
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
        if (root == null) {
            try (CoreApi core = new CoreApi(configuration)) {
                final ConfClasses conformance = core.getConformance("application/json").getData();
                if (conformance.getConformsTo().contains(Conformance.COLLECTIONS)) {
                    root = new CollectionResource(configuration);
                } else {
                    root = new UndefinedResource(configuration);
                }
            } catch (ServiceException ex) {
                throw new DataStoreException(ex);
            } catch (Exception ex) {
                throw new DataStoreException(ex);
            }
        }

        return Collections.singleton(root);
    }

    @Override
    public void close() throws DataStoreException {
    }

}
