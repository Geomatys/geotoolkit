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
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for BalloonStyleType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BalloonStyleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractSubStyleType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/kml/2.2}color" minOccurs="0"/>
 *           &lt;element ref="{http://www.opengis.net/kml/2.2}bgColor" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}textColor" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}text" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}displayMode" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}BalloonStyleSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}BalloonStyleObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "BalloonStyleType", propOrder = {
    "color",
    "bgColor",
    "textColor",
    "text",
    "displayMode",
    "balloonStyleSimpleExtensionGroup",
    "balloonStyleObjectExtensionGroup"
})
public class BalloonStyleType
    extends AbstractSubStyleType
{

    @XmlElement(type = String.class, defaultValue = "ffffffff")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    private byte[] color;
    @XmlElement(type = String.class, defaultValue = "ffffffff")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    private byte[] bgColor;
    @XmlElement(type = String.class, defaultValue = "ff000000")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    private byte[] textColor;
    private String text;
    @XmlElement(defaultValue = "default")
    private DisplayModeEnumType displayMode;
    @XmlElement(name = "BalloonStyleSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    private List<Object> balloonStyleSimpleExtensionGroup;
    @XmlElement(name = "BalloonStyleObjectExtensionGroup")
    private List<AbstractObjectType> balloonStyleObjectExtensionGroup;

    /**
     * Gets the value of the color property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public byte[] getColor() {
        return color;
    }

    /**
     * Sets the value of the color property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColor(final byte[] value) {
        this.color = ((byte[]) value);
    }

    /**
     * Gets the value of the bgColor property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public byte[] getBgColor() {
        return bgColor;
    }

    /**
     * Sets the value of the bgColor property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBgColor(final byte[] value) {
        this.bgColor = ((byte[]) value);
    }

    /**
     * Gets the value of the textColor property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public byte[] getTextColor() {
        return textColor;
    }

    /**
     * Sets the value of the textColor property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTextColor(final byte[] value) {
        this.textColor = ((byte[]) value);
    }

    /**
     * Gets the value of the text property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setText(final String value) {
        this.text = value;
    }

    /**
     * Gets the value of the displayMode property.
     *
     * @return
     *     possible object is
     *     {@link DisplayModeEnumType }
     *
     */
    public DisplayModeEnumType getDisplayMode() {
        return displayMode;
    }

    /**
     * Sets the value of the displayMode property.
     *
     * @param value
     *     allowed object is
     *     {@link DisplayModeEnumType }
     *
     */
    public void setDisplayMode(final DisplayModeEnumType value) {
        this.displayMode = value;
    }

    /**
     * Gets the value of the balloonStyleSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the balloonStyleSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBalloonStyleSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getBalloonStyleSimpleExtensionGroup() {
        if (balloonStyleSimpleExtensionGroup == null) {
            balloonStyleSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.balloonStyleSimpleExtensionGroup;
    }

    /**
     * Gets the value of the balloonStyleObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the balloonStyleObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBalloonStyleObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getBalloonStyleObjectExtensionGroup() {
        if (balloonStyleObjectExtensionGroup == null) {
            balloonStyleObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.balloonStyleObjectExtensionGroup;
    }

}
