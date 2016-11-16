/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2014, Geomatys
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
package org.geotoolkit.temporal.reference;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ComparisonMode;
import org.opengis.temporal.Calendar;
import org.opengis.temporal.Clock;
import org.opengis.temporal.ClockTime;
import org.opengis.util.InternationalString;
import org.opengis.metadata.extent.Extent;
import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.temporal.CalendarEra;

/**
 * A clock provides a basis for defining temporal position within a day. 
 * A clock shall be used with a calendar in order to provide a complete description 
 * of a temporal position within a specific day. 
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 * 
 * @version 4.0
 * @since   4.0
 */
@XmlType(name = "TimeClock_Type", propOrder = {
    "referenceEvent",
    "referenceTim",
    "UTCReferenc",
    "dateBasis"
})
@XmlRootElement(name = "TimeClock")
public class DefaultClock extends DefaultTemporalReferenceSystem implements Clock {

    /**
     * Provide the name or description of an event, such as solar noon or sunrise.
     */
    private InternationalString referenceEvent;
    
    /**
     * Provides the time of day associated with the reference event expressed as a time of day  in the given clock.
     * <blockquote><font size="-1">The reference time is usually the origin of the clock scale.</font></blockquote>
     */
    private ClockTime referenceTime;
    
    /**
     * This is the 24-hour local or UTC time that corresponds to the reference time.
     */
    private ClockTime utcReference;
    
    /**
     * Collection of {@link Calendar} that use this {@link CalendarEra} as a reference for dating.
     */
    private Collection<Calendar> dateBasis;
    
    /**
     * Create a new {@link Clock} implementation initialize with the given parameters.<br/>
     * The properties given in argument follow the same rules than for the
     * {@linkplain DefaultTemporalCRS#DefaultTemporalCRS(java.util.Map, org.opengis.referencing.datum.TemporalDatum, org.opengis.referencing.cs.TimeCS)  super-class constructor}.
     * The following table is a reminder of current main (not all) properties:
     *
     * <table class="ISO 19108">
     *   <caption>Recognized properties (non exhaustive list)</caption>
     *   <tr>
     *     <th>Property name</th>
     *     <th>Value type</th>
     *     <th>Returned by</th>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#NAME_KEY}</td>
     *     <td>{@link org.opengis.referencing.ReferenceIdentifier} or {@link String}</td>
     *     <td>{@link #getName()}</td>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.datum.Datum#DOMAIN_OF_VALIDITY_KEY}</td>
     *     <td>{@link org.opengis.metadata.extent.Extent}</td>
     *     <td>{@link #getDomainOfValidity()}</td>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.temporal.Calendar#REFERENCE_EVENT_KEY}</td>
     *     <td>{@link org.opengis.util.InternationalString}</td>
     *     <td>{@link #getReferenceEvent()}</td>
     *   </tr>
     * </table>
     *
     * @param properties The properties to be given to the coordinate reference system.
     * @param referenceTime The time of day associated with the reference event expressed as a time of day  in the given clock.
     * @param utcReference The 24-hour local or UTC time that corresponds to the reference time.
     */
    public DefaultClock(Map<String, ?> properties, 
            ClockTime referenceTime, ClockTime utcReference, Collection<Calendar> dateBasis) {
        super(properties);
        final Object ref = properties.get(Calendar.REFERENCE_EVENT_KEY);
        ArgumentChecks.ensureNonNull("referenceEvent", ref);
        if (!(ref instanceof InternationalString))
            throw new IllegalArgumentException("reference Event must be instance of referenceEvent");
        this.referenceEvent = (InternationalString) ref;
        this.referenceTime  = referenceTime;
        this.utcReference   = utcReference;
        this.dateBasis      = dateBasis;
    }

    /**
     * Empty constructor only use for XML binding.
     */
    private DefaultClock() {
        super();
    }
    
    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Instant to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(Clock)
     */
    private DefaultClock(final Clock object) {
        super(object);
        if (object != null) {
            this.referenceEvent = object.getReferenceEvent();
            ArgumentChecks.ensureNonNull("referenceEvent", referenceEvent);
            this.referenceTime  = object.getReferenceTime();
            this.utcReference   = object.getUTCReference();
            if (object instanceof DefaultClock) {
                this.dateBasis = ((DefaultClock) object).getDateBasis();
            }            
        }
    }

