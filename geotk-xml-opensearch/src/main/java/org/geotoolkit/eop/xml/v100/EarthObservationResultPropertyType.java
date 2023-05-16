/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019
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


package org.geotoolkit.eop.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour EarthObservationResultPropertyType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="EarthObservationResultPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://earth.esa.int/eop}EarthObservationResult"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EarthObservationResultPropertyType", propOrder = {
    "earthObservationResult"
})
public class EarthObservationResultPropertyType {

    @XmlElement(name = "EarthObservationResult", required = true)
    protected EarthObservationResultType earthObservationResult;

    /**
     * Obtient la valeur de la propriété earthObservationResult.
     *
     * @return
     *     possible object is
     *     {@link EarthObservationResultType }
     *
     */
    public EarthObservationResultType getEarthObservationResult() {
        return earthObservationResult;
    }

    /**
     * Définit la valeur de la propriété earthObservationResult.
     *
     * @param value
     *     allowed object is
     *     {@link EarthObservationResultType }
     *
     */
    public void setEarthObservationResult(EarthObservationResultType value) {
        this.earthObservationResult = value;
    }

}
