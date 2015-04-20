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
 */
package org.geotoolkit.parameter;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.measure.unit.Unit;

import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.opengis.util.InternationalString;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.metadata.Identifier;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Descriptions;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.metadata.iso.quality.DefaultConformanceResult;
import org.apache.sis.util.iso.DefaultNameSpace;

import static org.apache.sis.parameter.Parameters.castOrWrap;


/**
 * Helper methods for working with the parameter API from {@link org.opengis.parameter} package.
 * This class provides methods for {@linkplain #search searching}, {@linkplain #ensureSet setting
 * a value}, <i>etc.</i> from a parameter name.
 * Names in simple {@code String} form are preferred over the full {@code ParameterDescriptor}
 * object because:
 *
 * <ul>
 *   <li><p>The parameter descriptor may not be always available. For example a user may looks for
 *       the {@code "semi_major"} axis length (because it is documented in OGC specification under
 *       that name) but doesn't know and doesn't care about who is providing the implementation. In
 *       such case, he doesn't have the parameter's descriptor. He only have the parameter's name,
 *       and creating a descriptor from that name (a descriptor independent of any implementation)
 *       is tedious.</p></li>
 *   <li><p>Parameter descriptors are implementation-dependent. For example if a user searches for
 *       the above-cited {@code "semi_major"} axis length using the {@linkplain
 *       org.geotoolkit.referencing.operation.provider.MapProjection#SEMI_MAJOR Geotk's descriptor}
 *       for that parameter, we will fail to find this parameter in any alternative
 *       {@link ParameterValueGroup} implementations. This is against GeoAPI's inter-operability goal.</p></li>
 * </ul>
 *
 * When a method in this class expects a full {@code ParameterDescriptor} object in argument
 * (for example {@link #value(ParameterDescriptor, ParameterValueGroup)}), the
 * {@linkplain ParameterDescriptor#getName descriptor name} and alias are used for the search - this
 * class does <strong>not</strong> look for strictly equal {@code ParameterDescriptor} objects.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.1
 * @module
 */
public final class Parameters extends Static {
    /**
     * Small number for floating point comparisons.
     */
    private static final double EPS = 1E-8;

    /**
     * The explanation for conformance results.
     */
    private static final InternationalString EXPLAIN =
            Descriptions.formatInternational(Descriptions.Keys.CONFORMANCE_MEANS_VALID_PARAMETERS);

    /**
     * An empty parameter group. This group contains no parameters.
     *
     * @deprecated Will be removed.
     */
    @Deprecated
    public static final ParameterDescriptorGroup EMPTY_GROUP =
            new DefaultParameterDescriptorGroup(Vocabulary.format(Vocabulary.Keys.EMPTY));

    /**
     * Do not allow instantiation of this utility class.
     */
    private Parameters() {
    }

    /**
     * Checks a parameter value against its {@linkplain ParameterDescriptor parameter descriptor}.
     * This method compares the {@linkplain ParameterValue#getValue() value} against the expected
     *  {@linkplain ParameterDescriptor#getValueClass value class}, the
     * [{@linkplain ParameterDescriptor#getMinimumValue minimum} &hellip;
     *  {@linkplain ParameterDescriptor#getMaximumValue maximum}] range and the set of
     *  {@linkplain ParameterDescriptor#getValidValues valid values}.
     * <p>
     * This is a convenience method for testing a single parameter value assuming that its
     * descriptor is correct. It returns a {@code boolean} because in case of failure, the
     * faulty parameter is known: it is the one given in argument to this method. For a more
     * generic test which can work on group of parameters and provide more details about which
     * parameter failed and why, use the
     * {@link #isValid(GeneralParameterValue, GeneralParameterDescriptor)} method instead.
     *
     * @param value The parameter to test.
     * @return {@code true} if the given parameter is valid.
     *
     * @category verification
     */
    public static boolean isValid(final ParameterValue<?> value) { // LGPL
        return isValidValue(value.getDescriptor(), value.getValue(), false) == null;
    }

