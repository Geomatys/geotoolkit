/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.map;

import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.apache.sis.util.logging.Logging;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;

/**
 * A collection which is calculated on the fly by a process.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ProcessedCollection extends AbstractCollection{

    private static final Logger LOGGER = Logging.getLogger(ProcessedCollection.class);
    
    private ProcessDescriptor processDescriptor;
    private ParameterValueGroup inputParam;
    private String resultParam;
    private long lifespan = 0;
    
    private ParameterValueGroup result;
    private long lastCall = 0;
    
    public ProcessedCollection(){
        
    }

    /**
     * Get ProcessDescriptor called to obtain the result collection.
     * @return ProcessDescriptor
     */
    public ProcessDescriptor getProcessDescriptor() {
        return processDescriptor;
    }

    /**
     * Set ProcessDescriptor called to obtain the result collection.
     * @return ProcessDescriptor
     */
    public void setProcessDescriptor(ProcessDescriptor processDescriptor) {
        this.processDescriptor = processDescriptor;
    }

    /**
     * Get ParameterValueGroup used when executing the process.
     * @return ProcessDescriptor
     */
    public ParameterValueGroup getInputParameters() {
        return inputParam;
    }

    /**
     * Set ParameterValueGroup used when executing the process.
     * @return ProcessDescriptor
     */
    public void setInputParameters(ParameterValueGroup inputParam) {
        this.inputParam = inputParam;
    }

    /**
     * Get name of the parameter is the result parameterValueGroup to use
     * as a Collection.
     * @return String parameter name
     */
    public String getResultParameter() {
        return resultParam;
    }

    /**
     * Set name of the parameter is the result parameterValueGroup to use
     * as a Collection.
     * @return String parameter name
     */
    public void setResultParameter(String resultParam) {
        this.resultParam = resultParam;
    }

    /**
     * Lapse of time the result of the process is valid.
     * A negative value means the lifespan is infinite.
     * A value of zero will cause the process to be executed on each access.
     * It is recommanded to have a value superior to 5.000 milliseconds to avoid
     * to much processing, yet this higly depends on the process itself.
     * 
     * @return long lifespan in millisecond
     */
    public long getLifespan() {
        return lifespan;
    }
    
    /**
     * {@see ProcessedCollection.getLifespan}
     * @param lifespan 
     */
    public void setLifespan(long lifespan) {
        this.lifespan = lifespan;
    }

    private Collection getResult(){
        if(processDescriptor == null || inputParam == null || resultParam == null){
            LOGGER.log(Level.WARNING, "ProcessedCollection not configured.");
            return Collections.emptyList();
        }
        
        //check lifespan
        if(result != null && lifespan>=0 && (System.currentTimeMillis()-lastCall)>lifespan ){
            result = null;
        }
        
        //execute process if requiered
        if(result == null){
            lastCall = System.currentTimeMillis();
            try{
                final Process process = processDescriptor.createProcess(inputParam);
                result = process.call();
            }catch(Exception ex){
                //we should not catch exception, but we don't want to break the stack because of a
                //uncorrect process script (groovy, javascript,...)
                LOGGER.log(Level.WARNING, "Processing failed : "+ex.getMessage(), ex);
            }
        }
        
        if(result == null){
            return Collections.EMPTY_LIST;
        }else{
            Object col = null;
            try{
                col = result.parameter(resultParam).getValue();
            }catch(ParameterNotFoundException ex){
                LOGGER.log(Level.WARNING, "Parameter "+resultParam+" is not in the result parameters.");
            }
            
            if(col == null){
                col = Collections.EMPTY_LIST;
            }else if(col instanceof Collection){
                //do nothing
            }else if(col.getClass().isArray()){
                final int size = Array.getLength(col);
                final List lst = new ArrayList(size);
                for(int i=0;i<size;i++){
                    lst.add(Array.get(col, i));
                }                
                col = lst;
            }else{
                //create a singleton collection
                col = Collections.singletonList(col);
            }
            
            return (Collection)col;
        }
        
    }
    
    @Override
    public Iterator iterator() {
        final Collection col = getResult();
        return col.iterator();
    }

    @Override
    public int size() {
        final Collection col = getResult();
        return col.size();
    }

}
