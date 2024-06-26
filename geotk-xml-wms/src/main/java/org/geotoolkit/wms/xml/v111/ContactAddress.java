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
package org.geotoolkit.wms.xml.v111;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractContactAddress;


/**
 * <p>Java class for anonymous complex type.
 *
 *
 *  @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "addressType",
    "address",
    "city",
    "stateOrProvince",
    "postCode",
    "country"
})
@XmlRootElement(name = "ContactAddress")
public class ContactAddress implements AbstractContactAddress {

    @XmlElement(name = "AddressType", required = true)
    private String addressType;
    @XmlElement(name = "Address", required = true)
    private String address;
    @XmlElement(name = "City", required = true)
    private String city;
    @XmlElement(name = "StateOrProvince", required = true)
    private String stateOrProvince;
    @XmlElement(name = "PostCode", required = true)
    private String postCode;
    @XmlElement(name = "Country", required = true)
    private String country;

    /**
     * An empty constructor used by JAXB.
     */
     ContactAddress() {
     }

    /**
     * Build a new Contact adress object
     */
    public ContactAddress(final String addressType, final String address, final String city,
            final String stateOrProvince, final String postCode, final String country) {
        this.address         = address;
        this.addressType     = addressType;
        this.city            = city;
        this.country         = country;
        this.postCode        = postCode;
        this.stateOrProvince = stateOrProvince;
    }

    /**
     * Gets the value of the addressType property.
     */
    public String getAddressType() {
        return addressType;
    }

    /**
     * Gets the value of the address property.
     */
    public String getAddress() {
        return address;
    }

   /**
     * Gets the value of the city property.
     *
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the value of the stateOrProvince property.
     */
    public String getStateOrProvince() {
        return stateOrProvince;
    }

    /**
     * Gets the value of the postCode property.
     *
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * Gets the value of the country property.
     *
     */
    public String getCountry() {
        return country;
    }
}
