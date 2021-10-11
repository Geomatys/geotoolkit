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
package org.geotoolkit.kml.xml.v220;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LookAtType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LookAtType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractViewType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}longitude" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}latitude" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}altitude" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}heading" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}tilt" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}range" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}altitudeModeGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LookAtSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LookAtObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LookAtType", propOrder = {
    "longitude",
    "latitude",
    "altitude",
    "heading",
    "tilt",
    "range",
    "altitudeModeGroup",
    "lookAtSimpleExtensionGroup",
    "lookAtObjectExtensionGroup"
})
public class LookAtType
    extends AbstractViewType
{

    @XmlElement(defaultValue = "0.0")
    private Double longitude;
    @XmlElement(defaultValue = "0.0")
    private Double latitude;
    @XmlElement(defaultValue = "0.0")
    private Double altitude;
    @XmlElement(defaultValue = "0.0")
    private Double heading;
    @XmlElement(defaultValue = "0.0")
    private Double tilt;
    @XmlElement(defaultValue = "0.0")
    private Double range;
    @XmlElementRef(name = "altitudeModeGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class)
    private JAXBElement<?> altitudeModeGroup;
    @XmlElement(name = "LookAtSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    private List<Object> lookAtSimpleExtensionGroup;
    @XmlElement(name = "LookAtObjectExtensionGroup")
    private List<AbstractObjectType> lookAtObjectExtensionGroup;

    /**
     * Gets the value of the longitude property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setLongitude(final Double value) {
        this.longitude = value;
    }

    /**
     * Gets the value of the latitude property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setLatitude(final Double value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the altitude property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getAltitude() {
        return altitude;
    }

    /**
     * Sets the value of the altitude property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setAltitude(final Double value) {
        this.altitude = value;
    }

    /**
     * Gets the value of the heading property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getHeading() {
        return heading;
    }

    /**
     * Sets the value of the heading property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setHeading(final Double value) {
        this.heading = value;
    }

    /**
     * Gets the value of the tilt property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getTilt() {
        return tilt;
    }

    /**
     * Sets the value of the tilt property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setTilt(final Double value) {
        this.tilt = value;
    }

    /**
     * Gets the value of the range property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getRange() {
        return range;
    }

    /**
     * Sets the value of the range property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setRange(final Double value) {
        this.range = value;
    }

    /**
     * Gets the value of the altitudeModeGroup property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link AltitudeModeEnumType }{@code >}
     *
     */
    public JAXBElement<?> getAltitudeModeGroup() {
        return altitudeModeGroup;
    }

    /**
     * Sets the value of the altitudeModeGroup property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link AltitudeModeEnumType }{@code >}
     *
     */
    public void setAltitudeModeGroup(final JAXBElement<?> value) {
        this.altitudeModeGroup = ((JAXBElement<?> ) value);
    }

    /**
     * Gets the value of the lookAtSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lookAtSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLookAtSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getLookAtSimpleExtensionGroup() {
        if (lookAtSimpleExtensionGroup == null) {
            lookAtSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.lookAtSimpleExtensionGroup;
    }

    /**
     * Gets the value of the lookAtObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lookAtObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLookAtObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getLookAtObjectExtensionGroup() {
        if (lookAtObjectExtensionGroup == null) {
            lookAtObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.lookAtObjectExtensionGroup;
    }

}
