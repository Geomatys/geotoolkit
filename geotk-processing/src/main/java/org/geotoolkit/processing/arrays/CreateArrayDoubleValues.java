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

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 */
public class CreateArrayDoubleValues extends AbstractProcess {

    public CreateArrayDoubleValues(final ProcessDescriptor desc, final ParameterValueGroup parameter) {
        super(desc, parameter);
    }

    @Override
    protected void execute() {

        final Double value1 = inputParameters.getValue(CreateArrayDoubleValuesDescriptor.INPUT_FIRST);
        final Double value2 = inputParameters.getValue(CreateArrayDoubleValuesDescriptor.INPUT_SECOND);
        final Double value3 = inputParameters.getValue(CreateArrayDoubleValuesDescriptor.INPUT_THIRD);
        final Double value4 = inputParameters.getValue(CreateArrayDoubleValuesDescriptor.INPUT_FOURTH);
        final Double value5 = inputParameters.getValue(CreateArrayDoubleValuesDescriptor.INPUT_FIFTH);
        final Double value6 = inputParameters.getValue(CreateArrayDoubleValuesDescriptor.INPUT_SIXTH);
        final Double value7 = inputParameters.getValue(CreateArrayDoubleValuesDescriptor.INPUT_SEVENTH);
        final Double value8 = inputParameters.getValue(CreateArrayDoubleValuesDescriptor.INPUT_EIGHTH);
        final Double value9 = inputParameters.getValue(CreateArrayDoubleValuesDescriptor.INPUT_NINTH);
        final Double value10 = inputParameters.getValue(CreateArrayDoubleValuesDescriptor.INPUT_TENTH);

        final Double[] set = Arrays.stream(new Double[]{value1, value2, value3, value4, value5, value6, value7, value8, value9, value10})
                .filter(Objects::nonNull)
                .toArray(Double[]::new);

        outputParameters.getOrCreate(CreateArrayDoubleValuesDescriptor.RESULT).setValue(set);
    }
}
