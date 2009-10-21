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
 * <p>Java class for ViewVolumeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ViewVolumeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}leftFov" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}rightFov" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}bottomFov" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}topFov" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}near" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ViewVolumeSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ViewVolumeObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ViewVolumeType", propOrder = {
    "leftFov",
    "rightFov",
    "bottomFov",
    "topFov",
    "near",
    "viewVolumeSimpleExtensionGroup",
    "viewVolumeObjectExtensionGroup"
})
public class ViewVolumeType extends AbstractObjectType {

    @XmlElement(defaultValue = "0.0")
    private Double leftFov;
    @XmlElement(defaultValue = "0.0")
    private Double rightFov;
    @XmlElement(defaultValue = "0.0")
    private Double bottomFov;
    @XmlElement(defaultValue = "0.0")
    private Double topFov;
    @XmlElement(defaultValue = "0.0")
    private Double near;
    @XmlElement(name = "ViewVolumeSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    private List<Object> viewVolumeSimpleExtensionGroup;
    @XmlElement(name = "ViewVolumeObjectExtensionGroup")
    private List<AbstractObjectType> viewVolumeObjectExtensionGroup;

    /**
     * Gets the value of the leftFov property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getLeftFov() {
        return leftFov;
    }

    /**
     * Sets the value of the leftFov property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setLeftFov(Double value) {
        this.leftFov = value;
    }

    /**
     * Gets the value of the rightFov property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRightFov() {
        return rightFov;
    }

    /**
     * Sets the value of the rightFov property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRightFov(Double value) {
        this.rightFov = value;
    }

    /**
     * Gets the value of the bottomFov property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBottomFov() {
        return bottomFov;
    }

    /**
     * Sets the value of the bottomFov property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBottomFov(Double value) {
        this.bottomFov = value;
    }

    /**
     * Gets the value of the topFov property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getTopFov() {
        return topFov;
    }

    /**
     * Sets the value of the topFov property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setTopFov(Double value) {
        this.topFov = value;
    }

    /**
     * Gets the value of the near property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getNear() {
        return near;
    }

    /**
     * Sets the value of the near property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setNear(Double value) {
        this.near = value;
    }

    /**
     * Gets the value of the viewVolumeSimpleExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the viewVolumeSimpleExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getViewVolumeSimpleExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getViewVolumeSimpleExtensionGroup() {
        if (viewVolumeSimpleExtensionGroup == null) {
            viewVolumeSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.viewVolumeSimpleExtensionGroup;
    }

    /**
     * Gets the value of the viewVolumeObjectExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the viewVolumeObjectExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getViewVolumeObjectExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     * 
     * 
     */
    public List<AbstractObjectType> getViewVolumeObjectExtensionGroup() {
        if (viewVolumeObjectExtensionGroup == null) {
            viewVolumeObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.viewVolumeObjectExtensionGroup;
    }

}
