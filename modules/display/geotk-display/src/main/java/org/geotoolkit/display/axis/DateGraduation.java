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
package org.geotoolkit.display.axis;

import java.util.Date;
import java.util.TimeZone;
import java.text.DateFormat;
import java.awt.RenderingHints;

import javax.measure.unit.Unit;
import javax.measure.quantity.Duration;
import javax.measure.converter.UnitConverter;

import org.apache.sis.measure.Units;
import org.geotoolkit.util.Utilities;

import static org.apache.sis.measure.Units.MILLISECOND;


/**
 * A graduation using dates on a linear axis.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public class DateGraduation extends AbstractGraduation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -7590383805990568769L;

    /**
     * The minimal value for this graduation, in milliseconds elapsed since January 1st,
     * 1970 (no matter what the graduation units are). Default to current time (today).
     */
    private long minimum = System.currentTimeMillis();

    /**
     * The maximal value for this graduation, in milliseconds elapsed since January 1st,
     * 1970 (no matter what the graduation units are). Default to tomorrow.
     */
    private long maximum = minimum + 24*60*60*1000L;

    /**
     * The time zone for graduation labels.
     */
    private TimeZone timezone;

    /**
     * The converter from {@link org.geotoolkit.measure.Units#MILLISECOND} to {@link #getUnit}.
     * Will be created only when first needed.
     */
    private transient UnitConverter fromMillis;

    /**
     * The converter from {@link #getUnit} to {@link org.geotoolkit.measure.Units#MILLISECOND}.
     * Will be created only when first needed.
     */
    private transient UnitConverter toMillis;

    /**
     * Constructs a graduation with the supplied time zone.
     * Unit default to {@linkplain org.geotoolkit.measure.Units#MILLISECOND milliseconds}.
     *
     * @param  timezone The timezone.
     */
    public DateGraduation(final TimeZone timezone) {
        this(timezone, MILLISECOND);
    }

    /**
     * Constructs a graduation with the supplied time zone and unit.
     *
     * @param  timezone The timezone.
     * @param  unit The unit, or {@code null} if unknown. Must be compatible with
     *         {@linkplain org.geotoolkit.measure.Units#MILLISECOND milliseconds}.
     */
    public DateGraduation(final TimeZone timezone, final Unit<Duration> unit) {
        super(Units.ensureTemporal(unit));
        this.timezone = (TimeZone) timezone.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked") // Checked by constructor and setters.
    public Unit<Duration> getUnit() {
        return (Unit<Duration>) super.getUnit();
    }

    /**
     * Returns the converter from {@link org.geotoolkit.measure.Units#MILLISECOND}
     * to {@link #getUnit}.
     */
    private UnitConverter fromMillis() {
        assert Thread.holdsLock(this);
        if (fromMillis == null) {
            Unit<Duration> unit = getUnit();
            if (unit == null) {
                unit = MILLISECOND;
            }
            fromMillis = MILLISECOND.getConverterTo(unit);
        }
        return fromMillis;
    }

    /**
     * Returns the converter from {@link #getUnit} to {@link org.geotoolkit.measure.Units#MILLISECOND}.
     */
    private UnitConverter toMillis() {
        assert Thread.holdsLock(this);
        if (toMillis == null) {
            Unit<Duration> unit = getUnit();
            if (unit == null) {
                unit = MILLISECOND;
            }
            toMillis = unit.getConverterTo(MILLISECOND);
        }
        return toMillis;
    }

    /**
     * Set the minimum value for this graduation. If the new minimum is greater than the current
     * maximum, then the maximum will also be set to a value greater than or equal to the minimum.
     *
     * @param  time The new minimum.
     * @return {@code true} if the state of this graduation changed as a result of this call, or
     *         {@code false} if the new value is identical to the previous one.
     *
     * @see #setMaximum(Date)
     */
    public boolean setMinimum(final Date time) {
        final long value = time.getTime();
        final long oldMin, oldMax;
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
        firePropertyChange("minimum", oldMin, time);
        if (changed) {
            firePropertyChange("maximum", oldMax, time);
        }
        return changed || (value != oldMin);
    }

    /**
     * Set the maximum value for this graduation. If the new maximum is less than the current
     * minimum, then the minimum will also be set to a value less than or equal to the maximum.
     *
     * @param  time The new maximum.
     * @return {@code true} if the state of this graduation changed as a result of this call, or
     *         {@code false} if the new value is identical to the previous one.
     *
     * @see #setMinimum(Date)
     */
    public boolean setMaximum(final Date time) {
        final long value = time.getTime();
        final long oldMin, oldMax;
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
        firePropertyChange("maximum", oldMax, time);
        if (changed) {
            firePropertyChange("minimum", oldMin, time);
        }
        return changed || (value != oldMax);
    }

    /**
     * Sets the minimum value as a real number. This method converts the value to
     * {@linkplain org.geotoolkit.measure.Units#MILLISECOND milliseconds} and invokes
     * {@link #setMinimum(Date)}.
     */
    @Override
    public final boolean setMinimum(final double value) {
        ensureFinite("minimum", value);
        final Date time;
        synchronized (this) {
            time = new Date(Math.round(toMillis().convert(value)));
        }
        return setMinimum(time);
    }

    /**
     * Sets the maximum value as a real number. This method converts the value to
     * {@linkplain org.geotoolkit.measure.Units#MILLISECOND milliseconds} and invokes
     * {@link #setMaximum(Date)}.
     */
    @Override
    public final boolean setMaximum(final double value) {
        ensureFinite("maximum", value);
        final Date time;
        synchronized (this) {
            time = new Date(Math.round(toMillis().convert(value)));
        }
        return setMaximum(time);
    }

    /**
     * Returns the minimal value for this graduation. The value is in units of {@link #getUnit}.
     * By default, it is the number of milliseconds elapsed since January 1st, 1970 at 00:00 UTC.
     *
     * @see #setMinimum(double)
     * @see #getMaximum
     * @see #getSpan
     */
    @Override
    public synchronized double getMinimum() {
        return fromMillis().convert(minimum);
    }

    /**
     * Returns the maximal value for this graduation. The value is in units of {@link #getUnit}.
     * By default, it is the number of milliseconds elapsed since January 1st, 1970 at 00:00 UTC.
     *
     * @see #setMaximum(double)
     * @see #getMinimum
     * @see #getSpan
     */
    @Override
    public synchronized double getMaximum() {
        return fromMillis().convert(maximum);
    }

    /**
     * Returns the graduation's range. This is equivalents to computing
     * <code>{@link #getMaximum} - {@link #getMinimum}</code>, but using integer arithmetic.
     */
    @Override
    public synchronized double getSpan() {
        if (getUnit() == MILLISECOND) {
            return maximum - minimum;
        } else {
            // TODO: we would need something similar to AffineTransform.deltaTransform(...)
            //       here in order to performs the conversion in a more efficient way.
            final UnitConverter toMillis = toMillis();
            return toMillis.convert(maximum) - toMillis.convert(minimum);
        }
    }

    /**
     * Returns the timezone for this graduation.
     *
     * @return The current timezone.
     */
    public synchronized TimeZone getTimeZone() {
        return timezone;
    }

    /**
     * Sets the time zone for this graduation. This affect only the way labels are displayed.
     *
     * @param timezone The new timezone.
     */
    public synchronized void setTimeZone(final TimeZone timezone) {
        this.timezone = (TimeZone) timezone.clone();
    }

    /**
     * Returns a string representation of the time zone for this graduation.
     */
    @Override
    String getSymbol() {
        return getTimeZone().getDisplayName();
    }

    /**
     * Changes the graduation units. This method will automatically convert minimum and maximum
     * values from the old units to the new one.
     *
     * @param unit The new units, or {@code null} if unknown. If null, minimum and maximum values
     *             are not converted.
     * @throws IllegalArgumentException if the specified unit is not a time unit.
     */
    @Override
    public void setUnit(final Unit<?> unit) throws IllegalArgumentException {
        Units.ensureTemporal(unit);
        fromMillis = null;
        toMillis   = null;
        // Nothing to convert here. The conversions are performed
        // on the fly by 'getMinimum()' / 'getMaximum()'.
        super.setUnit(unit);
    }

    /**
     * Returns the format to use for formatting labels. The format really used by
     * {@link TickIterator#currentLabel} may not be the same. For example, some
     * iterators may choose to show or hide hours, minutes and seconds.
     */
    @Override
    public synchronized DateFormat getFormat() {
        final DateFormat format = DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.SHORT, getLocale());
        format.setTimeZone(timezone);
        return format;
    }

    /**
     * Returns an iterator object that iterates along the graduation ticks
     * and provides access to the graduation values. If an optional {@link
     * RenderingHints} is specified, tick locations are adjusted according
     * values for {@link #VISUAL_AXIS_LENGTH} and {@link #VISUAL_TICK_SPACING}
     * keys.
     *
     * @param  hints Rendering hints, or {@code null} for the default hints.
     * @param  reuse An iterator to reuse if possible, or {@code null}
     *         to create a new one. A non-null object may help to reduce the
     *         number of object garbage-collected when rendering the axis.
     * @return A iterator to use for iterating through the graduation. This
     *         iterator may or may not be the {@code reuse} object.
     */
    @Override
    public synchronized TickIterator getTickIterator(final RenderingHints hints,
                                                     final TickIterator   reuse)
    {
        final float visualAxisLength  = getVisualAxisLength (hints);
        final float visualTickSpacing = getVisualTickSpacing(hints);
        long minimum = this.minimum;
        long maximum = this.maximum;
        if (!(minimum < maximum)) { // Uses '!' for catching NaN.
            minimum = (minimum+maximum)/2 - 12*60*60*1000L;
            maximum = minimum + 24*60*60*1000L;
        }
        final DateIterator it;
        if (reuse instanceof DateIterator) {
            it = (DateIterator) reuse;
            it.setLocale(getLocale());
            it.setTimeZone(getTimeZone());
        } else {
            it = new DateIterator(getTimeZone(), getLocale());
        }
        it.init(minimum, maximum, visualAxisLength, visualTickSpacing);
        return it;
    }

    /**
     * Support for reporting property changes. This method can be called when a property
     * has changed. It will send the appropriate {@link java.beans.PropertyChangeEvent}
     * to any registered {@link PropertyChangeListeners}.
     *
     * @param propertyName The property whose value has changed.
     * @param oldValue     The property's previous value.
     * @param newValue     The property's new value.
     */
    private void firePropertyChange(final String propertyName,
                                    final long oldValue, final Date newValue)
    {
        if (oldValue != newValue.getTime()) {
            listenerList.firePropertyChange(propertyName, new Date(oldValue), newValue);
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
            final DateGraduation that = (DateGraduation) object;
            // We should lock object as well, but can't because of deadlocks.
            synchronized (this) {
                return this.minimum == that.minimum &&
                       this.maximum == that.maximum &&
                       Utilities.equals(this.timezone, that.timezone);
            }
        }
        return false;
    }

    /**
     * Returns a hash value for this graduation.
     */
    @Override
    public synchronized int hashCode() {
        final long lcode = minimum + 31*maximum;
        int code = (int)lcode ^ (int)(lcode >>> 32);
        if (timezone != null) {
            code ^= timezone.hashCode();
        }
        return code ^ super.hashCode();
    }
}
