/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.converter;

import org.apache.sis.util.Classes;
import org.geotoolkit.resources.Errors;


/**
 * An abstract class for simple {@linkplain ObjectConverter Object Converters}. The default
 * implementation assumes that simple converters have no restriction (i.e. they can convert
 * every values) and preserve order (i.e. if <var>A</var> is smaller than <var>B</var> before
 * conversion, the same holds after conversion). However subclasses can change this default
 * by overriding the methods defined in this abstract class.
 *
 * @param <S> The base type of source objects.
 * @param <T> The base type of converted objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.00
 * @module
 */
public abstract class SimpleConverter<S,T> implements ObjectConverter<S,T> {
    /**
     * Default constructor.
     */
    protected SimpleConverter() {
    }

    /**
     * Returns {@code false} by default, assuming that this converter does not have any
     * restriction. Subclasses may override.
     *
     * @return {@code true} if this converter accepts only a subset of source values.
     */
    @Override
    public boolean hasRestrictions() {
        return false;
    }

    /**
     * Returns {@code true} by default, assuming this converter preserves order.
     * Subclasses may override.
     *
     * @return {@code true} if this converter preserve order.
     */
    @Override
    public boolean isOrderPreserving() {
        return true;
    }

    /**
     * Returns {@code false} by default, assuming this converter preserves order.
     * Subclasses may override.
     *
     * @return {@code true} if this converter reverse order.
     */
    @Override
    public boolean isOrderReversing() {
        return false;
    }

    /**
     * Formats an error message for a value that can't be converted.
     *
     * @param  name  The parameter name.
     * @param  value The parameter value.
     * @param  cause The cause for the failure, or {@code null} if none.
     * @return The error message.
     */
    static String formatErrorMessage(final String name, final Object value, final Exception cause) {
        String message = Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2, name, value);
        if (cause != null) {
            final String cm = cause.getLocalizedMessage();
            if (cm != null) {
                message = message + System.getProperty("line.separator", "\n") + cm;
            }
        }
        return message;
    }

    /**
     * Returns a string representation of this converter for debugging purpose.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + '[' + getSourceClass().getSimpleName() +
                "\u00A0\u21E8\u00A0" + getTargetClass().getSimpleName() + ']';
    }
}
