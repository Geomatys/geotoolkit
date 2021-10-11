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
package org.geotoolkit.wfs.xml.v110;

import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.Version;
import org.geotoolkit.wfs.xml.BaseRequest;


/**
 * XML encoded WFS operation request base, for all operations except GetCapabilities.
 *
 *
 * <p>Java class for BaseRequestType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BaseRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="service" type="{http://www.opengis.net/ows}ServiceType" default="WFS" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" default="1.1.0" />
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseRequestType")
@XmlSeeAlso({
    TransactionType.class,
    LockFeatureType.class,
    GetFeatureType.class,
    GetFeatureWithLockType.class,
    GetGmlObjectType.class,
    DescribeFeatureTypeType.class
})
public abstract class BaseRequestType implements BaseRequest {

    @XmlAttribute
    private String service;
    @XmlAttribute
    private String version;
    @XmlAttribute
    private String handle;

    @XmlTransient
    private Map<String, String> prefixMapping;

    public BaseRequestType() {

    }

    public BaseRequestType(final String service, final String version, final String handle) {
        this.service = service;
        this.version = version;
        this.handle  = handle;
    }

    /**
     * Gets the value of the service property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getService() {
        if (service == null) {
            return "WFS";
        } else {
            return service;
        }
    }

    /**
     * Sets the value of the service property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setService(final String value) {
        this.service = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Version getVersion() {
        if (version != null) {
            return new Version(version);
        }
        return null;
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
     * Gets the value of the handle property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Sets the value of the handle property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHandle(final String value) {
        this.handle = value;
    }

    /**
     * @return the prefixMapping
     */
    public Map<String, String> getPrefixMapping() {
        return prefixMapping;
    }

    /**
     * @param prefixMapping the prefixMapping to set
     */
    public void setPrefixMapping(Map<String, String> prefixMapping) {
        this.prefixMapping = prefixMapping;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append(']');
        if (handle != null) {
            sb.append("handle=").append(handle).append('\n');
        }
        if (version != null) {
            sb.append("version=").append(version).append('\n');
        }
        if (service != null) {
            sb.append("service=").append(service).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BaseRequestType) {
            final BaseRequestType that = (BaseRequestType) object;
            return  Objects.equals(this.handle, that.handle) &&
                    Objects.equals(this.service, that.service) &&
                    Objects.equals(this.version, that.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.service != null ? this.service.hashCode() : 0);
        hash = 73 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 73 * hash + (this.handle != null ? this.handle.hashCode() : 0);
        return hash;
    }
}
