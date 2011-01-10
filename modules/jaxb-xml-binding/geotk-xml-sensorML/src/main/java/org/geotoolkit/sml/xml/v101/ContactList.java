/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.sml.xml.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v311.StringOrRefType;
import org.geotoolkit.sml.xml.AbstractContactList;
import org.geotoolkit.sml.xml.AbstractContactListMember;
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
 *         &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
 *         &lt;element name="member" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;group ref="{http://www.opengis.net/sensorML/1.0.1}ContactGroup"/>
 *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
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
    "description",
    "member"
})
@XmlRootElement(name = "ContactList")
public class ContactList implements AbstractContactList {

    @XmlElement(namespace = "http://www.opengis.net/gml")
    private StringOrRefType description;
    @XmlElement(required = true)
    private List<ContactList.Member> member;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    public ContactList() {

    }

    public ContactList(final AbstractContactList cl) {
        if (cl != null) {
            this.id = cl.getId();
            this.description = cl.getDescription();
            if (cl.getMember() != null) {
                this.member = new ArrayList<Member>();
                for (AbstractContactListMember m : cl.getMember()) {
                    this.member.add(new ContactList.Member(m));
                }
            }
        }
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link StringOrRefType }
     *     
     */
    public StringOrRefType getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringOrRefType }
     *     
     */
    public void setDescription(final StringOrRefType value) {
        this.description = value;
    }

    /**
     * Gets the value of the member property.
     * 
    */
    public List<ContactList.Member> getMember() {
        if (member == null) {
            member = new ArrayList<ContactList.Member>();
        }
        return this.member;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(final String value) {
        this.id = value;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof ContactList) {
            final ContactList that = (ContactList) object;
            return Utilities.equals(this.description, that.description)    &&
                   Utilities.equals(this.id,          that.id)             &&
                   Utilities.equals(this.member,      that.member);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 13 * hash + (this.member != null ? this.member.hashCode() : 0);
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;group ref="{http://www.opengis.net/sensorML/1.0.1}ContactGroup"/>
     *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "person",
        "responsibleParty"
    })
    public static class Member implements AbstractContactListMember {

        @XmlElement(name = "Person")
        private Person person;
        @XmlElement(name = "ResponsibleParty")
        private ResponsibleParty responsibleParty;
        @XmlAttribute(namespace = "http://www.opengis.net/gml")
        @XmlSchemaType(name = "anyURI")
        private String remoteSchema;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String type;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String href;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String role;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String arcrole;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String title;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String show;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String actuate;

        public Member() {
            
        }

        public Member(final AbstractContactListMember member) {
            if (member != null) {
                this.actuate = member.getActuate();
                this.arcrole = member.getArcrole();
                this.href = member.getHref();
                if (member.getPerson() != null) {
                    this.person = new Person(member.getPerson());
                }
                this.remoteSchema = member.getRemoteSchema();
                if (member.getResponsibleParty() != null) {
                    this.responsibleParty = new ResponsibleParty(member.getResponsibleParty());
                }
                this.role = member.getRole();
                this.show = member.getShow();
                this.title = member.getTitle();
                this.type = member.getType();
            }
        }
        /**
         * Gets the value of the person property.
         * 
         * @return
         *     possible object is
         *     {@link Person }
         *     
         */
        public Person getPerson() {
            return person;
        }

        /**
         * Sets the value of the person property.
         * 
         * @param value
         *     allowed object is
         *     {@link Person }
         *     
         */
        public void setPerson(final Person value) {
            this.person = value;
        }

        /**
         * Gets the value of the responsibleParty property.
         * 
         * @return
         *     possible object is
         *     {@link ResponsibleParty }
         *     
         */
        public ResponsibleParty getResponsibleParty() {
            return responsibleParty;
        }

        /**
         * Sets the value of the responsibleParty property.
         * 
         * @param value
         *     allowed object is
         *     {@link ResponsibleParty }
         *     
         */
        public void setResponsibleParty(final ResponsibleParty value) {
            this.responsibleParty = value;
        }

        /**
         * Gets the value of the remoteSchema property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRemoteSchema() {
            return remoteSchema;
        }

        /**
         * Sets the value of the remoteSchema property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRemoteSchema(final String value) {
            this.remoteSchema = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
        return type;
    }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(final String value) {
            this.type = value;
        }

        /**
         * Gets the value of the href property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHref() {
            return href;
        }

        /**
         * Sets the value of the href property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHref(final String value) {
            this.href = value;
        }

        /**
         * Gets the value of the role property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRole() {
            return role;
        }

        /**
         * Sets the value of the role property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRole(final String value) {
            this.role = value;
        }

        /**
         * Gets the value of the arcrole property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getArcrole() {
            return arcrole;
        }

        /**
         * Sets the value of the arcrole property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setArcrole(final String value) {
            this.arcrole = value;
        }

        /**
         * Gets the value of the title property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTitle() {
            return title;
        }

        /**
         * Sets the value of the title property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTitle(final String value) {
            this.title = value;
        }

        /**
         * Gets the value of the show property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getShow() {
            return show;
        }

        /**
         * Sets the value of the show property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setShow(final String value) {
            this.show = value;
        }

        /**
         * Gets the value of the actuate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getActuate() {
            return actuate;
        }

        /**
         * Sets the value of the actuate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setActuate(final String value) {
            this.actuate = value;
        }

        /**
         * Verify if this entry is identical to specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }

            if (object instanceof Member) {
                final Member that = (Member) object;
                return Utilities.equals(this.actuate,      that.actuate)       &&
                       Utilities.equals(this.arcrole,      that.arcrole)       &&
                       Utilities.equals(this.href,         that.href)          &&
                       Utilities.equals(this.remoteSchema, that.remoteSchema)  &&
                       Utilities.equals(this.role,         that.role)          &&
                       Utilities.equals(this.show,         that.show)          &&
                       Utilities.equals(this.title,        that.title)         &&
                       Utilities.equals(this.responsibleParty, that.responsibleParty) &&
                       Utilities.equals(this.person,       that.person)        &&
                       Utilities.equals(this.type,         that.type);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 17 * hash + (this.person != null ? this.person.hashCode() : 0);
            hash = 17 * hash + (this.responsibleParty != null ? this.responsibleParty.hashCode() : 0);
            hash = 17 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
            hash = 17 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
            hash = 17 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
            hash = 17 * hash + (this.href != null ? this.href.hashCode() : 0);
            hash = 17 * hash + (this.role != null ? this.role.hashCode() : 0);
            hash = 17 * hash + (this.show != null ? this.show.hashCode() : 0);
            hash = 17 * hash + (this.title != null ? this.title.hashCode() : 0);
            hash = 17 * hash + (this.type != null ? this.type.hashCode() : 0);
            return hash;
        }
    }

}
