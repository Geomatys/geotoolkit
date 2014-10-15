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

import java.util.Collection;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.temporal.Calendar;
import org.opengis.temporal.CalendarDate;
import org.opengis.temporal.CalendarEra;
import org.opengis.temporal.JulianDate;
import org.opengis.temporal.Period;
import org.opengis.util.InternationalString;

/**
 * Characteristic of each calendar era.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 * 
 * @version 4.0
 * @since   4.0
 */
@XmlType(name = "TimeCalendarEra_Type", propOrder = {
    "name",
    "referenceEvent",
    "referenceDate",
    "julianReference",
    "epochOfUse"
})
@XmlRootElement(name = "TimeCalendarEra")
public class DefaultCalendarEra implements CalendarEra {

    /**
     * Name by which this calendar is known.
     */
    private InternationalString name;
    
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
     * Create a default {@link CalendarEra} implementation initialize with the given parameters.
     * 
     * @param name Name by which this calendar is known.
     * @param referenceEvent Event used as the datum for this calendar era.
     * @param referenceDate Date of the reference event in the calendar being described.
     * @param julianReference {@linkplain JulianDate julian date} that corresponds to the reference date.
     * @param epochOfUse {@linkplain Period period} for which the calendar era was used as a reference for dating.
     */
    public DefaultCalendarEra(final InternationalString name, final InternationalString referenceEvent, 
            final CalendarDate referenceDate, final JulianDate julianReference, final Period epochOfUse) {
        ArgumentChecks.ensureNonNull("name", name);
        ArgumentChecks.ensureNonNull("referenceDate", referenceDate);
        ArgumentChecks.ensureNonNull("referenceEvent", referenceEvent);
        ArgumentChecks.ensureNonNull("julianReference", julianReference);
        ArgumentChecks.ensureNonNull("epochOfUse", epochOfUse);
        this.name            = name;
        this.referenceDate   = referenceDate;
        this.referenceEvent  = referenceEvent;
        this.julianReference = julianReference;
        this.epochOfUse      = epochOfUse;
    }

    /**
     * Returns an uniquely {@code CharacterString} that identify the calendar era within this calendar.
     * 
     * @return name by which this calendar is known.
     */
    @Override
    @XmlElement(name = "name", required = true)
    public InternationalString getName() {
        return name;
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
    @XmlElement(name = "referenceDate", required = true)
    public CalendarDate getReferenceDate() {
        return referenceDate;
    }

    /**
     * Returns the {@linkplain JulianDate julian date} that corresponds to the reference date.
     * 
     * @return {@linkplain JulianDate julian date} of the reference event.
     */
    @Override
    @XmlElement(name = "julianReference", required = true)
    public JulianDate getJulianReference() {
        return julianReference;
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
     * Set a new name by which this calendar is known.
     * 
     * @param name The new name by which this calendar will be known.
     */
    public void setName(final InternationalString name) {
        ArgumentChecks.ensureNonNull("name", name);
        this.name = name;
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

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof CalendarEra) {
            final DefaultCalendarEra that = (DefaultCalendarEra) object;

            return Objects.equals(this.datingSystem, that.datingSystem) &&
                    Objects.equals(this.epochOfUse, that.epochOfUse) &&
                    Objects.equals(this.julianReference, that.julianReference) &&
                    Objects.equals(this.name, that.name) &&
                    Objects.equals(this.referenceDate, that.referenceDate) &&
                    Objects.equals(this.referenceEvent, that.referenceEvent);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.datingSystem != null ? this.datingSystem.hashCode() : 0);
        hash = 37 * hash + (this.epochOfUse != null ? this.epochOfUse.hashCode() : 0);
        hash = 37 * hash + (this.julianReference != null ? this.julianReference.hashCode() : 0);
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.referenceDate != null ? this.referenceDate.hashCode() : 0);
        hash = 37 * hash + (this.referenceEvent != null ? this.referenceEvent.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("CalendarEra:").append('\n');
        if (name != null) {
            s.append("name:").append(name).append('\n');
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
