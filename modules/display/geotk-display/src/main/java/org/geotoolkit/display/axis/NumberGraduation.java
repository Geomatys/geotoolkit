/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display.axis;

import java.util.Locale;
import java.text.Format;
import java.text.NumberFormat;
import java.awt.RenderingHints;

import javax.measure.unit.Unit;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.ConversionException;

import static java.lang.Double.doubleToLongBits;


/**
 * A graduation using numbers on a linear axis.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.0
 *
 * @since 2.0
 * @module
 */
public class NumberGraduation extends AbstractGraduation {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -3074504745332240845L;

    /**
     * The minimal value for this graduation. Default to 0.
     */
    private double minimum = 0;

    /**
     * The maximal value for this graduation. Default to 10.
     */
    private double maximum = 10;

    /**
     * Constructs a graduation with the supplied units.
     *
     * @param unit The axis's units, or {@code null} if unknow.
     */
    public NumberGraduation(final Unit<?> unit) {
        super(unit);
    }

    /**
     * Sets the minimum value for this graduation. If the new minimum is greater
     * than the current maximum, then the maximum will also be set to a value
     * greater than or equal to the minimum.
     *
     * @param  value The new minimum in {@link #getUnit} units.
     * @return {@code true} if the state of this graduation changed as a result of this call,
     *         or {@code false} if the new value is identical to the previous one.
     * @throws IllegalArgumentException Si {@code value} is NaN ou infinite.
     *
     * @see #getMinimum
     * @see #setMaximum(double)
     */
    @Override
    public boolean setMinimum(final double value) throws IllegalArgumentException {
        ensureFinite("minimum", value);
        final double oldMin, oldMax;
        final boolean changed;
        synchronized (this) {
            oldMin = minimum;
            oldMax = maximum;
            minimum = value;
            if (maximum < value) {
                maximum = value;
                changed = true;
            } else {
                changed = false;
            }
        }
        final Double valueObject = value;
        listenerList.firePropertyChange("minimum", oldMin, valueObject);
        if (changed) {
            listenerList.firePropertyChange("maximum", oldMax, valueObject);
        }
        return changed || doubleToLongBits(value) != doubleToLongBits(oldMin);
    }

    /**
     * Sets the maximum value for this graduation. If the new maximum is less than the current
     * minimum, then the minimum will also be set to a value less than or equal to the maximum.
     *
     * @param  value The new maximum in {@link #getUnit} units.
     * @return {@code true} if the state of this graduation changed as a result of this call,
     *         or {@code false} if the new value is identical to the previous one.
     * @throws IllegalArgumentException If {@code value} is NaN ou infinite.
     *
     * @see #getMaximum
     * @see #setMinimum(double)
     */
    @Override
    public boolean setMaximum(final double value) throws IllegalArgumentException {
        ensureFinite("maximum", value);
        final double oldMin, oldMax;
        final boolean changed;
        synchronized (this) {
            oldMin = minimum;
            oldMax = maximum;
            maximum = value;
            if (minimum > value) {
                minimum = value;
                changed = true;
            } else {
                changed = false;
            }
        }
        final Double valueObject = value;
        listenerList.firePropertyChange("maximum", oldMax, valueObject);
        if (changed) {
            listenerList.firePropertyChange("minimum", oldMin, valueObject);
        }
        return changed || doubleToLongBits(value) != doubleToLongBits(oldMax);
    }

    /**
     * Returns the minimal value for this graduation
     *
     * @return The minimal value in {@link #getUnit} units.
     *
     * @see #setMinimum(double)
     * @see #getMaximum
     * @see #getRange
     */
    @Override
    public synchronized double getMinimum() {
        return minimum;
    }

    /**
     * Returns the maximal value for this graduation.
     *
     * @return The maximal value in {@link #getUnit} units.
     *
     * @see #setMaximum(double)
     * @see #getMinimum
     * @see #getRange
     */
    @Override
    public synchronized double getMaximum() {
        return maximum;
    }

    /**
     * Returns the graduation's range. This is equivalents to computing
     * <code>{@linkplain #getMaximum} - {@linkplain #getMinimum}</code>.
     */
    @Override
    public synchronized double getRange() {
        return maximum - minimum;
    }

