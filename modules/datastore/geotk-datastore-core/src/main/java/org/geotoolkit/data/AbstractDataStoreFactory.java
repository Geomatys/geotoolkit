/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.data.DataStoreFactorySpi.Param;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.parameter.FloatParameter;
import org.geotoolkit.parameter.Parameter;

import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;

/**
 * A best of toolkit for DataStoreFactory implementors.
 * <p>
 * Will also allow me to mess with the interface API without breaking every
 * last DataStoreFactorySpi out there.
 * </p>
 * <p>
 * The default implementations often hinge around the use of
 * getParameterInfo and the correct use of Param by your subclass.
 * </p>
 * <p>
 * You still have to implement a few methods:
 * </p>
 * <pre><code>
 * public DataSourceMetadataEnity createMetadata( Map params ) throws IOException {
 * 	    String host = (String) HOST.lookUp(params);
 *      String user = (String) USER.lookUp(params);
 *      Integer port = (Integer) PORT.lookUp(params);
 *      String database = (String) DATABASE.lookUp(params);
 *
 *      String description = "Connection to "+getDisplayName()+" on "+host+" as "+user ;
 *      return new DataSourceMetadataEnity( host+":"+port, database, description );
 * }</code></pre>
 *
 * @author Jody Garnett, Refractions Research
 * @source $URL$
 */
public abstract class AbstractDataStoreFactory extends Factory implements DataStoreFactorySpi {

    /** Default Implementation abuses the naming convention.
     * <p>
     * Will return <code>Foo</code> for
     * <code>org.geotools.data.foo.FooFactory</code>.
     * </p>
     * @return return display name based on class name
     */
    @Override
    public String getDisplayName() {
        String name = this.getClass().getName();

        name = name.substring(name.lastIndexOf('.'));
        if (name.endsWith("Factory")) {
            name = name.substring(0, name.length() - 7);
        } else if (name.endsWith("FactorySpi")) {
            name = name.substring(0, name.length() - 10);
        }
        return name;
    }

    /**
     * Default implementation verifies the Map against the Param information.
     * <p>
     * It will ensure that:
     * <ul>
     * <li>params is not null
     * <li>Everything is of the correct type (or upcovertable
     * to the correct type without Error)
     * <li>Required Parameters are present
     * </ul>
     * </p>
     * <p>
     * <p>
     * Why would you ever want to override this method?
     * If you want to check that a expected file exists and is a directory.
     * </p>
     * Overrride:
     * <pre><code>
     * public boolean canProcess( Map params ) {
     *     if( !super.canProcess( params ) ){
     *          return false; // was not in agreement with getParametersInfo
     *     }
     *     // example check
     *     File file = (File) DIRECTORY.lookup( params ); // DIRECTORY is a param
     *     return file.exists() && file.isDirectory();
     * }
     * </code></pre>
     * @param params
     * @return true if params is in agreement with getParametersInfo, override for additional checks.
     */
    @Override
    public boolean canProcess(Map params) {
        if (params == null) {
            return false;
        }
        Param arrayParameters[] = getParametersInfo();
        for (int i = 0; i < arrayParameters.length; i++) {
            Param param = arrayParameters[i];
            Object value;
            if (!params.containsKey(param.key)) {
                if (param.required) {
                    return false; // missing required key!
                } else {
                    continue;
                }
            }
            try {
                value = param.lookUp(params);
            } catch (IOException e) {
                // could not upconvert/parse to expected type!
                // even if this parameter is not required
                // we are going to refuse to process
                // these params
                return false;
            }
            if (value == null) {
                if (param.required) {
                    return (false);
                }
            } else {
                if (!param.type.isInstance(value)) {
                    return false; // value was not of the required type
                }
            }
        }
        return true;
    }

    public ParameterDescriptorGroup getParameters() {
        Param params[] = getParametersInfo();
        DefaultParameterDescriptor parameters[] = new DefaultParameterDescriptor[params.length];
        for (int i = 0; i < params.length; i++) {
            Param param = params[i];
            parameters[i] = new ParamDescriptor(params[i]);
        }
        Map properties = new HashMap();
        properties.put("name", getDisplayName());
        properties.put("remarks", getDescription());
        return new DefaultParameterDescriptorGroup(properties, parameters);
    }
}

class ParamDescriptor extends DefaultParameterDescriptor {

    private static final long serialVersionUID = 1L;
    Param param;

    public ParamDescriptor(Param param) {
        super(param.key, param.description, Object.class, param.sample, param.required);
        this.param = param;
    }

    @Override
    public ParameterValue createValue() {
        if (Double.TYPE.equals(getValueClass())) {
            return new FloatParameter(this) {

                protected Object valueOf(String text) throws IOException {
                    return param.handle(text);
                }
            };
        }
        return new Parameter(this) {

            protected Object valueOf(String text) throws IOException {
                return param.handle(text);
            }
        };
    }
};
