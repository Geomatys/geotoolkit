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
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.RelativePosition;
import org.opengis.temporal.TemporalOrder;
import org.opengis.temporal.TemporalPrimitive;

/**
 * An abstract class that represents a non-decomposed element of geometry or topology of time.
 * 
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 */
public abstract class DefaultTemporalPrimitive extends AbstractIdentifiedObject implements TemporalPrimitive, TemporalOrder {

    /**
     * 
     * @param properties
     * @throws IllegalArgumentException 
     */
    public DefaultTemporalPrimitive(Map<String, ?> properties) throws IllegalArgumentException {
        super(properties);
    }
    
    protected DefaultTemporalPrimitive() {
        super(org.apache.sis.internal.referencing.NilReferencingObject.INSTANCE);
    }
    
    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Instant to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(TemporalGeometricPrimitive)
     */
    protected DefaultTemporalPrimitive(final TemporalPrimitive object) {
        super(object);
    }

    /**
     * Returns a value for relative position which are provided by the enumerated data type TM_RelativePosition 
     * and are based on the 13 temporal relationships identified by Allen (1983).
     * @param other TemporalPrimitive
     */
    @Override
    public RelativePosition relativePosition(final TemporalPrimitive other) {
        if (this instanceof Instant && other instanceof Instant) {
            Instant timeobject = (Instant) this;
            Instant instantOther = (Instant) other;

            if (timeobject.getDate() == null || instantOther.getDate() == null) {
                return null;
            } else if (timeobject.getDate().before(instantOther.getDate())) {
                return RelativePosition.BEFORE;
            } else {
                return (timeobject.getDate().compareTo(instantOther.getDate()) == 0) ? RelativePosition.EQUALS : RelativePosition.AFTER;
            }

        } else {
            if (this instanceof Period && other instanceof Instant) {
                Period timeobject = (Period) this;
                Instant instantarg = (Instant) other;

                if (timeobject.getEnding().getDate().before(instantarg.getDate())) {
                    return RelativePosition.BEFORE;
                } else if (timeobject.getEnding().getDate().compareTo(instantarg.getDate()) == 0) {
                    return RelativePosition.ENDED_BY;
                } else if (timeobject.getBeginning().getDate().before(instantarg.getDate()) &&
                    timeobject.getEnding().getDate().after(instantarg.getDate())) {
                    return RelativePosition.CONTAINS;
                } else {
                     return (timeobject.getBeginning().getDate().compareTo(instantarg.getDate()) == 0) ? RelativePosition.BEGUN_BY : RelativePosition.AFTER;
                }
                
            } else {
                if (this instanceof Instant && other instanceof Period) {
                    Instant timeobject = (Instant) this;
                    Period instantarg = (Period) other;

                    if (instantarg.getEnding().getDate().before(timeobject.getDate())) {
                        return RelativePosition.AFTER;
                    } else if (instantarg.getEnding().getDate().compareTo(timeobject.getDate()) == 0) {
                        return RelativePosition.ENDS;
                    } else if (instantarg.getBeginning().getDate().before(timeobject.getDate()) &&
                            instantarg.getEnding().getDate().after(timeobject.getDate())) {
                        return RelativePosition.DURING;
                    } else {
                        return (instantarg.getBeginning().getDate().compareTo(timeobject.getDate()) == 0) ? RelativePosition.BEGINS : RelativePosition.BEFORE;
                    }


                } else {
                    if (this instanceof Period && other instanceof Period) {
                        Period timeobject = (Period) this;
                        Period instantarg = (Period) other;

                        if (timeobject.getEnding().getDate().before(instantarg.getBeginning().getDate())) {
                            return RelativePosition.BEFORE;
                        } else if (timeobject.getEnding().getDate().compareTo(instantarg.getBeginning().getDate()) == 0) {
                            return RelativePosition.MEETS;
                        } else if (timeobject.getBeginning().getDate().before(instantarg.getBeginning().getDate()) &&
                                timeobject.getEnding().getDate().after(instantarg.getBeginning().getDate()) &&
                                timeobject.getEnding().getDate().before(instantarg.getEnding().getDate())) {
                            return RelativePosition.OVERLAPS;
                        } else if (timeobject.getBeginning().getDate().compareTo(instantarg.getBeginning().getDate()) == 0 &&
                                timeobject.getEnding().getDate().before(instantarg.getEnding().getDate())) {
                            return RelativePosition.BEGINS;
                        } else if (timeobject.getBeginning().getDate().compareTo(instantarg.getBeginning().getDate()) == 0 &&
                                timeobject.getEnding().getDate().after(instantarg.getEnding().getDate())) {
                            return RelativePosition.BEGUN_BY;
                        } else if (timeobject.getBeginning().getDate().after(instantarg.getBeginning().getDate()) &&
                                timeobject.getEnding().getDate().before(instantarg.getEnding().getDate())) {
                            return RelativePosition.DURING;
                        } else if (timeobject.getBeginning().getDate().before(instantarg.getBeginning().getDate()) &&
                                timeobject.getEnding().getDate().after(instantarg.getEnding().getDate())) {
                            return RelativePosition.CONTAINS;
                        } else if (timeobject.getBeginning().getDate().compareTo(instantarg.getBeginning().getDate()) == 0 &&
                                timeobject.getEnding().getDate().compareTo(instantarg.getEnding().getDate()) == 0) {
                            return RelativePosition.EQUALS;
                        } else if (timeobject.getBeginning().getDate().after(instantarg.getBeginning().getDate()) &&
                                timeobject.getBeginning().getDate().before(instantarg.getEnding().getDate()) &&
                                timeobject.getEnding().getDate().after(instantarg.getEnding().getDate())) {
                            return RelativePosition.OVERLAPPED_BY;
                        } else if (timeobject.getBeginning().getDate().after(instantarg.getBeginning().getDate()) &&
                                timeobject.getEnding().getDate().compareTo(instantarg.getEnding().getDate()) == 0) {
                            return RelativePosition.ENDS;
                        } else if (timeobject.getBeginning().getDate().before(instantarg.getBeginning().getDate()) &&
                                timeobject.getEnding().getDate().compareTo(instantarg.getEnding().getDate()) == 0) {
                            return RelativePosition.ENDED_BY;
                        } else {
                            return (timeobject.getBeginning().getDate().compareTo(instantarg.getEnding().getDate()) == 0) ? RelativePosition.MET_BY : RelativePosition.AFTER;
                        }
                    } else {
                        return null;
                    }
                }
            }
        }
    }
}