    /**
     * Sets the graduation's minimum, maximum and units. This method will fire property change
     * events for {@code "minimum"}, {@code "maximum"} and {@code "unit"} property names.
     *
     * @param min The minimal value in the graduation.
     * @param max The maximal value in the graduation.
     * @param unit The graduation unit.
     */
    public void setRange(double min, double max, final Unit<?> unit) {
        final Double  oldMin;
        final Double  oldMax;
        final Unit<?> oldUnit;
        synchronized (this) {
            oldMin  = this.minimum;
            oldMax  = this.maximum;
            oldUnit = this.unit;
            this.unit    = unit;
            this.minimum = Math.min(min, max);
            this.maximum = Math.max(min, max);
            min = minimum;
            max = maximum;
        }
        listenerList.firePropertyChange("unit",    oldUnit, unit);
        listenerList.firePropertyChange("minimum", oldMin,  min);
        listenerList.firePropertyChange("maximum", oldMax,  max);
    }

    /**
     * Changes the graduation's units. This method will automatically convert minimum and
     * maximum values from the old units to the new one.
     *
     * @param  unit The new units, or {@code null} if unknow.
     *         If null, minimum and maximum values are not converted.
     * @throws ConversionException if units are not convertible.
     */
    @Override
    public void setUnit(final Unit<?> unit) throws ConversionException {
        final Double oldMin;
        final Double oldMax;
        final Unit<?> oldUnit;
        double min, max;
        synchronized (this) {
            oldMin  = min = minimum;
            oldMax  = max = maximum;
            oldUnit = this.unit;
            if (oldUnit!=null && unit!=null) {
                final UnitConverter converter = oldUnit.getConverterTo(unit);
                min = converter.convert(min);
                max = converter.convert(max);
            }
            this.unit    = unit;
            this.minimum = Math.min(min, max);
            this.maximum = Math.max(min, max);
            min = minimum;
            max = maximum;
        }
        listenerList.firePropertyChange("unit",    oldUnit, unit);
        listenerList.firePropertyChange("minimum", oldMin,  min);
        listenerList.firePropertyChange("maximum", oldMax,  max);
    }

    /**
     * Returns the format to use for formatting labels. The format really used by
     * {@link TickIterator#currentLabel} may not be the same. For example, some
     * iterators may adjust automatically the number of fraction digits.
     */
    @Override
    public Format getFormat() {
        return NumberFormat.getNumberInstance(getLocale());
    }

    /**
     * Returns an iterator object that iterates along the graduation ticks and provides access to
     * the graduation values. If an optional {@link RenderingHints} is specified, tick locations are
     * adjusted according values for {@link #VISUAL_AXIS_LENGTH} and {@link #VISUAL_TICK_SPACING}
     * keys.
     *
     * @param  hints Rendering hints, or {@code null} for the default hints.
     * @param  reuse An iterator to reuse if possible, or {@code null} to create a new one. A
     *         non-null object may help to reduce the number of object garbage-collected when
     *         rendering the axis.
     * @return A iterator to use for iterating through the graduation. This
     *         iterator may or may not be the {@code reuse} object.
     */
    @Override
    public synchronized TickIterator getTickIterator(final RenderingHints hints,
                                                     final TickIterator reuse)
    {
        final float visualAxisLength  = getVisualAxisLength (hints);
        final float visualTickSpacing = getVisualTickSpacing(hints);
        double minimum = this.minimum;
        double maximum = this.maximum;
        if (!(minimum < maximum)) {  // Uses '!' for catching NaN.
            minimum = (minimum + maximum) * 0.5 - 0.5;
            maximum = minimum + 1;
        }
        final NumberIterator it = getTickIterator(reuse, getLocale());
        it.init(minimum, maximum, visualAxisLength, visualTickSpacing);
        return it;
    }

    /**
     * Constructs or reuses an iterator. This method is overridden by
     * {@link LogarithmicNumberGraduation}.
     */
    NumberIterator getTickIterator(final TickIterator reuse, final Locale locale) {
        if (reuse!=null && reuse.getClass().equals(NumberIterator.class)) {
            final NumberIterator it = (NumberIterator) reuse;
            it.setLocale(locale);
            return it;
        } else {
            return new NumberIterator(locale);
        }
    }

    /**
     * Compares this graduation with the specified object for equality.
     * This method do not compare registered listeners.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final NumberGraduation that = (NumberGraduation) object;
            // We should lock object as well, but can't because of deadlocks.
            synchronized (this) {
                return doubleToLongBits(this.minimum) == doubleToLongBits(that.minimum) &&
                       doubleToLongBits(this.maximum) == doubleToLongBits(that.maximum);
            }
        }
        return false;
    }

    /**
     * Returns a hash value for this graduation.
     */
    @Override
    public synchronized int hashCode() {
        final long code = doubleToLongBits(minimum) +
                     31 * doubleToLongBits(maximum);
        return (int)code ^ (int)(code >>> 32) ^ super.hashCode();
    }
}
