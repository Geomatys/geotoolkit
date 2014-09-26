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
import net.jcip.annotations.Immutable;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.geotoolkit.metadata.Citations;


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
@Immutable
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
        this(Collections.singletonMap(NAME_KEY, name), parameters);
    }

    /**
     * Constructs a parameter group from a name and an authority.
     * This parameter group will be required exactly once.
     *
     * @param authority The authority (e.g. {@link Citations#OGC OGC}).
     * @param name The parameter group name.
     * @param parameters The {@linkplain #descriptors() parameter descriptors} for this group.
     *
     * @since 2.2
     */
    public DefaultParameterDescriptorGroup(final Citation authority, final String name,
                                           final GeneralParameterDescriptor... parameters)
    {
        this(Collections.singletonMap(NAME_KEY, new NamedIdentifier(authority, name)), parameters);
    }

    /**
     * Constructs a parameter group from a set of properties.
     * This parameter group will be required exactly once. The properties map is given unchanged to
     * the {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param parameters The {@linkplain #descriptors() parameter descriptors} for this group.
     */
    public DefaultParameterDescriptorGroup(final Map<String,?> properties,
                                           final GeneralParameterDescriptor... parameters)
    {
        this(properties, 1, 1, parameters);
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

    /**
     * Creates a new instance of {@linkplain ParameterGroup parameter value group}
     * initialized with the {@linkplain ParameterDescriptor#getDefaultValue default values}.
     * The {@linkplain ParameterGroup#getDescriptor parameter descriptor} for the
     * created group will be {@code this} object.
     */
    @Override
    public ParameterValueGroup createValue() {
        return new ParameterGroup(this);
    }

    /**
     * Returns a string representation of this descriptor. The default implementation
     * delegates to {@link ParameterWriter#toString(ParameterDescriptorGroup)}, which
     * will format this descriptor in a table.
     *
     * @since 3.17
     */
    @Override
    public String toString() {
        return ParameterWriter.toString(this);
    }
}
