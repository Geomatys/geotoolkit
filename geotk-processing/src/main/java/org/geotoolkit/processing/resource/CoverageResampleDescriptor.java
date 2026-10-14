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

import org.apache.sis.coverage.grid.GridGeometry;
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

/**
 * Process calling {@link ResourceProcessor#resample(GridCoverageResource, GridGeometry)}.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageResampleDescriptor extends AbstractProcessDescriptor {

    /** Process name : resample */
    public static final String NAME = "resource:resample";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<GridCoverageResource> SOURCE = new ParameterBuilder()
            .addName("source")
            .setRemarks("Coverage resource to resample")
            .setRequired(true)
            .create(GridCoverageResource.class, null);
    public static final ParameterDescriptor<GridGeometry> TARGET = new ParameterBuilder()
            .addName("target")
            .setRemarks("Grid geometry of the resampled resource")
            .setRequired(true)
            .create(GridGeometry.class, null);
    public static final ParameterDescriptor<String> IDENTIFIER = ResourceProcessing.IDENTIFIER;
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(SOURCE, TARGET, IDENTIFIER);

    /**
     * Output parameters
     */
    public static final ParameterDescriptor<GridCoverageResource> RESULT = new ParameterBuilder()
            .addName(ResourceProcessing.RESULT_NAME)
            .setRemarks("Resampled coverage resource")
            .setRequired(true)
            .create(GridCoverageResource.class, null);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new CoverageResampleDescriptor();

    private CoverageResampleDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Wraps a coverage resource with a resample operator, to ensure it fits a provided grid geometry. "
                        + "Resampling is performed when the resource is read."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ChildProcess(input);
    }

    private static class ChildProcess extends AbstractProcess {

        public ChildProcess(final ParameterValueGroup input) {
            super(CoverageResampleDescriptor.INSTANCE, input);
        }

        @Override
        protected void execute() throws ProcessException {
            final GridCoverageResource source = inputParameters.getValue(SOURCE);
            final GridGeometry target = inputParameters.getValue(TARGET);

            final GridCoverageResource result = ResourceProcessing.createProcessor(inputParameters).resample(source, target);
            outputParameters.getOrCreate(RESULT).setValue(result);
        }
    }
}