    /**
     * Checks a parameter value against its descriptor, and returns an error message if it is
     * not conformant. This method is similar to {@link AbstractParameter#ensureValidValue}.
     * If one implementation is modified, the other one should be updated accordingly.
     *
     * @param <T> The expected type of value.
     * @param value The parameter to test.
     * @param localized {@code true} if a fully localized message is wanted in case of failure,
     *        or {@code false} for a sentinel value (not useful for reporting to the user).
     * @param descriptor The descriptor to use for fetching the conditions.
     * @return {@code null} if the given parameter is valid, or an error message otherwise.
     */
    private static <T> InternationalString isValidValue(final ParameterDescriptor<T> descriptor,
            final Object value, final boolean localized) // LGPL
    {
        /*
         * Accept null values only if explicitly authorized.
         */
        if (value == null) {
            if (descriptor.getMinimumOccurs() == 0) {
                return null; // Test pass.
            }
            return localized ? Errors.formatInternational(Errors.Keys.NO_PARAMETER_1,
                    AbstractParameter.getName(descriptor)) : EXPLAIN;
        }
        /*
         * Check the type.
         */
        final Class<?> type = value.getClass();
        final Class<T> expected = descriptor.getValueClass();
        if (!expected.isAssignableFrom(type)) {
            return localized ? Errors.formatInternational(Errors.Keys.ILLEGAL_CLASS_2,
                    type, expected) : EXPLAIN;
        }
        final T typedValue = expected.cast(value);
        /*
         * Check the range (if any).
         */
        final Comparable<T> minimum = descriptor.getMinimumValue();
        final Comparable<T> maximum = descriptor.getMaximumValue();
        if ((minimum != null && minimum.compareTo(typedValue) > 0) ||
            (maximum != null && maximum.compareTo(typedValue) < 0))
        {
            return localized ? Errors.formatInternational(Errors.Keys.VALUE_OUT_OF_BOUNDS_3,
                    value, minimum, maximum) : EXPLAIN;
        }
        /*
         * Check the enumeration (if any).
         */
        final Set<T> validValues = descriptor.getValidValues();
        if (validValues!=null && !validValues.contains(value)) {
            return localized ? Errors.formatInternational(Errors.Keys.ILLEGAL_ARGUMENT_2,
                    AbstractParameter.getName(descriptor), value) : EXPLAIN;
        }
        return null; // Test pass.
    }

    /**
     * Checks a parameter value against the conditions specified by the given descriptor.
     * If the given parameter is an instance of {@link ParameterValue}, then this method
     * compares the {@linkplain ParameterValue#getValue() value} against the expected
     *  {@linkplain ParameterDescriptor#getValueClass value class}, the
     * [{@linkplain ParameterDescriptor#getMinimumValue minimum} &hellip;
     *  {@linkplain ParameterDescriptor#getMaximumValue maximum}] range and the set of
     *  {@linkplain ParameterDescriptor#getValidValues valid values}.
     * If this method is an instance of {@link ParameterValueGroup}, then the check
     * is applied recursively for every parameter in this group.
     *
     * @param  value The parameter to test.
     * @param  descriptor The descriptor to use for fetching the conditions.
     * @return A conformance result having the attribute <code>{@linkplain ConformanceResult#pass()
     *         pass}=true</code> if the parameter is valid. If the parameter is not valid, then
     *         the result {@linkplain ConformanceResult#getExplanation() explanation} gives the
     *         raison.
     *
     * @since 3.05
     * @category verification
     */
    public static ConformanceResult isValid(final GeneralParameterValue value,
            final GeneralParameterDescriptor descriptor)
    {
        /*
         * Create a conformance result initialized to a successful check.
         */
        final DefaultConformanceResult result = new DefaultConformanceResult(
                descriptor.getName().getAuthority(), EXPLAIN, true);
        final GeneralParameterValue failure = isValid(value, descriptor, result);
        /*
         * If the validity test failed, update the conformance result accordingly.
         */
        if (failure != null) {
            final Identifier name = failure.getDescriptor().getName();
            result.setExplanation(Descriptions.formatInternational(
                    Descriptions.Keys.NON_CONFORM_PARAMETER_2, name, result.getExplanation()));
            result.setSpecification(name.getAuthority());
            result.setPass(Boolean.FALSE);
        }
        return result;
    }

