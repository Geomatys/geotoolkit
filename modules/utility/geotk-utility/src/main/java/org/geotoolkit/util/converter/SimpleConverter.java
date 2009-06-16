/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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


/**
 * An abstract class for simple converter. The default implementation assumes that simple converters
 * have no restriction (i.e. they can convert every values) and preserve order (i.e. if <var>A</var>
 * is smaller than <var>B</var> before conversion, the same holds after conversion). However
 * subclasses can change this default by overriding the methods defined in this abstract class.
 *
 * @param <S> The base type of source objects.
 * @param <T> The base type of converted objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
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
     * Returns a string representation of this converter for debugging purpose.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + '[' + getSourceClass().getSimpleName() +
                "\u00A0\u21E8\u00A0" + getTargetClass().getSimpleName() + ']';
    }
}
