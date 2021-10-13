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
package org.geotoolkit.csw.xml.v202;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.AbstractCswRequest;
import org.geotoolkit.ows.xml.RequestBase;
import org.apache.sis.util.Version;


/**
 *
 * Base type for all request messages except GetCapabilities.
 * The attributes identify the relevant service type and version.
 *
 *
 * <p>Java class for RequestBaseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RequestBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="service" use="required" type="{http://www.opengis.net/ows}ServiceType" fixed="CSW" />
 *       &lt;attribute name="version" use="required" type="{http://www.opengis.net/ows}VersionType" fixed="2.0.2" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestBaseType")
@XmlSeeAlso({
    DescribeRecordType.class,
    GetRecordByIdType.class,
    GetDomainType.class,
    GetRecordsType.class,
    HarvestType.class,
    TransactionType.class
})
public abstract class RequestBaseType implements RequestBase, AbstractCswRequest {

    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute(required = true)
    private String version;

    /**
     * An empty constructor used by JAXB
     */
    RequestBaseType() {

    }

    /**
     * Super contructor used by thi child classes
     *
     * @param service the name of the service (fixed to "CSW")
     * @param version the version of the service
     */
    protected RequestBaseType(final String service, final String version) {
        this.service = service;
        this.version = version;
    }

    protected RequestBaseType(final RequestBaseType other) {
        if (other != null) {
            this.service = other.service;
            this.version = other.version;
        }
    }

    /**
     * Gets the value of the service property.
     */
    @Override
    public String getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     */
    @Override
    public void setService(final String service) {
        this.service = service;
    }

    /**
     * Gets the value of the version property.
     */
    @Override
    public Version getVersion() {
        if (version != null) {
            return new Version(version);
        }
        return null;
    }

    /**
     * Gets the value of the version property.
     */
    @Override
    public void setVersion(final String version) {
        this.version = version;
    }

     /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof RequestBaseType) {
            final RequestBaseType that = (RequestBaseType) object;
            return Objects.equals(this.service,  that.service) &&
                   Objects.equals(this.version,  that.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.service != null ? this.service.hashCode() : 0);
        hash = 59 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]").append('\n');

        if (service != null) {
            s.append("service: ").append(service).append('\n');
        }
        if (version != null) {
            s.append("version: ").append(version).append('\n');
        }
        return s.toString();
    }
}