    /**
     * Checks a parameter value against the conditions specified by the given descriptor.
     * In case of failure, the explanation message of the given {@code result} is updated
     * with the cause, and this method returns the descriptor of the parameter that failed
     * the check.
     *
     * @param  value      The parameter to test.
     * @param  descriptor The descriptor to use for fetching the conditions.
     * @param  result     The conformance result to update in case of failure.
     * @return {@code null} in case of success, or the faulty parameter in case of failure.
     */
    private static GeneralParameterValue isValid(final GeneralParameterValue value,
            final GeneralParameterDescriptor descriptor, final DefaultConformanceResult result)
    {
        if (descriptor instanceof ParameterDescriptor<?>) {
            /*
             * For a single parameter (the most common case), the value shall be an instance
             * of ParameterValue. If it is not, then the error message will be formatted at
             * the end of this method.
             */
            if (value instanceof ParameterValue<?>) {
                final InternationalString failure = isValidValue((ParameterDescriptor<?>) descriptor,
                        ((ParameterValue<?>) value).getValue(), true);
                if (failure != null) {
                    result.setExplanation(failure);
                    return value;
                }
                return null; // The test pass.
            }
        } else if (descriptor instanceof ParameterDescriptorGroup) {
            /*
             * For a group, the value shall be an instance of ParameterValueGroup. If it
             * is not, then the error message will be formatted at the end of this method.
             */
            if (value instanceof ParameterValueGroup) {
                final Map<GeneralParameterDescriptor,Integer> count = new HashMap<>();
                final ParameterDescriptorGroup group = (ParameterDescriptorGroup) descriptor;
                for (final GeneralParameterValue element : ((ParameterValueGroup) value).values()) {
                    final String name = getName(element.getDescriptor(), group);
                    final GeneralParameterDescriptor desc;
                    try {
                        desc = group.descriptor(name);
                    } catch (ParameterNotFoundException e) {
                        result.setExplanation(Errors.formatInternational(
                                Errors.Keys.UNEXPECTED_PARAMETER_1, name));
                        return value;
                    }
                    final GeneralParameterValue failure = isValid(element, desc, result);
                    if (failure != null) {
                        return failure;
                    }
                    // Count the occurrence of parameters.
                    Integer old = count.put(desc, 1);
                    if (old != null) {
                        count.put(desc, old + 1);
                    }
                }
                /*
                 * All parameters pass their individual test.
                 * Now check their cardinality.
                 */
                for (final GeneralParameterDescriptor desc : group.descriptors()) {
                    final Integer nw = count.get(desc);
                    final int n = (nw != null) ? nw : 0;
                    final int min = desc.getMinimumOccurs();
                    final int max = desc.getMaximumOccurs();
                    if (n < min || n > max) {
                        final String name = desc.getName().getCode();
                        final short key;
                        final Object[] param;
                        if (n == 0) {
                            key = Errors.Keys.NO_PARAMETER_1;
                            param = new Object[] {name};
                        } else {
                            key = Errors.Keys.ILLEGAL_OCCURS_FOR_PARAMETER_4;
                            param = new Object[] {name, nw, min, max};
                        }
                        result.setExplanation(Errors.formatInternational(key, param));
                        return value;
                    }
                }
                return null; // The test pass.
            }
        }
        /*
         * If we reach this point, the type of the parameter value is not consistent
         * with the type of the descriptor. Format the error message.
         */
        final Class<? extends GeneralParameterValue> type;
        if (value instanceof ParameterValue<?>) {
            type = ParameterValue.class;
        } else if (value instanceof ParameterValueGroup) {
            type = ParameterValueGroup.class;
        } else {
            type = GeneralParameterValue.class;
        }
        result.setExplanation(Errors.formatInternational(Errors.Keys.ILLEGAL_OPERATION_FOR_VALUE_CLASS_1, type));
        return value;
    }

    /**
     * Returns the name of the given parameter, using the authority code space expected by
     * the given group if possible.
     *
     * @param  parameter The parameter for which the name is wanted.
     * @param  group The group to use for determining the authority code space.
     * @return The name of the given parameter.
     *
     * @since 3.05
     */
    private static String getName(final GeneralParameterDescriptor parameter, final ParameterDescriptorGroup group) {
        String name = IdentifiedObjects.getName(parameter, group.getName().getAuthority());
        if (name == null) {
            name = parameter.getName().getCode();
        }
        return name;
    }

