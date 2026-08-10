/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogc.xml.exception.ServiceExceptionReport;
import org.geotoolkit.ogcwxs.client.map.WMSApi;
import org.geotoolkit.ogcwxs.request.map.GetCapabilities;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.WMSVersion;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;


/**
 * Generates WMS requests objects on a WMS server.
 *
 * @author Olivier Terral (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
public final class WebMapClient extends DataStore implements Aggregate {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.ogcwxs.storage");

    private final WMSApi api;
    private final ServiceConfiguration configuration;
    private final URI uri;
    private Resource rootNode;
    private final String id;

    //local cache
    private AbstractWMSCapabilities capabilities;
    private WMSVersion version;


    WebMapClient(WMSProvider provider, StorageConnector connector) throws DataStoreException {
        super(provider, connector);
        uri = connector.getStorageAs(URI.class);
        connector.closeAllExcept(null);

        configuration = ServiceConfiguration.builder()
            .updateBaseUri(uri.toString())
            .build();
        api = new WMSApi(configuration);

        String str = configuration.getBaseUri();
        id = str.replace("http://", "").replace("https://", "");

    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        final ParameterValueGroup parameters = WMSProvider.PARAMETERS_DESCRIPTOR.createValue();
        try {
            parameters.parameter(WMSProvider.URL.getName().getCode()).setValue(uri.toURL());
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

    public WMSApi getApi() {
        return api;
    }

    /**
     * Returns the {@linkplain AbstractWMSCapabilities capabilities} response for this server.
     *
     * @return {@linkplain AbstractWMSCapabilities capabilities} response but never {@code null}.
     * @throws CapabilitiesException
     */
    public AbstractWMSCapabilities getServiceCapabilities() throws CapabilitiesException{

        if (capabilities != null) {
            return capabilities;
        }

        final GetCapabilities capa = new GetCapabilities();
        capa.setVersion(WMSVersion.auto);
        final ServiceRequest<Supplier<AbstractWMSCapabilities>> request = api.getCapabilities(capa);

        try {
            capabilities = request.send().body().get();
        } catch (Exception ex) {
            throw new CapabilitiesException("Cannot handle GetCapabilities", ex);
        }

        //force throw CapabilitiesException if the returned capabilities object is null
        if (capabilities == null) {
            throw new CapabilitiesException("The capabilities document is null.");
        }

        version = WMSVersion.getVersion(this.capabilities.getVersion());
        return capabilities;
    }

    /**
     * Returns the request version.
     */
    public WMSVersion getVersion() throws CapabilitiesException {
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
        AbstractWMSCapabilities capas = this.capabilities;
        if (capas != null) {
            currentUpdateSequence = capas.getUpdateSequence();
        }

        final GetCapabilities getCaps = new GetCapabilities();
        getCaps.setVersion(WMSVersion.auto);
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

            } else if (lastCapa instanceof AbstractWMSCapabilities) {
                AbstractWMSCapabilities lc = (AbstractWMSCapabilities) lastCapa;
                newUpdateSequence = lc.getUpdateSequence();
            }
            changed = !Objects.equals(currentUpdateSequence, newUpdateSequence);
            if (changed) {
                capabilities = (AbstractWMSCapabilities) lastCapa;
            }

        } catch (Exception ex) {
            capabilities = null;
        }

        if (changed) {
            rootNode = null;
        }
        return changed;
    }


    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        final Resource root = getRootResource();
        return (root == null) ? Collections.EMPTY_LIST : Collections.singletonList(root);
    }

    private synchronized Resource getRootResource() throws DataStoreException {
        if (rootNode != null) {
            return rootNode;
        }
        final AbstractWMSCapabilities capa;
        try {
            capa = getServiceCapabilities();
        } catch (CapabilitiesException ex) {
            throw new DataStoreException(ex);
        }
        rootNode = asResource(capa.getCapability().getLayer()).orElse(null);
        return rootNode;
    }

    Optional<Resource> asResource(final AbstractLayer layer) throws DataStoreException {
        if (layer == null) {
            return Optional.empty();
        }

        final boolean isData = layer.getName() != null && layer.isQueryable();
        final boolean isGroup = layer.getLayer() != null && !layer.getLayer().isEmpty();
        final Resource result;
        if (isData && isGroup) {
            result = new QueryableAggregate(this, layer);
        } else if (isGroup) {
            result = new WMSAggregate(this, layer);
        } else if (isData) {
            result = new WMSResource(this, layer.getName());
        } else {
            result = null;
        }

        return Optional.ofNullable(result);
    }

    protected GridCoverageResource createReference(GenericName name) throws DataStoreException{
        return new WMSResource(this,name);
    }

    @Override
    public void close() {
    }

}
