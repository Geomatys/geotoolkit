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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *                 DIF used discrete fields for different phone numbers (fax,
 *                 work, etc.) UMM defines a "name-value" style list which
 *                 should future proof phone numbers.
 *
 *                 * Number : phone number
 *                 * Type : The type of telephone number provided
 *
 *                 | DIF 9    | ECHO 10   | UMM     | DIF 10   | Notes |
 *                 | -------- | ----------| ------- | -------- | ----- |
 *                 | Phone    | Phone     | Phone   | Phone    |       |
 *
 *
 * <p>Classe Java pour PhoneType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="PhoneType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Type" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}PhoneTypeEnum"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhoneType", propOrder = {
    "number",
    "type"
})
public class PhoneType {

    @XmlElement(name = "Number", required = true)
    protected String number;
    @XmlElement(name = "Type", required = true)
    @XmlSchemaType(name = "string")
    protected PhoneTypeEnum type;

    /**
     * Obtient la valeur de la propriété number.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNumber() {
        return number;
    }

    /**
     * Définit la valeur de la propriété number.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link PhoneTypeEnum }
     *
     */
    public PhoneTypeEnum getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link PhoneTypeEnum }
     *
     */
    public void setType(PhoneTypeEnum value) {
        this.type = value;
    }

}
