/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.measure;

import javax.measure.unit.Unit;
import org.apache.sis.internal.util.Numerics;

// Related to JDK7
import java.util.Objects;


/**
 * A scalar value with a unit of measurement.
 *
 * @author  Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public final class Measure extends Number {
    /**
     * For compatibility with different versions.
     */
    private static final long serialVersionUID = 6917234039472328164L;

    /**
     * The scalar value.
     */
    private final double value;

    /**
     * The unit, or {@code null} if unknown or inapplicable.
     */
    private final Unit<?> unit;

    /**
     * Creates a new measure with the specified value and unit.
     *
     * @param value The value.
     * @param unit  The unit of measurement for the given value, or {@code null} if unknown or inapplicable.
     */
    public Measure(final double value, final Unit<?> unit) {
        this.value = value;
        this.unit  = unit;
    }

    /**
     * Returns the scalar value.
     *
     * @return The scalar value.
     */
    @Override
    public double doubleValue() {
        return value;
    }

    /**
     * Returns the scalar value casted as a {@code float}.
     *
     * @return The scalar value.
     */
    @Override
    public float floatValue() {
        return (float) value;
    }

    /**
     * Returns the scalar value {@linkplain Math#round(double) rounded} to the nearest long integer.
     *
     * @return The scalar value.
     */
    @Override
    public long longValue() {
        return Math.round(value);
    }

    /**
     * Returns the scalar value {@linkplain Math#round(float) rounded} to the nearest integer.
     *
     * @return The scalar value.
     */
    @Override
    public int intValue() {
        return Math.round((float) value);
    }

    /**
     * Returns the unit of measurement.
     *
     * @return The unit of measurement, or {@code null} if unknown or inapplicable.
     */
    public Unit<?> getUnit() {
        return unit;
    }

    /**
     * Returns a hash code value for this measure.
     *
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        final long hash = Double.doubleToLongBits(value) + Objects.hashCode(unit);
        return ((int) hash) ^ (int) (hash >>> Integer.SIZE);
    }

    /**
     * Compares this measure with the specified object for equality.
     *
     * @param object The object to compare with this measure.
     * @return {@code true} if the given object is equal to this measure.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof Measure) {
            final Measure that = (Measure) object;
            return Numerics.equals(value, that.value) &&
                    Objects.equals(unit,  that.unit);
        }
        return false;
    }

    /**
     * Returns a string representation of this measure.
     *
     * @return A string representation of this measure.
     */
    @Override
    public String toString() {
        if (unit == null) {
            return String.valueOf(value);
        }
        return new StringBuilder().append(value).append(' ').append(unit).toString();
    }
}
