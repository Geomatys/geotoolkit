/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.measure.unit.Unit;
import org.opengis.metadata.citation.Citation;

/**
 * ExtendedParameterDescriptor extent the {@link DefaultParameterDescriptor} class to add
 * a {@code userObject} Map that will contain others additionals parameters.
 * Add also a new constructor that take parameter name and remarks and others parameters like
 * validValues, minimum, maximum and units.
 * 
 * @author Quentin Boileau (Geomatys).
 * 
 * @see Parameter
 * @see DefaultParameterDescriptor
 * 
 * @module pending
 */
public class ExtendedParameterDescriptor<T> extends DefaultParameterDescriptor<T> {

    private Map<String, Object> userObject;
    
    /**
     * {@inheritDoc}
     */
    public ExtendedParameterDescriptor(final ExtendedParameterDescriptor<T> descriptor) {
        
        super(descriptor);
        this.userObject = descriptor.getUserObject();
    }

    /**
     * {@inheritDoc}
     * 
     * @param userObject map that contain additionnal value for the parameter.
     */
    public ExtendedParameterDescriptor(final String name, 
                                       final Class<T> valueClass, 
                                       final T[] validValues, 
                                       final T defaultValue, 
                                       final Map<String, Object> userObject) {
        
        super(name, valueClass, validValues, defaultValue);
        this.userObject = userObject;
    }

    /**
     * {@inheritDoc}
     * 
     * @param userObject map that contain additionnal value for the parameter.
     */
    public ExtendedParameterDescriptor(final String name, 
                                       final CharSequence remarks, 
                                       final Class<T> valueClass, 
                                       final T defaultValue, 
                                       final boolean required, 
                                       final Map<String, Object> userObject) {
        
        super(name, remarks, valueClass, defaultValue, required);
        this.userObject = userObject;
    }

    /**
     * {@inheritDoc}
     * 
     * @param userObject map that contain additionnal value for the parameter.
     */
    public ExtendedParameterDescriptor(final Map<String, ?> properties, 
                                       final Class<T> valueClass, 
                                       final T[] validValues, 
                                       final T defaultValue, 
                                       final Comparable<T> minimum, 
                                       final Comparable<T> maximum, 
                                       final Unit<?> unit, 
                                       final boolean required, 
                                       final Map<String, Object> userObject) {
        
        super(properties, valueClass, validValues, defaultValue, minimum, maximum, unit, required);
        this.userObject = userObject;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @param userObject map that contain additionnal value for the parameter.
     */
    public ExtendedParameterDescriptor(final String name, 
                                       final CharSequence remarks,  
                                       final Class<T> valueClass, 
                                       final T[] validValues, 
                                       final T defaultValue, 
                                       final Comparable<T> minimum, 
                                       final Comparable<T> maximum, 
                                       final Unit<?> unit, 
                                       final boolean required, 
                                       final Map<String, Object> userObject) {
        
        super(properties(name, remarks), valueClass, validValues, defaultValue, minimum, maximum, unit, required);
        this.userObject = userObject;
    }

    /**
     * {@inheritDoc}
     * 
     * @param userObject map that contain additionnal value for the parameter.
     */
    public ExtendedParameterDescriptor(final Citation authority, 
                                       final String name, 
                                       final Class<T> valueClass, 
                                       final T[] validValues, 
                                       final T defaultValue, 
                                       final Comparable<T> minimum, 
                                       final Comparable<T> maximum, 
                                       final Unit<?> unit, 
                                       final boolean required, 
                                       final Map<String, Object> userObject) {
        
        super(authority, name, valueClass, validValues, defaultValue, minimum, maximum, unit, required);
        this.userObject = userObject;
    }

    /**
     * Returns the user object map.
     *
     * @return The user object map.
     */
    public Map<String, Object> getUserObject() {
        return userObject;
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Map<String,CharSequence> properties(final String name, final CharSequence remarks) {
        final Map<String,CharSequence> properties;
        if (remarks == null ){
            properties = Collections.singletonMap(NAME_KEY, (CharSequence) name);
        } else {
            properties = new HashMap<String,CharSequence>(4);
            properties.put(NAME_KEY,    name);
            properties.put(REMARKS_KEY, remarks);
        }
        return properties;
    }
    
}
