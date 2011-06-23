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

import java.util.UUID;
import org.opengis.parameter.GeneralParameterValue;
import org.quartz.Scheduler;
import org.quartz.impl.JobDetailImpl;

import static org.geotoolkit.process.quartz.ProcessJob.*;

/**
 * Quartz job detail specialized for GeotoolKit process.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ProcessJobDetail extends JobDetailImpl {
        
    public ProcessJobDetail(final String factoryId, final String processId, final GeneralParameterValue parameters){
        this(factoryId+"."+processId+"-"+UUID.randomUUID(), null, factoryId, processId, parameters);
    }
    
    public ProcessJobDetail(final String name, final String group, final String factoryId, 
            final String processId, final GeneralParameterValue parameters){
        super(name, group, ProcessJob.class);
        if(group == null){
            setGroup(Scheduler.DEFAULT_GROUP);
        }
        getJobDataMap().put(KEY_FACTORY_ID, factoryId);
        getJobDataMap().put(KEY_PROCESS_ID, processId);
        getJobDataMap().put(KEY_PARAMETERS, parameters);
    }
    
}