    /**
     * Returns a Geotk implementation with the values of the given arbitrary implementation.
     * This method performs the first applicable action in the following choices:
     *
     * <ul>
     *   <li>If the given object is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given object is already an instance of
     *       {@code DefaultClock}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultClock} instance is created using the
     *       {@linkplain #DefaultClock(Clock) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultClock castOrCopy(final Clock object) {
        if (object == null || object instanceof DefaultClock) {
            return (DefaultClock) object;
        }
        return new DefaultClock(object);
    }
    
    /**
     * Returns name or description of an event, such as solar noon or sunrise,
     * which fixes the position of the base scale of the clock.
     * 
     * @return Event used as the datum for this clock.
     */
    @Override
    @XmlElement(name = "referenceEvent", required = true)
    public InternationalString getReferenceEvent() {
        return referenceEvent;
    }

    /**
     * Returns time of day associated with the reference event expressed as a time of day in the given clock.
     * <blockquote><font size="-1">The reference time is usually the origin of the clock scale.</font></blockquote>
     * 
     * @return Time of the reference event for this clock.
     */
    @Override
    public ClockTime getReferenceTime() {
        return referenceTime;
    }
    
    /**
     * Returns String which represent {@link #referenceTime} object adapted for JAXB marshalling.
     * 
     * @return String which represent {@link #referenceTime} object adapted for JAXB marshalling.
     */
    @XmlElement(name = "referenceTime", required = true)
    private String getReferenceTim() {
        final Number[] ref = referenceTime.getClockTime();
        String refs = ((ref[0].intValue() < 10) ? "0" : "")+ref[0];
        for (int r = 1; r < ref.length; r++) {
            final String str = ((ref[r].intValue() < 10) ? "0" : "") + ref[r];
            refs = refs+":"+(str);
        }
        return refs;
    }

    /**
     * Returns 24-hour local or UTC time that corresponds to the reference time.
     * 
     * @return UTC time of the reference event.
     */
    @Override
    public ClockTime getUTCReference() {
        return utcReference;
    }
    
    /**
     * Returns String which represent {@link #utcReference} object adapted for JAXB marshalling.
     * 
     * @return String which represent {@link #utcReference} object adapted for JAXB marshalling.
     */
    @XmlElement(name = "utcReference", required = true)
    private String getUTCReferenc() {
        final Number[] ref = utcReference.getClockTime();
        String refs = ((ref[0].intValue() < 10) ? "0" : "") + ref[0];
        for (int r = 1; r < ref.length; r++) {
            final String str = ((ref[r].intValue() < 10) ? "0" : "") + ref[r];
            refs = refs+":"+(str);
        }
        return refs;
    }
    
    /**
     * Returns Collection of {@link Calendar} that use this {@link CalendarEra} as a reference for dating.
     * 
     * @return Collection of {@link Calendar} that use this {@link CalendarEra} as a reference for dating.
     */
    @XmlElement(name = "dateBasis")
    public Collection<Calendar> getDateBasis() {
        return dateBasis;
    }

    /**
     * Returns convertion from a 24 hours local or UTC time and the equivalent time of day
     * expressed in terms of specified clock.
     * 
     * @param uTime The 24 hours local or UTC time.
     * @return Convertion of UTC time to a time of this clock.
     */
    @Override
    public ClockTime clkTrans(final ClockTime uTime) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns a time of day expressed in terms from the specified clock and 
     * the equivalent time of day in 24 hour or UTC time. 
     * 
     * @param clkTime The time of day expressed in terms of the specified clock which will be converted.
     * @return UTC time from time on this clock to UTC time convertion.
     */
    @Override
    public ClockTime utcTrans(final ClockTime clkTime) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object object, ComparisonMode mode) {
        boolean sup = super.equals(object, mode);
        if (!sup) return false;
        final DefaultClock that;
        if (object instanceof DefaultClock) {
            that = (DefaultClock) object;

            return Objects.equals(this.dateBasis, that.dateBasis) &&
                    Objects.equals(this.referenceEvent, that.referenceEvent) &&
                    Objects.equals(this.referenceTime, that.referenceTime) &&
                    Objects.equals(this.utcReference, that.utcReference);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected long computeHashCode() {
        int hash = 5;
        hash = 37 * hash + (this.dateBasis != null ? this.dateBasis.hashCode() : 0);
        hash = 37 * hash + (this.referenceEvent != null ? this.referenceEvent.hashCode() : 0);
        hash = 37 * hash + (this.referenceTime  != null ? this.referenceTime.hashCode()  : 0);
        hash = 37 * hash + (this.utcReference   != null ? this.utcReference.hashCode()   : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Clock:").append('\n');
        if (referenceEvent != null) {
            s.append("referenceEvent:").append(referenceEvent).append('\n');
        }
        if (referenceTime != null) {
            s.append("referenceTime:").append(referenceTime).append('\n');
        }
        if (utcReference != null) {
            s.append("utcReference:").append(utcReference).append('\n');
        }
        if (dateBasis != null) {
            s.append("dateBasis:").append(dateBasis).append('\n');
        }
        return s.toString();
    }
}
