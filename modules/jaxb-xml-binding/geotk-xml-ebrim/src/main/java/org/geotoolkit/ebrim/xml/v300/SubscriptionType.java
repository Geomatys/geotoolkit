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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.util.Utilities;


/**
 * A Subscription for specified Events in an ebXML V3+ registry.
 * 
 * <p>Java class for SubscriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SubscriptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}Action" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="selector" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="startTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="endTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="notificationInterval" type="{http://www.w3.org/2001/XMLSchema}duration" default="P1D" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriptionType", propOrder = {
    "action"
})
@XmlRootElement(name = "Subscription")
public class SubscriptionType extends RegistryObjectType {

    @XmlElementRef(name = "Action", namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", type = JAXBElement.class)
    private List<JAXBElement<? extends ActionType>> action;
    @XmlAttribute(required = true)
    private String selector;
    @XmlAttribute
    private XMLGregorianCalendar startTime;
    @XmlAttribute
    private XMLGregorianCalendar endTime;
    @XmlAttribute
    private Duration notificationInterval;
    
    @XmlTransient
    private static ObjectFactory factory = new ObjectFactory();

    /**
     * Gets the value of the action property.
     */
    public List<JAXBElement<? extends ActionType>> getAction() {
        if (action == null) {
            action = new ArrayList<JAXBElement<? extends ActionType>>();
        }
        return this.action;
    }
    
        /**
     * Sets the value of the action property.
     */
    public void setAction(final ActionType action) {
        if (this.action == null) {
            this.action = new ArrayList<JAXBElement<? extends ActionType>>();
        }
        if (action instanceof NotifyActionType)
            this.action.add(factory.createNotifyAction((NotifyActionType)action));
        else
            this.action.add(factory.createAction(action));
    }
    
    /**
     * Sets the value of the action property.
     */
    public void setAction(final List<JAXBElement<? extends ActionType>>  action) {
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
     * Gets the value of the startTime property.
     */
    public XMLGregorianCalendar getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     *     
     */
    public void setStartTime(final XMLGregorianCalendar value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the endTime property.
     */
    public XMLGregorianCalendar getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     */
    public void setEndTime(final XMLGregorianCalendar value) {
        this.endTime = value;
    }

    /**
     * Gets the value of the notificationInterval property.
     */
    public Duration getNotificationInterval() {
        return notificationInterval;
    }

    /**
     * Sets the value of the notificationInterval property.
     */
    public void setNotificationInterval(final Duration value) {
        this.notificationInterval = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (endTime != null) {
            sb.append("endTime:").append(endTime).append('\n');
        }
        if (notificationInterval != null) {
            sb.append("notificationInterval:").append(notificationInterval).append('\n');
        }
        if (selector != null) {
            sb.append("selector:").append(selector).append('\n');
        }
        if (startTime != null) {
            sb.append("startTime:").append(startTime).append('\n');
        }
        if (action != null) {
            sb.append("action:\n");
            for (JAXBElement<? extends ActionType> cl : action) {
                sb.append(cl).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SubscriptionType && super.equals(obj)) {
            final SubscriptionType that = (SubscriptionType) obj;
            boolean eq = false;
            if (this.action == null && that.action == null) {
                eq = true;
            } else if (this.action != null && that.action != null) {
                if (this.action.size() == that.action.size()) {
                    eq = true;
                    for (int i = 0; i < this.action.size(); i++) {
                        if (!this.action.get(i).getValue().equals(that.action.get(i).getValue())) {
                            eq = false;
                        }
                    }
                }
            }
            return Utilities.equals(this.endTime,              that.endTime) &&
                   Utilities.equals(this.notificationInterval, that.notificationInterval) &&
                   Utilities.equals(this.selector,             that.selector) &&
                   Utilities.equals(this.startTime,            that.startTime) &&
                   eq;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + super.hashCode();
        hash = 29 * hash + (this.action != null ? this.action.hashCode() : 0);
        hash = 29 * hash + (this.selector != null ? this.selector.hashCode() : 0);
        hash = 29 * hash + (this.startTime != null ? this.startTime.hashCode() : 0);
        hash = 29 * hash + (this.endTime != null ? this.endTime.hashCode() : 0);
        hash = 29 * hash + (this.notificationInterval != null ? this.notificationInterval.hashCode() : 0);
        return hash;
    }


}
