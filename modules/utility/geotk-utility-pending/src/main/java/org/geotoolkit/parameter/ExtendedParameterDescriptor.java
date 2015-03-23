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

import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.measure.Range;
import org.opengis.metadata.citation.Citation;

/**
 * ExtendedParameterDescriptor extent the {@link org.apache.sis.parameter.DefaultParameterDescriptor} class to add
 * a {@code userObject} Map that will contain others additional parameters.
 * Add also a new constructor that take parameter name and remarks and others parameters like
 * validValues, minimum, maximum and units.
 * 
 * @author Quentin Boileau (Geomatys).
 * 
 * @see org.apache.sis.parameter.DefaultParameterDescriptor
 * 
 * @module pending
 */
public class ExtendedParameterDescriptor<T> extends org.apache.sis.parameter.DefaultParameterDescriptor<T> {

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
     * @param userObject map that contain additional value for the parameter.
     */
    public ExtendedParameterDescriptor(final String name, 
                                       final Class<T> valueClass, 
                                       final T[] validValues, 
                                       final T defaultValue, 
                                       final Map<String, Object> userObject) {

        super(properties(name, null), 1, 1, valueClass, null, validValues, defaultValue);
        this.userObject = userObject;
    }

    /**
     * {@inheritDoc}
     * 
     * @param userObject map that contain additional value for the parameter.
     */
    public ExtendedParameterDescriptor(final String name, 
                                       final CharSequence remarks, 
                                       final Class<T> valueClass, 
                                       final T defaultValue, 
                                       final boolean required, 
                                       final Map<String, Object> userObject) {
        super(properties(name, remarks), (required ? 1 : 0), 1, valueClass, null, null, defaultValue);
        this.userObject = userObject;
    }

    /**
     * {@inheritDoc}
     *
     * @param name
     * @param remarks
     * @param minOccurs
     * @param maxOccurs
     * @param valueClass
     * @param defaultValue
     * @param userObject
     */
    public ExtendedParameterDescriptor(final String name,
                                       final CharSequence remarks,
                                       final int minOccurs,
                                       final int maxOccurs,
                                       final Class<T> valueClass,
                                       final T defaultValue,
                                       final Map<String, Object> userObject) {
        super(properties(name, remarks),minOccurs, maxOccurs, valueClass, null, null, defaultValue);
        this.userObject = userObject;
    }

    /**
     * {@inheritDoc}
     *
     * @param unit not used since ExtendedParameter extend SIS DefaultParameterDescriptor.
     * @param userObject map that contain additional value for the parameter.
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

        super(properties, (required ? 1 : 0), 1, valueClass, toRange(valueClass, minimum, maximum, unit), validValues, defaultValue);
        this.userObject = userObject;
    }

    /**
     * {@inheritDoc}
     *
     * @param unit not used since ExtendedParameter extend SIS DefaultParameterDescriptor.
     * @param userObject map that contain additional value for the parameter.
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

        super(properties(name, remarks), (required ? 1 : 0), 1, valueClass, toRange(valueClass, minimum, maximum, unit), validValues, defaultValue);
        this.userObject = userObject;
    }

    /**
     * {@inheritDoc}
     *
     * @param authority not used since ExtendedParameter extend SIS DefaultParameterDescriptor.
     * @param userObject map that contain additional value for the parameter.
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

        super(properties(name, null), (required ? 1 : 0), 1, valueClass, toRange(valueClass, minimum, maximum, unit), validValues, defaultValue);
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
            properties = new HashMap<>(4);
            properties.put(NAME_KEY,    name);
            properties.put(REMARKS_KEY, remarks);
        }
        return properties;
    }

    /**
     * Create a range from min/max values.
     *
     * @param valueClass
     * @param minimum value inclusive
     * @param maximum value inclusive
     * @param unit
     * @return a Range
     */
    private static <T> Range<?> toRange(Class<T> valueClass, Comparable<T> minimum, Comparable<T> maximum, Unit<?> unit) {
        if (unit != null && Number.class.isAssignableFrom(valueClass)) {
            return new MeasurementRange(valueClass, (Number)minimum, true, (Number)maximum, true, unit);
        } else {
            return new Range(valueClass, minimum, true, maximum, true);
        }
    }
    
}
