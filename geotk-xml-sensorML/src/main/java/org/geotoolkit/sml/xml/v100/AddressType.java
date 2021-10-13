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
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractAddress;

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
 *         &lt;element name="deliveryPoint" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="administrativeArea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="postalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="electronicMailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "deliveryPoint",
    "city",
    "administrativeArea",
    "postalCode",
    "country",
    "electronicMailAddress"
})
public class AddressType implements AbstractAddress {

    private List<String> deliveryPoint;
    private String city;
    private String administrativeArea;
    private String postalCode;
    private String country;
    private String electronicMailAddress;

    public AddressType() {
    }

    public AddressType(final String deliveryPoint, final String city, final String administrativeArea, final String postalCode, final String country,
            final String electronicMailAddress) {
        this.administrativeArea = administrativeArea;
        this.city = city;
        this.country = country;
        this.deliveryPoint = new ArrayList<String>();
        if (deliveryPoint != null) {
            this.deliveryPoint.add(deliveryPoint);
        }
        this.electronicMailAddress = electronicMailAddress;
        this.postalCode = postalCode;

    }

     public AddressType(final AbstractAddress ad) {
        if (ad != null) {
            this.administrativeArea    = ad.getAdministrativeArea();
            this.city                  = ad.getCity();
            this.country               = ad.getCountry();
            this.deliveryPoint         = ad.getDeliveryPoint();
            this.electronicMailAddress = ad.getElectronicMailAddress();
            this.postalCode            = ad.getPostalCode();
        }
    }

    /**
     * Gets the value of the deliveryPoint property.
     *
     */
    public List<String> getDeliveryPoint() {
        if (deliveryPoint == null) {
            deliveryPoint = new ArrayList<String>();
        }
        return this.deliveryPoint;
    }

    /**
     * Gets the value of the deliveryPoint property.
     *
     */
    public void setDeliveryPoint(final String deliveryPoint) {
        if (this.deliveryPoint == null) {
            this.deliveryPoint = new ArrayList<String>();
        }
        this.deliveryPoint.add(deliveryPoint);
    }

    /**
     * Gets the value of the deliveryPoint property.
     *
     */
    public void setDeliveryPoint(final List<String> deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }
    /**
     * Gets the value of the city property.
     *
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
     * Gets the value of the administrativeArea property.
     */
    public String getAdministrativeArea() {
        return administrativeArea;
    }

    /**
     * Sets the value of the administrativeArea property.
     *
     */
    public void setAdministrativeArea(final String value) {
        this.administrativeArea = value;
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
     * Gets the value of the electronicMailAddress property.
     */
    public String getElectronicMailAddress() {
        return electronicMailAddress;
    }

    /**
     * Sets the value of the electronicMailAddress property.
     */
    public void setElectronicMailAddress(final String value) {
        this.electronicMailAddress = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Address]").append("\n");
        if (city != null) {
            sb.append("city: ").append(city).append('\n');
        }
        if (administrativeArea != null) {
            sb.append("administrativeArea: ").append(administrativeArea).append('\n');
        }
        if (postalCode != null) {
            sb.append("postalCode: ").append(postalCode).append('\n');
        }
        if (country != null) {
            sb.append("country: ").append(country).append('\n');
        }
        if (electronicMailAddress != null) {
            sb.append("electronicMailAddress: ").append(electronicMailAddress).append('\n');
        }
        if (deliveryPoint != null) {
            for (String d : deliveryPoint) {
                sb.append("deliveryPoint: ").append(d).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof AddressType) {
            final AddressType that = (AddressType) object;
            return Objects.equals(this.administrativeArea, that.administrativeArea) &&
                    Objects.equals(this.city, that.city) &&
                    Objects.equals(this.country, that.country) &&
                    Objects.equals(this.deliveryPoint, that.deliveryPoint) &&
                    Objects.equals(this.postalCode, that.postalCode) &&
                    Objects.equals(this.electronicMailAddress, that.electronicMailAddress);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.deliveryPoint != null ? this.deliveryPoint.hashCode() : 0);
        hash = 83 * hash + (this.city != null ? this.city.hashCode() : 0);
        hash = 83 * hash + (this.administrativeArea != null ? this.administrativeArea.hashCode() : 0);
        hash = 83 * hash + (this.postalCode != null ? this.postalCode.hashCode() : 0);
        hash = 83 * hash + (this.country != null ? this.country.hashCode() : 0);
        hash = 83 * hash + (this.electronicMailAddress != null ? this.electronicMailAddress.hashCode() : 0);
        return hash;
    }
}
