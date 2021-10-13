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
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v311.StringOrRefType;
import org.geotoolkit.sml.xml.AbstractContactList;
import org.geotoolkit.sml.xml.AbstractContactListMember;


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
 *                 &lt;group ref="{http://www.opengis.net/sensorML/1.0}ContactGroup"/>
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
 * @module
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
    private String id;

    public ContactList() {

    }

    public ContactList(final ResponsibleParty resp) {
        this.member = new ArrayList<Member>();
        this.member.add(new Member(resp));
    }

    public ContactList(final Person person) {
        this.member = new ArrayList<Member>();
        this.member.add(new Member(person));
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
     */
    public StringOrRefType getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     */
    public void setDescription(final StringOrRefType value) {
        this.description = value;
    }

    /**
     * Gets the value of the member property.
     */
    public List<ContactList.Member> getMember() {
        if (member == null) {
            member = new ArrayList<ContactList.Member>();
        }
        return this.member;
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
            return Objects.equals(this.description, that.description)    &&
                   Objects.equals(this.id,          that.id)             &&
                   Objects.equals(this.member,      that.member);
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
     *       &lt;group ref="{http://www.opengis.net/sensorML/1.0}ContactGroup"/>
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
        @XmlAttribute
        private List<String> nilReason;
        @XmlAttribute(namespace = "http://www.opengis.net/gml")
        private String remoteSchema;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String actuate;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String arcrole;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String href;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String role;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String show;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String title;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String type;

        public Member() {

        }

        public Member(final Person person) {
            this.person = person;
        }

        public Member(final ResponsibleParty responsibleParty) {
            this.responsibleParty = responsibleParty;
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
         */
        public Person getPerson() {
            return person;
        }

        /**
         * Sets the value of the person property.
         */
        public void setPerson(final Person value) {
            this.person = value;
        }

        /**
         * Gets the value of the responsibleParty property.
         */
        public ResponsibleParty getResponsibleParty() {
            return responsibleParty;
        }

        /**
         * Sets the value of the responsibleParty property.
         */
        public void setResponsibleParty(final ResponsibleParty value) {
            this.responsibleParty = value;
        }

        /**
         * Gets the value of the nilReason property.
         *
         */
        public List<String> getNilReason() {
            if (nilReason == null) {
                nilReason = new ArrayList<String>();
            }
            return this.nilReason;
        }

        /**
         * Gets the value of the remoteSchema property.
         */
        public String getRemoteSchema() {
            return remoteSchema;
        }

        /**
         * Sets the value of the remoteSchema property.
         */
        public void setRemoteSchema(final String value) {
            this.remoteSchema = value;
        }

        /**
         * Gets the value of the actuate property.
         */
        public String getActuate() {
            return actuate;
        }

        /**
         * Sets the value of the actuate property.
         */
        public void setActuate(final String value) {
            this.actuate = value;
        }

        /**
         * Gets the value of the arcrole property.
         */
        public String getArcrole() {
            return arcrole;
        }

        /**
         * Sets the value of the arcrole property.
         */
        public void setArcrole(final String value) {
            this.arcrole = value;
        }

        /**
         * Gets the value of the href property.
         */
        public String getHref() {
            return href;
        }

        /**
         * Sets the value of the href property.
         */
        public void setHref(final String value) {
            this.href = value;
        }

        /**
         * Gets the value of the role property.
         */
        public String getRole() {
            return role;
        }

        /**
         * Sets the value of the role property.
         */
        public void setRole(final String value) {
            this.role = value;
        }

        /**
         * Gets the value of the show property.
         */
        public String getShow() {
            return show;
        }

        /**
         * Sets the value of the show property.
         */
        public void setShow(final String value) {
            this.show = value;
        }

        /**
         * Gets the value of the title property.
         */
        public String getTitle() {
            return title;
        }

        /**
         * Sets the value of the title property.
         */
        public void setTitle(final String value) {
            this.title = value;
        }

        /**
         * Gets the value of the type property.
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         */
        public void setType(final String value) {
            this.type = value;
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
                return Objects.equals(this.actuate,      that.actuate)       &&
                       Objects.equals(this.arcrole,      that.arcrole)       &&
                       Objects.equals(this.href,         that.href)          &&
                       Objects.equals(this.nilReason,    that.nilReason)     &&
                       Objects.equals(this.remoteSchema, that.remoteSchema)  &&
                       Objects.equals(this.role,         that.role)          &&
                       Objects.equals(this.show,         that.show)          &&
                       Objects.equals(this.title,        that.title)         &&
                       Objects.equals(this.responsibleParty, that.responsibleParty) &&
                       Objects.equals(this.person,       that.person)        &&
                       Objects.equals(this.type,         that.type);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 17 * hash + (this.person != null ? this.person.hashCode() : 0);
            hash = 17 * hash + (this.responsibleParty != null ? this.responsibleParty.hashCode() : 0);
            hash = 17 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
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
