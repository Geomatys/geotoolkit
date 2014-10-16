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

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ComparisonMode;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.Position;
import org.opengis.temporal.TemporalReferenceSystem;

/**
 * A zero-dimensional geometric primitive that represents position in time, equivalent to a point
 * in space.
 * 
 * @author Mehdi Sidhoum (Geomatys)
 * @author Remi Marechal (Geomatys)
 * @module pending
 */
@XmlType(name = "TimeInstant_Type", propOrder = {
    "position"
})
@XmlRootElement(name = "TimeInstant")
public class DefaultInstant extends DefaultTemporalGeometricPrimitive implements Instant {

    /**
     * This is the Collection of temporal {@link org.opengis.temporal.Period}s,
     * for which this Instant is the beginning. The collection may be empty.
     */
    private Collection<Period> begunBy;
    
    /**
     * This is the Collection of temporal {@link org.opengis.temporal.Period}s,
     * for which this Instant is the end. The collection may be empty.
     */
    private Collection<Period> endBy;
    
    /**
     * This is the position of this {@linkplain Instant TM_Instant}, 
     * it shall be associated with a single {@link TemporalReferenceSystem}.
     */
    private Position position;

    /**
     * Empty constructor only use for XML binding.
     */
    public DefaultInstant() {
    }

    /**
     * Creates a default {@link Instant} implementation from the given properties and position.
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
     *     <td>{@link ReferenceIdentifier} or {@link String}</td>
     *     <td>{@link #getName()}</td>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#IDENTIFIERS_KEY}</td>
     *     <td>{@link ReferenceIdentifier} (optionally as array)</td>
     *     <td>{@link #getIdentifiers()}</td>
     *   </tr>
     * </table>
     *
     * @param properties The properties to be given to this object.
     * @param position the position of this {@linkplain Instant TM_Instant}, it shall be associated with a single {@link TemporalReferenceSystem}.
     * @throws IllegalArgumentException 
     */
    public DefaultInstant(Map<String, ?> properties, Position position) {
        this(properties, position, null, null);
    }

    /**
     * Creates a default {@link Instant} implementation from the given properties, position, begin and end.
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
     *     <td>{@link ReferenceIdentifier} or {@link String}</td>
     *     <td>{@link #getName()}</td>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#IDENTIFIERS_KEY}</td>
     *     <td>{@link ReferenceIdentifier} (optionally as array)</td>
     *     <td>{@link #getIdentifiers()}</td>
     *   </tr>
     * </table>
     *
     * @param properties The properties to be given to this object.
     * @param position the position of this {@linkplain Instant TM_Instant}, it shall be associated with a single {@link TemporalReferenceSystem}.
     * @param begunBy Collection of temporal {@link org.opengis.temporal.Period}s, for which this Instant is the beginning. The collection may be empty.
     * @param endBy Collection of temporal {@link org.opengis.temporal.Period}s, for which this Instant is the end. The collection may be empty.
     * @throws IllegalArgumentException 
     */
    public DefaultInstant(Map<String, ?> properties,  Position position, Collection<Period> begunBy, Collection<Period> endBy) throws IllegalArgumentException {
        super(properties);
        ArgumentChecks.ensureNonNull("position", position);
        this.position = position;
        this.begunBy  = begunBy;
        this.endBy    = endBy;
    }
    
    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Instant to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(Instant)
     */
    private DefaultInstant(final Instant object) {
        if (object != null) {
            this.position = object.getPosition();
        }
    }

    /**
     * Returns a Geotk implementation with the values of the given arbitrary implementation.
     * This method performs the first applicable action in the following choices:
     *
     * <ul>
     *   <li>If the given object is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given object is already an instance of
     *       {@code DefaultInstant}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultInstant} instance is created using the
     *       {@linkplain #DefaultInstant(Instant) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultInstant castOrCopy(final Instant object) {
        if (object == null || object instanceof DefaultInstant) {
            return (DefaultInstant) object;
        }
        return new DefaultInstant(object);
    }

    /**
     * Get the position of this instant.
     */
    @Override
    @XmlElement(name = "timePosition", required = true)
    public Position getPosition() {
        return position;
    }
    
    /**
     * Returns the Collection of temporal {@link org.opengis.temporal.Period}s,
     * for which this Instant is the beginning.
     * 
     * @return the Collection of temporal {@link org.opengis.temporal.Period}s,
     * for which this Instant is the beginning. The collection may be empty.
     * @see org.opengis.temporal.Period#getBeginning()
     */
    @Override
    public Collection<Period> getBegunBy() {
        return begunBy;
    }
    
    /**
     * Returns the collection of temporal {@link org.opengis.temporal.Period}s,
     * for which this Instant is the end.
     * 
     * @return the Collection of temporal {@link org.opengis.temporal.Period}s,
     * for which this Instant is the end. The collection may be empty.
     * @see org.opengis.temporal.Period#getEnding()
     */
    @Override
    public Collection<Period> getEndedBy() {
        return endBy;
    }

    /**
     * Set a new position of this {@linkplain Instant TM_Instant}, 
     * it shall be associated with a single {@link TemporalReferenceSystem}.
     * 
     * @param position The new position of this {@linkplain Instant TM_Instant}.
     */
    public void setPosition(final Position position) {
        this.position = position;
    }

    /**
     * Set a new collection of temporal {@link org.opengis.temporal.Period}s,
     * for which this Instant is the beginning.
     * 
     * @param begunBy The new collection of temporal {@link org.opengis.temporal.Period}s,
     * for which this Instant is the beginning.
     */
    public void setBegunBy(final Collection<Period> begunBy) {
        this.begunBy = begunBy;
    }

    /**
     * Set a new collection of temporal {@link org.opengis.temporal.Period}s,
     * for which this Instant is the end.
     * 
     * @param endBy The new collection of temporal {@link org.opengis.temporal.Period}s,
     * for which this Instant is the end.
     */
    public void setEndBy(final Collection<Period> endBy) {
        this.endBy = endBy;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(Object object, ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultInstant) {
            final DefaultInstant that = (DefaultInstant) object;

            return Objects.equals(this.position, that.position) &&
                    Objects.equals(this.begunBy, that.begunBy) &&
                    Objects.equals(this.endBy, that.endBy);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected long computeHashCode() {
        int hash = 5;
        hash = 37 * hash + (this.position != null ? this.position.hashCode() : 0);
        hash = 37 * hash + (this.begunBy != null ? this.begunBy.hashCode() : 0);
        hash = 37 * hash + (this.endBy != null ? this.endBy.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n').append("Instant:").append('\n');
        if (position != null) {
            s.append("position:").append(position).append('\n');
        }
        if (begunBy != null) {
            s.append("begunBy:").append(begunBy).append('\n');
        }
        if (endBy != null) {
            s.append("endBy:").append(endBy).append('\n');
        }
        return s.toString();
    }
}
