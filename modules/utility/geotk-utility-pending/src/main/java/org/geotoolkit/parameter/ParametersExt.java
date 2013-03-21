/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.parameter;

import java.util.ArrayList;
import java.util.List;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Utility methods for parameters.
 * @author Johann Sorel (Geomatys)
 */
public final class ParametersExt {
    
    private ParametersExt(){}
    
    /**
     * List of all parameters, ParameterValue AND ParameterGroups.
     * Live list modifiable.
     */
    public static List<GeneralParameterValue> getParameters(ParameterValueGroup group){
        return group.values();
    }
        
    /**
     * Get the first parameter for this name, do not create parameter if missing.
     */
    public static GeneralParameterValue getParameter(ParameterValueGroup group,String name){
        final List<GeneralParameterValue> params = getParameters(group);
        for(GeneralParameterValue p : params){
            if(p.getDescriptor().getName().getCode().equalsIgnoreCase(name)){
                return p;
            }
        }
        return null;
    }
    
    /**
     * List of all parameters, ParameterValue OR ParameterGroups of this name.
     */
    public static List<GeneralParameterValue> getParameters(ParameterValueGroup group,String name){
        final List<GeneralParameterValue> params = getParameters(group);
        final List<GeneralParameterValue> result = new ArrayList<GeneralParameterValue>();
        for(GeneralParameterValue p : params){
            if(p.getDescriptor().getName().getCode().equalsIgnoreCase(name)){
                result.add(p);
            }
        }
        return result;
    }
    
    /**
     * Get the first parameter for this name, create parameter if missing.
     */
    public static GeneralParameterValue getOrCreateParameter(ParameterValueGroup group,String name){
        GeneralParameterValue param = getParameter(group, name);
        if(param != null) return param;
        //create it
        param = group.getDescriptor().descriptor(name).createValue();
        getParameters(group).add(param);
        return param;
    }
    
    /**
     * Get parameter value, do not create parameter if missing.
     */
    public static ParameterValue<?> getValue(ParameterValueGroup group,String name){
        final List<GeneralParameterValue> params = getParameters(group);
        for(GeneralParameterValue p : params){
            if(p instanceof ParameterValue && p.getDescriptor().getName().getCode().equalsIgnoreCase(name)){
                return (ParameterValue<?>) p;
            }
        }
        return null;
    }
    
    /**
     * Get parameter value, create parameter if missing.
     */
    public static ParameterValue<?> getOrCreateValue(ParameterValueGroup group,String name){
        ParameterValue param = getValue(group, name);
        if(param != null) return param;
        //create it
        param = (ParameterValue) group.getDescriptor().descriptor(name).createValue();
        getParameters(group).add(param);
        return param;
    }
    
    /**
     * Get parameter group, do not create parameter if missing.
     */
    public static ParameterValueGroup getGroup(ParameterValueGroup group,String name){
        final List<GeneralParameterValue> params = getParameters(group);
        for(GeneralParameterValue p : params){
            if(p instanceof ParameterValueGroup && p.getDescriptor().getName().getCode().equalsIgnoreCase(name)){
                return (ParameterValueGroup) p;
            }
        }
        return null;
    }
    
    /**
     * Get parameter group list, do not create parameter if missing.
     */
    public static List<ParameterValueGroup> getGroups(ParameterValueGroup group,String name){
        final List<GeneralParameterValue> params = getParameters(group);
        final List<ParameterValueGroup> result = new ArrayList<ParameterValueGroup>();
        for(GeneralParameterValue p : params){
            if(p instanceof ParameterValueGroup && p.getDescriptor().getName().getCode().equalsIgnoreCase(name)){
                result.add((ParameterValueGroup)p);
            }
        }
        return result;
    }
    
    /**
     * Get parameter group, create if missing, return first one otherwise !
     */
    public static ParameterValueGroup getOrCreateGroup(ParameterValueGroup group,String name){
        ParameterValueGroup param = getGroup(group, name);
        if(param != null) return param;
        //create it
        param = (ParameterValueGroup) group.getDescriptor().descriptor(name).createValue();
        getParameters(group).add(param);
        return param;
    }
    
    /**
     * create and return a parameter group.
     */
    public static ParameterValueGroup createGroup(ParameterValueGroup group,String name){
        ParameterValueGroup param = (ParameterValueGroup) group.getDescriptor().descriptor(name).createValue();
        getParameters(group).add(param);
        return param;
    }
    
}
