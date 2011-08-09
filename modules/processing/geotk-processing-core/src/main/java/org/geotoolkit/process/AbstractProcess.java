/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.process;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.metadata.quality.ConformanceResult;
import org.geotoolkit.parameter.Parameters;
import javax.swing.event.EventListenerList;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractProcess implements Process{

    protected final EventListenerList listeners = new EventListenerList();
    
    protected final ProcessDescriptor descriptor;
    protected final ParameterValueGroup outputParameters;
    protected ParameterValueGroup inputParameters;

    public AbstractProcess(final ProcessDescriptor desc, final ParameterValueGroup input){
        ensureNonNull("descriptor", desc);
        ensureNonNull("input", input);
        this.descriptor = desc;
        this.outputParameters = descriptor.getOutputDescriptor().createValue();
        this.inputParameters = input;
        
        final ConformanceResult res = Parameters.isValid(inputParameters, inputParameters.getDescriptor());
        if(!res.pass()){
            throw new IllegalArgumentException("Input parameters are unvalid.");
        }
        
    }

    @Override
    public ProcessDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public ParameterValueGroup getInput() {
        return inputParameters;
    }
    
    @Override
    public void addListener(final ProcessListener listener) {
        listeners.add(ProcessListener.class, listener);
    }

    @Override
    public void removeListener(final ProcessListener listener){        
        listeners.remove(ProcessListener.class, listener);
    }

    @Override
    public ProcessListener[] getListeners(){
        return listeners.getListeners(ProcessListener.class);
    }
    
    /**
     * Forward a start event to all listeners.
     * @param event 
     */
    protected void fireStartEvent(final ProcessEvent event){
        for(ProcessListener listener : listeners.getListeners(ProcessListener.class)){
            listener.started(event);
        }
    }
    
    /**
     * Forward a progress event to all listeners.
     * @param event 
     */
    protected void fireProgressEvent(final ProcessEvent event){
        for(ProcessListener listener : listeners.getListeners(ProcessListener.class)){
            listener.progressing(event);
        }
    }
    
    /**
     * Forward a fail event to all listeners.
     * @param event 
     */
    protected void fireFailEvent(final ProcessEvent event){
        for(ProcessListener listener : listeners.getListeners(ProcessListener.class)){
            listener.failed(event);
        }
    }
    
    /**
     * Forward an end event to all listeners.
     * @param event 
     */
    protected void fireEndEvent(final ProcessEvent event){
        for(ProcessListener listener : listeners.getListeners(ProcessListener.class)){
            listener.completed(event);
        }
    }

}
