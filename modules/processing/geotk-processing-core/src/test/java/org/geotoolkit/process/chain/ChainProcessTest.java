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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.xml.bind.JAXBException;
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
 
    private Chain createChain(){        
        //produce a chain equivalent to :  ($1 + 10) / $2
        final Chain chain = new Chain("myChain");
        
        //input/out/constants parameters
        final Parameter a = chain.addInputParameter("a", Double.class, "desc",1,1,null);
        final Parameter b = chain.addInputParameter("b", Double.class, "desc",1,1,null);   
        final Parameter r = chain.addOutputParameter("r", Double.class, "desc",1,1,null);       
        final Constant c = chain.addConstant(1, Double.class, 10d);        
        
        //chain blocks
        final ChainElement add = chain.addChainElement(2, "demo", "add");
        final ChainElement divide = chain.addChainElement(3, "demo", "divide");        
        
        //execution flow links
        chain.addFlowLink(Chain.IN_PARAMS, add.getId());
        chain.addFlowLink(add.getId(), divide.getId());
        chain.addFlowLink(divide.getId(), Chain.OUT_PARAMS);
        
        //data flow links
        chain.addDataLink(Chain.IN_PARAMS, a.getCode(), add.getId(), MockAddDescriptor.FIRST_NUMBER.getName().getCode());
        chain.addDataLink(c.getId(), "", add.getId(), MockAddDescriptor.SECOND_NUMBER.getName().getCode());
        chain.addDataLink(add.getId(), MockAddDescriptor.RESULT_NUMBER.getName().getCode(), divide.getId(), MockDivideDescriptor.FIRST_NUMBER.getName().getCode());
        chain.addDataLink(Chain.IN_PARAMS, b.getCode(), divide.getId(), MockDivideDescriptor.SECOND_NUMBER.getName().getCode());
        chain.addDataLink(divide.getId(), MockDivideDescriptor.RESULT_NUMBER.getName().getCode(), Chain.OUT_PARAMS, r.getCode());
        
        return chain;
    }
    
    @Test
    public void testChain() throws ProcessException{
        
        final Chain chain = createChain();
        
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
    
    @Test
    public void testXmlRW() throws ProcessException, JAXBException, IOException{
        
        final Chain before = createChain();
        
        final File f = File.createTempFile("chain", ".xml");
        before.write(f);
        
        final Chain chain = Chain.read(f);
        
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
