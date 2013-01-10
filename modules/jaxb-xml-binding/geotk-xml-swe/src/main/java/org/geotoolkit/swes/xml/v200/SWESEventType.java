/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swes.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.ows.xml.v110.LanguageStringType;
import org.geotoolkit.w3c.adressing.xml.v2005.EndpointReferenceType;


/**
 * <p>Java class for SWESEventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SWESEventType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}AbstractSWESType">
 *       &lt;sequence>
 *         &lt;element name="eventTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="code" type="{http://www.opengis.net/swes/2.0}EventCodeType"/>
 *         &lt;element name="message" type="{http://www.opengis.net/ows/1.1}LanguageStringType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="service">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.w3.org/2005/08/addressing}EndpointReference"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SWESEventType", propOrder = {
    "eventTime",
    "code",
    "message",
    "service"
})
@XmlSeeAlso({
    OfferingChangedType.class,
    SensorChangedType.class
})
public class SWESEventType
    extends AbstractSWESType
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar eventTime;
    @XmlElement(required = true)
    private String code;
    private List<LanguageStringType> message;
    @XmlElement(required = true)
    private SWESEventType.Service service;

    /**
     * Gets the value of the eventTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEventTime() {
        return eventTime;
    }

    /**
     * Sets the value of the eventTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEventTime(XMLGregorianCalendar value) {
        this.eventTime = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link LanguageStringType }
     * 
     */
    public List<LanguageStringType> getMessage() {
        if (message == null) {
            message = new ArrayList<LanguageStringType>();
        }
        return this.message;
    }

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link SWESEventType.Service }
     *     
     */
    public SWESEventType.Service getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link SWESEventType.Service }
     *     
     */
    public void setService(SWESEventType.Service value) {
        this.service = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.w3.org/2005/08/addressing}EndpointReference"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "endpointReference"
    })
    public static class Service {

        @XmlElement(name = "EndpointReference", namespace = "http://www.w3.org/2005/08/addressing", required = true)
        private EndpointReferenceType endpointReference;

        /**
         * Gets the value of the endpointReference property.
         * 
         * @return
         *     possible object is
         *     {@link EndpointReferenceType }
         *     
         */
        public EndpointReferenceType getEndpointReference() {
            return endpointReference;
        }

        /**
         * Sets the value of the endpointReference property.
         * 
         * @param value
         *     allowed object is
         *     {@link EndpointReferenceType }
         *     
         */
        public void setEndpointReference(EndpointReferenceType value) {
            this.endpointReference = value;
        }

    }

}
