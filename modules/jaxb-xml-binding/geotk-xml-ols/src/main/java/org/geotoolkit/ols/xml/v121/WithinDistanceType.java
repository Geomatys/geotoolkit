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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines a spatial filter which selects POIs located within a specified distance from a location.
 * 
 * <p>Java class for WithinDistanceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WithinDistanceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}_Location"/>
 *         &lt;element name="MinimumDistance" type="{http://www.opengis.net/xls}DistanceType" minOccurs="0"/>
 *         &lt;element name="MaximumDistance" type="{http://www.opengis.net/xls}DistanceType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WithinDistanceType", propOrder = {
    "location",
    "minimumDistance",
    "maximumDistance"
})
public class WithinDistanceType {

    @XmlElementRef(name = "_Location", namespace = "http://www.opengis.net/xls", type = JAXBElement.class)
    private JAXBElement<? extends AbstractLocationType> location;
    @XmlElement(name = "MinimumDistance")
    private DistanceType minimumDistance;
    @XmlElement(name = "MaximumDistance")
    private DistanceType maximumDistance;

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractPOIType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AddressType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PositionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractLocationType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PointOfInterestType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractPositionType }{@code >}
     *     
     */
    public JAXBElement<? extends AbstractLocationType> getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractPOIType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AddressType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PositionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractLocationType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PointOfInterestType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractPositionType }{@code >}
     *     
     */
    public void setLocation(JAXBElement<? extends AbstractLocationType> value) {
        this.location = ((JAXBElement<? extends AbstractLocationType> ) value);
    }

    /**
     * Gets the value of the minimumDistance property.
     * 
     * @return
     *     possible object is
     *     {@link DistanceType }
     *     
     */
    public DistanceType getMinimumDistance() {
        return minimumDistance;
    }

    /**
     * Sets the value of the minimumDistance property.
     * 
     * @param value
     *     allowed object is
     *     {@link DistanceType }
     *     
     */
    public void setMinimumDistance(DistanceType value) {
        this.minimumDistance = value;
    }

    /**
     * Gets the value of the maximumDistance property.
     * 
     * @return
     *     possible object is
     *     {@link DistanceType }
     *     
     */
    public DistanceType getMaximumDistance() {
        return maximumDistance;
    }

    /**
     * Sets the value of the maximumDistance property.
     * 
     * @param value
     *     allowed object is
     *     {@link DistanceType }
     *     
     */
    public void setMaximumDistance(DistanceType value) {
        this.maximumDistance = value;
    }

}
