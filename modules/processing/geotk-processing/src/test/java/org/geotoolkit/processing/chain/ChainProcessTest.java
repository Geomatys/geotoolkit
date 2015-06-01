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
package org.geotoolkit.processing.chain;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.chain.model.Chain;
import org.geotoolkit.processing.chain.model.ElementProcess;
import org.geotoolkit.processing.chain.model.Constant;
import org.geotoolkit.processing.chain.model.ElementCondition;
import org.geotoolkit.processing.chain.model.FlowLink;
import org.geotoolkit.processing.chain.model.Parameter;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.geotoolkit.processing.chain.model.Element.*;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ChainProcessTest {
 
    private Chain createSimpleChain(){        
        //produce a chain equivalent to :  ($1 + 10) / $2
        final Chain chain = new Chain("myChain");
        int id = 1;
        
        //input/out/constants parameters
        final Parameter a = chain.addInputParameter("a", Double.class, "desc",1,1,null);
        final Parameter b = chain.addInputParameter("b", Double.class, "desc",1,1,null);   
        final Parameter r = chain.addOutputParameter("r", Double.class, "desc",1,1,null);       
        final Constant c = chain.addConstant(id++, Double.class, 10d);        
        
        //chain blocks
        final ElementProcess add = chain.addProcessElement(id++, "demo", "add");
        final ElementProcess divide = chain.addProcessElement(id++, "demo", "divide");        
        
        //execution flow links
        chain.addFlowLink(BEGIN.getId(), add.getId());
        chain.addFlowLink(add.getId(), divide.getId());
        chain.addFlowLink(divide.getId(), END.getId());
        
        //data flow links
        chain.addDataLink(BEGIN.getId(), a.getCode(), add.getId(), "first");
        chain.addDataLink(c.getId(), "", add.getId(), "second");
        chain.addDataLink(add.getId(), "result", divide.getId(), "first");
        chain.addDataLink(BEGIN.getId(), b.getCode(), divide.getId(), "second");
        chain.addDataLink(divide.getId(), "result", END.getId(), r.getCode());
        
        return chain;
    }
    
    private Chain createBranchChain(){        
        //produce a chain equivalent to :  (($a+10) > 20) ? *10 : /10
        final Chain chain = new Chain("branchChain");
        int id = 1;
        
        //input/out/constants parameters
        final Parameter a = chain.addInputParameter("a", Double.class, "desc",1,1,null);
        final Parameter r = chain.addOutputParameter("r", Double.class, "desc",1,1,null);       
        final Constant c10 = chain.addConstant(id++, Double.class, 10d);        
        
        //chain blocks
        final ElementProcess add = chain.addProcessElement(id++, "demo", "add");
        final ElementProcess multi = chain.addProcessElement(id++, "demo", "multiply");  
        final ElementProcess divide = chain.addProcessElement(id++, "demo", "divide");        
        final ElementCondition condition = chain.addConditionElement(id++);
        condition.getInputs().add(new Parameter("value", Double.class, "", 1, 1));
        condition.setSyntax("CQL");
        condition.setExpression("value > 20");
                
        //execution flow links
        chain.addFlowLink(BEGIN.getId(), add.getId());
        chain.addFlowLink(add.getId(), condition.getId());
        final FlowLink success = chain.addFlowLink(condition.getId(), multi.getId()); condition.getSuccess().add(success);
        final FlowLink fail = chain.addFlowLink(condition.getId(), divide.getId()); condition.getFailed().add(fail);
        chain.addFlowLink(divide.getId(), END.getId());
        chain.addFlowLink(multi.getId(), END.getId());
        
        //data flow links
        chain.addDataLink(c10.getId(), "",    add.getId(), "second");
        chain.addDataLink(c10.getId(), "",  multi.getId(), "second");
        chain.addDataLink(c10.getId(), "", divide.getId(), "second");
        chain.addDataLink(BEGIN.getId(), a.getCode(), add.getId(), "first");
        chain.addDataLink(add.getId(), "result", condition.getId(), "value");
        chain.addDataLink(add.getId(), "result",     multi.getId(), "first");
        chain.addDataLink(add.getId(), "result",    divide.getId(), "first");
        chain.addDataLink(divide.getId(), "result", END.getId(), r.getCode());
        chain.addDataLink(multi.getId(),  "result", END.getId(), r.getCode());
        
        return chain;
    }
    
    @Test
    public void testSimpleChain() throws ProcessException{
        
        final Chain chain = createSimpleChain();
        
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
    public void testSimpleXmlRW() throws ProcessException, JAXBException, IOException{
        
        final Chain before = createSimpleChain();
        
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
    
    @Test
    public void testBranchChain() throws ProcessException{
        
        final Chain chain = createBranchChain();
        
        //process registries to use
        final Set<MockProcessRegistry> registries = Collections.singleton(new MockProcessRegistry());
        
        //create a process descriptor to use it like any process.
        final ProcessDescriptor desc = new ChainProcessDescriptor(chain, MockProcessRegistry.IDENTIFICATION, registries);
        
        //input params , condition evaluates to TRUE----------------------------
        ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("a").setValue(15d);
        
        Process process = desc.createProcess(input);
        ParameterValueGroup result = process.call();
        
        assertEquals(250d, result.parameter("r").doubleValue(),0.000001);
        
        //input params , condition evaluates to FALSE---------------------------
        input = desc.getInputDescriptor().createValue();
        input.parameter("a").setValue(-5d);
        
        process = desc.createProcess(input);
        result = process.call();
        
        assertEquals(0.5d, result.parameter("r").doubleValue(),0.000001);
        
    }
    
    @Test
    public void testBranchXmlRW() throws ProcessException, JAXBException, IOException{
        
        final Chain before = createBranchChain();
        
        final File f = File.createTempFile("chain", ".xml");
        before.write(f);
        
        final Chain chain = Chain.read(f);
        
        //process registries to use
        final Set<MockProcessRegistry> registries = Collections.singleton(new MockProcessRegistry());
        
        //create a process descriptor to use it like any process.
        final ProcessDescriptor desc = new ChainProcessDescriptor(chain, MockProcessRegistry.IDENTIFICATION, registries);
        
        //input params , condition evaluates to TRUE----------------------------
        ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("a").setValue(15d);
        
        Process process = desc.createProcess(input);
        ParameterValueGroup result = process.call();
        
        assertEquals(250d, result.parameter("r").doubleValue(),0.000001);
        
        //input params , condition evaluates to FALSE---------------------------
        input = desc.getInputDescriptor().createValue();
        input.parameter("a").setValue(-5d);
        
        process = desc.createProcess(input);
        result = process.call();
        
        assertEquals(0.5d, result.parameter("r").doubleValue(),0.000001);
        
    }
    
}
