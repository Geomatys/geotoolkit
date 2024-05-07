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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import java.time.temporal.TemporalAmount;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.RelativePosition;
import org.opengis.temporal.Separation;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 * An abstract class with two subclasses for representing
 * a temporal instant and a temporal period.
 *
 * @author Mehdi Sidhoum (Geomatys)
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class DefaultTemporalGeometricPrimitive extends DefaultTemporalPrimitive implements TemporalGeometricPrimitive, Separation {
    public DefaultTemporalGeometricPrimitive(Map<String, ?> properties) throws IllegalArgumentException {
        super(properties);
    }

    protected DefaultTemporalGeometricPrimitive() {
        super();
    }

    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Instant to copy values from, or {@code null} if none.
     */
    protected DefaultTemporalGeometricPrimitive(final TemporalGeometricPrimitive object) {
        super(object);
    }

    /**
     * Returns the distance from this TM_GeometricPrimitive to another TM_GeometricPrimitive,
     * i.e. the absolute value of the difference between their temporal positions.
     * @return Duration between this geometry and the given one.
     */
    @Override
    public TemporalAmount distance(final TemporalGeometricPrimitive other) {
        long diff = 0L;
        if (this.relativePosition(other).equals(RelativePosition.BEFORE) || this.relativePosition(other).equals(RelativePosition.AFTER)) {
            if (this instanceof Instant && other instanceof Instant) {
                diff = Math.min(Math.abs(((Instant) other).getDate().getTime() - ((Instant) this).getDate().getTime()),
                        Math.abs(((Instant) this).getDate().getTime() - ((Instant) other).getDate().getTime()));
            } else {
                if (this instanceof Instant && other instanceof Period) {
                    diff = Math.min(Math.abs(((Period) other).getBeginning().getDate().getTime() - ((Instant) this).getDate().getTime()),
                            Math.abs(((Period) other).getEnding().getDate().getTime() - ((Instant) this).getDate().getTime()));
                } else {
                    if (this instanceof Period && other instanceof Instant) {
                        diff = Math.min(Math.abs(((Instant) other).getDate().getTime() - ((Period) this).getEnding().getDate().getTime()),
                                Math.abs(((Instant) other).getDate().getTime() - ((Period) this).getBeginning().getDate().getTime()));
                    } else {
                        if (this instanceof Period && other instanceof Period) {
                            diff = Math.min(Math.abs(((Period) other).getEnding().getDate().getTime() - ((Period) this).getBeginning().getDate().getTime()),
                                    Math.abs(((Period) other).getBeginning().getDate().getTime() - ((Period) this).getEnding().getDate().getTime()));
                        }
                    }
                }
            }
        } else {
            if (this.relativePosition(other).equals(RelativePosition.BEGINS) ||
                    this.relativePosition(other).equals(RelativePosition.BEGUN_BY) ||
                    this.relativePosition(other).equals(RelativePosition.CONTAINS) ||
                    this.relativePosition(other).equals(RelativePosition.DURING) ||
                    this.relativePosition(other).equals(RelativePosition.ENDED_BY) ||
                    this.relativePosition(other).equals(RelativePosition.ENDS) ||
                    this.relativePosition(other).equals(RelativePosition.EQUALS) ||
                    this.relativePosition(other).equals(RelativePosition.MEETS) ||
                    this.relativePosition(other).equals(RelativePosition.MET_BY) ||
                    this.relativePosition(other).equals(RelativePosition.OVERLAPPED_BY) ||
                    this.relativePosition(other).equals(RelativePosition.OVERLAPS)) {
                diff = 0L;
            }
        }
        return TemporalUtilities.durationFromMillis(Math.abs(diff));
    }
}
