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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines a location to be visited along a route.
 * 
 * <p>Java class for WayPointType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WayPointType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractWayPointType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}_Location"/>
 *         &lt;element ref="{http://www.opengis.net/xls}GeocodeMatchCode" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="stop" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WayPointType", propOrder = {
    "location",
    "geocodeMatchCode"
})
public class WayPointType extends AbstractWayPointType {

    @XmlElementRef(name = "_Location", namespace = "http://www.opengis.net/xls", type = JAXBElement.class)
    private JAXBElement<? extends AbstractLocationType> location;
    @XmlElement(name = "GeocodeMatchCode")
    private GeocodingQOSType geocodeMatchCode;
    @XmlAttribute
    private Boolean stop;

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
     * Gets the value of the geocodeMatchCode property.
     * 
     * @return
     *     possible object is
     *     {@link GeocodingQOSType }
     *     
     */
    public GeocodingQOSType getGeocodeMatchCode() {
        return geocodeMatchCode;
    }

    /**
     * Sets the value of the geocodeMatchCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeocodingQOSType }
     *     
     */
    public void setGeocodeMatchCode(GeocodingQOSType value) {
        this.geocodeMatchCode = value;
    }

    /**
     * Gets the value of the stop property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isStop() {
        if (stop == null) {
            return true;
        } else {
            return stop;
        }
    }

    /**
     * Sets the value of the stop property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setStop(Boolean value) {
        this.stop = value;
    }

}
