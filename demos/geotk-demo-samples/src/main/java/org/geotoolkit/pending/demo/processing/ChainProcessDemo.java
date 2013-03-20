
package org.geotoolkit.pending.demo.processing;

import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.chain.ChainProcessDescriptor;
import org.geotoolkit.process.chain.model.Chain;
import org.geotoolkit.process.chain.model.ChainElement;
import org.geotoolkit.process.chain.model.Constant;
import org.geotoolkit.process.chain.model.Parameter;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Demo of a chain of processes.
 */
public class ChainProcessDemo {
    
    public static void main(String[] args) throws ProcessException {
        
        //produce a chain equivalent to :  ($a + 10) / $b
        final Chain chain = new Chain("myChain");
        
        //input/out/constants parameters
        final Parameter a = chain.addInputParameter("a", Double.class, "desc",1,1,null);
        final Parameter b = chain.addInputParameter("b", Double.class, "desc",1,1,null);   
        final Parameter r = chain.addOutputParameter("r", Double.class, "desc",1,1,null);       
        final Constant c = chain.addConstant(1, Double.class, 10d);        
        
        //chain blocks
        final ChainElement add = chain.addChainElement(2, "math", "add");
        final ChainElement divide = chain.addChainElement(3, "math", "divide");        
        
        //execution flow links
        chain.addFlowLink(Chain.IN_PARAMS, add.getId());
        chain.addFlowLink(add.getId(), divide.getId());
        chain.addFlowLink(divide.getId(), Chain.OUT_PARAMS);
        
        //data flow links
        chain.addDataLink(Chain.IN_PARAMS, a.getCode(), add.getId(), "first");
        chain.addDataLink(c.getId(), "", add.getId(), "second");
        chain.addDataLink(add.getId(), "result", divide.getId(), "first");
        chain.addDataLink(Chain.IN_PARAMS, b.getCode(), divide.getId(), "second");
        chain.addDataLink(divide.getId(), "result", Chain.OUT_PARAMS, r.getCode());
        
        
        //////////////////// execute the chain /////////////////////////////////
        //create a process descriptor to use it like any process.
        final ProcessDescriptor desc = new ChainProcessDescriptor(chain, new DefaultDataIdentification());
        
        //input params 
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("a").setValue(15d);
        input.parameter("b").setValue(2d);
        
        final org.geotoolkit.process.Process process = desc.createProcess(input);
        final ParameterValueGroup result = process.call();
        
        System.out.println(result.parameter("r").getValue());
    }
    
}
