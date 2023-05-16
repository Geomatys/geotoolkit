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
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.FederatedSearchResult;


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
public class FederatedSearchResultType extends FederatedSearchResultBaseType implements FederatedSearchResult {

    @XmlElement(required = true)
    protected SearchResultsType searchResult;

    public FederatedSearchResultType() {

    }

    public FederatedSearchResultType(String catalogueURL, SearchResultsType searchResult) {
        super(catalogueURL);
        this.searchResult = searchResult;
    }

    /**
     * Obtient la valeur de la propriété searchResult.
     */
    @Override
    public SearchResultsType getSearchResult() {
        return searchResult;
    }

    /**
     * Définit la valeur de la propriété searchResult.
     */
    public void setSearchResult(SearchResultsType value) {
        this.searchResult = value;
    }

    @Override
    public int getMatched() {
        if (searchResult != null) {
            return searchResult.getNumberOfRecordsMatched();
        }
        return 0;
    }

    @Override
    public int getReturned() {
        if (searchResult != null) {
            return searchResult.getNumberOfRecordsReturned();
        }
        return 0;
    }
}
