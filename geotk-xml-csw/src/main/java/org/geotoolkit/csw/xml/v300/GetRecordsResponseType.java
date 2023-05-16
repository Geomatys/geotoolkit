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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.GetRecordsResponse;


/**
 *
 *             The response message for a GetRecords request. Some or all of the
 *             matching records may be included as children of the SearchResults
 *             element. The RequestId is only included if the client specified it.
 *
 *
 * <p>Classe Java pour GetRecordsResponseType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GetRecordsResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestId" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="SearchStatus" type="{http://www.opengis.net/cat/csw/3.0}RequestStatusType"/>
 *         &lt;element name="SearchResults" type="{http://www.opengis.net/cat/csw/3.0}SearchResultsType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetRecordsResponseType", propOrder = {
    "requestId",
    "searchStatus",
    "searchResults"
})
@XmlRootElement(name ="GetRecordsResponse")
public class GetRecordsResponseType implements GetRecordsResponse {

    @XmlElement(name = "RequestId")
    @XmlSchemaType(name = "anyURI")
    protected String requestId;
    @XmlElement(name = "SearchStatus", required = true)
    protected RequestStatusType searchStatus;
    @XmlElement(name = "SearchResults", required = true)
    protected SearchResultsType searchResults;
    @XmlAttribute(name = "version")
    protected String version;

    /**
     * An empty constructor used by JAXB
     */
    GetRecordsResponseType() {
    }

    /**
     * Build a new response to a getRecords request
     */
    public GetRecordsResponseType(final String requestId, final long time, final String version, final SearchResultsType searchResults) {
        this.requestId = requestId;
        this.searchStatus = new RequestStatusType(time);
        this.version = version;
        this.searchResults = searchResults;

    }

    /**
     * Obtient la valeur de la propriété requestId.
     */
    @Override
    public String getRequestId() {
        return requestId;
    }

    /**
     * Définit la valeur de la propriété requestId.
     */
    @Override
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Obtient la valeur de la propriété searchStatus.
     */
    @Override
    public RequestStatusType getSearchStatus() {
        return searchStatus;
    }

    /**
     * Définit la valeur de la propriété searchStatus.
     */
    public void setSearchStatus(RequestStatusType value) {
        this.searchStatus = value;
    }

    /**
     * Obtient la valeur de la propriété searchResults.
     */
    public SearchResultsType getSearchResults() {
        return searchResults;
    }

    /**
     * Définit la valeur de la propriété searchResults.
     */
    public void setSearchResults(SearchResultsType value) {
        this.searchResults = value;
    }

    /**
     * Obtient la valeur de la propriété version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Définit la valeur de la propriété version.
     */
    public void setVersion(String value) {
        this.version = value;
    }
}
