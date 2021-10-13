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
package org.geotoolkit.ows.xml.v110;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractServiceProvider;


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
 *         &lt;element name="ProviderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProviderSite" type="{http://www.opengis.net/ows/1.1}OnlineResourceType" minOccurs="0"/>
 *         &lt;element name="ServiceContact" type="{http://www.opengis.net/ows/1.1}ResponsiblePartySubsetType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *  @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "providerName",
    "providerSite",
    "serviceContact"
})
@XmlRootElement(name = "ServiceProvider")
public class ServiceProvider implements AbstractServiceProvider {

    @XmlElement(name = "ProviderName", required = true)
    private String providerName;
    @XmlElement(name = "ProviderSite")
    private OnlineResourceType providerSite;
    @XmlElement(name = "ServiceContact", required = true)
    private ResponsiblePartySubsetType serviceContact;

    /**
     * Empty constructor used by JAXB.
     */
    ServiceProvider(){
    }

    /**
     * Build a new Service provider.
     */
    public ServiceProvider(final String providerName, final OnlineResourceType providerSite, final ResponsiblePartySubsetType serviceContact){
        this.providerName   = providerName;
        this.providerSite   = providerSite;
        this.serviceContact = serviceContact;
    }

    /**
     * Gets the value of the providerName property.
     */
    @Override
    public String getProviderName() {
        return providerName;
    }

    /**
     * Gets the value of the providerSite property.
     */
    @Override
    public OnlineResourceType getProviderSite() {
        return providerSite;
    }

    /**
     * Gets the value of the serviceContact property.
     *
     */
    @Override
    public ResponsiblePartySubsetType getServiceContact() {
        return serviceContact;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ServiceProvider) {
            final ServiceProvider that = (ServiceProvider) object;

            return Objects.equals(this.providerName,   that.providerName) &&
                   Objects.equals(this.providerSite,   that.providerSite) &&
                   Objects.equals(this.serviceContact, that.serviceContact);
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.providerName != null ? this.providerName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("class: ServiceProvider name= ").append(providerName).append('\n');
        if(providerSite != null)
            s.append("providerSite").append(providerSite.toString()).append('\n');
        if (serviceContact != null)
            s.append("ServiceContact").append(serviceContact.toString()).append('\n');
        return s.toString();
    }


}
