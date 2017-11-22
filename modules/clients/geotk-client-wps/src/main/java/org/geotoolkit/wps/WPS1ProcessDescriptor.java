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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.DefaultInternationalString;
import org.geotoolkit.ows.xml.v110.AllowedValues;
import org.geotoolkit.ows.xml.v110.AnyValue;
import org.geotoolkit.ows.xml.v110.DomainMetadataType;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;
import org.geotoolkit.wps.adaptor.BboxAdaptor;
import org.geotoolkit.wps.adaptor.ComplexAdaptor;
import org.geotoolkit.wps.adaptor.DataAdaptor;
import org.geotoolkit.wps.adaptor.LiteralAdaptor;
import org.geotoolkit.wps.xml.DataDescription;
import org.geotoolkit.wps.xml.Format;
import org.geotoolkit.wps.xml.ProcessOfferings;
import org.geotoolkit.wps.xml.v100.ComplexDataCombinationType;
import org.geotoolkit.wps.xml.v100.DescriptionType;
import org.geotoolkit.wps.xml.v100.InputDescriptionType;
import org.geotoolkit.wps.xml.v100.LiteralInputType;
import org.geotoolkit.wps.xml.v100.LiteralOutputType;
import org.geotoolkit.wps.xml.v100.OutputDescriptionType;
import org.geotoolkit.wps.xml.v100.ProcessDescriptionType;
import org.geotoolkit.wps.xml.v100.SupportedCRSsType;
import org.geotoolkit.wps.xml.v100.SupportedComplexDataInputType;
import org.geotoolkit.wps.xml.v100.ValuesReferenceType;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;
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
            InternationalString displayName,
            ParameterDescriptorGroup inputs,
            ParameterDescriptorGroup outputs) {
        super(processIdentifier, registry.getIdentification(), processAbstract, displayName, inputs,outputs);
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


    public static WPS1ProcessDescriptor create(WPSProcessingRegistry registry, final String processIdentifier)
            throws Exception {

        final ProcessOfferings wpsProcessDescriptions = registry.getClient().getDescribeProcess(Collections.singletonList(processIdentifier));
        final ProcessDescriptionType wpsProcessDesc = (ProcessDescriptionType) wpsProcessDescriptions.getProcesses().get(0);

        final InternationalString processAbstract;
        if (wpsProcessDesc.getFirstAbstract() != null) {
            processAbstract = new DefaultInternationalString(wpsProcessDesc.getFirstAbstract());
        } else {
            processAbstract = new DefaultInternationalString("");
        }

        final InternationalString processDisplayName;
        if (wpsProcessDesc.getFirstTitle() != null) {
            processDisplayName = new DefaultInternationalString(wpsProcessDesc.getFirstTitle());
        } else {
            processDisplayName = new DefaultInternationalString("");
        }

        // INPUTS
        final List<GeneralParameterDescriptor> inputDescriptors = new ArrayList<>();
        if (wpsProcessDesc.getDataInputs() != null) {
            for (final InputDescriptionType inputDesc : wpsProcessDesc.getDataInputs().getInput()) {
                inputDescriptors.add(toDescriptor(processIdentifier, inputDesc));
            }
        }

        //OUTPUTS
        final List<GeneralParameterDescriptor> outputDescriptors = new ArrayList<>();
        if (wpsProcessDesc.getProcessOutputs() != null) {
            for (final OutputDescriptionType outputDesc : wpsProcessDesc.getProcessOutputs().getOutput()) {
                outputDescriptors.add(toDescriptor(processIdentifier, outputDesc));
            }
        }

        final ParameterDescriptorGroup inputs = new ParameterBuilder().addName("inputs").createGroup(
                inputDescriptors.toArray(new ParameterDescriptor[inputDescriptors.size()]));
        final ParameterDescriptorGroup outputs = new ParameterBuilder().addName("ouptuts").createGroup(
                outputDescriptors.toArray(new ParameterDescriptor[outputDescriptors.size()]));

        return new WPS1ProcessDescriptor(registry, wpsProcessDesc, processIdentifier, processAbstract, processDisplayName, inputs, outputs);
    }

    /**
     * Convert DescriptionType to GeneralParameterDescriptor.
     *
     * @param input
     * @return
     * @throws UnsupportedOperationException if data type could not be mapped
     */
    private static GeneralParameterDescriptor toDescriptor(String processId, DescriptionType input) throws UnsupportedParameterException{

        final String inputName = input.getIdentifier().getValue();
        final String inputAbstract = input.getFirstAbstract();
        final String inputTitle = input.getFirstTitle();
        final boolean required;

        final Map<String, String> properties = new HashMap<>();
        properties.put("name", inputName);
        if (inputAbstract!=null && !inputAbstract.isEmpty()) {
            properties.put("remarks", inputAbstract);
        }
        if (inputTitle != null) {
            properties.put(IdentifiedObject.ALIAS_KEY, inputTitle);
        }

        DataDescription dataDesc;
        if (input instanceof InputDescriptionType) {
            final InputDescriptionType id = (InputDescriptionType) input;
            dataDesc = id.getBoundingBoxData();
            if (dataDesc==null) dataDesc = id.getComplexData();
            if (dataDesc==null) dataDesc = id.getLiteralData();
            final Integer max = id.getMaxOccurs();
            final Integer min = id.getMinOccurs();
            required = min!=0;
        } else if(input instanceof OutputDescriptionType) {
            final OutputDescriptionType od = (OutputDescriptionType) input;
            dataDesc = od.getBoundingBoxOutput();
            if (dataDesc==null) dataDesc = od.getComplexOutput();
            if (dataDesc==null) dataDesc = od.getLiteralOutput();
            required = true;
        } else {
            throw new IllegalArgumentException("Unexpected description type "+input.getClass());
        }

        if (dataDesc instanceof LiteralOutputType) {
            final LiteralOutputType cd = (LiteralOutputType) dataDesc;
            final DomainMetadataType dataType = cd.getDataType();
            final LiteralAdaptor adaptor = LiteralAdaptor.create(cd);
            Object defaultValue = null;

            if (cd instanceof LiteralInputType) {
                final LiteralInputType li = (LiteralInputType) cd;
                final AllowedValues allowedValues = li.getAllowedValues();
                final AnyValue anyValue = li.getAnyValue();
                final String defaultValueStr = li.getDefaultValue();
                final ValuesReferenceType valuesReference = li.getValuesReference();
                if (defaultValueStr!=null) {
                    defaultValue = adaptor.convert(defaultValueStr);
                }
            } else {
                final String reference = dataType.getReference();
                final String value = dataType.getValue();
            }

            return new ExtendedParameterDescriptor(properties,
                    adaptor.getValueClass(),
                    null, defaultValue, null, null, adaptor.getUnit(), required,
                    Collections.singletonMap(DataAdaptor.USE_ADAPTOR, adaptor));

        } else if (dataDesc instanceof SupportedCRSsType) {
            final BboxAdaptor adaptor = BboxAdaptor.create((SupportedCRSsType) dataDesc);
            return new ExtendedParameterDescriptor(properties, Envelope.class,
                    null, null, null, null, null, required,Collections.singletonMap(DataAdaptor.USE_ADAPTOR, adaptor));

        } else if (dataDesc instanceof SupportedComplexDataInputType) {

            final SupportedComplexDataInputType scdt = (SupportedComplexDataInputType) dataDesc;
            final ComplexDataCombinationType complexDefault = scdt.getDefault();

            final List<Format> formats = new ArrayList<>();
            if (complexDefault != null && complexDefault.getFormat() != null) {
                formats.add(complexDefault.getFormat());
            }
            if (scdt.getSupported()!=null) {
                formats.addAll(scdt.getSupported().getFormat());
            }

            //find a complexe type adaptor
            DataAdaptor adaptor = null;
            for(Format format : formats){
                adaptor = ComplexAdaptor.getAdaptor(format);
                if (adaptor!=null) break;
            }
            if (adaptor == null) {
                final StringBuilder sb = new StringBuilder();
                for (Format format : formats) {
                    if(sb.length()!=0) sb.append(", ");
                    sb.append(format.getMimeType()).append(' ');
                    sb.append(format.getEncoding()).append(' ');
                    sb.append(format.getSchema());
                }
                throw new UnsupportedParameterException(processId,inputName,"No compatible format found for parameter "+inputName+" formats : "+sb);
            }

            return new ExtendedParameterDescriptor(properties, adaptor.getValueClass(),
                    null, null, null, null, null, required, Collections.singletonMap(DataAdaptor.USE_ADAPTOR, adaptor));

        } else {
            throw new IllegalArgumentException("Unexpected data type "+dataDesc.getClass());
        }

    }

}
