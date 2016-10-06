/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.Unit;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.client.AbstractClient;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.client.ClientFactory;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.ows.xml.v110.BoundingBoxType;
import org.geotoolkit.ows.xml.v110.DomainMetadataType;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.*;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessingRegistry;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.iso.DefaultInternationalString;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.v100.DescribeProcess100;
import org.geotoolkit.wps.v100.Execute100;
import org.geotoolkit.wps.v100.GetCapabilities100;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.*;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.storage.DataStores;

import org.opengis.geometry.Envelope;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
import org.opengis.util.NoSuchIdentifierException;

/**
 * WPS server, used to aquiere capabilites and requests process.
 *
 * @author Quentin Boileau @module pending
 */
public class WebProcessingClient extends AbstractClient implements ProcessingRegistry {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.wps");
    private static final long TIMEOUT_CAPS = 10000L;

    /**
     * A key for {@link ExtendedParameterDescriptor} user data map. Specify the format to use for parameter, using {FormatSupport} object.
     */
    private static final String USE_FORMAT_KEY = "format";

    //process descriptors
    private final Map<String, ProcessDescriptor> descriptors = new HashMap<>();
    /** A map whose key is a process identifier, and value a boolean to specify if it supports outputs as reference (true) or not (false). */
    private final Map<String, Boolean> storageSupported = new HashMap<>();
    /** A map whose key is a process identifier, and value a boolean to specify if it supports status (true) or not (false). */
    private final Map<String, Boolean> statusSupported = new HashMap<>();

    /**
     * A map to specify for each process if we should ask its outputs as reference. Key is process identifier, and value
     * a boolean : true if we want references, false otherwise. It's important to notice that even if we set a value to
     * true, references will be used ONLY if this process can handle it (check it with {@link WebProcessingClient#supportStorage(String)}.
     */
    private final Map<String, Boolean> outputAsReference = new HashMap<>();

    private boolean descriptorsCached = false;
    private WPSCapabilitiesType capabilities;

    private String storageDirectory;
    private String storageURL;

    private boolean forceGET = true;

    /**
     * Static enumeration of WPS server versions.
     */
    public static enum WPSVersion {

        v100("1.0.0");
        private final String code;

        private WPSVersion(final String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        /**
         * Get the version enum from the string code.
         *
         * @param version
         * @return The enum which matches with the given string.
         * @throws IllegalArgumentException if the enum class does not contain any enum types for the given string
         *                                  value.
         */
        public static WPSVersion getVersion(final String version) {
            for (WPSVersion vers : values()) {
                if (vers.getCode().equals(version)) {
                    return vers;
                }
            }

            try {
                return WPSVersion.valueOf(version);
            } catch (IllegalArgumentException ex) {
            }

            throw new IllegalArgumentException("The given string \"" + version + "\" is not "
                    + "a known version.");
        }
    }

    /**
     * Constructor
     *
     * @param serverURL
     * @param version
     */
    public WebProcessingClient(final URL serverURL, final String version) throws CapabilitiesException {
        this(serverURL, null, version, true);
    }

    /**
     * Costructor with forceGET tunning.
     *
     * @param serverURL
     * @param version
     * @param forceGET if true, GetCapabilities and DescribeProcess will be request in GET, otherwise POST is used.
     * @throws CapabilitiesException
     */
    public WebProcessingClient(final URL serverURL, final String version, final boolean forceGET) throws CapabilitiesException {
        this(serverURL, null, version, forceGET);
    }

    /**
     * Constructor
     *
     * @param serverURL
     * @param security
     * @param version
     * @throws CapabilitiesException
     */
    public WebProcessingClient(final URL serverURL, final ClientSecurity security, final String version) throws CapabilitiesException {
        super(create(WPSClientFactory.PARAMETERS, serverURL, security));
        if (version.equals("1.0.0")) {
            Parameters.getOrCreate(WPSClientFactory.VERSION, parameters).setValue(WPSVersion.v100.getCode());
        } else {
            throw new IllegalArgumentException("Unknown version : " + version);
        }
        getCapabilities();
        LOGGER.log(Level.INFO, "Web processing client initialization complete.");
    }

