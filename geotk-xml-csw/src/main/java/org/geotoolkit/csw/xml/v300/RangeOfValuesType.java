/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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
package org.geotoolkit.csw.xml.v300;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour RangeOfValuesType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="RangeOfValuesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MinValue" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="MaxValue" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RangeOfValuesType", propOrder = {
    "minValue",
    "maxValue"
})
public class RangeOfValuesType {

    @XmlElement(name = "MinValue")
    protected Object minValue;
    @XmlElement(name = "MaxValue")
    protected Object maxValue;

    /**
     * Obtient la valeur de la propriété minValue.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    public Object getMinValue() {
        return minValue;
    }

    /**
     * Définit la valeur de la propriété minValue.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setMinValue(Object value) {
        this.minValue = value;
    }

    /**
     * Obtient la valeur de la propriété maxValue.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    public Object getMaxValue() {
        return maxValue;
    }

    /**
     * Définit la valeur de la propriété maxValue.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setMaxValue(Object value) {
        this.maxValue = value;
    }

}
