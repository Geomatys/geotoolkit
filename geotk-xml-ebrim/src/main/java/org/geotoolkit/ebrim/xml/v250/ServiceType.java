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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ServiceType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryEntryType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ServiceBinding" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ServiceType", propOrder = {
    "serviceBinding"
})
@XmlRootElement(name = "Service")
public class ServiceType extends RegistryEntryType {

    @XmlElement(name = "ServiceBinding")
    private List<ServiceBindingType> serviceBinding;

    /**
     * Gets the value of the serviceBinding property.
     */
    public List<ServiceBindingType> getServiceBinding() {
        if (serviceBinding == null) {
            serviceBinding = new ArrayList<ServiceBindingType>();
        }
        return this.serviceBinding;
    }

    /**
     * Sets the value of the serviceBinding property.
     */
    public void setServiceBinding(final ServiceBindingType serviceBinding) {
        if (this.serviceBinding == null) {
            this.serviceBinding = new ArrayList<ServiceBindingType>();
        }
        this.serviceBinding.add(serviceBinding);
    }

    /**
     * Sets the value of the serviceBinding property.
     */
    public void setServiceBinding(final List<ServiceBindingType> serviceBinding) {
        this.serviceBinding = serviceBinding;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (serviceBinding != null) {
            sb.append("serviceBinding:\n");
            for (ServiceBindingType sbi : serviceBinding) {
                sb.append(sbi).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ServiceType && super.equals(obj)) {
            final ServiceType that = (ServiceType) obj;
            return Objects.equals(this.serviceBinding, that.serviceBinding);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + super.hashCode();
        hash = 79 * hash + (this.serviceBinding != null ? this.serviceBinding.hashCode() : 0);
        return hash;
    }

}
