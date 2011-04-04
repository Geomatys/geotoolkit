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
package org.geotoolkit.ebrim.xml.v300;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.util.Utilities;


/**
 * An Event that forms an audit trail in ebXML Registry.
 * 
 * <p>Java class for AuditableEventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuditableEventType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element name="affectedObjects" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ObjectRefListType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="eventType" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="timestamp" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="user" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="requestId" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuditableEventType", propOrder = {
    "affectedObjects"
})
@XmlRootElement(name = "AuditableEvent")
public class AuditableEventType extends RegistryObjectType {

    @XmlElement(required = true)
    private ObjectRefListType affectedObjects;
    @XmlAttribute(required = true)
    private String eventType;
    @XmlAttribute(required = true)
    private XMLGregorianCalendar timestamp;
    @XmlAttribute(required = true)
    private String user;
    @XmlAttribute(required = true)
    private String requestId;

    /**
     * Gets the value of the affectedObjects property.
     */
    public ObjectRefListType getAffectedObjects() {
        return affectedObjects;
    }

    /**
     * Sets the value of the affectedObjects property.
     */
    public void setAffectedObjects(final ObjectRefListType value) {
        this.affectedObjects = value;
    }

    /**
     * Gets the value of the eventType property.
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Sets the value of the eventType property.
     */
    public void setEventType(final String value) {
        this.eventType = value;
    }

    /**
     * Gets the value of the timestamp property.
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     */
    public void setTimestamp(final XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the user property.
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     */
    public void setUser(final String value) {
        this.user = value;
    }

    /**
     * Gets the value of the requestId property.
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     */
    public void setRequestId(final String value) {
        this.requestId = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (affectedObjects != null) {
            sb.append("affectedObjects:").append(affectedObjects).append('\n');
        }
        if (eventType != null) {
            sb.append("eventType:").append(eventType).append('\n');
        }
        if (requestId != null) {
            sb.append("requestId:").append(requestId).append('\n');
        }
        if (timestamp != null) {
            sb.append("timestamp:").append(timestamp).append('\n');
        }
        if (user != null) {
            sb.append("user:").append(user).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AuditableEventType && super.equals(obj)) {
            final AuditableEventType that = (AuditableEventType) obj;
            return Utilities.equals(this.affectedObjects, that.affectedObjects) &&
                   Utilities.equals(this.eventType,       that.eventType) &&
                   Utilities.equals(this.requestId,       that.requestId) &&
                   Utilities.equals(this.timestamp,       that.timestamp) &&
                   Utilities.equals(this.user,            that.user);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + super.hashCode();
        hash = 89 * hash + (this.affectedObjects != null ? this.affectedObjects.hashCode() : 0);
        hash = 89 * hash + (this.eventType != null ? this.eventType.hashCode() : 0);
        hash = 89 * hash + (this.timestamp != null ? this.timestamp.hashCode() : 0);
        hash = 89 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 89 * hash + (this.requestId != null ? this.requestId.hashCode() : 0);
        return hash;
    }
}
