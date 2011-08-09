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
package org.geotoolkit.process.math.absolute;

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.math.AbstractProcessTest;

import org.opengis.parameter.ParameterValueGroup;

import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.util.NoSuchIdentifierException;

/**
 * JUnit test of absolute process
 * @author Quentin Boileau
 * @module pending
 */
public class AbsoluteTest extends AbstractProcessTest{

   

    public AbsoluteTest() {
        super("absolute");
    }

    @Test
    public void testAbsolute() throws NoSuchIdentifierException, ProcessException {

        // Inputs first
        final double first = -2.6;

        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("math", "absolute");

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("first").setValue(first);
        final org.geotoolkit.process.Process proc = desc.createProcess(in);

        //result
        final Double result = (Double) proc.call().parameter("result").getValue();

       
        assertEquals(new Double(2.6), result);
    }
    
}
