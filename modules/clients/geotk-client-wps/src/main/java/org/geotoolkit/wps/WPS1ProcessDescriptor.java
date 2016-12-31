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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.measure.Unit;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.measure.Units;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.iso.DefaultInternationalString;
import org.geotoolkit.ows.xml.v110.DomainMetadataType;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;
import static org.geotoolkit.wps.WPSProcessingRegistry.USE_FORMAT_KEY;
import static org.geotoolkit.wps.WPSProcessingRegistry.USE_FORM_KEY;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.ProcessOffering;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.ComplexDataCombinationType;
import org.geotoolkit.wps.xml.v100.ComplexDataDescriptionType;
import org.geotoolkit.wps.xml.v100.InputDescriptionType;
import org.geotoolkit.wps.xml.v100.LiteralInputType;
import org.geotoolkit.wps.xml.v100.LiteralOutputType;
import org.geotoolkit.wps.xml.v100.OutputDescriptionType;
import org.geotoolkit.wps.xml.v100.ProcessDescriptionType;
import org.geotoolkit.wps.xml.v100.ProcessDescriptions;
import org.geotoolkit.wps.xml.v100.SupportedCRSsType;
import org.geotoolkit.wps.xml.v100.SupportedComplexDataInputType;
import org.geotoolkit.wps.xml.v100.SupportedComplexDataType;
import org.geotoolkit.wps.xml.v100.SupportedUOMsType;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WPS1ProcessDescriptor extends AbstractProcessDescriptor {

    private final WPSProcessingRegistry registry;
    private final ProcessDescriptionType type;
    private boolean outputAsReference = false;

    private WPS1ProcessDescriptor(WPSProcessingRegistry registry, ProcessDescriptionType type,
            String processIdentifier,
            InternationalString processAbstract,
            ParameterDescriptorGroup inputs,
            ParameterDescriptorGroup outputs) {
        super(processIdentifier, registry.getIdentification(), processAbstract,inputs,outputs);
        this.registry = registry;
        this.type = type;
    }

    public void setOutputAsReference(boolean outputAsReference) {
        this.outputAsReference = outputAsReference;
    }

    /**
     * Check the current output settings for this process.
     *
     * @return True if the process return its outputs as reference, false otherwise.
     */
    public boolean isOutputAsReference() {
        return outputAsReference;
    }

    public boolean isStatusSupported() {
        return type.isStatusSupported();
    }

    public boolean isStorageSupported() {
        return type.isStoreSupported();
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        final WPS1Process process = new WPS1Process(registry, this, input);
        process.setAsReference(outputAsReference);
        process.setStatusReport(isStatusSupported());
        return process;
    }


    public static WPS1ProcessDescriptor create(WPSProcessingRegistry registry, ProcessOffering processBriefType) 
            throws IOException, JAXBException, UnsupportedParameterException {


        final String processIdentifier = processBriefType.getIdentifier().getValue();

        final InternationalString processAbstract;
        if (processBriefType.getSingleAbstract() != null) {
            processAbstract = new DefaultInternationalString(processBriefType.getSingleAbstract().getValue());
        } else {
            processAbstract = new DefaultInternationalString("");
        }

        final List<ParameterDescriptor> inputDescriptors = new ArrayList<>();

        final ProcessDescriptions wpsProcessDescriptions = getDescribeProcess(registry,Collections.singletonList(processIdentifier));

        final ProcessDescriptionType wpsProcessDesc = wpsProcessDescriptions.getProcessDescription().get(0);

        // INPUTS
        if (wpsProcessDesc.getDataInputs() != null) {
            final List<InputDescriptionType> inputDescriptionList = wpsProcessDesc.getDataInputs().getInput();

            for (final InputDescriptionType inputDesc : inputDescriptionList) {
                final String inputName = inputDesc.getIdentifier().getValue();
                final String inputAbstract = (inputDesc.getAbstract() == null)? "" : inputDesc.getAbstract().getValue();
                final Integer max = inputDesc.getMaxOccurs();
                final Integer min = inputDesc.getMinOccurs();

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
                                throw new UnsupportedParameterException(processIdentifier, inputName,"No compatible format found");
                            }
                        }

                        final WPSIO.FormatSupport support = new WPSIO.FormatSupport(clazz, WPSIO.IOType.INPUT, mime, encoding, schema, false);
                        final Map<String,Object> userMap = new HashMap<>();
                        userMap.put(USE_FORMAT_KEY, support);
                        userMap.put(USE_FORM_KEY, "complex");

                        inputDescriptors.add(new ExtendedParameterDescriptor(
                                properties, clazz, null, null, null, null, null, true, userMap));

                    } else {
                        throw new UnsupportedParameterException(processIdentifier, inputName,"No default format specified for input");
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
                        unit = Units.valueOf(inputUom.getDefault().getUOM().getValue());
                    }

                    WPSObjectConverter converter = null;
                    try {
                        converter = WPSIO.getConverter(clazz, WPSIO.IOType.INPUT, WPSIO.FormChoice.LITERAL);
                        if (converter == null) {
                            throw new UnsupportedParameterException(processIdentifier, inputName,"Can't find the converter for the default literal input value.");
                        }
                    } catch (UnconvertibleObjectException ex) {
                        throw new UnsupportedParameterException(processIdentifier, inputName,"Can't find the converter for the default literal input value.", ex);
                    }

                    //At this state the converter can't be null.
                    final Map<String,Object> userMap = new HashMap<>();
                    userMap.put(USE_FORM_KEY, "literal");
                    try {
                        inputDescriptors.add(new ExtendedParameterDescriptor(properties,
                                clazz, null, converter.convert(defaultValue, null), null, null, unit, min != 0,userMap));
                    } catch (UnconvertibleObjectException ex2) {
                        throw new UnsupportedParameterException(processIdentifier, inputName,"Can't convert the default literal input value.", ex2);
                    }
                } else if (bboxInput != null) {
                    final Map<String,Object> userMap = new HashMap<>();
                    userMap.put(USE_FORM_KEY, "bbox");

                    inputDescriptors.add(new ExtendedParameterDescriptor(properties,
                            Envelope.class, null, null, null, null, null, min != 0,userMap));
                } else {
                    throw new UnsupportedParameterException(processIdentifier, inputName,"Unidentifiable input");
                }
            }
        }


        final List<ParameterDescriptor> outputDescriptors = new ArrayList<>();

        //OUTPUTS
        if (wpsProcessDesc.getProcessOutputs() != null) {

            final List<OutputDescriptionType> outputDescriptionList = wpsProcessDesc.getProcessOutputs().getOutput();
            for (final OutputDescriptionType outputDesc : outputDescriptionList) {
                final String outputName = outputDesc.getIdentifier().getValue();
                final String outputAbstract = (outputDesc.getAbstract() == null)? null: outputDesc.getAbstract().getValue();

                final SupportedComplexDataType complexOutput = outputDesc.getComplexOutput();
                final LiteralOutputType literalOutput = outputDesc.getLiteralOutput();
                final SupportedCRSsType bboxOutput = outputDesc.getBoundingBoxOutput();


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
                                throw new UnsupportedParameterException(processIdentifier,outputName,"No compatible format found for output");
                            }
                        }

                        final WPSIO.FormatSupport support = new WPSIO.FormatSupport(clazz, WPSIO.IOType.OUTPUT, mime, encoding, schema, false);
                        outputDescriptors.add(new ExtendedParameterDescriptor(
                                outputName, outputAbstract, clazz, null, true, Collections.singletonMap(USE_FORMAT_KEY, (Object) support)));
                    } else {
                        throw new UnsupportedParameterException(processIdentifier,outputName,"No default format specified for output");
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
                        unit = Units.valueOf(inputUom.getDefault().getUOM().getValue());
                    }
                    
                    if (unit!=null && Double.class.equals(clazz)) {
                        outputDescriptors.add(new ParameterBuilder().addName(outputName).setRemarks(outputAbstract).createBounded(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, unit));
                    } else {
                        outputDescriptors.add(new ParameterBuilder().addName(outputName).setRemarks(outputAbstract).create(clazz, null));
                    }

                } else if (bboxOutput != null) {
                    outputDescriptors.add(new ParameterBuilder().addName(outputName).setRemarks(outputAbstract).create(Envelope.class, null));

                } else {
                    throw new UnsupportedParameterException(processIdentifier,outputName,"Unidentifiable output");
                }
            }
        }



        final ParameterDescriptorGroup inputs = new ParameterBuilder().addName("inputs").createGroup(
                inputDescriptors.toArray(new ParameterDescriptor[inputDescriptors.size()]));
        final ParameterDescriptorGroup outputs = new ParameterBuilder().addName("ouptuts").createGroup(
                outputDescriptors.toArray(new ParameterDescriptor[outputDescriptors.size()]));


        return new WPS1ProcessDescriptor(registry, wpsProcessDesc, processIdentifier, processAbstract, inputs, outputs);
    }



    /**
     * @return ProcessDescriptions : WPS process description
     */
    private static ProcessDescriptions getDescribeProcess(WPSProcessingRegistry registry, final List<String> processIDs)
            throws IOException, JAXBException {

        final ProcessDescriptions description;

        //Thread to prevent infinite request on a server
        final DescribeProcessRequest describe = registry.getClient().createDescribeProcess();
        describe.setTimeout(10000);
        describe.getContent().setIdentifier(processIDs);
        try (final InputStream request = describe.getResponseStream()) {
            final Unmarshaller unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
            description = (ProcessDescriptions) unmarshaller.unmarshal(request);
            WPSMarshallerPool.getInstance().recycle(unmarshaller);
        }

        return description;
    }
}
