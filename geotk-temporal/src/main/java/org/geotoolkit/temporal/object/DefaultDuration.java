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

import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.List;


/**
 * A data type to be used for describing length or distance in the temporal dimension.
 *
 * @deprecated Should be used only when the standard {@code java.time} objects do not fit.
 * This class is incorrect because it assumes constant day, month and year lengths.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @author Remi Marechal (Geomatys)
 */
@Deprecated
public class DefaultDuration implements TemporalAmount {
    /**
     * All supported units, in decreasing duration.
     */
    private static final List<TemporalUnit> UNITS = List.of(
        ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.WEEKS,
        ChronoUnit.DAYS,  ChronoUnit.HOURS,  ChronoUnit.MINUTES,
        ChronoUnit.SECONDS, ChronoUnit.MILLIS);

    /**
     * Length of each supported units.
     * Must be in the same order as {@link #UNITS}.
     */
    private static final long[] UNIT_DURATIONS = {
        TemporalConstants.YEAR_MS, TemporalConstants.MONTH_MS, TemporalConstants.WEEK_MS,
        TemporalConstants.DAY_MS,  TemporalConstants.HOUR_MS,  TemporalConstants.MINUTE_MS,
        TemporalConstants.SECOND_MS, 1
    };

    /**
     * Symbols as characters in the same order as above arrays, excluding milliseconds.
     * Note that "M" is repeated twice, for months and for minutes.
     */
    private static final String SYMBOLS = "YMWDHMS";

    private final long[] fields = new long[UNIT_DURATIONS.length];

    final TemporalAmount tryToPeriod() {
        for (int i=4; i<fields.length; i++) {
            if (fields[i] != 0) {
                return this;
            }
        }
        return Period.of(Math.toIntExact(fields[0]), Math.toIntExact(fields[1]), Math.toIntExact(fields[2]*7 + fields[3]));
    }

    /**
     * Creates a new instance initialized to a duration of 0.
     */
    public DefaultDuration() {
    }

    /**
     * {@return the list of units that are supported by this implementation}.
     */
    @Override
    public List<TemporalUnit> getUnits() {
        return UNITS;
    }

    @Override
    public long get(TemporalUnit unit) {
        int i = UNITS.indexOf(unit);
        if (i >= 0) {
            return fields[i];
        } else {
            throw new UnsupportedTemporalTypeException("Unsupported temporal unit: " + unit);
        }
    }

    public void set(TemporalUnit unit, long value) {
        int i = UNITS.indexOf(unit);
        if (i >= 0) {
            fields[i] = value;
        } else {
            throw new UnsupportedTemporalTypeException("Unsupported temporal unit: " + unit);
        }
    }

    /**
     * {@return the current length in milliseconds}.
     */
    public long getTimeInMillis() {
        long duration = 0;
        for (int i=0; i<UNIT_DURATIONS.length; i++) {
            duration = Math.addExact(duration, Math.multiplyExact(fields[i], UNIT_DURATIONS[i]));
        }
        return duration;
    }

    /**
     * Sets the duration in milliseconds.
     */
    public void setTimeInMillis(long durationInMilliSeconds) {
        for (int i=0; i<UNIT_DURATIONS.length; i++) {
            final long length = UNIT_DURATIONS[i];
            fields[i] = durationInMilliSeconds / length;
            durationInMilliSeconds %= length;
        }
    }

    @Override
    public Temporal addTo(Temporal temporal) {
        return ((Instant) temporal).plusMillis(getTimeInMillis());
    }

    @Override
    public Temporal subtractFrom(Temporal temporal) {
        return ((Instant) temporal).minusMillis(getTimeInMillis());
    }

    @Override
    public boolean equals(final Object object) {
        return (object instanceof DefaultDuration) && Arrays.equals(((DefaultDuration) object).fields, fields);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(fields) ^ 5782;
    }

    /**
     * {@return a duration value in ISO 8601 format}. The pattern is PnYnMnDTnHnMnS.
     */
    @Override
    public String toString() {
        var s = new StringBuilder().append("P");
        for (int i=0; i<SYMBOLS.length(); i++) {
            char symbol = SYMBOLS.charAt(i);
            long value = fields[i];
            if (value != 0) {
                s.append(value).append(symbol);
            }
            if (symbol == 'D') {
                s.append("T");
            }
        }
        if (s.charAt(s.length() - 1) == 'T') {
            s.append("0S");
        }
        return s.toString();
    }

    public void parse(final String periodDuration) {
        final int length = periodDuration.length();
        int base = 1;
        if (periodDuration.startsWith("P")) {
            boolean isTime = false;
            for (int i=1; i<length; i++) {
                final char c = Character.toUpperCase(periodDuration.charAt(i));
                if (c == 'T' && base == i) {
                    isTime = true;
                    base = i + 1;
                    continue;
                }
                final int type = isTime ? SYMBOLS.lastIndexOf(c) : SYMBOLS.indexOf(c);
                if (type >= 0) {
                    fields[type] = Long.parseLong(periodDuration, base, i, 10);
                    base = i + 1;
                }
            }
        }
        if (base < length) {
            throw new IllegalArgumentException(
                    "The period descritpion is malformed, should not respect ISO8601 : "
                            + periodDuration);
        }
    }
}
