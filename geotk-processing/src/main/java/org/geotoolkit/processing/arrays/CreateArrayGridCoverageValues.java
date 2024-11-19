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
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 */
public class CreateArrayGridCoverageValues extends AbstractProcess {

    public CreateArrayGridCoverageValues(final ProcessDescriptor desc, final ParameterValueGroup parameter) {
        super(desc, parameter);
    }

    @Override
    protected void execute() {

        final GridCoverage value1 = inputParameters.getValue(CreateArrayGridCoverageValuesDescriptor.INPUT_FIRST);
        final GridCoverage value2 = inputParameters.getValue(CreateArrayGridCoverageValuesDescriptor.INPUT_SECOND);
        final GridCoverage value3 = inputParameters.getValue(CreateArrayGridCoverageValuesDescriptor.INPUT_THIRD);
        final GridCoverage value4 = inputParameters.getValue(CreateArrayGridCoverageValuesDescriptor.INPUT_FOURTH);
        final GridCoverage value5 = inputParameters.getValue(CreateArrayGridCoverageValuesDescriptor.INPUT_FIFTH);
        final GridCoverage value6 = inputParameters.getValue(CreateArrayGridCoverageValuesDescriptor.INPUT_SIXTH);
        final GridCoverage value7 = inputParameters.getValue(CreateArrayGridCoverageValuesDescriptor.INPUT_SEVENTH);
        final GridCoverage value8 = inputParameters.getValue(CreateArrayGridCoverageValuesDescriptor.INPUT_EIGHTH);
        final GridCoverage value9 = inputParameters.getValue(CreateArrayGridCoverageValuesDescriptor.INPUT_NINTH);
        final GridCoverage value10 = inputParameters.getValue(CreateArrayGridCoverageValuesDescriptor.INPUT_TENTH);

        final GridCoverage[] set = Arrays.stream(new GridCoverage[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10})
                .filter(Objects::nonNull)
                .toArray(GridCoverage[]::new);

        outputParameters.getOrCreate(CreateArrayGridCoverageValuesDescriptor.RESULT).setValue(set);
    }
}
