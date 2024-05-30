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

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.sis.util.ComparisonMode;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.Period;
import org.opengis.temporal.RelativePosition;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.temporal.TemporalPosition;
import org.opengis.temporal.TemporalPrimitive;

/**
 * A zero-dimensional geometric primitive that represents position in time, equivalent to a point
 * in space.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @author Remi Marechal (Geomatys)
 */
@XmlType(name = "TimeInstant_Type", propOrder = {
    "date"
})
@XmlRootElement(name = "TimeInstant")
public class DefaultInstant extends DefaultTemporalPrimitive implements TemporalGeometricPrimitive, InstantWrapper {
    private static final AtomicLong COUNT = new AtomicLong();

    private final Temporal date;

    /**
     * Describing internal {@link TemporalPosition temporal positions} referenced
     * to other {@linkplain TemporalReferenceSystem temporal reference systems}.
     */
    private final TemporalPosition temporalPosition;

    /**
     * Empty constructor only use for XML binding.
     */
    public DefaultInstant() {
        this.date             = null;
        this.temporalPosition = null;
    }

    public DefaultInstant(Temporal date) throws IllegalArgumentException {
        this(Map.of(NAME_KEY, "period" + COUNT.incrementAndGet()), date);
    }

    /**
     * Creates a default {@link Instant} implementation from the given properties.
     *
     * @param properties The properties to be given to this object.
     */
    public DefaultInstant(Map<String, ?> properties, Temporal date) throws IllegalArgumentException {
        super(properties);
        this.date = Objects.requireNonNull(date);
        this.temporalPosition = null;
    }

    /**
     * Creates a default {@link Instant} implementation from the given properties.
     *
     * @param properties The properties to be given to this object.
     * @param temporalPosition the {@link TemporalPosition} of this {@link Instant},
     * it shall be associated with a single {@link TemporalReferenceSystem}.
     * @throws NullPointerException if temporalPosition is {@code null}.
     */
    public DefaultInstant(final Map<String, ?> properties, final TemporalPosition temporalPosition) throws IllegalArgumentException {
        super(properties);
        this.date = null;
        this.temporalPosition = Objects.requireNonNull(temporalPosition);
    }

    /**
     * {@inheritDoc}.
     * May returns {@code null} if {@link Instant} was create from
     * {@link DefaultInstant#DefaultInstant(java.util.Map, org.opengis.temporal.TemporalPosition) }.
     *
     * @deprecated Use {@link #getInstant()} instead.
     */
    @Deprecated
    @XmlElement(name = "timePosition", required = true)
    public Date getDate() {
        return InstantWrapper.super.getDate();
    }

    @Override
    public Temporal getTemporal() {
        return date;
    }

    /**
     * {@inheritDoc}.
     * May returns {@code null} if {@link Instant} was create from
     * {@link DefaultInstant#DefaultInstant(java.util.Map, java.util.Date)}.
     */
    public TemporalPosition getTemporalPosition() {
        return temporalPosition;
    }

    /**
     * @deprecated Not correctly implemented.
     */
    @Override
    @Deprecated
    public TemporalAmount length() {
        return TemporalUtilities.durationFromMillis(Math.abs(InstantWrapper.toInstant(date).toEpochMilli()));
    }

    /**
     * @deprecated Not correctly implemented.
     */
    @Override
    @Deprecated
    public TemporalAmount distance(final TemporalGeometricPrimitive other) {
        long diff = 0L;
        var pos = relativePosition(other);
        if (pos.equals(RelativePosition.BEFORE) || pos.equals(RelativePosition.AFTER)) {
            final long t0 = InstantWrapper.toInstant(date).toEpochMilli();
            if (other instanceof InstantWrapper t) {
                diff = Math.abs(t0 - InstantWrapper.toInstant(t.getTemporal()).toEpochMilli());
            } else {
                if (other instanceof Period) {
                    diff = Math.min(Math.abs(InstantWrapper.toInstant(((Period) other).getBeginning()).toEpochMilli() - t0),
                            Math.abs(InstantWrapper.toInstant(((Period) other).getEnding()).toEpochMilli() - t0));
                }
            }
        }
        return TemporalUtilities.durationFromMillis(Math.abs(diff));
    }

