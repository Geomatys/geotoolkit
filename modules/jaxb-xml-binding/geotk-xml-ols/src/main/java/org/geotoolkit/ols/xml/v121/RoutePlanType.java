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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Defines the criteria upon which a route is determined.
 * 
 * <p>Java class for RoutePlanType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RoutePlanType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}RoutePreference"/>
 *         &lt;element ref="{http://www.opengis.net/xls}WayPointList"/>
 *         &lt;element ref="{http://www.opengis.net/xls}AvoidList" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="useRealTimeTraffic" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="expectedStartTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="expectedEndTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoutePlanType", propOrder = {
    "routePreference",
    "wayPointList",
    "avoidList"
})
public class RoutePlanType {

    @XmlElement(name = "RoutePreference", required = true)
    private RoutePreferenceType routePreference;
    @XmlElement(name = "WayPointList", required = true)
    private WayPointListType wayPointList;
    @XmlElement(name = "AvoidList")
    private AvoidListType avoidList;
    @XmlAttribute
    private Boolean useRealTimeTraffic;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar expectedStartTime;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar expectedEndTime;

    /**
     * Gets the value of the routePreference property.
     * 
     * @return
     *     possible object is
     *     {@link RoutePreferenceType }
     *     
     */
    public RoutePreferenceType getRoutePreference() {
        return routePreference;
    }

    /**
     * Sets the value of the routePreference property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoutePreferenceType }
     *     
     */
    public void setRoutePreference(RoutePreferenceType value) {
        this.routePreference = value;
    }

    /**
     * Gets the value of the wayPointList property.
     * 
     * @return
     *     possible object is
     *     {@link WayPointListType }
     *     
     */
    public WayPointListType getWayPointList() {
        return wayPointList;
    }

    /**
     * Sets the value of the wayPointList property.
     * 
     * @param value
     *     allowed object is
     *     {@link WayPointListType }
     *     
     */
    public void setWayPointList(WayPointListType value) {
        this.wayPointList = value;
    }

    /**
     * Gets the value of the avoidList property.
     * 
     * @return
     *     possible object is
     *     {@link AvoidListType }
     *     
     */
    public AvoidListType getAvoidList() {
        return avoidList;
    }

    /**
     * Sets the value of the avoidList property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvoidListType }
     *     
     */
    public void setAvoidList(AvoidListType value) {
        this.avoidList = value;
    }

    /**
     * Gets the value of the useRealTimeTraffic property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isUseRealTimeTraffic() {
        if (useRealTimeTraffic == null) {
            return false;
        } else {
            return useRealTimeTraffic;
        }
    }

    /**
     * Sets the value of the useRealTimeTraffic property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseRealTimeTraffic(Boolean value) {
        this.useRealTimeTraffic = value;
    }

    /**
     * Gets the value of the expectedStartTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpectedStartTime() {
        return expectedStartTime;
    }

    /**
     * Sets the value of the expectedStartTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpectedStartTime(XMLGregorianCalendar value) {
        this.expectedStartTime = value;
    }

    /**
     * Gets the value of the expectedEndTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpectedEndTime() {
        return expectedEndTime;
    }

    /**
     * Sets the value of the expectedEndTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpectedEndTime(XMLGregorianCalendar value) {
        this.expectedEndTime = value;
    }

}