    /**
     * Constructor
     *
     * @param serverURL
     * @param security
     * @param version
     * @param forceGET if true, GetCapabilities and DescribeProcess will be request in GET, otherwise POST is used.
     * @throws CapabilitiesException
     */
    public WebProcessingClient(final URL serverURL, final ClientSecurity security, final String version, final boolean forceGET)
            throws CapabilitiesException {
        super(create(WPSClientFactory.PARAMETERS, serverURL, security));
        if (version.equals("1.0.0")) {
            Parameters.getOrCreate(WPSClientFactory.VERSION, parameters).setValue(WPSVersion.v100.getCode());
        } else {
            throw new IllegalArgumentException("Unknown version : " + version);
        }
        this.forceGET = forceGET;

        getCapabilities();
        LOGGER.log(Level.INFO, "Web processing client initialization complete.");
    }

    /**
     * Constructor
     *
     * @param serverURL
     * @param security
     * @param version
     * @throws CapabilitiesException
     */
    public WebProcessingClient(final URL serverURL, final ClientSecurity security, final WPSVersion version) throws CapabilitiesException {
        super(create(WPSClientFactory.PARAMETERS, serverURL, security));
        if (version == null) {
            throw new IllegalArgumentException("Unknown version : " + version);
        }
        Parameters.getOrCreate(WPSClientFactory.VERSION, parameters).setValue(version);

        getCapabilities();
        LOGGER.log(Level.INFO, "Web processing client initialization complete.");
    }

    public WebProcessingClient(ParameterValueGroup params) throws CapabilitiesException {
        super(params);
        getCapabilities();
        LOGGER.log(Level.INFO, "Web processing client initialization complete.");
    }

    @Override
    public ClientFactory getFactory() {
        return (ClientFactory) DataStores.getFactoryById(WPSClientFactory.NAME);
    }

    /**
     * @return WPSVersion : currently used version for this server
     */
    public WPSVersion getVersion() {
        return WPSVersion.getVersion(Parameters.value(WPSClientFactory.VERSION, parameters));
    }

