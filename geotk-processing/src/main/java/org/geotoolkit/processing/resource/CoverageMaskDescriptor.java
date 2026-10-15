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

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.FeatureSet;
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

/**
 * Process calling {@link ResourceProcessor#mask(GridCoverageResource, FeatureSet, boolean)}.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageMaskDescriptor extends AbstractProcessDescriptor {

    /** Process name : mask */
    public static final String NAME = "resource:mask";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<GridCoverageResource> SOURCE = new ParameterBuilder()
            .addName("source")
            .setRemarks("Coverage resource on which to apply a mask")
            .setRequired(true)
            .create(GridCoverageResource.class, null);
    public static final ParameterDescriptor<FeatureSet> MASK = new ParameterBuilder()
            .addName("mask")
            .setRemarks("Feature set with masking features")
            .setRequired(true)
            .create(FeatureSet.class, null);
    public static final ParameterDescriptor<Boolean> MASK_INSIDE = new ParameterBuilder()
            .addName("maskInside")
            .setRemarks("True for masking pixels inside the shapes, false for masking outside")
            .setRequired(true)
            .create(Boolean.class, null);
    public static final ParameterDescriptor<String> IDENTIFIER = ResourceProcessing.IDENTIFIER;
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(SOURCE, MASK, MASK_INSIDE, IDENTIFIER);

    /**
     * Output parameters
     */
    public static final ParameterDescriptor<GridCoverageResource> RESULT = new ParameterBuilder()
            .addName(ResourceProcessing.RESULT_NAME)
            .setRemarks("Masked coverage resource")
            .setRequired(true)
            .create(GridCoverageResource.class, null);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new CoverageMaskDescriptor();

    private CoverageMaskDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Apply a mask defined by a feature set on a coverage resource."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ChildProcess(input);
    }

    private static class ChildProcess extends AbstractProcess {

        public ChildProcess(final ParameterValueGroup input) {
            super(CoverageMaskDescriptor.INSTANCE, input);
        }

        @Override
        protected void execute() throws ProcessException {
            final GridCoverageResource source = inputParameters.getValue(SOURCE);
            final FeatureSet mask = inputParameters.getValue(MASK);
            final boolean maskInside = inputParameters.getValue(MASK_INSIDE);

            final GridCoverageResource result = ResourceProcessing.createProcessor(inputParameters).mask(source, mask, maskInside);
            outputParameters.getOrCreate(RESULT).setValue(result);
        }
    }
}
