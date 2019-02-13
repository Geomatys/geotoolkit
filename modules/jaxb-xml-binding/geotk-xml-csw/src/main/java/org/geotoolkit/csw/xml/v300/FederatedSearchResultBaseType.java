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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.FederatedSearchResultBase;


/**
 * <p>Classe Java pour FederatedSearchResultBaseType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="FederatedSearchResultBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="catalogueURL" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FederatedSearchResultBaseType")
@XmlSeeAlso({
    FederatedExceptionType.class,
    FederatedSearchResultType.class
})
public abstract class FederatedSearchResultBaseType implements FederatedSearchResultBase {

    @XmlAttribute(name = "catalogueURL", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String catalogueURL;

    public FederatedSearchResultBaseType() {

    }

    public FederatedSearchResultBaseType(String catalogueURL) {
        this.catalogueURL = catalogueURL;
    }

    /**
     * Obtient la valeur de la propriété catalogueURL.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCatalogueURL() {
        return catalogueURL;
    }

    /**
     * Définit la valeur de la propriété catalogueURL.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCatalogueURL(String value) {
        this.catalogueURL = value;
    }

}
