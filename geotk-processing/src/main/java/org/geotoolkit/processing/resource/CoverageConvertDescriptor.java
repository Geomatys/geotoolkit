/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.processing.resource;

import java.util.function.Function;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.storage.ResourceProcessor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform1D;

/**
 * Process calling {@link ResourceProcessor#convert(GridCoverageResource, MathTransform1D[], Function)}.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageConvertDescriptor extends AbstractProcessDescriptor {

    /** Process name : convert */
    public static final String NAME = "resource:convert";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<GridCoverageResource> SOURCE = new ParameterBuilder()
            .addName("source")
            .setRemarks("Coverage resource to convert")
            .setRequired(true)
            .create(GridCoverageResource.class, null);
    public static final ParameterDescriptor<MathTransform1D[]> CONVERTERS = new ParameterBuilder()
            .addName("converters")
            .setRemarks("Transfer functions to apply on each sample dimension of the source coverage")
            .setRequired(true)
            .create(MathTransform1D[].class, null);
    @SuppressWarnings("rawtypes")
    public static final ParameterDescriptor<Function> SAMPLE_DIMENSION_MODIFIER = new ParameterBuilder()
            .addName("sampleDimensionModifier")
            .setRemarks("Function of type Function<SampleDimension.Builder, SampleDimension> invoked for "
                    + "customizing the sample dimensions of the converted coverage")
            .setRequired(false)
            .create(Function.class, null);
    public static final ParameterDescriptor<String> IDENTIFIER = ResourceProcessing.IDENTIFIER;
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(SOURCE, CONVERTERS, SAMPLE_DIMENSION_MODIFIER, IDENTIFIER);

    /**
     * Output parameters
     */
    public static final ParameterDescriptor<GridCoverageResource> RESULT = new ParameterBuilder()
            .addName(ResourceProcessing.RESULT_NAME)
            .setRemarks("Converted coverage resource")
            .setRequired(true)
            .create(GridCoverageResource.class, null);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new CoverageConvertDescriptor();

    private CoverageConvertDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Returns a coverage resource with sample values converted by the given functions."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ChildProcess(input);
    }

    private static class ChildProcess extends AbstractProcess {

        public ChildProcess(final ParameterValueGroup input) {
            super(CoverageConvertDescriptor.INSTANCE, input);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void execute() throws ProcessException {
            final GridCoverageResource source = inputParameters.getValue(SOURCE);
            final MathTransform1D[] converters = inputParameters.getValue(CONVERTERS);
            final Function<SampleDimension.Builder, SampleDimension> modifier = inputParameters.getValue(SAMPLE_DIMENSION_MODIFIER);

            final GridCoverageResource result = ResourceProcessing.createProcessor(inputParameters).convert(source, converters, modifier);
            outputParameters.getOrCreate(RESULT).setValue(result);
        }
    }
}
