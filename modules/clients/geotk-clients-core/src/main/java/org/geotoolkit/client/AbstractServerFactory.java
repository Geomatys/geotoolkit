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
import java.util.Map;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.metadata.iso.quality.DefaultConformanceResult;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Abstract Server factory.
 *
 * @author Johann Sorel
 * @module pending
 */
public abstract class AbstractServerFactory extends Factory implements ServerFactory {

    /**
     * Server URL, Mandatory.
     */
    public static final ParameterDescriptor<URL> URL =
            new DefaultParameterDescriptor<URL>("url","Server url",URL.class,null,true);
    /**
     * Security, Optional.
     */
    public static final ParameterDescriptor<ClientSecurity> SECURITY =
            new DefaultParameterDescriptor<ClientSecurity>("security","Server security",ClientSecurity.class,null,false);
    
    /** 
     * Default Implementation abuses the naming convention.
     * <p>
     * Will return <code>Foo</code> for
     * <code>org.geotoolkit.data.foo.FooFactory</code>.
     * </p>
     * @return return display name based on class name
     */
    @Override
    public String getDisplayName() {
        String name = this.getClass().getName();

        name = name.substring(name.lastIndexOf('.'));
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
    public String getDescription() {
        return getDisplayName();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Server create(final Map<String, ? extends Serializable> params) throws DataStoreException {
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
    public boolean canProcess(final Map<String, ? extends Serializable> params) {
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

}
