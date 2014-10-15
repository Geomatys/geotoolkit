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
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.temporal.OrdinalEra;
import org.opengis.temporal.OrdinalReferenceSystem;
import org.opengis.util.InternationalString;

/**
 * Provides a reference to the ordinal era in which the instant occurs.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 * 
 * @version 4.0
 * @since   4.0
 */
@XmlType(name = "TimeOrdinalEra_Type", propOrder = {
    "begin",
    "end",
    "members", 
    "group"
})
@XmlRootElement(name = "TimeOrdinalEra")
public class DefaultOrdinalEra implements OrdinalEra {

    /**
     * This is a string that identifies the ordinal era within the {@linkplain OrdinalReferenceSystem TM_OrdinalReferenceSystem}.
     */
    private InternationalString name;
    
    /**
     * This is the temporal position at which the ordinal era began, if it is known.
     */
    private Date beginning;
    
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
     * @param name The string that identifies the ordinal era within the {@linkplain OrdinalReferenceSystem TM_OrdinalReferenceSystem}.
     * @param beginning The temporal position at which the ordinal era began, if it is known.
     * @param end The temporal position at which the ordinal era ended.
     */
    public DefaultOrdinalEra(final InternationalString name, final Date beginning, final Date end) {
        
        if (! beginning.before(end))
            throw new IllegalArgumentException("The beginning date of the OrdinalEra must be less than (i.e. earlier than) the end date of this OrdinalEra.");
        this.name      = name;
        this.beginning = beginning;
        this.end       = end;
    }

    /**
     * Create a default implementation of {@link OrdinalEra} initialize by given parameters.
     * 
     * @param name The string that identifies the ordinal era within the {@linkplain OrdinalReferenceSystem TM_OrdinalReferenceSystem}.
     * @param beginning The temporal position at which the ordinal era began, if it is known.
     * @param end The temporal position at which the ordinal era ended.
     * @param member The {@linkplain OrdinalEra ordinal eras} that subdivide this ordinal era.
     */
    public DefaultOrdinalEra(final InternationalString name, final Date beginning, 
            final Date end, final Collection<OrdinalEra> member) {
        this.beginning = beginning;
        this.end       = end;

        for (OrdinalEra ordinalEra : member) {
            ((DefaultOrdinalEra) ordinalEra).setGroup(this);
        }
    }
    
//    /**
//     * Private constructor adapted for XML binding.
//     */
//    private DefaultOrdinalEra() {
//        super(null);
//    }
    
    /**
     * Returns name that identifies a specific ordinal era.
     * 
     * @return name that identifies a specific ordinal era. 
     */
    @Override
    @XmlElement(name = "name", required = true)
    public InternationalString getName() {
        return name;
    }

    /**
     * Returns the temporal position at which the ordinal era began, if it is known.
     * 
     * @return the temporal position at which the ordinal era began, if it is known.
     */
    @Override
    @XmlElement(name = "start")
    public Date getBeginning() {
        return beginning;
    }

    /**
     * Returns the temporal position at which the ordinal era ended.
     * 
     * @return the temporal position at which the ordinal era ended.
     */
    @Override
    @XmlElement(name = "end")
    public Date getEnd() {
        return end;
    }

    /**
     * Returns the {@linkplain OrdinalEra ordinal eras} that subdivide this ordinal era.
     * 
     * @return the {@linkplain OrdinalEra ordinal eras} that subdivide this ordinal era.
     */
    @Override
    @XmlElement(name = "member", required = true)
    public Collection<OrdinalEra> getComposition() {
        return member;
    }
    
    /**
     * Return an alone {@link OrdinalEra} implementation witch regroup other {@link OrdinalEra}. 
     * 
     * @return an alone {@link OrdinalEra} implementation witch regroup other {@link OrdinalEra}. 
     */
    @XmlElement(name = "group")
    public DefaultOrdinalEra getGroup() {
        return group;
    }

    /**
     * Set a new temporal position at which the ordinal era began, if it is known.
     * 
     * @param beginning The new temporal position at which the ordinal era began, if it is known.
     */
    public void setBeginning(final Date beginning) {
        this.beginning = beginning;
    }

    /**
     * Set a new temporal position at which the ordinal era ended.
     * 
     * @param end The new temporal position at which the ordinal era ended.
     */
    public void setEnd(final Date end) {
        this.end = end;
    }

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
    public boolean equals(Object object) {
        if (object instanceof DefaultOrdinalEra) {
            final DefaultOrdinalEra that = (DefaultOrdinalEra) object;

            return Objects.equals(this.beginning, that.beginning) &&
                    Objects.equals(this.end, that.end) &&
                    Objects.equals(this.member, that.member) &&
                    Objects.equals(this.group, that.group);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.beginning != null ? this.beginning.hashCode() : 0);
        hash = 37 * hash + (this.end != null ? this.end.hashCode() : 0);
        hash = 37 * hash + (this.member != null ? this.member.hashCode() : 0);
        hash = 37 * hash + (this.group != null ? this.group.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n').append("OrdinalEra:").append('\n');
        if (beginning != null) {
            s.append("beginning:").append(beginning).append('\n');
        }
        if (end != null) {
            s.append("end:").append(end).append('\n');
        }
        if (member != null) {
            s.append("composition:").append(member).append('\n');
        }
        if (group != null) {
            s.append("group:").append(group).append('\n');
        }
        return s.toString();
    }
}
