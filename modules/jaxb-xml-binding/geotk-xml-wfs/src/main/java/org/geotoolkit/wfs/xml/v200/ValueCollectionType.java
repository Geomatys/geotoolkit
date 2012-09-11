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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wfs.xml.ValueCollection;


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
public class ValueCollectionType implements ValueCollection {

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
    private int numberReturned;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String next;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String previous;

    public ValueCollectionType() {

    }

    public ValueCollectionType(final Integer numberOfFeatures, final XMLGregorianCalendar timeStamp) {
        this.numberReturned = numberOfFeatures;
        this.timeStamp = timeStamp;
    }

    /**
     * Gets the value of the member property.
     *
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
    @Override
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
    @Override
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
     *     {@link int }
     *
     */
    @Override
    public int getNumberReturned() {
        return numberReturned;
    }

    /**
     * Sets the value of the numberReturned property.
     *
     * @param value
     *     allowed object is
     *     {@link int }
     *
     */
    public void setNumberReturned(int value) {
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (additionalValues != null) {
            sb.append("additionalValues:").append(additionalValues).append('\n');
        }
        if (member != null && !member.isEmpty()) {
            sb.append("member:\n");
            for (MemberPropertyType m : member) {
                sb.append(m).append('\n');
            }
        }
        if (next != null) {
            sb.append("next:").append(next).append('\n');
        }
        sb.append("numberReturned:").append(numberReturned).append('\n');
        if (numberMatched != null) {
            sb.append("numberMatched:").append(numberMatched).append('\n');
        }
        if (previous != null) {
            sb.append("previous:").append(previous).append('\n');
        }
        if (timeStamp != null) {
            sb.append("timeStamp:").append(timeStamp).append('\n');
        }
        if (truncatedResponse != null) {
            sb.append("truncatedResponse:").append(truncatedResponse).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ValueCollectionType) {
            final ValueCollectionType that = (ValueCollectionType) obj;
            return Objects.equals(this.member,            that.member) &&
                   Objects.equals(this.additionalValues,  that.additionalValues) &&
                   Objects.equals(this.next,              that.next) &&
                   Objects.equals(this.numberMatched,     that.numberMatched) &&
                   Objects.equals(this.numberReturned,    that.numberReturned) &&
                   Objects.equals(this.previous,          that.previous) &&
                   Objects.equals(this.timeStamp,         that.timeStamp) &&
                   Objects.equals(this.truncatedResponse, that.truncatedResponse);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.additionalValues != null ? this.additionalValues.hashCode() : 0);
        hash = 23 * hash + (this.truncatedResponse != null ? this.truncatedResponse.hashCode() : 0);
        hash = 23 * hash + (this.member != null ? this.member.hashCode() : 0);
        hash = 23 * hash + (this.timeStamp != null ? this.timeStamp.hashCode() : 0);
        hash = 23 * hash + (this.numberMatched != null ? this.numberMatched.hashCode() : 0);
        hash = 23 * hash + this.numberReturned;
        hash = 23 * hash + (this.next != null ? this.next.hashCode() : 0);
        hash = 23 * hash + (this.previous != null ? this.previous.hashCode() : 0);
        return hash;
    }
}
