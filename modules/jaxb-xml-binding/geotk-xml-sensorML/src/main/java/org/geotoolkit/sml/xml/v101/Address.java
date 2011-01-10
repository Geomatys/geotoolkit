/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.sml.xml.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractAddress;
import org.geotoolkit.util.Utilities;

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
public class Address implements AbstractAddress {

    private List<String> deliveryPoint;
    private String city;
    private String administrativeArea;
    private String postalCode;
    private String country;
    private String electronicMailAddress;

    public Address() {
    }

    public Address(final AbstractAddress ad) {
        if (ad != null) {
            this.administrativeArea    = ad.getAdministrativeArea();
            this.city                  = ad.getCity();
            this.country               = ad.getCountry();
            this.deliveryPoint         = ad.getDeliveryPoint();
            this.electronicMailAddress = ad.getElectronicMailAddress();
            this.postalCode            = ad.getPostalCode();
        }
    }

    public Address(final String deliveryPoint, final String city, final String administrativeArea, final String postalCode, final String country,
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
     * Gets the value of the city property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCity(final String value) {
        this.city = value;
    }

    /**
     * Gets the value of the administrativeArea property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAdministrativeArea() {
        return administrativeArea;
    }

    /**
     * Sets the value of the administrativeArea property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAdministrativeArea(final String value) {
        this.administrativeArea = value;
    }

    /**
     * Gets the value of the postalCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPostalCode(final String value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the country property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCountry(final String value) {
        this.country = value;
    }

    /**
     * Gets the value of the electronicMailAddress property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getElectronicMailAddress() {
        return electronicMailAddress;
    }

    /**
     * Sets the value of the electronicMailAddress property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
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

        if (object instanceof Address) {
            final Address that = (Address) object;
            return Utilities.equals(this.administrativeArea, that.administrativeArea)
                    && Utilities.equals(this.city, that.city)
                    && Utilities.equals(this.country, that.country)
                    && Utilities.equals(this.deliveryPoint, that.deliveryPoint)
                    && Utilities.equals(this.postalCode, that.postalCode)
                    && Utilities.equals(this.electronicMailAddress, that.electronicMailAddress);
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

