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
package org.geotoolkit.processing.math.todegree;

import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcessTest;
import org.opengis.util.NoSuchIdentifierException;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;

import org.opengis.parameter.ParameterValueGroup;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit test of ToDegree process
 * @author Quentin Boileau
 * @module
 */
public class ToDegreeTest extends AbstractProcessTest {


    public ToDegreeTest() {
        super("math:toDegree");
    }

    @Test
    public void testToDegree() throws NoSuchIdentifierException, ProcessException{

        // Inputs first
        final double first = 0.56;

        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"math:toDegree");

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("first").setValue(first);
        final org.geotoolkit.process.Process proc = desc.createProcess(in);

        //result
        final Double result = (Double) proc.call().parameter("result").getValue();

        assertEquals(32.0856, result.doubleValue(), 0.0001) ;
    }

}
