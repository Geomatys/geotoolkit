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
import javax.xml.bind.JAXBElement;
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
import org.geotoolkit.wps.adaptor.BboxAdaptor;
import org.geotoolkit.wps.adaptor.ComplexAdaptor;
import org.geotoolkit.wps.adaptor.LiteralAdaptor;
import org.geotoolkit.wps.adaptor.DataAdaptor;
import org.geotoolkit.ows.xml.v200.MetadataType;
import org.geotoolkit.ows.xml.v200.AdditionalParametersType;
import org.geotoolkit.ows.xml.v200.AdditionalParameter;
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
import org.w3c.dom.Node;

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
            InternationalString displayName, ParameterDescriptorGroup inputs, ParameterDescriptorGroup outputs) {
        super(name,registry.getIdentification(),abs,displayName,inputs,outputs);
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
        if (summary.getFirstAbstract()!= null) {
            abs = new DefaultInternationalString(summary.getFirstAbstract());
        } else {
            abs = new DefaultInternationalString("");
        }

        final InternationalString displayName;
        if (summary.getFirstTitle()!= null) {
            displayName = new DefaultInternationalString(summary.getFirstTitle());
        } else {
            displayName = new DefaultInternationalString("");
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

        return new WPS2ProcessDescriptor(processIdentifier, registry, abs, displayName, inputs, outputs);
    }

    public static ProcessDescriptor create(WPSProcessingRegistry registry, ProcessOffering offering) throws IOException, JAXBException, UnsupportedParameterException {
        final String processIdentifier = offering.getIdentifier().getValue();

        final InternationalString abs;
        if (offering.getFirstAbstract()!= null) {
            abs = new DefaultInternationalString(offering.getFirstAbstract());
        } else {
            abs = new DefaultInternationalString("");
        }

        final InternationalString displayName;
        if (offering.getFirstTitle()!= null) {
            displayName = new DefaultInternationalString(offering.getFirstTitle());
        } else {
            displayName = new DefaultInternationalString("");
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

        return new WPS2ProcessDescriptor(processIdentifier, registry, abs, displayName, inputs, outputs);
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
        final int min;
        final int max;
        if (input instanceof InputDescriptionType) {
            final InputDescriptionType id = (InputDescriptionType) input;
            subInputs = id.getInput();
            dataDescType = id.getDataDescription()==null?null : id.getDataDescription().getValue();
            final String maxValue = id.getMaxOccurs();
            if ("unbounded".equals(maxValue)) {
                max = Integer.MAX_VALUE;
            } else if (maxValue != null) {
                max = Integer.parseInt(maxValue);
            } else {
                max = 1;
            }
            min = id.getMinOccurs();
        } else if(input instanceof OutputDescriptionType) {
            final OutputDescriptionType od = (OutputDescriptionType) input;
            subInputs = od.getOutput();
            dataDescType = od.getDataDescription()==null?null : od.getDataDescription().getValue();
            min = 1;
            max = 1;
        } else {
            throw new IllegalArgumentException("Unexpected description type "+input.getClass());
        }

        final String inputName = input.getIdentifier().getValue();
        final String inputAbstract = input.getAbstract().isEmpty() ? null : input.getAbstract().get(0).getValue();

        String remarks = null;
        if(!input.getAbstract().isEmpty() && !input.getAbstract().get(0).getValue().isEmpty()) {
            remarks = input.getAbstract().get(0).getValue();
        }

        Map userObject = new HashMap();
        for (JAXBElement<? extends MetadataType> metaJB : input.getMetadata()) {
            MetadataType meta = metaJB.getValue();
            if (meta instanceof AdditionalParametersType) {
                AdditionalParametersType params = (AdditionalParametersType)meta;
                for (AdditionalParameter param : params.getAdditionalParameter()) {
                    userObject.put(param.getName().getValue(), param.getValue());
                }
            }
        }

        if (dataDescType instanceof LiteralDataType) {
            final LiteralDataType cd = (LiteralDataType) dataDescType;

            for(LiteralDataType.LiteralDataDomain domain : cd.getLiteralDataDomain()) {

                final LiteralAdaptor adaptor = LiteralAdaptor.create(domain);
                if (adaptor==null) continue;

                String defaultValueValue = null;
                final ValueType defaultValue = domain.getDefaultValue();
                if (defaultValue!=null) defaultValueValue = defaultValue.getValue();
                final Unit unit = getUnit(domain.getUOM());
                try {
                    userObject.put(DataAdaptor.USE_ADAPTOR, adaptor);
                    return new ExtendedParameterDescriptor(inputName, remarks, min, max, adaptor.getValueClass(), adaptor.convert(defaultValueValue), userObject);
                } catch (UnconvertibleObjectException ex2) {
                    throw new UnsupportedParameterException(processId, inputName, "Can't convert the default literal input value.", ex2);
                }
            }

            throw new UnsupportedParameterException(processId,inputName,"Unidentifiable literal input "+inputName);

        } else if (dataDescType instanceof ComplexDataType) {
            final ComplexDataType cdt = (ComplexDataType) dataDescType;

            //ensure default format is first in the list
            Collections.sort(cdt.getFormat(), (Format o1, Format o2) -> {
                boolean d1 = Boolean.TRUE.equals(o1.isDefault());
                boolean d2 = Boolean.TRUE.equals(o2.isDefault());
                if(d1==d2) return 0;
                return d1 ? -1 : +1;
            });

            //find a complexe type adaptor
            DataAdaptor adaptor = null;
            for(Format format : cdt.getFormat()){
                adaptor = ComplexAdaptor.getAdaptor(format);
                if (adaptor!=null) break;
            }
            if (adaptor == null) {
                final StringBuilder sb = new StringBuilder();
                for(Format format : cdt.getFormat()){
                    if(sb.length()!=0) sb.append(", ");
                    sb.append(format.getMimeType()).append(' ');
                    sb.append(format.getEncoding()).append(' ');
                    sb.append(format.getSchema());
                }
                throw new UnsupportedParameterException(processId,inputName,"No compatible format found for parameter "+inputName+" formats : "+sb);
            }
            userObject.put(DataAdaptor.USE_ADAPTOR, adaptor);
            return new ExtendedParameterDescriptor(inputName, remarks, min, max, adaptor.getValueClass(), null, userObject);

        } else if (dataDescType instanceof BoundingBoxData) {

            final BboxAdaptor adaptor = BboxAdaptor.create((BoundingBoxData) dataDescType);
            userObject.put(DataAdaptor.USE_ADAPTOR, adaptor);
            return new ExtendedParameterDescriptor(inputName, remarks, min, max, Envelope.class, null, userObject);

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
            throw new UnsupportedParameterException(processId, inputName, "Unidentifiable input " + inputName + " " + dataDescType);
        }
    }


    private static Unit getUnit(DomainMetadataType type) {
        if(type==null || type.getValue()==null) return null;
        return Units.valueOf(type.getValue());
    }

}
