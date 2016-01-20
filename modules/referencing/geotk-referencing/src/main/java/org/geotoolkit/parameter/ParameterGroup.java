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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.lang.reflect.Field;

import org.opengis.metadata.Identifier;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterTypeException;
import org.opengis.parameter.InvalidParameterCardinalityException;
import org.apache.sis.parameter.DefaultParameterDescriptorGroup;

import org.geotoolkit.resources.Errors;
import org.apache.sis.referencing.IdentifiedObjects;

import static org.apache.sis.util.ArgumentChecks.*;
import static org.apache.sis.util.collection.Containers.hashMapCapacity;


/**
 * A group of related parameter values. The same group can be repeated more than once in
 * an {@linkplain org.opengis.referencing.operation.Operation operation} or higher level
 * {@link ParameterValueGroup}, if those instances contain different values of one or more
 * {@link ParameterValue}s which suitably distinguish among those groups.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.17
 *
 * @see DefaultParameterDescriptorGroup
 * @see Parameter
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.parameter.DefaultParameterValueGroup}.
 */
@Deprecated
public class ParameterGroup extends AbstractParameter implements ParameterValueGroup {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -1985309386356545126L;

    /**
     * An empty parameter value group. This group contains no parameter value.
     */
    public static final ParameterValueGroup EMPTY = new ParameterGroup(
            Collections.singletonMap(ParameterDescriptorGroup.NAME_KEY, "Void"));

    /**
     * The {@linkplain #values() parameter values} for this group.
     */
    private final ArrayList<GeneralParameterValue> values;

    /**
     * A view of {@link #values} as an checked list. Will be constructed only when first needed.
     * Note that elements can be added in this list and modified. Example:
     *
     * {@preformat java
     *     values().get(i).setValue(myValue);
     * }
     */
    private transient List<GeneralParameterValue> asList;

    /**
     * Constructs a parameter group from the specified descriptor.
     * All {@linkplain #values parameter values} will be initialized
     * to their default value.
     *
     * @param descriptor The descriptor for this group.
     */
    public ParameterGroup(final ParameterDescriptorGroup descriptor) {
        super(descriptor);
        final List<GeneralParameterDescriptor> parameters = descriptor.descriptors();
        values = new ArrayList<>(parameters.size());
        for (final GeneralParameterDescriptor element : parameters) {
            for (int count=element.getMinimumOccurs(); --count>=0;) {
                final GeneralParameterValue value = element.createValue();
                ensureNonNull("createValue", value);
                values.add(value);
            }
        }
    }

    /**
     * Constructs a parameter group from the specified descriptor and list of parameters.
     *
     * @param  descriptor The descriptor for this group.
     * @param  values The list of parameter values.
     * @throws IllegalStateException If the number of parameter values is not in the
     *         range of minimum and maximum occurrences declared in the descriptor.
     */
    public ParameterGroup(final ParameterDescriptorGroup descriptor,
                          final GeneralParameterValue... values)
    {
        super(descriptor);
        ensureNonNull("values", values);
        this.values = new ArrayList<>(Arrays.asList(values));
        final List<GeneralParameterDescriptor> parameters = descriptor.descriptors();
        final Map<GeneralParameterDescriptor,Integer> occurrences =
                new LinkedHashMap<>(hashMapCapacity(parameters.size()));
        for (final GeneralParameterDescriptor param : parameters) {
            ensureNonNull("parameters", param);
            occurrences.put(param, 0);
        }
        ensureValidOccurs(values, occurrences);
    }

