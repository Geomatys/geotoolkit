/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.csw.xml.v202;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.GetRecordsResponse;


/**
 * 
 * The response message for a GetRecords request. Some or all of the 
 * matching records may be included as children of the SearchResults 
 * element. The RequestId is only included if the client specified it.
 *          
 * 
 * <p>Java class for GetRecordsResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetRecordsResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestId" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="SearchStatus" type="{http://www.opengis.net/cat/csw/2.0.2}RequestStatusType"/>
 *         &lt;element name="SearchResults" type="{http://www.opengis.net/cat/csw/2.0.2}SearchResultsType"/>
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
@XmlRootElement(name = "GetRecordsResponse" )        
public class GetRecordsResponseType implements GetRecordsResponse {

    @XmlElement(name = "RequestId")
    @XmlSchemaType(name = "anyURI")
    private String requestId;
    @XmlElement(name = "SearchStatus", required = true)
    private RequestStatusType searchStatus;
    @XmlElement(name = "SearchResults", required = true)
    private SearchResultsType searchResults;
    @XmlAttribute
    private String version;

    /**
     * An empty constructor used by JAXB
     */
    GetRecordsResponseType() {
        
    }
    
    /**
     * Build a new response to a getRecords request
     */
    public GetRecordsResponseType(String requestId, long time, String version, SearchResultsType searchResults) {
        this.requestId     = requestId;
        this.searchStatus  = new RequestStatusType(time);
        this.version       = version;
        this.searchResults = searchResults;
        
    }
    
    /**
     * Gets the value of the requestId property.
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Gets the value of the searchStatus property.
     */
    public RequestStatusType getSearchStatus() {
        return searchStatus;
    }

    /**
     * Gets the value of the searchResults property.
     */
    public SearchResultsType getSearchResults() {
        return searchResults;
    }

    /**
     * Gets the value of the version property.
     */
    public String getVersion() {
        return version;
    }
    
    public void setRequestId(String value) {
        this.requestId = value;
    }

    public void setVersion(String value) {
        this.version = value;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[GetRecordsResponseType] version=").append(version).append(":").append('\n');
        if (requestId != null) {
            s.append("Request ID: ").append(requestId).append('\n');
        }
        if (searchStatus != null) {
            s.append("searchStatus: ").append(searchStatus).append('\n');
        }
        if (searchResults != null) {
            s.append("searchResult: ").append(searchResults).append('\n');
        }
        return s.toString();
    }
}
