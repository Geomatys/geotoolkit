/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.apache.sis.internal.jaxb.gml;

import java.sql.Time;
import java.util.Date;
import java.util.Collection;
import java.util.Collections;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.Duration;
import org.opengis.temporal.Position;
import org.opengis.temporal.RelativePosition;
import org.opengis.temporal.TemporalPosition;
import org.opengis.temporal.TemporalPrimitive;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.util.InternationalString;


/**
 * A dummy {@link Instant} implementation, for testing the JAXB elements without dependency
 * toward the {@code geotk-temporal} module.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
final class DummyInstant implements Instant, Position {
    /**
     * The time, in milliseconds elapsed since January 1st, 1970.
     */
    private final long time;

    /**
     * Creates a new instant initialized to the given value.
     */
    DummyInstant(final Date time) {
        this.time = time.getTime();
    }

    /**
     * Returns the date of this instant object.
     */
    @Override
    public Date getDate() {
        return new Date(time);
    }

    /**
     * Returns the position, which is {@code this}.
     */
    @Override
    public Position getPosition() {
        return this;
    }

    /**
     * Empty properties.
     */
    @Override public Time                getTime()     {return null;}
    @Override public InternationalString getDateTime() {return null;}
    @Override public TemporalPosition    anyOther()    {return null;}
    @Override public Collection<Period>  getBegunBy()  {return Collections.emptySet();}
    @Override public Collection<Period>  getEndedBy()  {return Collections.emptySet();}
    @Override public Duration            length()      {return null;}

    /**
     * Unsupported operations.
     */
    @Override public RelativePosition relativePosition(TemporalPrimitive  other) {throw new UnsupportedOperationException();}
    @Override public Duration         distance(TemporalGeometricPrimitive other) {throw new UnsupportedOperationException();}
}