    /**
     * Returns the value of the given parameter in the given group if defined, or create a
     * default parameter otherwise. This method is equivalent to:
     *
     * {@preformat java
     *     return cast(group.parameter(parameter.getName().getCode()));
     * }
     *
     * except that this method tries to use an identifier of the same authority than the
     * given {@code group} if possible.
     *
     * @param  parameter The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter object.
     * @throws ParameterNotFoundException if the parameter is not valid for the given group.
     *
     * @see ParameterValueGroup#parameter(String)
     *
     * @since 3.18
     * @category query
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.parameter.Parameters}.
     */
    @Deprecated
    public static ParameterValue<?> getOrCreate(final ParameterDescriptor<?> parameter,
            final ParameterValueGroup group) throws ParameterNotFoundException
    {
        return castOrWrap(group).getOrCreate(parameter);
    }

    /**
     * Returns the parameter value as an object for the given descriptor.
     * The method uses the {@link ParameterValue#getValue()} method for fetching the value.
     *
     * @param  <T> The type of the parameter value.
     * @param  parameter The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value, or {@code null} if the parameter
     *         is optional and the user didn't provided any value.
     * @throws ParameterNotFoundException if the parameter is mandatory and not found in the group.
     *
     * @since 3.00
     * @category query
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.parameter.Parameters}.
     */
    @Deprecated
    public static <T> T value(final ParameterDescriptor<T> parameter, final ParameterValueGroup group)
            throws ParameterNotFoundException
    {
        return castOrWrap(group).getValue(parameter);
    }

    /**
     * Returns the parameter value as a string for the given descriptor.
     * This method uses the {@link ParameterValue#stringValue()} method for fetching the value.
     * Note that the result may be different than the above {@link #value value} method if the
     * {@code ParameterValue} implementation performs string conversions.
     *
     * @param  parameter The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value, or {@code null} if the parameter
     *         is optional and the user didn't provided any value.
     * @throws ParameterNotFoundException if the parameter is mandatory and not found in the group.
     *
     * @category query
     * @since 3.00
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.parameter.Parameters}.
     */
    @Deprecated
    public static String stringValue(final ParameterDescriptor<?> parameter, final ParameterValueGroup group)
            throws ParameterNotFoundException
    {
        try {
            return castOrWrap(group).stringValue((ParameterDescriptor) parameter);
        } catch (IllegalStateException e) {
            return null; // Deprecated practice, but done for now for compatibility with previous behaviour.
        }
    }

    /**
     * Returns the parameter value as a boolean for the given descriptor.
     * This method uses the {@link ParameterValue#booleanValue()} method for fetching the value.
     * Note that the result may be different than the above {@link #value value} method if the
     * {@code ParameterValue} implementation performs numeric conversions.
     *
     * @param  parameter The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value, or {@code null} if the parameter
     *         is optional and the user didn't provided any value.
     * @throws ParameterNotFoundException if the parameter is mandatory and not found in the group.
     *
     * @category query
     * @since 3.18
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.parameter.Parameters}.
     */
    @Deprecated
    public static Boolean booleanValue(final ParameterDescriptor<?> parameter, final ParameterValueGroup group)
            throws ParameterNotFoundException
    {
        try {
            return castOrWrap(group).booleanValue((ParameterDescriptor) parameter);
        } catch (IllegalStateException e) {
            return null; // Deprecated practice, but done for now for compatibility with previous behaviour.
        }
    }

    /**
     * Returns the parameter value as an integer for the given descriptor.
     * This method uses the {@link ParameterValue#intValue()} method for fetching the value.
     * Note that the result may be different than the above {@link #value value} method if the
     * {@code ParameterValue} implementation performs numeric conversions.
     *
     * @param  parameter The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value, or {@code null} if the parameter
     *         is optional and the user didn't provided any value.
     * @throws ParameterNotFoundException if the parameter is mandatory and not found in the group.
     *
     * @category query
     * @since 3.00
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.parameter.Parameters}.
     */
    @Deprecated
    public static Integer integerValue(final ParameterDescriptor<?> parameter, final ParameterValueGroup group)
            throws ParameterNotFoundException
    {
        try {
            return castOrWrap(group).intValue((ParameterDescriptor) parameter);
        } catch (IllegalStateException e) {
            return null; // Deprecated practice, but done for now for compatibility with previous behaviour.
        }
    }

