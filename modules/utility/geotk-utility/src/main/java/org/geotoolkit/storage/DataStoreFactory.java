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
package org.geotoolkit.storage;

import org.opengis.parameter.ParameterValueGroup;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import static org.apache.sis.storage.DataStoreProvider.LOCATION;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.parameter.Parameters;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;

/**
 * Base interface for {@link org.apache.sis.storage.DataStore} factories. The aim is to get a factory with metadata which
 * would allow user to identify the data type managed by the factory.
 *
 * @author Alexis Manin (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public abstract class DataStoreFactory extends DataStoreProvider {

    /**
     * Identifier, Mandatory.
     * Subclasses should redeclared this parameter with a different default value.
     */
    public static final ParameterDescriptor<String> IDENTIFIER = new ParameterBuilder()
            .addName("identifier")
            .addName(Bundle.formatInternational(Bundle.Keys.paramIdentifierAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramIdentifierRemarks))
            .setRequired(true)
            .create(String.class, null);

    /**
     *
     * @return A metadata object giving general information about data support of this factory.
     */
    public abstract FactoryMetadata getMetadata();

    /**
     * Name suitable for display to end user.
     *
     * <p>
     * A display name for this data store type with several translations.
     * </p>
     *
     * <p>
     * Default Implementation abuses the naming convention.
     * Will return <code>Foo</code> for
     * <code>org.geotoolkit.data.foo.FooFactory</code>.
     * </p>
     *
     * @return A short name suitable for display in a user interface. Must be an International string.
     */
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

    /**
     * Describe the nature of the data source constructed by this factory.
     *
     * <p>
     * A description of this data store type with several translations.
     * </p>
     *
     * @return A human readable description that is suitable for inclusion in a
     *         list of available data sources.
     */
    public CharSequence getDescription() {
        return "";
    }

    /**
     * @param params
     * @return
     * @see org.geotoolkit.storage.DataStoreFactory#canProcess(java.util.Map)
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
            final ParameterValueGroup params = getOpenParameters().createValue();
            params.parameter(LOCATION).setValue(locationValue);

            if (canProcess(params)) {
                return new ProbeResult(true, null, null);
            }
        } catch(IllegalArgumentException ex) {}

        return new ProbeResult(false, null, null);
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
            final ParameterValueGroup params = getOpenParameters().createValue();
            params.parameter(LOCATION).setValue(locationValue);

            if (canProcess(params)) {
                return open(params);
            }
        } catch(IllegalArgumentException ex) {}

        throw new DataStoreException("Unsupported input");
    }

    /**
     * Open a link to the storage location.
     * This method is intended to open an existing storage.
     * <br/>
     * If the purpose is to create a new one storage use the create method :
     * @see DataStoreFactory#create(org.opengis.parameter.ParameterValueGroup)
     *
     * @param params
     * @return DataStore opened store
     * @throws DataStoreException if parameters are incorrect or connexion failed.
     */
    @Override
    public abstract DataStore open(ParameterValueGroup params) throws DataStoreException;

    /**
     * Create a new storage location.
     * This method is intended to create from scratch a new storage location.
     * <br/>
     * If the purpose is to open an already existing  storage use the open method :
     * @see DataStoreFactory#open(org.opengis.parameter.ParameterValueGroup)
     *
     * @param params
     * @return FeatureStore created store
     * @throws DataStoreException if parameters are incorrect or creation failed.
     */
    public DataStore create(ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Store creation not supported");
    }

    /**
     * @param params
     * @see #checkIdentifier(org.opengis.parameter.ParameterValueGroup)
     * @throws DataStoreException if identifier is not valid
     */
    protected void ensureCanProcess(final ParameterValueGroup params) throws DataStoreException{
        final boolean valid = canProcess(params);
        if(!valid){
            throw new DataStoreException("Parameter values not supported by this factory.");
        }
    }

    /**
     * Check if the Identifier parameter exist.
     * if it exist, it must be set to 'value' otherwise return false.
     * if not present, return true;
     * @param params
     * @return
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
                    .addName(IDENTIFIER.getAlias().iterator().next())
                    .setRemarks(IDENTIFIER.getRemarks())
                    .setRequired(true)
                    .createEnumerated(String.class, new String[]{idValue}, idValue);
    }

}
