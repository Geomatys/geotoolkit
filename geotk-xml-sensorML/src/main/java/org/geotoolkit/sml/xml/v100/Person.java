/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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
import org.geotoolkit.sml.xml.AbstractPerson;


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
 *         &lt;element name="surname" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="userID" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="affiliation" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="phoneNumber" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}token"/>
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
    "surname",
    "name",
    "userID",
    "affiliation",
    "phoneNumber",
    "email"
})
@XmlRootElement(name = "Person")
public class Person implements AbstractPerson {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String surname;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String name;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String userID;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String affiliation;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String phoneNumber;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String email;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    public Person() {

    }

    public Person(final AbstractPerson person) {
        if (person != null) {
            this.affiliation = person.getAffiliation();
            this.email       = person.getEmail();
            this.id          = person.getId();
            this.name        = person.getName();
            this.phoneNumber = person.getPhoneNumber();
            this.surname     = person.getSurname();
            this.userID      = person.getUserID();
        }
    }

    /**
     * Gets the value of the surname property.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the value of the surname property.
     */
    public void setSurname(final String value) {
        this.surname = value;
    }

    /**
     * Gets the value of the name property.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     */
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Gets the value of the userID property.
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Sets the value of the userID property.
     */
    public void setUserID(final String value) {
        this.userID = value;
    }

    /**
     * Gets the value of the affiliation property.
     */
    public String getAffiliation() {
        return affiliation;
    }

    /**
     * Sets the value of the affiliation property.
     */
    public void setAffiliation(final String value) {
        this.affiliation = value;
    }

    /**
     * Gets the value of the phoneNumber property.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     */
    public void setPhoneNumber(final String value) {
        this.phoneNumber = value;
    }

    /**
     * Gets the value of the email property.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     */
    public void setEmail(final String value) {
        this.email = value;
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

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof Person && super.equals(object)) {
            final Person that = (Person) object;
            return Objects.equals(this.affiliation,     that.affiliation)       &&
                   Objects.equals(this.email,           that.email)             &&
                   Objects.equals(this.id,              that.id)                &&
                   Objects.equals(this.name,            that.name)              &&
                   Objects.equals(this.phoneNumber,     that.phoneNumber)       &&
                   Objects.equals(this.surname,         that.surname)           &&
                   Objects.equals(this.userID,          that.userID);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.surname != null ? this.surname.hashCode() : 0);
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 89 * hash + (this.userID != null ? this.userID.hashCode() : 0);
        hash = 89 * hash + (this.affiliation != null ? this.affiliation.hashCode() : 0);
        hash = 89 * hash + (this.phoneNumber != null ? this.phoneNumber.hashCode() : 0);
        hash = 89 * hash + (this.email != null ? this.email.hashCode() : 0);
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

}
