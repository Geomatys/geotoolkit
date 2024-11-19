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
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.math.CoverageMathsWithValueAbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.processing.coverage.math.sum.CoverageSumWithValueDescriptor.IN_COVERAGE;
import static org.geotoolkit.processing.coverage.math.sum.CoverageSumWithValueDescriptor.IN_VALUE;
import static org.geotoolkit.processing.coverage.math.sum.CoverageSumWithValueDescriptor.OUT_COVERAGE;


/**
 * @author Quentin BIALOTA (Geomatys)
 */
public class CoverageSumWithValueProcess extends CoverageMathsWithValueAbstractProcess {

    public CoverageSumWithValueProcess(final ProcessDescriptor desc, final ParameterValueGroup parameter) {
        super(desc, parameter);
    }

    @Override
    protected double performOperation(double value1, double value2) {
        return value1 + value2;
    }

    @Override
    protected void execute() throws ProcessException {
        GridCoverage inputCoverage = inputParameters.getValue(IN_COVERAGE);
        Double inputValue = inputParameters.getValue(IN_VALUE);

        final GridCoverage resultCoverage = executeOperation(inputCoverage, inputValue);

        outputParameters.getOrCreate(OUT_COVERAGE).setValue(resultCoverage);
    }
}
