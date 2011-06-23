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
package org.geotoolkit.process.quartz;

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessMonitor;

import org.opengis.parameter.ParameterValueGroup;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Quartz job executing a geotoolkit process.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ProcessJob implements Job{

    public static final String KEY_FACTORY_ID = "factoryID";
    public static final String KEY_PROCESS_ID = "processID";
    public static final String KEY_PARAMETERS = "processParams";
    
    @Override
    public void execute(final JobExecutionContext jec) throws JobExecutionException {
        final JobDataMap parameters = jec.getJobDetail().getJobDataMap();
        
        final Object objFactoryId = parameters.get(KEY_FACTORY_ID);
        final Object objProcessId = parameters.get(KEY_PROCESS_ID);
        final Object objProcessParams = parameters.get(KEY_PARAMETERS);
        
        if(!(objFactoryId instanceof String)){
            throw new JobExecutionException("Factory id is not String, value found : " + objFactoryId);
        }
        if(!(objProcessId instanceof String)){
            throw new JobExecutionException("Process id is not String, value found : " + objProcessId);
        }
        if(!(objProcessParams instanceof ParameterValueGroup)){
            throw new JobExecutionException("Parameters is not an ISO parameter, value found : " + objProcessParams);
        }
        
        final String factoryId = (String) objFactoryId;
        final String processId = (String) objProcessId;
        final ParameterValueGroup params = (ParameterValueGroup) objProcessParams;        
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(factoryId, processId);
        
        if(desc == null){
            throw new JobExecutionException("Process not found for id : " + objFactoryId+"."+objProcessId);
        }
        
        final StoreExceptionMonitor monitor = new StoreExceptionMonitor();
        final Process process = desc.createProcess();        
        process.setInput(params);
        process.setMonitor(monitor);
        process.run();
        
        //set the result int he context, for listener that might want it.
        final ParameterValueGroup result = process.getOutput();
        jec.setResult(result);
        
        //forward process error
        if(monitor.failed != null){
            throw monitor.failed;
        }        
    }

    private final class StoreExceptionMonitor implements ProcessMonitor{

        JobExecutionException failed = null;
        
        @Override
        public void started(ProcessEvent event) {
        }

        @Override
        public void progressing(ProcessEvent event) {
        }

        @Override
        public void ended(ProcessEvent event) {
        }

        @Override
        public void failed(ProcessEvent event) {
            failed = new JobExecutionException(String.valueOf(event.getMessage()), event.getThrowable(),false);
        }
        
    }
    
    
}
