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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessListener;
import org.apache.sis.util.ArgumentChecks;

import org.opengis.parameter.ParameterValueGroup;

import org.quartz.Scheduler;
import org.quartz.impl.JobDetailImpl;

import static org.geotoolkit.processing.quartz.ProcessJob.*;

/**
 * Quartz job detail specialized for GeotoolKit process.
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ProcessJobDetail extends JobDetailImpl {
        
    public ProcessJobDetail(final String factoryId, final String processId, final ParameterValueGroup parameters){
        this(factoryId+"."+processId+"-"+UUID.randomUUID(), null, factoryId, processId, parameters);
    }
    
    public ProcessJobDetail(final Process process){
        this(createJobName(process), null, extractProcessID(process), extractFactoryName(process), process.getInput() );
        getJobDataMap().put(KEY_PROCESS, process);
    }
    
    public ProcessJobDetail(final String name, final String group, final String factoryId, 
            final String processId, final ParameterValueGroup parameters){
        super(name, group, ProcessJob.class);
        if(group == null){
            setGroup(Scheduler.DEFAULT_GROUP);
        }
        getJobDataMap().put(KEY_FACTORY_ID, factoryId);
        getJobDataMap().put(KEY_PROCESS_ID, processId);
        getJobDataMap().put(KEY_PARAMETERS, parameters);
    }
    
    /**
     * @return name of the authority
     */
    public String getFactoryIdentifier(){
        return getJobDataMap().getString(KEY_FACTORY_ID);
    }
    
    /**
     * @return name of the process
     */
    public String getProcessIdentifier(){
        return getJobDataMap().getString(KEY_PROCESS_ID);
    }
    
    /**
     * @return parameter values of the process.
     */
    public ParameterValueGroup getParameters(){
        return (ParameterValueGroup) getJobDataMap().get(KEY_PARAMETERS);
    }
    
    public List<ProcessListener> getListeners(){
        final List<ProcessListener> listeners = new ArrayList<>();
        if(getJobDataMap().get(KEY_PROCESS) != null){
            final AbstractProcess process = (AbstractProcess) getJobDataMap().get(KEY_PROCESS);
            Collections.addAll(listeners, process.getListeners());
        }
        return listeners;
    }
    
    /**
     * Crate the job name composed by the process identifier and his factory and an unique UUID.
     * @param process
     * @return job name.
     */
    private static String createJobName(final Process process){
        ArgumentChecks.ensureNonNull("process", process);
        final ProcessDescriptor procDesc = process.getDescriptor();
        final String factory = procDesc.getIdentifier().getAuthority().getTitle().toString();
        final String processID = procDesc.getIdentifier().getCode();
        return factory + "." + processID + "-" + UUID.randomUUID().toString();
    }
    
    /**
     * Extract the process factory name from the {@link ProcessDescriptor}.
     * @param process
     * @return process factory
     */
    private static String extractFactoryName(final Process process){
        ArgumentChecks.ensureNonNull("process", process);
        return process.getDescriptor().getIdentifier().getAuthority().getTitle().toString();
    }
    
    /**
     * Extract the process identifier from the {@link ProcessDescriptor}.
     * @param process
     * @return process identifier
     */
    private static String extractProcessID(final Process process){
        ArgumentChecks.ensureNonNull("process", process);
        return process.getDescriptor().getIdentifier().getCode();
    }
}
