/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.dto.connectedsystems.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.serializer.TimePeriodDeserializer;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.serializer.TimePeriodSerializer;

/**
 * A time period, encoded on the wire as a 2 element JSON array {@code [begin, end]} where each
 * element is either an ISO 8601 date-time string or the literal {@code "now"}.
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
@JsonSerialize(using = TimePeriodSerializer.class)
@JsonDeserialize(using = TimePeriodDeserializer.class)
public class TimePeriod {

    /**
     * ISO 8601 date-time, or the literal {@code "now"}.
     */
    private String begin;

    /**
     * ISO 8601 date-time, or the literal {@code "now"}.
     */
    private String end;

    public TimePeriod() {
    }

    public TimePeriod(String begin, String end) {
        this.begin = begin;
        this.end = end;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public TimePeriod begin(String begin) {
        setBegin(begin);
        return this;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public TimePeriod end(String end) {
        setEnd(end);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TimePeriod other)) return false;
        return Objects.equals(begin, other.begin) && Objects.equals(end, other.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(begin, end);
    }

    @Override
    public String toString() {
        return "[" + begin + ", " + end + "]";
    }

}
