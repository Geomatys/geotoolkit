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
 * <p>Java class for LodType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LodType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}minLodPixels" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}maxLodPixels" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}minFadeExtent" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}maxFadeExtent" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LodSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LodObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LodType", propOrder = {
    "minLodPixels",
    "maxLodPixels",
    "minFadeExtent",
    "maxFadeExtent",
    "lodSimpleExtensionGroup",
    "lodObjectExtensionGroup"
})
public class LodType extends AbstractObjectType {

    @XmlElement(defaultValue = "0.0")
    private Double minLodPixels;
    @XmlElement(defaultValue = "-1.0")
    private Double maxLodPixels;
    @XmlElement(defaultValue = "0.0")
    private Double minFadeExtent;
    @XmlElement(defaultValue = "0.0")
    private Double maxFadeExtent;
    @XmlElement(name = "LodSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    private List<Object> lodSimpleExtensionGroup;
    @XmlElement(name = "LodObjectExtensionGroup")
    private List<AbstractObjectType> lodObjectExtensionGroup;

    /**
     * Gets the value of the minLodPixels property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMinLodPixels() {
        return minLodPixels;
    }

    /**
     * Sets the value of the minLodPixels property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMinLodPixels(Double value) {
        this.minLodPixels = value;
    }

    /**
     * Gets the value of the maxLodPixels property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMaxLodPixels() {
        return maxLodPixels;
    }

    /**
     * Sets the value of the maxLodPixels property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMaxLodPixels(Double value) {
        this.maxLodPixels = value;
    }

    /**
     * Gets the value of the minFadeExtent property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMinFadeExtent() {
        return minFadeExtent;
    }

    /**
     * Sets the value of the minFadeExtent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMinFadeExtent(Double value) {
        this.minFadeExtent = value;
    }

    /**
     * Gets the value of the maxFadeExtent property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMaxFadeExtent() {
        return maxFadeExtent;
    }

    /**
     * Sets the value of the maxFadeExtent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMaxFadeExtent(Double value) {
        this.maxFadeExtent = value;
    }

    /**
     * Gets the value of the lodSimpleExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lodSimpleExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLodSimpleExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getLodSimpleExtensionGroup() {
        if (lodSimpleExtensionGroup == null) {
            lodSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.lodSimpleExtensionGroup;
    }

    /**
     * Gets the value of the lodObjectExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lodObjectExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLodObjectExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     * 
     * 
     */
    public List<AbstractObjectType> getLodObjectExtensionGroup() {
        if (lodObjectExtensionGroup == null) {
            lodObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.lodObjectExtensionGroup;
    }

}
