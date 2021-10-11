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
 * <p>Java class for ScreenOverlayType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ScreenOverlayType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractOverlayType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}overlayXY" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}screenXY" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}rotationXY" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}size" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}rotation" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ScreenOverlaySimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ScreenOverlayObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ScreenOverlayType", propOrder = {
    "overlayXY",
    "screenXY",
    "rotationXY",
    "size",
    "rotation",
    "screenOverlaySimpleExtensionGroup",
    "screenOverlayObjectExtensionGroup"
})
public class ScreenOverlayType
    extends AbstractOverlayType
{

    private Vec2Type overlayXY;
    private Vec2Type screenXY;
    private Vec2Type rotationXY;
    private Vec2Type size;
    @XmlElement(defaultValue = "0.0")
    private Double rotation;
    @XmlElement(name = "ScreenOverlaySimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    private List<Object> screenOverlaySimpleExtensionGroup;
    @XmlElement(name = "ScreenOverlayObjectExtensionGroup")
    private List<AbstractObjectType> screenOverlayObjectExtensionGroup;

    /**
     * Gets the value of the overlayXY property.
     *
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *
     */
    public Vec2Type getOverlayXY() {
        return overlayXY;
    }

    /**
     * Sets the value of the overlayXY property.
     *
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *
     */
    public void setOverlayXY(final Vec2Type value) {
        this.overlayXY = value;
    }

    /**
     * Gets the value of the screenXY property.
     *
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *
     */
    public Vec2Type getScreenXY() {
        return screenXY;
    }

    /**
     * Sets the value of the screenXY property.
     *
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *
     */
    public void setScreenXY(final Vec2Type value) {
        this.screenXY = value;
    }

    /**
     * Gets the value of the rotationXY property.
     *
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *
     */
    public Vec2Type getRotationXY() {
        return rotationXY;
    }

    /**
     * Sets the value of the rotationXY property.
     *
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *
     */
    public void setRotationXY(final Vec2Type value) {
        this.rotationXY = value;
    }

    /**
     * Gets the value of the size property.
     *
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *
     */
    public Vec2Type getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     *
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *
     */
    public void setSize(final Vec2Type value) {
        this.size = value;
    }

    /**
     * Gets the value of the rotation property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getRotation() {
        return rotation;
    }

    /**
     * Sets the value of the rotation property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setRotation(final Double value) {
        this.rotation = value;
    }

    /**
     * Gets the value of the screenOverlaySimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the screenOverlaySimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getScreenOverlaySimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getScreenOverlaySimpleExtensionGroup() {
        if (screenOverlaySimpleExtensionGroup == null) {
            screenOverlaySimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.screenOverlaySimpleExtensionGroup;
    }

    /**
     * Gets the value of the screenOverlayObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the screenOverlayObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getScreenOverlayObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getScreenOverlayObjectExtensionGroup() {
        if (screenOverlayObjectExtensionGroup == null) {
            screenOverlayObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.screenOverlayObjectExtensionGroup;
    }

}
