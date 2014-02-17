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
 * Defines the Determine Route response parameters.
 * 
 * <p>Java class for DetermineRouteResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DetermineRouteResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractResponseParametersType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}RouteHandle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/xls}RouteSummary"/>
 *         &lt;element ref="{http://www.opengis.net/xls}RouteGeometry" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/xls}RouteInstructionsList" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/xls}RouteMap" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DetermineRouteResponseType", propOrder = {
    "routeHandle",
    "routeSummary",
    "routeGeometry",
    "routeInstructionsList",
    "routeMap"
})
public class DetermineRouteResponseType extends AbstractResponseParametersType {

    @XmlElement(name = "RouteHandle")
    private RouteHandleType routeHandle;
    @XmlElement(name = "RouteSummary", required = true)
    private RouteSummaryType routeSummary;
    @XmlElement(name = "RouteGeometry")
    private RouteGeometryType routeGeometry;
    @XmlElement(name = "RouteInstructionsList")
    private RouteInstructionsListType routeInstructionsList;
    @XmlElement(name = "RouteMap")
    private List<RouteMapType> routeMap;

    /**
     * Reference to the route stored at the Route Determination Service server.
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
     * Response for requested route summary.
     * 
     * @return
     *     possible object is
     *     {@link RouteSummaryType }
     *     
     */
    public RouteSummaryType getRouteSummary() {
        return routeSummary;
    }

    /**
     * Sets the value of the routeSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link RouteSummaryType }
     *     
     */
    public void setRouteSummary(RouteSummaryType value) {
        this.routeSummary = value;
    }

    /**
     * Response for requested route geometry.
     * 
     * @return
     *     possible object is
     *     {@link RouteGeometryType }
     *     
     */
    public RouteGeometryType getRouteGeometry() {
        return routeGeometry;
    }

    /**
     * Sets the value of the routeGeometry property.
     * 
     * @param value
     *     allowed object is
     *     {@link RouteGeometryType }
     *     
     */
    public void setRouteGeometry(RouteGeometryType value) {
        this.routeGeometry = value;
    }

    /**
     * Response for requested route instructions.
     * 
     * @return
     *     possible object is
     *     {@link RouteInstructionsListType }
     *     
     */
    public RouteInstructionsListType getRouteInstructionsList() {
        return routeInstructionsList;
    }

    /**
     * Sets the value of the routeInstructionsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link RouteInstructionsListType }
     *     
     */
    public void setRouteInstructionsList(RouteInstructionsListType value) {
        this.routeInstructionsList = value;
    }

    /**
     * Response list for requested route maps.Gets the value of the routeMap property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the routeMap property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRouteMap().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RouteMapType }
     * 
     * 
     */
    public List<RouteMapType> getRouteMap() {
        if (routeMap == null) {
            routeMap = new ArrayList<RouteMapType>();
        }
        return this.routeMap;
    }

}
