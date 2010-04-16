/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractEventList;
import org.geotoolkit.sml.xml.AbstractEventListMember;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="member" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0}Event"/>
 *                 &lt;/sequence>
 *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/gml}id"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "member"
})
@XmlRootElement(name = "EventList")
public class EventList implements AbstractEventList {

    @XmlElement(required = true)
    private List<EventListMember> member;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    public EventList() {

    }

    public EventList(AbstractEventList el) {
        if (el != null) {
            this.id = el.getId();
            if (el.getMember() != null) {
                this.member = new ArrayList<EventListMember>();
                for (AbstractEventListMember m : el.getMember()) {
                    this.member.add(new EventListMember(m));
                }
            }
        }
    }
    
    /**
     * Gets the value of the member property.
     */
    public List<EventListMember> getMember() {
        if (member == null) {
            member = new ArrayList<EventListMember>();
        }
        return this.member;
    }

    /**
     * Gets the value of the member property.
     */
    public void setMember(List<EventListMember> member) {
        this.member = member;
    }

    /**
     * Gets the value of the member property.
     */
    public void setMember(EventListMember member) {
        if (this.member == null) {
            this.member = new ArrayList<EventListMember>();
        }
        this.member.add(member);
    }

    
    /**
     * Gets the value of the id property.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(String value) {
        this.id = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[EventList]").append("\n");
        if (member != null) {
            for (EventListMember k : member) {
                sb.append("member:").append(k).append('\n');
            }
        }
        if (id != null) {
            sb.append("id: ").append(id).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof EventList) {
            final EventList that = (EventList) object;

            return Utilities.equals(this.member, that.member)
                    && Utilities.equals(this.id, that.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.member != null ? this.member.hashCode() : 0);
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
