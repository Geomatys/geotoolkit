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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.apache.sis.util.logging.Logging;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.temporal.Duration;
import org.opengis.temporal.Instant;
import org.opengis.temporal.OrdinalReferenceSystem;
import org.opengis.temporal.Period;
import org.opengis.temporal.RelativePosition;
import org.opengis.temporal.Separation;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 * An abstract class with two subclasses for representing
 * a temporal instant and a temporal period.
 * 
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
@XmlAccessorType(XmlAccessType.NONE)
//@XmlSeeAlso({DefaultInstant.class, DefaultPeriod.class})
 public abstract class DefaultTemporalGeometricPrimitive extends DefaultTemporalPrimitive implements TemporalGeometricPrimitive, Separation {

    private static final Logger LOGGER = Logging.getLogger(DefaultTemporalGeometricPrimitive.class);

    /**
     * 
     * @param properties
     * @throws IllegalArgumentException 
     */
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
     *
     * @see #castOrCopy(TemporalGeometricPrimitive)
     */
    protected DefaultTemporalGeometricPrimitive(final TemporalGeometricPrimitive object) {
        super(object);
//        if (object != null) {
//            this.referenceEvent = object.getReferenceEvent();
//            ArgumentChecks.ensureNonNull("referenceEvent", referenceEvent);
//            this.referenceTime  = object.getReferenceTime();
//            this.utcReference   = object.getUTCReference();
//            if (object instanceof DefaultPeriod) {
//                this.dateBasis = ((DefaultClock) object).getDateBasis();
//            }            
//        }
    }

//    /**
//     * Returns a Geotk implementation with the values of the given arbitrary implementation.
//     * This method performs the first applicable action in the following choices:
//     *
//     * <ul>
//     *   <li>If the given object is {@code null}, then this method returns {@code null}.</li>
//     *   <li>Otherwise if the given object is already an instance of
//     *       {@code DefaultTemporalGeometricPrimitive}, then it is returned unchanged.</li>
//     *   <li>Otherwise a new {@code DefaultTemporalGeometricPrimitive} instance is created using the
//     *       {@linkplain #DefaultTemporalGeometricPrimitive(TemporalGeometricPrimitive) copy constructor}
//     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
//     *       metadata contained in the given object are not recursively copied.</li>
//     * </ul>
//     *
//     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
//     * @return A Geotk implementation containing the values of the given object (may be the
//     *         given object itself), or {@code null} if the argument was null.
//     */
//    public static DefaultTemporalGeometricPrimitive castOrCopy(final TemporalGeometricPrimitive object) {
//        if (object == null || object instanceof DefaultTemporalGeometricPrimitive) {
//            return (DefaultTemporalGeometricPrimitive) object;
//        }
//        return new DefaultTemporalGeometricPrimitive(object);
//    }
    
    /**
     * Returns the distance from this TM_GeometricPrimitive to another TM_GeometricPrimitive, 
     * i.e. the absolute value of the difference between their temporal positions.
     * @param other
     * @return Duration between this geometry and the given one.
     */
    @Override
    public Duration distance(final TemporalGeometricPrimitive other) {
        Duration response = null;
        long diff = 0L;

//        if (this instanceof Instant && other instanceof Instant) {
//            if (((Instant) this).getPosition().anyOther() != null && ((Instant) other).getPosition().anyOther() != null) {
//                if (!((DefaultTemporalPosition) ((Instant) this).getPosition().anyOther()).getFrame().equals(((DefaultTemporalPosition) ((Instant) other).getPosition().anyOther()).getFrame())) {
//                    try {
//                        throw new Exception("the TM_TemporalPositions are not both associated with the same TM_ReferenceSystem !");
//                    } catch (Exception ex) {
//                        LOGGER.log(Level.WARNING, null, ex);
//                    }
//                }
//            } else if (((Instant) this).getPosition().anyOther() != null) {
//                if (((Instant) this).getPosition().anyOther().getIndeterminatePosition() != null ||
//                        ((DefaultTemporalPosition) ((Instant) this).getPosition().anyOther()).getFrame() instanceof OrdinalReferenceSystem) {
//                    try {
//                        throw new Exception("either of the two TM_TemporalPositions is indeterminate or is associated with a TM_OrdinalReferenceSystem !");
//                    } catch (Exception ex) {
//                        LOGGER.log(Level.WARNING, null, ex);
//                    }
//                }
//            } else if (((Instant) other).getPosition().anyOther() != null) {
//                if (((Instant) other).getPosition().anyOther().getIndeterminatePosition() != null ||
//                        ((DefaultTemporalPosition) ((Instant) other).getPosition().anyOther()).getFrame() instanceof OrdinalReferenceSystem) {
//                    try {
//                        throw new Exception("either of the two TM_TemporalPositions is indeterminate or is associated with a TM_OrdinalReferenceSystem !");
//                    } catch (Exception ex) {
//                        LOGGER.log(Level.WARNING, null, ex);
//                    }
//                }
//            }
//        }
        
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

        response = new DefaultPeriodDuration(Math.abs(diff));
        return response;
    }

    /**
     * Returns the length of this TM_GeometricPrimitive
     * @return the length of this TM_GeometricPrimitive
     */
    @Override
    public Duration length() {
        Duration response = null;
        long diff = 0L;
        if (this instanceof Instant) {
            response = new DefaultPeriodDuration(Math.abs(diff));
            return response;
        } else {
            if (this instanceof Period) {
                if (((Period) this).getBeginning() != null &&
                        ((Period) this).getEnding() != null) {
                    response = ((DefaultInstant) ((Period) this).getBeginning()).distance(((DefaultInstant) ((Period) this).getEnding()));
                    return response;
                }
            }
            return null;
        }
    }
}
