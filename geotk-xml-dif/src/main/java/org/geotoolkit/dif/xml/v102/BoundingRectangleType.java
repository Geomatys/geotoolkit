/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

package org.geotoolkit.dif.xml.v102;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *                 Latitude and Longitude points are moved here from the
 *                 Spatial_Coverage field under DIF 9.
 *
 *                 | DIF 9                 | ECHO 10                 | UMM                     | DIF 10                | Notes                        |
 *                 | --------------------- | ----------------------- | ----------------------- | --------------------- | ---------------------------- |
 *                 |           -           | CenterPoint             | CenterPoint             | Center_Point          | Added to match ECHO10 field  |
 *                 | Southernmost_Latitude | SouthBoundingCoordinate | SouthBoundingCoordinate | Southernmost_Latitude | Renamed                      |
 *                 | Northernmost_Latitude | NorthBoundingCoordinate | NorthBoundingCoordinate | Northernmost_Latitude | Renamed                      |
 *                 | Westernmost_Longitude | WestBoundingCoordinate  | WestBoundingCoordinate  | Westernmost_Longitude | Renamed                      |
 *                 | Easternmost_Longitude | EastBoundingCoordinate  | EastBoundingCoordinate  | Easternmost_Longitude | Renamed                      |
 *
 *                 | Minimum_Altitude      |              -          | Minimum_Altitude        | Minimum_Altitude      | Note: should be a number from now on, enforce later                   |
 *                 | Maximum_Altitude      |              -          | Maximum_Altitude        | Maximum_Altitude      | Note: should be a number from now on, enforce later                   |
 *                 |           -           |              -          | Altitude_Unit           | Altitude_Unit         | Added because ECHO uses units in the dropped VerticalSystemDefinition |
 *                 | Minimum_Depth         |              -          | Minimum_Depth           | Minimum_Depth         | Note: should be a number from now on, enforce later                   |
 *                 | Maximum_Depth         |              -          | Maximum_Depth           | Maximum_Depth         | Note: should be a number from now on, enforce later                   |
 *                 |         -             |              -          | Depth_Unit              | Depth_Unit            | Added because ECHO uses units in the dropped VerticalSystemDefinition |
 *
 *
 * <p>Classe Java pour BoundingRectangleType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="BoundingRectangleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Center_Point" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}Point" minOccurs="0"/>
 *         &lt;element name="Southernmost_Latitude" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Northernmost_Latitude" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Westernmost_Longitude" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Easternmost_Longitude" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Minimum_Altitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Maximum_Altitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Altitude_Unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Minimum_Depth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Maximum_Depth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Depth_Unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoundingRectangleType", propOrder = {
    "centerPoint",
    "southernmostLatitude",
    "northernmostLatitude",
    "westernmostLongitude",
    "easternmostLongitude",
    "minimumAltitude",
    "maximumAltitude",
    "altitudeUnit",
    "minimumDepth",
    "maximumDepth",
    "depthUnit"
})
public class BoundingRectangleType {

    @XmlElement(name = "Center_Point")
    protected Point centerPoint;
    @XmlElement(name = "Southernmost_Latitude", required = true)
    protected String southernmostLatitude;
    @XmlElement(name = "Northernmost_Latitude", required = true)
    protected String northernmostLatitude;
    @XmlElement(name = "Westernmost_Longitude", required = true)
    protected String westernmostLongitude;
    @XmlElement(name = "Easternmost_Longitude", required = true)
    protected String easternmostLongitude;
    @XmlElement(name = "Minimum_Altitude")
    protected String minimumAltitude;
    @XmlElement(name = "Maximum_Altitude")
    protected String maximumAltitude;
    @XmlElement(name = "Altitude_Unit")
    protected String altitudeUnit;
    @XmlElement(name = "Minimum_Depth")
    protected String minimumDepth;
    @XmlElement(name = "Maximum_Depth")
    protected String maximumDepth;
    @XmlElement(name = "Depth_Unit")
    protected String depthUnit;

    /**
     * Obtient la valeur de la propriété centerPoint.
     *
     * @return
     *     possible object is
     *     {@link Point }
     *
     */
    public Point getCenterPoint() {
        return centerPoint;
    }

    /**
     * Définit la valeur de la propriété centerPoint.
     *
     * @param value
     *     allowed object is
     *     {@link Point }
     *
     */
    public void setCenterPoint(Point value) {
        this.centerPoint = value;
    }

    /**
     * Obtient la valeur de la propriété southernmostLatitude.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSouthernmostLatitude() {
        return southernmostLatitude;
    }

    /**
     * Définit la valeur de la propriété southernmostLatitude.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSouthernmostLatitude(String value) {
        this.southernmostLatitude = value;
    }

    /**
     * Obtient la valeur de la propriété northernmostLatitude.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNorthernmostLatitude() {
        return northernmostLatitude;
    }

    /**
     * Définit la valeur de la propriété northernmostLatitude.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNorthernmostLatitude(String value) {
        this.northernmostLatitude = value;
    }

    /**
     * Obtient la valeur de la propriété westernmostLongitude.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWesternmostLongitude() {
        return westernmostLongitude;
    }

    /**
     * Définit la valeur de la propriété westernmostLongitude.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWesternmostLongitude(String value) {
        this.westernmostLongitude = value;
    }

    /**
     * Obtient la valeur de la propriété easternmostLongitude.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEasternmostLongitude() {
        return easternmostLongitude;
    }

    /**
     * Définit la valeur de la propriété easternmostLongitude.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEasternmostLongitude(String value) {
        this.easternmostLongitude = value;
    }

    /**
     * Obtient la valeur de la propriété minimumAltitude.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMinimumAltitude() {
        return minimumAltitude;
    }

    /**
     * Définit la valeur de la propriété minimumAltitude.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMinimumAltitude(String value) {
        this.minimumAltitude = value;
    }

    /**
     * Obtient la valeur de la propriété maximumAltitude.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMaximumAltitude() {
        return maximumAltitude;
    }

    /**
     * Définit la valeur de la propriété maximumAltitude.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaximumAltitude(String value) {
        this.maximumAltitude = value;
    }

    /**
     * Obtient la valeur de la propriété altitudeUnit.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAltitudeUnit() {
        return altitudeUnit;
    }

    /**
     * Définit la valeur de la propriété altitudeUnit.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAltitudeUnit(String value) {
        this.altitudeUnit = value;
    }

    /**
     * Obtient la valeur de la propriété minimumDepth.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMinimumDepth() {
        return minimumDepth;
    }

    /**
     * Définit la valeur de la propriété minimumDepth.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMinimumDepth(String value) {
        this.minimumDepth = value;
    }

    /**
     * Obtient la valeur de la propriété maximumDepth.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMaximumDepth() {
        return maximumDepth;
    }

    /**
     * Définit la valeur de la propriété maximumDepth.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaximumDepth(String value) {
        this.maximumDepth = value;
    }

    /**
     * Obtient la valeur de la propriété depthUnit.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDepthUnit() {
        return depthUnit;
    }

    /**
     * Définit la valeur de la propriété depthUnit.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDepthUnit(String value) {
        this.depthUnit = value;
    }

}
