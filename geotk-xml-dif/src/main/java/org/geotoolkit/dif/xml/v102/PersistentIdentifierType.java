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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *                 In the past, fields such as Dataset_DOI encoded DOI and ARK values as "doi:10.1000/182" or "ark:/NAAN/Name[Qualifier]".
 *                 Much feedback was given on the wisdom of this syntax, based on that feedback the DOI fields in DIF have been changed to
 *                 support a type allowing for any value.
 *
 *
 * <p>Classe Java pour PersistentIdentifierType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="PersistentIdentifierType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}PersistentIdentifierEnum"/>
 *         &lt;element name="Identifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersistentIdentifierType", propOrder = {
    "type",
    "identifier"
})
public class PersistentIdentifierType {

    @XmlElement(name = "Type", required = true)
    @XmlSchemaType(name = "string")
    protected PersistentIdentifierEnum type;
    @XmlElement(name = "Identifier", required = true)
    protected String identifier;

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link PersistentIdentifierEnum }
     *
     */
    public PersistentIdentifierEnum getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link PersistentIdentifierEnum }
     *
     */
    public void setType(PersistentIdentifierEnum value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété identifier.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Définit la valeur de la propriété identifier.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentifier(String value) {
        this.identifier = value;
    }

}
