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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.internal.referencing.NilReferencingObject;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.geotoolkit.temporal.object.DefaultTemporalNode;
import org.opengis.metadata.Identifier;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import org.opengis.temporal.OrdinalEra;
import org.opengis.temporal.OrdinalReferenceSystem;
import org.opengis.temporal.TemporalNode;
import org.opengis.util.InternationalString;

/**
 * Provides a reference to the ordinal era in which the instant occurs.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 * 
 * @version 4.0
 * @since   4.0
 */
@XmlType(name = "TimeOrdinalEra_Type", propOrder = {
    "start",
    "endd",
    "member", 
    "groupp"
})
@XmlRootElement(name = "TimeOrdinalEra")
public class DefaultOrdinalEra extends AbstractIdentifiedObject implements OrdinalEra {
    
    /**
     * This is the temporal position at which the ordinal era began, if it is known.
     */
    private Date begin;
    
    /**
     * This is the temporal position at which the ordinal era ended.
     */
    private Date end;
    
    /**
     * {@linkplain OrdinalEra ordinal eras} that subdivide this ordinal era.
     */
    private Collection<OrdinalEra> member;
    
    /**
     * Define an alone {@link OrdinalEra} implementation witch regroup other {@link OrdinalEra}. 
     */
    private DefaultOrdinalEra group;

    /**
     * Create a default implementation of {@link OrdinalEra} initialize by given parameters.
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
     *    <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#IDENTIFIERS_KEY}</td>
     *     <td>{@link org.opengis.referencing.ReferenceIdentifier} or {@link String}</td>
     *     <td>{@link #getIdentifiers() }</td>
     *   </tr>
     *    <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#REMARKS_KEY}</td>
     *     <td>{@link org.opengis.util.InternationalString}</td>
     *     <td>{@link #getRemarks()}</td>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.temporal.Calendar#REFERENCE_EVENT_KEY}</td>
     *     <td>{@link org.opengis.util.InternationalString}</td>
     *     <td>{@link #getReferenceEvent()}</td>
     *   </tr>
     * </table>
     * 
     * @param name The string that identifies the ordinal era within the {@linkplain OrdinalReferenceSystem TM_OrdinalReferenceSystem}.
     * @param beginning The temporal position at which the ordinal era began, if it is known.
     * @param end The temporal position at which the ordinal era ended.
     */
    public DefaultOrdinalEra(final Map<String, ?> properties, final Date beginning, final Date end) {
        super(properties);
        if (! beginning.before(end))
            throw new IllegalArgumentException("The beginning date of the OrdinalEra must be less than (i.e. earlier than) the end date of this OrdinalEra.");
        this.begin = beginning;
        this.end   = end;
    }

    /**
     * Create a default implementation of {@link OrdinalEra} initialize by given parameters.
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
     *    <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#IDENTIFIERS_KEY}</td>
     *     <td>{@link org.opengis.referencing.ReferenceIdentifier} or {@link String}</td>
     *     <td>{@link #getIdentifiers() }</td>
     *   </tr>
     *    <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#REMARKS_KEY}</td>
     *     <td>{@link org.opengis.util.InternationalString}</td>
     *     <td>{@link #getRemarks()}</td>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.temporal.Calendar#REFERENCE_EVENT_KEY}</td>
     *     <td>{@link org.opengis.util.InternationalString}</td>
     *     <td>{@link #getReferenceEvent()}</td>
     *   </tr>
     * </table>
     * 
     * @param name The string that identifies the ordinal era within the {@linkplain OrdinalReferenceSystem TM_OrdinalReferenceSystem}.
     * @param beginning The temporal position at which the ordinal era began, if it is known.
     * @param end The temporal position at which the ordinal era ended.
     * @param member The {@linkplain OrdinalEra ordinal eras} that subdivide this ordinal era.
     */
    public DefaultOrdinalEra(final Map<String, ?> properties, final Date beginning, 
            final Date end, final Collection<OrdinalEra> member) {
        super(properties);
        this.begin  = beginning;
        this.end    = end;
        this.member = member;
        if (member != null)
            for (OrdinalEra ordinalEra : this.member) {
                ((DefaultOrdinalEra) ordinalEra).setGroup(this);
            }
    }
    
