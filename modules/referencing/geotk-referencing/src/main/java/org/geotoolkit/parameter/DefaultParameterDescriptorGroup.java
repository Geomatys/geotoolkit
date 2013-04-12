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
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Collections;
import java.util.LinkedList;
import net.jcip.annotations.Immutable;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterNameException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.util.ComparisonMode;

import static org.geotoolkit.util.Utilities.hash;
import static org.geotoolkit.util.Utilities.deepEquals;
import static org.geotoolkit.util.ArgumentChecks.ensureNonNull;
import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * The definition of a group of related parameters used by an operation method.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 3.18
 *
 * @see ParameterGroup
 * @see DefaultParameterDescriptor
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultParameterDescriptorGroup extends AbstractParameterDescriptor
        implements ParameterDescriptorGroup
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4613190550542423839L;

    /**
     * The maximum number of times that values for this parameter group or
     * parameter are required.
     */
    private final int maximumOccurs;

    /**
     * The {@linkplain #descriptors() parameter descriptors} for this group.
     */
    private final GeneralParameterDescriptor[] parameters;

    /**
     * A view of {@link #parameters} as an immutable list. Will be constructed
     * only when first needed.
     */
    private transient List<GeneralParameterDescriptor> asList;

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
        maximumOccurs = group.getMaximumOccurs();
        final List<GeneralParameterDescriptor> c = group.descriptors();
        parameters = c.toArray(new GeneralParameterDescriptor[c.size()]);
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
        super(properties, minimumOccurs, maximumOccurs);
        this.maximumOccurs = maximumOccurs;
        ensureNonNull("parameters", parameters);
        this.parameters = new GeneralParameterDescriptor[parameters.length];
        for (int i=0; i<parameters.length; i++) {
            this.parameters[i] = parameters[i];
            ensureNonNull("parameters", i, parameters);
        }
        /*
         * Ensure there is no conflict in parameter names.
         */
        parameters = this.parameters;
        for (int i=0; i<parameters.length; i++) {
            final String name = parameters[i].getName().getCode();
            for (int j=0; j<parameters.length; j++) {
                if (i != j) {
                    if (IdentifiedObjects.nameMatches(parameters[j], name)) {
                        throw new InvalidParameterNameException(Errors.format(
                                Errors.Keys.DUPLICATED_PARAMETER_NAME_$4,
                                parameters[j].getName().getCode(), j, name, i), name);
                    }
                }
            }
        }
    }

    /**
     * The maximum number of times that values for this parameter group are required.
     *
     * @see #getMinimumOccurs
     */
    @Override
    public int getMaximumOccurs() {
        return maximumOccurs;
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
     * A view of {@link #parameters} as an unmodifiable list. This class overides
     * {@link #contains} with a faster implementation based on {@link HashSet}.
     * It can help for map projection implementations (among other), which test
     * often for a parameter validity.
     */
    private static final class AsList extends UnmodifiableArrayList<GeneralParameterDescriptor> {
        /** For compatibility with different versions. */
        private static final long serialVersionUID = -2116304004367396735L;

        /** The element as a set. Will be constructed only when first needed. */
        private transient Set<GeneralParameterDescriptor> asSet;

        /** Constructs a list for the specified array. */
        public AsList(final GeneralParameterDescriptor[] array) {
            super(array);
        }

        /** Tests for the inclusion of the specified descriptor. */
        @Override
        public boolean contains(final Object object) {
            if (asSet == null) {
                asSet = new HashSet<>(this);
            }
            return asSet.contains(object);
        }
    }

    /**
     * Returns the parameters in this group.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public List<GeneralParameterDescriptor> descriptors() {
        if (asList == null) {
            if (parameters == null) {
                asList = Collections.emptyList();
            } else switch (parameters.length) {
                case 0:  asList = Collections.emptyList();                  break;
                case 1:  asList = Collections.singletonList(parameters[0]); break;
                case 2:  // fall through
                case 3:  asList = UnmodifiableArrayList.wrap(parameters);   break;
                default: asList = new AsList(parameters);                   break;
            }
        }
        return asList;
    }

    /**
     * Returns the first parameter in this group for the specified
     * {@linkplain Identifier#getCode identifier code}.
     *
     * @param  name The case insensitive identifier code of the parameter to search for.
     * @return The parameter for the given identifier code.
     * @throws ParameterNotFoundException if there is no parameter for the given identifier code.
     */
    @Override
    public GeneralParameterDescriptor descriptor(String name) throws ParameterNotFoundException {
        ensureNonNull("name", name);
        name = name.trim();
        List<DefaultParameterDescriptorGroup> subgroups = null;
        List<GeneralParameterDescriptor> parameters = descriptors();
        while (parameters != null) {
            for (final GeneralParameterDescriptor param : parameters) {
                if (IdentifiedObjects.nameMatches(param, name)) {
                    return param;
                }
                if (param instanceof DefaultParameterDescriptorGroup) {
                    if (subgroups == null) {
                        subgroups = new LinkedList<>();
                    }
                    assert !subgroups.contains(param) : param;
                    subgroups.add((DefaultParameterDescriptorGroup) param);
                }
            }
            /*
             * Looks in subgroups only after all parameters in the current group have been verified.
             * Search in a "first in, first out" basis.
             */
            if (isNullOrEmpty(subgroups)) {
                break;
            }
            parameters = subgroups.remove(0).descriptors();
        }
        throw new ParameterNotFoundException(Errors.format(Errors.Keys.UNKNOWN_PARAMETER_NAME_$1, name), name);
    }

    /**
     * Compares the specified object with this parameter group for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object, mode)) {
            switch (mode) {
                case STRICT: {
                    final DefaultParameterDescriptorGroup that = (DefaultParameterDescriptorGroup) object;
                    return maximumOccurs == that.maximumOccurs && Arrays.equals(parameters, that.parameters);
                }
                default: {
                    final ParameterDescriptorGroup that = (ParameterDescriptorGroup) object;
                    return deepEquals(descriptors(), that.descriptors(), mode);
                    // Note: maximumOccurs is tested by the parent class.
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(Arrays.hashCode(parameters), super.computeHashCode());
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
