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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour SpecificInformationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="SpecificInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="localAttribute" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="localValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpecificInformationType", propOrder = {
    "localAttribute",
    "localValue"
})
public class SpecificInformationType {

    @XmlElement(required = true)
    protected String localAttribute;
    @XmlElement(required = true)
    protected String localValue;

    /**
     * Obtient la valeur de la propriété localAttribute.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLocalAttribute() {
        return localAttribute;
    }

    /**
     * Définit la valeur de la propriété localAttribute.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLocalAttribute(String value) {
        this.localAttribute = value;
    }

    /**
     * Obtient la valeur de la propriété localValue.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLocalValue() {
        return localValue;
    }

    /**
     * Définit la valeur de la propriété localValue.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLocalValue(String value) {
        this.localValue = value;
    }

}
