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

import org.geotoolkit.parameter.Parameters;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractProcess implements Process{

    private static final ProcessMonitor DUMMY_MONITOR = new ProcessMonitor() {

        @Override
        public void started(ProcessEvent event) {
            final Throwable error = event.getThrowable();
            if(error != null){
                error.printStackTrace();
            }
        }

        @Override
        public void progressing(ProcessEvent event) {
            final Throwable error = event.getThrowable();
            if(error != null){
                error.printStackTrace();
            }
        }

        @Override
        public void ended(ProcessEvent event) {
            final Throwable error = event.getThrowable();
            if(error != null){
                error.printStackTrace();
            }
        }

        @Override
        public void failed(ProcessEvent event) {
            final Throwable error = event.getThrowable();
            if(error != null){
                error.printStackTrace();
            }
        }
    };

    protected final ProcessDescriptor descriptor;
    protected ParameterValueGroup inputParameters;
    private ProcessMonitor monitor = null;

    public AbstractProcess(ProcessDescriptor desc){
        if(desc == null){
            throw new NullPointerException("Descriptor is null");
        }
        this.descriptor = desc;
    }

    @Override
    public ProcessDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void setInput(ParameterValueGroup parameter) {
        inputParameters = parameter;
        final ConformanceResult res = Parameters.isValid(inputParameters, inputParameters.getDescriptor());
        if(!res.pass()){
            throw new IllegalArgumentException("Input parameters are unvalid.");
        }
    }

    @Override
    public ParameterValueGroup getOutput() {
        return descriptor.getOutputDescriptor().createValue();
    }

    @Override
    public void setMonitor(ProcessMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public ProcessMonitor getMonitor() {
        if(monitor == null){
            return DUMMY_MONITOR;
        }else{
            return monitor;
        }
    }


}
