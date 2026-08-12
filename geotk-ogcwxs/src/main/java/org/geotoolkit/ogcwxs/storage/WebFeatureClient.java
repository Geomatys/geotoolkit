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

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
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
import org.geotoolkit.ogcwxs.client.feature.WFSApi;
import org.geotoolkit.ogcwxs.request.feature.GetCapabilities;
import org.geotoolkit.wfs.xml.WFSCapabilities;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * Generates WFS requests objects on a WFS server.
 *
 * <p>
 * Only read access is provided : {@code Transaction} and {@code LockFeature} are not covered by
 * {@link WFSApi} and so are not exposed here either.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Bialota (Geomatys)
 */
public final class WebFeatureClient extends DataStore implements Aggregate {

    private final WFSApi api;
    private final ServiceConfiguration configuration;
    private final URI uri;
    private final String id;
    private Collection<Resource> components;

    //local cache
    private WFSCapabilities capabilities;
    private WFSVersion version;

    WebFeatureClient(WFSProvider provider, StorageConnector connector) throws DataStoreException {
        super(provider, connector);
        uri = connector.getStorageAs(URI.class);
        connector.closeAllExcept(null);

        configuration = ServiceConfiguration.builder()
            .updateBaseUri(uri.toString())
            .build();
        api = new WFSApi(configuration);

        String str = configuration.getBaseUri();
        id = str.replace("http://", "").replace("https://", "");
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        final ParameterValueGroup parameters = WFSProvider.PARAMETERS_DESCRIPTOR.createValue();
        try {
            parameters.parameter(WFSProvider.URL.getName().getCode()).setValue(uri.toURL());
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

    public WFSApi getApi() {
        return api;
    }

    /**
     * Returns the {@linkplain WFSCapabilities capabilities} response for this server.
     *
     * @return {@linkplain WFSCapabilities capabilities} response but never {@code null}.
     * @throws CapabilitiesException
     */
    public WFSCapabilities getServiceCapabilities() throws CapabilitiesException {

        if (capabilities != null) {
            return capabilities;
        }

        final GetCapabilities capa = new GetCapabilities();
        final ServiceRequest<Supplier<WFSCapabilities>> request = api.getCapabilities(capa);

        try {
            capabilities = request.send().body().get();
        } catch (Exception ex) {
            throw new CapabilitiesException("Cannot handle GetCapabilities", ex);
        }

        //force throw CapabilitiesException if the returned capabilities object is null
        if (capabilities == null) {
            throw new CapabilitiesException("The capabilities document is null.");
        }

        version = WFSVersion.fromCode(capabilities.getVersion());
        return capabilities;
    }

    /**
     * Returns the request version.
     */
    public WFSVersion getVersion() throws CapabilitiesException {
        getServiceCapabilities();
        return version;
    }

    /**
     * Request a new capabilities to check updateSequence.
     * If capabilities updateSequence has changed, resources are updated.
     *
     * @return true if capabilities and resources has changed.
     */
    public boolean checkForUpdates() {

        String currentUpdateSequence = null;
        WFSCapabilities capas = this.capabilities;
        if (capas != null) {
            currentUpdateSequence = capas.getUpdateSequence();
        }

        final GetCapabilities getCaps = new GetCapabilities();
        getCaps.setUpdateSequence(currentUpdateSequence);

        String newUpdateSequence = null;
        boolean changed = true;
        try {
            WFSCapabilities lastCapa = api.getCapabilities(getCaps).send().body().get();
            if (lastCapa != null) {
                newUpdateSequence = lastCapa.getUpdateSequence();
            }
            changed = !Objects.equals(currentUpdateSequence, newUpdateSequence);
            if (changed) {
                capabilities = lastCapa;
            }
        } catch (Exception ex) {
            capabilities = null;
        }

        if (changed) {
            components = null;
        }
        return changed;
    }

    @Override
    public synchronized Collection<Resource> components() throws DataStoreException {
        checkForUpdates();
        if (components == null) {
            final WFSCapabilities capa;
            try {
                capa = getServiceCapabilities();
            } catch (CapabilitiesException ex) {
                throw new DataStoreException(ex);
            }

            final List<Resource> resources = new ArrayList<>();
            for (final org.geotoolkit.wfs.xml.FeatureType ftt : capa.getFeatureTypeList().getFeatureType()) {
                resources.add(new WFSResource(this, ftt));
            }
            components = Collections.unmodifiableList(resources);
        }
        return components;
    }

    @Override
    public void close() {
    }

}
