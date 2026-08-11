/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2026, Geomatys
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

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogc.xml.exception.ServiceExceptionReport;
import org.geotoolkit.ogcwxs.client.coverage.WCSApi;
import org.geotoolkit.ogcwxs.request.coverage.GetCapabilities;
import org.geotoolkit.wcs.xml.CoverageInfo;
import org.geotoolkit.wcs.xml.GetCapabilitiesResponse;
import org.geotoolkit.wcs.xml.WCSVersion;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * Generates WCS requests objects on a WCS server.
 *
 * <p>
 * WCS 1.0.0 and 2.0.0/2.0.1 are supported, as exposed by {@link WCSApi}. WCS 1.1.1 and 2.1 remain
 * unsupported.
 * </p>
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Quentin Bialota (Geomatys)
 */
public final class WebCoverageClient extends DataStore implements Aggregate {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.ogcwxs.storage");

    private final WCSApi api;
    private final ServiceConfiguration configuration;
    private final URI uri;
    private List<Resource> resources;
    private final String id;

    //local cache
    private GetCapabilitiesResponse capabilities;

    WebCoverageClient(WCSProvider provider, StorageConnector connector) throws DataStoreException {
        super(provider, connector);
        uri = connector.getStorageAs(URI.class);
        connector.closeAllExcept(null);

        configuration = ServiceConfiguration.builder()
            .updateBaseUri(uri.toString())
            .build();
        api = new WCSApi(configuration);

        String str = configuration.getBaseUri();
        id = str.replace("http://", "").replace("https://", "");
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        final ParameterValueGroup parameters = WCSProvider.PARAMETERS_DESCRIPTOR.createValue();
        try {
            parameters.parameter(WCSProvider.URL.getName().getCode()).setValue(uri.toURL());
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
        return Optional.of(parameters);
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(Names.createLocalName(null, null, id));
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }

    public WCSApi getApi() {
        return api;
    }

    /**
     * Returns the WCS version negotiated with the server, i.e. the version declared by the last
     * fetched {@linkplain #getServiceCapabilities() capabilities} document.
     */
    public WCSVersion getVersion() throws CapabilitiesException {
        return WCSVersion.fromCode(getServiceCapabilities().getVersion());
    }

    /**
     * Returns the {@linkplain GetCapabilitiesResponse capabilities} response for this server.
     *
     * @return {@linkplain GetCapabilitiesResponse capabilities} response but never {@code null}.
     * @throws CapabilitiesException
     */
    public GetCapabilitiesResponse getServiceCapabilities() throws CapabilitiesException {

        if (capabilities != null) {
            return capabilities;
        }

        final GetCapabilities capa = new GetCapabilities();
        capa.setVersion(WCSVersion.v100);
        final ServiceRequest<Supplier<GetCapabilitiesResponse>> request = api.getCapabilities(capa);

        try {
            capabilities = request.send().body().get();
        } catch (Exception ex) {
            throw new CapabilitiesException("Cannot handle GetCapabilities", ex);
        }

        //force throw CapabilitiesException if the returned capabilities object is null
        if (capabilities == null) {
            throw new CapabilitiesException("The capabilities document is null.");
        }

        return capabilities;
    }

    /**
     * Request a new capabilities to check updateSequence.
     * If capabilities updateSequence has changed, resources are updated.
     *
     * @return true if capabilities and resources has changed.
     */
    public boolean checkForUpdates() {

        String currentUpdateSequence = null;
        WCSVersion currentVersion = WCSVersion.v100;
        GetCapabilitiesResponse capas = this.capabilities;
        if (capas != null) {
            currentUpdateSequence = capas.getUpdateSequence();
            currentVersion = WCSVersion.fromCode(capas.getVersion());
        }

        final GetCapabilities getCaps = new GetCapabilities();
        getCaps.setVersion(currentVersion);
        getCaps.setUpdateSequence(currentUpdateSequence);

        String newUpdateSequence = null;
        boolean changed = true;
        try {
            Object lastCapa = api.getCapabilities(getCaps).send().body().get();
            if (lastCapa instanceof ServiceExceptionReport) {
                ServiceExceptionReport report = (ServiceExceptionReport) lastCapa;
                if (!report.getException().isEmpty() && "CurrentUpdateSequence".equalsIgnoreCase(report.getException().get(0).getExceptionCode())) {
                    newUpdateSequence = currentUpdateSequence;
                    changed = false;
                } else {
                     throw report.toException();
                }

            } else if (lastCapa instanceof GetCapabilitiesResponse) {
                GetCapabilitiesResponse lc = (GetCapabilitiesResponse) lastCapa;
                newUpdateSequence = lc.getUpdateSequence();
            }
            changed = !Objects.equals(currentUpdateSequence, newUpdateSequence);
            if (changed) {
                capabilities = (GetCapabilitiesResponse) lastCapa;
            }

        } catch (Exception ex) {
            capabilities = null;
        }

        if (changed) {
            resources = null;
        }
        return changed;
    }

    @Override
    public synchronized Collection<Resource> components() throws DataStoreException {
        if (resources != null) {
            return resources;
        }

        final GetCapabilitiesResponse capa;
        try {
            capa = getServiceCapabilities();
        } catch (CapabilitiesException ex) {
            throw new DataStoreException(ex);
        }

        //content model is a flat list of coverage offerings for both 1.0.0 and 2.x, unlike WMS
        //which exposes a layer tree.
        final List<Resource> result = new ArrayList<>();
        for (CoverageInfo info : capa.getContents().getCoverageInfos()) {
            result.add(new WCSResource(this, info));
        }
        resources = result;
        return resources;
    }

    @Override
    public void close() {
    }

}
