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
import org.geotoolkit.client.ServerFactory;
import org.geotoolkit.client.ServerFinder;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.ows.xml.v110.BoundingBoxType;
import org.geotoolkit.ows.xml.v110.DomainMetadataType;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.*;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessingRegistry;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.DefaultInternationalString;
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
    //process descriptors
    private final Map<String, ProcessDescriptor> descriptors = new HashMap<String, ProcessDescriptor>();
    private boolean isDescriptorsRequested = false;
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
         * value.
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
    public WebProcessingServer(final URL serverURL, final String version) {
        this(serverURL, null, version);
    }

    /**
     * Constructor
     *
     * @param serverURL
     * @param version
     */
    public WebProcessingServer(final URL serverURL, final ClientSecurity security, final String version) {
        super(create(WPSServerFactory.PARAMETERS, serverURL, security));
        if (version.equals("1.0.0")) {
            Parameters.getOrCreate(WPSServerFactory.VERSION, parameters).setValue(WPSVersion.v100.getCode());
        } else {
            throw new IllegalArgumentException("Unknowned version : " + version);
        }
        this.capabilities = null;
    }

    /**
     * Constructor
     *
     * @param serverURL
     * @param version
     */
    public WebProcessingServer(final URL serverURL, final ClientSecurity security, final WPSVersion version) {
        super(create(WPSServerFactory.PARAMETERS, serverURL, security));
        if (version == null) {
            throw new IllegalArgumentException("Unknowned version : " + version);
        }
        Parameters.getOrCreate(WPSServerFactory.VERSION, parameters).setValue(version);
        this.capabilities = null;
    }

    public WebProcessingServer(ParameterValueGroup params) {
        super(params);
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
    public WPSCapabilitiesType getCapabilities() {

        if (capabilities != null) {
            return capabilities;
        }
        //Thread to prevent infinite request on a server
        final Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    final InputStream is = createGetCapabilities().getResponseStream();
                    final Unmarshaller unmarhaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                    capabilities = ((JAXBElement<WPSCapabilitiesType>) unmarhaller.unmarshal(is)).getValue();
                } catch (Exception ex) {
                    capabilities = null;
                    try {
                        LOGGER.log(Level.WARNING, "Wrong URL, the server doesn't answer : " + createGetCapabilities().getURL().toString(), ex);
                    } catch (MalformedURLException ex1) {
                        LOGGER.log(Level.WARNING, "Malformed URL, the server doesn't answer. ", ex1);
                    }
                }
            }
        };
        thread.start();
        final long start = System.currentTimeMillis();
        try {
            thread.join(10000);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.WARNING, "The thread to obtain GetCapabilities doesn't answer.", ex);
        }
        if ((System.currentTimeMillis() - start) > 10000) {
            LOGGER.log(Level.WARNING, "TimeOut error, the server takes too much time to answer. ");
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

                try {
                    final InputStream request = describe.getResponseStream();
                    final Unmarshaller unmarhaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                    description[0] = (ProcessDescriptions) unmarhaller.unmarshal(request);
                } catch (Exception ex) {
                    description[0] = null;
                    try {
                        LOGGER.log(Level.WARNING, "Wrong URL, the server doesn't answer : " + describe.getURL().toString(), ex);
                    } catch (MalformedURLException ex1) {
                        LOGGER.log(Level.WARNING, "Malformed URL, the server doesn't answer. ", ex1);
                    }
                }
            }
        };
        thread.start();
        final long start = System.currentTimeMillis();
        try {
            thread.join(10000);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.WARNING, "The thread to obtain GetCapabilities doesn't answer.", ex);
        }
        if ((System.currentTimeMillis() - start) > 10000) {
            LOGGER.log(Level.WARNING, "TimeOut error, the server takes too much time to answer. ");
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
                throw new IllegalArgumentException("Version was not defined");
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
                throw new IllegalArgumentException("Version was not defined");
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
                throw new IllegalArgumentException("Version was not defined");
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
        if (!isDescriptorsRequested) {
            requestDescriptors();
        }
    }

    private void requestDescriptors() {
        getCapabilities();
        if (capabilities.getProcessOfferings() != null) {
            final List<ProcessBriefType> processBrief = capabilities.getProcessOfferings().getProcess();

            for (final ProcessBriefType processBriefType : processBrief) {

                final String processIdentifier = processBriefType.getIdentifier().getValue();
                final InternationalString processAbstract = new DefaultInternationalString(processBriefType.getAbstract().getValue());
                final List<ParameterDescriptor> inputDescriptors = new ArrayList<ParameterDescriptor>();
                final List<ParameterDescriptor> outputDescriptors = new ArrayList<ParameterDescriptor>();
                final Map<String, String> inputTypes = new HashMap<String, String>();

                boolean supportedIO = true;

                final ProcessDescriptions wpsProcessDescriptions = getDescribeProcess(Collections.singletonList(processIdentifier));
                if (wpsProcessDescriptions.getProcessDescription() != null) {
                    final ProcessDescriptionType wpsProcessDesc = wpsProcessDescriptions.getProcessDescription().get(0);

                    if (wpsProcessDesc.getDataInputs() != null) {
                        final List<InputDescriptionType> inputDescriptionList = wpsProcessDesc.getDataInputs().getInput();
                        final List<OutputDescriptionType> outputDescriptionList = wpsProcessDesc.getProcessOutputs().getOutput();

                        // INPUTS
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
                                    final String mime = complexDefault.getFormat().getMimeType(); 
                                    final String encoding = complexDefault.getFormat().getEncoding();
                                    final String schema = complexDefault.getFormat().getSchema();

                                    final Class clazz = WPSIO.findClass(WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX, mime, encoding, schema, null);
                                    if (clazz == null) {
                                        LOGGER.log(Level.WARNING, "Input complex class for "+inputName+" not found.");
                                        supportedIO = false;
                                        break;
                                    }
                                    inputDescriptors.add(new DefaultParameterDescriptor(properties, clazz, null, null, null, null, null, min == 0));
                                    inputTypes.put(inputName, "complex");
                                } else {
                                    LOGGER.log(Level.WARNING, "No defaut format for complex input "+inputName+".");
                                    supportedIO = false;
                                }
                            }
                            
                            if (literalInput != null) {
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
                                        supportedIO = false;
                                        break;
                                    }
                                } catch (NonconvertibleObjectException ex) {
                                    LOGGER.log(Level.WARNING, "Can't find the converter for the default literal input value.", ex);
                                    supportedIO = false;
                                    break;
                                }
                               
                                //At this state the converter can't be null.
                                try {
                                    inputDescriptors.add(new DefaultParameterDescriptor(properties, clazz, null, converter.convert(defaultValue, null), null, null, unit, min == 0));
                                    inputTypes.put(inputName, "literal");
                                } catch (NonconvertibleObjectException ex2) {
                                    LOGGER.log(Level.WARNING, "Can't convert the default literal input value.", ex2);
                                    supportedIO = false;
                                    break;
                                }
                            }
                            
                            if (bboxInput != null) {
                                inputDescriptors.add(new DefaultParameterDescriptor(properties, Envelope.class, null, null, null, null, null, min == 0));
                                inputTypes.put(inputName, "bbox");
                            }
                        }

                        if (supportedIO) {
                            //OUTPUTS
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
                                    if (complexDefault != null && complexDefault.getFormat() != null) {
                                        final String mime = complexDefault.getFormat().getMimeType(); 
                                        final String encoding = complexDefault.getFormat().getEncoding();
                                        final String schema = complexDefault.getFormat().getSchema();

                                        final Class clazz = WPSIO.findClass(WPSIO.IOType.OUTPUT, WPSIO.FormChoice.COMPLEX, mime, encoding, schema, null);
                                        if (clazz == null) {
                                            LOGGER.log(Level.WARNING, "Output complex class for "+outputName+" not found.");
                                            supportedIO = false;
                                            break;
                                        }
                                        
                                        outputDescriptors.add(new DefaultParameterDescriptor(outputName, outputAbstract, clazz, null, true));
                                    } else  {
                                        LOGGER.log(Level.WARNING, "No defaut format for complex output "+outputName+".");
                                        supportedIO = false;
                                    }
                                }
                                
                                if (literalOutput != null) {
                                    final DomainMetadataType inputType = literalOutput.getDataType();
                                    final Class clazz = WPSIO.findClass(WPSIO.IOType.OUTPUT, WPSIO.FormChoice.LITERAL, null, null, null, inputType);
                                    final SupportedUOMsType inputUom = literalOutput.getUOMs();
                                    Unit unit = null;
                                    if (inputUom != null) {
                                        unit = Unit.valueOf(inputUom.getDefault().getUOM().getValue());
                                    }

                                    outputDescriptors.add(new DefaultParameterDescriptor(properties, clazz, null, null, null, null, unit, true));
                                }
                                if (bboxOutput != null) {
                                    outputDescriptors.add(new DefaultParameterDescriptor(properties, Envelope.class, null, null, null, null, null, true));
                                }
                            }
                        }
                    }
                }

                if (supportedIO) {
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
                                    final ExecuteResponse response = sendRequest(exec, this);
                                    fillOutputs(outputParameters, getDescriptor(), response);
                                }
                            };
                            return proc;
                        }
                    };

                    descriptors.put(processIdentifier, processDesc);
                } else {
                    LOGGER.log(Level.WARNING, "Process " + processIdentifier + " not supported.");
                }
            }
        }
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
                        
                        wpsIN.add(new WPSInputBoundingBox(inputIdentifier,lower, upper, crs, dim));
                        
                    } else if ("complex".equals(type)) {
                        //get the defaults mimeType/encoding/schema for the requested input class.
                        final WPSIO.FormatSupport support = WPSIO.getDefaultFormats(inputClazz, WPSIO.IOType.OUTPUT);
                        if (support != null) {
                            final String mime = support.getMimeType();
                            final String encoding = support.getEncoding();
                            final String schema = support.getSchema();
                            wpsIN.add(new WPSInputComplex(inputIdentifier, value, inputClazz, encoding, schema, mime));
                        }
                    }
                }
            }

            /*
             * OUPTUTS
             */
            for (final GeneralParameterDescriptor outputGeneDesc : outputParamDesc) {
                if (outputGeneDesc instanceof ParameterDescriptor) {
                    final ParameterDescriptor outputDesc = (ParameterDescriptor) outputGeneDesc;
                    
                    wpsOUT.add(new WPSOutput(outputDesc.getName().getCode()));
                }
            }

            final Execute100 exec100 = new Execute100(serverURL.toString(), null);
            exec100.setIdentifier(descriptor.getIdentifier().getCode());
            exec100.setInputs(wpsIN);
            exec100.setOutputs(wpsOUT);
            exec100.setStorageDirectory(storageDirectory);
            exec100.setStorageURL(storageURL);
            return exec100.makeRequest();
            
        } catch (NonconvertibleObjectException ex) {
            throw new ProcessException("Error during conversion step.", null, ex);
        }
    }

    /**
     * Send the Execute request to the server URL an return the unmarshalled response.
     * 
     * @param exec the request
     * @param process process used for throw ProcessException
     * @return ExecuteResponse.
     * @throws ProcessException is can't reach the server or if there is an error durring Marshalling/Unmarshalling request 
     * or response.
     */
    private ExecuteResponse sendRequest(final Execute exec, final Process process) throws ProcessException {
        
        final MarshallerPool pool = WPSMarshallerPool.getInstance();
        Unmarshaller unmarshaller = null;
        Marshaller marshaller = null;
        InputStream requestIS = null;
        OutputStream requestOS = null;

        try {
            unmarshaller = pool.acquireUnmarshaller();
            marshaller = pool.acquireMarshaller();

            // Build the request content (POST method)
            final StringWriter content = new StringWriter();
            marshaller.marshal(exec, content);

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
            final Object respObj = unmarshaller.unmarshal(requestIS);

            if (respObj instanceof ExecuteResponse) {
                return (ExecuteResponse) respObj;
            }

            throw new ProcessException("Invalid response type.", process, null);

        } catch (JAXBException ex) {
            throw new ProcessException("Error when trying to parse the Execute response xml: ", process, ex);
        } catch (IOException ex) {
            throw new ProcessException("Error when trying to send request to the WPS server :", process, ex);
        } finally {
            pool.release(unmarshaller);
            pool.release(marshaller);
            try {
                if (requestIS != null) {
                    requestIS.close();
                }
                if (requestOS != null) {
                    requestOS.close();
                }
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Can't close stream.", ex);
            }
        }
    }
    
    /**
     * Fill {@link ParameterValueGroup parameters} of the process using the WPS {@link ExecuteResponse response}.
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
