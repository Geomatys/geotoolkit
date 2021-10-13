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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrientationType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OrientationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}heading" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}tilt" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}roll" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}OrientationSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}OrientationObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "OrientationType", propOrder = {
    "heading",
    "tilt",
    "roll",
    "orientationSimpleExtensionGroup",
    "orientationObjectExtensionGroup"
})
public class OrientationType
    extends AbstractObjectType
{

    @XmlElement(defaultValue = "0.0")
    private Double heading;
    @XmlElement(defaultValue = "0.0")
    private Double tilt;
    @XmlElement(defaultValue = "0.0")
    private Double roll;
    @XmlElement(name = "OrientationSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    private List<Object> orientationSimpleExtensionGroup;
    @XmlElement(name = "OrientationObjectExtensionGroup")
    private List<AbstractObjectType> orientationObjectExtensionGroup;

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
     * Gets the value of the roll property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getRoll() {
        return roll;
    }

    /**
     * Sets the value of the roll property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setRoll(final Double value) {
        this.roll = value;
    }

    /**
     * Gets the value of the orientationSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orientationSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrientationSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getOrientationSimpleExtensionGroup() {
        if (orientationSimpleExtensionGroup == null) {
            orientationSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.orientationSimpleExtensionGroup;
    }

    /**
     * Gets the value of the orientationObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orientationObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrientationObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getOrientationObjectExtensionGroup() {
        if (orientationObjectExtensionGroup == null) {
            orientationObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.orientationObjectExtensionGroup;
    }

}
