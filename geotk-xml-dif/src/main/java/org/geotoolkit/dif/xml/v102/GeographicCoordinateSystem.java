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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *
 *
 * <p>Classe Java pour GeographicCoordinateSystem complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GeographicCoordinateSystem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GeographicCoordinateUnits" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LatitudeResolution" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="LongitudeResolution" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeographicCoordinateSystem", propOrder = {
    "geographicCoordinateUnits",
    "latitudeResolution",
    "longitudeResolution"
})
public class GeographicCoordinateSystem {

    @XmlElement(name = "GeographicCoordinateUnits")
    protected String geographicCoordinateUnits;
    @XmlElement(name = "LatitudeResolution")
    protected BigDecimal latitudeResolution;
    @XmlElement(name = "LongitudeResolution")
    protected BigDecimal longitudeResolution;

    /**
     * Obtient la valeur de la propriété geographicCoordinateUnits.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGeographicCoordinateUnits() {
        return geographicCoordinateUnits;
    }

    /**
     * Définit la valeur de la propriété geographicCoordinateUnits.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGeographicCoordinateUnits(String value) {
        this.geographicCoordinateUnits = value;
    }

    /**
     * Obtient la valeur de la propriété latitudeResolution.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getLatitudeResolution() {
        return latitudeResolution;
    }

    /**
     * Définit la valeur de la propriété latitudeResolution.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setLatitudeResolution(BigDecimal value) {
        this.latitudeResolution = value;
    }

    /**
     * Obtient la valeur de la propriété longitudeResolution.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getLongitudeResolution() {
        return longitudeResolution;
    }

    /**
     * Définit la valeur de la propriété longitudeResolution.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setLongitudeResolution(BigDecimal value) {
        this.longitudeResolution = value;
    }

}
