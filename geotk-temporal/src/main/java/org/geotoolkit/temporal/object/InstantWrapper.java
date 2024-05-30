/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2024, Geomatys
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
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Optional;
import org.opengis.temporal.TemporalPrimitive;


/**
 * Wrappers for a {@code java.time.temporal.Temporal}, usually an {@code Instant}.
 * Used during the transition to Java time API.
 */
public interface InstantWrapper {
    /**
     * {@return the wrapped temporal}.
     */
    Temporal getTemporal();

    static Instant toInstant(Temporal t) {
        return org.apache.sis.util.privy.TemporalDate.toInstant(t, ZoneOffset.UTC);
    }

    @Deprecated
    default Date getDate() {
        return org.apache.sis.util.privy.TemporalDate.toDate(getTemporal());
    }

    public static Optional<Temporal> unwrap(Object value) {
        if (value instanceof InstantWrapper t) {
            return Optional.ofNullable(t.getTemporal());
        }
        if (value instanceof TemporalPrimitive t) {
            value = t.position();
            if (value instanceof InstantWrapper w) {
                return Optional.ofNullable(w.getTemporal());
            }
        }
        if (value instanceof Instant t) {
            return Optional.of(t);
        }
        return Optional.empty();
    }
}
