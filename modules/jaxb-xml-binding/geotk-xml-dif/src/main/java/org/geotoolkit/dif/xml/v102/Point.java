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

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *
 *
 * <p>Classe Java pour Point complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="Point">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Point_Longitude" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}Longitude"/>
 *         &lt;element name="Point_Latitude" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}Latitude"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Point", propOrder = {
    "pointLongitude",
    "pointLatitude"
})
public class Point {

    @XmlElement(name = "Point_Longitude", required = true)
    protected BigDecimal pointLongitude;
    @XmlElement(name = "Point_Latitude", required = true)
    protected BigDecimal pointLatitude;

    /**
     * Obtient la valeur de la propriété pointLongitude.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getPointLongitude() {
        return pointLongitude;
    }

    /**
     * Définit la valeur de la propriété pointLongitude.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setPointLongitude(BigDecimal value) {
        this.pointLongitude = value;
    }

    /**
     * Obtient la valeur de la propriété pointLatitude.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getPointLatitude() {
        return pointLatitude;
    }

    /**
     * Définit la valeur de la propriété pointLatitude.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setPointLatitude(BigDecimal value) {
        this.pointLatitude = value;
    }

}
