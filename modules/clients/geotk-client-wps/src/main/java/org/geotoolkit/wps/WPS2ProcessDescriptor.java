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
import javax.measure.Unit;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.measure.Units;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.iso.DefaultInternationalString;
import org.geotoolkit.ows.xml.v200.DomainMetadataType;
import org.geotoolkit.ows.xml.v200.ValueType;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;
import static org.geotoolkit.wps.WPSProcessingRegistry.USE_FORMAT_KEY;
import static org.geotoolkit.wps.WPSProcessingRegistry.USE_FORM_KEY;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v200.BoundingBoxData;
import org.geotoolkit.wps.xml.v200.ComplexDataType;
import org.geotoolkit.wps.xml.v200.DataDescriptionType;
import org.geotoolkit.wps.xml.v200.DescriptionType;
import org.geotoolkit.wps.xml.v200.Format;
import org.geotoolkit.wps.xml.v200.InputDescriptionType;
import org.geotoolkit.wps.xml.v200.LiteralDataType;
import org.geotoolkit.wps.xml.v200.OutputDescriptionType;
import org.geotoolkit.wps.xml.v200.ProcessDescriptionType;
import org.geotoolkit.wps.xml.v200.ProcessOffering;
import org.geotoolkit.wps.xml.v200.ProcessOfferings;
import org.geotoolkit.wps.xml.v200.ProcessSummaryType;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * WPS2 process descriptor.
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WPS2ProcessDescriptor extends AbstractProcessDescriptor {

    private final WPSProcessingRegistry registry;
    private ProcessSummaryType summary;

    public WPS2ProcessDescriptor(String name, WPSProcessingRegistry registry, InternationalString abs,
            ParameterDescriptorGroup inputs, ParameterDescriptorGroup outputs) {
        super(name,registry.getIdentification(),abs,inputs,outputs);
        this.registry = registry;
    }

    public ProcessSummaryType getSummary() {
        return summary;
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new WPS2Process(registry, this, input);
    }

    public static ProcessDescriptor create(WPSProcessingRegistry registry, ProcessSummaryType summary) throws IOException, JAXBException, UnsupportedParameterException {
        final String processIdentifier = summary.getIdentifier().getValue();

        final InternationalString abs;
        if (summary.getSingleAbstract() != null) {
            abs = new DefaultInternationalString(summary.getSingleAbstract().getValue());
        } else {
            abs = new DefaultInternationalString("");
        }

        final ProcessOfferings offerings = getDescribeProcess(registry, processIdentifier);
        final ProcessOffering offering = offerings.getProcessOffering().get(0);
        final ProcessDescriptionType process = offering.getProcess();

        final List<GeneralParameterDescriptor> inputLst = new ArrayList<>();
        final List<GeneralParameterDescriptor> outputLst = new ArrayList<>();
        for (final InputDescriptionType input : process.getInput()) {
            inputLst.add(toDescriptor(processIdentifier,input));
        }

        for (final OutputDescriptionType outputDesc : process.getOutput()) {
            outputLst.add(toDescriptor(processIdentifier,outputDesc));
        }

        final ParameterDescriptorGroup inputs = new ParameterBuilder().addName("inputs").createGroup(
                inputLst.toArray(new GeneralParameterDescriptor[inputLst.size()]));
        final ParameterDescriptorGroup outputs = new ParameterBuilder().addName("ouptuts").createGroup(
                outputLst.toArray(new GeneralParameterDescriptor[outputLst.size()]));

        return new WPS2ProcessDescriptor(processIdentifier, registry, abs, inputs, outputs);
    }
    
    public static ProcessDescriptor create(WPSProcessingRegistry registry, ProcessOffering offering) throws IOException, JAXBException, UnsupportedParameterException {
        final String processIdentifier = offering.getIdentifier().getValue();

        final InternationalString abs;
        if (offering.getSingleAbstract() != null) {
            abs = new DefaultInternationalString(offering.getSingleAbstract().getValue());
        } else {
            abs = new DefaultInternationalString("");
        }

        final ProcessDescriptionType process = offering.getProcess();

        final List<GeneralParameterDescriptor> inputLst = new ArrayList<>();
        final List<GeneralParameterDescriptor> outputLst = new ArrayList<>();
        for (final InputDescriptionType input : process.getInput()) {
            inputLst.add(toDescriptor(processIdentifier,input));
        }

        for (final OutputDescriptionType outputDesc : process.getOutput()) {
            outputLst.add(toDescriptor(processIdentifier,outputDesc));
        }

        final ParameterDescriptorGroup inputs = new ParameterBuilder().addName("inputs").createGroup(
                inputLst.toArray(new GeneralParameterDescriptor[inputLst.size()]));
        final ParameterDescriptorGroup outputs = new ParameterBuilder().addName("ouptuts").createGroup(
                outputLst.toArray(new GeneralParameterDescriptor[outputLst.size()]));

        return new WPS2ProcessDescriptor(processIdentifier, registry, abs, inputs, outputs);
    }

    /**
     * @return ProcessDescriptions : WPS process description
     */
    private static ProcessOfferings getDescribeProcess(WPSProcessingRegistry registry, final String processID)
            throws IOException, JAXBException {

        final ProcessOfferings description;

        //Thread to prevent infinite request on a server
        final DescribeProcessRequest describe = registry.getClient().createDescribeProcess();
        describe.setTimeout(10000);
        describe.getContent().setIdentifier(Collections.singletonList(processID));
        try (final InputStream request = describe.getResponseStream()) {
            final Unmarshaller unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
            description = (ProcessOfferings) unmarshaller.unmarshal(request);
            WPSMarshallerPool.getInstance().recycle(unmarshaller);
        }

        return description;
    }

    /**
     * Convert DescriptionType to GeneralParameterDescriptor.
     * 
     * @param input
     * @return
     * @throws UnsupportedOperationException if data type could not be mapped
     */
    private static GeneralParameterDescriptor toDescriptor(String processId, DescriptionType input) throws UnsupportedParameterException{

        final List<? extends DescriptionType> subInputs;
        final DataDescriptionType dataDescType;
        final boolean required;
        if (input instanceof InputDescriptionType) {
            final InputDescriptionType id = (InputDescriptionType) input;
            subInputs = id.getInput();
            dataDescType = id.getDataDescription()==null?null : id.getDataDescription().getValue();
            final String max = id.getMaxOccurs();
            final Integer min = id.getMinOccurs();
            required = min!=0;
        } else if(input instanceof OutputDescriptionType) {
            final OutputDescriptionType od = (OutputDescriptionType) input;
            subInputs = od.getOutput();
            dataDescType = od.getDataDescription()==null?null : od.getDataDescription().getValue();
            required = true;
        } else {
            throw new IllegalArgumentException("Unexpected description type "+input.getClass());
        }

        final String inputName = input.getIdentifier().getValue();
        final String inputAbstract = input.getAbstract().isEmpty() ? null : input.getAbstract().get(0).getValue();
        
        final Map<String, String> properties = new HashMap<>();
        properties.put("name", inputName);
        if(!input.getAbstract().isEmpty() && !input.getAbstract().get(0).getValue().isEmpty())
        properties.put("remarks", input.getAbstract().get(0).getValue());


        if (dataDescType instanceof LiteralDataType) {
            final LiteralDataType cd = (LiteralDataType) dataDescType;

            for(LiteralDataType.LiteralDataDomain domain : cd.getLiteralDataDomain()) {

                Class clazz = getValueClass(domain.getDataType());
                if (clazz == null) clazz = String.class;

                String defaultValueValue = null;
                final ValueType defaultValue = domain.getDefaultValue();
                if (defaultValue!=null) defaultValueValue = defaultValue.getValue();
                final Unit unit = getUnit(domain.getUOM());

                WPSObjectConverter converter = null;
                try {
                    converter = WPSIO.getConverter(clazz, WPSIO.IOType.INPUT, WPSIO.FormChoice.LITERAL);
                    if (converter == null) {
                        throw new UnsupportedParameterException(processId,inputName,"Can't find the converter for the default literal input value.");
                    }
                } catch (UnconvertibleObjectException ex) {
                    throw new UnsupportedParameterException(processId,inputName,"Can't find the converter for the default literal input value.", ex);
                }

                //At this state the converter can't be null.
                try {
                    final Map<String,Object> userMap = new HashMap<>();
                    userMap.put(USE_FORM_KEY, "literal");
                    return new ExtendedParameterDescriptor(properties, clazz,
                            null, converter.convert(defaultValueValue, null), null, null, unit, required,userMap);
                } catch (UnconvertibleObjectException ex2) {
                    throw new UnsupportedParameterException(processId,inputName,"Can't convert the default literal input value.", ex2);
                }
            }

            throw new UnsupportedParameterException(processId,inputName,"Unidentifiable literal input "+inputName);

        } else if (dataDescType instanceof ComplexDataType) {
            final ComplexDataType cdt = (ComplexDataType) dataDescType;

            WPSIO.FormatSupport support = null;
            Class valueClass = null;
            for(Format format : cdt.getFormat()){
                final String mime     = format.getMimeType();
                final String encoding = format.getEncoding();
                final String schema   = format.getSchema();
                final Class clazz = WPSIO.findClass(WPSIO.IOType.INPUT, WPSIO.FormChoice.COMPLEX, mime, encoding, schema, null);

                if (clazz!=null) {
                    valueClass = clazz;
                    support = new WPSIO.FormatSupport(clazz, WPSIO.IOType.INPUT, mime, encoding, schema, false);
                    if (Boolean.TRUE.equals(format.isDefault())) {
                        //default type found, don't check any other
                        break;
                    }
                }
            }

            if (valueClass == null) {
                throw new UnsupportedParameterException(processId,inputName,"No compatible format found for parameter "+inputName);
            }

            final Map<String,Object> userMap = new HashMap<>();
            userMap.put(USE_FORMAT_KEY, support);
            userMap.put(USE_FORM_KEY, "complex");
            return new ExtendedParameterDescriptor(properties, valueClass,
                    null, null, null, null, null, required, userMap);

        } else if (dataDescType instanceof BoundingBoxData) {
            final BoundingBoxData cdt = (BoundingBoxData) dataDescType;
            final Map<String,Object> userMap = new HashMap<>();
            userMap.put(USE_FORM_KEY, "bbox");
            return new ExtendedParameterDescriptor(properties, Envelope.class,
                    null, null, null, null, null, required,userMap);

        } else if (!subInputs.isEmpty()) {
            //sub group type

            final List<GeneralParameterDescriptor> params = new ArrayList<>();
            for (DescriptionType dt : subInputs) {
                params.add(toDescriptor(processId,dt));
            }

            return new ParameterBuilder()
                    .addName(inputName)
                    .setRemarks(inputAbstract)
                    .createGroup(params.toArray(new GeneralParameterDescriptor[0]));

        } else {
            throw new UnsupportedParameterException(processId,inputName,"Unidentifiable input "+inputName);
        }
    }


    private static Unit getUnit(DomainMetadataType type) {
        if(type==null || type.getValue()==null) return null;
        return Units.valueOf(type.getValue());
    }

    private static Class getValueClass(DomainMetadataType type) {
        if(type==null) return null;
        Class clazz = findClass(type.getReference());
        if(clazz==null) clazz = findClass(type.getValue());
        if(clazz==null) clazz = String.class;
        return clazz;
    }

    private static Class findClass(String value) {
        if(value==null) return null;
        Class clazz = null;
        try {
            clazz = Class.forName(value);
        } catch (ClassNotFoundException ex) {
            value = value.toLowerCase();
            if (value.contains("double")) {
                clazz = Double.class;
            } else if (value.contains("boolean")) {
                clazz = Boolean.class;
            } else if (value.contains("float")) {
                clazz = Float.class;
            } else if (value.contains("short")) {
                clazz = Short.class;
            } else if (value.contains("integer")) {
                clazz = Integer.class;
            } else if (value.contains("long")) {
                clazz = Long.class;
            }
        }
        return clazz;
    }

}