    /**
     * Private constructor adapted for XML binding.
     */
    private DefaultOrdinalEra() {
        super(NilReferencingObject.INSTANCE);
    }
    
    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Calendar to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(Calendar)
     */
    private DefaultOrdinalEra (final OrdinalEra object) {
        super(object);
        if (object != null) {
            begin  = object.getBegin();
            end    = object.getEnd();
            member = object.getMember();
            if (member != null)
                for (OrdinalEra ordinalEra : member) {
                    ((DefaultOrdinalEra) ordinalEra).setGroup(this);
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
     *       {@code DefaultCalendar}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultOrdinalEra} instance is created using the
     *       {@linkplain #DefaultOrdinalEra(OrdinalEra) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultOrdinalEra  castOrCopy(final OrdinalEra object) {
        if (object == null || object instanceof DefaultOrdinalEra) {
            return (DefaultOrdinalEra) object;
        }
        return new DefaultOrdinalEra(object);
    }
    
    /**
     * Returns the temporal position at which the ordinal era began, if it is known.
     * 
     * @return the temporal position at which the ordinal era began, if it is known.
     */
    @Override
    public Date getBegin() {
        return begin;
    }

    /**
     * Returns the temporal position at which the ordinal era ended.
     * 
     * @return the temporal position at which the ordinal era ended.
     */
    @Override
//    @XmlElement(name = "end")
    public Date getEnd() {
        return end;
    }

    /**
     * Returns the {@linkplain OrdinalEra ordinal eras} that subdivide this ordinal era.
     * 
     * @return the {@linkplain OrdinalEra ordinal eras} that subdivide this ordinal era.
     */
    @Override
    @XmlElement(name = "member")
    public Collection<OrdinalEra> getMember() {
        return member;
    }
    
    /**
     * Returns an alone {@link OrdinalEra} implementation witch regroup other {@link OrdinalEra}. 
     * 
     * @return an alone {@link OrdinalEra} implementation witch regroup other {@link OrdinalEra}. 
     */
    public DefaultOrdinalEra getGroup() {
        return group;
    }
    
    /**
     * Returns a {@link TemporalNode} create from {@link #begin} object, use for XML binding.
     * 
     * @return a {@link TemporalNode} create from {@link #begin} object, use for XML binding.
     */
    @XmlElement(name = "start")
    private DefaultTemporalNode getStart() {
        final Identifier iden = getName();
        final Map<String, Object> instantProp = new HashMap<>();
        instantProp.put(NAME_KEY, new NamedIdentifier(Citations.CRS, iden.getCode()+"_begin instant"));
        final Map<String, Object> nodeProp = new HashMap<>();
        nodeProp.put(NAME_KEY, new NamedIdentifier(Citations.CRS, iden.getCode()+"_start node"));
        final DefaultTemporalNode start = new DefaultTemporalNode(nodeProp, new DefaultInstant(instantProp, begin), null, null);
        return start;
    }
    
    /**
     * Returns a {@link TemporalNode} create from {@link #end} object, use for XML binding.
     * 
     * @return a {@link TemporalNode} create from {@link #end} object, use for XML binding.
     */
    @XmlElement(name = "end")
    private DefaultTemporalNode getEndd() {
        final Identifier iden = getName();
        final Map<String, Object> instantProp = new HashMap<>();
        instantProp.put(NAME_KEY, new NamedIdentifier(Citations.CRS, iden.getCode()+"_end instant"));
        final Map<String, Object> nodeProp = new HashMap<>();
        nodeProp.put(NAME_KEY, new NamedIdentifier(Citations.CRS, iden.getCode()+"_end node"));
        final DefaultTemporalNode start = new DefaultTemporalNode(nodeProp, new DefaultInstant(instantProp, begin), null, null);
        return start;
    }
    
    /**
     * Private method adapted for XML binding.
     * Return {@code null} because GML specification do not specify any things about "group".
     * 
     * @return {@code null}.
     */
    @XmlElement(name = "group")
    private DefaultOrdinalEra getGroupp() {
        return null;
    }

    //    /**
    //     * Set a new temporal position at which the ordinal era began, if it is known.
    //     *
    //     * @param beginning The new temporal position at which the ordinal era began, if it is known.
    //     */
    //    public void setBeginning(final Date beginning) {
    //        this.beginning = beginning;
    //    }
    //
    //    /**
    //     * Set a new temporal position at which the ordinal era ended.
    //     *
    //     * @param end The new temporal position at which the ordinal era ended.
    //     */
    //    public void setEnd(final Date end) {
    //        this.end = end;
    //    }
    //
        /**
         * Set a new {@link OrdinalEra} implementation witch regroup other {@link OrdinalEra}.
         *
         * @param group The new {@link OrdinalEra} implementation witch regroup other {@link OrdinalEra}.
         */
        public void setGroup(final DefaultOrdinalEra group) {
            this.group = group;
        }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object object, ComparisonMode mode) {
        if (object instanceof DefaultOrdinalEra) {
            final DefaultOrdinalEra that = (DefaultOrdinalEra) object;

            return Objects.equals(this.begin, that.begin) &&
                    Objects.equals(this.end, that.end) &&
                    Objects.equals(this.member, that.member) &&
                    Objects.equals(this.group, that.group);
        }
        return false;
    }

//    /**
//     * {@inheritDoc }
//     */
//    @Override
//    public boolean equals(Object object, comp) {
//        if (object instanceof DefaultOrdinalEra) {
//            final DefaultOrdinalEra that = (DefaultOrdinalEra) object;
//
//            return Objects.equals(this.beginning, that.beginning) &&
//                    Objects.equals(this.end, that.end) &&
//                    Objects.equals(this.member, that.member) &&
//                    Objects.equals(this.group, that.group);
//        }
//        return false;
//    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected long computeHashCode() {
        int hash = 5;
        hash = 37 * hash + (this.begin != null ? this.begin.hashCode() : 0);
        hash = 37 * hash + (this.end != null ? this.end.hashCode() : 0);
        hash = 37 * hash + (this.member != null ? this.member.hashCode() : 0);
        hash = 37 * hash + (this.group != null ? this.group.hashCode() : 0);
        return hash;
    }

//    /**
//     * {@inheritDoc }
//     */
//    @Override
//    public int hashCode() {
//        int hash = 5;
//        hash = 37 * hash + (this.beginning != null ? this.beginning.hashCode() : 0);
//        hash = 37 * hash + (this.end != null ? this.end.hashCode() : 0);
//        hash = 37 * hash + (this.member != null ? this.member.hashCode() : 0);
//        hash = 37 * hash + (this.group != null ? this.group.hashCode() : 0);
//        return hash;
//    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n').append("OrdinalEra:").append('\n');
        if (begin != null) {
            s.append("beginning:").append(begin).append('\n');
        }
        if (end != null) {
            s.append("end:").append(end).append('\n');
        }
        if (member != null) {
            s.append("composition:").append(member).append('\n');
        }
        if (group != null) {
            s.append("group:").append(group.getName().toString() ).append('\n');
        }
        return s.toString();
    }
}
