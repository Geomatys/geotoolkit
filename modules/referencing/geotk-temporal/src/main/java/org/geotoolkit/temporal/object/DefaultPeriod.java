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
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ComparisonMode;
import org.opengis.temporal.Duration;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.RelativePosition;

/**
 * A one-dimensional geometric primitive that represent extent in time.
 * 
 * @author Mehdi Sidhoum (Geomatys)
 * @author Remi Marechal (Geomatys)
 * @module pending
 * @version 4.0
 * @since   4.0
 */
@XmlType(name = "TimePeriod_Type", propOrder = {
    "beginning",
    "ending",
    "duration"
})
@XmlRootElement(name = "TimePeriod")
public class DefaultPeriod extends DefaultTemporalGeometricPrimitive implements Period {

    /**
     * This is the TM_Instant at which this Period starts.
     */
    private Instant begining;
    
    /**
     * This is the TM_Instant at which this Period ends.
     */
    private Instant ending;

    /**
     * Empty constructor only use for XML binding.
     */
    private DefaultPeriod() {
    }
    
    /**
     * Creates a default {@link Period} implementation from the given properties and {@link Instant}.
     * The properties given in argument follow the same rules than for the
     * {@linkplain DefaultTemporalGeometricPrimitive#DefaultTemporalGeometricPrimitive(java.util.Map) )  super-class constructor}.
     * 
     * <table class="referencingTemporal">
     *   <caption>Recognized properties (non exhaustive list)</caption>
     *   <tr>
     *     <th>Property name</th>
     *     <th>Value type</th>
     *     <th>Returned by</th>
     *   </tr>
     *   <tr>
     *     <th colspan="3" class="hsep">Defined in parent class (reminder)</th>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#NAME_KEY}</td>
     *     <td>{@link Identifier} or {@link String}</td>
     *     <td>{@link #getName()}</td>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#IDENTIFIERS_KEY}</td>
     *     <td>{@link Identifier} (optionally as array)</td>
     *     <td>{@link #getIdentifiers()}</td>
     *   </tr>
     * </table>
     *
     * @param properties The properties to be given to this object.
     * @param begining begin instant of the period.
     * @param ending end instant of the period. 
     * @throws IllegalArgumentException 
     */
    public DefaultPeriod(final Map<String , ?> properties, final Instant begining, final Instant ending) {
        super(properties);
        ArgumentChecks.ensureNonNull("begining", begining);
        ArgumentChecks.ensureNonNull("ending", ending);
	//-- begining must be before or equals ending
        if (begining != null && 
                (RelativePosition.BEFORE.equals(begining.relativePosition(ending)) ||
                RelativePosition.EQUALS.equals(begining.relativePosition(ending)))) {
             this.begining = begining;
             this.ending = ending;
         }
     }
    
    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Instant to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(Period)
     */
    private DefaultPeriod(final Period object) {
        super(object);
        if (object != null) {
            begining = object.getBeginning();
            ending = object.getEnding();
            
            if (object instanceof DefaultPeriod) {
                //--- voir pour get duration
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
     *       {@code DefaultPeriod}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultPeriod} instance is created using the
     *       {@linkplain #DefaultPeriod(Period) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultPeriod castOrCopy(final Period object) {
        if (object == null || object instanceof DefaultPeriod) {
            return (DefaultPeriod) object;
        }
        return new DefaultPeriod(object);
    }

    /**
     * Returns {@link Period} to the {@link Instant} at which it starts.
     * 
     * @return {@link Period} to the {@link Instant} at which it starts.
     */
    @Override
    @XmlElement(name = "begin", required = true)
    public Instant getBeginning() {
        return begining;
    }
    
    /**
     * Set {@link Period} to the {@link Instant} at which it starts.
     * 
     * @param begining start {@link Instant} of the {@link Period}.
     */
    public void setBegining(final Instant begining) {
        this.begining = begining;
    }

    /**
     * Returns {@link Period} to the {@link Instant} at which it ends.
     * 
     * @return {@link Period} to the {@link Instant} at which it ends.
     */
    @Override
    @XmlElement(name = "end", required = true)
    public Instant getEnding() {
        return ending;
    }
    
    /**
     * Duration only use for XML binding.
     * 
     * @return {@link String} which represent duration for XML binding format.
     */
    @XmlElement(name = "duration")
    private String getDuration() {
        Duration dur = super.length();
        if (dur != null && (dur instanceof DefaultDuration)) {
            final DefaultPeriodDuration defPerDur = (dur instanceof DefaultPeriodDuration) ? (DefaultPeriodDuration) dur : new DefaultPeriodDuration(((DefaultDuration)dur).getTimeInMillis());
            return defPerDur.toString();
        }
        return null;
    }
    
    /**
     * Set {@link Period} to the {@link Instant} at which it ends.
     * 
     * @param begining start {@link Instant} of the {@link Period}.
     */
    public void setEnding(final Instant ending) {
        this.ending = ending;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, ComparisonMode comp) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultPeriod) {
            final DefaultPeriod that = (DefaultPeriod) object;

            return Objects.equals(this.begining, that.begining) &&
                    Objects.equals(this.ending, that.ending);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected long computeHashCode() {
        int hash = 5;
        hash = 37 * hash + (this.begining != null ? this.begining.hashCode() : 0);
        hash = 37 * hash + (this.ending != null ? this.ending.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Period:").append('\n');
        if (begining != null) {
            s.append("begin:").append(begining).append('\n');
        }
        if (ending != null) {
            s.append("end:").append(ending).append('\n');
        }

        return s.toString();
    }
}
