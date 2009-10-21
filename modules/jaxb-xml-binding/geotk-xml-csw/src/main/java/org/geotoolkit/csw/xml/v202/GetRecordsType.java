/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.GetRecordsRequest;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.util.Utilities;


/**
 * 
 * The principal means of searching the catalogue. 
 * The matching catalogue entries may be included with the response. 
 * The client may assign a requestId (absolute URI). 
 * A distributed search is performed if the DistributedSearch element is present and the catalogue is a member of a federation. 
 * Profiles may allow alternative query expressions.
 * 
 * <p>Java class for GetRecordsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetRecordsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/2.0.2}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="DistributedSearch" type="{http://www.opengis.net/cat/csw/2.0.2}DistributedSearchType" minOccurs="0"/>
 *         &lt;element name="ResponseHandler" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/cat/csw/2.0.2}AbstractQuery"/>
 *           &lt;any/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/cat/csw/2.0.2}BasicRetrievalOptions"/>
 *       &lt;attribute name="requestId" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="resultType" type="{http://www.opengis.net/cat/csw/2.0.2}ResultType" default="hits" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
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
    @XmlElementRefs({
        @XmlElementRef(name = "AbstractQuery", namespace = "http://www.opengis.net/cat/csw/2.0.2", type = AbstractQueryType.class),
        @XmlElementRef(name = "Query", namespace = "http://www.opengis.net/cat/csw/2.0.2", type = QueryType.class)
    })
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
     */
    public DistributedSearchType getDistributedSearch() {
        return distributedSearch;
    }

    /**
     * Gets the value of the responseHandler property.
     * (unmodifiable)
     */
    public List<String> getResponseHandler() {
        if (responseHandler == null) {
            responseHandler = new ArrayList<String>();
        }
        return Collections.unmodifiableList(responseHandler);
    }

    /**
     * Gets the value of the abstractQuery property.
     */
    public AbstractQueryType getAbstractQuery() {
        return abstractQuery;
    }

    /**
     * Sets the value of the abstractQuery property.
     * 
     */
    public void setAbstractQuery(AbstractQueryType value) {
        this.abstractQuery = value;
    }
    
    /**
     * Gets the value of the any property.
     */
    public Object getAny() {
        return any;
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
     */
    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    /**
     * Sets the value of the resultType property which is a string.
     */
    public void setResultType(String resultType) {
        this.resultType = ResultType.fromValue(resultType);
    }
    
    /**
     * Gets the value of the outputFormat property.
     */
    public String getOutputFormat() {
        return outputFormat;
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
     * Initialize the start position.
     * 
     * @param startPosition 
     */
    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    /**
     * Gets the value of the startPosition property.
     */
    public Integer getStartPosition() {
        if (startPosition == null) {
            return new Integer("1");
        } else {
            return startPosition;
        }
    }

    /**
     * Gets the value of the maxRecords property.
     */
    public Integer getMaxRecords() {
        if (maxRecords == null) {
            return 10;
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

    public void setTypeNames(List<QName> typenames) {
        abstractQuery.setTypeNames(typenames);
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
    
     /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetRecordsType && super.equals(object)) {
            final GetRecordsType that = (GetRecordsType) object;
            return Utilities.equals(this.abstractQuery,  that.abstractQuery)   &&
                   Utilities.equals(this.distributedSearch,  that.distributedSearch)   &&
                   Utilities.equals(this.maxRecords,  that.maxRecords)   &&
                   Utilities.equals(this.outputFormat,  that.outputFormat)   &&
                   Utilities.equals(this.outputSchema,  that.outputSchema)   &&
                   Utilities.equals(this.requestId,  that.requestId)   &&
                   Utilities.equals(this.responseHandler,  that.responseHandler)   &&
                   Utilities.equals(this.resultType,  that.resultType)   &&
                   Utilities.equals(this.startPosition,  that.startPosition)   &&
                   Utilities.equals(this.any ,  that.any);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.distributedSearch != null ? this.distributedSearch.hashCode() : 0);
        hash = 79 * hash + (this.responseHandler != null ? this.responseHandler.hashCode() : 0);
        hash = 79 * hash + (this.abstractQuery != null ? this.abstractQuery.hashCode() : 0);
        hash = 79 * hash + (this.any != null ? this.any.hashCode() : 0);
        hash = 79 * hash + (this.requestId != null ? this.requestId.hashCode() : 0);
        hash = 79 * hash + (this.resultType != null ? this.resultType.hashCode() : 0);
        hash = 79 * hash + (this.outputFormat != null ? this.outputFormat.hashCode() : 0);
        hash = 79 * hash + (this.outputSchema != null ? this.outputSchema.hashCode() : 0);
        hash = 79 * hash + (this.startPosition != null ? this.startPosition.hashCode() : 0);
        hash = 79 * hash + (this.maxRecords != null ? this.maxRecords.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        
        if (distributedSearch != null) {
            s.append("distributedSearch: ").append(distributedSearch).append('\n');
        }
        if (responseHandler != null) {
            s.append("responseHandler: ").append(responseHandler).append('\n');
        }
        if (abstractQuery != null) {
            s.append("abstractQuery: ").append(abstractQuery).append('\n');
        }
        if (any != null) {
            s.append("any: ").append(any).append('\n');
        }
        if (requestId != null) {
            s.append("requestId: ").append(requestId).append('\n');
        }
        if (resultType != null) {
            s.append("resultType: ").append(resultType).append('\n');
        }
        if (outputFormat != null) {
            s.append("outputFormat: ").append(outputFormat).append('\n');
        }
        if (outputSchema != null) {
            s.append("outputSchema: ").append(outputSchema).append('\n');
        }
        if (startPosition != null) {
            s.append("startPosition: ").append(startPosition).append('\n');
        }
        if (maxRecords != null) {
            s.append("maxRecords: ").append(maxRecords).append('\n');
        }
        return s.toString();
    }
}
