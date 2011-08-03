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
import javax.xml.bind.annotation.XmlType;


/**
 * Defines the Determine Route request parameters.
 * 
 * <p>Java class for DetermineRouteRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DetermineRouteRequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractRequestParametersType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/xls}RouteHandle"/>
 *           &lt;element ref="{http://www.opengis.net/xls}RoutePlan"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/xls}RouteInstructionsRequest" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/xls}RouteGeometryRequest" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/xls}RouteMapRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="provideRouteHandle" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="distanceUnit" type="{http://www.opengis.net/xls}DistanceUnitType" default="M" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DetermineRouteRequestType", propOrder = {
    "routeHandle",
    "routePlan",
    "routeInstructionsRequest",
    "routeGeometryRequest",
    "routeMapRequest"
})
public class DetermineRouteRequestType extends AbstractRequestParametersType {

    @XmlElement(name = "RouteHandle")
    private RouteHandleType routeHandle;
    @XmlElement(name = "RoutePlan")
    private RoutePlanType routePlan;
    @XmlElement(name = "RouteInstructionsRequest")
    private RouteInstructionsRequestType routeInstructionsRequest;
    @XmlElement(name = "RouteGeometryRequest")
    private RouteGeometryRequestType routeGeometryRequest;
    @XmlElement(name = "RouteMapRequest")
    private RouteMapRequestType routeMapRequest;
    @XmlAttribute
    private Boolean provideRouteHandle;
    @XmlAttribute
    private DistanceUnitType distanceUnit;

    /**
     * Reference to a proviously determined route stored at the Route Determination Service server.
     * 
     * @return
     *     possible object is
     *     {@link RouteHandleType }
     *     
     */
    public RouteHandleType getRouteHandle() {
        return routeHandle;
    }

    /**
     * Sets the value of the routeHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link RouteHandleType }
     *     
     */
    public void setRouteHandle(RouteHandleType value) {
        this.routeHandle = value;
    }

    /**
     * Gets the value of the routePlan property.
     * 
     * @return
     *     possible object is
     *     {@link RoutePlanType }
     *     
     */
    public RoutePlanType getRoutePlan() {
        return routePlan;
    }

    /**
     * Sets the value of the routePlan property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoutePlanType }
     *     
     */
    public void setRoutePlan(RoutePlanType value) {
        this.routePlan = value;
    }

    /**
     * Request parameters for turn-by-turn route directions and advisories formatted for presentation.
     * 
     * @return
     *     possible object is
     *     {@link RouteInstructionsRequestType }
     *     
     */
    public RouteInstructionsRequestType getRouteInstructionsRequest() {
        return routeInstructionsRequest;
    }

    /**
     * Sets the value of the routeInstructionsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link RouteInstructionsRequestType }
     *     
     */
    public void setRouteInstructionsRequest(RouteInstructionsRequestType value) {
        this.routeInstructionsRequest = value;
    }

    /**
     * Request parameters for route geometry.
     * 
     * @return
     *     possible object is
     *     {@link RouteGeometryRequestType }
     *     
     */
    public RouteGeometryRequestType getRouteGeometryRequest() {
        return routeGeometryRequest;
    }

    /**
     * Sets the value of the routeGeometryRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link RouteGeometryRequestType }
     *     
     */
    public void setRouteGeometryRequest(RouteGeometryRequestType value) {
        this.routeGeometryRequest = value;
    }

    /**
     * Gets the value of the routeMapRequest property.
     * 
     * @return
     *     possible object is
     *     {@link RouteMapRequestType }
     *     
     */
    public RouteMapRequestType getRouteMapRequest() {
        return routeMapRequest;
    }

    /**
     * Sets the value of the routeMapRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link RouteMapRequestType }
     *     
     */
    public void setRouteMapRequest(RouteMapRequestType value) {
        this.routeMapRequest = value;
    }

    /**
     * Gets the value of the provideRouteHandle property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isProvideRouteHandle() {
        if (provideRouteHandle == null) {
            return false;
        } else {
            return provideRouteHandle;
        }
    }

    /**
     * Sets the value of the provideRouteHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setProvideRouteHandle(Boolean value) {
        this.provideRouteHandle = value;
    }

    /**
     * Gets the value of the distanceUnit property.
     * 
     * @return
     *     possible object is
     *     {@link DistanceUnitType }
     *     
     */
    public DistanceUnitType getDistanceUnit() {
        if (distanceUnit == null) {
            return DistanceUnitType.M;
        } else {
            return distanceUnit;
        }
    }

    /**
     * Sets the value of the distanceUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link DistanceUnitType }
     *     
     */
    public void setDistanceUnit(DistanceUnitType value) {
        this.distanceUnit = value;
    }

}
