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
package org.geotoolkit.process.math.avg;

import org.geotoolkit.process.math.sum.*;
import org.geotoolkit.process.math.min.*;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.math.AbstractProcessTest;

import org.opengis.parameter.ParameterValueGroup;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit test of Sum process
 * @author Quentin Boileau
 * @module pending
 */
public class AvgTest extends AbstractProcessTest{

   

    public AvgTest() {
        super("avg");
    }

    @Test
    public void testSum() {

        // Inputs first
        final Double[] set = {new Double(15.5), 
                              new Double(10.02), 
                              new Double(1.43), 
                              new Double(-3.03), 
                              new Double(4.53), 
                              new Double(-6.21)};

        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("math", "avg");
        final org.geotoolkit.process.Process proc = desc.createProcess();

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("set").setValue(set);
        proc.setInput(in);
        proc.run();

        //result
        final Double result = (Double) proc.getOutput().parameter("result").getValue();
       
        assertEquals(3.7066, result.doubleValue(), 0.0001);
    }
    
}
