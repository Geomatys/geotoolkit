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
import org.geotoolkit.gml.xml.v311.CodeListType;


/**
 * <p>Classe Java pour BrowseInformationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="BrowseInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="THUMBNAIL"/>
 *               &lt;enumeration value="QUICKLOOK"/>
 *               &lt;enumeration value="ALBUM"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="subType" type="{http://www.opengis.net/gml}CodeListType" minOccurs="0"/>
 *         &lt;element name="referenceSystemIdentifier" type="{http://earth.esa.int/eop}CodeWithAuthorityType"/>
 *         &lt;element name="fileName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BrowseInformationType", propOrder = {
    "type",
    "subType",
    "referenceSystemIdentifier",
    "fileName"
})
public class BrowseInformationType {

    @XmlElement(required = true)
    protected String type;
    protected CodeListType subType;
    @XmlElement(required = true)
    protected CodeWithAuthorityType referenceSystemIdentifier;
    @XmlElement(required = true)
    protected String fileName;

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété subType.
     *
     * @return
     *     possible object is
     *     {@link CodeListType }
     *
     */
    public CodeListType getSubType() {
        return subType;
    }

    /**
     * Définit la valeur de la propriété subType.
     *
     * @param value
     *     allowed object is
     *     {@link CodeListType }
     *
     */
    public void setSubType(CodeListType value) {
        this.subType = value;
    }

    /**
     * Obtient la valeur de la propriété referenceSystemIdentifier.
     *
     * @return
     *     possible object is
     *     {@link CodeWithAuthorityType }
     *
     */
    public CodeWithAuthorityType getReferenceSystemIdentifier() {
        return referenceSystemIdentifier;
    }

    /**
     * Définit la valeur de la propriété referenceSystemIdentifier.
     *
     * @param value
     *     allowed object is
     *     {@link CodeWithAuthorityType }
     *
     */
    public void setReferenceSystemIdentifier(CodeWithAuthorityType value) {
        this.referenceSystemIdentifier = value;
    }

    /**
     * Obtient la valeur de la propriété fileName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Définit la valeur de la propriété fileName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

}
