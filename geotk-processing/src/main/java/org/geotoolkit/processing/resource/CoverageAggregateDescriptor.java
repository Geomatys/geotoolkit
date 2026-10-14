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
import org.apache.sis.storage.DataStoreException;
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
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Process calling {@link ResourceProcessor#aggregateDomain2D(GridCoverageResource[], CoordinateReferenceSystem, boolean)}.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageAggregateDescriptor extends AbstractProcessDescriptor{

    /**Process name : aggregate-domain */
    public static final String NAME = "resource:aggregate-domain";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<GridCoverageResource[]> SOURCES = new ParameterBuilder()
            .addName("sources")
            .setRemarks("Coverage resources to merge")
            .setRequired(true)
            .create(GridCoverageResource[].class, null);
    public static final ParameterDescriptor<CoordinateReferenceSystem> CRS = new ParameterBuilder()
            .addName("crs")
            .setRemarks("The produced resource CRS, if undefined the best match is used")
            .setRequired(false)
            .create(CoordinateReferenceSystem.class, null);
    public static final ParameterDescriptor<Boolean> BY_SCALE = new ParameterBuilder()
            .addName("byScale")
            .setRemarks("True to aggregate resources by most appropriate scale first, otherwise use the sources order")
            .setRequired(true)
            .create(Boolean.class, Boolean.FALSE);
    public static final ParameterDescriptor<String> IDENTIFIER = ResourceProcessing.IDENTIFIER;
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(SOURCES, CRS, BY_SCALE, IDENTIFIER);

    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<GridCoverageResource> RESULT = new ParameterBuilder()
            .addName(ResourceProcessing.RESULT_NAME)
            .setRemarks("Aggregated coverage resource")
            .setRequired(true)
            .create(GridCoverageResource.class, null);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new CoverageAggregateDescriptor();

    private CoverageAggregateDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Aggregate grid coverage resources as a single coverage resource on the same horizontal CRS. "
                        + "Coverage gaps are progressively filled by the next resource until there are no more resources or no more gaps."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ChildProcess(input);
    }

    private static class ChildProcess extends AbstractProcess {

        public ChildProcess(final ParameterValueGroup input) {
            super(CoverageAggregateDescriptor.INSTANCE,input);
        }

        @Override
        protected void execute() throws ProcessException {
            final GridCoverageResource[] sources = inputParameters.getValue(SOURCES);
            final CoordinateReferenceSystem crs = inputParameters.getValue(CRS);
            final boolean byScale = inputParameters.getValue(BY_SCALE);

            final GridCoverageResource result;
            try {
                result = ResourceProcessing.createProcessor(inputParameters).aggregateDomain2D(sources, crs, byScale);
            } catch (DataStoreException | TransformException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }
            outputParameters.getOrCreate(RESULT).setValue(result);
        }
    }
}
