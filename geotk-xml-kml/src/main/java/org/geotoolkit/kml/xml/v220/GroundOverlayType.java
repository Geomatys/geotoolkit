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
 * <p>Java class for GroundOverlayType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GroundOverlayType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractOverlayType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}altitude" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}altitudeModeGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LatLonBox" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}GroundOverlaySimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}GroundOverlayObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "GroundOverlayType", propOrder = {
    "altitude",
    "altitudeModeGroup",
    "latLonBox",
    "groundOverlaySimpleExtensionGroup",
    "groundOverlayObjectExtensionGroup"
})
public class GroundOverlayType extends AbstractOverlayType {

    @XmlElement(defaultValue = "0.0")
    private Double altitude;
    @XmlElementRef(name = "altitudeModeGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class)
    private JAXBElement<?> altitudeModeGroup;
    @XmlElement(name = "LatLonBox")
    private LatLonBoxType latLonBox;
    @XmlElement(name = "GroundOverlaySimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    private List<Object> groundOverlaySimpleExtensionGroup;
    @XmlElement(name = "GroundOverlayObjectExtensionGroup")
    private List<AbstractObjectType> groundOverlayObjectExtensionGroup;

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
     * Gets the value of the latLonBox property.
     *
     * @return
     *     possible object is
     *     {@link LatLonBoxType }
     *
     */
    public LatLonBoxType getLatLonBox() {
        return latLonBox;
    }

    /**
     * Sets the value of the latLonBox property.
     *
     * @param value
     *     allowed object is
     *     {@link LatLonBoxType }
     *
     */
    public void setLatLonBox(final LatLonBoxType value) {
        this.latLonBox = value;
    }

    /**
     * Gets the value of the groundOverlaySimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groundOverlaySimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroundOverlaySimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getGroundOverlaySimpleExtensionGroup() {
        if (groundOverlaySimpleExtensionGroup == null) {
            groundOverlaySimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.groundOverlaySimpleExtensionGroup;
    }

    /**
     * Gets the value of the groundOverlayObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groundOverlayObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroundOverlayObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getGroundOverlayObjectExtensionGroup() {
        if (groundOverlayObjectExtensionGroup == null) {
            groundOverlayObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.groundOverlayObjectExtensionGroup;
    }

}
