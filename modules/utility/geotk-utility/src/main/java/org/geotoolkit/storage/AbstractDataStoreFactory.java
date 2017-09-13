/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2016, Geomatys
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
package org.geotoolkit.storage;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.metadata.iso.quality.DefaultConformanceResult;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.parameter.Parameters;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 * General implementation of methods for DataStoreFactory implementations.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractDataStoreFactory extends DataStoreFactory {

    /**
     * Identifier, Mandatory.
     * Subclasses should redeclared this parameter with a different default value.
     */
    public static final ParameterDescriptor<String> IDENTIFIER = new ParameterBuilder()
            .addName("identifier")
            .setRequired(true)
            .create(String.class, null);

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
                    .setRemarks(IDENTIFIER.getRemarks())
                    .setRequired(true)
                    .createEnumerated(String.class, new String[]{idValue}, idValue);
    }

    /** Default Implementation abuses the naming convention.
     * <p>
     * Will return <code>Foo</code> for
     * <code>org.geotoolkit.data.foo.FooFactory</code>.
     * </p>
     * @return return display name based on class name
     */
    @Override
    public CharSequence getDisplayName() {
        String name = this.getClass().getName();

        name = name.substring(name.lastIndexOf('.')+1);
        if (name.endsWith("Factory")) {
            name = name.substring(0, name.length() - 7);
        }
        return name;
    }

    @Override
    public String getShortName() {
        return getDisplayName().toString();
    }


    @Override
    public org.apache.sis.storage.DataStore open(StorageConnector connector) throws DataStoreException {
        GeneralParameterDescriptor desc;
        try {
            desc = getOpenParameters().descriptor(LOCATION);
        } catch (ParameterNotFoundException e) {
            throw new DataStoreException("Unsupported input");
        }

        if (!(desc instanceof ParameterDescriptor)) {
            throw new DataStoreException("Unsupported input");
        }

        try {
            final Object locationValue = connector.getStorageAs(((ParameterDescriptor)desc).getValueClass());
            final Map params = Collections.singletonMap(LOCATION, locationValue);
            if (canProcess(params)) {
                return open(params);
            }
        } catch(IllegalArgumentException ex) {}

        throw new DataStoreException("Unsupported input");
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {

        GeneralParameterDescriptor desc;
        try {
            desc = getOpenParameters().descriptor(LOCATION);
        } catch (ParameterNotFoundException e) {
            return new ProbeResult(false, null, null);
        }

        if (!(desc instanceof ParameterDescriptor)) {
            return new ProbeResult(false, null, null);
        }

        try {
            final Object locationValue = connector.getStorageAs(((ParameterDescriptor)desc).getValueClass());
            final Map params = Collections.singletonMap(LOCATION, locationValue);
            if (canProcess(params)) {
                return new ProbeResult(true, null, null);
            }
        } catch(IllegalArgumentException ex) {}

        return new ProbeResult(false, null, null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStore open(Map<String, ? extends Serializable> params) throws DataStoreException {
        final ParameterValueGroup prm;
        try{
            prm = Parameters.toParameter(forceIdentifier(params), getOpenParameters());
        }catch(IllegalArgumentException ex){
            throw new DataStoreException(ex);
        }
        return open(prm);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStore create(Map<String, ? extends Serializable> params) throws DataStoreException {
        final ParameterValueGroup prm;
        try{
            prm = Parameters.toParameter(forceIdentifier(params), getOpenParameters());
        }catch(IllegalArgumentException ex){
            throw new DataStoreException(ex);
        }
        return create(prm);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canProcess(Map<String, ? extends Serializable> params) {
        params = forceIdentifier(params);

        final ParameterValueGroup prm = Parameters.toParameter(params, getOpenParameters());
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

        final ParameterDescriptorGroup desc = getOpenParameters();
        if(!desc.getName().getCode().equalsIgnoreCase(params.getDescriptor().getName().getCode())){
            return false;
        }

        final ConformanceResult result = Parameters.isValid(params, desc);
        return (result != null) && Boolean.TRUE.equals(result.pass());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ConformanceResult availability() {
        DefaultConformanceResult result =  new DefaultConformanceResult();
        result.setPass(Boolean.TRUE);
        return result;
    }

    /**
     * Set the identifier parameter in the map if not present.
     */
    protected Map<String,Serializable> forceIdentifier(Map params){

        if(!params.containsKey(IDENTIFIER.getName().getCode())){
            //identifier is not specified, force it
            final ParameterDescriptorGroup desc = getOpenParameters();
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
        final String expectedId = ((ParameterDescriptor<String>)getOpenParameters()
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
     * @see #checkIdentifier(org.opengis.parameter.ParameterValueGroup)
     * @throws DataStoreException if identifier is not valid
     */
    protected void ensureCanProcess(final ParameterValueGroup params) throws DataStoreException{
        final boolean valid = canProcess(params);
        if(!valid){
            throw new DataStoreException("Parameter values not supported by this factory.");
        }
    }

}
