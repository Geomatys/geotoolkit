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
 *                 Field describes media options, size, data format, and fees involved in distributing the data set.
 *
 *               | DIF 9            | ECHO 10           | UMM            | DIF 10       | Notes      |
 *               | ---------------- | ----------------- | -------------- | ------------ | ---------- |
 *               | Distribution     |         -         | Distribution   | Distribution | No change  |
 *               | Fees             | Price             |        -       | Fees         | Not in UMM |
 *
 *
 *
 * <p>Classe Java pour DistributionType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="DistributionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Distribution_Media" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Distribution_Size" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Distribution_Format" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Fees" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DistributionType", propOrder = {
    "distributionMedia",
    "distributionSize",
    "distributionFormat",
    "fees"
})
public class DistributionType {

    @XmlElement(name = "Distribution_Media")
    protected String distributionMedia;
    @XmlElement(name = "Distribution_Size")
    protected String distributionSize;
    @XmlElement(name = "Distribution_Format")
    protected String distributionFormat;
    @XmlElement(name = "Fees")
    protected String fees;

    /**
     * Obtient la valeur de la propriété distributionMedia.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDistributionMedia() {
        return distributionMedia;
    }

    /**
     * Définit la valeur de la propriété distributionMedia.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDistributionMedia(String value) {
        this.distributionMedia = value;
    }

    /**
     * Obtient la valeur de la propriété distributionSize.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDistributionSize() {
        return distributionSize;
    }

    /**
     * Définit la valeur de la propriété distributionSize.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDistributionSize(String value) {
        this.distributionSize = value;
    }

    /**
     * Obtient la valeur de la propriété distributionFormat.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDistributionFormat() {
        return distributionFormat;
    }

    /**
     * Définit la valeur de la propriété distributionFormat.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDistributionFormat(String value) {
        this.distributionFormat = value;
    }

    /**
     * Obtient la valeur de la propriété fees.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFees() {
        return fees;
    }

    /**
     * Définit la valeur de la propriété fees.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFees(String value) {
        this.fees = value;
    }

}
