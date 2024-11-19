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
package org.geotoolkit.processing.arrays;

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
public class CreateArrayGridCoverageValuesDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "arrays.createGridCoverageValues";
    public static final InternationalString ABSTRACT = new SimpleInternationalString("Create an Array of GridCoverage with maximum 10 different inputs");

    public static final String INPUT_FIRST_NAME = "first";
    private static final String INPUT_FIRST_REMARKS = "First value";
    public static final ParameterDescriptor<GridCoverage> INPUT_FIRST = new ParameterBuilder()
            .addName(INPUT_FIRST_NAME)
            .setRemarks(INPUT_FIRST_REMARKS)
            .setRequired(false)
            .create(GridCoverage.class, null);

    public static final String INPUT_SECOND_NAME = "second";
    private static final String INPUT_SECOND_REMARKS = "Second value";
    public static final ParameterDescriptor<GridCoverage> INPUT_SECOND = new ParameterBuilder()
            .addName(INPUT_SECOND_NAME)
            .setRemarks(INPUT_SECOND_REMARKS)
            .setRequired(false)
            .create(GridCoverage.class, null);

    public static final String INPUT_THIRD_NAME = "third";
    private static final String INPUT_THIRD_REMARKS = "Third value";
    public static final ParameterDescriptor<GridCoverage> INPUT_THIRD = new ParameterBuilder()
            .addName(INPUT_THIRD_NAME)
            .setRemarks(INPUT_THIRD_REMARKS)
            .setRequired(false)
            .create(GridCoverage.class, null);

    public static final String INPUT_FOURTH_NAME = "fourth";
    private static final String INPUT_FOURTH_REMARKS = "Fourth value";
    public static final ParameterDescriptor<GridCoverage> INPUT_FOURTH = new ParameterBuilder()
            .addName(INPUT_FOURTH_NAME)
            .setRemarks(INPUT_FOURTH_REMARKS)
            .setRequired(false)
            .create(GridCoverage.class, null);

    public static final String INPUT_FIFTH_NAME = "fifth";
    private static final String INPUT_FIFTH_REMARKS = "Fifth value";
    public static final ParameterDescriptor<GridCoverage> INPUT_FIFTH = new ParameterBuilder()
            .addName(INPUT_FIFTH_NAME)
            .setRemarks(INPUT_FIFTH_REMARKS)
            .setRequired(false)
            .create(GridCoverage.class, null);

    public static final String INPUT_SIXTH_NAME = "sixth";
    private static final String INPUT_SIXTH_REMARKS = "Sixth value";
    public static final ParameterDescriptor<GridCoverage> INPUT_SIXTH = new ParameterBuilder()
            .addName(INPUT_SIXTH_NAME)
            .setRemarks(INPUT_SIXTH_REMARKS)
            .setRequired(false)
            .create(GridCoverage.class, null);

    public static final String INPUT_SEVENTH_NAME = "seventh";
    private static final String INPUT_SEVENTH_REMARKS = "Seventh value";
    public static final ParameterDescriptor<GridCoverage> INPUT_SEVENTH = new ParameterBuilder()
            .addName(INPUT_SEVENTH_NAME)
            .setRemarks(INPUT_SEVENTH_REMARKS)
            .setRequired(false)
            .create(GridCoverage.class, null);

    public static final String INPUT_EIGHTH_NAME = "eighth";
    private static final String INPUT_EIGHTH_REMARKS = "Eighth value";
    public static final ParameterDescriptor<GridCoverage> INPUT_EIGHTH = new ParameterBuilder()
            .addName(INPUT_EIGHTH_NAME)
            .setRemarks(INPUT_EIGHTH_REMARKS)
            .setRequired(false)
            .create(GridCoverage.class, null);

    public static final String INPUT_NINTH_NAME = "ninth";
    private static final String INPUT_NINTH_REMARKS = "Ninth value";
    public static final ParameterDescriptor<GridCoverage> INPUT_NINTH = new ParameterBuilder()
            .addName(INPUT_NINTH_NAME)
            .setRemarks(INPUT_NINTH_REMARKS)
            .setRequired(false)
            .create(GridCoverage.class, null);

    public static final String INPUT_TENTH_NAME = "tenth";
    private static final String INPUT_TENTH_REMARKS = "Tenth value";
    public static final ParameterDescriptor<GridCoverage> INPUT_TENTH = new ParameterBuilder()
            .addName(INPUT_TENTH_NAME)
            .setRemarks(INPUT_TENTH_REMARKS)
            .setRequired(false)
            .create(GridCoverage.class, null);

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder().addName("InputParameters").setRequired(true)
            .createGroup(INPUT_FIRST, INPUT_SECOND, INPUT_THIRD, INPUT_FOURTH, INPUT_FIFTH,
                    INPUT_SIXTH, INPUT_SEVENTH, INPUT_EIGHTH, INPUT_NINTH, INPUT_TENTH);

    public static final String RESULT_NAME = "set";
    private static final String RESULT_REMARKS = "Set of double";
    public static final ParameterDescriptor<GridCoverage[]> RESULT = new ParameterBuilder()
            .addName(RESULT_NAME)
            .setRemarks(RESULT_REMARKS)
            .setRequired(true)
            .create(GridCoverage[].class, null);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder().addName("OutputParameters").setRequired(true)
            .createGroup(RESULT);

    public static final ProcessDescriptor INSTANCE = new CreateArrayGridCoverageValuesDescriptor();

    public CreateArrayGridCoverageValuesDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION, ABSTRACT, INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new CreateArrayGridCoverageValues(this, input);
    }

}
