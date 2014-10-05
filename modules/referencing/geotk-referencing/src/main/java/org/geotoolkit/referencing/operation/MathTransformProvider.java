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
package org.geotoolkit.referencing.operation;

import java.util.Map;
import java.util.HashMap;
import javax.measure.unit.Unit;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterNameException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.util.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.SingleOperation;

import org.geotoolkit.resources.Errors;
import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.util.ArgumentChecks;

import static org.geotoolkit.referencing.IdentifiedObjects.EMPTY_ALIAS_ARRAY;
import static org.geotoolkit.referencing.IdentifiedObjects.EMPTY_IDENTIFIER_ARRAY;


/**
 * An {@linkplain DefaultOperationMethod operation method} capable to create a
 * {@linkplain MathTransform math transform} from set of {@linkplain GeneralParameterValue
 * parameter values}. Implementations of this class should be listed in the following file
 * (see the {@linkplain org.geotoolkit.factory factory package} for more information about
 * how to manage providers registered in such files):
 *
 * {@preformat text
 *     META-INF/services/org.geotoolkit.referencing.operation.MathTransformProvider
 * }
 *
 * The {@linkplain DefaultMathTransformFactory default math transform factory} will parse the
 * above files in all JAR files in order to get all available providers on a system. In Geotk,
 * most providers are defined in the {@linkplain org.geotoolkit.referencing.operation.provider
 * provider sub-package}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.01
 *
 * @since 1.2
 * @level advanced
 * @module
 *
 * @see org.geotoolkit.referencing.operation.provider
 * @see org.geotoolkit.factory
 */
@Immutable
public abstract class MathTransformProvider extends DefaultOperationMethod {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7530475536803158473L;

    /**
     * Constructs a math transform provider from a set of parameters. The provider
     * {@linkplain #getIdentifiers identifiers} will be the same than the parameter
     * ones.
     *
     * @param sourceDimension Number of dimensions in the source CRS of this operation method.
     * @param targetDimension Number of dimensions in the target CRS of this operation method.
     * @param parameters The set of parameters (never {@code null}).
     */
    public MathTransformProvider(final int sourceDimension,
                                 final int targetDimension,
                                 final ParameterDescriptorGroup parameters)
    {
        this(toMap(parameters), sourceDimension, targetDimension, parameters);
    }

