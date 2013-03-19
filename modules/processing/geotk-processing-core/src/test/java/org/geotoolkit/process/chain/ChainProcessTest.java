/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.process.chain;

import java.util.Collections;
import java.util.Set;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.chain.model.Chain;
import org.geotoolkit.process.chain.model.ChainElement;
import org.geotoolkit.process.chain.model.Constant;
import org.geotoolkit.process.chain.model.Parameter;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ChainProcessTest {
 
    @Test
    public void testChain() throws ProcessException{
        
        //produce a chain equivalent to :  ($1 + 10) / $2
        final Chain chain = new Chain("myChain");
        
        //input parameters
        final Parameter a = new Parameter("a", Double.class, "desc",1,1);
        final Parameter b = new Parameter("b", Double.class, "desc",1,1);        
        chain.getInputs().add(a);
        chain.getInputs().add(b);
        
        //output parameters
        final Parameter r = new Parameter("r", Double.class, "desc",1,1);
        chain.getOutputs().add(r);
        
        //a constant
        final Constant cst = new Constant(1, Double.class, "10");
        chain.getConstants().add(cst);
        
        //chain blocks
        final ChainElement add = new ChainElement(2, "demo", "add");
        final ChainElement divide = new ChainElement(3, "demo", "divide");
        chain.getChainElements().add(add);
        chain.getChainElements().add(divide);
        
        //execution flow links
        final Chain.ExecutionLinkDto execLink1 = new Chain.ExecutionLinkDto(Chain.IN_PARAMS, add.getId());
        final Chain.ExecutionLinkDto execLink2 = new Chain.ExecutionLinkDto(add.getId(), divide.getId());
        final Chain.ExecutionLinkDto execLink3 = new Chain.ExecutionLinkDto(divide.getId(), Chain.OUT_PARAMS);
        chain.getExecutionLinks().add(execLink1);
        chain.getExecutionLinks().add(execLink2);
        chain.getExecutionLinks().add(execLink3);
        
        //value flow links
        final Chain.LinkDto link1 = new Chain.LinkDto(
                Chain.IN_PARAMS, a.getCode(), add.getId(), MockAddDescriptor.FIRST_NUMBER);
        final Chain.LinkDto link2 = new Chain.LinkDto(
                cst.getId(), "",                add.getId(), MockAddDescriptor.SECOND_NUMBER);
        final Chain.LinkDto link3 = new Chain.LinkDto(
                add.getId(), MockAddDescriptor.RESULT_NUMBER, divide.getId(), MockDivideDescriptor.FIRST_NUMBER);
        final Chain.LinkDto link4 = new Chain.LinkDto(
                Chain.IN_PARAMS, b.getCode(),               divide.getId(), MockDivideDescriptor.SECOND_NUMBER);
        final Chain.LinkDto link5 = new Chain.LinkDto(
                divide.getId(), MockDivideDescriptor.RESULT_NUMBER,               Chain.OUT_PARAMS, r.getCode());
        chain.getLinks().add(link1);
        chain.getLinks().add(link2);
        chain.getLinks().add(link3);
        chain.getLinks().add(link4);
        chain.getLinks().add(link5);
        
        //process registries to use
        final Set<MockProcessRegistry> registries = Collections.singleton(new MockProcessRegistry());
        
        //create a process descriptor to use it like any process.
        final ProcessDescriptor desc = new ChainProcessDescriptor(chain, MockProcessRegistry.IDENTIFICATION, registries);
        
        //input params 
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("a").setValue(15d);
        input.parameter("b").setValue(2d);
        
        final Process process = desc.createProcess(input);
        final ParameterValueGroup result = process.call();
        
        assertEquals(12.5d, result.parameter("r").doubleValue(),0.000001);
        
    }
    
}
