/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.parameter;

import java.util.Map;
import java.util.Collections;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.apache.sis.referencing.AbstractIdentifiedObject;


/**
 * The definition of a group of related parameters used by an operation method.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 4.00
 *
 * @see ParameterGroup
 * @see DefaultParameterDescriptor
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.parameter.DefaultParameterDescriptorGroup}.
 */
public class DefaultParameterDescriptorGroup extends org.apache.sis.parameter.DefaultParameterDescriptorGroup {
    /**
     * Constructs a group with the same values than the specified one. This copy constructor
     * may be used in order to wraps an arbitrary implementation into a Geotk one.
     *
     * @param group The descriptor to copy.
     *
     * @since 2.2
     */
    public DefaultParameterDescriptorGroup(final ParameterDescriptorGroup group) {
        super(group);
    }

    /**
     * Constructs a parameter group from a name.
     * This parameter group will be required exactly once.
     *
     * @param name The parameter group name.
     * @param parameters The {@linkplain #descriptors() parameter descriptors} for this group.
     */
    public DefaultParameterDescriptorGroup(final String name,
                                           final GeneralParameterDescriptor... parameters)
    {
        this(Collections.singletonMap(NAME_KEY, name), 1, 1, parameters);
    }

    /**
     * Constructs a parameter group from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map)
     * super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param minimumOccurs The {@linkplain #getMinimumOccurs minimum number of times}
     *        that values for this parameter group are required.
     * @param maximumOccurs The {@linkplain #getMaximumOccurs maximum number of times}
     *        that values for this parameter group are required.
     * @param parameters The {@linkplain #descriptors() parameter descriptors} for this group.
     */
    public DefaultParameterDescriptorGroup(final Map<String,?> properties,
                                           final int minimumOccurs,
                                           final int maximumOccurs,
                                           GeneralParameterDescriptor... parameters)
    {
        super(properties, minimumOccurs, maximumOccurs, parameters);
    }
}
