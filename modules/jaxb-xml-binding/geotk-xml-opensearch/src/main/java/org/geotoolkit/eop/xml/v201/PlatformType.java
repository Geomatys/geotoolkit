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
package org.geotoolkit.eop.xml.v201;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour PlatformType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="PlatformType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shortName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="serialIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orbitType" type="{http://www.opengis.net/eop/2.1}OrbitTypeValueType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PlatformType", propOrder = {
    "shortName",
    "serialIdentifier",
    "orbitType"
})
public class PlatformType {

    @XmlElement(required = true)
    protected String shortName;
    protected String serialIdentifier;
    @XmlSchemaType(name = "string")
    protected OrbitTypeValueType orbitType;

    /**
     * Obtient la valeur de la propriété shortName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Définit la valeur de la propriété shortName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setShortName(String value) {
        this.shortName = value;
    }

    /**
     * Obtient la valeur de la propriété serialIdentifier.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSerialIdentifier() {
        return serialIdentifier;
    }

    /**
     * Définit la valeur de la propriété serialIdentifier.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSerialIdentifier(String value) {
        this.serialIdentifier = value;
    }

    /**
     * Obtient la valeur de la propriété orbitType.
     *
     * @return
     *     possible object is
     *     {@link OrbitTypeValueType }
     *
     */
    public OrbitTypeValueType getOrbitType() {
        return orbitType;
    }

    /**
     * Définit la valeur de la propriété orbitType.
     *
     * @param value
     *     allowed object is
     *     {@link OrbitTypeValueType }
     *
     */
    public void setOrbitType(OrbitTypeValueType value) {
        this.orbitType = value;
    }

}
