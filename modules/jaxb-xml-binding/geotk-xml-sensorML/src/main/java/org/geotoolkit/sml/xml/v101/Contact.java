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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractContact;
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
 *       &lt;choice minOccurs="0">
 *         &lt;group ref="{http://www.opengis.net/sensorML/1.0.1}ContactGroup"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}ContactList"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
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
    "person",
    "responsibleParty",
    "contactList"
})
@XmlRootElement(name = "Contact")
public class Contact implements AbstractContact {

    @XmlElement(name = "Person")
    private Person person;
    @XmlElement(name = "ResponsibleParty")
    private ResponsibleParty responsibleParty;
    @XmlElement(name = "ContactList")
    private ContactList contactList;
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

    public Contact() {

    }

    public Contact(final ResponsibleParty responsibleParty) {
        this.responsibleParty = responsibleParty;
    }
    
    public Contact(final AbstractContact contact) {
        if (contact != null) {
            this.actuate = contact.getActuate();
            this.arcrole = contact.getArcrole();
            if (contact.getContactList() != null) {
                this.contactList = new ContactList(contact.getContactList());
            }
            this.href = contact.getHref();
            if (contact.getPerson() != null) {
                this.person = new Person(contact.getPerson());
            }
            this.remoteSchema = contact.getRemoteSchema();
            if (contact.getResponsibleParty() != null) {
                this.responsibleParty = new ResponsibleParty(contact.getResponsibleParty());
            }
            this.role = contact.getRole();
            this.show = contact.getShow();
            this.title = contact.getTitle();
            this.type = contact.getType();
        }
    }

    public Contact(final String role, final ResponsibleParty responsibleParty) {
        this.role             = role;
        this.responsibleParty = responsibleParty;
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
     * Gets the value of the contactList property.
     * 
     * @return
     *     possible object is
     *     {@link ContactList }
     *     
     */
    public ContactList getContactList() {
        return contactList;
    }

    /**
     * Sets the value of the contactList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactList }
     *     
     */
    public void setContactList(final ContactList value) {
        this.contactList = value;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Contact]").append("\n");
        if (person != null) {
            sb.append("person: ").append(person).append('\n');
        }
        if (responsibleParty != null) {
            sb.append("responsibleParty: ").append(responsibleParty).append('\n');
        }
        if (contactList != null) {
            sb.append("contactList: ").append(contactList).append('\n');
        }
        if (remoteSchema != null) {
            sb.append("remoteSchema: ").append(remoteSchema).append('\n');
        }
        if (actuate != null) {
            sb.append("actuate: ").append(actuate).append('\n');
        }
        if (arcrole != null) {
            sb.append("actuate: ").append(arcrole).append('\n');
        }
        if (href != null) {
            sb.append("href: ").append(href).append('\n');
        }
        if (role != null) {
            sb.append("role: ").append(role).append('\n');
        }
        if (show != null) {
            sb.append("show: ").append(show).append('\n');
        }
        if (title != null) {
            sb.append("title: ").append(title).append('\n');
        }
        if (type != null) {
            sb.append("type: ").append(type).append('\n');
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

        if (object instanceof Contact) {
            final Contact that = (Contact) object;
            return Utilities.equals(this.actuate,      that.actuate)       &&
                   Utilities.equals(this.arcrole,      that.arcrole)       &&
                   Utilities.equals(this.href,         that.href)          &&
                   Utilities.equals(this.remoteSchema, that.remoteSchema)  &&
                   Utilities.equals(this.role,         that.role)          &&
                   Utilities.equals(this.show,         that.show)          &&
                   Utilities.equals(this.title,        that.title)         &&
                   Utilities.equals(this.contactList,  that.contactList)   &&
                   Utilities.equals(this.responsibleParty, that.responsibleParty) &&
                   Utilities.equals(this.person,       that.person)        &&
                   Utilities.equals(this.type,         that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.person != null ? this.person.hashCode() : 0);
        hash = 13 * hash + (this.responsibleParty != null ? this.responsibleParty.hashCode() : 0);
        hash = 13 * hash + (this.contactList != null ? this.contactList.hashCode() : 0);
        hash = 13 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 13 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 13 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 13 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 13 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 13 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 13 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 13 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

}
