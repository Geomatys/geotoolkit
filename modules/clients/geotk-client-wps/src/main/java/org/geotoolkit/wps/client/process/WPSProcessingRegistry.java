/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.wps.client.process;

import java.io.IOException;
import java.io.UncheckedIOException;
import org.geotoolkit.wps.client.WebProcessingClient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessingRegistry;
import org.geotoolkit.wps.client.WPSClientFactory;
import org.geotoolkit.wps.xml.v200.Capabilities;
import org.geotoolkit.wps.xml.v200.Contents;
import org.geotoolkit.wps.xml.v200.ProcessOffering;
import org.geotoolkit.wps.xml.v200.ProcessOfferings;
import org.geotoolkit.wps.xml.v200.ProcessSummary;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.util.NoSuchIdentifierException;

/**
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WPSProcessingRegistry implements ProcessingRegistry {

    static final Logger LOGGER = Logging.getLogger("org.geotoolkit.wps");

    private final WebProcessingClient client;

    //process descriptors
    private Map<String, Object> descriptors;

   /**
    * If set to true, when asking for a descriptor not yet in the cache,
    * the registry will perform a GetCapabilities request,
    * in order to look if the process has been newly added to the WPS server.
    */
    private boolean dynamicLoading = false;

    private String storageDirectory;
    private String storageURL;


    public WPSProcessingRegistry(WebProcessingClient client) throws CapabilitiesException {
        this(client, isDynamicLoading(client));
    }

    public WPSProcessingRegistry(WebProcessingClient client, final boolean dynamicLoading) throws CapabilitiesException {
        this.client = client;
        this.dynamicLoading = dynamicLoading;
        client.getCapabilities();
    }

    public WebProcessingClient getClient() {
        return client;
    }

    @Override
    public Identification getIdentification() {
        final Identifier name = client.getOpenParameters().getDescriptor().getName();
        final DefaultServiceIdentification identification = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(name);
        final DefaultCitation citation = new DefaultCitation(name.getCode());
        citation.setIdentifiers(Collections.singleton(id));
        identification.setCitation(citation);
        return identification;
    }

    @Override
    public List<ProcessDescriptor> getDescriptors() {
        checkDescriptors(true);
        final Collection values = descriptors.values();
        return new ArrayList<>(values);
    }

    @Override
    public List<String> getNames() {
        checkDescriptors(false);
        final Set<String> keys = descriptors.keySet();
        return new ArrayList<>(keys);
    }

    @Override
    public ProcessDescriptor getDescriptor(final String name) throws NoSuchIdentifierException {
        checkDescriptors(false);
        Object desc = descriptors.get(name);

        // if dynamic loading enabled we check the WPS server to see
        // if the process has been added after the registry start.
        if (desc == null && dynamicLoading) {
            try {
                desc = checkDescriptor(name);
            } catch(Exception ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
                throw new NoSuchIdentifierException("No process descriptor for name :" + name, name);
            }
        }

        if (desc instanceof ProcessDescriptor) {
            return (ProcessDescriptor) desc;
        }

        final ProcessDescriptor pd;
        if (desc == null) {
            throw new NoSuchIdentifierException("No process descriptor for name :" + name, name);
        } else if (desc instanceof ProcessOffering) {
            try {
                pd =WPS2ProcessDescriptor.create(this, (ProcessOffering) desc);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            } catch (JAXBException|UnsupportedParameterException ex) {
                throw new RuntimeException(ex);
            }
        } else if (desc instanceof ProcessSummary) {
            try {
                pd = toProcessDescriptor(((ProcessSummary) desc).getIdentifier().getValue());
            } catch(UnsupportedParameterException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            } catch(Throwable ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        } else throw new UnsupportedOperationException("Cannot work with "+desc.getClass());

        descriptors.put(pd.getIdentifier().getCode(), pd);
        return pd;
    }

    public String getStorageDirectory() {
        return storageDirectory;
    }

    public void setStorageDirectory(String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    public String getStorageURL() {
        return storageURL;
    }

    public void setStorageURL(String storageURL) {
        this.storageURL = storageURL;
    }

    /**
     *
     * @param loadDescription force loading all descriptor
     */
    private synchronized void checkDescriptors(boolean loadDescription) {
        if (descriptors!=null && !loadDescription) return;

        if (descriptors==null) descriptors = new ConcurrentHashMap<>();
        final Capabilities capabilities;
        try {
            capabilities = client.getServiceCapabilities();
        } catch(CapabilitiesException ex) {
            //find a better way to return the exception
            //it should not happen since we called a getCapabilities at registry creation
            throw new RuntimeException(ex);
        }

        final Contents contents = capabilities.getContents();
        if (contents == null) {
            return;
        }

        final List<ProcessSummary> processBrief = contents.getProcessSummary();

        if (loadDescription) {
            final ExecutorService exec = Executors.newFixedThreadPool(8);

            for (final ProcessSummary processBriefType : processBrief) {
                final String processId = processBriefType.getIdentifier().getValue();
                if (descriptors.get(processId) instanceof ProcessDescriptor) continue;

                exec.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final ProcessDescriptor processDesc = toProcessDescriptor(processId);
                            descriptors.put(processDesc.getIdentifier().getCode(), processDesc);
                        } catch(UnsupportedParameterException ex) {
                            LOGGER.log(Level.WARNING, ex.getMessage());
                        } catch(Exception ex) {
                            LOGGER.log(Level.WARNING, ex.getMessage(),ex);
                        }
                    }
                });
            }

            exec.shutdown();
            try {
                exec.awaitTermination(10, TimeUnit.MINUTES);// TODO: better timeout management
            } catch (InterruptedException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        } else {
            for (final ProcessSummary processBriefType : processBrief) {
                descriptors.put(processBriefType.getIdentifier().getValue(), processBriefType);
            }
        }
    }

    /**
     * Look for a process in the distant WPS server.
     * If the process is present in the Capabilities document,
     * a new descriptor will be added to the list.
     *
     * @param name Identifier of the process we search.
     */
    private ProcessDescriptor checkDescriptor(String name) throws Exception {
        ProcessOfferings processBrief = client.getDescribeProcess(Collections.singletonList(name));
        if (processBrief == null || processBrief.getProcessOffering().isEmpty()) {
            return null;
        }

        final ProcessDescriptor processDesc = toProcessDescriptor(name);
        descriptors.put(processDesc.getIdentifier().getCode(), processDesc);
        return processDesc;
    }

    private ProcessDescriptor toProcessDescriptor(String processID) throws Exception {
        return WPS2ProcessDescriptor.create(WPSProcessingRegistry.this, processID);
    }

    private static boolean isDynamicLoading(final WebProcessingClient client) {
        final Parameters p = Parameters.castOrWrap(client.getOpenParameters());
        final Boolean isDynamic = p.getValue(WPSClientFactory.DYNAMIC_LOADING);
        return isDynamic == null? false : isDynamic;
    }
}
