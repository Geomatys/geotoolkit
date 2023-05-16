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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ConceptualSchemeType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ConceptualSchemeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Document" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="Authority" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConceptualSchemeType", propOrder = {
    "name",
    "document",
    "authority"
})
public class ConceptualSchemeType {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Document", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String document;
    @XmlElement(name = "Authority", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String authority;

    /**
     * Obtient la valeur de la propriété name.
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété document.
     */
    public String getDocument() {
        return document;
    }

    /**
     * Définit la valeur de la propriété document.
     */
    public void setDocument(String value) {
        this.document = value;
    }

    /**
     * Obtient la valeur de la propriété authority.
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * Définit la valeur de la propriété authority.
     */
    public void setAuthority(String value) {
        this.authority = value;
    }
}
