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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *                 Added from ECHO 10
 *
 *                 This entity defines the two dimensional coordinate systems for
 *                 the collection. The two dimensional coordinate system
 *                 information is an alternative way to express spatial coverage.
 *                 Granules in the collection that specify two dimensional
 *                 coordinate data must conform to one of the systems defined by
 *                 the collection.
 *
 *                 * TwoD_Coordinate_System_Name : Defines the name of the Two Dimensional coordinate System. Must be unique within a collection.
 *                 * Coordinate1 : Defines the horizontal coordinate.
 *                 * Coordinate2 : Defines the vertical coordinate.
 *
 *
 * <p>Classe Java pour TwoDCoordinateSystem complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="TwoDCoordinateSystem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TwoD_Coordinate_System_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Coordinate1" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}TwoDCoordinate"/>
 *         &lt;element name="Coordinate2" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}TwoDCoordinate"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TwoDCoordinateSystem", propOrder = {
    "twoDCoordinateSystemName",
    "coordinate1",
    "coordinate2"
})
public class TwoDCoordinateSystem {

    @XmlElement(name = "TwoD_Coordinate_System_Name", required = true)
    protected String twoDCoordinateSystemName;
    @XmlElement(name = "Coordinate1", required = true)
    protected TwoDCoordinate coordinate1;
    @XmlElement(name = "Coordinate2", required = true)
    protected TwoDCoordinate coordinate2;

    /**
     * Obtient la valeur de la propriété twoDCoordinateSystemName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTwoDCoordinateSystemName() {
        return twoDCoordinateSystemName;
    }

    /**
     * Définit la valeur de la propriété twoDCoordinateSystemName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTwoDCoordinateSystemName(String value) {
        this.twoDCoordinateSystemName = value;
    }

    /**
     * Obtient la valeur de la propriété coordinate1.
     *
     * @return
     *     possible object is
     *     {@link TwoDCoordinate }
     *
     */
    public TwoDCoordinate getCoordinate1() {
        return coordinate1;
    }

    /**
     * Définit la valeur de la propriété coordinate1.
     *
     * @param value
     *     allowed object is
     *     {@link TwoDCoordinate }
     *
     */
    public void setCoordinate1(TwoDCoordinate value) {
        this.coordinate1 = value;
    }

    /**
     * Obtient la valeur de la propriété coordinate2.
     *
     * @return
     *     possible object is
     *     {@link TwoDCoordinate }
     *
     */
    public TwoDCoordinate getCoordinate2() {
        return coordinate2;
    }

    /**
     * Définit la valeur de la propriété coordinate2.
     *
     * @param value
     *     allowed object is
     *     {@link TwoDCoordinate }
     *
     */
    public void setCoordinate2(TwoDCoordinate value) {
        this.coordinate2 = value;
    }

}
