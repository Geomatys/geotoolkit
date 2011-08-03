/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * A set of precise and complete data elements that cannot be subdivided and that describe the physical location of a place.
 * 
 * <p>Java class for StreetAddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StreetAddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}_StreetLocation" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/xls}Street" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="locator" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StreetAddressType", propOrder = {
    "streetLocation",
    "street"
})
public class StreetAddressType {

    @XmlElementRef(name = "_StreetLocation", namespace = "http://www.opengis.net/xls", type = JAXBElement.class)
    private JAXBElement<? extends AbstractStreetLocatorType> streetLocation;
    @XmlElement(name = "Street", required = true)
    private List<StreetNameType> street;
    @XmlAttribute
    @XmlSchemaType(name = "anySimpleType")
    private String locator;

    /**
     * Gets the value of the streetLocation property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractStreetLocatorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BuildingLocatorType }{@code >}
     *     
     */
    public JAXBElement<? extends AbstractStreetLocatorType> getStreetLocation() {
        return streetLocation;
    }

    /**
     * Sets the value of the streetLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractStreetLocatorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BuildingLocatorType }{@code >}
     *     
     */
    public void setStreetLocation(JAXBElement<? extends AbstractStreetLocatorType> value) {
        this.streetLocation = ((JAXBElement<? extends AbstractStreetLocatorType> ) value);
    }

    /**
     * Gets the value of the street property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the street property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStreet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StreetNameType }
     * 
     * 
     */
    public List<StreetNameType> getStreet() {
        if (street == null) {
            street = new ArrayList<StreetNameType>();
        }
        return this.street;
    }

    /**
     * Gets the value of the locator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocator() {
        return locator;
    }

    /**
     * Sets the value of the locator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocator(String value) {
        this.locator = value;
    }

}
