/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.processing.coverage.math.sum;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * @author Quentin BIALOTA (Geomatys)
 */
public class CoverageSumWithValueDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "coverage:math:sumWithValue";
    public static final InternationalString ABSTRACT = new SimpleInternationalString("Sum a Coverage with a single value");

    public static final String IN_COVERAGE_NAME = "coverage";
    private static final String IN_COVERAGE_REMARKS = "Input coverage";

    /**
     * Mandatory - Coverage.
     */
    public static final ParameterDescriptor<GridCoverage> IN_COVERAGE = new ParameterBuilder()
            .addName("coverage")
            .addName(IN_COVERAGE_NAME)
            .setRemarks(IN_COVERAGE_REMARKS)
            .setRequired(true)
            .create(GridCoverage.class,null);

    public static final String IN_VALUE_NAME = "value";
    private static final String IN_VALUE_REMARKS = "Input value";

    /**
     * Mandatory - Value.
     */
    public static final ParameterDescriptor<Double> IN_VALUE = new ParameterBuilder()
            .addName("value")
            .addName(IN_VALUE_NAME)
            .setRemarks(IN_VALUE_REMARKS)
            .setRequired(true)
            .create(Double.class,null);

    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder().addName("InputParameters").setRequired(true)
            .createGroup(IN_COVERAGE, IN_VALUE);

    public static final String OUT_COVERAGE_NAME = "result";
    private static final String OUT_COVERAGE_REMARKS = "Result as a coverage";

    /**
     * Mandatory - Resulting coverage.
     */
    public static final ParameterDescriptor<GridCoverage> OUT_COVERAGE = new ParameterBuilder()
            .addName(OUT_COVERAGE_NAME)
            .setRemarks(OUT_COVERAGE_REMARKS)
            .setRequired(true)
            .create(GridCoverage.class,null);

    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder()
            .addName("OutputParameters").createGroup(OUT_COVERAGE);

    public static final ProcessDescriptor INSTANCE = new CoverageSumWithValueDescriptor();

    public CoverageSumWithValueDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION, ABSTRACT,
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new CoverageSumWithValueProcess(this, input);
    }
}