    /**
     * Constructs a parameter group from the specified list of parameters.
     *
     * @param  properties The properties for the {@linkplain DefaultParameterDescriptorGroup
     *         operation parameter group} to construct from the list of parameters.
     * @param  values The list of parameter values.
     * @throws IllegalStateException If the number of parameter values is not in the
     *         range of minimum and maximum occurrences declared in the descriptor.
     */
    public ParameterGroup(final Map<String,?> properties, final GeneralParameterValue... values) {
        super(createDescriptor(properties, values));
        this.values = new ArrayList<>(Arrays.asList(values));
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     *
     * @throws IllegalStateException If the number of parameter values is not in the
     *         range of minimum and maximum occurrences declared in the descriptor.
     */
    private static ParameterDescriptorGroup createDescriptor(
            final Map<String,?> properties, final GeneralParameterValue[] values)
    {
        ensureNonNull("values", values);
        final Map<GeneralParameterDescriptor,Integer> occurrences =
                new LinkedHashMap<>(hashMapCapacity(values.length));
        for (int i=0; i<values.length; i++) {
            ensureNonNullElement("values", i, values);
            occurrences.put(values[i].getDescriptor(), 0);
        }
        ensureValidOccurs(values, occurrences);
        final Set<GeneralParameterDescriptor> descriptors = occurrences.keySet();
        return new DefaultParameterDescriptorGroup(properties, 1, 1,
                descriptors.toArray(new GeneralParameterDescriptor[descriptors.size()]));
    }

    /**
     * Makes sure that the number of occurrences of each values is inside the expected range.
     *
     * @param  values The list of parameter values.
     * @param  occurrences A map of the number of occurrences of a value for each descriptor.
     *         The key must be {@link GeneralParameterDescriptor} instances and the values
     *         must be {@link Integer} initialized with the 0 value.
     * @throws IllegalStateException If the number of parameter values is not in the
     *         range of minimum and maximum occurrences declared in the descriptor.
     */
    private static void ensureValidOccurs(final GeneralParameterValue[] values,
                                          final Map<GeneralParameterDescriptor,Integer> occurrences)
    {
        /*
         * Count the parameters occurrences.
         */
        for (int i=0; i<values.length; i++) {
            ensureNonNullElement("values", i, values);
            final GeneralParameterDescriptor descriptor = values[i].getDescriptor();
            final Integer count = occurrences.put(descriptor, 1);
            if (count == null) {
                final String name = getName(descriptor);
                throw new InvalidParameterTypeException(Errors.format(
                        Errors.Keys.IllegalDescriptorForParameter_1, name), name);
            }
            if (count != 0) {
                // Rarely enter in this block because there is usually at most 1 occurrence.
                occurrences.put(descriptor, count + 1);
            }
        }
        /*
         * Now check if the occurrences are in the expected ranges.
         */
        for (final Map.Entry<GeneralParameterDescriptor,Integer> entry : occurrences.entrySet()) {
            final GeneralParameterDescriptor descriptor = entry.getKey();
            final int count = entry.getValue();
            final int min   = descriptor.getMinimumOccurs();
            final int max   = descriptor.getMaximumOccurs();
            if (!(count>=min && count<=max)) {
                final String name = getName(descriptor);
                throw new InvalidParameterCardinalityException(Errors.format(
                        Errors.Keys.IllegalOccursForParameter_4, name, count, min, max), name);
            }
        }
    }

    /**
     * Returns the abstract definition of this group of parameters.
     */
    @Override
    public ParameterDescriptorGroup getDescriptor() {
        return (ParameterDescriptorGroup) super.getDescriptor();
    }

    /**
     * Returns the values in this group. Changes in this list are reflected on this
     * {@code ParameterValueGroup}. The returned list supports the
     * {@link List#add(Object) add} operation.
     */
    @Override
    public List<GeneralParameterValue> values() {
        if (asList == null) {
            asList = new ParameterValueList((ParameterDescriptorGroup) descriptor, values);
        }
        return asList;
    }

    /**
     * Returns the parameter value at the specified index.
     *
     * @param  index The zero-based index.
     * @return The parameter value at the specified index.
     * @throws IndexOutOfBoundsException if the specified index is out of bounds.
     */
    final GeneralParameterValue parameter(final int index) throws IndexOutOfBoundsException {
        return values.get(index);
    }

    /**
     * Returns the value in this group for the specified {@linkplain Identifier#getCode identifier code}.
     * If no {@linkplain ParameterValue parameter value} is found but a {@linkplain ParameterDescriptor
     * parameter descriptor} is found (which may occurs if the parameter is optional, i.e.
     * <code>{@linkplain ParameterDescriptor#getMinimumOccurs minimumOccurs} == 0</code>),
     * then a {@linkplain ParameterValue parameter value} is automatically created and initialized
     * to its {@linkplain ParameterDescriptor#getDefaultValue default value} (if any).
     * <p>
     * This convenience method provides a way to get and set parameter values by name. For example
     * the following idiom fetches a floating point value for the {@code "false_easting"} parameter:
     *
     * {@preformat java
     *     double value = parameter("false_easting").doubleValue();
     * }
     *
     * This method does not search recursively in subgroups. This is because more than one subgroup
     * may exist for the same {@linkplain ParameterDescriptorGroup descriptor}. The user must
     * {@linkplain #groups query all subgroups} and select explicitly the appropriate one to use.
     *
     * @param  name The case insensitive {@linkplain Identifier#getCode identifier code} of the
     *              parameter to search for.
     * @return The parameter value for the given identifier code.
     * @throws ParameterNotFoundException if there is no parameter value for the given identifier
     *         code.
     *
     * @see Parameters#getOrCreate(ParameterDescriptor, ParameterValueGroup)
     */
    @Override
    public ParameterValue<?> parameter(String name) throws ParameterNotFoundException {
        ensureNonNull("name", name);
        name = name.trim();
        for (final GeneralParameterValue value : values) {
            if (value instanceof ParameterValue<?>) {
                if (IdentifiedObjects.isHeuristicMatchForName(value.getDescriptor(), name)) {
                    return (ParameterValue<?>) value;
                }
            }
        }
        /*
         * No existing parameter found. Check if an optional parameter exists.
         * If such a descriptor is found, create it, add it to the list of values
         * and returns it.
         */
        for (final GeneralParameterDescriptor descriptor : getDescriptor().descriptors()) {
            if (descriptor instanceof ParameterDescriptor<?>) {
                if (IdentifiedObjects.isHeuristicMatchForName(descriptor, name)) {
                    final ParameterValue<?> value = ((ParameterDescriptor<?>) descriptor).createValue();
                    values.add(value);
                    return value;
                }
            }
        }
        throw new ParameterNotFoundException(Errors.format(Errors.Keys.UnknownParameterName_1, name), name);
    }

    /**
     * Returns all subgroups with the specified name. This method do not create new groups.
     * If the requested group is optional (i.e.
     * <code>{@linkplain ParameterDescriptor#getMinimumOccurs minimumOccurs} == 0</code>)
     * and no value were set, then this method returns an empty set.
     *
     * @param  name The case insensitive {@linkplain Identifier#getCode identifier code}
     *         of the parameter group to search for.
     * @return The set of all parameter group for the given identifier code.
     * @throws ParameterNotFoundException If no {@linkplain ParameterDescriptorGroup descriptor}
     *         was found for the given name.
     */
    @Override
    public List<ParameterValueGroup> groups(String name) throws ParameterNotFoundException {
        ensureNonNull("name", name);
        name = name.trim();
        final List<ParameterValueGroup> groups = new ArrayList<>(Math.min(values.size(), 10));
        for (final GeneralParameterValue value : values) {
            if (value instanceof ParameterValueGroup) {
                if (IdentifiedObjects.isHeuristicMatchForName(value.getDescriptor(), name)) {
                    groups.add((ParameterValueGroup) value);
                }
            }
        }
        /*
         * No groups were found. Check if the group actually exists (i.e. is declared in the
         * descriptor). If it doesn't exists, then an exception is thrown. If it exists (i.e.
         * it is simply an optional group not yet defined), then returns an empty list.
         */
        if (groups.isEmpty()) {
            final GeneralParameterDescriptor check =
                    ((ParameterDescriptorGroup) descriptor).descriptor(name);
            if (!(check instanceof ParameterDescriptorGroup)) {
                throw new ParameterNotFoundException(Errors.format(
                        Errors.Keys.UnknownParameterName_1, name), name);
            }
        }
        return groups;
    }

    /**
     * Creates a new group of the specified name. The specified name must be the
     * {@linkplain Identifier#getCode identifier code} of a {@linkplain ParameterDescriptorGroup
     * descriptor group}.
     *
     * @param  name The case insensitive {@linkplain Identifier#getCode identifier code} of the
     *              parameter group to create.
     * @return A newly created parameter group for the given identifier code.
     * @throws ParameterNotFoundException If no {@linkplain ParameterDescriptorGroup descriptor}
     *         was found for the given name.
     * @throws InvalidParameterCardinalityException If this parameter group already contains the
     *         {@linkplain ParameterDescriptorGroup#getMaximumOccurs maximum number of occurrences}
     *         of subgroups of the given name.
     */
    @Override
    public ParameterValueGroup addGroup(String name)
            throws ParameterNotFoundException, InvalidParameterCardinalityException
    {
        final GeneralParameterDescriptor check =
                ((ParameterDescriptorGroup) descriptor).descriptor(name);
        if (!(check instanceof ParameterDescriptorGroup)) {
            throw new ParameterNotFoundException(Errors.format(
                    Errors.Keys.UnknownParameterName_1, name), name);
        }
        int count = 0;
        for (final GeneralParameterValue value : values) {
            if (IdentifiedObjects.isHeuristicMatchForName(value.getDescriptor(), name)) {
                count++;
            }
        }
        if (count >= check.getMaximumOccurs()) {
            throw new InvalidParameterCardinalityException(Errors.format(
                    Errors.Keys.TooManyOccurrences_2, name, count), name);
        }
        final ParameterValueGroup value = ((ParameterDescriptorGroup) check).createValue();
        values.add(value);
        return value;
    }

    /**
     * Compares the specified object with this parameter for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final ParameterGroup that = (ParameterGroup) object;
            return Objects.equals(this.values, that.values);
        }
        return false;
    }

    /**
     * Returns a hash value for this parameter.
     *
     * @return The hash code value. This value doesn't need to be the same
     *         in past or future versions of this class.
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ values.hashCode();
    }

    /**
     * Returns a deep copy of this group of parameter values.
     * Included parameter values and subgroups are cloned recursively.
     *
     * @return A copy of this group of parameter values.
     */
    @Override
    @SuppressWarnings("unchecked")
    public ParameterGroup clone() {
        final ParameterGroup copy = (ParameterGroup) super.clone();
        try {
            // Set the 'provider' field using reflection, because this field is final. This is
            // a legal usage for cloning according Field.set(...) documentation in J2SE 1.5.
            final Field field = ParameterGroup.class.getDeclaredField("values");
            field.setAccessible(true);
            field.set(copy, values.clone());
        } catch (ReflectiveOperationException cause) {
            throw new AssertionError(cause);
        }
        for (int i=copy.values.size(); --i>=0;) {
            copy.values.set(i, copy.values.get(i).clone());
        }
        copy.asList = null;
        return copy;
    }
}
