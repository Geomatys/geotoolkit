/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

package org.geotoolkit.dif.xml.v102;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *
 *                 | DIF 9             | ECHO 10       | UMM           | DIF 10         | Notes   |
 *                 | ----------------- | ------------- | ------------- | -------------- | ------- |
 *                 | Address           | StreetAddress | StreetAddress | Street_Address | Renamed |
 *                 | City              | City          | City          | City           |         |
 *                 | Province_or_State | StateProvince | StateProvince | State_Province | Renamed |
 *                 | Postal_Code       | PostalCode    | PostalCode    | Postal_Code    |         |
 *                 | Country           | Country       | Country       | Country        |         |
 *
 *
 *
 * <p>Classe Java pour AddressType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="AddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Street_Address" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="State_Province" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Postal_Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddressType", propOrder = {
    "streetAddress",
    "city",
    "stateProvince",
    "postalCode",
    "country"
})
public class AddressType {

    @XmlElement(name = "Street_Address")
    protected List<String> streetAddress;
    @XmlElement(name = "City")
    protected String city;
    @XmlElement(name = "State_Province")
    protected String stateProvince;
    @XmlElement(name = "Postal_Code")
    protected String postalCode;
    @XmlElement(name = "Country")
    protected String country;

    public AddressType() {

    }

    public AddressType(String streetAddress, String city, String stateProvince, String postalCode, String country) {
        if (streetAddress != null) {
            this.streetAddress = new ArrayList<>();
            this.streetAddress.add(streetAddress);
        }
        this.city = city;
        this.stateProvince = stateProvince;
        this.postalCode = postalCode;
        this.country = country;
    }

    /**
     * Gets the value of the streetAddress property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the streetAddress property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStreetAddress().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getStreetAddress() {
        if (streetAddress == null) {
            streetAddress = new ArrayList<>();
        }
        return this.streetAddress;
    }

    /**
     * Obtient la valeur de la propriété city.
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
     * Définit la valeur de la propriété city.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Obtient la valeur de la propriété stateProvince.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStateProvince() {
        return stateProvince;
    }

    /**
     * Définit la valeur de la propriété stateProvince.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStateProvince(String value) {
        this.stateProvince = value;
    }

    /**
     * Obtient la valeur de la propriété postalCode.
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
     * Définit la valeur de la propriété postalCode.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    /**
     * Obtient la valeur de la propriété country.
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
     * Définit la valeur de la propriété country.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCountry(String value) {
        this.country = value;
    }

}
