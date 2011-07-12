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
package org.geotoolkit.process.math.toradian;

import org.geotoolkit.process.math.round.*;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.math.AbstractProcessTest;

import org.opengis.parameter.ParameterValueGroup;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit test of ToRadian process
 * @author Quentin Boileau
 * @module pending
 */
public class ToRadianTest extends AbstractProcessTest{

   

    public ToRadianTest() {
        super("toRadian");
    }

    @Test
    public void testToRadian() {

        // Inputs first
        final double first = 24.64;

        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("math", "toRadian");
        final org.geotoolkit.process.Process proc = desc.createProcess();

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("first").setValue(first);
        proc.setInput(in);
        proc.run();

        //result
        final Double result = (Double) proc.getOutput().parameter("result").getValue();
       
        assertEquals(0.4300, result.doubleValue(), 0.0001);
    }
    
}
