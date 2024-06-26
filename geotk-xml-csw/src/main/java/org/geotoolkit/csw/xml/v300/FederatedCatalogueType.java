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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour FederatedCatalogueType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="FederatedCatalogueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="catalogueURL" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="timeout" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FederatedCatalogueType")
public class FederatedCatalogueType {

    @XmlAttribute(name = "catalogueURL", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String catalogueURL;
    @XmlAttribute(name = "timeout")
    @XmlSchemaType(name = "unsignedLong")
    protected Integer timeout;

    /**
     * Obtient la valeur de la propriété catalogueURL.
     */
    public String getCatalogueURL() {
        return catalogueURL;
    }

    /**
     * Définit la valeur de la propriété catalogueURL.
     */
    public void setCatalogueURL(String value) {
        this.catalogueURL = value;
    }

    /**
     * Obtient la valeur de la propriété timeout.
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * Définit la valeur de la propriété timeout.
     */
    public void setTimeout(Integer value) {
        this.timeout = value;
    }
}
