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

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreProvider;
import org.geotoolkit.parameter.Parameters;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Abstract Observation Store Factory.
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractObservationStoreFactory extends DataStoreProvider  {

    private static final ParameterBuilder BUILDER = new ParameterBuilder();

    /**
     * Identifier, Mandatory.
     * Subclasses should redeclared this parameter with a different default value.
     */
    public static final ParameterDescriptor<String> IDENTIFIER = BUILDER
            .addName("identifier")
            .addName(Bundle.formatInternational(Bundle.Keys.paramIdentifierAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramIdentifierRemarks))
            .setRequired(true)
            .create(String.class, null);

    /**
     * Namespace, Optional.
     * Default namespace used for feature type.
     */
    public static final ParameterDescriptor<String> NAMESPACE = BUILDER
            .addName("namespace")
            .addName(Bundle.formatInternational(Bundle.Keys.paramNamespaceAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramNamespaceRemarks))
            .setRequired(false)
            .create(String.class, null);

    /**
     * Phenomenon identifier prefix, Optional.
     * Default prefix to add to phenomeon identifiers.
     */
    public static final String PHENOMENON_ID_BASE_NAME = "phenomenon-id-base";
    public static final ParameterDescriptor<String> PHENOMENON_ID_BASE = BUILDER
            .addName(PHENOMENON_ID_BASE_NAME)
            .setRemarks(PHENOMENON_ID_BASE_NAME)
            .setRequired(false)
            .create(String.class, null);

    /**
     * Observation template identifier prefix, Optional.
     * Default prefix to add to observation template identifiers.
     */
    public static final String OBSERVATION_TEMPLATE_ID_BASE_NAME = "observation-template-id-base";
    public static final ParameterDescriptor<String> OBSERVATION_TEMPLATE_ID_BASE = BUILDER
            .addName(OBSERVATION_TEMPLATE_ID_BASE_NAME)
            .setRemarks(OBSERVATION_TEMPLATE_ID_BASE_NAME)
            .setRequired(false)
            .create(String.class, null);

    /**
     * Observation identifier prefix, Optional.
     * Default prefix to add to observation identifiers.
     */
    public static final String OBSERVATION_ID_BASE_NAME = "observation-id-base";
    public static final ParameterDescriptor<String> OBSERVATION_ID_BASE = BUILDER
            .addName(OBSERVATION_ID_BASE_NAME)
            .setRemarks(OBSERVATION_ID_BASE_NAME)
            .setRequired(false)
            .create(String.class, null);

    /**
     * Sensor identifier prefix, Optional.
     * Default prefix to add to sensor identifiers.
     */
    public static final String SENSOR_ID_BASE_NAME = "sensor-id-base";
    public static final ParameterDescriptor<String> SENSOR_ID_BASE = BUILDER
            .addName(SENSOR_ID_BASE_NAME)
            .setRemarks(SENSOR_ID_BASE_NAME)
            .setRequired(false)
            .create(String.class, null);

    /**
     * @param params Store parameters candidate.
     *
     * @return true if the store can handle the specified parameters.
     */
    public boolean canProcess(final ParameterValueGroup params) {
        if(params == null){
            return false;
        }

        //check identifier value is exist
        final boolean validId = checkIdentifier(params);
        if(!validId){
            return false;
        }

        final ParameterDescriptorGroup desc = getOpenParameters();
        if(!desc.getName().getCode().equalsIgnoreCase(params.getDescriptor().getName().getCode())){
            return false;
        }

        final ConformanceResult result = Parameters.isValid(params, desc);
        return (result != null) && Boolean.TRUE.equals(result.pass());
    }

    /**
     * Check if the Identifier parameter exist.
     * if it exist, it must be set to 'value' otherwise return false.
     * if not present, return true;
     * @param params Store parameters candidate.
     *
     * @return true if the identifier param has been found and is correct.
     */
    protected boolean checkIdentifier(final ParameterValueGroup params){
        final String expectedId;
        try{
            expectedId = ((ParameterDescriptor<String>)getOpenParameters()
                .descriptor(IDENTIFIER.getName().getCode())).getDefaultValue();
        }catch(ParameterNotFoundException ex){
            //this feature store factory does not declare a identifier id
            return true;
        }

        for(GeneralParameterValue val : params.values()){
            if(val.getDescriptor().getName().getCode().equals(IDENTIFIER.getName().getCode())){
                final Object candidate = ((ParameterValue)val).getValue();
                return expectedId.equals(candidate);
            }
        }

        return true;
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
        return new ParameterBuilder()
                    .addName(IDENTIFIER.getName().getCode())
                    .setRemarks(IDENTIFIER.getRemarks().orElse(null))
                    .setRequired(true)
                    .createEnumerated(String.class, new String[]{idValue}, idValue);
    }
}
