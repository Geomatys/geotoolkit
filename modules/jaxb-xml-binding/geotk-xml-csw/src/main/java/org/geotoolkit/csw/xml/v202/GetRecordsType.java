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
import java.util.List;
import java.util.Objects;
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
import org.geotoolkit.csw.xml.ResultType;
import org.geotoolkit.ogc.xml.XMLFilter;
import org.geotoolkit.ogc.xml.v110.FilterType;


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
 * @module
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
    public GetRecordsType(final String service, final String version, final ResultType resultType,
            final String requestId, final String outputFormat, final String outputSchema, final Integer startPosition,
            final Integer maxRecords, final AbstractQueryType abstractQuery,
            final DistributedSearchType distributedSearch) {

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

    public GetRecordsType(final GetRecordsType other) {
        super(other);
        if (other != null) {
            if (other.abstractQuery instanceof QueryType) {
                this.abstractQuery = new QueryType((QueryType)other.abstractQuery);
            } else if (other.abstractQuery != null) {
                throw new IllegalArgumentException("Uncloneable query object:" + other.getClass().getName());
            }
            if (other.distributedSearch != null) {
                this.distributedSearch = new DistributedSearchType(other.distributedSearch);
            }
            this.maxRecords = other.maxRecords;
            this.outputFormat = other.outputFormat;
            this.outputSchema = other.outputSchema;
            this.requestId    = other.requestId;
            if (other.responseHandler != null) {
                this.responseHandler = new ArrayList<>(other.responseHandler);
            }
            this.resultType = other.resultType;
            this.startPosition = other.startPosition;
        }

    }

    /**
     * Gets the value of the distributedSearch property.
     */
    @Override
    public DistributedSearchType getDistributedSearch() {
        return distributedSearch;
    }

    /**
     * Gets the value of the responseHandler property.
     */
    public List<String> getResponseHandler() {
        if (responseHandler == null) {
            responseHandler = new ArrayList<>();
        }
        return responseHandler;
    }


    public void setResponseHandler(final List<String> responseHandler) {
        this.responseHandler = responseHandler;
    }

    /**
     * Gets the value of the abstractQuery property.
     */
    @Override
    public AbstractQueryType getAbstractQuery() {
        return abstractQuery;
    }

    /**
     * Sets the value of the abstractQuery property.
     *
     */
    public void setAbstractQuery(final AbstractQueryType value) {
        this.abstractQuery = value;
    }

    /**
     * Gets the value of the any property.
     */
    public Object getAny() {
        return any;
    }

    public void setAny(final Object any) {
        this.any = any;
    }

    /**
     * Gets the value of the requestId property.
     */
    @Override
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     *
     */
    @Override
    public void setRequestId(final String value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the resultType property.
     */
    @Override
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
    public void setResultType(final ResultType resultType) {
        this.resultType = resultType;
    }

    /**
     * Sets the value of the resultType property which is a string.
     */
    @Override
    public void setResultType(final String resultType) {
        this.resultType = ResultType.fromValue(resultType);
    }

    /**
     * Gets the value of the outputFormat property.
     */
    @Override
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * Sets the value of the outputFormat property.
     *
     */
    @Override
    public void setOutputFormat(final String value) {
        this.outputFormat = value;
    }

    /**
     * Gets the value of the outputSchema property.
     */
    @Override
    public String getOutputSchema() {
        return outputSchema;
    }

    /**
     * Sets the value of the outputSchema property.
     *
     */
    @Override
    public void setOutputSchema(final String value) {
        this.outputSchema = value;
    }

    /**
     * Initialize the start position.
     *
     * @param startPosition
     */
    @Override
    public void setStartPosition(final Integer startPosition) {
        this.startPosition = startPosition;
    }

    /**
     * Gets the value of the startPosition property.
     */
    @Override
    public Integer getStartPosition() {
        if (startPosition == null) {
            return 1;
        } else {
            return startPosition;
        }
    }

    /**
     * Gets the value of the maxRecords property.
     */
    @Override
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
    @Override
    public void setMaxRecords(final Integer value) {
        this.maxRecords = value;
    }

    @Override
    public void setTypeNames(final List<QName> typenames) {
        abstractQuery.setTypeNames(typenames);
    }

    @Override
    public void removeConstraint() {
        abstractQuery.setConstraint(null);
    }

    @Override
    public void setCQLConstraint(final String CQLQuery) {
        abstractQuery.setConstraint(new QueryConstraintType(CQLQuery, "1.1.0"));
    }

    /**
     * This method set a query constraint by a filter.
     * @param filter FilterType
     */
    @Override
    public void setFilterConstraint(final XMLFilter filter) {
        if (filter instanceof FilterType) {
            abstractQuery.setConstraint(new QueryConstraintType((FilterType) filter, "1.1.0"));
        } else {
            throw new IllegalArgumentException("Not a v110 filter");
        }
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
            return Objects.equals(this.abstractQuery,  that.abstractQuery)   &&
                   Objects.equals(this.distributedSearch,  that.distributedSearch)   &&
                   Objects.equals(this.getMaxRecords(),  that.getMaxRecords())   &&
                   Objects.equals(this.outputFormat,  that.outputFormat)   &&
                   Objects.equals(this.outputSchema,  that.outputSchema)   &&
                   Objects.equals(this.requestId,  that.requestId)   &&
                   Objects.equals(this.responseHandler,  that.responseHandler)   &&
                   Objects.equals(this.getResultType(),  that.getResultType())   &&
                   Objects.equals(this.getStartPosition(),  that.getStartPosition())   &&
                   Objects.equals(this.any ,  that.any);
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
