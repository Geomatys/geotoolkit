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
package org.geotoolkit.ebrim.xml.v250;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element name="Action" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ActionType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="selector" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="notificationInterval" type="{http://www.w3.org/2001/XMLSchema}duration" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "action"
})
@XmlRootElement(name = "Subscription")
public class Subscription extends RegistryObjectType {

    @XmlElement(name = "Action", namespace = "", required = true)
    private List<ActionType> action;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String selector;
    @XmlAttribute
    private XMLGregorianCalendar startDate;
    @XmlAttribute
    private XMLGregorianCalendar endDate;
    @XmlAttribute
    private Duration notificationInterval;

    /**
     * Gets the value of the action property.
     */
    public List<ActionType> getAction() {
        if (action == null) {
            action = new ArrayList<ActionType>();
        }
        return this.action;
    }
    
    /**
     * Sets the value of the action property.
     */
    public void setAction(final ActionType action) {
        if (this.action == null) {
            this.action = new ArrayList<ActionType>();
        }
        this.action.add(action);
    }
    
    /**
     * Sets the value of the action property.
     */
    public void setAction(final List<ActionType> action) {
        this.action = action;
    }

    /**
     * Gets the value of the selector property.
     */
    public String getSelector() {
        return selector;
    }

    /**
     * Sets the value of the selector property.
     */
    public void setSelector(final String value) {
        this.selector = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     */
    public void setStartDate(final XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     */
    public void setEndDate(final XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the notificationInterval property.
     *     
     */
    public Duration getNotificationInterval() {
        return notificationInterval;
    }

    /**
     * Sets the value of the notificationInterval property.
     *     
     */
    public void setNotificationInterval(final Duration value) {
        this.notificationInterval = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (endDate != null) {
            sb.append("endDate:").append(endDate).append('\n');
        }
        if (notificationInterval != null) {
            sb.append("notificationInterval:").append(notificationInterval).append('\n');
        }
        if (selector != null) {
            sb.append("selector:").append(selector).append('\n');
        }
        if (startDate != null) {
            sb.append("startDate:").append(startDate).append('\n');
        }
        if (action != null) {
            sb.append("action:\n");
            for (ActionType ac : action) {
                sb.append(ac).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Subscription && super.equals(obj)) {
            final Subscription that = (Subscription) obj;
            return Utilities.equals(this.action,               that.action) &&
                   Utilities.equals(this.endDate,              that.endDate) &&
                   Utilities.equals(this.notificationInterval, that.notificationInterval) &&
                   Utilities.equals(this.selector,             that.selector) &&
                   Utilities.equals(this.startDate,            that.startDate);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + super.hashCode();
        hash = 53 * hash + (this.action != null ? this.action.hashCode() : 0);
        hash = 53 * hash + (this.selector != null ? this.selector.hashCode() : 0);
        hash = 53 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 53 * hash + (this.endDate != null ? this.endDate.hashCode() : 0);
        hash = 53 * hash + (this.notificationInterval != null ? this.notificationInterval.hashCode() : 0);
        return hash;
    }
}