    /**
     * Returns the parameter value as a list of integers for the given descriptor.
     * This method uses the {@link ParameterValue#intValueList()} method for fetching the values.
     * Note that the result may be different than the above {@link #value value} method if the
     * {@code ParameterValue} implementation performs numeric conversions.
     *
     * @param  parameter The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value, or {@code null} if the parameter
     *         is optional and the user didn't provided any value.
     * @throws ParameterNotFoundException if the parameter is mandatory and not found in the group.
     *
     * @category query
     * @since 3.18
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.parameter.Parameters}.
     */
    @Deprecated
    public static int[] integerValueList(final ParameterDescriptor<?> parameter, final ParameterValueGroup group)
            throws ParameterNotFoundException
    {
        try {
            return castOrWrap(group).intValueList((ParameterDescriptor) parameter);
        } catch (IllegalStateException e) {
            return null; // Deprecated practice, but done for now for compatibility with previous behaviour.
        }
    }

    /**
     * Returns the parameter value as a floating point number for the given descriptor.
     * Values are automatically converted into the standard units specified by the
     * supplied {@code param} argument.
     * <p>
     * This method uses the {@link ParameterValue#doubleValue(Unit)} method for fetching the value.
     * Note that the result may be different than the above {@link #value value} method if the
     * {@code ParameterValue} implementation performs numeric conversions.
     *
     * @param  parameter The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value, or {@code NaN} if the parameter
     *         is optional and the user didn't provided any value.
     * @throws ParameterNotFoundException if the parameter is mandatory and not found in the group.
     *
     * @category query
     * @since 3.00
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.parameter.Parameters}.
     */
    @Deprecated
    public static double doubleValue(final ParameterDescriptor<?> parameter, final ParameterValueGroup group)
            throws ParameterNotFoundException
    {
        try {
            return castOrWrap(group).doubleValue((ParameterDescriptor) parameter);
        } catch (IllegalStateException e) {
            return Double.NaN; // Deprecated practice, but done for now for compatibility with previous behaviour.
        }
    }

    /**
     * Returns the parameter value as a list of floating point numbers for the given descriptor.
     * Values are automatically converted into the standard units specified by the
     * supplied {@code param} argument.
     * <p>
     * This method uses the {@link ParameterValue#doubleValueList(Unit)} method for fetching the
     * values. Note that the result may be different than the above {@link #value value} method
     * if the {@code ParameterValue} implementation performs numeric conversions.
     *
     * @param  parameter The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value, or {@code null} if the parameter
     *         is optional and the user didn't provided any value.
     * @throws ParameterNotFoundException if the parameter is mandatory and not found in the group.
     *
     * @category query
     * @since 3.18
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.parameter.Parameters}.
     */
    @Deprecated
    public static double[] doubleValueList(final ParameterDescriptor<?> parameter, final ParameterValueGroup group)
            throws ParameterNotFoundException
    {
        try {
            return castOrWrap(group).doubleValueList((ParameterDescriptor) parameter);
        } catch (IllegalStateException e) {
            return null; // Deprecated practice, but done for now for compatibility with previous behaviour.
        }
    }

    /**
     * Searches all parameters with the specified name. The given {@code name} is
     * compared against parameter {@linkplain GeneralParameterDescriptor#getName name} and
     * {@linkplain GeneralParameterDescriptor#getAlias alias}. This method search recursively
     * in subgroups up to the specified depth:
     * <p>
     * <ul>
     *   <li>If {@code maxDepth} is equal to 0, then this method returns {@code param}
     *       if and only if it matches the specified name.</li>
     *   <li>If {@code maxDepth} is equal to 1 and {@code param} is an instance of
     *       {@link ParameterDescriptorGroup}, then this method checks all elements
     *       in this group but not in subgroups.</li>
     *   <li>...</li>
     *   <li>If {@code maxDepth} is a high number (e.g. 100), then this method checks all elements
     *       in all subgroups up to the specified depth, which is likely to be never reached. In
     *       this case, {@code maxDepth} can be seen as a safeguard against never ending loops, for
     *       example if parameters graph contains cyclic entries.</li>
     * </ul>
     *
     * @param  parameter The parameter to inspect.
     * @param  name  The name of the parameter to search for. See the
     *               <a href="#skip-navbar_top">class javadoc</a> for a rational about the usage
     *               of name as a key instead of {@linkplain ParameterDescriptor descriptor}.
     * @param maxDepth The maximal depth while descending down the parameter tree.
     * @return The set (possibly empty) of parameters with the given name.
     *
     * @category query
     */
    public static List<GeneralParameterValue> search(final GeneralParameterValue parameter,
            final String name, int maxDepth) // LGPL
    {
        final List<GeneralParameterValue> list = new ArrayList<>();
        search(parameter, name, maxDepth, list);
        return list;
    }

