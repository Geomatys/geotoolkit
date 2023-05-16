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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour InstrumentType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="InstrumentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shortName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="instrumentType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstrumentType", propOrder = {
    "shortName",
    "description",
    "instrumentType"
})
public class InstrumentType {

    protected String shortName;
    protected String description;
    protected String instrumentType;

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
     * Obtient la valeur de la propriété description.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Définit la valeur de la propriété description.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Obtient la valeur de la propriété instrumentType.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInstrumentType() {
        return instrumentType;
    }

    /**
     * Définit la valeur de la propriété instrumentType.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInstrumentType(String value) {
        this.instrumentType = value;
    }

}
