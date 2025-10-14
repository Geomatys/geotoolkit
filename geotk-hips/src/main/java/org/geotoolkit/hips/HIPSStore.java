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
package org.geotoolkit.hips;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class HIPSStore extends DataStore implements Aggregate {

    private final URI uri;
    final HIPSService service;

    //cache
    private HIPSList list;
    private List<HIPSCoverageResource> resources;


    public HIPSStore(HIPSProvider provider, StorageConnector connector) throws DataStoreException {
        super(provider, connector);
        uri = connector.getStorageAs(URI.class);
        connector.closeAllExcept(null);

        String baseUri = uri.toString();
        if (baseUri.endsWith("/")) baseUri = baseUri.substring(0, baseUri.length()-1);
        final ServiceConfiguration conf = ServiceConfiguration.builder()
                .updateBaseUri(baseUri)
                .build();
        service = new HIPSService(conf);
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        final Parameters parameters = Parameters.castOrWrap(HIPSProvider.PARAMETERS_DESCRIPTOR.createValue());
        parameters.parameter(HIPSProvider.LOCATION).setValue(uri);
        return Optional.of(parameters);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }


    @Override
    public synchronized Collection<? extends Resource> components() throws DataStoreException {

        if (resources == null) {
            resources = new ArrayList<>();
            try {
                final HIPSList hipsList = service.getHipsList().getData();
                for (HIPSProperties properties : hipsList) {
                    final HIPSCoverageResource resource = new HIPSCoverageResource(this, properties);
                    resources.add(resource);
                }

            } catch (ServiceException ex) {
                throw new DataStoreException(ex);
            }
        }

        return Collections.unmodifiableList(resources);
    }

    @Override
    public void close() throws DataStoreException {
    }
}
