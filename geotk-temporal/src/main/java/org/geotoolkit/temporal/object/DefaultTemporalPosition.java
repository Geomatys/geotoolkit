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

import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Objects;
import java.util.Optional;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.TemporalPosition;

/**
 * Used for describing temporal positions referenced to other temporal reference systems.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @author Remi Marechal (Geomatys).
 */
public class DefaultTemporalPosition implements TemporalPosition {
    /**
     * This is the {@link TemporalReferenceSystem} associated with this {@link TemporalPosition},
     * if not specified, it is assumed to be an association to the Gregorian calendar and UTC.
     */
    private final TemporalCRS frame;

    /**
     * Provide the only value for {@link TemporalPosition}
     * unless a subtype of {@link TemporalPosition} is used as the data type, or {@code null} if none.
     */
    private IndeterminateValue indeterminatePosition;

    /**
     * Creates a new instance from a {@link TemporalReferenceSystem} and an {@link IndeterminateValue}.
     *
     * @param frame the associated {@link TemporalReferenceSystem}.
     * @param indeterminatePosition Provide the only value for {@link TemporalPosition}
     * unless a subtype of {@link TemporalPosition} is used as the data type, or {@code null} if none.
     * @throws NullPointerException if frame is {@code null}.
     */
    public DefaultTemporalPosition(final TemporalCRS frame, final IndeterminateValue indeterminatePosition) {
        ArgumentChecks.ensureNonNull("frame", frame);
        this.frame                 = frame;
        this.indeterminatePosition = indeterminatePosition;
    }

    @Override
    public Optional<IndeterminateValue> getIndeterminatePosition() {
        return Optional.ofNullable(indeterminatePosition);
    }

    @Override
    public TemporalCRS getFrame() {
        return frame;
    }

    @Override
    public boolean isSupported(TemporalUnit unit) {
        return false;
    }

    @Override
    public Temporal with(TemporalField field, long newValue) {
        throw new UnsupportedTemporalTypeException("Not supported yet.");
    }

    @Override
    public Temporal plus(long amountToAdd, TemporalUnit unit) {
        throw new UnsupportedTemporalTypeException("Not supported yet.");
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        throw new UnsupportedTemporalTypeException("Not supported yet.");
    }

    @Override
    public boolean isSupported(TemporalField field) {
        throw new UnsupportedTemporalTypeException("Not supported yet.");
    }

    @Override
    public long getLong(TemporalField field) {
        throw new UnsupportedTemporalTypeException("Not supported yet.");
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultTemporalPosition that) {
            return Objects.equals(this.frame, that.frame) &&
                    Objects.equals(this.indeterminatePosition, that.indeterminatePosition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.frame != null ? this.frame.hashCode() : 0);
        hash = 37 * hash + (this.indeterminatePosition != null ? this.indeterminatePosition.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("TemporalPosition:").append('\n');
        if (frame != null) {
            s.append("frame:").append(frame).append('\n');
        }
        if (indeterminatePosition != null) {
            s.append("indeterminatePosition:").append(indeterminatePosition).append('\n');
        }
        return s.toString();
    }
}
