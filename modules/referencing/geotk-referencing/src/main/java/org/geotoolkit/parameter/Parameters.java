/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.measure.unit.Unit;

import org.opengis.util.InternationalString;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterTypeException;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.metadata.Identifier;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.metadata.iso.quality.DefaultConformanceResult;


/**
 * Utility class for methods helping implementing, and working with the parameter API from
 * {@link org.opengis.parameter} package. This class provides methods for {@linkplain #search
 * searching}, {@linkplain #ensureSet setting a value}, <i>etc.</i> from a parameter name.
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
 * When a method expects a full {@code ParameterDescriptor} object as argument (for example
 * {@link #value value}), the {@linkplain ParameterDescriptor#getName descriptor name} and
 * alias are used for the search - this class do <strong>not</strong> looks for strictly
 * equals {@code ParameterDescriptor} objects.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD)
 * @version 3.05
 *
 * @since 2.1
 * @module
 */
@Static
public final class Parameters {
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
     */
    public static ParameterDescriptorGroup EMPTY_GROUP =
            new DefaultParameterDescriptorGroup(Vocabulary.format(Vocabulary.Keys.EMPTY));

    /**
     * Do not allows instantiation of this utility class.
     */
    private Parameters() {
    }

    /**
     * Casts the given parameter descriptor to the given type. An exception is thrown
     * immediately if the parameter does not have the expected value class. This
     * is a helper method for type safety when using Java 5 parameterized types.
     *
     * @param  <T> The expected value class.
     * @param  descriptor The descriptor to cast, or {@code null}.
     * @param  type The expected value class.
     * @return The descriptor casted to the given type, or {@code null} if the given descriptor was null.
     * @throws ClassCastException if the given descriptor doesn't have the expected value class.
     *
     * @since 2.5
     */
    @SuppressWarnings("unchecked")
    public static <T> ParameterDescriptor<T> cast(ParameterDescriptor<?> descriptor, Class<T> type)
            throws ClassCastException
    {
        if (descriptor != null) {
            final Class<?> actual = descriptor.getValueClass();
            // We require a strict equality - not type.isAssignableFrom(actual) - because in
            // the later case we could have (to be strict) to return a <? extends T> type.
            if (!type.equals(actual)) {
                throw new ClassCastException(Errors.format(Errors.Keys.BAD_PARAMETER_TYPE_$2,
                        descriptor.getName().getCode(), actual));
            }
        }
        return (ParameterDescriptor<T>) descriptor;
    }

