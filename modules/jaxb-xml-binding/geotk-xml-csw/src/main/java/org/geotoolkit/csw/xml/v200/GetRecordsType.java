/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.csw.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.GetRecordsRequest;
import org.geotoolkit.ogc.xml.FilterType;


/**
 * The principal means of searching catalogue content. 
 * The matching catalogue entries may be included with the response.
 * The client may assign a requestId (absolute URI). 
 * A distributed search is performed if the distributedSearch element is present.
 *          
 * 
 * <p>Java class for GetRecordsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetRecordsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="DistributedSearch" type="{http://www.opengis.net/cat/csw}DistributedSearchType" minOccurs="0"/>
 *         &lt;element name="ResponseHandler" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/cat/csw}AbstractQuery"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/cat/csw}BasicRetrievalOptions"/>
 *       &lt;attribute name="requestId" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="resultType" type="{http://www.opengis.net/cat/csw}ResultType" default="hits" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetRecordsType", propOrder = {
    "distributedSearch",
    "responseHandler",
    "abstractQuery",
    "any"
})
@XmlRootElement(name = "GetRecords")        
public class GetRecordsType extends RequestBaseType implements GetRecordsRequest {

    @XmlElement(name = "DistributedSearch")
    private DistributedSearchType distributedSearch;
    @XmlElement(name = "ResponseHandler")
    @XmlSchemaType(name = "anyURI")
    private List<String> responseHandler;
    @XmlElementRef(name = "AbstractQuery", namespace = "http://www.opengis.net/cat/csw", type = JAXBElement.class)
    private AbstractQueryType abstractQuery;
    @XmlAnyElement(lax = true)
    private Object any;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String requestId;
    @XmlAttribute
    private ResultType resultType;
    @XmlAttribute
    private String outputFormat;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String outputSchema;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer startPosition;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer maxRecords;

    /**
     * An empty constructor used by JAXB
     */
    public GetRecordsType() {
        
    }
    
    /**
     * Build a new GetRecords request
     */
    public GetRecordsType(String service, String version, ResultType resultType, 
            String requestId, String outputFormat, String outputSchema, Integer startPosition,
            Integer maxRecords, AbstractQueryType abstractQuery,
            DistributedSearchType distributedSearch) {
        
        super(service, version);
        this.resultType        = resultType;
        this.requestId         = requestId;
        this.outputFormat      = outputFormat;
        this.outputSchema      = outputSchema;
        this.startPosition     = startPosition;
        this.maxRecords        = maxRecords;
        this.abstractQuery     = abstractQuery;
        this.distributedSearch = distributedSearch;
        
        
    }
    
    /**
     * Gets the value of the distributedSearch property.
     * 
     */
    public DistributedSearchType getDistributedSearch() {
        return distributedSearch;
    }

    /**
     * Sets the value of the distributedSearch property.
     * 
     */
    public void setDistributedSearch(DistributedSearchType value) {
        this.distributedSearch = value;
    }

    /**
     * Gets the value of the responseHandler property.
     * 
     */
    public List<String> getResponseHandler() {
        if (responseHandler == null) {
            responseHandler = new ArrayList<String>();
        }
        return this.responseHandler;
    }

    /**
     * Gets the value of the abstractQuery property.
     * 
     */
    public AbstractQueryType getAbstractQuery() {
        return abstractQuery;
    }
    
    /**
     * Gets the value of the any property.
     */
    public Object getAny() {
        return any;
    }

    /**
     * Sets the value of the abstractQuery property.
     * 
     */
    public void setAbstractQuery(AbstractQueryType value) {
        this.abstractQuery = value;
    }

    /**
     * Gets the value of the requestId property.
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the resultType property.
     */
    public ResultType getResultType() {
        if (resultType == null) {
            return ResultType.HITS;
        } else {
            return resultType;
        }
    }

    /**
     * Sets the value of the resultType property.
     * 
     */
    public void setResultType(ResultType value) {
        this.resultType = value;
    }
    
    /**
     * Sets the value of the resultType property which is a string.
     */
    public void setResultType(String resultType) {
        this.resultType = ResultType.fromValue(resultType);
    }

    /**
     * Gets the value of the outputFormat property.
     * 
     */
    public String getOutputFormat() {
        if (outputFormat == null) {
            return "text/xml";
        } else {
            return outputFormat;
        }
    }

    /**
     * Sets the value of the outputFormat property.
     * 
     */
    public void setOutputFormat(String value) {
        this.outputFormat = value;
    }

    /**
     * Gets the value of the outputSchema property.
     */
    public String getOutputSchema() {
        return outputSchema;
    }

    /**
     * Sets the value of the outputSchema property.
     * 
     */
    public void setOutputSchema(String value) {
        this.outputSchema = value;
    }

    /**
     * Gets the value of the startPosition property.
     * 
     */
    public Integer getStartPosition() {
        if (startPosition == null) {
            return new Integer("1");
        } else {
            return startPosition;
        }
    }

    /**
     * Sets the value of the startPosition property.
     * 
     */
    public void setStartPosition(Integer value) {
        this.startPosition = value;
    }

    /**
     * Gets the value of the maxRecords property.
     * 
     */
    public Integer getMaxRecords() {
        if (maxRecords == null) {
            return new Integer("10");
        } else {
            return maxRecords;
        }
    }

    /**
     * Sets the value of the maxRecords property.
     * 
     */
    public void setMaxRecords(Integer value) {
        this.maxRecords = value;
    }

    public void setTypeNames(List<QName> typeNames) {
        if (typeNames != null) {
            abstractQuery.setTypeNames(typeNames);
        }
    }

    public void removeConstraint() {
        abstractQuery.setConstraint(null);
    }

    public void setCQLConstraint(String CQLQuery) {
        abstractQuery.setConstraint(new QueryConstraintType(CQLQuery, "1.1.0"));
    }
    
    /**
     * This method set a query constraint by a filter.
     * @param filter FilterType
     */
    public void setFilterConstraint(FilterType filter) {
        abstractQuery.setConstraint(new QueryConstraintType(filter, "1.1.0"));
    }

}
