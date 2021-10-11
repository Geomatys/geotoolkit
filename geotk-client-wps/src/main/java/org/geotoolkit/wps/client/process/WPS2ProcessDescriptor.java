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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.measure.Unit;
import javax.xml.bind.JAXBException;
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
import org.geotoolkit.wps.xml.v200.BoundingBoxData;
import org.geotoolkit.wps.xml.v200.ComplexData;
import org.geotoolkit.wps.xml.v200.DataDescription;
import org.geotoolkit.wps.xml.v200.Description;
import org.geotoolkit.wps.xml.v200.Format;
import org.geotoolkit.wps.xml.v200.InputDescription;
import org.geotoolkit.wps.xml.v200.LiteralData;
import org.geotoolkit.wps.xml.v200.LiteralDataDomain;
import org.geotoolkit.wps.xml.v200.OutputDescription;
import org.geotoolkit.wps.xml.v200.ProcessDescription;
import org.geotoolkit.wps.xml.v200.ProcessOffering;
import org.geotoolkit.wps.xml.v200.ProcessOfferings;
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
class WPS2ProcessDescriptor extends AbstractProcessDescriptor {

    private final WPSProcessingRegistry registry;
    private final ProcessOffering offering;

    /**
     * Do not make this constructor public. We NEED the original process offering
     * for control purpose (accepted modes, response types, etc.).
     * @param offering The offering served by the WPS to use for execution.
     * @param name Identifier to use for this process.
     * @param registry The parent processing registry to use for execution.
     * @param abs Description of the process
     * @param displayName Title of the process.
     * @param inputs Process input, converted from original offering.
     * @param outputs Process output, converted from original offering.
     */
    private WPS2ProcessDescriptor(
            ProcessOffering offering,
            String name,
            WPSProcessingRegistry registry,
            InternationalString abs,
            InternationalString displayName,
            ParameterDescriptorGroup inputs,
            ParameterDescriptorGroup outputs
    ) {
        super(name, registry.getIdentification(), abs, displayName, inputs, outputs);
        this.offering = offering;
        this.registry = registry;
    }

    public ProcessOffering getOffering() {
        return offering;
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new WPS2Process(registry, this, input);
    }

    public static ProcessDescriptor create(WPSProcessingRegistry registry, final String processIdentifier) throws Exception {
        final ProcessOfferings offerings = registry.getClient().getDescribeProcess(Collections.singletonList(processIdentifier));
        final List<ProcessOffering> offeringList = offerings.getProcessOffering();
        if (offeringList == null || offeringList.isEmpty()) {
            throw new RuntimeException("No process description has been found for identifier "+processIdentifier);
        } else if (offeringList.size() != 1) {
            throw new RuntimeException(String.format(
                    "We asked for exactly one process descriptor (%s), but we've got %d results.",
                    processIdentifier, offeringList.size()
            ));
        }

        final ProcessOffering offering = offeringList.get(0);

        return create(registry, offering);
    }

    public static ProcessDescriptor create(WPSProcessingRegistry registry, ProcessOffering offering) throws IOException, JAXBException, UnsupportedParameterException {
        final ProcessDescription process = offering.getProcess();
        final String processIdentifier = process.getIdentifier().getValue();

        final InternationalString abs;
        if (process.getFirstAbstract()!= null) {
            abs = new DefaultInternationalString(process.getFirstAbstract());
        } else {
            abs = new DefaultInternationalString("");
        }

        final InternationalString displayName;
        if (process.getFirstTitle()!= null) {
            displayName = new DefaultInternationalString(process.getFirstTitle());
        } else {
            displayName = new DefaultInternationalString("");
        }

        final List<GeneralParameterDescriptor> inputLst = new ArrayList<>();
        final List<GeneralParameterDescriptor> outputLst = new ArrayList<>();
        for (final InputDescription input : process.getInputs()) {
            inputLst.add(toDescriptor(processIdentifier,input));
        }

        for (final OutputDescription outputDesc : process.getOutputs()) {
            outputLst.add(toDescriptor(processIdentifier,outputDesc));
        }

        final ParameterDescriptorGroup inputs = new ParameterBuilder().addName("inputs").createGroup(
                inputLst.toArray(new GeneralParameterDescriptor[inputLst.size()]));
        final ParameterDescriptorGroup outputs = new ParameterBuilder().addName("ouptuts").createGroup(
                outputLst.toArray(new GeneralParameterDescriptor[outputLst.size()]));

        return new WPS2ProcessDescriptor(offering, processIdentifier, registry, abs, displayName, inputs, outputs);
    }

