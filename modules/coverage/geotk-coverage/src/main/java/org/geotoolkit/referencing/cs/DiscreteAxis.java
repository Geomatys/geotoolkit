/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.referencing.cs;

import java.util.Set;
import java.util.Arrays;
import java.util.Collection;
import java.io.Serializable;
import javax.measure.unit.Unit;

import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.cs.RangeMeaning;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;

import org.geotoolkit.lang.Decorator;
import org.geotoolkit.util.NumberRange;


/**
 * A default implementation of {@link DiscreteCoordinateSystemAxis}. This implementation wraps
 * an existing axis and adds discrete ordinate values. While not mandatory, the ordinates values
 * are better to be in strictly increasing or decreasing order (this is not verified).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.15
 * @module
 */
@Decorator(CoordinateSystemAxis.class)
final class DiscreteAxis implements CoordinateSystemAxis, DiscreteCoordinateSystemAxis<Double>, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 7456302077762863016L;

    /**
     * The axis on which to delegates every method calls.
     */
    protected final CoordinateSystemAxis axis;

    /**
     * The ordinate values. <strong>Do not modify the ordinate values</strong>.
     */
    final double[] ordinates;

    /**
     * Constructs a new coordinate system axis which delegates every method calls to
     * the given axis.
     *
     * @param axis The coordinate system axis to wrap.
     * @param ordinates The ordinate values. This array is <strong>not</strong> cloned.
     */
    DiscreteAxis(final CoordinateSystemAxis axis, final double... ordinates) {
        this.axis = axis;
        this.ordinates = ordinates;
    }

    /**
     * Returns the name of the wrapped axis.
     */
    @Override
    public ReferenceIdentifier getName() {
        return axis.getName();
    }

    /**
     * Returns the alias of the wrapped axis.
     */
    @Override
    public Collection<GenericName> getAlias() {
        return axis.getAlias();
    }

    /**
     * Returns the identifiers of the wrapped axis.
     */
    @Override
    public Set<ReferenceIdentifier> getIdentifiers() {
        return axis.getIdentifiers();
    }

    /**
     * The abbreviation used by the wrapped axis.
     */
    @Override
    public String getAbbreviation() {
        return axis.getAbbreviation();
    }

    /**
     * Returns the direction of the wrapped axis.
     */
    @Override
    public AxisDirection getDirection() {
        return axis.getDirection();
    }

    /**
     * Returns the range meaning of the wrapped axis. Note that this is not related
     * to {@link #getOrdinateRangeAt(int)}.
     */
    @Override
    public RangeMeaning getRangeMeaning() {
        return axis.getRangeMeaning();
    }

    /**
     * Returns the unit of the wrapped axis.
     */
    @Override
    public Unit<?> getUnit() {
        return axis.getUnit();
    }

    /**
     * Returns the minimal value of the wrapped axis.
     */
    @Override
    public double getMinimumValue() {
        return axis.getMinimumValue();
    }

    /**
     * Returns the maximal value of the wrapped axis.
     */
    @Override
    public synchronized double getMaximumValue() {
        return axis.getMaximumValue();
    }

    /**
     * Returns the number of ordinate values.
     */
    @Override
    public int length() {
        return ordinates.length;
    }

    /**
     * Returns the type of ordinate values.
     *
     * @since 3.20
     */
    @Override
    public Class<Double> getElementType() {
        return Double.class;
    }

    /**
     * Returns the ordinate value at the given index.
     */
    @Override
    public Double getOrdinateAt(final int index) throws IndexOutOfBoundsException {
        return ordinates[index];
    }

    /**
     * Returns the range at the given index.
     */
    @Override
    public NumberRange<Double> getOrdinateRangeAt(final int index) throws IndexOutOfBoundsException {
        if (ordinates.length < 2) {
            throw new UnsupportedOperationException();
        }
        final double value = ordinates[index];
        final int lower = (index == 0) ? 0 : index-1;
        final int upper = Math.min(ordinates.length-1, index+1);
        double min = value - 0.5*(ordinates[lower + 1] - ordinates[lower]);
        double max = value + 0.5*(ordinates[upper] - ordinates[upper - 1]);
        final boolean decreasing = (min > max);
        if (decreasing) {
            final double tmp = min;
            min = max;
            max = tmp;
        }
        return NumberRange.create(min, !decreasing, max, decreasing);
    }

    /**
     * Returns the remarks of the wrapped axis.
     */
    @Override
    public InternationalString getRemarks() {
        return axis.getRemarks();
    }

    /**
     * Returns the WKT formatted by the wrapped axis. We can delegates directly to
     * the wrapped array because the ordinate values are not part of WKT formatting.
     */
    @Override
    public String toWKT() throws UnsupportedOperationException {
        return axis.toWKT();
    }

    /**
     * Returns the string representation of the wrapped axis.
     * This is usually the same than the WKT representation.
     */
    @Override
    public String toString() {
        return axis.toString();
    }

    /**
     * Returns a hash code value for this axis.
     */
    @Override
    public int hashCode() {
        return axis.hashCode()  + 31 * Arrays.hashCode(ordinates);
    }

    /**
     * Compares this axis with the given object for equality.
     *
     * @param other The object to compare with this axis for equality.
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof DiscreteAxis) {
            final DiscreteAxis that = (DiscreteAxis) other;
            return axis.equals(that.axis) && Arrays.equals(ordinates, that.ordinates);
        }
        return false;
    }
}