    /**
     * Implementation of the search algorithm. The result is stored in the supplied set.
     */
    private static void search(final GeneralParameterValue parameter, final String name,
            final int maxDepth, final Collection<GeneralParameterValue> list)
    {
        if (maxDepth >= 0) {
            if (IdentifiedObjects.isHeuristicMatchForName(parameter.getDescriptor(), name)) {
                list.add(parameter);
            }
            if ((maxDepth != 0) && (parameter instanceof ParameterValueGroup)) {
                for (final GeneralParameterValue value : ((ParameterValueGroup) parameter).values()) {
                    search(value, name, maxDepth-1, list);
                }
            }
        }
    }

    /**
     * Gets the
     * {@linkplain ParameterDescriptor#getName name}-{@linkplain ParameterValue#getValue value}
     * pairs. This method copies all parameter values into the supplied {@code destination} map.
     * Keys are parameter names as {@link String} objects, and values are parameter values as
     * arbitrary objects.
     * <p>
     * If the given parameters is a group having sub-groups, then all subgroups are extracted
     * recursively with their keys prefixed by the sub-groups name followed by the
     * {@linkplain DefaultNameSpace#DEFAULT_SEPARATOR default namespace separator}.
     * <p>
     * This map provides a convenient way to copy the content of a {@link ParameterValueGroup}
     * to a {@link java.util.Properties} instance.
     *
     * @param parameters  The parameters to extract values from.
     * @param destination The destination map.
     *
     * @category query
     * @since 3.18
     */
    public static void copy(final GeneralParameterValue parameters, final Map<? super String, Object> destination) {
        copy(parameters, destination, null, 0, true);
    }

    /**
     * Implementation of the public {@link #copy(GeneralParameterValue, Map)} method
     * for recursive invocations.
     *
     * @param parameters  The parameters to extract values from.
     * @param destination The destination map.
     * @param buffer      A buffer which contains the prefix of every keys, or {@code null}.
     * @param base        The number of valid characters in the buffer.
     * @param isRoot      {@code true} if the group to be added is the root, or {@code false}
     *                    on recursive invocations.
     * @return The buffer, which may have been created by some recursive invocation.
     *
     * @category query
     */
    private static StringBuilder copy(final GeneralParameterValue parameters,
            final Map<? super String, Object> destination,
            StringBuilder buffer, int base, final boolean isRoot)
    {
        final String name = parameters.getDescriptor().getName().getCode();
        if (parameters instanceof ParameterValue<?>) {
            final Object value = ((ParameterValue<?>) parameters).getValue();
            String key = name;
            if (base > 0) {
                buffer.setLength(base);
                key = buffer.append(name).toString();
            }
            final Object old = destination.put(key, value);
            if (old != null && !old.equals(value)) {
                // This code will fails to detect if a null value was explicitly supplied
                // previously. We assume that such case is uncommon and not a big deal.
                throw new IllegalArgumentException(Errors.format(Errors.Keys.INCONSISTENT_VALUE));
            }
        }
        if (parameters instanceof ParameterValueGroup) {
            if (!isRoot) {
                if (buffer == null) {
                    buffer = new StringBuilder(32);
                }
                buffer.setLength(base);
                base = buffer.append(name).append(DefaultNameSpace.DEFAULT_SEPARATOR).length();
            }
            for (final GeneralParameterValue value : ((ParameterValueGroup) parameters).values()) {
                buffer = copy(value, destination, buffer, base, false);
            }
        }
        return buffer;
    }

    /**
     * Copies all parameter values from {@code source} to {@code target}. A typical usage of
     * this method is for transferring values from an arbitrary implementation to some specific
     * implementation (e.g. a parameter group implementation backed by a
     * {@link java.awt.image.renderable.ParameterBlock} for image processing operations).
     *
     * @param source The parameters to copy.
     * @param target Where to copy the source parameters.
     *
     * @category update
     * @since 2.2
     *
     * @deprecated Moved to Apache SIS as {@link org.apache.sis.parameter.Parameters#copy(ParameterValueGroup, ParameterValueGroup).
     */
    @Deprecated
    private static void copy(final ParameterValueGroup source, final ParameterValueGroup target) {
        for (final GeneralParameterValue param : source.values()) {
            final String name = param.getDescriptor().getName().getCode();
            if (param instanceof ParameterValueGroup) {
                copy((ParameterValueGroup) param, target.addGroup(name));
            } else {
                target.parameter(name).setValue(((ParameterValue<?>) param).getValue());
            }
        }
    }

