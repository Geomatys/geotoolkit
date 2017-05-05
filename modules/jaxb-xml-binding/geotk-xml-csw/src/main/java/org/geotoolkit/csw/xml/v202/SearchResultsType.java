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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.csw.xml.SearchResults;


/**
 * Includes representations of result set members if maxRecords > 0.
 * The items must conform to one of the csw:Record views or a profile-specific representation.
 *
 *      resultSetId  - id of the result set (a URI).
 *
 *      elementSet  - The element set that has been returned (i.e., "brief", "summary", "full")
 *
 *      recordSchema  - schema reference for included records(URI)
 *
 *      numberOfRecordsMatched  - number of records matched by the query
 *
 *      numberOfRecordsReturned - number of records returned to client
 *
 *      nextRecord - position of next record in the result set (0 if no records remain).
 *
 *      expires - the time instant when the result set expires and is discarded (ISO 8601 format)
 *
 * <p>Java class for SearchResultsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SearchResultsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/cat/csw/2.0.2}AbstractRecord" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;any/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="resultSetId" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="elementSet" type="{http://www.opengis.net/cat/csw/2.0.2}ElementSetType" />
 *       &lt;attribute name="recordSchema" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="numberOfRecordsMatched" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="numberOfRecordsReturned" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="nextRecord" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="expires" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchResultsType", propOrder = {
    "any"
})
public class SearchResultsType implements SearchResults {

    @XmlAnyElement(lax = true)
    private List<Object> any;

    /**
     * Id of the result set (a URI).
     */
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String resultSetId;

    /**
     * The element set that has been returned (i.e., "brief", "summary", "full").
     */
    @XmlAttribute
    private ElementSetType elementSet;

    /**
     * Schema reference for included records(URI).
     */
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String recordSchema;

    /**
     * Number of records matched by the query.
     */
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    private int numberOfRecordsMatched;

    /**
     * Number of records returned to client.
     */
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    private int numberOfRecordsReturned;

    /**
     * Position of next record in the result set (0 if no records remain).
     */
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private int nextRecord;

    /**
     * The time instant when the result set expires and is discarded (ISO 8601 format)
     */
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar expires;

    @XmlTransient
    private ObjectFactory factory = new ObjectFactory();

    /**
     * An empty constructor used by JAXB
     */
    SearchResultsType() {

    }

    /**
     * build a new search results (HITS MODE).
     */
    public SearchResultsType(final String resultSetId, final ElementSetType elementSet, final int numberOfResultMatched, final int nextRecord) {
        this.resultSetId            = resultSetId;
        this.elementSet             = elementSet;
        this.numberOfRecordsMatched = numberOfResultMatched;
        this.nextRecord             = nextRecord;


    }

    /**
     * build a new search results. (RESULTS mode - OGCCORE mode).
     *
     */
    public SearchResultsType(final String resultSetId, final ElementSetType elementSet, final int numberOfResultMatched,
            final List<Object> records, final Integer numberOfRecordsReturned, final int nextRecord) {
        this.resultSetId             = resultSetId;
        this.elementSet              = elementSet;
        this.numberOfRecordsMatched  = numberOfResultMatched;
        this.numberOfRecordsReturned = numberOfRecordsReturned;
        this.nextRecord              = nextRecord;
        this.any                     = records;
    }

    /**
     * Gets the value of the any property.
     * (unModifiable)
     */
    @Override
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return any;
    }

    /**
     * Gets the value of the resultSetId property.
     */
    @Override
    public String getResultSetId() {
        return resultSetId;
    }

    /**
     * Gets the value of the elementSet property.
     */
    @Override
    public ElementSetType getElementSet() {
        return elementSet;
    }

    /**
     * Gets the value of the recordSchema property.
     */
    @Override
    public String getRecordSchema() {
        return recordSchema;
    }

    /**
     * Gets the value of the numberOfRecordsMatched property.
     */
    @Override
    public int getNumberOfRecordsMatched() {
        return numberOfRecordsMatched;
    }

    /**
     * Gets the value of the numberOfRecordsReturned property.
     */
    @Override
    public int getNumberOfRecordsReturned() {
        return numberOfRecordsReturned;
    }

    /**
     * Gets the value of the nextRecord property.
     */
    @Override
    public int getNextRecord() {
        return nextRecord;
    }

    /**
     * Gets the value of the expires property.
     */
    @Override
    public XMLGregorianCalendar getExpires() {
        return expires;
    }

    @Override
    public void setResultSetId(final String value) {
        this.resultSetId = value;
    }

    @Override
    public void setRecordSchema(final String value) {
        this.recordSchema = value;
    }

    @Override
    public void setNumberOfRecordsMatched(final int value) {
        this.numberOfRecordsMatched = value;
    }

    @Override
    public void setNumberOfRecordsReturned(final int value) {
        this.numberOfRecordsReturned = value;
    }

    @Override
    public void setNextRecord(final int value) {
        this.nextRecord = value;
    }

    @Override
    public void setExpires(final XMLGregorianCalendar value) {
        this.expires = value;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[SearchResultType] :").append('\n');
        if (resultSetId != null)
            s.append("resultSetId: ").append(resultSetId).append('\n');
        if (elementSet != null)
            s.append("elementSet:").append(elementSet.value()).append('\n');
        if (recordSchema != null)
            s.append("recordShema: ").append(recordSchema).append('\n');

        s.append("nbRec Matched = ").append(numberOfRecordsMatched).append(" nbRec Returned = ").append(numberOfRecordsReturned);
        s.append("next record = ").append(nextRecord).append('\n');

        if (expires != null) {
            s.append("expires at: ").append(expires);
        }

        if (any != null && !any.isEmpty()) {
            s.append("nb records: ").append(any.size());
        }
        return s.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SearchResultsType) {
            final SearchResultsType that  = (SearchResultsType) object;
            return Objects.equals(this.any,                     that.any)                     &&
                   Objects.equals(this.elementSet,              that.elementSet)              &&
                   Objects.equals(this.expires,                 that.expires)                 &&
                   Objects.equals(this.nextRecord,              that.nextRecord)              &&
                   Objects.equals(this.numberOfRecordsMatched,  that.numberOfRecordsMatched)  &&
                   Objects.equals(this.numberOfRecordsReturned, that.numberOfRecordsReturned) &&
                   Objects.equals(this.recordSchema,            that.recordSchema)            &&
                   Objects.equals(this.resultSetId,             that.resultSetId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.any != null ? this.any.hashCode() : 0);
        hash = 53 * hash + (this.resultSetId != null ? this.resultSetId.hashCode() : 0);
        hash = 53 * hash + this.elementSet.hashCode();
        hash = 53 * hash + (this.recordSchema != null ? this.recordSchema.hashCode() : 0);
        hash = 53 * hash + this.numberOfRecordsMatched;
        hash = 53 * hash + this.numberOfRecordsReturned;
        hash = 53 * hash + this.nextRecord;
        hash = 53 * hash + (this.expires != null ? this.expires.hashCode() : 0);
        return hash;
    }

}
