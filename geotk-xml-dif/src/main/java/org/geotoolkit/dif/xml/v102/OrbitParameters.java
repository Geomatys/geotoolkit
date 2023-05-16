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
 * <p>Classe Java pour OrbitParameters complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="OrbitParameters">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Swath_Width" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Period" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Inclination_Angle" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Number_Of_Orbits" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Start_Circular_Latitude" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrbitParameters", propOrder = {
    "swathWidth",
    "period",
    "inclinationAngle",
    "numberOfOrbits",
    "startCircularLatitude"
})
public class OrbitParameters {

    @XmlElement(name = "Swath_Width", required = true)
    protected BigDecimal swathWidth;
    @XmlElement(name = "Period", required = true)
    protected BigDecimal period;
    @XmlElement(name = "Inclination_Angle", required = true)
    protected BigDecimal inclinationAngle;
    @XmlElement(name = "Number_Of_Orbits", required = true)
    protected BigDecimal numberOfOrbits;
    @XmlElement(name = "Start_Circular_Latitude")
    protected BigDecimal startCircularLatitude;

    /**
     * Obtient la valeur de la propriété swathWidth.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getSwathWidth() {
        return swathWidth;
    }

    /**
     * Définit la valeur de la propriété swathWidth.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setSwathWidth(BigDecimal value) {
        this.swathWidth = value;
    }

    /**
     * Obtient la valeur de la propriété period.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getPeriod() {
        return period;
    }

    /**
     * Définit la valeur de la propriété period.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setPeriod(BigDecimal value) {
        this.period = value;
    }

    /**
     * Obtient la valeur de la propriété inclinationAngle.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getInclinationAngle() {
        return inclinationAngle;
    }

    /**
     * Définit la valeur de la propriété inclinationAngle.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setInclinationAngle(BigDecimal value) {
        this.inclinationAngle = value;
    }

    /**
     * Obtient la valeur de la propriété numberOfOrbits.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getNumberOfOrbits() {
        return numberOfOrbits;
    }

    /**
     * Définit la valeur de la propriété numberOfOrbits.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setNumberOfOrbits(BigDecimal value) {
        this.numberOfOrbits = value;
    }

    /**
     * Obtient la valeur de la propriété startCircularLatitude.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getStartCircularLatitude() {
        return startCircularLatitude;
    }

    /**
     * Définit la valeur de la propriété startCircularLatitude.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setStartCircularLatitude(BigDecimal value) {
        this.startCircularLatitude = value;
    }

}
