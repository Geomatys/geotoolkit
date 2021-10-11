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
package org.geotoolkit.wcs.xml.v100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractAddress;


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
 *         &lt;element name="deliveryPoint" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="administrativeArea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="postalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="electronicMailAddress" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "AddressType", propOrder = {
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
        this.deliveryPoint         = new ArrayList<>();
        this.deliveryPoint.add(deliveryPoint);
        this.electronicMailAddress = new ArrayList<>();
        this.electronicMailAddress.add(electronicMailAddress);
        this.postalCode            = postalCode;
    }

    /**
     * Gets the value of the deliveryPoint property.
     * (unmodifiable)
     */
    @Override
    public List<String> getDeliveryPoint() {
        if (deliveryPoint == null) {
            deliveryPoint = new ArrayList<>();
        }
        return Collections.unmodifiableList(deliveryPoint);
    }

    /**
     * Gets the value of the city property.
     */
    @Override
    public String getCity() {
        return city;
    }

    /**
     * Gets the value of the administrativeArea property.
     */
    @Override
    public String getAdministrativeArea() {
        return administrativeArea;
    }

    /**
     * Gets the value of the postalCode property.
     */
    @Override
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Gets the value of the country property.
     */
    @Override
    public String getCountry() {
        return country;
    }

    /**
     * Gets the value of the electronicMailAddress property.
     * (unmodifiable)
     */
    @Override
    public List<String> getElectronicMailAddress() {
        if (electronicMailAddress == null) {
            electronicMailAddress = new ArrayList<>();
        }
        return Collections.unmodifiableList(electronicMailAddress);
    }

}