    /**
     * Casts the given parameter value to the given type. An exception is thrown
     * immediately if the parameter does not have the expected value class. This
     * is a helper method for type safety when using Java 5 parameterized types.
     *
     * @param  <T> The expected value class.
     * @param  value The value to cast, or {@code null}.
     * @param  type The expected value class.
     * @return The value casted to the given type, or {@code null} if the given value was null.
     * @throws ClassCastException if the given value doesn't have the expected value class.
     *
     * @since 2.5
     */
    @SuppressWarnings("unchecked")
    public static <T> ParameterValue<T> cast(final ParameterValue<?> value, final Class<T> type)
            throws ClassCastException
    {
        if (value != null) {
            final ParameterDescriptor<?> descriptor = value.getDescriptor();
            final Class<?> actual = descriptor.getValueClass();
            if (!type.equals(actual)) { // Same comment than cast(ParameterDescriptor)...
                throw new ClassCastException(Errors.format(Errors.Keys.BAD_PARAMETER_TYPE_$2,
                        descriptor.getName().getCode(), actual));
            }
        }
        return (ParameterValue<T>) value;
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
     * @param parameter The parameter to test.
     * @return {@code true} if the given parameter is valid.
     */
    public static boolean isValid(final ParameterValue<?> parameter) {
        return isValidValue(parameter.getDescriptor(), parameter.getValue(), false) == null;
    }

    /**
     * Checks a parameter value against its descriptor, and returns an error message if it is
     * not conformant. This method is similar to {@link AbstractParameter#ensureValidValue}.
     * If one implementation is modified, the other one should be updated accordingly.
     *
     * @param <T> The expected type of value.
     * @param parameter The parameter to test.
     * @param localized {@code true} if a fully localized message is wanted in case of failure,
     *        or {@code false} for a sentinal value (not useful for reporting to the user).
     * @param descriptor The descriptor to use for fetching the conditions.
     * @return {@code null} if the given parameter is valid, or an error message otherwise.
     */
    private static <T> InternationalString isValidValue(final ParameterDescriptor<T> descriptor,
            final Object value, final boolean localized)
    {
        /*
         * Accept null values only if explicitly authorized.
         */
        if (value == null) {
            if (descriptor.getMinimumOccurs() == 0) {
                return null; // Test pass.
            }
            return localized ? Errors.formatInternational(Errors.Keys.MISSING_PARAMETER_$1,
                    AbstractParameter.getName(descriptor)) : EXPLAIN;
        }
        /*
         * Check the type.
         */
        final Class<?> type = value.getClass();
        final Class<T> expected = descriptor.getValueClass();
        if (!expected.isAssignableFrom(type)) {
            return localized ? Errors.formatInternational(Errors.Keys.ILLEGAL_CLASS_$2,
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
            return localized ? Errors.formatInternational(Errors.Keys.VALUE_OUT_OF_BOUNDS_$3,
                    value, minimum, maximum) : EXPLAIN;
        }
        /*
         * Check the enumeration (if any).
         */
        final Set<T> validValues = descriptor.getValidValues();
        if (validValues!=null && !validValues.contains(value)) {
            return localized ? Errors.formatInternational(Errors.Keys.ILLEGAL_ARGUMENT_$2,
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
     * @param  parameter The parameter to test.
     * @param  descriptor The descriptor to use for fetching the conditions.
     * @return A conformance result having the attribute <code>{@linkplain ConformanceResult#pass()
     *         pass}=true</code> if the parameter is valid. If the parameter is not valid, then
     *         the result {@linkplain ConformanceResult#getExplanation() explanation} gives the
     *         raison.
     *
     * @since 3.05
     */
    public static ConformanceResult isValid(final GeneralParameterValue parameter,
            final GeneralParameterDescriptor descriptor)
    {
        /*
         * Create a conformance result initialized to a successful check.
         */
        final DefaultConformanceResult result = new DefaultConformanceResult(
                descriptor.getName().getAuthority(), EXPLAIN, true);
        final GeneralParameterValue failure = isValid(parameter, descriptor, result);
        /*
         * If the validity test failed, update the conformance result accordingly.
         */
        if (failure != null) {
            final Identifier name = failure.getDescriptor().getName();
            result.setExplanation(Descriptions.formatInternational(
                    Descriptions.Keys.NON_CONFORM_PARAMETER_$2, name, result.getExplanation()));
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
     * @param  parameter  The parameter to test.
     * @param  descriptor The descriptor to use for fetching the conditions.
     * @param  result     The conformance result to update in case of failure.
     * @return {@code null} in case of success, or the faulty parameter in case of failure.
     */
    private static GeneralParameterValue isValid(final GeneralParameterValue parameter,
            final GeneralParameterDescriptor descriptor, final DefaultConformanceResult result)
    {
        if (descriptor instanceof ParameterDescriptor<?>) {
            /*
             * For a single parameter (the most common case), the value shall be an instance
             * of ParameterValue. If it is not, then the error message will be formatted at
             * the end of this method.
             */
            if (parameter instanceof ParameterValue<?>) {
                final InternationalString failure = isValidValue((ParameterDescriptor<?>) descriptor,
                        ((ParameterValue<?>) parameter).getValue(), true);
                if (failure != null) {
                    result.setExplanation(failure);
                    return parameter;
                }
                return null; // The test pass.
            }
        } else if (descriptor instanceof ParameterDescriptorGroup) {
            /*
             * For a group, the value shall be an instance of ParameterValueGroup. If it
             * is not, then the error message will be formatted at the end of this method.
             */
            if (parameter instanceof ParameterValueGroup) {
                final Map<GeneralParameterDescriptor,Integer> count = new HashMap<GeneralParameterDescriptor,Integer>();
                final ParameterDescriptorGroup group = (ParameterDescriptorGroup) descriptor;
                for (final GeneralParameterValue element : ((ParameterValueGroup) parameter).values()) {
                    final String name = getName(element.getDescriptor(), group);
                    final GeneralParameterDescriptor desc;
                    try {
                        desc = group.descriptor(name);
                    } catch (ParameterNotFoundException e) {
                        result.setExplanation(Errors.formatInternational(
                                Errors.Keys.UNEXPECTED_PARAMETER_$1, name));
                        return parameter;
                    }
                    final GeneralParameterValue failure = isValid(element, desc, result);
                    if (failure != null) {
                        return failure;
                    }
                    // Count the occurence of parameters.
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
                        final int key;
                        final Object[] param;
                        if (n == 0) {
                            key = Errors.Keys.MISSING_PARAMETER_$1;
                            param = new Object[] {name};
                        } else {
                            key = Errors.Keys.ILLEGAL_OCCURS_FOR_PARAMETER_$4;
                            param = new Object[] {name, nw, min, max};
                        }
                        result.setExplanation(Errors.formatInternational(key, param));
                        return parameter;
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
        if (parameter instanceof ParameterValue<?>) {
            type = ParameterValue.class;
        } else if (parameter instanceof ParameterValueGroup) {
            type = ParameterValueGroup.class;
        } else {
            type = GeneralParameterValue.class;
        }
        result.setExplanation(Errors.formatInternational(Errors.Keys.ILLEGAL_OPERATION_FOR_VALUE_CLASS_$1, type));
        return parameter;
    }

    /**
     * Returns the name of the given parameter, using the authority code space expected by
     * the given group if possible.
     *
     * @param  param The parameter for which the name is wanted.
     * @param  group The group to use for determining the authority code space.
     * @return The name of the given parameter.
     *
     * @since 3.05
     */
    private static String getName(final GeneralParameterDescriptor param, final ParameterDescriptorGroup group) {
        String name = AbstractIdentifiedObject.getName(param, group.getName().getAuthority());
        if (name == null) {
            name = param.getName().getCode();
        }
        return name;
    }

    /**
     * Returns the parameter value for the specified operation parameter.
     *
     * @param  param The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value.
     * @throws ParameterNotFoundException if the parameter is not found.
     */
    private static <T> ParameterValue<T> getParameter(final ParameterDescriptor<T> param,
                                                      final ParameterValueGroup    group)
            throws ParameterNotFoundException
    {
        /*
         * Searches for an identifier matching the group's authority, if any.
         * This is needed if the parameter values group was created from an
         * EPSG database for example: we need to use the EPSG names instead
         * of the OGC ones.
         */
        final String name = getName(param, group.getDescriptor());
        if (param.getMinimumOccurs() != 0) {
            return cast(group.parameter(name), param.getValueClass());
        }
        /*
         * The parameter is optional. We don't want to invokes 'parameter(name)', because we don't
         * want to create a new parameter is the user didn't supplied one. Search the parameter
         * ourself (so we don't create any), and returns null if we don't find any.
         *
         * TODO: A simplier solution would be to add a 'isDefined' method in GeoAPI,
         *       or something similar.
         */
        final GeneralParameterDescriptor search;
        search = group.getDescriptor().descriptor(name);
        if (search instanceof ParameterDescriptor<?>) {
            for (final GeneralParameterValue candidate : group.values()) {
                if (search.equals(candidate.getDescriptor())) {
                    return cast((ParameterValue<?>) candidate, param.getValueClass());
                }
            }
        }
        return null;
    }

    /**
     * Returns the parameter value as an object for the given descriptor.
     *
     * @param  <T> The type of parameter value.
     * @param  param The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value, or {@code null} if the parameter
     *         is optional and the user didn't provided any value.
     * @throws ParameterNotFoundException if the parameter is mandatory and not found in the group.
     *
     * @since 3.00
     */
    public static <T> T value(final ParameterDescriptor<T> param,
                              final ParameterValueGroup    group)
            throws ParameterNotFoundException
    {
        final ParameterValue<T> value = getParameter(param, group);
        return (value != null) ? value.getValue() : null;
    }

    /**
     * Returns the parameter value as a string for the given descriptor.
     *
     * @param  param The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value, or {@code null} if the parameter
     *         is optional and the user didn't provided any value.
     * @throws ParameterNotFoundException if the parameter is mandatory and not found in the group.
     *
     * @since 3.00
     */
    public static String stringValue(final ParameterDescriptor<?> param,
                                     final ParameterValueGroup    group)
            throws ParameterNotFoundException
    {
        final ParameterValue<?> value = getParameter(param, group);
        return (value != null) ? value.stringValue() : null;
    }

    /**
     * Returns the parameter value as an integer for the given descriptor.
     *
     * @param  param The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value, or {@code null} if the parameter
     *         is optional and the user didn't provided any value.
     * @throws ParameterNotFoundException if the parameter is mandatory and not found in the group.
     *
     * @since 3.00
     */
    public static Integer integerValue(final ParameterDescriptor<?> param,
                                       final ParameterValueGroup    group)
            throws ParameterNotFoundException
    {
        final ParameterValue<?> value = getParameter(param, group);
        return (value != null) ? value.intValue() : null;
    }

    /**
     * Returns the parameter value as a floating point number for the given descriptor.
     * Values are automatically converted into the standard units specified by the
     * supplied {@code param} argument.
     *
     * @param  param The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value, or {@code NaN} if the parameter
     *         is optional and the user didn't provided any value.
     * @throws ParameterNotFoundException if the parameter is mandatory and not found in the group.
     *
     * @since 3.00
     */
    public static double doubleValue(final ParameterDescriptor<?> param,
                                     final ParameterValueGroup    group)
            throws ParameterNotFoundException
    {
        final Unit<?> unit = param.getUnit();
        final ParameterValue<?> value = getParameter(param, group);
        return (value == null) ? Double.NaN :
                (unit != null) ? value.doubleValue(unit) : value.doubleValue();
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
     * @param  param The parameter to inspect.
     * @param  name  The name of the parameter to search for. See the
     *               <a href="#skip-navbar_top">class javadoc</a> for a rational about the usage
     *               of name as a key instead of {@linkplain ParameterDescriptor descriptor}.
     * @param maxDepth The maximal depth while descending down the parameter tree.
     * @return The set (possibly empty) of parameters with the given name.
     */
    public static List<GeneralParameterValue> search(final GeneralParameterValue param,
            final String name, int maxDepth)
    {
        final List<GeneralParameterValue> list = new ArrayList<GeneralParameterValue>();
        search(param, name, maxDepth, list);
        return list;
    }

    /**
     * Implementation of the search algorithm. The result is stored in the supplied set.
     */
    private static void search(final GeneralParameterValue param, final String name,
                               final int maxDepth, final Collection<GeneralParameterValue> list)
    {
        if (maxDepth >= 0) {
            if (AbstractIdentifiedObject.nameMatches(param.getDescriptor(), name)) {
                list.add(param);
            }
            if ((maxDepth != 0) && (param instanceof ParameterValueGroup)) {
                for (final GeneralParameterValue value : ((ParameterValueGroup) param).values()) {
                    search(value, name, maxDepth-1, list);
                }
            }
        }
    }

    /**
     * Copies all parameter values from {@code source} to {@code target}. A typical usage of
     * this method is for transfering values from an arbitrary implementation to some specific
     * implementation (e.g. a parameter group implementation backed by a
     * {@link java.awt.image.renderable.ParameterBlock} for image processing operations).
     *
     * @param source The parameters to copy.
     * @param target Where to copy the source parameters.
     *
     * @since 2.2
     */
    public static void copy(final ParameterValueGroup source, final ParameterValueGroup target) {
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
     * Gets a flat view of
     * {@linkplain ParameterDescriptor#getName name}-{@linkplain ParameterValue#getValue value}
     * pairs. This method copies all parameter values into the supplied {@code destination} map.
     * Keys are parameter names as {@link String} objects, and values are parameter values as
     * arbitrary objects. All subgroups (if any) are extracted recursively.
     *
     * @param  parameters  The parameters to extract values from.
     * @param  destination The destination map, or {@code null} for a default one.
     * @return {@code destination}, or a new map if {@code destination} was null.
     */
    public static Map<String,Object> toNameValueMap(final GeneralParameterValue parameters,
                                                    Map<String,Object> destination)
    {
        if (destination == null) {
            destination = new LinkedHashMap<String,Object>();
        }
        if (parameters instanceof ParameterValue<?>) {
            final ParameterValue<?> param = (ParameterValue<?>) parameters;
            final Object value = param.getValue();
            final Object old = destination.put(param.getDescriptor().getName().getCode(), value);
            if (old!=null && !old.equals(value)) {
                // TODO: This code will fails to detect if a null value was explicitly supplied
                //       previously. We assume that this case should be uncommon and not a big deal.
                throw new IllegalArgumentException(Errors.format(Errors.Keys.INCONSISTENT_VALUE));
            }
        }
        if (parameters instanceof ParameterValueGroup) {
            final ParameterValueGroup group = (ParameterValueGroup) parameters;
            for (final GeneralParameterValue value : group.values()) {
                destination = toNameValueMap(value, destination);
            }
        }
        return destination;
    }

    /**
     * Ensures that the specified parameter is set. The {@code value} is set if and only if
     * no value were already set by the user for the given {@code name}.
     * <p>
     * The {@code force} argument said what to do if the named parameter is already set. If the
     * value matches, nothing is done in all case. If there is a mismatch and {@code force} is
     * {@code true}, then the parameter is overridden with the specified {@code value}. Otherwise,
     * the parameter is left unchanged but a warning is logged with the {@link Level#FINE FINE}
     * level.
     *
     * @param parameters The set of projection parameters.
     * @param name       The parameter name to set.
     * @param value      The value to set, or to expect if the parameter is already set.
     * @param unit       The value unit.
     * @param force      {@code true} for forcing the parameter to the specified {@code value}
     *                   is case of mismatch.
     * @return {@code true} if the were a mismatch, or {@code false} if the parameters can be
     *         used with no change.
     */
    public static boolean ensureSet(final ParameterValueGroup parameters,
                                    final String name, final double value, final Unit<?> unit,
                                    final boolean force)
    {
        final ParameterValue<?> parameter;
        try {
            parameter = parameters.parameter(name);
        } catch (ParameterNotFoundException ignore) {
            /*
             * Parameter not found. This exception should not occurs most of the time.
             * If it occurs, we will not try to set the parameter here, but the same
             * exception is likely to occurs at MathTransform creation time. The later
             * is the expected place for this exception, so we will let it happen there.
             */
            return false;
        }
        try {
            if (Math.abs(parameter.doubleValue(unit) / value - 1) <= EPS) {
                return false;
            }
        } catch (InvalidParameterTypeException exception) {
            /*
             * The parameter is not a floating point value. Don't try to set it. An exception is
             * likely to be thrown at MathTransform creation time, which is the expected place.
             */
            return false;
        } catch (IllegalStateException exception) {
            /*
             * No value were set for this parameter, and there is no default value.
             */
            parameter.setValue(value, unit);
            return true;
        }
        /*
         * A value was set, but is different from the expected value.
         */
        if (force) {
            parameter.setValue(value, unit);
        } else {
            final LogRecord record = new LogRecord(Level.FINE,
                    Errors.format(Errors.Keys.VALUE_ALREADY_DEFINED_$1, name));
            record.setSourceClassName(Parameters.class.getName());
            record.setSourceMethodName("ensureSet");
            Logging.log(Parameters.class, record);
        }
        return true;
    }
}
