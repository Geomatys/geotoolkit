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
import org.opengis.referencing.crs.SingleCRS;

/**
 * Process calling {@link ResourceProcessor#appendDimension(GridCoverageResource, double, double, SingleCRS)}.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageAppendDimensionDescriptor extends AbstractProcessDescriptor {

    /** Process name : append-dimension */
    public static final String NAME = "resource:append-dimension";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<GridCoverageResource> SOURCE = new ParameterBuilder()
            .addName("source")
            .setRemarks("Coverage resource on which to append a dimension")
            .setRequired(true)
            .create(GridCoverageResource.class, null);
    public static final ParameterDescriptor<Double> LOWER = new ParameterBuilder()
            .addName("lower")
            .setRemarks("Lower coordinate value of the slice, in units of the CRS")
            .setRequired(true)
            .create(Double.class, null);
    public static final ParameterDescriptor<Double> SPAN = new ParameterBuilder()
            .addName("span")
            .setRemarks("Size of the slice, in units of the CRS")
            .setRequired(true)
            .create(Double.class, null);
    public static final ParameterDescriptor<SingleCRS> CRS = new ParameterBuilder()
            .addName("crs")
            .setRemarks("One-dimensional coordinate reference system of the slice")
            .setRequired(true)
            .create(SingleCRS.class, null);
    public static final ParameterDescriptor<String> IDENTIFIER = ResourceProcessing.IDENTIFIER;
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(SOURCE, LOWER, SPAN, CRS, IDENTIFIER);

    /**
     * Output parameters
     */
    public static final ParameterDescriptor<GridCoverageResource> RESULT = new ParameterBuilder()
            .addName(ResourceProcessing.RESULT_NAME)
            .setRemarks("Coverage resource with the dimension added")
            .setRequired(true)
            .create(GridCoverageResource.class, null);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new CoverageAppendDimensionDescriptor();

    private CoverageAppendDimensionDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Appends a single grid dimension after the dimensions of the source coverage resource. "
                        + "Typically used for adding a vertical axis to a two-dimensional coverage."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ChildProcess(input);
    }

    private static class ChildProcess extends AbstractProcess {

        public ChildProcess(final ParameterValueGroup input) {
            super(CoverageAppendDimensionDescriptor.INSTANCE, input);
        }

        @Override
        protected void execute() throws ProcessException {
            final GridCoverageResource source = inputParameters.getValue(SOURCE);
            final double lower = inputParameters.getValue(LOWER);
            final double span = inputParameters.getValue(SPAN);
            final SingleCRS crs = inputParameters.getValue(CRS);

            final GridCoverageResource result = ResourceProcessing.createProcessor(inputParameters).appendDimension(source, lower, span, crs);
            outputParameters.getOrCreate(RESULT).setValue(result);
        }
    }
}
