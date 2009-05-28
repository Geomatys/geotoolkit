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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractLatLonBoxType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractLatLonBoxType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}north" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}south" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}east" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}west" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractLatLonBoxSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractLatLonBoxObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractLatLonBoxType", propOrder = {
    "north",
    "south",
    "east",
    "west",
    "abstractLatLonBoxSimpleExtensionGroup",
    "abstractLatLonBoxObjectExtensionGroup"
})
@XmlSeeAlso({
    LatLonBoxType.class,
    LatLonAltBoxType.class
})
public abstract class AbstractLatLonBoxType
    extends AbstractObjectType
{

    @XmlElement(defaultValue = "180.0")
    private Double north;
    @XmlElement(defaultValue = "-180.0")
    private Double south;
    @XmlElement(defaultValue = "180.0")
    private Double east;
    @XmlElement(defaultValue = "-180.0")
    private Double west;
    @XmlElement(name = "AbstractLatLonBoxSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    private List<Object> abstractLatLonBoxSimpleExtensionGroup;
    @XmlElement(name = "AbstractLatLonBoxObjectExtensionGroup")
    private List<AbstractObjectType> abstractLatLonBoxObjectExtensionGroup;

    /**
     * Gets the value of the north property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getNorth() {
        return north;
    }

    /**
     * Sets the value of the north property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setNorth(Double value) {
        this.north = value;
    }

    /**
     * Gets the value of the south property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSouth() {
        return south;
    }

    /**
     * Sets the value of the south property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSouth(Double value) {
        this.south = value;
    }

    /**
     * Gets the value of the east property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getEast() {
        return east;
    }

    /**
     * Sets the value of the east property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setEast(Double value) {
        this.east = value;
    }

    /**
     * Gets the value of the west property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWest() {
        return west;
    }

    /**
     * Sets the value of the west property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWest(Double value) {
        this.west = value;
    }

    /**
     * Gets the value of the abstractLatLonBoxSimpleExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractLatLonBoxSimpleExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractLatLonBoxSimpleExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAbstractLatLonBoxSimpleExtensionGroup() {
        if (abstractLatLonBoxSimpleExtensionGroup == null) {
            abstractLatLonBoxSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.abstractLatLonBoxSimpleExtensionGroup;
    }

    /**
     * Gets the value of the abstractLatLonBoxObjectExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractLatLonBoxObjectExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractLatLonBoxObjectExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     * 
     * 
     */
    public List<AbstractObjectType> getAbstractLatLonBoxObjectExtensionGroup() {
        if (abstractLatLonBoxObjectExtensionGroup == null) {
            abstractLatLonBoxObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.abstractLatLonBoxObjectExtensionGroup;
    }

}
