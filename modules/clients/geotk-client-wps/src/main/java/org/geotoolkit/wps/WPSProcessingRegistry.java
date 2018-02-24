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
package org.geotoolkit.wps;

import java.io.IOException;
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
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessingRegistry;
import org.geotoolkit.wps.xml.ProcessOffering;
import org.geotoolkit.wps.xml.ProcessOfferings;
import org.geotoolkit.wps.xml.WPSCapabilities;
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
    private boolean allLoaded = false;

   /**
    * If set to true, when asking for a descriptor not yet in the cache,
    * the registry will perform a GetCapabilities request,
    * in order to look if the process has been newly added to the WPS server.
    */
    private boolean dynamicLoading = false;

    private String storageDirectory;
    private String storageURL;


    public WPSProcessingRegistry(WebProcessingClient client, boolean dynamicLoading) throws CapabilitiesException {
        this.client = client;
        this.dynamicLoading = dynamicLoading;
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

        if (desc == null) {
            throw new NoSuchIdentifierException("No process descriptor for name :" + name, name);
        } else if (desc instanceof ProcessOffering) {
            try {
                final ProcessDescriptor processDesc = toProcessDescriptor(((ProcessOffering) desc).getIdentifier().getValue());
                descriptors.put(processDesc.getIdentifier().getCode(), processDesc);
                return processDesc;
            } catch(UnsupportedParameterException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            } catch(Throwable ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        } else {
            return (ProcessDescriptor) desc;
        }
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
        if (descriptors!=null && allLoaded) return;

        if (descriptors==null) descriptors = new ConcurrentHashMap<>();
        final WPSCapabilities capabilities;
        try {
            capabilities = client.getServiceCapabilities();
        } catch(CapabilitiesException ex) {
            //find a better way to return the exception
            //it should not happen since we called a getCapabilities at registry creation
            throw new RuntimeException(ex);
        }

        if (capabilities.getProcessOfferings() == null) {
            return;
        }
        final List<? extends ProcessOffering> processBrief = capabilities.getProcessOfferings().getProcesses();

        if (loadDescription) {
            final ExecutorService exec = Executors.newFixedThreadPool(8);

            for (final ProcessOffering processBriefType : processBrief) {
                final String processId = processBriefType.getIdentifier().getValue();
                if (descriptors.get(processId) instanceof ProcessDescriptor) continue;

                exec.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final ProcessDescriptor processDesc = toProcessDescriptor(processId);
                            descriptors.put(processDesc.getIdentifier().getCode(), processDesc);
                        } catch(UnsupportedParameterException ex) {
                            LOGGER.log(Level.INFO, ex.getMessage());
                        } catch(Throwable ex) {
                            LOGGER.log(Level.INFO, ex.getMessage(),ex);
                        }
                    }
                });
            }

            exec.shutdown();
            try {
                exec.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        } else {

            for (final ProcessOffering processBriefType : processBrief) {
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
        if (processBrief == null || processBrief.getProcesses().isEmpty()) {
            return null;
        }

        final ProcessDescriptor processDesc = toProcessDescriptor(name);
        descriptors.put(processDesc.getIdentifier().getCode(), processDesc);
        return processDesc;
    }

    private ProcessDescriptor toProcessDescriptor(String processID) throws Exception {
        switch (client.getVersion()) {
            case v100 : return WPS1ProcessDescriptor.create(WPSProcessingRegistry.this, processID);
            case v200 : return WPS2ProcessDescriptor.create(WPSProcessingRegistry.this, processID);
            default : throw new IOException("Unknown wps version : "+ client.getVersion());
        }
    }


    /**
     * Specify if you want outputs sent back as references for the process identified by given name.
     * @param processId The identifier of the process wanted.
     * @param asReference True if you want references in output, false otherwise.
     * @throws NoSuchIdentifierException If we can't find a process matching given name.
     */
    public void setOutputsAsReference(final String processId, final boolean asReference) throws NoSuchIdentifierException {
        final WPS1ProcessDescriptor desc = (WPS1ProcessDescriptor) getDescriptor(processId);
        desc.setOutputAsReference(asReference && desc.isStorageSupported());
    }

    /**
     * Check the current output settings for this process.
     * @param processId The name of the process to check.
     * @return True if the process return its outputs as reference, false otherwise.
     * @throws NoSuchIdentifierException If we can't find a process matching given name.
     */
    public boolean isOutputAsReference(final String processId) throws NoSuchIdentifierException {
        return ((WPS1ProcessDescriptor)getDescriptor(processId)).isOutputAsReference();
    }

    /**
     * Set all processes to send (or not) references for its outputs. Default behaviour is no reference. An important
     * fact is that references are going to be used only if the process support storage (see
     * {@link WebProcessingClient#supportStorage(String)}).
     * @param choice True if you want reference as output, false otherwise.
     */
    public void setOutputAsReferenceForAll(final boolean choice) {
        checkDescriptors(true);
        for (Object desc : descriptors.values()) {
            ((WPS1ProcessDescriptor)desc).setOutputAsReference(choice && ((WPS1ProcessDescriptor)desc).isStorageSupported());
        }
    }

    /**
     * Inform the user if the process identified by given String can return outputs as reference or not.
     * @param processId The name of the process to check.
     * @return True if this process can use reference for its outputs, false otherwise.
     * @throws NoSuchIdentifierException If we can't find the named process on WPS server, or if we can't manage it.
     */
    public boolean supportStorage(final String processId) throws NoSuchIdentifierException {
        return ((WPS1ProcessDescriptor)getDescriptor(processId)).isStorageSupported();
    }

    /**
     * Inform the user if the process identified by given String can do quick updates of its status document.
     * @param processId The name of the process to check.
     * @return True if this process can update status before process ending, false otherwise.
     * @throws NoSuchIdentifierException If we can't find the named process on WPS server, or if we can't manage it.
     */
    public boolean supportStatus(final String processId) throws NoSuchIdentifierException {
        return ((WPS1ProcessDescriptor)getDescriptor(processId)).isStatusSupported();
    }

}
