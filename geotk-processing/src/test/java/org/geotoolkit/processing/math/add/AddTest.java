/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.processing.math.add;

import org.geotoolkit.process.ProcessException;
import org.opengis.util.NoSuchIdentifierException;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.AbstractProcessTest;
import org.geotoolkit.processing.GeotkProcessingRegistry;

import org.opengis.parameter.ParameterValueGroup;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit test of Add process
 * @author Quentin Boileau
 * @module
 */
public class AddTest extends AbstractProcessTest {


    public AddTest() {
        super("math:add");
    }

    @Test
    public void testAdd() throws NoSuchIdentifierException, ProcessException{

        // Inputs first
        final double first = 10.5;
        final double second = 22.3;

        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"math:add");

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("first").setValue(first);
        in.parameter("second").setValue(second);
        final org.geotoolkit.process.Process proc = desc.createProcess(in);

        //result
        final Double result = (Double) proc.call().parameter("result").getValue();

        assertEquals(new Double(32.8), result);
    }

}
