/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

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
 *         &lt;element name="ProviderSite" type="{http://www.opengis.net/ows/2.0}OnlineResourceType" minOccurs="0"/>
 *         &lt;element name="ServiceContact" type="{http://www.opengis.net/ows/2.0}ResponsiblePartySubsetType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
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
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getProviderName() {
        return providerName;
    }

    /**
     * Sets the value of the providerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProviderName(String value) {
        this.providerName = value;
    }

    /**
     * Gets the value of the providerSite property.
     * 
     * @return
     *     possible object is
     *     {@link OnlineResourceType }
     *     
     */
    @Override
    public OnlineResourceType getProviderSite() {
        return providerSite;
    }

    /**
     * Sets the value of the providerSite property.
     * 
     * @param value
     *     allowed object is
     *     {@link OnlineResourceType }
     *     
     */
    public void setProviderSite(OnlineResourceType value) {
        this.providerSite = value;
    }

    /**
     * Gets the value of the serviceContact property.
     * 
     * @return
     *     possible object is
     *     {@link ResponsiblePartySubsetType }
     *     
     */
    @Override
    public ResponsiblePartySubsetType getServiceContact() {
        return serviceContact;
    }

    /**
     * Sets the value of the serviceContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponsiblePartySubsetType }
     *     
     */
    public void setServiceContact(ResponsiblePartySubsetType value) {
        this.serviceContact = value;
    }

}
