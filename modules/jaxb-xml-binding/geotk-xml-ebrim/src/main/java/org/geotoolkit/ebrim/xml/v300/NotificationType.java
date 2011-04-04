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
import org.geotoolkit.util.Utilities;


/**
 * Notification of registry events.
 * 
 * <p>Java class for NotificationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NotificationType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectList"/>
 *       &lt;/sequence>
 *       &lt;attribute name="subscription" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotificationType", propOrder = {
    "registryObjectList"
})
@XmlRootElement(name = "Notification")
public class NotificationType extends RegistryObjectType {

    @XmlElement(name = "RegistryObjectList", required = true)
    private RegistryObjectListType registryObjectList;
    @XmlAttribute(required = true)
    private String subscription;

    /**
     * Gets the value of the registryObjectList property.
     */
    public RegistryObjectListType getRegistryObjectList() {
        return registryObjectList;
    }

    /**
     * Sets the value of the registryObjectList property.
     * 
     */
    public void setRegistryObjectList(final RegistryObjectListType value) {
        this.registryObjectList = value;
    }

    /**
     * Gets the value of the subscription property.
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     * Sets the value of the subscription property.
     */
    public void setSubscription(final String value) {
        this.subscription = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (registryObjectList != null) {
            sb.append("registryObjectList:").append(registryObjectList).append('\n');
        }
        if (subscription != null) {
            sb.append("subscription:").append(subscription).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof NotificationType && super.equals(obj)) {
            final NotificationType that = (NotificationType) obj;
            return Utilities.equals(this.registryObjectList, that.registryObjectList) &&
                   Utilities.equals(this.subscription,       that.subscription);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + super.hashCode();
        hash = 31 * hash + (this.registryObjectList != null ? this.registryObjectList.hashCode() : 0);
        hash = 31 * hash + (this.subscription != null ? this.subscription.hashCode() : 0);
        return hash;
    }
}
