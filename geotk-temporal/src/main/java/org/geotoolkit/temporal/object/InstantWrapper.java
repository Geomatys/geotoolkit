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
import java.util.Date;
import java.util.Optional;
import org.opengis.temporal.TemporalPrimitive;


/**
 * Wrappers for a {@code java.time.Instant}.
 * Used during the transition to Java time API.
 */
public interface InstantWrapper {
    /**
     * {@return the wrapped instant}.
     */
    Instant getInstant();

    @Deprecated
    default Date getDate() {
        Instant t = getInstant();
        return (t != null) ? Date.from(t) : null;
    }

    public static Optional<Instant> unwrap(Object value) {
        if (value instanceof InstantWrapper t) {
            return Optional.ofNullable(t.getInstant());
        }
        if (value instanceof TemporalPrimitive t) {
            value = t.position();
            if (value instanceof InstantWrapper w) {
                return Optional.ofNullable(w.getInstant());
            }
        }
        if (value instanceof Instant t) {
            return Optional.of(t);
        }
        return Optional.empty();
    }
}
