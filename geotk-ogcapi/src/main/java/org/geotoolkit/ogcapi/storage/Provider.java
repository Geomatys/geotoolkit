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
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceResponse;
import org.geotoolkit.ogcapi.client.common.CoreApi;
import org.geotoolkit.ogcapi.model.common.ConfClasses;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Provider extends DataStoreProvider {

    public static final String NAME = "ogcapi";

    /**
     * Server URI, Mandatory.
     */
    public static final ParameterDescriptor<URI> URI = new ParameterBuilder()
            .addName(LOCATION)
            .setRequired(true)
            .create(URI.class, null);

    public static final ParameterDescriptorGroup PARAMETERS = new ParameterBuilder()
            .addName(NAME)
            .createGroup(URI);

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS;
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        final URI uri = connector.getStorageAs(URI.class);

        final OpenApiConfiguration config = OpenApiConfiguration.builder()
                .updateBaseUri(uri.toString())
                .build();
        try (CoreApi core = new CoreApi(config)) {
            ServiceResponse<ConfClasses> conformance = core.getConformance("application/json");
            if (conformance.getStatusCode() == 200 && conformance.getData() != null) {
                return ProbeResult.SUPPORTED;
            } else {
                return ProbeResult.UNSUPPORTED_STORAGE;
            }
        } catch (ServiceException ex) {
            return ProbeResult.UNSUPPORTED_STORAGE;
        } catch (Exception ex) {
            return ProbeResult.UNSUPPORTED_STORAGE;
        }
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        return new Store(this, connector);
    }

}
