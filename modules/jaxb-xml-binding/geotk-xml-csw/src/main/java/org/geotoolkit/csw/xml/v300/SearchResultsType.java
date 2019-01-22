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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.csw.xml.SearchResults;


/**
 *
 *             Includes representations of result set members if maxRecords > 0.
 *             The items must conform to one of the csw30:Record views or a
 *             profile-specific representation.
 *
 *             resultSetId             - id of the result set (a URI).
 *             elementSet              - The element set that has been returned
 *                                       (e.g., "brief", "summary", "full")
 *             recordSchema            - schema reference for included records(URI)
 *             numberOfRecordsMatched  - number of records matched by the query
 *             numberOfRecordsReturned - number of records returned to client
 *             nextRecord              - position of next record in the result set
 *                                       (0 if no records remain).
 *             expires                 - the time instant when the result set
 *                                       expires and is discarded (ISO8601 format)
 *             elapsedTime             - runtime information of the search
 *                                       within the federated catalogue
 *
 *
 * <p>Classe Java pour SearchResultsType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="SearchResultsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/cat/csw/3.0}AbstractRecord" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/cat/csw/3.0}FederatedSearchResultBase" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="resultSetId" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="elementSet" type="{http://www.opengis.net/cat/csw/3.0}ElementSetType" />
 *       &lt;attribute name="recordSchema" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="numberOfRecordsMatched" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="numberOfRecordsReturned" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="nextRecord" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="expires" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="elapsedTime" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *       &lt;attribute name="status" type="{http://www.opengis.net/cat/csw/3.0}ResultsStatusType" default="subset" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchResultsType", propOrder = {
    "abstractRecord",
    "any",
    "federatedSearchResultBase"
})
public class SearchResultsType implements SearchResults {

    @XmlElementRef(name = "AbstractRecord", namespace = "http://www.opengis.net/cat/csw/3.0", type = JAXBElement.class, required = false)
    protected List<JAXBElement<? extends AbstractRecordType>> abstractRecord;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlElementRef(name = "FederatedSearchResultBase", namespace = "http://www.opengis.net/cat/csw/3.0", type = JAXBElement.class, required = false)
    protected List<JAXBElement<? extends FederatedSearchResultBaseType>> federatedSearchResultBase;
    @XmlAttribute(name = "resultSetId")
    @XmlSchemaType(name = "anyURI")
    protected String resultSetId;
    @XmlAttribute(name = "elementSet")
    protected String elementSet;
    @XmlAttribute(name = "recordSchema")
    @XmlSchemaType(name = "anyURI")
    protected String recordSchema;
    @XmlAttribute(name = "numberOfRecordsMatched", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Integer numberOfRecordsMatched;
    @XmlAttribute(name = "numberOfRecordsReturned", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Integer numberOfRecordsReturned;
    @XmlAttribute(name = "nextRecord")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Integer nextRecord;
    @XmlAttribute(name = "expires")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expires;
    @XmlAttribute(name = "elapsedTime")
    @XmlSchemaType(name = "unsignedLong")
    protected Integer elapsedTime;
    @XmlAttribute(name = "status")
    protected ResultsStatusType status;

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
        this.numberOfRecordsMatched = numberOfResultMatched;
        this.nextRecord             = nextRecord;
        if (elementSet != null) {
            this.elementSet         = elementSet.value();
        }
    }

    /**
     * build a new search results. (RESULTS mode - OGCCORE mode).
     *
     */
    public SearchResultsType(final String resultSetId, final ElementSetType elementSet, final int numberOfResultMatched,
            final List<Object> records, final Integer numberOfRecordsReturned, final int nextRecord) {
        this.resultSetId             = resultSetId;
        this.numberOfRecordsMatched  = numberOfResultMatched;
        this.numberOfRecordsReturned = numberOfRecordsReturned;
        this.nextRecord              = nextRecord;
        this.any                     = records;
        if (elementSet != null) {
            this.elementSet          = elementSet.value();
        }
    }

    /**
     * Gets the value of the abstractRecord property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractRecord property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractRecord().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link RecordType }{@code >}
     * {@link JAXBElement }{@code <}{@link DCMIRecordType }{@code >}
     * {@link JAXBElement }{@code <}{@link BriefRecordType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractRecordType }{@code >}
     * {@link JAXBElement }{@code <}{@link SummaryRecordType }{@code >}
     *
     *
     */
    public List<JAXBElement<? extends AbstractRecordType>> getAbstractRecord() {
        if (abstractRecord == null) {
            abstractRecord = new ArrayList<>();
        }
        return this.abstractRecord;
    }

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    @Override
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }

    /**
     * Gets the value of the federatedSearchResultBase property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the federatedSearchResultBase property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFederatedSearchResultBase().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link FederatedSearchResultBaseType }{@code >}
     * {@link JAXBElement }{@code <}{@link FederatedSearchResultType }{@code >}
     * {@link JAXBElement }{@code <}{@link FederatedExceptionType }{@code >}
     *
     *
     */
    public List<JAXBElement<? extends FederatedSearchResultBaseType>> getFederatedSearchResultBase() {
        if (federatedSearchResultBase == null) {
            federatedSearchResultBase = new ArrayList<>();
        }
        return this.federatedSearchResultBase;
    }

    /**
     * Obtient la valeur de la propriété resultSetId.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getResultSetId() {
        return resultSetId;
    }

    /**
     * Définit la valeur de la propriété resultSetId.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setResultSetId(String value) {
        this.resultSetId = value;
    }

    /**
     * Obtient la valeur de la propriété elementSet.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public ElementSetType getElementSet() {
        if (elementSet != null) {
            return ElementSetType.fromValue(elementSet);
        }
        return null;
    }

    /**
     * Définit la valeur de la propriété elementSet.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setElementSet(String value) {
        this.elementSet = value;
    }

    /**
     * Obtient la valeur de la propriété recordSchema.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getRecordSchema() {
        return recordSchema;
    }

    /**
     * Définit la valeur de la propriété recordSchema.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setRecordSchema(String value) {
        this.recordSchema = value;
    }

    /**
     * Obtient la valeur de la propriété numberOfRecordsMatched.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    @Override
    public int getNumberOfRecordsMatched() {
        if (numberOfRecordsMatched == null) {
            return 0;
        }
        return numberOfRecordsMatched;
    }

    /**
     * Définit la valeur de la propriété numberOfRecordsMatched.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    @Override
    public void setNumberOfRecordsMatched(int value) {
        this.numberOfRecordsMatched = value;
    }

    /**
     * Obtient la valeur de la propriété numberOfRecordsReturned.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    @Override
    public int getNumberOfRecordsReturned() {
        if (numberOfRecordsReturned == null) {
            return 0;
        }
        return numberOfRecordsReturned;
    }

    /**
     * Définit la valeur de la propriété numberOfRecordsReturned.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    @Override
    public void setNumberOfRecordsReturned(int value) {
        this.numberOfRecordsReturned = value;
    }

    /**
     * Obtient la valeur de la propriété nextRecord.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    @Override
    public int getNextRecord() {
        if (nextRecord == null) {
            return 0;
        }
        return nextRecord;
    }

    /**
     * Définit la valeur de la propriété nextRecord.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    @Override
    public void setNextRecord(int value) {
        this.nextRecord = value;
    }

    /**
     * Obtient la valeur de la propriété expires.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    @Override
    public XMLGregorianCalendar getExpires() {
        return expires;
    }

    /**
     * Définit la valeur de la propriété expires.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    @Override
    public void setExpires(XMLGregorianCalendar value) {
        this.expires = value;
    }

    /**
     * Obtient la valeur de la propriété elapsedTime.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Définit la valeur de la propriété elapsedTime.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setElapsedTime(Integer value) {
        this.elapsedTime = value;
    }

    /**
     * Obtient la valeur de la propriété status.
     *
     * @return
     *     possible object is
     *     {@link ResultsStatusType }
     *
     */
    public ResultsStatusType getStatus() {
        if (status == null) {
            return ResultsStatusType.SUBSET;
        } else {
            return status;
        }
    }

    /**
     * Définit la valeur de la propriété status.
     *
     * @param value
     *     allowed object is
     *     {@link ResultsStatusType }
     *
     */
    public void setStatus(ResultsStatusType value) {
        this.status = value;
    }

}
