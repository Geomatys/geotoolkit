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
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Mapping of the same named interface in ebRIM.
 * 
 * <p>Java class for PostalAddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PostalAddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="city" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ShortName" />
 *       &lt;attribute name="country" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ShortName" />
 *       &lt;attribute name="postalCode" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ShortName" />
 *       &lt;attribute name="stateOrProvince" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ShortName" />
 *       &lt;attribute name="street" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ShortName" />
 *       &lt;attribute name="streetNumber" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}String32" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostalAddressType")
public class PostalAddressType {

    @XmlAttribute
    private String city;
    @XmlAttribute
    private String country;
    @XmlAttribute
    private String postalCode;
    @XmlAttribute
    private String stateOrProvince;
    @XmlAttribute
    private String street;
    @XmlAttribute
    private String streetNumber;

    /**
     * Gets the value of the city property.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     */
    public void setCity(final String value) {
        this.city = value;
    }

    /**
     * Gets the value of the country property.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     */
    public void setCountry(final String value) {
        this.country = value;
    }

    /**
     * Gets the value of the postalCode property.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     */
    public void setPostalCode(final String value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the stateOrProvince property.
     */
    public String getStateOrProvince() {
        return stateOrProvince;
    }

    /**
     * Sets the value of the stateOrProvince property.
     */
    public void setStateOrProvince(final String value) {
        this.stateOrProvince = value;
    }

    /**
     * Gets the value of the street property.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the value of the street property.
     */
    public void setStreet(final String value) {
        this.street = value;
    }

    /**
     * Gets the value of the streetNumber property.
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * Sets the value of the streetNumber property.
     */
    public void setStreetNumber(final String value) {
        this.streetNumber = value;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append('[').append(this.getClass().getSimpleName()).append(']').append('\n');
        if (city != null) {
            s.append("city:").append(city).append('\n');
        }
        if (country != null) {
            s.append("country:").append(country).append('\n');
        }
        if (postalCode != null) {
            s.append("postalCode:").append(postalCode).append('\n');
        }
        if (stateOrProvince != null) {
            s.append("stateOrProvince:").append(stateOrProvince).append('\n');
        }
        if (street != null) {
            s.append("street:").append(street).append('\n');
        }
        if (streetNumber != null) {
            s.append("streetNumber:").append(streetNumber).append('\n');
        }
        return s.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PostalAddressType) {
            final PostalAddressType that = (PostalAddressType) obj;
            return Utilities.equals(this.city,             that.city) &&
                    Utilities.equals(this.country,         that.country) &&
                    Utilities.equals(this.postalCode,      that.postalCode) &&
                    Utilities.equals(this.stateOrProvince, that.stateOrProvince) &&
                    Utilities.equals(this.street,          that.street) &&
                   Utilities.equals(this.streetNumber,     that.streetNumber);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.city != null ? this.city.hashCode() : 0);
        hash = 37 * hash + (this.country != null ? this.country.hashCode() : 0);
        hash = 37 * hash + (this.postalCode != null ? this.postalCode.hashCode() : 0);
        hash = 37 * hash + (this.stateOrProvince != null ? this.stateOrProvince.hashCode() : 0);
        hash = 37 * hash + (this.street != null ? this.street.hashCode() : 0);
        hash = 37 * hash + (this.streetNumber != null ? this.streetNumber.hashCode() : 0);
        return hash;
    }
}
