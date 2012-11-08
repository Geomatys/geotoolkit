/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *    (C) 2012, Johann Sorel
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
package org.geotoolkit.client;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.metadata.iso.quality.DefaultConformanceResult;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ResourceInternationalString;
import org.geotoolkit.util.collection.MapUtilities;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * Abstract Server factory.
 *
 * @author Johann Sorel
 * @module pending
 */
public abstract class AbstractServerFactory extends Factory implements ServerFactory {

    /**
     * commonly used translation for version parameters.
     */
    public static final InternationalString I18N_VERSION = new ResourceInternationalString("org/geotoolkit/client/bundle","version");
    
    /**
     * Identifier, Mandatory.
     * Subclasses should redeclared this parameter with a different default value.
     */
    public static final ParameterDescriptor<String> IDENTIFIER =
            new DefaultParameterDescriptor<String>("identifier",
                    new ResourceInternationalString("org/geotoolkit/client/bundle", "identifier"),
                    String.class,null,true);
    
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
                                 AbstractServerFactory.IDENTIFIER.getRemarks()),
            String.class, 
            new String[]{idValue}, 
            idValue,
            null,
            null,
            null,
            true);
    }
    
    /**
     * Server URL, Mandatory.
     */
    public static final ParameterDescriptor<URL> URL =
            new DefaultParameterDescriptor<URL>("url",
                    new ResourceInternationalString("org/geotoolkit/client/bundle", "url"),
                    URL.class,null,true);
    /**
     * Security, Optional.
     */
    public static final ParameterDescriptor<ClientSecurity> SECURITY =
            new DefaultParameterDescriptor<ClientSecurity>("security",
                    new ResourceInternationalString("org/geotoolkit/client/bundle", "security"),
                    ClientSecurity.class,null,false);
    
    /**
     * Cache images in memory, Optional.
     */
    public static final ParameterDescriptor<Boolean> IMAGE_CACHE =
            new DefaultParameterDescriptor<Boolean>("imagecache", 
                    new ResourceInternationalString("org/geotoolkit/client/bundle", "imageCache"),
                    Boolean.class,false,false);
    /**
     * Use NIO when possible for queries, Optional.
     */
    public static final ParameterDescriptor<Boolean> NIO_QUERIES =
            new DefaultParameterDescriptor<Boolean>("nio",
                    new ResourceInternationalString("org/geotoolkit/client/bundle", "nio"),
                    Boolean.class,Boolean.FALSE,false);
    
    /** 
     * Default Implementation abuses the naming convention.
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
    
    /** 
     * Default Implementation returns the display name.
     * @return return display name
     */
    @Override
    public CharSequence getDescription() {
        return getDisplayName();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Server open(Map<String, ? extends Serializable> params) throws DataStoreException {
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
    public boolean canProcess(Map<String, ? extends Serializable> params) {
        params = forceIdentifier(params);
        
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
        
        final ConformanceResult result = Parameters.isValid(params, getParametersDescriptor());
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
     * @see #checkIdentifier(org.opengis.parameter.ParameterValueGroup) 
     * @throws DataStoreException if identifier is not valid
     */
    protected void checkCanProcessWithError(final ParameterValueGroup params) throws DataStoreException{
        final boolean valid = canProcess(params);
        if(!valid){
            throw new DataStoreException("Parameter values not supported by this factory.");
        }
    }
    
}
