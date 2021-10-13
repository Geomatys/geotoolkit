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

import java.util.Set;
import java.util.Objects;
import java.io.Serializable;
import javax.measure.Unit;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.InvalidParameterValueException;

import org.geotoolkit.util.Cloneable;
import org.apache.sis.measure.Units;
import org.geotoolkit.resources.Errors;
import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.io.wkt.FormattableObject;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * The root class of {@link ParameterValue} and {@link ParameterValueGroup} implementations.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 4.00
 *
 * @see AbstractParameterDescriptor
 *
 * @since 2.0
 * @module
 */
@Deprecated
public abstract class AbstractParameter extends FormattableObject
           implements GeneralParameterValue, Serializable, Cloneable
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 8458179223988766398L;

    /**
     * The abstract definition of this parameter or group of parameters.
     */
    protected final GeneralParameterDescriptor descriptor;

    /**
     * Constructs a parameter value from the specified descriptor.
     *
     * @param descriptor The abstract definition of this parameter or group of parameters.
     */
    protected AbstractParameter(final GeneralParameterDescriptor descriptor) {
        this.descriptor = descriptor;
        ensureNonNull("descriptor", descriptor);
    }

    /**
     * Returns the abstract definition of this parameter or group of parameters.
     */
    @Override
    public GeneralParameterDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Ensures that the given value is valid according the specified parameter descriptor.
     * This convenience method ensures that {@code value} is assignable to the
     * {@linkplain ParameterDescriptor#getValueClass() expected class}, is between the
     * {@linkplain ParameterDescriptor#getMinimumValue() minimum} and
     * {@linkplain ParameterDescriptor#getMaximumValue() maximum} values and is one of the
     * {@linkplain ParameterDescriptor#getValidValues() set of valid values}.
     * If the value fails any of those tests, then an exception is thrown.
     * <p>
     * This method is similar to {@link Parameters#isValid(ParameterValue)} except that the
     * exception contains an error message formatted with a description of the failure raison.
     *
     * @param  <T> The type of parameter value. The given {@code value} should typically be an
     *         instance of this class. This is not required by this method signature but is
     *         checked by this method implementation.
     * @param  descriptor The parameter descriptor to check against.
     * @param  value The value to check, or {@code null}.
     * @return The value casted to the descriptor parameterized type, or the
     *         {@linkplain ParameterDescriptor#getDefaultValue() default value}
     *         if the given value was null while the parameter is mandatory.
     * @throws InvalidParameterValueException if the parameter value is invalid.
     */
    static <T> T ensureValidValue(final ParameterDescriptor<T> descriptor, final Object value)
            throws InvalidParameterValueException
    {
        if (value == null) {
            if (descriptor.getMinimumOccurs() != 0) {
                return descriptor.getDefaultValue();
            }
            return null;
        }
        final String error;
        /*
         * Note: the implementation below is similar (except for different error message) to the
         * one in Parameters.isValidValue(ParameterDescriptor, Object). If one implementation is
         * modified, the other should be updated accordingly. The main difference is that null
         * values are replaced by the default value instead than being a conformance error.
         */
        final Class<T> type = descriptor.getValueClass();
        if (!type.isInstance(value)) {
            error = Errors.format(Errors.Keys.IllegalOperationForValueClass_1, value.getClass());
        } else {
            final T typedValue = type.cast(value);
            final Comparable<T> minimum = descriptor.getMinimumValue();
            final Comparable<T> maximum = descriptor.getMaximumValue();
            if ((minimum != null && minimum.compareTo(typedValue) > 0) ||
                (maximum != null && maximum.compareTo(typedValue) < 0))
            {
                error = Errors.format(Errors.Keys.ValueOutOfBounds_3, value, minimum, maximum);
            } else {
                final Set<T> validValues = descriptor.getValidValues();
                if (validValues!=null && !validValues.contains(value)) {
                    error = Errors.format(Errors.Keys.IllegalArgument_2, getName(descriptor), value);
                } else {
                    /*
                     * Passed every tests - the value is valid.
                     */
                    return typedValue;
                }
            }
        }
        throw new InvalidParameterValueException(error, getName(descriptor), value);
    }

    /**
     * Returns an exception initialized with a "Unitless parameter" error message for the
     * specified descriptor.
     */
    protected static IllegalStateException unitlessParameter(final GeneralParameterDescriptor descriptor) {
        return new IllegalStateException(Errors.format(
                Errors.Keys.UnitlessParameter_1, getName(descriptor)));
    }

    /**
     * Convenience method returning the name of the specified descriptor. This method is used
     * mostly for output to be read by human, not for processing. Consequently, we may consider
     * to returns a localized name in a future version.
     */
    protected static String getName(final GeneralParameterDescriptor descriptor) {
        return descriptor.getName().getCode();
    }

    /**
     * Returns the unit type as one of error message code. Used for checking unit with a better
     * error message formatting if needed.
     */
    static short getUnitMessageID(final Unit<?> unit) {
        if (Units.isLinear  (unit)) return Errors.Keys.NonLinearUnit_1;
        if (Units.isAngular (unit)) return Errors.Keys.NonAngularUnit_1;
        if (Units.isTemporal(unit)) return Errors.Keys.NonTemporalUnit_1;
        if (Units.isScale   (unit)) return Errors.Keys.NonScaleUnit_1;
        return Errors.Keys.IncompatibleUnit_1;
    }

    /**
     * Returns a copy of this parameter value or group.
     */
    @Override
    public AbstractParameter clone() {
        try {
            return (AbstractParameter) super.clone();
        } catch (CloneNotSupportedException exception) {
            // Should not happen, since we are cloneable
            throw new AssertionError(exception);
        }
    }

    /**
     * Compares the specified object with this parameter for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final AbstractParameter that = (AbstractParameter) object;
            return Objects.equals(this.descriptor, that.descriptor);
        }
        return false;
    }

    /**
     * Returns a hash value for this parameter. This value doesn't need
     * to be the same in past or future versions of this class.
     */
    @Override
    public int hashCode() {
        return descriptor.hashCode() ^ (int) serialVersionUID;
    }

    /**
     * Formats the inner part of this parameter as
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#PARAMETER"><cite>Well
     * Known Text</cite> (WKT)</A>.
     */
    @Override
    public String formatTo(final Formatter formatter) {
        if (this instanceof ParameterValue<?>) {
            return new Bridge((ParameterValue<?>) this).formatTo(formatter);
        }
        return "PARAMETER";
    }

    @Deprecated // Temporary bridge while we complete the migration to SIS.
    private static final class Bridge<T> extends org.apache.sis.parameter.DefaultParameterValue<T> {
        Bridge(final ParameterValue<T> param) {
            super(param);
        }

        @Override
        public String formatTo(final Formatter formatter) {
            return super.formatTo(formatter);
        }
    }
}
