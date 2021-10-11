/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.service;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.internal.jaxb.gco.GO_GenericName;
import org.opengis.util.LocalName;


/**
 * <p>Java class for SV_ServiceType_Type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SV_ServiceType_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SV_ServiceType_Type")
@XmlRootElement(name = "SV_ServiceType")
public class ServiceType {

    @XmlJavaTypeAdapter(GO_GenericName.class)
    @XmlElement
    private LocalName serviceType;

    public ServiceType() {
    }

    public ServiceType(final LocalName serviceType) {
        this.serviceType = serviceType;
    }

    public LocalName getServiceType() {
        return serviceType;
    }

    public void setServiceType(final LocalName serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ServiceType) {
            final ServiceType that = (ServiceType) object;
            return Objects.equals(this.serviceType, that.serviceType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.serviceType != null ? this.serviceType.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ServiceType]\n");
        if ( serviceType != null) {
            sb.append("serviceType:").append(serviceType);
        }
        return sb.toString();
    }
}