    @Override
    public RelativePosition relativePosition(final TemporalPrimitive other) {
        final Instant date = InstantWrapper.toInstant(this.date);
        if (other instanceof DefaultInstant instantOther) {
            final Instant otherDate = InstantWrapper.toInstant(instantOther.date);
            // test the relative position when the other paramter has an indeterminate value.
            if (date == null || otherDate == null) {
                if (date != null && instantOther.temporalPosition != null && instantOther.temporalPosition.getIndeterminatePosition() != null) {
                    IndeterminateValue indeterminatePosition = instantOther.temporalPosition.getIndeterminatePosition().orElse(null);
                    if (indeterminatePosition == IndeterminateValue.AFTER) {
                       return RelativePosition.AFTER;
                    } else if (indeterminatePosition == IndeterminateValue.BEFORE) {
                       return RelativePosition.BEFORE;
                    } else if (indeterminatePosition == IndeterminateValue.NOW) {
                       long currentMillis = System.currentTimeMillis();
                       long toMillis = date.toEpochMilli();
                       if (toMillis > currentMillis) {
                           return RelativePosition.AFTER;
                       } else if (toMillis < currentMillis) {
                           return RelativePosition.BEFORE;
                       } else {
                           return RelativePosition.EQUALS;
                       }
                    }
                } else if (otherDate != null && temporalPosition != null && temporalPosition.getIndeterminatePosition() != null) {
                    IndeterminateValue indeterminatePosition =  temporalPosition.getIndeterminatePosition().orElse(null);
                    if (indeterminatePosition == IndeterminateValue.AFTER) {
                       return RelativePosition.AFTER;
                    } else if (indeterminatePosition == IndeterminateValue.BEFORE) {
                       return RelativePosition.BEFORE;
                    } else if (indeterminatePosition == IndeterminateValue.NOW) {
                       long currentMillis = System.currentTimeMillis();
                       long toMillis = otherDate.toEpochMilli();
                       if (toMillis > currentMillis) {
                           return RelativePosition.BEFORE;
                       } else if (toMillis < currentMillis) {
                           return RelativePosition.AFTER;
                       } else {
                           return RelativePosition.EQUALS;
                       }
                    }
                }
                return null;
            } else if (date.isBefore(otherDate)) {
                return RelativePosition.BEFORE;
            } else {
                return (date.compareTo(otherDate) == 0) ? RelativePosition.EQUALS : RelativePosition.AFTER;
            }
        } else if (other instanceof Period instantarg) {
            final Instant ending = InstantWrapper.toInstant(instantarg.getEnding());
            final Instant beginning = InstantWrapper.toInstant(instantarg.getBeginning());
            if (ending.isBefore(date)) {
                return RelativePosition.AFTER;
            } else if (ending.compareTo(date) == 0) {
                return RelativePosition.ENDS;
            } else if (beginning.isBefore(date) &&
                    ending.isAfter(date)) {
                return RelativePosition.DURING;
            } else {
                return (beginning.compareTo(date) == 0) ? RelativePosition.BEGINS : RelativePosition.BEFORE;
            }
        } else {
            return null;
        }
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(Object object, ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultInstant that) {
            return Objects.equals(date, that.date) &&
                   Objects.equals(temporalPosition, that.temporalPosition);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected long computeHashCode() {
        int hash = 5;
        hash = 37 * hash + (date != null ? date.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n').append("Instant:").append('\n');
        if (date != null) {
            s.append("date:").append(date).append('\n');
        }
        if (temporalPosition != null) {
            s.append("temporalPosition :").append(temporalPosition).append('\n');
        }
        return s.toString();
    }
}
