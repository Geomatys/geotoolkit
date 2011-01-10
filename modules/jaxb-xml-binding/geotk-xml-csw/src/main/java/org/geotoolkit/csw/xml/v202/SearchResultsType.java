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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.csw.xml.SearchResults;
import org.geotoolkit.util.Utilities;


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
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchResultsType", propOrder = {
    "abstractRecord",
    "any"
})
public class SearchResultsType implements SearchResults {

    @XmlElementRef(name = "AbstractRecord", namespace = "http://www.opengis.net/cat/csw/2.0.2", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractRecordType>> abstractRecord;
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
            final List<AbstractRecordType> records, final Integer numberOfRecordsReturned, final int nextRecord) {
        this.resultSetId             = resultSetId;
        this.elementSet              = elementSet;
        this.numberOfRecordsMatched  = numberOfResultMatched;
        this.numberOfRecordsReturned = numberOfRecordsReturned;
        this.nextRecord              = nextRecord;
        
        abstractRecord = new ArrayList<JAXBElement<? extends AbstractRecordType>>(); 
        for (int i = 0; i < records.size(); i++) {
            
            AbstractRecordType record = records.get(i);
            
            if (record == null) continue;
            
            if (record instanceof BriefRecordType) {
                abstractRecord.add(factory.createBriefRecord((BriefRecordType)record));
            } else if (record instanceof RecordType) {
                abstractRecord.add(factory.createRecord((RecordType)record));
            } else if (record instanceof SummaryRecordType) {
                abstractRecord.add(factory.createSummaryRecord((SummaryRecordType)record));
            } else if (record instanceof DCMIRecordType) {
                abstractRecord.add(factory.createDCMIRecord((DCMIRecordType)record));
            } else {
                throw new IllegalArgumentException(" unknow AbstractRecord subType:" + record.getClass().getSimpleName());
            }
        }
        
    }
    
    /**
     * build a new search results. (RESULTS mode - ISO19139 mode).
     * 
     */
    public SearchResultsType(final String resultSetId, final ElementSetType elementSet, final int numberOfResultMatched,
            final Integer numberOfRecordsReturned, final List<Object> records, final int nextRecord) {
        this.resultSetId             = resultSetId;
        this.elementSet              = elementSet;
        this.numberOfRecordsMatched  = numberOfResultMatched;
        this.numberOfRecordsReturned = numberOfRecordsReturned;
        this.any                     = records;
        this.nextRecord              = nextRecord;
        
    }
    
    
    /**
     * Gets the value of the abstractRecord property.
     * (unModifiable)
     */
    public List<JAXBElement<? extends AbstractRecordType>> getAbstractRecord() {
        if (abstractRecord == null) {
            abstractRecord = new ArrayList<JAXBElement<? extends AbstractRecordType>>();
        }
        return Collections.unmodifiableList(abstractRecord);
    }

    /**
     * Gets the value of the any property.
     * (unModifiable)
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return Collections.unmodifiableList(any);
    }

    /**
     * Gets the value of the resultSetId property.
     */
    public String getResultSetId() {
        return resultSetId;
    }

    /**
     * Gets the value of the elementSet property.
     */
    public ElementSetType getElementSet() {
        return elementSet;
    }

    /**
     * Gets the value of the recordSchema property.
     */
    public String getRecordSchema() {
        return recordSchema;
    }

    /**
     * Gets the value of the numberOfRecordsMatched property.
     */
    public int getNumberOfRecordsMatched() {
        return numberOfRecordsMatched;
    }

    /**
     * Gets the value of the numberOfRecordsReturned property.
     */
    public int getNumberOfRecordsReturned() {
        return numberOfRecordsReturned;
    }

    /**
     * Gets the value of the nextRecord property.
     */
    public int getNextRecord() {
        return nextRecord;
    }

    
    /**
     * Gets the value of the expires property.
     */
    public XMLGregorianCalendar getExpires() {
        return expires;
    }
    
    public void setResultSetId(final String value) {
        this.resultSetId = value;
    }

    public void setRecordSchema(final String value) {
        this.recordSchema = value;
    }

    public void setNumberOfRecordsMatched(final int value) {
        this.numberOfRecordsMatched = value;
    }

    public void setNumberOfRecordsReturned(final int value) {
        this.numberOfRecordsReturned = value;
    }

    public void setNextRecord(final int value) {
        this.nextRecord = value;
    }

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
        
        if (abstractRecord != null && abstractRecord.size() != 0) {
            s.append("nb CSW records: ").append(abstractRecord.size());
            
        }
        if (any != null && any.size() != 0) {
            s.append("nb Other records: ").append(any.size());
        }
        return s.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SearchResultsType) {
            SearchResultsType that  = (SearchResultsType) object;
            boolean abstractRecordB = false;
            if (this.abstractRecord != null && that.abstractRecord != null) {
                if (this.abstractRecord.size() == that.abstractRecord.size()) {
                    abstractRecordB = true;
                    for (int i = 0; i < this.abstractRecord.size(); i++) {
                        if (!this.abstractRecord.get(i).getValue().equals(that.abstractRecord.get(i).getValue())) {
                            abstractRecordB = false;
                            break;
                        }
                    }
                }
            
            } else if (this.abstractRecord == null && that.abstractRecord == null) {
                abstractRecordB = true;
            }

            
            return abstractRecordB                                                              &&
                   Utilities.equals(this.any,                     that.any)                     &&
                   Utilities.equals(this.elementSet,              that.elementSet)              &&
                   Utilities.equals(this.expires,                 that.expires)                 &&
                   Utilities.equals(this.nextRecord,              that.nextRecord)              &&
                   Utilities.equals(this.numberOfRecordsMatched,  that.numberOfRecordsMatched)  &&
                   Utilities.equals(this.numberOfRecordsReturned, that.numberOfRecordsReturned) &&
                   Utilities.equals(this.recordSchema,            that.recordSchema)            &&
                   Utilities.equals(this.resultSetId,             that.resultSetId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.abstractRecord != null ? this.abstractRecord.hashCode() : 0);
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
