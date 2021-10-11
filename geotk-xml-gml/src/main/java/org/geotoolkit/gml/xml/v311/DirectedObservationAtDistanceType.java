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

package org.geotoolkit.gml.xml.v311;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour DirectedObservationAtDistanceType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="DirectedObservationAtDistanceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}DirectedObservationType">
 *       &lt;sequence>
 *         &lt;element name="distance" type="{http://www.opengis.net/gml}MeasureType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectedObservationAtDistanceType", propOrder = {
    "distance"
})
public class DirectedObservationAtDistanceType extends DirectedObservationType {

    @XmlElement(required = true)
    protected MeasureType distance;

    /**
     * Obtient la valeur de la propriété distance.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getDistance() {
        return distance;
    }

    /**
     * Définit la valeur de la propriété distance.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setDistance(MeasureType value) {
        this.distance = value;
    }

}
