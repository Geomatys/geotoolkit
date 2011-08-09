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

import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * Describe a chained process.
 * 
 * @author johann Sorel (Geomatys)
 * @module pending
 */
public class ChainedProcessDescriptor extends AbstractProcessDescriptor{

    private final List<Object> stack;
    
    public ChainedProcessDescriptor(final String name, final Identification factoryId, final InternationalString abs,
            final List<Object> stack) {
        this(new DerivateIdentifier(name, factoryId),abs,stack);
    }
    
    public ChainedProcessDescriptor(final Identifier id, final InternationalString desc, final List<Object> stack){
        super(id,desc,getInputDescriptor(stack),getOutputDescriptor(stack));
        this.stack = UnmodifiableArrayList.wrap(stack.toArray()); //defensive copy
        
        for(int i=0,n=this.stack.size();i<n;i++){
            if(i%2 == 0){
                if(!(this.stack.get(i) instanceof ProcessDescriptor)){
                    throw new IllegalArgumentException("Element number "+i+" of the stack must be a process descriptor.");
                }
            }else{
                if(!(this.stack.get(i) instanceof ParameterMapper)){
                    throw new IllegalArgumentException("Element number "+i+" of the stack must be a parameter mapper.");
                }
            }
        }
        
    }
        
    private static ParameterDescriptorGroup getInputDescriptor(final List<Object> stack){
        if(stack.isEmpty()){
            throw new IllegalArgumentException("No elements in the stack");
        }
        
        final Object obj = stack.get(0);
        if(obj instanceof ProcessDescriptor){
            return ((ProcessDescriptor)obj).getInputDescriptor();
        }else{
            throw new IllegalArgumentException("First element of the stack must be a process descriptor.");
        }
    }
    
     private static ParameterDescriptorGroup getOutputDescriptor(final List<Object> stack){
        if(stack.isEmpty()){
            throw new IllegalArgumentException("No elements in the stack");
        }
        
        final Object obj = stack.get(stack.size()-1);
        if(obj instanceof ProcessDescriptor){
            return ((ProcessDescriptor)obj).getInputDescriptor();
        }else{
            throw new IllegalArgumentException("Last element of the stack must be a process descriptor.");
        }
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ChainedProcess(this, input, stack);
    }
    
}
