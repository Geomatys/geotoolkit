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
import org.geotoolkit.csw.xml.ResultType;
import org.geotoolkit.ogc.xml.XMLFilter;
import org.geotoolkit.ogc.xml.v200.FilterType;


/**
 *
 *             The principal means of searching the catalogue. The matching
 *             catalogue entries may be included with the response. The client
 *             may assign a requestId (absolute URI). A distributed search is
 *             performed if the DistributedSearch element is present and the
 *             catalogue is a member of a federation. Profiles may allow
 *             alternative query expressions.
 *
 *
 * <p>Classe Java pour GetRecordsType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GetRecordsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/3.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="DistributedSearch" type="{http://www.opengis.net/cat/csw/3.0}DistributedSearchType" minOccurs="0"/>
 *         &lt;element name="ResponseHandler" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/cat/csw/3.0}AbstractQuery"/>
 *           &lt;any namespace='##other'/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/cat/csw/3.0}BasicRetrievalOptions"/>
 *       &lt;attribute name="requestId" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
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
@XmlRootElement(name="GetRecords")
public class GetRecordsType extends RequestBaseType implements GetRecordsRequest {

    @XmlElement(name = "DistributedSearch")
    protected DistributedSearchType distributedSearch;
    @XmlElement(name = "ResponseHandler")
    @XmlSchemaType(name = "anyURI")
    protected List<String> responseHandler;
    @XmlElementRef(name = "AbstractQuery", namespace = "http://www.opengis.net/cat/csw/3.0", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends AbstractQueryType> abstractQuery;
    @XmlAnyElement(lax = true)
    protected Object any;
    @XmlAttribute(name = "requestId")
    @XmlSchemaType(name = "anyURI")
    protected String requestId;
    @XmlAttribute(name = "outputFormat")
    protected String outputFormat;
    @XmlAttribute(name = "outputSchema")
    @XmlSchemaType(name = "anyURI")
    protected String outputSchema;
    @XmlAttribute(name = "startPosition")
    @XmlSchemaType(name = "positiveInteger")
    protected Integer startPosition;
    @XmlAttribute(name = "maxRecords")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Integer maxRecords;

    /**
     * An empty constructor used by JAXB
     */
    public GetRecordsType() {

    }

    /**
     * Build a new GetRecords request
     */
    public GetRecordsType(final String service, final String version,
            final String requestId, final String outputFormat, final String outputSchema, final Integer startPosition,
            final Integer maxRecords, final AbstractQueryType abstractQuery,
            final DistributedSearchType distributedSearch) {

        super(service, version);
        this.requestId         = requestId;
        this.outputFormat      = outputFormat;
        this.outputSchema      = outputSchema;
        this.startPosition     = startPosition;
        this.maxRecords        = maxRecords;
        this.distributedSearch = distributedSearch;
        if (abstractQuery != null) {
            ObjectFactory factory = new ObjectFactory();
            if (abstractQuery instanceof QueryType) {
                this.abstractQuery = factory.createQuery((QueryType) abstractQuery);
            } else {
                this.abstractQuery = factory.createAbstractQuery(abstractQuery);
            }
        }
    }

    public GetRecordsType(final GetRecordsType other) {
        super(other);
        if (other != null) {
            if (other.abstractQuery != null) {
                ObjectFactory factory = new ObjectFactory();
                if (other.abstractQuery.getValue() instanceof QueryType) {
                    this.abstractQuery = factory.createQuery(new QueryType((QueryType)other.abstractQuery.getValue()));
                } else if (other.abstractQuery != null) {
                    throw new IllegalArgumentException("Uncloneable query object:" + other.getClass().getName());
                }
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
            this.startPosition = other.startPosition;
        }

    }

    /**
     * Obtient la valeur de la propriété distributedSearch.
     *
     * @return
     *     possible object is
     *     {@link DistributedSearchType }
     *
     */
    @Override
    public DistributedSearchType getDistributedSearch() {
        return distributedSearch;
    }

    /**
     * Définit la valeur de la propriété distributedSearch.
     *
     * @param value
     *     allowed object is
     *     {@link DistributedSearchType }
     *
     */
    public void setDistributedSearch(DistributedSearchType value) {
        this.distributedSearch = value;
    }

    /**
     * Gets the value of the responseHandler property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the responseHandler property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResponseHandler().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getResponseHandler() {
        if (responseHandler == null) {
            responseHandler = new ArrayList<>();
        }
        return this.responseHandler;
    }

    /**
     * Obtient la valeur de la propriété abstractQuery.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link QueryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractQueryType }{@code >}
     *
     */
    @Override
    public AbstractQueryType getAbstractQuery() {
        if (abstractQuery != null) {
            return abstractQuery.getValue();
        }
        return null;
    }

    /**
     * Définit la valeur de la propriété abstractQuery.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link QueryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractQueryType }{@code >}
     *
     */
    public void setAbstractQuery(JAXBElement<? extends AbstractQueryType> value) {
        this.abstractQuery = value;
    }

    /**
     * Obtient la valeur de la propriété any.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    public Object getAny() {
        return any;
    }

    /**
     * Définit la valeur de la propriété any.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setAny(Object value) {
        this.any = value;
    }

    /**
     * Obtient la valeur de la propriété requestId.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getRequestId() {
        return requestId;
    }

    /**
     * Définit la valeur de la propriété requestId.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Obtient la valeur de la propriété outputFormat.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOutputFormat() {
        if (outputFormat == null) {
            return "application/xml";
        } else {
            return outputFormat;
        }
    }

    /**
     * Définit la valeur de la propriété outputFormat.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOutputFormat(String value) {
        this.outputFormat = value;
    }

    /**
     * Obtient la valeur de la propriété outputSchema.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOutputSchema() {
        if (outputSchema == null) {
            return "http://www.opengis.net/cat/csw/3.0";
        } else {
            return outputSchema;
        }
    }

    /**
     * Définit la valeur de la propriété outputSchema.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOutputSchema(String value) {
        this.outputSchema = value;
    }

    /**
     * Obtient la valeur de la propriété startPosition.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
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
     * Définit la valeur de la propriété startPosition.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    @Override
    public void setStartPosition(Integer value) {
        this.startPosition = value;
    }

    /**
     * Obtient la valeur de la propriété maxRecords.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
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
     * Définit la valeur de la propriété maxRecords.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    @Override
    public void setMaxRecords(Integer value) {
        this.maxRecords = value;
    }

    @Override
    public void setResultType(String resultType) {
        // not in 3.0.0 implementations
    }

    @Override
    public ResultType getResultType() {
        return ResultType.RESULTS;
    }

    /**
     * This method set a query constraint by a filter.
     * @param filter FilterType
     */
    @Override
    public void setFilterConstraint(final XMLFilter filter) {
        if (filter instanceof FilterType) {
            if (abstractQuery != null) {
                AbstractQueryType query = abstractQuery.getValue();
                query.setConstraint(new QueryConstraintType((FilterType) filter, "2.0.0"));
            }
        } else {
            throw new IllegalArgumentException("Not a v110 filter");
        }
    }

    @Override
    public void setCQLConstraint(final String CQLQuery) {
        if (abstractQuery != null) {
            AbstractQueryType query = abstractQuery.getValue();
            query.setConstraint(new QueryConstraintType(CQLQuery, "2.0.0"));
        }
    }

     @Override
    public void removeConstraint() {
        if (abstractQuery != null) {
            AbstractQueryType query = abstractQuery.getValue();
            query.setConstraint(null);
        }
    }

    @Override
    public void setTypeNames(final List<QName> typenames) {
        if (abstractQuery != null) {
            AbstractQueryType query = abstractQuery.getValue();
            query.setTypeNames(typenames);
        }
    }
}
