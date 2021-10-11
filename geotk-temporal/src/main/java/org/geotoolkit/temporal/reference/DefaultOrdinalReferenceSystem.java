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
import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.temporal.OrdinalEra;
import org.opengis.temporal.OrdinalReferenceSystem;

/**
 * <p>An {@linkplain OrdinalReferenceSystem Ordinal Reference System} is based on an ordinal scale.
 * In its simpliest form, an ordinal temporal reference system is an ordered series of events.
 * Generally a specific series of events is associated with a single location.
 * Temporal relationships between different locations can be determinate only to the degree
 * that events at one location can be correlated with events at other locations on the basis
 * of non-temporal characteristics of the events. Such correlation can be used to develop a
 * more broadly based temporal reference system define in terms of periods within which similar
 * events have occured. The term {@linkplain OrdinalEra ordinal era} is use in this standard to
 * refer to such a period.</p>
 *
 * <blockquote><font size="-1">An {@linkplain OrdinalReferenceSystem Ordinal Temporal Reference System}
 * consists of a set of {@linkplain OrdinalEra ordinal era}. {@linkplain OrdinalReferenceSystem Ordinal Reference System}
 * are often hierarchically structured such that an {@linkplain OrdinalEra ordinal era} at a given
 *  level of hierarchy includes a sequence of coterminous shorter {@linkplain OrdinalEra ordinal era}</font></blockquote>
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @author Remi Marechal (Geomatys)
 * @module
 *
 * @version 4.0
 * @since   4.0
 */
@XmlType(name = "TimeOrdinalReferenceSystem_Type", propOrder = {
    "ordinalEraSequence"
})
@XmlRootElement(name = "TimeOrdinalReferenceSystem")
public class DefaultOrdinalReferenceSystem extends DefaultTemporalReferenceSystem implements OrdinalReferenceSystem {

    /**
     * An ordinal temporal reference system  consists of a set of ordinal eras,
     * that make up the highest level of hierarchy.
     */
    private final Collection<OrdinalEra> ordinalEraSequence;

    /**
     * Create a default {@link OrdinalReferenceSystem} implementation initialize with the given parameters.
     *
     * The properties given in argument follow the same rules than for the
     * {@linkplain DefaultTemporalReferenceSystem#DefaultTemporalReferenceSystem(java.util.Map, org.opengis.referencing.datum.TemporalDatum, org.opengis.referencing.cs.TimeCS)   super-class constructor}.
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
     * @param datum The datum.
     * @param cs The coordinate system.
     * @param ordinalEraSequence A hierarchically-structured collection of {@linkplain OrdinalEra ordinal era}.
     */
    public DefaultOrdinalReferenceSystem(Map<String, ?> properties, Collection<OrdinalEra> ordinalEraSequence) {
        super(properties);
        ArgumentChecks.ensureNonNull("ordinalEraSequence", ordinalEraSequence);
        this.ordinalEraSequence = ordinalEraSequence;
    }

    /**
     * Empty constructor only use during XML binding.
     */
    private DefaultOrdinalReferenceSystem() {
        ordinalEraSequence = null;
    }

    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The OrdinalReferenceSystem to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(OrdinalReferenceSystem)
     */
    public DefaultOrdinalReferenceSystem(final OrdinalReferenceSystem object) {
        super(object);
        if (object != null) {
            Collection<OrdinalEra> ordinalEraSeq = (Collection<OrdinalEra>) object.getComponents();
            ArgumentChecks.ensureNonNull("ordinalEraSequence", ordinalEraSeq);
            this.ordinalEraSequence = ordinalEraSeq;
        } else {
            ordinalEraSequence = null; //-- maybe an exception
        }
    }

    /**
     * Returns a Geotk implementation with the values of the given arbitrary implementation.
     * This method performs the first applicable action in the following choices:
     *
     * <ul>
     *   <li>If the given object is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given object is already an instance of
     *       {@code DefaultOrdinalReferenceSystem}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultOrdinalReferenceSystem} instance is created using the
     *       {@linkplain #DefaultOrdinalReferenceSystem(OrdinalReferenceSystem) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultOrdinalReferenceSystem castOrCopy(final OrdinalReferenceSystem object) {
        if (object == null || object instanceof DefaultTemporalCoordinateSystem) {
            return (DefaultOrdinalReferenceSystem) object;
        }
        return new DefaultOrdinalReferenceSystem(object);
    }

    /**
     * Returns the set of ordinal eras of which this ordinal reference system consists of.
     * <blockquote><font size="-1">{@linkplain OrdinalEra ordinal era} that make up the
     * highest level of hierarchy.</font></blockquote>
     *
     * @return A hierarchically-structured collection of {@linkplain OrdinalEra ordinal era}.
     */
    @Override
    @XmlElement(name = "component", required = true)
    public Collection<OrdinalEra> getComponents() {
        return ordinalEraSequence;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object object, ComparisonMode mode) {
        if (object == this) return true;
        final boolean sup = super.equals(object, mode);
        if (!sup) return false;
        if (object instanceof DefaultOrdinalReferenceSystem && super.equals(object)) {
            final DefaultOrdinalReferenceSystem that = (DefaultOrdinalReferenceSystem) object;

            return Objects.equals(this.ordinalEraSequence, that.ordinalEraSequence);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected long computeHashCode() {
        return 37 * getName().hashCode() * (this.ordinalEraSequence != null ? this.ordinalEraSequence.hashCode() : 0);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n').append("OrdinalReferenceSystem:").append('\n');
        if (ordinalEraSequence != null) {
            s.append("ordinalEraSequence:").append(ordinalEraSequence).append('\n');
        }
        return super.toString().concat("\n").concat(s.toString());
    }
}
