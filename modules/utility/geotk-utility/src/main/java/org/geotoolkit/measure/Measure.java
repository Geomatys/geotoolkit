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

import java.util.Objects;
import javax.measure.unit.Unit;
import net.jcip.annotations.Immutable;
import org.geotoolkit.util.Utilities;


/**
 * A scalar with a unit.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@Immutable
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
     * The unit.
     */
    private final Unit<?> unit;

    /**
     * Creates a new measure with the specified value and unit.
     *
     * @param value The value.
     * @param unit The unit of measurement for the given value.
     */
    public Measure(final double value, final Unit<?> unit) {
        this.value = value;
        this.unit  = unit;
    }

    /** Returns the scalar value. */ @Override public double doubleValue() {return         value;}
    /** Returns the scalar value. */ @Override public float   floatValue() {return (float) value;}
    /** Returns the scalar value. */ @Override public long     longValue() {return (long)  value;}
    /** Returns the scalar value. */ @Override public int       intValue() {return (int)   value;}
    /** Returns the scalar value. */ @Override public short   shortValue() {return (short) value;}
    /** Returns the scalar value. */ @Override public byte     byteValue() {return (byte)  value;}

    /**
     * Returns the unit.
     *
     * @return The unit of measurement.
     */
    public Unit<?> getUnit() {
        return unit;
    }

    /**
     * Returns a hash code value for this measure.
     */
    @Override
    public int hashCode() {
        long code = Double.doubleToLongBits(value);
        return Utilities.hash(unit, (int) code ^ (int)(code >>> 32));
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
            return Utilities.equals(value, that.value) &&
                     Objects.equals(unit,  that.unit);
        }
        return false;
    }

    /**
     * Returns a string representation of this measure.
     */
    @Override
    public String toString() {
        final String n = String.valueOf(value);
        return (unit != null) ? n + ' ' + unit : n;
    }
}
