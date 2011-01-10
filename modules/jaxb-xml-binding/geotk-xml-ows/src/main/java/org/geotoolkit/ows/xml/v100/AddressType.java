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
package org.geotoolkit.ows.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractAddress;
import org.geotoolkit.util.Utilities;


/**
 * Location of the responsible individual or organization. 
 * 
 * <p>Java class for AddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DeliveryPoint" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdministrativeArea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PostalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ElectronicMailAddress" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddressType", propOrder = {
    "deliveryPoint",
    "city",
    "administrativeArea",
    "postalCode",
    "country",
    "electronicMailAddress"
})
public class AddressType implements AbstractAddress {

    @XmlElement(name = "DeliveryPoint")
    private List<String> deliveryPoint;
    @XmlElement(name = "City")
    private String city;
    @XmlElement(name = "AdministrativeArea")
    private String administrativeArea;
    @XmlElement(name = "PostalCode")
    private String postalCode;
    @XmlElement(name = "Country")
    private String country;
    @XmlElement(name = "ElectronicMailAddress")
    private List<String> electronicMailAddress;

    /**
     * Empty constructor used by JAXB
     */
    AddressType(){
        
    }
    
    /**
     * Build a new Adress.
     */
    public AddressType(final List<String> deliveryPoint, final String city, final String administrativeArea,
            final String postalCode, final String country, final List<String> electronicMailAddress){
        this.administrativeArea    = administrativeArea;
        this.city                  = city;
        this.country               = country;
        this.deliveryPoint         = deliveryPoint;
        this.electronicMailAddress = electronicMailAddress;
        this.postalCode            = postalCode;
    }
    
    /**
     * Build a simple new Adress.
     */
    public AddressType(final String deliveryPoint, final String city, final String administrativeArea,
            final String postalCode, final String country, final String electronicMailAddress){
        this.administrativeArea    = administrativeArea;
        this.city                  = city;
        this.country               = country;
        this.deliveryPoint         = new ArrayList<String>();
        this.deliveryPoint.add(deliveryPoint);
        this.electronicMailAddress = new ArrayList<String>();
        this.electronicMailAddress.add(electronicMailAddress);
        this.postalCode            = postalCode;
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
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the value of the administrativeArea property.
     */
    public String getAdministrativeArea() {
        return administrativeArea;
    }

    /**
     * Gets the value of the postalCode property.
     * 
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Gets the value of the country property.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Gets the value of the electronicMailAddress property.
     */
    public List<String> getElectronicMailAddress() {
        if (electronicMailAddress == null) {
            electronicMailAddress = new ArrayList<String>();
        }
        return this.electronicMailAddress;
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AddressType) {
            final AddressType that = (AddressType) object;
            return Utilities.equals(this.administrativeArea,    that.administrativeArea)    &&
                   Utilities.equals(this.city,                  that.city)                  && 
                   Utilities.equals(this.country,               that.country)               && 
                   Utilities.equals(this.deliveryPoint,         that.deliveryPoint)         &&
                   Utilities.equals(this.electronicMailAddress, that.electronicMailAddress) &&
                   Utilities.equals(this.postalCode,            that.postalCode) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.deliveryPoint != null ? this.deliveryPoint.hashCode() : 0);
        hash = 79 * hash + (this.city != null ? this.city.hashCode() : 0);
        hash = 79 * hash + (this.administrativeArea != null ? this.administrativeArea.hashCode() : 0);
        hash = 79 * hash + (this.postalCode != null ? this.postalCode.hashCode() : 0);
        hash = 79 * hash + (this.country != null ? this.country.hashCode() : 0);
        hash = 79 * hash + (this.electronicMailAddress != null ? this.electronicMailAddress.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("class:AddressType").append('\n');
        s.append("city=").append(city).append(" country=").append(country).append(" postalCode=");
        s.append(postalCode).append(" administrativeArea=").append(administrativeArea).append('\n');
        s.append("deliveryPoint:");
        if (deliveryPoint != null) {
            for (String ss:deliveryPoint) {
                s.append(ss).append('\n');
            }
        }
        s.append("electronicMailAddress:");
        if (electronicMailAddress != null) {
            for (String ss:electronicMailAddress) {
                s.append(ss).append('\n');
            }
        }
        return s.toString();
    }

}
