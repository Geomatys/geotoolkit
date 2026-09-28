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
package org.geotoolkit.ogcapi.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.ogcapi.client.processes.ProcessesApi;
import org.geotoolkit.ogcapi.dto.processes.ProcessDescription;
import org.geotoolkit.ogcapi.dto.processes.ProcessList;
import org.geotoolkit.ogcapi.dto.processes.ProcessSummary;
import org.geotoolkit.ogcapi.request.processes.GetProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessingRegistry;
import org.opengis.metadata.identification.Identification;
import org.opengis.util.NoSuchIdentifierException;

/**
 * Processing registry backed by an OGC API - Processes service.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class OgcApiProcessingRegistry implements ProcessingRegistry, AutoCloseable {

    static final Logger LOGGER = Logger.getLogger("org.geotoolkit.ogcapi");

    private final ProcessesApi api;
    private final Identification identification;

    /**
     * Process summaries or descriptors, in server order.
     * Summaries are replaced by descriptors once described.
     */
    private Map<String, Object> descriptors;

    public OgcApiProcessingRegistry(OpenApiConfiguration configuration) {
        this.api = new ProcessesApi(configuration);

        final String name = configuration.getBaseUri().replace("http://", "").replace("https://", "");
        final DefaultServiceIdentification identification = new DefaultServiceIdentification();
        final DefaultCitation citation = new DefaultCitation(name);
        citation.setIdentifiers(Collections.singleton(new DefaultIdentifier(name)));
        identification.setCitation(citation);
        this.identification = identification;
    }

    ProcessesApi getApi() {
        return api;
    }

    @Override
    public Identification getIdentification() {
        return identification;
    }

    @Override
    public List<ProcessDescriptor> getDescriptors() {
        final List<ProcessDescriptor> result = new ArrayList<>();
        for (String name : getNames()) {
            try {
                result.add(getDescriptor(name));
            } catch (NoSuchIdentifierException | RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Failed to describe process " + name, ex);
            }
        }
        return result;
    }

    @Override
    public synchronized List<String> getNames() {
        return new ArrayList<>(getCache().keySet());
    }

    @Override
    public synchronized ProcessDescriptor getDescriptor(String name) throws NoSuchIdentifierException {
        final Object candidate = getCache().get(name);
        if (candidate instanceof ProcessDescriptor pd) {
            return pd;
        }

        // not described yet, or not listed : the process may have been added after the listing
        final ProcessDescription description;
        try {
            description = api.getProcess(new GetProcess().processId(name)).send().body().get();
        } catch (ServiceException ex) {
            final NoSuchIdentifierException nsi = new NoSuchIdentifierException("No process descriptor for name : " + name, name);
            nsi.initCause(ex);
            throw nsi;
        }
        if (description == null) {
            throw new NoSuchIdentifierException("No process descriptor for name : " + name, name);
        }

        final ProcessDescriptor pd = OgcApiProcessDescriptor.create(this, description);
        descriptors.put(name, pd);
        return pd;
    }

    /**
     * Load the process list on first call.
     */
    private synchronized Map<String, Object> getCache() {
        if (descriptors == null) {
            final ProcessList list;
            try {
                list = api.getProcesses().send().body().get();
            } catch (ServiceException ex) {
                throw new IllegalStateException("Failed to retrieve the process list", ex);
            }
            final Map<String, Object> cache = new LinkedHashMap<>();
            if (list != null) {
                for (ProcessSummary summary : list.getProcesses()) {
                    if (summary.getId() != null) {
                        cache.put(summary.getId(), summary);
                    }
                }
            }
            descriptors = cache;
        }
        return descriptors;
    }

    @Override
    public void close() throws Exception {
        api.close();
    }
}
