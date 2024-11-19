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
package org.geotoolkit.processing.coverage.math.substract;

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
public class CoverageSubstractDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "coverage:math:substract";
    public static final InternationalString ABSTRACT = new SimpleInternationalString("Substract single specified band of each coverage in a result coverage");

    public static final String IN_FIRST_COVERAGE_NAME = "first";
    private static final String IN_FIRST_COVERAGE_REMARKS = "First Coverage (A in A-B)";

    /**
     * Mandatory - First Coverage.
     */
    public static final ParameterDescriptor<GridCoverage> IN_FIRST_COVERAGE = new ParameterBuilder()
            .addName(IN_FIRST_COVERAGE_NAME)
            .setRemarks(IN_FIRST_COVERAGE_REMARKS)
            .setRequired(true)
            .create(GridCoverage.class,null);

    public static final String IN_SECOND_COVERAGE_NAME = "second";
    private static final String IN_SECOND_COVERAGE_REMARKS = "Second Coverage (B in A-B)";

    /**
     * Mandatory - Second Coverage.
     */
    public static final ParameterDescriptor<GridCoverage> IN_SECOND_COVERAGE = new ParameterBuilder()
            .addName(IN_SECOND_COVERAGE_NAME)
            .setRemarks(IN_SECOND_COVERAGE_REMARKS)
            .setRequired(true)
            .create(GridCoverage.class,null);


    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder().addName("InputParameters").setRequired(true)
            .createGroup(IN_FIRST_COVERAGE, IN_SECOND_COVERAGE);

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


    public static final ProcessDescriptor INSTANCE = new CoverageSubstractDescriptor();

    public CoverageSubstractDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION, ABSTRACT,
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new CoverageSubstractProcess(this, input);
    }
}