    /**
     * Constructs a math transform provider from a set of properties. The properties map is given
     * unchanged to the {@linkplain DefaultOperationMethod#DefaultOperationMethod(Map, Integer,
     * Integer, ParameterDescriptorGroup) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param sourceDimension Number of dimensions in the source CRS of this operation method.
     * @param targetDimension Number of dimensions in the target CRS of this operation method.
     * @param parameters The set of parameters (never {@code null}).
     */
    public MathTransformProvider(final Map<String,?> properties,
                                 final int sourceDimension,
                                 final int targetDimension,
                                 final ParameterDescriptorGroup parameters)
    {
        super(properties, sourceDimension, targetDimension, parameters);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Map<String,Object> toMap(final IdentifiedObject parameters) {
        ArgumentChecks.ensureNonNull("parameters", parameters);
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(NAME_KEY,        parameters.getName());
        properties.put(IDENTIFIERS_KEY, parameters.getIdentifiers().toArray(EMPTY_IDENTIFIER_ARRAY));
        properties.put(ALIAS_KEY,       parameters.getAlias()      .toArray(EMPTY_ALIAS_ARRAY));
        return properties;
    }

    /**
     * Returns {@code true} if at leat one identifier matches the given code in the given codespace.
     *
     * @since 3.03
     */
    final boolean identifierMatches(final String codespace, final String code) {
        for (final Identifier identifier : getIdentifiers()) {
            if (codespace.equals(identifier.getCodeSpace()) && code.equals(identifier.getCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the operation type. It may be
     * <code>{@linkplain org.opengis.referencing.operation.SingleOperation}.class</code>,
     * <code>{@linkplain org.opengis.referencing.operation.Conversion}.class</code>,
     * <code>{@linkplain org.opengis.referencing.operation.Projection}.class</code>,
     * <i>etc</i>.
     * <p>
     * The default implementation returns {@code SingleOperation.class}.
     * Subclass should overrides this methods and returns the appropriate
     * OpenGIS interface type (<strong>not</strong> the implementation type).
     *
     * @return The GeoAPI interface implemented by this operation.
     */
    @Override
    public Class<? extends SingleOperation> getOperationType() {
        return SingleOperation.class;
    }

    /**
     * Creates a math transform from the specified group of parameter values.
     * Subclasses can implements this method as in the example below:
     *
     * {@preformat java
     *     double semiMajor = values.parameter("semi_major").doubleValue(SI.METRE);
     *     double semiMinor = values.parameter("semi_minor").doubleValue(SI.METRE);
     *     // etc...
     *     return new MyTransform(semiMajor, semiMinor, ...);
     * }
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws InvalidParameterNameException if the values contains an unknown parameter.
     * @throws ParameterNotFoundException if a required parameter was not found.
     * @throws InvalidParameterValueException if a parameter has an invalid value.
     * @throws FactoryException if the math transform can't be created for some other reason
     *         (for example a required file was not found).
     */
    protected abstract MathTransform createMathTransform(ParameterValueGroup values)
            throws InvalidParameterNameException, ParameterNotFoundException,
                   InvalidParameterValueException, FactoryException;

    /**
     * Ensures that the given set of parameters contains only valid values.
     * This method compares all parameter names against the names declared in the
     * {@linkplain #getParameters operation method parameter descriptor}. If an unknown
     * parameter name is found, then an {@link InvalidParameterNameException} is thrown.
     * This method also ensures that all values are assignable to the
     * {@linkplain ParameterDescriptor#getValueClass expected class}, are between the
     * {@linkplain ParameterDescriptor#getMinimumValue minimum} and
     * {@linkplain ParameterDescriptor#getMaximumValue maximum} values and are one of the
     * {@linkplain ParameterDescriptor#getValidValues set of valid values}. If the value
     * fails any of those tests, then an {@link InvalidParameterValueException} is thrown.
     *
     * @param  values The parameters values to check.
     * @return The parameter values to use for {@linkplain MathTransform math transform}
     *         construction. May be different than the supplied {@code values}
     *         argument if some missing values needed to be filled with default values.
     * @throws InvalidParameterNameException if a parameter name is unknown.
     * @throws InvalidParameterValueException if a parameter has an invalid value.
     */
    protected ParameterValueGroup ensureValidValues(final ParameterValueGroup values)
            throws InvalidParameterNameException, InvalidParameterValueException
    {
        final ParameterDescriptorGroup parameters = getParameters();
        final GeneralParameterDescriptor descriptor = values.getDescriptor();
        if (parameters.equals(descriptor)) {
            /*
             * Since the "official" parameter descriptor was used, the descriptor should
             * have already enforced argument validity. Consequently, there is no need to
             * performs the check and we will avoid it as a performance enhancement.
             */
            return values;
        }
        /*
         * Copies the all values from the user-supplied group to the provider-supplied group.
         * The provider group should performs all needed checks. Furthermore, it is suppliers
         * responsibility to know about alias (e.g. OGC, EPSG, ESRI), while the user will
         * probably use the name from only one authority. With a copy, we gives a chances to
         * the provider-supplied parameters to uses its alias for understanding the user
         * parameter names.
         */
        final ParameterValueGroup copy = parameters.createValue();
        copy(values, copy);
        return copy;
    }

    /**
     * Implementation of {@code ensureValidValues}, to be invoked recursively
     * if the specified values contains sub-groups of values. This method copy all
     * values from the user-supplied parameter values into the provider-supplied
     * one. The provider one should understand alias, and performs name conversion
     * as well as argument checking on the fly.
     *
     * @param  values The parameters values to copy.
     * @param  copy   The parameters values where to put the copy.
     * @throws InvalidParameterNameException if a parameter name is unknown.
     * @throws InvalidParameterValueException if a parameter has an invalid value.
     */
    private static void copy(final ParameterValueGroup values, final ParameterValueGroup copy)
            throws InvalidParameterNameException, InvalidParameterValueException
    {
        for (final GeneralParameterValue value : values.values()) {
            final String name = value.getDescriptor().getName().getCode();
            if (value instanceof ParameterValueGroup) {
                /*
                 * Contains sub-group - invokes 'copy' recursively.
                 */
                final GeneralParameterDescriptor descriptor;
                descriptor = copy.getDescriptor().descriptor(name);
                if (descriptor instanceof ParameterDescriptorGroup) {
                    final ParameterValueGroup groups = (ParameterValueGroup) descriptor.createValue();
                    copy((ParameterValueGroup) value, groups);
                    values.groups(name).add(groups);
                    continue;
                } else {
                    throw new InvalidParameterNameException(Errors.format(
                            Errors.Keys.UNEXPECTED_PARAMETER_1, name), name);
                }
            }
            /*
             * Single parameter - copy the value, with special care for value with units.
             */
            final ParameterValue<?> source = (ParameterValue<?>) value;
            final ParameterValue<?> target;
            try {
                target = copy.parameter(name);
            } catch (ParameterNotFoundException cause) {
                throw new InvalidParameterNameException(Errors.format(
                            Errors.Keys.UNEXPECTED_PARAMETER_1, name), cause, name);
            }
            final Object  v    = source.getValue();
            final Unit<?> unit = source.getUnit();
            if (unit == null) {
                target.setValue(v);
            } else if (v instanceof Number) {
                target.setValue(((Number) v).doubleValue(), unit);
            } else if (v instanceof double[]) {
                target.setValue((double[]) v, unit);
            } else {
                throw new InvalidParameterValueException(Errors.format(
                        Errors.Keys.ILLEGAL_ARGUMENT_2, name, v), name, v);
            }
        }
    }

    /**
     * Format the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
     * Known Text</cite> (WKT)</A> element.
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name.
     */
    @Override
    public String formatTo(final Formatter formatter) {
        final Class<? extends SingleOperation> type = getOperationType();
        if (Projection.class.isAssignableFrom(type)) {
            return super.formatTo(formatter);
        }
        formatter.setInvalidWKT(this, null);
        return "OperationMethod";
    }
}
