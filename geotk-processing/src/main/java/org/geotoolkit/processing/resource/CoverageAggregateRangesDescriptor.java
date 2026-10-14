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
 * Process calling {@link ResourceProcessor#aggregateRanges(GridCoverageResource[], CoordinateReferenceSystem, int[][])}.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageAggregateRangesDescriptor extends AbstractProcessDescriptor {

    /** Process name : aggregate-ranges */
    public static final String NAME = "resource:aggregate-ranges";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<GridCoverageResource[]> SOURCES = new ParameterBuilder()
            .addName("sources")
            .setRemarks("Coverage resources whose bands shall be aggregated, in order")
            .setRequired(true)
            .create(GridCoverageResource[].class, null);
    public static final ParameterDescriptor<CoordinateReferenceSystem> CRS = new ParameterBuilder()
            .addName("crs")
            .setRemarks("The produced resource CRS, if undefined the best match is used")
            .setRequired(false)
            .create(CoordinateReferenceSystem.class, null);
    public static final ParameterDescriptor<int[][]> BANDS_PER_SOURCE = new ParameterBuilder()
            .addName("bandsPerSource")
            .setRemarks("Bands to use for each source coverage, in order. If undefined or null elements, all bands are used")
            .setRequired(false)
            .create(int[][].class, null);
    public static final ParameterDescriptor<String> IDENTIFIER = ResourceProcessing.IDENTIFIER;
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(SOURCES, CRS, BANDS_PER_SOURCE, IDENTIFIER);

    /**
     * Output parameters
     */
    public static final ParameterDescriptor<GridCoverageResource> RESULT = new ParameterBuilder()
            .addName(ResourceProcessing.RESULT_NAME)
            .setRemarks("Aggregated coverage resource")
            .setRequired(true)
            .create(GridCoverageResource.class, null);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new CoverageAggregateRangesDescriptor();

    private CoverageAggregateRangesDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Aggregates in a single coverage resource the specified bands of a sequence of source coverage resources, in order."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ChildProcess(input);
    }

    private static class ChildProcess extends AbstractProcess {

        public ChildProcess(final ParameterValueGroup input) {
            super(CoverageAggregateRangesDescriptor.INSTANCE, input);
        }

        @Override
        protected void execute() throws ProcessException {
            final GridCoverageResource[] sources = inputParameters.getValue(SOURCES);
            final CoordinateReferenceSystem crs = inputParameters.getValue(CRS);
            final int[][] bandsPerSource = inputParameters.getValue(BANDS_PER_SOURCE);

            final GridCoverageResource result;
            try {
                result = ResourceProcessing.createProcessor(inputParameters).aggregateRanges(sources, crs, bandsPerSource);
            } catch (DataStoreException | TransformException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }
            outputParameters.getOrCreate(RESULT).setValue(result);
        }
    }
}
