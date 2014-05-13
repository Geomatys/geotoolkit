/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.observation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.measure.unit.Unit;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Classes;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.util.collection.MapUtilities;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;

/**
 * Abstract Observation Store Factory.
 * 
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractObservationStoreFactory extends Factory implements ObservationStoreFactory {
 
    private static final String BUNDLE_PATH = "org/geotoolkit/data/bundle";

    /**
     * Identifier, Mandatory.
     * Subclasses should redeclared this parameter with a different default value.
     */
    public static final ParameterDescriptor<String> IDENTIFIER = createDescriptor("identifier",
                    new ResourceInternationalString(BUNDLE_PATH,"paramIdentifierAlias"),
                    new ResourceInternationalString(BUNDLE_PATH,"paramIdentifierRemarks"),
                    String.class,null,null,null,null,null,true);

    /**
     * Namespace, Optional.
     * Default namespace used for feature type.
     */
    public static final ParameterDescriptor<String> NAMESPACE = createDescriptor("namespace",
                    new ResourceInternationalString(BUNDLE_PATH,"paramNamespaceAlias"),
                    new ResourceInternationalString(BUNDLE_PATH,"paramNamespaceRemarks"),
                    String.class,null,null,null,null,null,false);
    
    /**
     * {@inheritDoc }
     *
     * @return a display name derivate from class name.
     */
    @Override
    public CharSequence getDisplayName() {
        String displayName = Classes.getShortClassName(this);
        if(displayName.endsWith("Factory")){
            displayName = displayName.substring(0, displayName.length() - 7);
        }
        return displayName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CharSequence getDescription() {
        return getDisplayName();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationStore open(Map<String, ? extends Serializable> params) throws DataStoreException {
        params = forceIdentifier(params);

        final ParameterValueGroup prm = FeatureUtilities.toParameter(params,getParametersDescriptor());
        if(prm == null){
            return null;
        }
        try{
            return open(prm);
        }catch(InvalidParameterValueException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObservationStore create(Map<String, ? extends Serializable> params) throws DataStoreException {
        params = forceIdentifier(params);

        final ParameterValueGroup prm = FeatureUtilities.toParameter(params,getParametersDescriptor());
        if(prm == null){
            return null;
        }
        try{
            return create(prm);
        }catch(InvalidParameterValueException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canProcess(Map params) {
        //check the identifier is set
        params = forceIdentifier(params);

        //ensure it's the valid identifier
        final Object id = params.get(IDENTIFIER.getName().getCode());
        try{
            final String expectedId = ((ParameterDescriptor<String>)getParametersDescriptor()
                .descriptor(IDENTIFIER.getName().getCode())).getDefaultValue();
            if(!expectedId.equals(id)){
                return false;
            }
        }catch(ParameterNotFoundException ex){
            //this feature store factory does not declare a identifier id
        }



        final ParameterValueGroup prm = FeatureUtilities.toParameter(params,getParametersDescriptor());
        if(prm == null){
            return false;
        }
        try{
            return canProcess(prm);
        }catch(InvalidParameterValueException ex){
            return false;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canProcess(final ParameterValueGroup params) {
        if(params == null){
            return false;
        }

        //check identifier value is exist
        final boolean validId = checkIdentifier(params);
        if(!validId){
            return false;
        }

        final ParameterDescriptorGroup desc = getParametersDescriptor();
        if(!desc.getName().getCode().equalsIgnoreCase(params.getDescriptor().getName().getCode())){
            return false;
        }

        final ConformanceResult result = Parameters.isValid(params, desc);
        return (result != null) && Boolean.TRUE.equals(result.pass());
    }
    
    /**
     * Set the identifier parameter in the map if not present.
     */
    private Map<String,Serializable> forceIdentifier(Map params){

        if(!params.containsKey(IDENTIFIER.getName().getCode())){
            //identifier is not specified, force it
            final ParameterDescriptorGroup desc = getParametersDescriptor();
            params = new HashMap<String, Serializable>(params);
            final Object value = ((ParameterDescriptor)desc.descriptor(IDENTIFIER.getName().getCode())).getDefaultValue();
            params.put(IDENTIFIER.getName().getCode(), (Serializable)value);
        }
        return params;
    }

    /**
     * Check if the Identifier parameter exist.
     * if it exist, it must be set to 'value' otherwise return false.
     * if not present, return true;
     * @return
     */
    protected boolean checkIdentifier(final ParameterValueGroup params){
        final String expectedId = ((ParameterDescriptor<String>)getParametersDescriptor()
                .descriptor(IDENTIFIER.getName().getCode())).getDefaultValue();

        for(GeneralParameterValue val : params.values()){
            if(val.getDescriptor().getName().getCode().equals(IDENTIFIER.getName().getCode())){
                final Object candidate = ((ParameterValue)val).getValue();
                return expectedId.equals(candidate);
            }
        }

        return true;
    }
    
    /**
     * Convinient method to open a parameter descriptor with an additional alias.
     */
    protected static <T> ParameterDescriptor<T> createDescriptor(final String name,
            final CharSequence alias, final CharSequence remarks, final Class<T> clazz,
            final T[] possibleValues, final T defaultValue, final Comparable<T> min,
            final Comparable<T> max, final Unit unit, final boolean requiered){
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IdentifiedObject.NAME_KEY, name);
        properties.put(IdentifiedObject.ALIAS_KEY, alias);
        properties.put(IdentifiedObject.REMARKS_KEY, remarks);
        return new DefaultParameterDescriptor(properties, clazz,
                possibleValues, defaultValue, min, max, unit, requiered);
    }
    
    /**
     * Create the identifier descriptor, and set only one valid value, the one in parameter.
     *
     * TODO : Maybe change the string in parameter to string array.
     * @param idValue the value to use for identifier.
     *
     * @return an identifier descriptor.
     */
    public static ParameterDescriptor<String> createFixedIdentifier(String idValue) {
            return new DefaultParameterDescriptor<String>(
            MapUtilities.buildMap(DefaultParameterDescriptor.NAME_KEY,
                                 IDENTIFIER.getName().getCode(),
                                 DefaultParameterDescriptor.REMARKS_KEY,
                                 IDENTIFIER.getRemarks()),
            String.class,
            new String[]{idValue},
            idValue,
            null,
            null,
            null,
            true);
    }
}
