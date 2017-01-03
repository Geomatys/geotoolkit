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
package org.geotoolkit.processing.groovy;

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

import java.util.HashMap;
import org.geotoolkit.processing.AbstractProcessTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnit test of groovy condition process
 * @author Christophe Mourette
 * @module
 */
public class GroovyTest extends AbstractProcessTest {


    public GroovyTest() {
        super("groovy:condition");
    }

    @Test
    public void testGroovy() throws NoSuchIdentifierException, ProcessException{

        // Inputs first
        final HashMap<String,Object> variables = new HashMap<String, Object>();
        final String  expression = "(i>2)";
        variables.put("i",new Integer(3)) ;
        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(FACTORY, "groovy:condition");

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("variables").setValue(variables);
        in.parameter("expression").setValue(expression);
        final org.geotoolkit.process.Process proc = desc.createProcess(in);

        //result
        final Boolean result = (Boolean) proc.call().parameter("result").getValue();

        assertTrue(result);
    }

}
