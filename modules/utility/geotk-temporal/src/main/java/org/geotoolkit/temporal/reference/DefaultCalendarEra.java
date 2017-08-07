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
package org.geotoolkit.temporal.reference;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.internal.referencing.NilReferencingObject;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.NullArgumentException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.temporal.Calendar;
import org.opengis.temporal.CalendarDate;
import org.opengis.temporal.CalendarEra;
import org.opengis.temporal.Clock;
import org.opengis.temporal.JulianDate;
import org.opengis.temporal.Period;
import org.opengis.util.InternationalString;

/**
 * Characteristic of each calendar era.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 *
 * @version 4.0
 * @since   4.0
 */
@XmlType(name = "TimeCalendarEra_Type", propOrder = {
    "referenceEvent",
    "referenceDat",
    "julianRef",
    "epochOfUse"
})
@XmlRootElement(name = "TimeCalendarEra")
public class DefaultCalendarEra extends AbstractIdentifiedObject implements CalendarEra {

    private static NumberFormat NUMBER_FORMAT;

    static {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        NUMBER_FORMAT = new DecimalFormat("#", dfs);
        NUMBER_FORMAT.setMinimumFractionDigits(9);
    }

    /**
     * Provide the name or description of a mythical or historic event which fixes the position of the base scale of the calendar era.
     */
    private InternationalString referenceEvent;

    /**
     * provide the date of the reference referenceEvent expressed as a date in the given calendar.
     * In most calendars, this date is the origin (i.e the first day) of the scale, but this is not always true.
     */
    private CalendarDate referenceDate;

    /**
     * Provide the Julian date that corresponds to the reference date.
     */
    private JulianDate julianReference;

    /**
     * Identify the {@link Period} for which the calendar era was used as a basis for dating,
     * the datatype for {@link Period#getBeginning() } and {@link Period#getEnding() } shall be JulianDate.
     */
    private Period epochOfUse;

    /**
     * Collection of {@link Calendar} that use this {@link CalendarEra} as a reference for dating.
     */
    private Collection<Calendar> datingSystem;

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
    public DefaultCalendarEra(Map<String, ?> properties,
            final CalendarDate referenceDate, final JulianDate julianReference, final Period epochOfUse) {
        super(properties);
        final Object ref = properties.get(Calendar.REFERENCE_EVENT_KEY);
        ArgumentChecks.ensureNonNull("referenceEvent", ref);
        ArgumentChecks.ensureNonNull("referenceDate", referenceDate);
        ArgumentChecks.ensureNonNull("julianReference", julianReference);
        ArgumentChecks.ensureNonNull("epochOfUse", epochOfUse);
        if (!(ref instanceof InternationalString))
            throw new IllegalArgumentException("reference Event must be instance of refernceEvent");
        this.referenceDate   = referenceDate;
        this.referenceEvent  = (InternationalString) ref;
        this.julianReference = julianReference;
        this.epochOfUse      = epochOfUse;
    }