    /**
     * Ensures that a value is set for the parameter of the specified name. This method returns {@code true}
     * if the parameter value <i>has been</i> or <i>should be</i> changed (depending on the {@code force}
     * argument value), or {@code false} otherwise. More specifically this method performs the following steps:
     *
     * <ul>
     *   <li>If the parameter for the given name has no value, unconditionally assign the given
     *       value to that parameter and returns {@code true}.</li>
     *   <li>Otherwise, compare the current parameter value with the specified value.
     *       If the values are approximatively equal, do nothing and return {@code false}.</li>
     *   <li>Otherwise there is a choice:
     *     <ul>
     *       <li>If the {@code force} argument is {@code true}, overwrite the current parameter
     *           value with the given value.</li>
     *       <li>Otherwise left the parameter value unchanged but log a warning.</li>
     *     </ul>
     *     Then return {@code true}.</li>
     * </ul>
     *
     * This method can be used when the same parameter values may be specified more than once
     * (for example from different sources of information), and we want to ensure that all
     * those values are consistent, without treating mismatches as fatal errors.
     *
     * @param parameters The set of projection parameters.
     * @param name       The parameter name to set.
     * @param value      The value to set, or to expect if the parameter is already set.
     * @param unit       The value unit.
     * @param force      {@code true} for forcing the parameter to the specified {@code value} in case of mismatch.
     * @return {@code true} if they were a mismatch (in which case the value has been updated if the {@code force}
     *         argument is {@code true}), or {@code false} if the parameter has been left unchanged.
     * @throws ParameterNotFoundException If no parameter of the given name has been found.
     *
     * @category update
     *
     * @deprecated Not used anymore. To avoid because of arbitrary threshold.
     */
    @Deprecated
    public static boolean ensureSet(final ParameterValueGroup parameters, final String name,
            final double value, final Unit<?> unit, boolean force) throws ParameterNotFoundException
    {
        final ParameterValue<?> parameter = parameters.parameter(name);
        try {
            final double current = parameter.doubleValue(unit);
            if (Math.abs(current / value - 1) <= EPS) {
                return false;
            }
            if (Double.isNaN(current)) {
                force = true;
            }
        } catch (IllegalStateException exception) {
            // No value was set for this parameter, and there is no default value.
            force = true;
        }
        // A value was set, but is different than the expected value.
        if (force) {
            parameter.setValue(value, unit);
        } else {
            Logging.log(Parameters.class, "ensureSet", new LogRecord(Level.FINE,
                    Errors.format(Errors.Keys.VALUE_ALREADY_DEFINED_1, name)));
        }
        return true;
    }

    /**
     * Convert a ParameterValueGroup in a Map of values. Map keys will be parameter names, and their associated value are
     * indeed map values. For nested Parameter group, a list of maps will be created inside the output map. Theorically,
     * this allows infinite group depth.
     *
     * @param source : Group of parametrs to convert.
     * @return A map which contains all parameters and groups of input Parameter group.
     */
    public static Map<String,Object> toMap(final ParameterValueGroup source) {
        ArgumentChecks.ensureNonNull("source", source);
        final HashMap<String, Object> result = new HashMap<>();
        String parameterName;
        for (GeneralParameterValue value : source.values()) {
            parameterName = value.getDescriptor().getName().getCode();
            if (value instanceof ParameterValue) {
                result.put(parameterName, ((ParameterValue) value).getValue());
            } else if (value instanceof ParameterValueGroup) {
                Collection<Map<String, Object>> subGroup = (List<Map<String, Object>>) result.get(parameterName);
                if (subGroup == null) {
                    subGroup = new ArrayList<>();
                    result.put(parameterName, subGroup);
                }
                subGroup.add(toMap((ParameterValueGroup) value));
            }
        }
        return result;
    }

