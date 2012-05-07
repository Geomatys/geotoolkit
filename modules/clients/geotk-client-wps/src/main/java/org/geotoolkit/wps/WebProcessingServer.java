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

import java.awt.image.RenderedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.Unit;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.client.AbstractServer;
import org.geotoolkit.client.ServerFactory;
import org.geotoolkit.client.ServerFinder;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.ows.xml.v110.DomainMetadataType;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.*;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessingRegistry;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wps.v100.DescribeProcess100;
import org.geotoolkit.wps.v100.Execute100;
import org.geotoolkit.wps.v100.GetCapabilities100;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.*;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;
import org.opengis.util.NoSuchIdentifierException;

/**
 * WPS server, used to aquiere capabilites and requests process.
 * @author Quentin Boileau
 * @modul pending
 */
public class WebProcessingServer extends AbstractServer implements ProcessingRegistry {
    
    private static final Logger LOGGER = Logging.getLogger(WebProcessingServer.class);
    
    //process descriptors
    private final Map<String,ProcessDescriptor> descriptors = new HashMap<String,ProcessDescriptor>();
    private boolean isDescriptorsRequested = false;
    
    private WPSCapabilitiesType capabilities;

    private static final Map<String, Class> SCHEMAS_CLASS_MAP = new HashMap<String, Class>();
    static {
        SCHEMAS_CLASS_MAP.put("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd", Geometry.class);
        SCHEMAS_CLASS_MAP.put("http://schemas.opengis.net/gml/3.1.1/base/feature.xsd", FeatureCollection.class);
    }
    
    private static final Map<String, Class> MIME_CLASS_MAP = new HashMap<String, Class>();
    static {
        MIME_CLASS_MAP.put("text/gml", FeatureCollection.class);
        MIME_CLASS_MAP.put("image/png", RenderedImage.class);
        MIME_CLASS_MAP.put("image/jpeg", RenderedImage.class);
        MIME_CLASS_MAP.put("image/gif", RenderedImage.class);
        MIME_CLASS_MAP.put("image/tiff", RenderedImage.class);
        MIME_CLASS_MAP.put("image/bmp", RenderedImage.class);
        MIME_CLASS_MAP.put("image/geotiff", GridCoverage2D.class);
    }
    
    /**
     * Static enumeration of WPS server versions. 
     */
    public static enum WPSVersion{

        v100("1.0.0");
        private final String code;

        private WPSVersion(final String code) {
            this.code = code;
        }
        public String getCode(){
            return code;
        }
        
        /**
         * Get the version enum from the string code.
         *
         * @param version
         * @return The enum which matches with the given string.
         * @throws IllegalArgumentException if the enum class does not contain any enum types
         *                                  for the given string value.
         */
        public static WPSVersion getVersion(final String version) {
            for (WPSVersion vers : values()) {
                if (vers.getCode().equals(version)) {
                    return vers;
                }
            }

            try{
                return WPSVersion.valueOf(version);
            }catch(IllegalArgumentException ex){}

            throw new IllegalArgumentException("The given string \""+ version +"\" is not " +
                    "a known version.");
        }
        
    }
    
   /**
     * Constructor
     * @param serverURL
     * @param version 
     */  
   public WebProcessingServer(final URL serverURL, final String version) {
        this(serverURL,null,version);
    }
   
   /**
     * Constructor
     * @param serverURL
     * @param version 
     */  
   public WebProcessingServer(final URL serverURL, final ClientSecurity security, final String version) {
        super(create(WPSServerFactory.PARAMETERS, serverURL, security));
       if (version.equals("1.0.0")) {
            Parameters.getOrCreate(WPSServerFactory.VERSION, parameters).setValue(WPSVersion.v100.getCode());
       } else {
           throw new IllegalArgumentException("Unkonwed version : " + version);
       }
       this.capabilities = null;
    }
   
