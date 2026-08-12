/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.storage;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.function.Supplier;
import org.apache.sis.storage.base.Capability;
import org.apache.sis.storage.base.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.Version;
import org.geotoolkit.client.AbstractClientProvider;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.ogcwxs.client.feature.WFSApi;
import org.geotoolkit.ogcwxs.request.feature.GetCapabilities;
import org.geotoolkit.wfs.xml.WFSCapabilities;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * DataStore provider for WFS.
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Bialota (Geomatys)
 */
@StoreMetadata(
        formatName = WFSProvider.NAME,
        capabilities = {Capability.READ},
        resourceTypes = {FeatureSet.class})
public final class WFSProvider extends AbstractClientProvider {

    public static final String NAME = "wfs";
    private static final String MIME_TYPE = "ogc/wfs";
    private static WFSProvider INSTANCE;

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR = new ParameterBuilder()
            .addName(NAME).createGroup(URL);

    /**
     * Get singleton instance of WFS provider.
     *
     * <p>
     * Note : this method is named after Java 9 service loader provider method.
     * {@link https://docs.oracle.com/javase/9/docs/api/java/util/ServiceLoader.html}
     * </p>
     *
     * @return singleton instance of WFS Provider
     */
    public static synchronized WFSProvider provider() {
        if (INSTANCE == null) INSTANCE = new WFSProvider();
        return INSTANCE;
    }

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    public CharSequence getDescription() {
        return "Web Feature Service";
    }

    public CharSequence getDisplayName() {
        return "OGC.WFS";
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        return new WebFeatureClient(this, connector);
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {

        final URI uri = connector.getStorageAs(URI.class);

        final ServiceConfiguration config = ServiceConfiguration.builder()
                .updateBaseUri(uri.toString())
                .build();
        try (WFSApi core = new WFSApi(config)) {
            final GetCapabilities capa = new GetCapabilities();
            final HttpResponse<Supplier<WFSCapabilities>> response = core.getCapabilities(capa).send();
            if (response.statusCode() == 200) {
                final WFSCapabilities cap = response.body().get();
                if (cap != null) {
                    return new ProbeResult(true, MIME_TYPE, new Version(cap.getVersion()));
                }
            }
        } catch (ServiceException ex) {
            return ProbeResult.UNSUPPORTED_STORAGE;
        } catch (Exception ex) {
            return ProbeResult.UNSUPPORTED_STORAGE;
        }

        return ProbeResult.UNSUPPORTED_STORAGE;
    }

}
