/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ValueCollectionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValueCollectionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}member" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}additionalValues" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}truncatedResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/wfs/2.0}StandardResponseParameters"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValueCollectionType", propOrder = {
    "member",
    "additionalValues",
    "truncatedResponse"
})
public class ValueCollectionType {

    private List<MemberPropertyType> member;
    private AdditionalValues additionalValues;
    private TruncatedResponse truncatedResponse;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar timeStamp;
    @XmlAttribute(required = true)
    private String numberMatched;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    private BigInteger numberReturned;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String next;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String previous;

    /**
     * Gets the value of the member property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the member property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMember().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MemberPropertyType }
     * 
     * 
     */
    public List<MemberPropertyType> getMember() {
        if (member == null) {
            member = new ArrayList<MemberPropertyType>();
        }
        return this.member;
    }

    /**
     * Gets the value of the additionalValues property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalValues }
     *     
     */
    public AdditionalValues getAdditionalValues() {
        return additionalValues;
    }

    /**
     * Sets the value of the additionalValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalValues }
     *     
     */
    public void setAdditionalValues(AdditionalValues value) {
        this.additionalValues = value;
    }

    /**
     * Gets the value of the truncatedResponse property.
     * 
     * @return
     *     possible object is
     *     {@link TruncatedResponse }
     *     
     */
    public TruncatedResponse getTruncatedResponse() {
        return truncatedResponse;
    }

    /**
     * Sets the value of the truncatedResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link TruncatedResponse }
     *     
     */
    public void setTruncatedResponse(TruncatedResponse value) {
        this.truncatedResponse = value;
    }

    /**
     * Gets the value of the timeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the value of the timeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimeStamp(XMLGregorianCalendar value) {
        this.timeStamp = value;
    }

    /**
     * Gets the value of the numberMatched property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberMatched() {
        return numberMatched;
    }

    /**
     * Sets the value of the numberMatched property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberMatched(String value) {
        this.numberMatched = value;
    }

    /**
     * Gets the value of the numberReturned property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberReturned() {
        return numberReturned;
    }

    /**
     * Sets the value of the numberReturned property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberReturned(BigInteger value) {
        this.numberReturned = value;
    }

    /**
     * Gets the value of the next property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNext() {
        return next;
    }

    /**
     * Sets the value of the next property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNext(String value) {
        this.next = value;
    }

    /**
     * Gets the value of the previous property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrevious() {
        return previous;
    }

    /**
     * Sets the value of the previous property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrevious(String value) {
        this.previous = value;
    }

}