    /**
     * Empty constructor only use for XML binding.
     */
    private DefaultCalendarEra() {
        super(NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The CalendarEra to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(CalendarEra)
     * @throws NullArgumentException if referenceEvent, referenceDate, julianReference or epochOfUse is {@code null}.
     */
    private DefaultCalendarEra(final CalendarEra object) {
        super(object);
        if (object != null) {
            this.referenceEvent = object.getReferenceEvent();
            ArgumentChecks.ensureNonNull("referenceEvent", referenceEvent);
            this.referenceDate  = object.getReferenceDate();
            ArgumentChecks.ensureNonNull("referenceDate", referenceDate);
            this.julianReference = object.getJulianReference();
            ArgumentChecks.ensureNonNull("julianReference", julianReference);
            this.epochOfUse = object.getEpochOfUse();
            ArgumentChecks.ensureNonNull("julianReference", julianReference);
            if (object instanceof DefaultCalendarEra) {
                this.datingSystem = ((DefaultCalendarEra) object).getDatingSystem();
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
     *       {@code DefaultCalendarEra}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultCalendarEra} instance is created using the
     *       {@linkplain #DefaultCalendarEra(CalendarEra) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultCalendarEra castOrCopy(final CalendarEra object) {
        if (object == null || object instanceof DefaultCalendarEra) {
            return (DefaultCalendarEra) object;
        }
        return new DefaultCalendarEra(object);
    }

    /**
     * Returns the name or description of a mythical or historic event which fixes the position
     * of the base scale of the calendar era.
     *
     * @return Event used as the datum for this calendar era.
     */
    @Override
    @XmlElement(name = "referenceEvent", required = true)
    public InternationalString getReferenceEvent() {
        return referenceEvent;
    }

    /**
     * Returns the date of the reference event expressed as a date in the given calendar.
     * <blockquote><font size="-1">In most calendars, this date is the origin (i.e., the first day)
     * of the scale, but this is not always {@code true}.</font></blockquote>
     *
     * @return Date of the reference event in the calendar being described.
     */
    @Override
    public CalendarDate getReferenceDate() {
        return referenceDate;
    }

    /**
     * Returns {@link #referenceDate} adapted for JAXB Marshalling.
     *
     * @return {@link #referenceDate} adapted for JAXB Marshalling.
     */
    @XmlElement(name = "referenceDate", required = true)
    private String getReferenceDat() {
        final int[] dat = referenceDate.getCalendarDate();
        String str = ""+dat[0];
        for(int i = 1; i < dat.length; i++){
            str = str + "-";
            if (dat[i] < 10) str = str+"0";
            str = str+dat[i];
        }
        return str;
    }

    /**
     * Returns the {@linkplain JulianDate julian date} that corresponds to the reference date.
     *
     * @return {@linkplain JulianDate julian date} of the reference event.
     */
    @Override
    public JulianDate getJulianReference() {
        return julianReference;
    }

    /**
     * Returns {@link #julianReference} adapted for JAXB Marshalling.
     *
     * @return {@link #julianReference} adapted for JAXB Marshalling.
     */
    @XmlElement(name = "julianReference", required = true)
    private String getJulianRef() {
        return NUMBER_FORMAT.format(julianReference.getCoordinateValue().doubleValue());
    }

    /**
     * Returns the {@linkplain Period period} for which the calendar era
     * was used as a reference for dating.
     *
     * @return The period, where the data type for {@linkplain Period#getBegin begin}
     *         and {@link Period#getEnd end} is {@link JulianDate}.
     */
    @Override
    @XmlElement(name = "epochOfUse", required = true)
    public Period getEpochOfUse() {
        return epochOfUse;
    }

    /**
     * Returns a collection of {@link Calendar} that use this {@link CalendarEra} as a reference for dating.
     *
     * @return collection of {@link Calendar} that use this {@link CalendarEra} as a reference for dating.
     */
    public Collection<Calendar> getDatingSystem() {
        return datingSystem;
    }

    /**
     * Set a new name or description of a mythical or historic event which fixes the position
     * of the base scale of the calendar era.
     *
     * @param referenceEvent The new event used as the datum for this calendar era.
     */
    public void setReferenceEvent(final InternationalString referenceEvent) {
        ArgumentChecks.ensureNonNull("referenceEvent", referenceEvent);
        this.referenceEvent = referenceEvent;
    }

    /**
     * Set a new date of the reference event expressed as a date in the given calendar.
     *
     * @param referenceDate The new date of the reference event in the calendar being described.
     */
    public void setReferenceDate(final CalendarDate referenceDate) {
        ArgumentChecks.ensureNonNull("referenceDate", referenceDate);
        this.referenceDate = referenceDate;
    }

    /**
     * Set a new {@linkplain JulianDate julian date} that corresponds to the reference date.
     *
     * @param julianReference The new {@linkplain JulianDate julian date} of the reference event.
     */
    public void setJulianReference(final JulianDate julianReference) {
        ArgumentChecks.ensureNonNull("julianReference", julianReference);
        this.julianReference = julianReference;
    }

    /**
     * Set a new {@linkplain Period period} for which the calendar era
     * was used as a reference for dating.
     *
     * @param epochOfUse The new period, where the data type for {@linkplain Period#getBegin begin}
     *         and {@link Period#getEnd end} is {@link JulianDate}.
     */
    public void setEpochOfUse(final Period epochOfUse) {
        ArgumentChecks.ensureNonNull("epochOfUse", epochOfUse);
        this.epochOfUse = epochOfUse;
    }

    /**
     * Set a new collection of {@link Calendar} that use this {@link CalendarEra} as a reference for dating.
     *
     * @param newValues The new collection of {@link Calendar} that use this {@link CalendarEra} as a reference for dating.
     */
    public void setDatingSystem(final Collection<Calendar> newValues) {
        this.datingSystem = newValues;
    }

    @Override
    public boolean equals(Object object, ComparisonMode mode) {
        if (object instanceof CalendarEra) {
            final DefaultCalendarEra that = (DefaultCalendarEra) object;

            return Objects.equals(this.datingSystem, that.datingSystem) &&
                    Objects.equals(this.epochOfUse, that.epochOfUse) &&
                    Objects.equals(this.julianReference, that.julianReference) &&
                    Objects.equals(this.getName(), that.getName()) &&
                    Objects.equals(this.referenceDate, that.referenceDate) &&
                    Objects.equals(this.referenceEvent, that.referenceEvent);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("CalendarEra:").append('\n');
        if (getName() != null) {
            s.append("name:").append(getName().getCode()).append('\n');
        }
        if (epochOfUse != null) {
            s.append("epochOfUse:").append(epochOfUse).append('\n');
        }
        if (referenceEvent != null) {
            s.append("referenceEvent:").append(referenceEvent).append('\n');
        }
        if (referenceDate != null) {
            s.append("referenceDate:").append(referenceDate).append('\n');
        }
        if (julianReference != null) {
            s.append("julianReference:").append(julianReference).append('\n');
        }
        if (datingSystem != null) {
            s.append("datingSystem:").append(datingSystem).append('\n');
        }
        return s.toString();
    }
}
