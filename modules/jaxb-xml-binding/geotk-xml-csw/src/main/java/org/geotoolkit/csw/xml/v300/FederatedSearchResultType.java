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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour FederatedSearchResultType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="FederatedSearchResultType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/3.0}FederatedSearchResultBaseType">
 *       &lt;sequence>
 *         &lt;element name="searchResult" type="{http://www.opengis.net/cat/csw/3.0}SearchResultsType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FederatedSearchResultType", propOrder = {
    "searchResult"
})
public class FederatedSearchResultType
    extends FederatedSearchResultBaseType
{

    @XmlElement(required = true)
    protected SearchResultsType searchResult;

    /**
     * Obtient la valeur de la propriété searchResult.
     *
     * @return
     *     possible object is
     *     {@link SearchResultsType }
     *
     */
    public SearchResultsType getSearchResult() {
        return searchResult;
    }

    /**
     * Définit la valeur de la propriété searchResult.
     *
     * @param value
     *     allowed object is
     *     {@link SearchResultsType }
     *
     */
    public void setSearchResult(SearchResultsType value) {
        this.searchResult = value;
    }

}
