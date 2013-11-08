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

import java.util.*;

import org.geotoolkit.referencing.IdentifiedObjects;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.parameter.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.util.InternationalString;

import javax.measure.unit.Unit;

/**
 * Utility methods for parameters.
 * @author Johann Sorel (Geomatys)
 * @author QuentinBoileau (Geomatys)
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
     * Get all parameters recursively for this class, do not create parameter if missing.
     */
    public static List<ParameterValue> getParameters(ParameterValueGroup group, Class type){

        final List<ParameterValue> result = new ArrayList<ParameterValue>();
        getParameters(group, type, result);
        return result;
    }

    private static void getParameters(ParameterValueGroup group, Class type, List<ParameterValue> result) {

        final List<GeneralParameterValue> params = getParameters(group);
        for(GeneralParameterValue p : params) {
            if(p instanceof ParameterValue) {
                final Class clazz = ((ParameterDescriptor)p.getDescriptor()).getValueClass();
                if (type.isAssignableFrom(clazz)) {
                    result.add((ParameterValue)p);
                }
            } else if (p instanceof ParameterValueGroup) {
                getParameters((ParameterValueGroup)p, type, result);
            }
        }
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


    /**
     * Search recursively all GeneralParameterDescriptors which have the requested name.
     * @param parameter Root GeneralParameterDescriptors
     * @param name parameter name to search
     * @param maxDepth the max depth to search for.
     * @return
     */
    public static List<GeneralParameterDescriptor> search(final GeneralParameterDescriptor parameter,
                                                          final String name, int maxDepth)
    {
        final List<GeneralParameterDescriptor> list = new ArrayList<GeneralParameterDescriptor>();
        search(parameter, name, maxDepth, list);
        return list;
    }

    /**
     * Implementation of the search algorithm. The result is stored in the supplied set.
     */
    private static void search(final GeneralParameterDescriptor parameter, final String name,
                               final int maxDepth, final Collection<GeneralParameterDescriptor> list)
    {
        if (maxDepth >= 0) {
            if (IdentifiedObjects.nameMatches(parameter, name)) {
                list.add(parameter);
            }
            if ((maxDepth != 0) && (parameter instanceof ParameterDescriptorGroup)) {
                for (final GeneralParameterDescriptor value : ((ParameterDescriptorGroup) parameter).descriptors()) {
                    search(value, name, maxDepth-1, list);
                }
            }
        }
    }

    /**
     * Search a GeneralParameterDescriptor in a path. 
     * Exemple : if separator is ':' and path is "input:group1:parameter"
     * the methode will search the GeneralParameterDescriptor named "parameter"
     * in ParameterDescriptorGroup "input" -> "group1".
     * 
     * @param parameter root ParameterDescriptorGroup
     * @param path path to GeneralParameterDescriptor
     * @param separator string used to separate groups and parameter names.
     * @return GeneralParameterDescriptor or null if not found
     */
    public static GeneralParameterDescriptor searchPath(final ParameterDescriptorGroup parameter,
                                                          final String path, final String separator) {
        final String[] pathParts = path.split(separator);
        
        //only one part, the parameter must be at first depth level.
        if (pathParts.length == 1) {
            return search(parameter, path, 1).get(0);
        }
        
        return searchPath(parameter, pathParts, 0);
    }
    
    /**
     * 
     * @param parameter
     * @param codes
     * @param index
     * @return 
     */
    private static GeneralParameterDescriptor searchPath(final ParameterDescriptorGroup parameter, 
                                                            final String[] codes, int index) {
        GeneralParameterDescriptor result = null;
        final String codePart = codes[index];

        for (GeneralParameterDescriptor param : parameter.descriptors()) {
            if (result != null) break;
            if (IdentifiedObjects.nameMatches(param, codePart)) {
                if (index == codes.length-1) {
                    result = param;
                } else {
                    if (param instanceof ParameterDescriptorGroup) {
                        result = searchPath((ParameterDescriptorGroup)param, codes, index+1);
                    }
                }
            }
        }
        return result;
        
    }
     
    /**
     * Add a GeneralParameterDescriptor to a ParameterDescriptorGroup.
     * 
     * @param root ParameterDescriptorGroup. Will not be modified.
     * @param newParam GeneralParameterDescriptor to add
     * @return the new ParameterDescriptorGroup
     */
    public static ParameterDescriptorGroup addParameterToDescriptorGroup(final ParameterDescriptorGroup root, final GeneralParameterDescriptor newParam) {

        ArgumentChecks.ensureNonNull("root", root);
        ArgumentChecks.ensureNonNull("newParam", newParam);

        final List<GeneralParameterDescriptor> parameters = new ArrayList<GeneralParameterDescriptor> (root.descriptors());
        parameters.add(newParam);

        int minOccurs = root.getMinimumOccurs();
        int maxOccurs = root.getMaximumOccurs();

        return createParameterDescriptorGroup(root.getName().getCode(), root.getRemarks(), minOccurs, maxOccurs, parameters);
    }

    /**
     * Remove a GeneralParameterDescriptor from a ParameterDescriptorGroup.
     *
     * @param root
     * @param toRemove GeneralParameterDescriptor to remove
     * @return the new ParameterDescriptorGroup
     */
    public static ParameterDescriptorGroup removeParameterToDescriptorGroup(final ParameterDescriptorGroup root, final GeneralParameterDescriptor toRemove) {

        ArgumentChecks.ensureNonNull("root", root);
        ArgumentChecks.ensureNonNull("toRemove", toRemove);

        final List<GeneralParameterDescriptor> parameters = new ArrayList<GeneralParameterDescriptor> (root.descriptors());
        parameters.remove(toRemove);
        int minOccurs = root.getMinimumOccurs();
        int maxOccurs = root.getMaximumOccurs();

        return createParameterDescriptorGroup(root.getName().getCode(), root.getRemarks(), minOccurs, maxOccurs, parameters);
    }


    /**
     * Regroup all parameters used to create a ParameterDescriptor.
     *
     * @param code parameter code String
     * @param remarks parameter description
     * @param valueClass parametrer type
     * @param validValues array of valids values
     * @param defaultValue default value Object
     * @param minValue min value Object
     * @param maxValue max value Object
     * @param unit parameter unit
     * @param required mandatory or not
     * @return a ParameterDescriptor
     */
    public static ParameterDescriptor createParameterDescriptor(final String code, final InternationalString remarks,
                                                                final Class valueClass, final Object[] validValues, final Object defaultValue, final Comparable minValue,
                                                                final Comparable maxValue, final Unit unit, final boolean required) {

        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(IdentifiedObject.NAME_KEY, code);
        paramMap.put(IdentifiedObject.REMARKS_KEY, remarks);

        return new DefaultParameterDescriptor(paramMap, valueClass, validValues, defaultValue, minValue, maxValue, unit, required);
    }


    /**
     * Regroup all parameters used to create a ParameterDescriptorGroup.
     *
     * @param code parameter code String
     * @param remarks parameter description
     * @param min min occurences
     * @param max max occurences
     * @param parameters list of children parameters.
     * @return a ParameterDescriptorGroup
     */
    public static ParameterDescriptorGroup createParameterDescriptorGroup(final String code, final InternationalString remarks,
                                                                          final int min, final int max, final List<GeneralParameterDescriptor> parameters) {

        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(IdentifiedObject.NAME_KEY, code);
        paramMap.put(IdentifiedObject.REMARKS_KEY, remarks);

        return new DefaultParameterDescriptorGroup(paramMap, min, max, parameters.toArray(new GeneralParameterDescriptor[parameters.size()]));
    }
    
    /**
     * Deep copy from a ParameterValueGroup source to a target ParameterValueGroup.
     * 
     * @param source
     * @param target 
     */
    public static void deepCopy(ParameterValueGroup source, ParameterValueGroup target) {
        
        for (GeneralParameterDescriptor paramDesc : source.getDescriptor().descriptors()) {
            final String paramCode = paramDesc.getName().getCode();
            
            if (paramDesc instanceof ParameterDescriptor) {
                final ParameterValue paramValue = source.parameter(paramCode);
                target.parameter(paramCode).setValue(paramValue.getValue());
            } else {
                
                final List<ParameterValueGroup> sourceValues = source.groups(paramCode);
                int targetValuesSize = target.groups(paramCode).size();
                
                if (targetValuesSize < sourceValues.size()) {
                    int toAdd = sourceValues.size() - targetValuesSize;
                    for (int i = 0; i < toAdd; i++) {
                        target.addGroup(paramCode);
                    }
                }
                
                final List<ParameterValueGroup> targetValues = target.groups(paramCode); 
                
                for (int i = 0; i < targetValues.size(); i++) {
                    deepCopy(sourceValues.get(i), targetValues.get(i));
                }
            }
        }
    }
}
