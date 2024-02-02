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

import java.util.Objects;
import java.io.Serializable;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.GeneralParameterDescriptor;

import org.geotoolkit.util.Cloneable;
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
     * Returns a copy of this parameter value or group.
     */
    @Override
    public GeneralParameterValue clone() {
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
