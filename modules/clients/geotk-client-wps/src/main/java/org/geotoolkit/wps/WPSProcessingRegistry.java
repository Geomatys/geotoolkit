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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessingRegistry;
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;
import org.geotoolkit.wps.xml.ProcessOffering;
import org.geotoolkit.wps.xml.WPSCapabilities;
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

    /**
     * A key for {@link ExtendedParameterDescriptor} user data map. Specify the format to use for parameter, using {FormatSupport} object.
     */
    static final String USE_FORMAT_KEY = "format";
    /**
     * A key for {@link ExtendedParameterDescriptor} user data map. Specify the form : literal/bbox/complex, using {FormatSupport} object.
     */
    static final String USE_FORM_KEY = "form";
    
    private final WebProcessingClient client;

    //process descriptors
    private Map<String, ProcessDescriptor> descriptors;
    
    private String storageDirectory;
    private String storageURL;
    
    
    public WPSProcessingRegistry(WebProcessingClient client) throws CapabilitiesException {
        this.client = client;
        client.getCapabilities();
    }

    public WebProcessingClient getClient() {
        return client;
    }

    @Override
    public Identification getIdentification() {
        return client.getFactory().getIdentification();
    }

    @Override
    public List<ProcessDescriptor> getDescriptors() {
        checkDescriptors();
        final Collection<ProcessDescriptor> values = descriptors.values();
        return new ArrayList<>(values);
    }

    @Override
    public List<String> getNames() {
        checkDescriptors();
        final Set<String> keys = descriptors.keySet();
        return new ArrayList<>(keys);
    }

    @Override
    public ProcessDescriptor getDescriptor(final String name) throws NoSuchIdentifierException {
        checkDescriptors();
        final ProcessDescriptor desc = descriptors.get(name);
        if (desc == null) {
            throw new NoSuchIdentifierException("No process descriptor for name :", name);
        } else {
            return desc;
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
    
    private synchronized void checkDescriptors() {
        if (descriptors!=null) return;

        descriptors = new HashMap<>();
        final WPSCapabilities capabilities;
        try {
            capabilities = client.getCapabilities();
        } catch(CapabilitiesException ex) {
            //find a better way to return the exception
            //it should not happen since we called a getCapabilities at registry creation
            throw new RuntimeException(ex);
        }

        if (capabilities.getProcessOfferings() == null) {
            return;
        }
        final List<? extends ProcessOffering> processBrief = capabilities.getProcessOfferings().getProcesses();

        for (final ProcessOffering processBriefType : processBrief) {
            try {
                if(processBriefType instanceof org.geotoolkit.wps.xml.v100.ProcessDescriptionType) {
                    final ProcessDescriptor processDesc = WPS1ProcessDescriptor.create(this, processBriefType);
                    descriptors.put(processDesc.getIdentifier().getCode(), processDesc);
                }else if(processBriefType instanceof org.geotoolkit.wps.xml.v100.ProcessBriefType) {
                    final ProcessDescriptor processDesc = WPS1ProcessDescriptor.create(this, processBriefType);
                    descriptors.put(processDesc.getIdentifier().getCode(), processDesc);
                } else if(processBriefType instanceof org.geotoolkit.wps.xml.v200.ProcessSummaryType) {
                    final ProcessDescriptor processDesc = WPS2ProcessDescriptor.create(this, (org.geotoolkit.wps.xml.v200.ProcessSummaryType)processBriefType);
                    descriptors.put(processDesc.getIdentifier().getCode(), processDesc);
                }
                
            } catch(UnsupportedParameterException ex) {
                LOGGER.log(Level.INFO, ex.getMessage());
            } catch(Throwable ex) {
                LOGGER.log(Level.INFO, ex.getMessage(),ex);
            }
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
        checkDescriptors();
        for (ProcessDescriptor desc : descriptors.values()) {
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