   /**
     * Constructor
     * @param serverURL
     * @param version 
     */  
   public WebProcessingServer(final URL serverURL, final ClientSecurity security, final WPSVersion version) {
        super(create(WPSServerFactory.PARAMETERS, serverURL, security));
        if (version == null) {
            throw new IllegalArgumentException("Unkonwed version : " + version);
        }
        Parameters.getOrCreate(WPSServerFactory.VERSION, parameters).setValue(version);
        this.capabilities = null;
    }

    public WebProcessingServer(ParameterValueGroup params) {
        super(params);
    }

    @Override
    public ServerFactory getFactory() {
        return ServerFinder.getFactory(WPSServerFactory.NAME);
    }
    
    /**
     * @return WPSVersion : currently used version for this server
     */
    public WPSVersion getVersion() {
        return WPSVersion.getVersion(Parameters.value(WPSServerFactory.VERSION, parameters));
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
                    final URL url = createGetCapabilities().getURL();
                    final Unmarshaller unmarhaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                    capabilities = ((JAXBElement<WPSCapabilitiesType>) unmarhaller.unmarshal(url)).getValue();
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
                    final URL url = describe.getURL();
                    final Unmarshaller unmarhaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                    description[0] = (ProcessDescriptions) unmarhaller.unmarshal(url);
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
     * @return GetCapabilitiesRequest : getCapabilities request.
     */
    public GetCapabilitiesRequest createGetCapabilities() {

        switch (getVersion()) {
            case v100:
                return new GetCapabilities100(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Create a describe process request
     * @return DescribeProcessRequest : describe process request.
     */
    public DescribeProcessRequest createDescribeProcess(){
        switch (getVersion()) {
            case v100:
                return new DescribeProcess100(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
    
    /**
     * Create an execute request
     * @return ExecuteRequest : execute request.
     */
    public ExecuteRequest createExecute(){
        switch (getVersion()) {
            case v100:
                return new Execute100(serverURL.toString(),getClientSecurity());
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
    public ProcessDescriptor getDescriptor(String name) throws NoSuchIdentifierException {
        checkDescriptors();
        final ProcessDescriptor desc = descriptors.get(name);
        if(desc == null){
            throw new NoSuchIdentifierException("No process descriptor for name :", name);
        }else{
            return desc;
        }
    }
    
    private void checkDescriptors () {
        if (!isDescriptorsRequested) {
            requestDescriptors();
        }
    }
    
    private void requestDescriptors () {
        getCapabilities();
        if (capabilities.getProcessOfferings() != null ) {
            final List<ProcessBriefType> processBrief = capabilities.getProcessOfferings().getProcess();
            
            for (final ProcessBriefType processBriefType : processBrief) {
                
                final String processIdentifier = processBriefType.getIdentifier().getValue();
                final InternationalString processAbstract   = new DefaultInternationalString(processBriefType.getAbstract().getValue());
                final List<ParameterDescriptor> inputDescriptors = new ArrayList<ParameterDescriptor>();
                final List<ParameterDescriptor> outputDescriptors = new ArrayList<ParameterDescriptor>();
                
                boolean supportedIO = true;
                
                final ProcessDescriptions wpsProcessDescriptions = getDescribeProcess(Collections.singletonList(processIdentifier));
                if (wpsProcessDescriptions.getProcessDescription() != null ) {
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
                            
                            final Map<String,String> properties = new HashMap<String, String>();
                            properties.put("name", inputName);
                            properties.put("remarks", inputAbstract);
                            
                            final SupportedComplexDataInputType complexInput = inputDesc.getComplexData();
                            final LiteralInputType literalInput = inputDesc.getLiteralData();
                            final SupportedCRSsType bboxInput = inputDesc.getBoundingBoxData();
                            
                            
                            if (complexInput != null) {
                                final ComplexDataCombinationType complexDefault = complexInput.getDefault();
                                final Class clazz = findClass(complexDefault);
                                if (clazz == null) {
                                    supportedIO = false;
                                    break;
                                }
                                inputDescriptors.add(new DefaultParameterDescriptor(properties, clazz, null, null, null, null, null, min == 0));
                            }
                            if (literalInput != null) {
                                final DomainMetadataType inputType = literalInput.getDataType();
                                final Class clazz = findClass(inputType);
                                final String defaultValue = literalInput.getDefaultValue();
                                final SupportedUOMsType inputUom = literalInput.getUOMs();
                                Unit unit = null;
                                if (inputUom != null) {
                                    unit = Unit.valueOf(inputUom.getDefault().getUOM().getValue());
                                }
                                
                                try {
                                    final ObjectConverter converter = ConverterRegistry.system().converter(String.class, clazz);
                                    inputDescriptors.add(new DefaultParameterDescriptor(properties, clazz, null, converter.convert(defaultValue), null, null, unit, min == 0));
                                } catch (NonconvertibleObjectException ex) {
                                    Logger.getLogger(WebProcessingServer.class.getName()).log(Level.WARNING, null, ex);
                                }
                                
                            }
                            if (bboxInput != null) {
                                inputDescriptors.add(new DefaultParameterDescriptor(properties, Envelope.class, null, null, null, null, null, min == 0));
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

                                final Map<String,String> properties = new HashMap<String, String>();
                                properties.put("name", outputName);
                                properties.put("remarks", outputAbstract);
                                
                                if (complexOutput != null) {
                                    final ComplexDataCombinationType complexDefault = complexOutput.getDefault();
                                    final Class clazz = findClass(complexDefault);
                                    if (clazz == null) {
                                        supportedIO = false;
                                        break;
                                    }
                                    
                                    outputDescriptors.add(new DefaultParameterDescriptor(outputName, outputAbstract, clazz, null, true));
                                }
                                if (literalOutput != null) {
                                    final DomainMetadataType inputType = literalOutput.getDataType();
                                    final Class clazz = findClass(inputType);
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
                    final ParameterDescriptorGroup ouptuts = new DefaultParameterDescriptorGroup("ouptuts", 
                            outputDescriptors.toArray(new DefaultParameterDescriptor[outputDescriptors.size()]));
                    
                    final ProcessDescriptor processDesc = new AbstractProcessDescriptor(processIdentifier, getIdentification(), processAbstract, inputs, ouptuts) {

                        @Override
                        public Process createProcess(ParameterValueGroup input) {
                            Process proc = new AbstractProcess(this, input) {

                                @Override
                                protected void execute() throws ProcessException {
                                    //TODO execute the process
                                    throw new UnsupportedOperationException("Not supported yet.");
                                }
                            };
                            return proc;
                        }
                    };
                    
                    descriptors.put(processIdentifier, processDesc);
                } else {
                    Logger.getLogger(WebProcessingServer.class.getName()).log(Level.WARNING, "Process not supported "+processIdentifier);
                }
            }
        }
    }
    
    private Class findClass (final DomainMetadataType dataType) {
        ArgumentChecks.ensureNonNull("dataType", dataType);
        
        String value = dataType.getValue();
        
        Class clazz = String.class;
        if (value != null && !value.isEmpty()) {
            try {
                clazz = Class.forName(value);
            } catch (ClassNotFoundException ex) {
                value = value.toLowerCase();
                if(value.contains("double")) {
                    clazz = Double.class;
                } else if (value.contains("boolean")) {
                    clazz = Boolean.class;
                } else if (value.contains("float")) {
                    clazz = Float.class;
                } else if (value.contains("integer")) {
                    clazz = Integer.class;
                } else if (value.contains("long")) {
                    clazz = Long.class;
                }
            }
        }
        
        return clazz;
    }
    
    private Class findClass(final ComplexDataCombinationType complexDefault) {
        ArgumentChecks.ensureNonNull("complexDefault", complexDefault);
        Class clazz = null;
        
        final ComplexDataDescriptionType desc = complexDefault.getFormat();
        final String mime = desc.getMimeType();
        final String schema = desc.getSchema();
        
        if ( mime != null) {
            if( MIME_CLASS_MAP.containsKey(mime)) {
                clazz = MIME_CLASS_MAP.get(mime);
            }
        }
        
        if (schema != null) {
            if (SCHEMAS_CLASS_MAP.containsKey(schema)) {
                clazz = SCHEMAS_CLASS_MAP.get(schema);
            }
        }
        return clazz;
    }
    
}
