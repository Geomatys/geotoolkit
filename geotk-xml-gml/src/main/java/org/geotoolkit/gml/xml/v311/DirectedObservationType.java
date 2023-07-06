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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour DirectedObservationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="DirectedObservationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}ObservationType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}direction"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectedObservationType", propOrder = {
    "direction"
})
@XmlSeeAlso({
    DirectedObservationAtDistanceType.class
})
public class DirectedObservationType extends ObservationType {

    @XmlElement(required = true)
    protected DirectionPropertyType direction;

    /**
     * Obtient la valeur de la propriété direction.
     *
     * @return
     *     possible object is
     *     {@link DirectionPropertyType }
     *
     */
    public DirectionPropertyType getDirection() {
        return direction;
    }

    /**
     * Définit la valeur de la propriété direction.
     *
     * @param value
     *     allowed object is
     *     {@link DirectionPropertyType }
     *
     */
    public void setDirection(DirectionPropertyType value) {
        this.direction = value;
    }

}
