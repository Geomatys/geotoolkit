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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.temporal.reference.DefaultTemporalReferenceSystem;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.TemporalPosition;
import org.opengis.temporal.TemporalReferenceSystem;

/**
 * Used for describing temporal positions referenced to other temporal reference systems.
 * 
 * @author Mehdi Sidhoum (Geomatys)
 * @author Remi Marechal (Geomatys).
 */
public class DefaultTemporalPosition implements TemporalPosition {

    /**
     * A default {@link TemporalReferenceSystem} if {@link #frame} is not specified assume Gregorian calendar.
     */
    private static final TemporalReferenceSystem GREGORIAN_CALENDAR;
    
    static {
        final Map<String, Object> gregUTCProp = new HashMap<>();
        gregUTCProp.put(IdentifiedObject.NAME_KEY, "Default Gregorian calendar for position");
        gregUTCProp.put(IdentifiedObject.IDENTIFIERS_KEY, new NamedIdentifier(Citations.CRS, new SimpleInternationalString("Gregorian calendar")));
        GREGORIAN_CALENDAR = new DefaultTemporalReferenceSystem(gregUTCProp);
    }
    
    /**
     * This is the {@link TemporalReferenceSystem} associated with this {@link TemporalPosition}, 
     * if not specified, it is assumed to be an association to the Gregorian calendar and UTC.
     */
    private final TemporalReferenceSystem frame;
    
    /**
     * Provide the only value for {@link TemporalPosition} 
     * unless a subtype of {@link TemporalPosition} is used as the data type, or {@code null} if none.
     */
    private IndeterminateValue indeterminatePosition;

    /**
     * Create a default Geotk implementation of {@link TemporalPosition} with the 
     * {@linkplain #frame associate Temporal Reference system} initialized by Gregorian calendar value. 
     * 
     * @param indeterminatePosition Provide the only value for {@link TemporalPosition} 
     * unless a subtype of {@link TemporalPosition} is used as the data type, or {@code null}.
     */
    public DefaultTemporalPosition(final IndeterminateValue indeterminatePosition) {
        this(GREGORIAN_CALENDAR, indeterminatePosition);
    }
    
    /**
     * Creates a new instance from a {@link TemporalReferenceSystem} and an {@link IndeterminateValue}.
     * 
     * @param frame the associated {@link TemporalReferenceSystem}.
     * @param indeterminatePosition Provide the only value for {@link TemporalPosition} 
     * unless a subtype of {@link TemporalPosition} is used as the data type, or {@code null} if none.
     * @throws NullArgumentException if frame is {@code null}.
     */
    public DefaultTemporalPosition(final TemporalReferenceSystem frame, final IndeterminateValue indeterminatePosition) {
        ArgumentChecks.ensureNonNull("frame", frame);
        this.frame                 = frame;
        this.indeterminatePosition = indeterminatePosition;
    }

    /**
     * {@inheritDoc }
     * May return {@code null}.
     */
    @Override
    public IndeterminateValue getIndeterminatePosition() {
        return this.indeterminatePosition;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalReferenceSystem getFrame() {
        return frame;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultTemporalPosition) {
            final DefaultTemporalPosition that = (DefaultTemporalPosition) object;

            return Objects.equals(this.frame, that.frame) &&
                    Objects.equals(this.indeterminatePosition, that.indeterminatePosition);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.frame != null ? this.frame.hashCode() : 0);
        hash = 37 * hash + (this.indeterminatePosition != null ? this.indeterminatePosition.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
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
