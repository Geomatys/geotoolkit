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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines a list of waypoints along a route.
 * 
 * <p>Java class for WayPointListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WayPointListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}StartPoint"/>
 *         &lt;element ref="{http://www.opengis.net/xls}ViaPoint" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/xls}EndPoint"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WayPointListType", propOrder = {
    "startPoint",
    "viaPoint",
    "endPoint"
})
public class WayPointListType {

    @XmlElement(name = "StartPoint", required = true)
    private WayPointType startPoint;
    @XmlElement(name = "ViaPoint")
    private List<WayPointType> viaPoint;
    @XmlElement(name = "EndPoint", required = true)
    private WayPointType endPoint;

    /**
     * Gets the value of the startPoint property.
     * 
     * @return
     *     possible object is
     *     {@link WayPointType }
     *     
     */
    public WayPointType getStartPoint() {
        return startPoint;
    }

    /**
     * Sets the value of the startPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link WayPointType }
     *     
     */
    public void setStartPoint(WayPointType value) {
        this.startPoint = value;
    }

    /**
     * Gets the value of the viaPoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the viaPoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getViaPoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WayPointType }
     * 
     * 
     */
    public List<WayPointType> getViaPoint() {
        if (viaPoint == null) {
            viaPoint = new ArrayList<WayPointType>();
        }
        return this.viaPoint;
    }

    /**
     * Gets the value of the endPoint property.
     * 
     * @return
     *     possible object is
     *     {@link WayPointType }
     *     
     */
    public WayPointType getEndPoint() {
        return endPoint;
    }

    /**
     * Sets the value of the endPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link WayPointType }
     *     
     */
    public void setEndPoint(WayPointType value) {
        this.endPoint = value;
    }

}
