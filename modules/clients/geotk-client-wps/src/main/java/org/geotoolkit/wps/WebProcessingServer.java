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
import java.net.MalformedURLException;
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

import org.geotoolkit.client.AbstractServer;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.client.ServerFactory;
import org.geotoolkit.client.ServerFinder;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.ows.xml.v110.BoundingBoxType;
import org.geotoolkit.ows.xml.v110.DomainMetadataType;
import org.geotoolkit.ows.xml.v110.ExceptionReport;
import org.geotoolkit.ows.xml.v110.ExceptionType;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.parameter.ExtendedParameterDescriptor;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.*;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessingRegistry;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.iso.DefaultInternationalString;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.v100.DescribeProcess100;
import org.geotoolkit.wps.v100.Execute100;
import org.geotoolkit.wps.v100.GetCapabilities100;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.*;
import org.geotoolkit.xml.MarshallerPool;

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
public class WebProcessingServer extends AbstractServer implements ProcessingRegistry {

    private static final Logger LOGGER = Logging.getLogger(WebProcessingServer.class);
    private static final long TIMEOUT_CAPS = 10000L;

    /**
     * A key for {@link ExtendedParameterDescriptor} user data map. Specify the format to use for parameter, using {FormatSupport} object.
     */
    private static final String USE_FORMAT_KEY = "format";

    //process descriptors
    private final Map<String, ProcessDescriptor> descriptors = new HashMap<String, ProcessDescriptor>();
    /** A map whose key is a process identifier, and value a boolean to specify if it supports outputs as reference (true) or not (false). */
    private final Map<String, Boolean> storageSupported = new HashMap<String, Boolean>();
    /** A map whose key is a process identifier, and value a boolean to specify if it supports status (true) or not (false). */
    private final Map<String, Boolean> statusSupported = new HashMap<String, Boolean>();

    /**
     * A map to specify for each process if we should ask its outputs as reference. Key is process identifier, and value
     * a boolean : true if we want references, false otherwise. It's important to notice that even if we set a value to
     * true, references will be used ONLY if this process can handle it (check it with {@link WebProcessingServer#supportStorage(String)}.
     */
    private final Map<String, Boolean> outputAsReference = new HashMap<String, Boolean>();

    private boolean descriptorsCached = false;
    private WPSCapabilitiesType capabilities;

    private String storageDirectory;
    private String storageURL;

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
    public WebProcessingServer(final URL serverURL, final String version) throws CapabilitiesException {
        this(serverURL, null, version);
    }

    /**
     * Constructor
     *
     * @param serverURL
     * @param security
     * @param version
     */
    public WebProcessingServer(final URL serverURL, final ClientSecurity security, final String version) throws CapabilitiesException {
        super(create(WPSServerFactory.PARAMETERS, serverURL, security));
        if (version.equals("1.0.0")) {
            Parameters.getOrCreate(WPSServerFactory.VERSION, parameters).setValue(WPSVersion.v100.getCode());
        } else {
            throw new IllegalArgumentException("Unknown version : " + version);
        }
        getCapabilities();
    }

    /**
     * Constructor
     *
     * @param serverURL
     * @param security
     * @param version
     */
    public WebProcessingServer(final URL serverURL, final ClientSecurity security, final WPSVersion version) throws CapabilitiesException {
        super(create(WPSServerFactory.PARAMETERS, serverURL, security));
        if (version == null) {
            throw new IllegalArgumentException("Unknown version : " + version);
        }
        Parameters.getOrCreate(WPSServerFactory.VERSION, parameters).setValue(version);

        getCapabilities();
    }

    public WebProcessingServer(ParameterValueGroup params) throws CapabilitiesException {
        super(params);
        getCapabilities();
    }

    @Override
    public ServerFactory getFactory() {
        return ServerFinder.getFactoryById(WPSServerFactory.NAME);
    }

