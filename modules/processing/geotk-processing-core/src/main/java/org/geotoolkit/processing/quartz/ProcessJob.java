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
package org.geotoolkit.processing.quartz;

import org.geotoolkit.processing.AbstractProcess;
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
import org.apache.sis.parameter.Parameters;

/**
 * Quartz job executing a geotoolkit process.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ProcessJob implements InterruptableJob {

    public static final String KEY_FACTORY_ID = "factoryID";
    public static final String KEY_PROCESS_ID = "processID";
    public static final String KEY_PARAMETERS = "processParams";
    public static final String KEY_PROCESS    = "processObj";

    private Process process = null;

    private String jobId = null;

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
        final Parameters params = Parameters.castOrWrap((ParameterValueGroup) objProcessParams);
        process = (Process) objProcess;

        if(process == null){
            final ProcessDescriptor desc = getProcessDescriptor(factoryId, processId);
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

    /**
     * Method to retrieve a process descriptor (overriden in subProject).
     *
     * @param factoryId
     * @param processId
     * @return
     * @throws NoSuchIdentifierException
     */
    protected ProcessDescriptor getProcessDescriptor(final String factoryId, final String processId) throws JobExecutionException {
        try {
            return ProcessFinder.getProcessDescriptor(factoryId, processId);
        } catch (NoSuchIdentifierException ex) {
            throw new JobExecutionException("Process not found for id : " + factoryId + "." + processId);
        }
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        if (process != null) {
            ((AbstractProcess) process).dismissProcess();
        }
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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
        public void dismissed(ProcessEvent event) {
        }

        @Override
        public void paused(ProcessEvent event) {
        }

        @Override
        public void resumed(ProcessEvent event) {
        }
    }

}
