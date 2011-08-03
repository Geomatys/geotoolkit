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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines the type of location constraints to perform search.
 * 
 * <p>Java class for POILocationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="POILocationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/xls}Address"/>
 *         &lt;element name="Nearest" type="{http://www.opengis.net/xls}NearestType"/>
 *         &lt;element name="WithinDistance" type="{http://www.opengis.net/xls}WithinDistanceType"/>
 *         &lt;element name="WithinBoundary" type="{http://www.opengis.net/xls}WithinBoundaryType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "POILocationType", propOrder = {
    "address",
    "nearest",
    "withinDistance",
    "withinBoundary"
})
public class POILocationType {

    @XmlElement(name = "Address")
    private AddressType address;
    @XmlElement(name = "Nearest")
    private NearestType nearest;
    @XmlElement(name = "WithinDistance")
    private WithinDistanceType withinDistance;
    @XmlElement(name = "WithinBoundary")
    private WithinBoundaryType withinBoundary;

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setAddress(AddressType value) {
        this.address = value;
    }

    /**
     * Gets the value of the nearest property.
     * 
     * @return
     *     possible object is
     *     {@link NearestType }
     *     
     */
    public NearestType getNearest() {
        return nearest;
    }

    /**
     * Sets the value of the nearest property.
     * 
     * @param value
     *     allowed object is
     *     {@link NearestType }
     *     
     */
    public void setNearest(NearestType value) {
        this.nearest = value;
    }

    /**
     * Gets the value of the withinDistance property.
     * 
     * @return
     *     possible object is
     *     {@link WithinDistanceType }
     *     
     */
    public WithinDistanceType getWithinDistance() {
        return withinDistance;
    }

    /**
     * Sets the value of the withinDistance property.
     * 
     * @param value
     *     allowed object is
     *     {@link WithinDistanceType }
     *     
     */
    public void setWithinDistance(WithinDistanceType value) {
        this.withinDistance = value;
    }

    /**
     * Gets the value of the withinBoundary property.
     * 
     * @return
     *     possible object is
     *     {@link WithinBoundaryType }
     *     
     */
    public WithinBoundaryType getWithinBoundary() {
        return withinBoundary;
    }

    /**
     * Sets the value of the withinBoundary property.
     * 
     * @param value
     *     allowed object is
     *     {@link WithinBoundaryType }
     *     
     */
    public void setWithinBoundary(WithinBoundaryType value) {
        this.withinBoundary = value;
    }

}