    @Override
    public Logger getLogger() {
        return super.getLogger();
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
     * @return WPSCapabilitiesType : WPS server capabilities
     */
    public WPSCapabilitiesType getCapabilities() throws CapabilitiesException {

        if (capabilities != null) {
            return capabilities;
        }

        //Thread to prevent infinite request on a server
        final Thread thread = new Thread() {

            @Override
            public void run() {
                Unmarshaller unmarshaller = null;
                try {
                    final InputStream is = createGetCapabilities().getResponseStream();
                    unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                    capabilities = ((JAXBElement<WPSCapabilitiesType>) unmarshaller.unmarshal(is)).getValue();
                    WPSMarshallerPool.getInstance().recycle(unmarshaller);
                    is.close();
                } catch (Exception ex) {
                    capabilities = null;
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        };
        thread.start();

        try {
            thread.join(TIMEOUT_CAPS);
        } catch (InterruptedException ex) {
            throw new CapabilitiesException("GetCapabilities takes too much time. Abort.");
        }

        if (capabilities == null) {
            throw new CapabilitiesException("A problem occured while getting Service capabilities.");
        }
        LOGGER.log(Level.INFO, "GetCapabilities request succeed.");
        return capabilities;
    }

    /**
     * @return ProcessDescriptions : WPS process description
     */
    private ProcessDescriptions getDescribeProcess(final List<String> processIDs) {

        final ProcessDescriptions[] description = new ProcessDescriptions[1];

        //Thread to prevent infinite request on a server
        final Thread thread = new Thread() {

            @Override
            public void run() {
                final DescribeProcessRequest describe = createDescribeProcess();
                describe.setIdentifiers(processIDs);
                try {
                    final InputStream request = describe.getResponseStream();
                    final Unmarshaller unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                    description[0] = (ProcessDescriptions) unmarshaller.unmarshal(request);
                    WPSMarshallerPool.getInstance().recycle(unmarshaller);
                    request.close();
                } catch (Exception ex) {
                    description[0] = null;
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        };
        thread.start();
        try {
            thread.join(TIMEOUT_CAPS);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.WARNING, "The thread to obtain describeProcess doesn't answer.", ex);
        }

        return description[0];
    }

    /**
     * Create a getCapabilities request.
     *
     * @return GetCapabilitiesRequest : getCapabilities request.
     */
    public GetCapabilitiesRequest createGetCapabilities() {

        switch (getVersion()) {
            case v100:
                return new GetCapabilities100(serverURL.toString(), getClientSecurity(), forceGET);
            default:
                throw new IllegalArgumentException("Version not defined or unsupported.");
        }
    }

    /**
     * Create a describe process request
     *
     * @return DescribeProcessRequest : describe process request.
     */
    public DescribeProcessRequest createDescribeProcess() {
        switch (getVersion()) {
            case v100:
                return new DescribeProcess100(serverURL.toString(), getClientSecurity(), forceGET);
            default:
                throw new IllegalArgumentException("Version was not defined or unsupported.");
        }
    }

    /**
     * Create an execute request
     *
     * @return ExecuteRequest : execute request.
     */
    public ExecuteRequest createExecute() {
        switch (getVersion()) {
            case v100:
                return new Execute100(serverURL.toString(), getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined or unsupported.");
        }
    }

    @Override
    public Identification getIdentification() {
        return getFactory().getIdentification();
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

    private void checkDescriptors() {
        if (!descriptorsCached) {
            requestDescriptors();
        }
    }

    private void requestDescriptors() {
        if (capabilities.getProcessOfferings() == null) {
            return;
        }
        final List<ProcessBriefType> processBrief = capabilities.getProcessOfferings().getProcess();

scan:   for (final ProcessBriefType processBriefType : processBrief) {

            final String processIdentifier = processBriefType.getIdentifier().getValue();

            final InternationalString processAbstract;
            if (processBriefType.getAbstract() != null) {
                processAbstract = new DefaultInternationalString(processBriefType.getAbstract().getValue());
            } else {
                processAbstract = new DefaultInternationalString("");
            }

            final List<ParameterDescriptor> inputDescriptors = new ArrayList<>();
            final List<ParameterDescriptor> outputDescriptors = new ArrayList<>();
            final Map<String, String> inputTypes = new HashMap<>();

            final ProcessDescriptions wpsProcessDescriptions = getDescribeProcess(Collections.singletonList(processIdentifier));
            if (wpsProcessDescriptions.getProcessDescription() == null) {
                continue;
            }

            final ProcessDescriptionType wpsProcessDesc = wpsProcessDescriptions.getProcessDescription().get(0);

            // INPUTS
            if (wpsProcessDesc.getDataInputs() != null) {
                final List<InputDescriptionType> inputDescriptionList = wpsProcessDesc.getDataInputs().getInput();

                for (final InputDescriptionType inputDesc : inputDescriptionList) {
                    final String inputName = inputDesc.getIdentifier().getValue();
                    final String inputAbstract = (inputDesc.getAbstract() == null)? "No description available" : inputDesc.getAbstract().getValue();
                    final Integer max = inputDesc.getMaxOccurs().intValue();
                    final Integer min = inputDesc.getMinOccurs().intValue();

                    final Map<String, String> properties = new HashMap<>();
                    properties.put("name", inputName);
                    properties.put("remarks", inputAbstract);

                    final SupportedComplexDataInputType complexInput = inputDesc.getComplexData();
                    final LiteralInputType literalInput = inputDesc.getLiteralData();
                    final SupportedCRSsType bboxInput = inputDesc.getBoundingBoxData();

                    if (complexInput != null) {
                        final ComplexDataCombinationType complexDefault = complexInput.getDefault();
                        if (complexDefault != null && complexDefault.getFormat() != null) {
                            String mime     = complexDefault.getFormat().getMimeType();
                            String encoding = complexDefault.getFormat().getEncoding();
                            String schema   = complexDefault.getFormat().getSchema();

                            /**
                             * Make a first try on default format, as it should be the more stable. If we don't support
                             * default format, we check the other supported formats until we find one we can use.
                             */
                            Class clazz = WPSIO.findClass(WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX, mime, encoding, schema, null);
                            if (clazz == null) {
                                for (ComplexDataDescriptionType currentDesc : complexInput.getSupported().getFormat()) {
                                    mime     = currentDesc.getMimeType();
                                    encoding = currentDesc.getEncoding();
                                    schema   = currentDesc.getSchema();
                                    clazz    = WPSIO.findClass(WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX, mime, encoding, schema, null);
                                    if (clazz != null) {
                                        break;
                                    }
                                }
                                if (clazz == null) {
                                    LOGGER.log(Level.WARNING, "No compatible format found for output (id: " +
                                            inputName + ", process (id: " + processIdentifier + ") is skipped.");
                                    continue scan;
                                }
                            }

                            final WPSIO.FormatSupport support = new WPSIO.FormatSupport(clazz, WPSIO.IOType.INPUT, mime, encoding, schema, false);
                            inputDescriptors.add(new ExtendedParameterDescriptor(
                                    properties, clazz, null, null, null, null, null, true, Collections.singletonMap(USE_FORMAT_KEY, (Object) support)));
                            inputTypes.put(inputName, "complex");

                        } else {
                            LOGGER.log(Level.WARNING, "Invalid describeProcess. No default format specified for input " +
                                    inputName + ". Process " + processIdentifier + ") is skipped.");
                            continue scan;
                        }

                    } else if (literalInput != null) {
                        final DomainMetadataType inputType = literalInput.getDataType();
                        Class clazz = WPSIO.findClass(WPSIO.IOType.INPUT, WPSIO.FormChoice.LITERAL, null, null, null, inputType);
                        if (clazz == null) {
                            clazz = String.class;
                        }
                        final String defaultValue = literalInput.getDefaultValue();
                        final SupportedUOMsType inputUom = literalInput.getUOMs();
                        Unit unit = null;
                        if (inputUom != null) {
                            unit = Unit.valueOf(inputUom.getDefault().getUOM().getValue());
                        }

                        WPSObjectConverter converter = null;
                        try {
                            converter = WPSIO.getConverter(clazz, WPSIO.IOType.INPUT, WPSIO.FormChoice.LITERAL);
                            if (converter == null) {
                                LOGGER.log(Level.WARNING, "Can't find the converter for the default literal input value.");
                                continue scan;
                            }
                        } catch (UnconvertibleObjectException ex) {
                            LOGGER.log(Level.WARNING, "Can't find the converter for the default literal input value.", ex);
                            continue scan;
                        }

                        //At this state the converter can't be null.
                        try {
                            inputDescriptors.add(new DefaultParameterDescriptor(properties, clazz, null, converter.convert(defaultValue, null), null, null, unit, min != 0));
                            inputTypes.put(inputName, "literal");
                        } catch (UnconvertibleObjectException ex2) {
                            LOGGER.log(Level.WARNING, "Can't convert the default literal input value.", ex2);
                            continue scan;
                        }
                    } else if (bboxInput != null) {
                        inputDescriptors.add(new DefaultParameterDescriptor(properties, Envelope.class, null, null, null, null, null, min != 0));
                        inputTypes.put(inputName, "bbox");
                    } else {
                        LOGGER.log(Level.WARNING, "Unidentifiable input (id: " + inputName + ", process is (id: " + processIdentifier + ") skipped.");
                        continue scan;
                    }
                }
            }

            //OUTPUTS
            if (wpsProcessDesc.getProcessOutputs() != null) {

                final List<OutputDescriptionType> outputDescriptionList = wpsProcessDesc.getProcessOutputs().getOutput();
                for (final OutputDescriptionType outputDesc : outputDescriptionList) {
                    final String outputName = outputDesc.getIdentifier().getValue();
                    final String outputAbstract = (outputDesc.getAbstract() == null)? "No description available" : outputDesc.getAbstract().getValue();

                    final SupportedComplexDataType complexOutput = outputDesc.getComplexOutput();
                    final LiteralOutputType literalOutput = outputDesc.getLiteralOutput();
                    final SupportedCRSsType bboxOutput = outputDesc.getBoundingBoxOutput();

                    final Map<String, String> properties = new HashMap<>();
                    properties.put("name", outputName);
                    properties.put("remarks", outputAbstract);

                    if (complexOutput != null) {
                        final ComplexDataCombinationType complexDefault = complexOutput.getDefault();
                        // If default format is missing, describe process is not valid, we stop here.
                        if (complexDefault != null && complexDefault.getFormat() != null) {
                            String mime     = complexDefault.getFormat().getMimeType();
                            String encoding = complexDefault.getFormat().getEncoding();
                            String schema   = complexDefault.getFormat().getSchema();

                            /**
                             * Make a first try on default format, as it should be the more stable. If we don't support
                             * default format, we check the other supported formats until we find one we can use.
                             */
                            Class clazz = WPSIO.findClass(WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX, mime, encoding, schema, null);
                            if (clazz == null) {
                                for (ComplexDataDescriptionType currentDesc : complexOutput.getSupported().getFormat()) {
                                    mime     = currentDesc.getMimeType();
                                    encoding = currentDesc.getEncoding();
                                    schema   = currentDesc.getSchema();
                                    clazz    = WPSIO.findClass(WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX, mime, encoding, schema, null);
                                    if (clazz != null) {
                                        break;
                                    }
                                }

                                if (clazz == null) {
                                    LOGGER.log(Level.WARNING, "No compatible format found for output (id: " +
                                            outputName + ", process (id: " + processIdentifier + ") is skipped.");
                                    continue scan;
                                }
                            }

                            final WPSIO.FormatSupport support = new WPSIO.FormatSupport(clazz, WPSIO.IOType.OUTPUT, mime, encoding, schema, false);
                            outputDescriptors.add(new ExtendedParameterDescriptor(
                                    outputName, outputAbstract, clazz, null, true, Collections.singletonMap(USE_FORMAT_KEY, (Object) support)));
                        } else {
                            LOGGER.log(Level.WARNING, "Invalid describeProcess. No default format specified for output " +
                                    outputName + ". Process " + processIdentifier + ") is skipped.");
                            continue scan;
                        }

                    } else if (literalOutput != null) {
                        final DomainMetadataType inputType = literalOutput.getDataType();
                        Class clazz = WPSIO.findClass(WPSIO.IOType.OUTPUT, WPSIO.FormChoice.LITERAL, null, null, null, inputType);
                        if (clazz == null) {
                            clazz = String.class;
                        }
                        final SupportedUOMsType inputUom = literalOutput.getUOMs();
                        Unit unit = null;
                        if (inputUom != null) {
                            unit = Unit.valueOf(inputUom.getDefault().getUOM().getValue());
                        }
                        outputDescriptors.add(new DefaultParameterDescriptor(properties, clazz, null, null, null, null, unit, true));

                    } else if (bboxOutput != null) {
                        outputDescriptors.add(new DefaultParameterDescriptor(properties, Envelope.class, null, null, null, null, null, true));

                    } else {
                        LOGGER.log(Level.WARNING, "Unidentifiable output (id: " + outputName + ", process is (id: " + processIdentifier + ") skipped.");
                        continue scan;
                    }
                }
            }

            final ParameterDescriptorGroup inputs = new ParameterBuilder().addName("inputs").createGroup(
                    inputDescriptors.toArray(new ParameterDescriptor[inputDescriptors.size()]));
            final ParameterDescriptorGroup outputs = new ParameterBuilder().addName("ouptuts").createGroup(
                    outputDescriptors.toArray(new ParameterDescriptor[outputDescriptors.size()]));

            //Process Descriptor creation
            final ProcessDescriptor processDesc = new AbstractProcessDescriptor(processIdentifier, getIdentification(), processAbstract, inputs, outputs) {
                @Override
                public Process createProcess(ParameterValueGroup input) {
                    return new WPSProcess(WebProcessingClient.this, this, inputTypes, input);
                }
            };

            storageSupported .put(processIdentifier, wpsProcessDesc.isStoreSupported());
            statusSupported  .put(processIdentifier, wpsProcessDesc.isStatusSupported());
            outputAsReference.put(processIdentifier, Boolean.FALSE);
            descriptors      .put(processIdentifier, processDesc);
        }

        descriptorsCached = true;
    }

    /**
     * Specify if you want outputs sent back as references for the process identified by given name.
     * @param processId The identifier of the process wanted.
     * @param asReference True if you want references in output, false otherwise.
     * @throws NoSuchIdentifierException If we can't find a process matching given name.
     */
    public void setOutputsAsReference(final String processId, final boolean asReference) throws NoSuchIdentifierException {
        checkDescriptors();
        final Boolean supportReference = storageSupported.get(processId);
        if(supportReference == null) {
            throw new NoSuchIdentifierException("No process descriptor for name :", processId);
        }

        outputAsReference.put(processId, supportReference && asReference);
    }

    /**
     * Check the current output settings for this process.
     * @param processId The name of the process to check.
     * @return True if the process return its outputs as reference, false otherwise.
     * @throws NoSuchIdentifierException If we can't find a process matching given name.
     */
    public boolean isOutputAsReference(final String processId) throws NoSuchIdentifierException {
        checkDescriptors();
        final Boolean asRef = outputAsReference.get(processId);
        if(asRef == null) {
            throw new NoSuchIdentifierException("No process descriptor for name :", processId);
        }
        return asRef;
    }

    /**
     * Set all processes to send (or not) references for its outputs. Default behaviour is no reference. An important
     * fact is that references are going to be used only if the process support storage (see
     * {@link WebProcessingClient#supportStorage(String)}).
     * @param choice True if you want reference as output, false otherwise.
     */
    public void setOutputAsReferenceForAll(final boolean choice) {
        checkDescriptors();
        for (Map.Entry<String, Boolean> current : outputAsReference.entrySet()) {
            current.setValue(choice && storageSupported.get(current.getKey()));
        }
    }

    /**
     * Inform the user if the process identified by given String can return outputs as reference or not.
     * @param processId The name of the process to check.
     * @return True if this process can use reference for its outputs, false otherwise.
     * @throws NoSuchIdentifierException If we can't find the named process on WPS server, or if we can't manage it.
     */
    public boolean supportStorage(final String processId) throws NoSuchIdentifierException {
        checkDescriptors();
        final Boolean result = storageSupported.get(processId);
        if (result == null) {
            throw new NoSuchIdentifierException("No process descriptor for name :", processId);
        }
        return result;
    }

    /**
     * Inform the user if the process identified by given String can do quick updates of its status document.
     * @param processId The name of the process to check.
     * @return True if this process can update status before process ending, false otherwise.
     * @throws NoSuchIdentifierException If we can't find the named process on WPS server, or if we can't manage it.
     */
    public boolean supportStatus(final String processId) throws NoSuchIdentifierException {
        checkDescriptors();
        final Boolean result = statusSupported.get(processId);
        if (result == null) {
            throw new NoSuchIdentifierException("No process descriptor for name :", processId);
        }
        return result;
    }

    /**
     * Make a WPS Execute request from {@link ParameterValueGroup values}.
     *
     * @param inputs
     * @param descriptor
     * @param inputTypes
     * @return
     * @throws ProcessException
     */
    public Execute createRequest(final ParameterValueGroup inputs, final ProcessDescriptor descriptor,
                                 final Map<String, String> inputTypes) throws ProcessException {

        try {

            final List<GeneralParameterDescriptor> inputParamDesc = inputs.getDescriptor().descriptors();
            final List<GeneralParameterDescriptor> outputParamDesc = descriptor.getOutputDescriptor().descriptors();

            final List<AbstractWPSInput> wpsIN = new ArrayList<>();
            final List<WPSOutput> wpsOUT = new ArrayList<>();

            final String processId = descriptor.getIdentifier().getCode();

            final boolean asReference = outputAsReference.get(processId);

            /*
             * INPUTS
             */
            for (final GeneralParameterDescriptor inputGeneDesc : inputParamDesc) {
                if (inputGeneDesc instanceof ParameterDescriptor) {
                    final ParameterDescriptor inputDesc = (ParameterDescriptor) inputGeneDesc;

                    final String inputIdentifier = inputDesc.getName().getCode();
                    final String type = inputTypes.get(inputIdentifier);
                    final Class inputClazz = inputDesc.getValueClass();
                    final Object value = inputs.parameter(inputIdentifier).getValue();
                    final String unit = inputDesc.getUnit() != null ? inputDesc.getUnit().toString() : null;

                    if ("literal".equals(type)) {
                        wpsIN.add(new WPSInputLiteral(inputIdentifier, String.valueOf(value), WPSConvertersUtils.getDataTypeString(getVersion().code, inputClazz), unit));

                    } else if ("bbox".equals(type)) {
                        final Envelope envelop = (Envelope) value;
                        final String crs = envelop.getCoordinateReferenceSystem().getName().getCode();
                        final int dim = envelop.getDimension();

                        final List<Double> lower = new ArrayList<>();
                        final List<Double> upper = new ArrayList<>();
                        for (int i = 0; i < dim; i++) {
                            lower.add(envelop.getLowerCorner().getOrdinate(i));
                            upper.add(envelop.getUpperCorner().getOrdinate(i));
                        }

                        wpsIN.add(new WPSInputBoundingBox(inputIdentifier, lower, upper, crs, dim));

                    } else if ("complex".equals(type)) {
                        String mime     = null;
                        String encoding = null;
                        String schema   = null;
                        if (inputGeneDesc instanceof ExtendedParameterDescriptor) {
                            final Map<String, Object> userMap = ((ExtendedParameterDescriptor) inputGeneDesc).getUserObject();
                            if(userMap.containsKey(USE_FORMAT_KEY)) {
                                final WPSIO.FormatSupport support = (WPSIO.FormatSupport) userMap.get(USE_FORMAT_KEY);
                                mime     = support.getMimeType();
                                encoding = support.getEncoding();
                                schema   = support.getSchema();
                            }
                        }

                        wpsIN.add(new WPSInputComplex(inputIdentifier, value, inputClazz, encoding, schema, mime));
                    }
                }
            }

            /*
             * OUPTUTS
             */
            for (final GeneralParameterDescriptor outputGeneDesc : outputParamDesc) {
                if (outputGeneDesc instanceof ParameterDescriptor) {
                    final ParameterDescriptor outputDesc = (ParameterDescriptor) outputGeneDesc;

                    final String outputIdentifier = outputDesc.getName().getCode();
                    final Class outputClazz = outputDesc.getValueClass();
                    String mime     = null;
                    String encoding = null;
                    String schema   = null;
                    if (outputDesc instanceof ExtendedParameterDescriptor) {
                        final Map<String, Object> userMap = ((ExtendedParameterDescriptor) outputDesc).getUserObject();
                        if(userMap.containsKey(USE_FORMAT_KEY)) {
                            final WPSIO.FormatSupport support = (WPSIO.FormatSupport) userMap.get(USE_FORMAT_KEY);
                            mime     = support.getMimeType();
                            encoding = support.getEncoding();
                            schema   = support.getSchema();
                        }
                    }

                    wpsOUT.add(new WPSOutput(outputIdentifier, encoding, schema, mime, null, asReference));
                }
            }

            final Execute100 exec100 = new Execute100(serverURL.toString(), null);
            exec100.setIdentifier(processId);
            exec100.setInputs(wpsIN);
            exec100.setOutputs(wpsOUT);
            exec100.setStorageDirectory(storageDirectory);
            exec100.setOutputStorage(asReference);
            // Status can be activated only if we ask outputs as references.
            exec100.setOutputStatus(asReference && statusSupported.get(processId));
            exec100.setStorageURL(storageURL);
            LOGGER.log(Level.INFO, "Execute request created for "+processId+" in "+((asReference)? "asynchronous": "synchronous") + " mode.");

            return exec100.makeRequest();

        } catch (UnconvertibleObjectException ex) {
            throw new ProcessException("Error during conversion step.", null, ex);
        }
    }

    /**
     * Send a secured request to the server URL in POST mode and return the unmarshalled response.
     *
     * @param request Request
     * @return Response of this request
     * @throws ProcessException is can't reach the server or if there is an
     *                          error during Marshalling/Unmarshalling request or response.
     */
    public Object sendSecuredRequestInPost(final Object request) throws JAXBException, IOException {
        final MarshallerPool pool = WPSMarshallerPool.getInstance();
        Unmarshaller unmarshaller;
        Marshaller marshaller;
        InputStream requestIS;
        OutputStream requestOS;

        unmarshaller = pool.acquireUnmarshaller();
        marshaller = pool.acquireMarshaller();

        // Build the request content (POST method)
        final StringWriter content = new StringWriter();
        marshaller.marshal(request, content);

        // Make request
        final ClientSecurity security = getClientSecurity();
        security.secure(serverURL);
        final URLConnection conec = serverURL.openConnection();
        conec.setRequestProperty("content-type", "text/xml");
        conec.setConnectTimeout((int)TIMEOUT_CAPS);
        conec.setDoOutput(true);
        security.secure(conec);

        LOGGER.log(Level.INFO, "Sending execute request.");
        // Write request content
        requestOS = conec.getOutputStream();
        security.encrypt(requestOS);
        final OutputStreamWriter writer = new OutputStreamWriter(requestOS);
        writer.write(content.toString());
        writer.flush();

        // Parse the response
        requestIS = security.decrypt(conec.getInputStream());
        final Object response = unmarshaller.unmarshal(requestIS);
        if (response instanceof JAXBElement) {
            return ((JAXBElement) response).getValue();
        }

        pool.recycle(unmarshaller);
        pool.recycle(marshaller);

        requestOS.close();
        requestIS.close();

        return response;
    }

    /**
     * Send a secured request to the server URL in GET mode and return the unmarshalled response.
     *
     * @param url Request
     * @return Response of this request
     * @throws ProcessException is can't reach the server or if there is an
     *                          error during Marshalling/Unmarshalling request or response.
     */
    public Object sendSecuredRequestInGet(final URL url) throws JAXBException, IOException {
        final ClientSecurity security = getClientSecurity();
        security.secure(url);

        final MarshallerPool pool = WPSMarshallerPool.getInstance();
        Unmarshaller unmarshaller = null;
        InputStream in = null;
        try {
            unmarshaller = pool.acquireUnmarshaller();
            in = url.openStream();
            final Object response = unmarshaller.unmarshal(in);

            pool.recycle(unmarshaller);

            if (response instanceof JAXBElement) {
                return ((JAXBElement) response).getValue();
            }
            return response;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * Fill {@link ParameterValueGroup parameters} of the process using the WPS
     * {@link ExecuteResponse response}.
     *
     * @param outputs
     * @param descriptor
     * @param response
     * @throws ProcessException if data conversion fails.
     */
    public void fillOutputs(final ParameterValueGroup outputs, final ProcessDescriptor descriptor, final ExecuteResponse response)
            throws ProcessException {
        ArgumentChecks.ensureNonNull("response", response);

        if (response.getProcessOutputs() != null) {

            final List<OutputDataType> wpsOutputs = response.getProcessOutputs().getOutput();

            LOGGER.log(Level.INFO, "Starting to parse output parameters. We found  "+wpsOutputs.size()+" of them.");
            for (final OutputDataType output : wpsOutputs) {

                LOGGER.log(Level.INFO, "Parsing "+output.getIdentifier().getValue()+" output.");
                final ParameterDescriptor outDesc = (ParameterDescriptor) descriptor.getOutputDescriptor().descriptor(output.getIdentifier().getValue());
                final Class clazz = outDesc.getValueClass();

                /*
                 * Reference
                 */
                if (output.getReference() != null) {

                    try {
                        outputs.parameter(output.getIdentifier().getValue()).setValue(WPSConvertersUtils.convertFromReference(output.getReference(), clazz));
                    } catch (UnconvertibleObjectException ex) {
                        throw new ProcessException(ex.getMessage(), null, ex);
                    }

                } else {
                    final DataType outputType = output.getData();

                    /*
                    * BBOX
                    */
                    if (outputType.getBoundingBoxData() != null) {
                        try {
                            final BoundingBoxType bbox = outputType.getBoundingBoxData();
                            final CoordinateReferenceSystem crs = CRS.forCode(bbox.getCrs());
                            final int dim = bbox.getDimensions();
                            final List<Double> lower = bbox.getLowerCorner();
                            final List<Double> upper = bbox.getUpperCorner();

                            final GeneralEnvelope envelope = new GeneralEnvelope(crs);
                            for (int i = 0; i < dim; i++) {
                                envelope.setRange(i, lower.get(i), upper.get(i));
                            }
                            outputs.parameter(output.getIdentifier().getValue()).setValue(envelope);

                        } catch (FactoryException ex) {
                            throw new ProcessException(ex.getMessage(), null, ex);
                        }

                   /*
                    * Complex
                    */
                    } else if (outputType.getComplexData() != null) {

                        try {
                            outputs.parameter(output.getIdentifier().getValue()).setValue(WPSConvertersUtils.convertFromComplex(outputType.getComplexData(), clazz));
                        } catch (UnconvertibleObjectException ex) {
                            throw new ProcessException(ex.getMessage(), null, ex);
                        }

                    /*
                    * Literal
                    */
                    } else if (outputType.getLiteralData() != null) {
                        try {
                            final LiteralDataType outputLiteral = outputType.getLiteralData();
                            final ObjectConverter converter = ObjectConverters.find(String.class, clazz);
                            outputs.parameter(output.getIdentifier().getValue()).setValue(converter.apply(outputLiteral.getValue()));
                        } catch (UnconvertibleObjectException ex) {
                            throw new ProcessException("Error during literal output conversion.", null, ex);
                        }
                    }
                }
            }
        }
    }
}