    /**
     * Convert Description to GeneralParameterDescriptor.
     *
     * @throws UnsupportedOperationException if data type could not be mapped
     */
    private static GeneralParameterDescriptor toDescriptor(String processId, Description input) throws UnsupportedParameterException {
        final List<? extends Description> subInputs;
        final DataDescription dataDescType;
        final int min;
        final int max;
        if (input instanceof InputDescription) {
            final InputDescription id = (InputDescription) input;
            subInputs = id.getInput();
            dataDescType = id.getDataDescription();
            max = id.getMaxOccurs();
            min = id.getMinOccurs();
        } else if(input instanceof OutputDescription) {
            final OutputDescription od = (OutputDescription) input;
            subInputs = od.getOutput();
            dataDescType = od.getDataDescription();
            min = 1;
            max = 1;
        } else {
            throw new IllegalArgumentException("Unexpected description type "+input.getClass());
        }

        final String inputName = input.getIdentifier().getValue();
        final String title = input.getFirstTitle();
        final String remarks = input.getFirstAbstract();

        Map userObject = new HashMap();
        for (MetadataType meta : input.getMetadata()) {
            if (meta instanceof AdditionalParametersType) {
                AdditionalParametersType params = (AdditionalParametersType)meta;
                for (AdditionalParameter param : params.getAdditionalParameter()) {
                    userObject.put(param.getName().getValue(), param.getValue());
                }
            }
        }

        if (dataDescType instanceof LiteralData) {
            final LiteralData cd = (LiteralData) dataDescType;

            for(LiteralDataDomain domain : cd.getLiteralDataDomain()) {

                final LiteralAdaptor adaptor = LiteralAdaptor.create(domain);
                if (adaptor==null) continue;

                String defaultValueValue = null;
                final ValueType defaultValue = domain.getDefaultValue();
                if (defaultValue!=null) defaultValueValue = defaultValue.getValue();
                final Unit unit = getUnit(domain.getUOM());
                Object[] allowedValues = null;
                if (domain.getAllowedValues() != null && domain.getAllowedValues().getStringValues() != null) {
                    allowedValues = new Object[domain.getAllowedValues().getStringValues().size()];
                    int i = 0;
                    for (String value : domain.getAllowedValues().getStringValues()) {
                        allowedValues[i] = adaptor.convert(value);
                        i++;
                    }
                }
                try {
                    userObject.put(DataAdaptor.USE_ADAPTOR, adaptor);
                    return new ExtendedParameterDescriptor(inputName, title, remarks, min, max, adaptor.getValueClass(), adaptor.convert(defaultValueValue), allowedValues, userObject);
                } catch (UnconvertibleObjectException ex2) {
                    throw new UnsupportedParameterException(processId, inputName, "Can't convert the default literal input value.", ex2);
                }
            }

            throw new UnsupportedParameterException(processId,inputName,"Unidentifiable literal input "+inputName);

        } else if (dataDescType instanceof ComplexData) {
            final ComplexData cdt = (ComplexData) dataDescType;

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
            return new ExtendedParameterDescriptor(inputName, title, remarks, min, max, adaptor.getValueClass(), null, null, userObject);

        } else if (dataDescType instanceof BoundingBoxData) {

            final BboxAdaptor adaptor = BboxAdaptor.create((BoundingBoxData) dataDescType);
            userObject.put(DataAdaptor.USE_ADAPTOR, adaptor);
            return new ExtendedParameterDescriptor(inputName, title, remarks, min, max, Envelope.class, null, null, userObject);

        } else if (!subInputs.isEmpty()) {
            //sub group type

            final List<GeneralParameterDescriptor> params = new ArrayList<>();
            for (Description dt : subInputs) {
                params.add(toDescriptor(processId,dt));
            }

            return new ParameterBuilder()
                    .addName(inputName)
                    .addName(title)
                    .setRemarks(remarks)
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