    /**
     * Transform a Map in a ParameterValueGroup. A default parameter is first created and all key found in the map
     * that match the descriptor will be completed.
     */
    public static ParameterValueGroup toParameter(final Map<String, ?> params, final ParameterDescriptorGroup desc) {
        ArgumentChecks.ensureNonNull("params", params);
        ArgumentChecks.ensureNonNull("desc", desc);
        return toParameter(params, desc, true);
    }

    /**
     * Transform a Map in a ParameterValueGroup. A default parameter is first created and all key found in the map
     * that match the descriptor will be completed.
     *
     * @param params         The map containing entries to put in a ParameterValueGroup
     * @param desc           The descriptor for the group to create.
     * @param checkMandatory if True, a parameter will be returned only if the map provide all mandatory parameters with
     *                       no default value.
     * @return A parameter value group matching input descriptor, filled with input map value.
     *         Return null if input map is incompatible with input descriptor.
     *
     * @throws ParameterNotFoundException If the map does not contains all needed mandatory
     *         parameters required by this descriptor.
     * @throws UnconvertibleObjectException If an error occurred while putting a value from input map
     *         to target Parameter group.
     */
    public static ParameterValueGroup toParameter(final Map<String, ?> params,
                                                  final ParameterDescriptorGroup desc, final boolean checkMandatory)
            throws ParameterNotFoundException, UnconvertibleObjectException {

        ArgumentChecks.ensureNonNull("params", params);
        ArgumentChecks.ensureNonNull("desc", desc);
        if (checkMandatory) {
            for (GeneralParameterDescriptor de : desc.descriptors()) {
                if (de.getMinimumOccurs() > 0 && !(params.containsKey(de.getName().getCode()))) {
                    if (de instanceof ParameterDescriptor && ((ParameterDescriptor) de).getDefaultValue() == null) {
                        //a mandatory parameter is not present
                        throw new ParameterNotFoundException("A mandatory parameter "+de.getName()+" was not found in the input parameters.", de.getName().getCode());
                    }
                }
            }
        }

        final ParameterValueGroup parameter = desc.createValue();
        GeneralParameterDescriptor subDesc;
        for (final Map.Entry<String, ?> entry : params.entrySet()) {

            try {
                subDesc = desc.descriptor(entry.getKey());
            } catch (ParameterNotFoundException ex) {
                //do nothing, the map may contain other values for other uses
                continue;
            }

            if (subDesc instanceof ParameterDescriptorGroup) {
                if (entry.getValue() instanceof Map) {
                    parameter.values().add(
                            toParameter((Map) entry.getValue(), (ParameterDescriptorGroup) subDesc, checkMandatory));
                } else if (entry.getValue() instanceof Collection) {

                    final List values = new ArrayList((Collection) entry.getValue());
                    final int nbGroups = values.size();

                    //already exist groups
                    int nbParamGroups = parameter.groups(entry.getKey()).size();

                    //create missing groups
                    if (nbGroups > nbParamGroups) {
                        int toAdd = nbGroups - nbParamGroups;
                        for (int i = 0; i < toAdd; i++) {
                            parameter.addGroup(entry.getKey());
                        }
                    }

                    // convert and copy map values into parameter groups
                    final List<ParameterValueGroup> paramGroups = parameter.groups(entry.getKey());
                    for (int i = 0; i < nbGroups; i++) {
                        Object valObj = values.get(i);
                        if (valObj instanceof Map) {
                            ParameterValueGroup newGroup = toParameter((Map) valObj, (ParameterDescriptorGroup) subDesc, checkMandatory);
                            copy(newGroup, paramGroups.get(i));
                        } else {
                            throw new IllegalArgumentException("Illegal value for parameter " + entry.getKey() + ". It's a parameter group, so we should have a nested map as input.");
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Illegal value for parameter " + entry.getKey() + ". It's a parameter group, so we should have a nested map as input.");
                }

            } else if (subDesc instanceof ParameterDescriptor) {

                final ParameterDescriptor pdesc = (ParameterDescriptor) subDesc;
                try {
                    final ParameterValue param = Parameters.getOrCreate(pdesc, parameter);
                    param.setValue(
                            ObjectConverters.convert(entry.getValue(), pdesc.getValueClass()));
                } catch (ParameterNotFoundException e) {
                    // If mandatory check is activated, possible errors at this point have already been checked.
                    continue;
                }

            } else {
                throw new IllegalArgumentException("Unsupported parameter type.");
            }
        }

        return parameter;
    }

}