    /**
     * @return WPSVersion : currently used version for this server
     */
    public WPSVersion getVersion() {
        return WPSVersion.getVersion(Parameters.value(WPSServerFactory.VERSION, parameters));
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
                    WPSMarshallerPool.getInstance().release(unmarshaller);
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
                Unmarshaller unmarshaller = null;
                try {
                    final InputStream request = describe.getResponseStream();
                    unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                    description[0] = (ProcessDescriptions) unmarshaller.unmarshal(request);
                    WPSMarshallerPool.getInstance().release(unmarshaller);
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
                return new GetCapabilities100(serverURL.toString(), getClientSecurity());
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
                return new DescribeProcess100(serverURL.toString(), getClientSecurity());
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
        return new ArrayList<ProcessDescriptor>(values);
    }

    @Override
    public List<String> getNames() {
        checkDescriptors();
        final Set<String> keys = descriptors.keySet();
        return new ArrayList<String>(keys);
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

        for (final ProcessBriefType processBriefType : processBrief) {

            final String processIdentifier = processBriefType.getIdentifier().getValue();
            final InternationalString processAbstract = new DefaultInternationalString(processBriefType.getAbstract().getValue());
            final List<ParameterDescriptor> inputDescriptors = new ArrayList<ParameterDescriptor>();
            final List<ParameterDescriptor> outputDescriptors = new ArrayList<ParameterDescriptor>();
            final Map<String, String> inputTypes = new HashMap<String, String>();

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
                    final String inputAbstract = inputDesc.getAbstract().getValue();
                    final Integer max = Integer.valueOf(inputDesc.getMaxOccurs().intValue());
                    final Integer min = Integer.valueOf(inputDesc.getMinOccurs().intValue());

                    final Map<String, String> properties = new HashMap<String, String>();
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
                                    break;
                                }
                            }

                            final WPSIO.FormatSupport support = new WPSIO.FormatSupport(clazz, WPSIO.IOType.INPUT, mime, encoding, schema, false);
                            inputDescriptors.add(new ExtendedParameterDescriptor(
                                    properties, clazz, null, null, null, null, null, true, Collections.singletonMap(USE_FORMAT_KEY, (Object) support)));
                            inputTypes.put(inputName, "complex");

                        } else {
                            LOGGER.log(Level.WARNING, "Invalid describeProcess. No default format specified for input " +
                                    inputName + ". Process " + processIdentifier + ") is skipped.");
                            break;
                        }

                    } else if (literalInput != null) {
                        final DomainMetadataType inputType = literalInput.getDataType();
                        final Class clazz = WPSIO.findClass(WPSIO.IOType.INPUT, WPSIO.FormChoice.LITERAL, null, null, null, inputType);
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
                                break;
                            }
                        } catch (NonconvertibleObjectException ex) {
                            LOGGER.log(Level.WARNING, "Can't find the converter for the default literal input value.", ex);
                            break;
                        }

                        //At this state the converter can't be null.
                        try {
                            inputDescriptors.add(new DefaultParameterDescriptor(properties, clazz, null, converter.convert(defaultValue, null), null, null, unit, min != 0));
                            inputTypes.put(inputName, "literal");
                        } catch (NonconvertibleObjectException ex2) {
                            LOGGER.log(Level.WARNING, "Can't convert the default literal input value.", ex2);
                            break;
                        }
                    } else if (bboxInput != null) {
                        inputDescriptors.add(new DefaultParameterDescriptor(properties, Envelope.class, null, null, null, null, null, min != 0));
                        inputTypes.put(inputName, "bbox");
                    } else {
                        LOGGER.log(Level.WARNING, "Unidentifiable input (id: " + inputName + ", process is (id: " + processIdentifier + ") skipped.");
                        break;
                    }
                }
            }

            //OUTPUTS
            if (wpsProcessDesc.getProcessOutputs() != null) {

                final List<OutputDescriptionType> outputDescriptionList = wpsProcessDesc.getProcessOutputs().getOutput();
                for (final OutputDescriptionType outputDesc : outputDescriptionList) {
                    final String outputName = outputDesc.getIdentifier().getValue();
                    final String outputAbstract = outputDesc.getAbstract().getValue();

                    final SupportedComplexDataType complexOutput = outputDesc.getComplexOutput();
                    final LiteralOutputType literalOutput = outputDesc.getLiteralOutput();
                    final SupportedCRSsType bboxOutput = outputDesc.getBoundingBoxOutput();

                    final Map<String, String> properties = new HashMap<String, String>();
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
                                    break;
                                }
                            }

                            final WPSIO.FormatSupport support = new WPSIO.FormatSupport(clazz, WPSIO.IOType.OUTPUT, mime, encoding, schema, false);
                            outputDescriptors.add(new ExtendedParameterDescriptor(
                                    outputName, outputAbstract, clazz, null, true, Collections.singletonMap(USE_FORMAT_KEY, (Object) support)));
                        } else {
                            LOGGER.log(Level.WARNING, "Invalid describeProcess. No default format specified for output " +
                                    outputName + ". Process " + processIdentifier + ") is skipped.");
                            break;
                        }

                    } else if (literalOutput != null) {
                        final DomainMetadataType inputType = literalOutput.getDataType();
                        final Class clazz = WPSIO.findClass(WPSIO.IOType.OUTPUT, WPSIO.FormChoice.LITERAL, null, null, null, inputType);
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
                        break;
                    }
                }
            }

            final ParameterDescriptorGroup inputs = new DefaultParameterDescriptorGroup("inputs",
                    inputDescriptors.toArray(new DefaultParameterDescriptor[inputDescriptors.size()]));
            final ParameterDescriptorGroup outputs = new DefaultParameterDescriptorGroup("ouptuts",
                    outputDescriptors.toArray(new DefaultParameterDescriptor[outputDescriptors.size()]));

            //Process Descriptor creation
            final ProcessDescriptor processDesc = new AbstractProcessDescriptor(processIdentifier, getIdentification(), processAbstract, inputs, outputs) {

                @Override
                public Process createProcess(ParameterValueGroup input) {

                    //Process creation
                    Process proc = new AbstractProcess(this, input) {

                        @Override
                        protected void execute() throws ProcessException {

                            final Execute exec = createRequest(getInput(), getDescriptor(), inputTypes);
                            final ExecuteResponse response = sendExecuteRequest(exec, this);
                            fillOutputs(outputParameters, getDescriptor(), response);
                        }
                    };
                    return proc;
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
     * {@link WebProcessingServer#supportStorage(String)}).
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

            final List<AbstractWPSInput> wpsIN = new ArrayList<AbstractWPSInput>();
            final List<WPSOutput> wpsOUT = new ArrayList<WPSOutput>();

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
                        wpsIN.add(new WPSInputLiteral(inputIdentifier, String.valueOf(value), WPSConvertersUtils.getDataTypeString(inputClazz), unit));

                    } else if ("bbox".equals(type)) {
                        final Envelope envelop = (Envelope) value;
                        final String crs = envelop.getCoordinateReferenceSystem().getName().getCode();
                        final int dim = envelop.getDimension();

                        final List<Double> lower = new ArrayList<Double>();
                        final List<Double> upper = new ArrayList<Double>();
                        for (int i = 0; i < dim; i++) {
                            lower.add(Double.valueOf(envelop.getLowerCorner().getOrdinate(i)));
                            upper.add(Double.valueOf(envelop.getUpperCorner().getOrdinate(i)));
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
            return exec100.makeRequest();

        } catch (NonconvertibleObjectException ex) {
            throw new ProcessException("Error during conversion step.", null, ex);
        }
    }

    /**
     * Send the Execute request to the server URL an return the unmarshalled response.
     *
     * @param exec    the request
     * @param process process used for throw ProcessException
     * @return ExecuteResponse.
     * @throws ProcessException is can't reach the server or if there is an error during Marshalling/Unmarshalling request
     *                          or response.
     */
    private ExecuteResponse sendExecuteRequest(final Execute exec, final Process process) throws ProcessException {
        try {
            Object respObj = sendSecuredRequestInPost(exec);

            if (respObj instanceof ExecuteResponse) {
                respObj = checkResult((ExecuteResponse) respObj);
                return (ExecuteResponse) respObj;
            }

            if (respObj instanceof ExceptionReport) {
                final ExceptionReport report = (ExceptionReport) respObj;
                final ExceptionType excep = report.getException().get(0);
                throw new ProcessException("Exception when executing the process.", process, new Exception(excep.toString()));
            }
            throw new ProcessException("Invalid response type.", process, null);

        } catch (ProcessException e) {
            throw e;
        } catch (JAXBException ex) {
            throw new ProcessException("Error when trying to parse the Execute response xml: ", process, ex);
        } catch (IOException ex) {
            throw new ProcessException("Error when trying to send request to the WPS server :", process, ex);
        } catch (Exception e) {
            throw new ProcessException(e.getMessage(), process, e);
        }
    }

    /**
     * A Function to ensure response object is success or failure. Otherwise, we request continually statusLocation until
     * we reach wanted result.
     * @param respObj The execute response given by service.
     */
    private ExecuteResponse checkResult(ExecuteResponse respObj) throws IOException, JAXBException, InterruptedException {
        StatusType status = respObj.getStatus();
        if (status.getProcessFailed() != null || status.getProcessSucceeded() != null) {
            return respObj;
        }

        final Unmarshaller unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();

        final ClientSecurity security = getClientSecurity();
        final URL statusLocation = security.secure(new URL(respObj.getStatusLocation()));
        Object tmpResponse;
        int timeLapse = 1000;
        /*
         * We start querying distant status location. To be aware of process success (or failure), we keep doing request
         * over time, until we get the right content. The time interval used for checking increase at each request, to
         * avoid overloading.
         */
        while(true) {
            timeLapse = timeLapse*2;
            synchronized (this) {
                wait(timeLapse);
            }
            tmpResponse = unmarshaller.unmarshal(security.decrypt(statusLocation.openStream()));
            if (tmpResponse instanceof JAXBElement) {
                tmpResponse = ((JAXBElement) tmpResponse).getValue();
            }

            if (tmpResponse instanceof ExecuteResponse) {
                status = ((ExecuteResponse) tmpResponse).getStatus();
                if (status.getProcessFailed() != null || status.getProcessSucceeded() != null) {
                    respObj = (ExecuteResponse) tmpResponse;
                    return respObj;
                }
            }
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
        conec.setConnectTimeout(60);
        conec.setDoOutput(true);
        security.secure(conec);

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

        pool.release(unmarshaller);
        pool.release(marshaller);

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

            pool.release(unmarshaller);

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

            for (final OutputDataType output : wpsOutputs) {

                final ParameterDescriptor outDesc = (ParameterDescriptor) descriptor.getOutputDescriptor().descriptor(output.getIdentifier().getValue());
                final Class clazz = outDesc.getValueClass();
                
                /*
                 * Reference
                 */
                if (output.getReference() != null) {

                    try {
                        outputs.parameter(output.getIdentifier().getValue()).setValue(WPSConvertersUtils.convertFromReference(output.getReference(), clazz));
                    } catch (NonconvertibleObjectException ex) {
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
                            final CoordinateReferenceSystem crs = CRS.decode(bbox.getCrs());
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
                        } catch (NonconvertibleObjectException ex) {
                            throw new ProcessException(ex.getMessage(), null, ex);
                        }
                        
                    /*
                    * Literal
                    */
                    } else if (outputType.getLiteralData() != null) {
                        try {
                            final LiteralDataType outputLiteral = outputType.getLiteralData();
                            final ObjectConverter converter = ConverterRegistry.system().converter(String.class, clazz);
                            outputs.parameter(output.getIdentifier().getValue()).setValue(converter.convert(outputLiteral.getValue()));
                        } catch (NonconvertibleObjectException ex) {
                            throw new ProcessException("Error during literal output conversion.", null, ex);
                        }
                    }
                }
            }
        }
    }
}
