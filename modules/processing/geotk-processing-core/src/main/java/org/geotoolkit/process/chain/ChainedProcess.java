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
package org.geotoolkit.process.chain;

import java.util.List;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Regroup a stack of process in a single process.
 * Each process in the stack will be called one after another, parameters
 * being transformer by the ParameterMapper between them.
 * 
 * @author johann Sorel (Geomatys)
 * @module pending
 */
public class ChainedProcess extends AbstractProcess{

    private final List<Object> stack;
    
    /**
     * 
     * @param stack : list starting with a Process and ending with process,
     * one process on two must be a ParameterMapper.
     */
    public ChainedProcess(final ProcessDescriptor desc, final List<Object> stack){
        super(desc);
        this.stack = stack;
    }
    
    @Override
    public void run() {
        getMonitor().started(new ProcessEvent(this, 0, null, null));
        
        ParameterValueGroup intermediateResult = inputParameters;;
        
        for(int i=0,n=stack.size(); i<n; i+=2){
            final Process p = ((ProcessDescriptor) stack.get(i)).createProcess();
            
            // first process, no parameter mapping
            //other ones must be transformed
            if(i!=0){
                final ParameterMapper mapper = (ParameterMapper) stack.get(i-1);
                intermediateResult = mapper.transform(intermediateResult, p.getDescriptor().getInputDescriptor());
            }
            
            p.setInput(inputParameters);
            p.run();
            intermediateResult = p.getOutput();            
        }
        
        final ParameterValueGroup result = getOutput();
        result.values().addAll(intermediateResult.values());
        
        getMonitor().ended(new ProcessEvent(this, 100, null, null));
    }
        
}
