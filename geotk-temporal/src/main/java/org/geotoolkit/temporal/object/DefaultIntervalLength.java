/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.temporal.object;

import java.util.Objects;
import javax.measure.UnitConverter;
import javax.measure.Unit;
import org.apache.sis.measure.Units;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.temporal.IntervalLength;

/**
 *A data type for intervals of time which supports the expression of duration in
 * terms of a specified multiple of a single unit of time.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 */
public class DefaultIntervalLength extends DefaultDuration implements IntervalLength {

    /**
     * Milli second unit.
     * @see #getTimeInMillis()
     */
    private static Unit MS_UNIT = Units.SECOND.divide(1000);

    /**
     * {@link UnitConverter} use to convert any unit into milli seconds if it's possible.
     * @see #getTimeInMillis()
     */
    private UnitConverter unitConverter;

    /**
     * This is the name of the unit of measure used to express the length of the interval.
     */
    private Unit unit;
    /**
     * This is the base of the multiplier of the unit.
     */
    private int radix;

    /**
     * This is the exponent of the base.
     */
    private int factor;

    /**
     * This is the length of the time interval as an integer multiple of one radix(exp -factor) of the specified unit.
     */
    private int value;

    /**
     * Creates a new instance of IntervalUnit example : Unit="second" radix=10 factor=3 value=7 specifies a time interval length of 7ms.
     *
     * @param unit   Unit of measure used to express the length of the interval.
     * @param radix  positive integer that is the base of the multiplier of the unit.
     * @param factor integer that is the exposant of the base.
     * @param value length of the time interval as an integer multiple of one radix^(-factor) of the specified unit.
     */
    public DefaultIntervalLength(final Unit unit, final int radix, final int factor, final int value) {
        ArgumentChecks.ensureNonNull("unit",   unit);
        ArgumentChecks.ensureNonNull("radix",  radix);
        ArgumentChecks.ensureStrictlyPositive("radix", radix);
        ArgumentChecks.ensureNonNull("factor", factor);
        ArgumentChecks.ensureNonNull("value",  value);
        this.unit     = unit;
        unitConverter = unit.getConverterTo(MS_UNIT);
        this.radix    = radix;
        this.factor   = factor;
        this.value    = value;
    }

    /**
     * Returns the {@link Unit} of measure used to express the length of the interval.
     *
     * @return the {@link Unit} of measure used to express the length of the interval.
     */
    @Override
    public Unit getUnit() {
        return unit;
    }

    /**
     * Returns positive {@code integer} that is the base of the mulitplier of the {@link Unit}.
     *
     * @return positive {@code integer} that is the base of the mulitplier of the {@link Unit}.
     */
    @Override
    public int getRadix() {
        return radix;
    }

    /**
     * Returns {@code integer} that is the exponent of the base.
     *
     * @return {@code integer} that is the exponent of the base.
     */
    @Override
    public int getFactor() {
        return factor;
    }

    /**
     * Returns the length of the time interval as an {@code integer} multiple of one radix^(-factor) of the specified {@link unit}.
     *
     * @return the length of the time interval as an {@code integer} multiple of one radix^(-factor) of the specified {@link unit}.
     */
    @Override
    public int getValue() {
        return value;
    }


    @Override
    public long getTimeInMillis() {
        return Double.doubleToLongBits(unitConverter.convert(value * StrictMath.pow(radix, -factor)));
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultIntervalLength) {
            final DefaultIntervalLength that = (DefaultIntervalLength) object;

            return Objects.equals(this.factor, that.factor) &&
                    Objects.equals(this.radix, that.radix) &&
                    Objects.equals(this.unit, that.unit) &&
                    Objects.equals(this.value, that.unit);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.unit != null ? this.unit.hashCode() : 0);
        hash = 37 * hash + this.factor;
        hash = 37 * hash + this.radix;
        hash = 37 * hash + this.value;
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("IntervalLength:").append('\n');
        if (unit != null) {
            s.append("unit:").append(unit).append('\n');
        }
        s.append("radix:").append(radix).append('\n');
        s.append("factor:").append(factor).append('\n');
        s.append("value:").append(value).append('\n');

        return s.toString();
    }
}
