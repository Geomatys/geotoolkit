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

import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Quentin BIALOTA (Geomatys)
 */
public class CreateArrayDoubleTest {

    @Test
    public void doubleArraysTest() throws Exception{

        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, CreateArrayDoubleValuesDescriptor.NAME);
        assertNotNull(desc);

        Double[] arrayExpected = {4.5d, 856.4545d, 87.0d};

        final ParameterValueGroup params = desc.getInputDescriptor().createValue();

        params.parameter(CreateArrayDoubleValuesDescriptor.INPUT_FIRST_NAME).setValue(arrayExpected[0]);
        params.parameter(CreateArrayDoubleValuesDescriptor.INPUT_SECOND_NAME).setValue(arrayExpected[1]);
        params.parameter(CreateArrayDoubleValuesDescriptor.INPUT_THIRD_NAME).setValue(arrayExpected[2]);

        final Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();

        Double[] array = (Double[]) result.parameter(CreateArrayDoubleValuesDescriptor.RESULT_NAME).getValue();
        assertNotNull(array);
        assertArrayEquals(arrayExpected, array);
    }
}
