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
import javax.xml.bind.annotation.XmlType;


/**
 *
 *                 Content can be markdown
 *
 *                 | DIF 9    | ECHO 10        | UMM      | DIF 10   | Notes     |
 *                 | -------- | -------------- | -------- | -------- | --------- |
 *                 | Abstract | Description    | Abstract | Abstract | No change |
 *                 | Purpose  | SuggestedUsage | Purpose  | Purpose  | No change |
 *
 *
 * <p>Classe Java pour SummaryType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="SummaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Abstract" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DisplayableTextType" minOccurs="0"/>
 *         &lt;element name="Purpose" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DisplayableTextType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummaryType", propOrder = {
    "_abstract",
    "purpose"
})
public class SummaryType {

    @XmlElement(name = "Abstract")
    protected DisplayableTextType _abstract;
    @XmlElement(name = "Purpose")
    protected DisplayableTextType purpose;

    /**
     * Obtient la valeur de la propriété abstract.
     *
     * @return
     *     possible object is
     *     {@link DisplayableTextType }
     *
     */
    public DisplayableTextType getAbstract() {
        return _abstract;
    }

    /**
     * Définit la valeur de la propriété abstract.
     *
     * @param value
     *     allowed object is
     *     {@link DisplayableTextType }
     *
     */
    public void setAbstract(DisplayableTextType value) {
        this._abstract = value;
    }

    /**
     * Obtient la valeur de la propriété purpose.
     *
     * @return
     *     possible object is
     *     {@link DisplayableTextType }
     *
     */
    public DisplayableTextType getPurpose() {
        return purpose;
    }

    /**
     * Définit la valeur de la propriété purpose.
     *
     * @param value
     *     allowed object is
     *     {@link DisplayableTextType }
     *
     */
    public void setPurpose(DisplayableTextType value) {
        this.purpose = value;
    }

}
