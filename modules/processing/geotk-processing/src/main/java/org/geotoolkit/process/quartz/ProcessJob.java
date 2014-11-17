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

import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.ProcessListener;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import java.util.ArrayList;
import java.util.List;

/**
 * Quartz job executing a geotoolkit process.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ProcessJob implements InterruptableJob {

    public static final String KEY_FACTORY_ID = "factoryID";
    public static final String KEY_PROCESS_ID = "processID";
    public static final String KEY_PARAMETERS = "processParams";
    public static final String KEY_PROCESS    = "processObj";

    private Process process = null;

    private final List<ProcessListener> listeners = new ArrayList<>();
    
    public void addListener(ProcessListener listener){
        listeners.add(listener);
    }
    
    @Override
    public void execute(final JobExecutionContext jec) throws JobExecutionException {
        final JobDataMap parameters = jec.getJobDetail().getJobDataMap();

        final Object objFactoryId = parameters.get(KEY_FACTORY_ID);
        final Object objProcessId = parameters.get(KEY_PROCESS_ID);
        final Object objProcessParams = parameters.get(KEY_PARAMETERS);
        final Object objProcess = parameters.get(KEY_PROCESS);

        if(!(objFactoryId instanceof String)){
            throw new JobExecutionException("Factory id is not String, value found : " + objFactoryId);
        }
        if(!(objProcessId instanceof String)){
            throw new JobExecutionException("Process id is not String, value found : " + objProcessId);
        }
        if(!(objProcessParams instanceof ParameterValueGroup)){
            throw new JobExecutionException("Parameters is not an ISO parameter, value found : " + objProcessParams);
        }
         if(objProcess != null && !(objProcess instanceof Process)){
            throw new JobExecutionException("Process object is invalid, value found : " + objProcess);
        }

        final String factoryId = (String) objFactoryId;
        final String processId = (String) objProcessId;
        final ParameterValueGroup params = (ParameterValueGroup) objProcessParams;
        process = (Process) objProcess;

        if(process == null){
            final ProcessDescriptor desc;
            try{
                desc = ProcessFinder.getProcessDescriptor(factoryId, processId);
            }catch(NoSuchIdentifierException ex){
                throw new JobExecutionException("Process not found for id : " + objFactoryId+"."+objProcessId);
            }
            process = desc.createProcess(params);
        }
        final StoreExceptionMonitor monitor = new StoreExceptionMonitor();
        process.addListener(monitor);
        for(ProcessListener pl : listeners){
            process.addListener(pl);
        }

        //set the result int he context, for listener that might want it.
        final ParameterValueGroup result;
        try {
            result = process.call();
        } catch (ProcessException ex) {
            if(monitor.failed != null){
                throw monitor.failed;
            } else{
                throw new JobExecutionException(ex);
            }
        }
        jec.setResult(result);

    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        if (process != null) {
            ((AbstractProcess) process).cancelProcess();
        }
    }

    private final class StoreExceptionMonitor implements ProcessListener{

        JobExecutionException failed = null;

        @Override
        public void started(ProcessEvent event) {
        }

        @Override
        public void progressing(ProcessEvent event) {
        }

        @Override
        public void completed(ProcessEvent event) {
        }

        @Override
        public void failed(ProcessEvent event) {
            final Exception exception = event.getException();
            final String message = String.valueOf(event.getTask()) + (exception != null ? " : "+exception.getMessage() : "");
            failed = new JobExecutionException(message, exception,false);
        }

        @Override
        public void paused(ProcessEvent event) {
        }

        @Override
        public void resumed(ProcessEvent event) {
        }
    }

}
