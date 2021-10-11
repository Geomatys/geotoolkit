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
package org.geotoolkit.wmc.xml.v110;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for ServerType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ServerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OnlineResource" type="{http://www.opengis.net/context}OnlineResourceType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="service" use="required" type="{http://www.opengis.net/context}serviceType" />
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServerType", propOrder = {
    "onlineResource"
})
public class ServerType {

    @XmlElement(name = "OnlineResource", required = true)
    protected OnlineResourceType onlineResource;
    @XmlAttribute(required = true)
    protected ServiceType service;
    @XmlAttribute
    protected String title;
    @XmlAttribute(required = true)
    protected String version;

    /**
     * Gets the value of the onlineResource property.
     *
     * @return
     *     possible object is
     *     {@link OnlineResourceType }
     *
     */
    public OnlineResourceType getOnlineResource() {
        return onlineResource;
    }

    /**
     * Sets the value of the onlineResource property.
     *
     * @param value
     *     allowed object is
     *     {@link OnlineResourceType }
     *
     */
    public void setOnlineResource(final OnlineResourceType value) {
        this.onlineResource = value;
    }

    /**
     * Gets the value of the service property.
     *
     * @return
     *     possible object is
     *     {@link ServiceType }
     *
     */
    public ServiceType getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceType }
     *
     */
    public void setService(final ServiceType value) {
        this.service = value;
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
     * Gets the value of the version property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(final String value) {
        this.version = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ServerType) {
            final ServerType that = (ServerType) object;

            return Objects.equals(this.onlineResource, that.onlineResource) &&
                   Objects.equals(this.service, that.service) &&
                   Objects.equals(this.title, that.title) &&
                   Objects.equals(this.version, that.version);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.onlineResource != null ? this.onlineResource.hashCode() : 0);
        hash = 41 * hash + (this.service != null ? this.service.hashCode() : 0);
        hash = 41 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 41 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[ServerType]\n");
        if (title != null) {
            s.append("title:").append(title).append('\n');
        }
        if (onlineResource != null) {
            s.append("onlineResource:").append(onlineResource).append('\n');
        }
        if (service != null) {
            s.append("service:").append(service).append('\n');
        }
        if (version != null) {
            s.append("version:").append(version).append('\n');
        }
        return s.toString();
    }
}
