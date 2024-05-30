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

import java.util.Map;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ComparisonMode;
import org.opengis.temporal.Period;
import org.opengis.temporal.RelativePosition;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.temporal.TemporalPrimitive;

/**
 * A one-dimensional geometric primitive that represent extent in time.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @author Remi Marechal (Geomatys)
 */
@XmlType(name = "TimePeriod_Type", propOrder = {
    "beginning",
    "ending",
    "duration"
})
@XmlRootElement(name = "TimePeriod")
public class DefaultPeriod extends DefaultTemporalPrimitive implements Period {
    private static final AtomicLong COUNT = new AtomicLong();

    /**
     * This is the {@link Instant} at which this Period starts.
     */
    @XmlElement(name = "begin", required = true)
    public final DefaultInstant beginning;

    /**
     * This is the {@link Instant} at which this Period ends.
     */
    @XmlElement(name = "end", required = true)
    public final DefaultInstant ending;

    /**
     * Empty constructor only use for XML binding.
     */
    private DefaultPeriod() {
        beginning = ending = null;
    }

    public DefaultPeriod(final DefaultInstant beginning, final DefaultInstant ending) {
        this(Map.of(NAME_KEY, "period" + COUNT.incrementAndGet()), beginning, ending);
    }

    /**
     * Creates a default {@link Period} implementation from the given properties.
     *
     * @param properties The properties to be given to this object.
     * @param beginning begin instant of the period.
     * @param ending end instant of the period.
     * @throws IllegalArgumentException
     */
    public DefaultPeriod(final Map<String,?> properties, final DefaultInstant beginning, final DefaultInstant ending) {
        super(properties);
        ArgumentChecks.ensureNonNull("begining", beginning);
        ArgumentChecks.ensureNonNull("ending", ending);
        //-- begining must be before or equals to ending
        if (beginning != null &&
           (RelativePosition.BEFORE.equals(beginning.relativePosition(ending)) ||
            RelativePosition.EQUALS.equals(beginning.relativePosition(ending)))) {
             this.beginning = beginning;
             this.ending = ending;
        } else {
            throw new IllegalArgumentException("begining must be before or equals ending");
        }
    }

    /**
     * Returns {@link Period} to the {@link Instant} at which it starts.
     *
     * @return {@link Period} to the {@link Instant} at which it starts.
     */
    @Override
    public final Temporal getBeginning() {
        return beginning.getTemporal();
    }

    /**
     * Returns {@link Period} to the {@link Instant} at which it ends.
     *
     * @return {@link Period} to the {@link Instant} at which it ends.
     */
    @Override
    public final Temporal getEnding() {
        return ending.getTemporal();
    }

    /**
     * @deprecated Not correctly implemented.
     */
    @Override
    @Deprecated
    public TemporalAmount length() {
        return (beginning != null && ending != null) ? beginning.distance(ending) : null;
    }

    /**
     * @deprecated Not correctly implemented.
     */
    @Override
    @Deprecated
    public TemporalAmount distance(final TemporalGeometricPrimitive other) {
        long diff = 0L;
        final long start = InstantWrapper.toInstant(getBeginning()).toEpochMilli();
        final long end   = InstantWrapper.toInstant(getEnding()).toEpochMilli();
        final RelativePosition pos = relativePosition(other);
        if (pos.equals(RelativePosition.BEFORE) || pos.equals(RelativePosition.AFTER)) {
            if (other instanceof InstantWrapper t) {
                final long t0 = InstantWrapper.toInstant(t.getTemporal()).toEpochMilli();
                diff = Math.min(Math.abs(t0 - end), Math.abs(t0 - start));
            } else {
                if (other instanceof Period p) {
                    diff = Math.min(Math.abs(InstantWrapper.toInstant(p.getEnding()).toEpochMilli() - start),
                            Math.abs(InstantWrapper.toInstant(p.getBeginning()).toEpochMilli() - end));
                }
            }
        }
        return TemporalUtilities.durationFromMillis(Math.abs(diff));
    }

    @Override
    public RelativePosition relativePosition(final TemporalPrimitive other) {
        final Instant start = InstantWrapper.toInstant(getBeginning());
        final Instant end = InstantWrapper.toInstant(getEnding());
        if (other instanceof InstantWrapper instantarg) {
            Instant t = InstantWrapper.toInstant(instantarg.getTemporal());
            if (end.isBefore(t)) {
                return RelativePosition.BEFORE;
            } else if (end.compareTo(t) == 0) {
                return RelativePosition.ENDED_BY;
            } else if (start.isBefore(t) &&
                end.isAfter(t)) {
                return RelativePosition.CONTAINS;
            } else {
                 return (start.compareTo(t) == 0) ? RelativePosition.BEGUN_BY : RelativePosition.AFTER;
            }
        } else if (other instanceof Period instantarg) {
            final var otherStart = InstantWrapper.toInstant(instantarg.getBeginning());
            final var otherEnd = InstantWrapper.toInstant(instantarg.getEnding());
            if (end.isBefore(otherStart)) {
                return RelativePosition.BEFORE;
            } else if (end.compareTo(otherStart) == 0) {
                return RelativePosition.MEETS;
            } else if (start.isBefore(otherStart) && end.isAfter(otherStart) && end.isBefore(otherEnd)) {
                return RelativePosition.OVERLAPS;
            } else if (start.compareTo(otherStart) == 0 && end.isBefore(otherEnd)) {
                return RelativePosition.BEGINS;
            } else if (start.compareTo(otherStart) == 0 && end.isAfter(otherEnd)) {
                return RelativePosition.BEGUN_BY;
            } else if (start.isAfter(otherStart) && end.isBefore(otherEnd)) {
                return RelativePosition.DURING;
            } else if (start.isBefore(otherStart) && end.isAfter(otherEnd)) {
                return RelativePosition.CONTAINS;
            } else if (start.compareTo(otherStart) == 0 && end.compareTo(otherEnd) == 0) {
                return RelativePosition.EQUALS;
            } else if (start.isAfter(otherStart) && start.isBefore(otherEnd) && end.isAfter(otherEnd)) {
                return RelativePosition.OVERLAPPED_BY;
            } else if (start.isAfter(otherStart) && end.compareTo(otherEnd) == 0) {
                return RelativePosition.ENDS;
            } else if (start.isBefore(otherStart) && end.compareTo(otherEnd) == 0) {
                return RelativePosition.ENDED_BY;
            } else {
                return (start.compareTo(otherEnd) == 0) ? RelativePosition.MET_BY : RelativePosition.AFTER;
            }
        } else {
            return null;
        }
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, ComparisonMode comp) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultPeriod) {
            final DefaultPeriod that = (DefaultPeriod) object;

            return Objects.equals(beginning, that.beginning) &&
                   Objects.equals(ending, that.ending);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected long computeHashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(beginning);
        hash = 37 * hash + Objects.hashCode(ending);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Period:").append('\n');
        if (beginning != null) {
            s.append("begin:").append(beginning).append('\n');
        }
        if (ending != null) {
            s.append("end:").append(ending).append('\n');
        }
        return s.toString();
    }
}
