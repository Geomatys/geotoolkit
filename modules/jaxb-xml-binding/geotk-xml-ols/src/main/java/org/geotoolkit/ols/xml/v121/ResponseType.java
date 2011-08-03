/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines the response information returned from a service response.
 * 
 * <p>Java class for ResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractBodyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}ErrorList" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/xls}_ResponseParameters" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="requestID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="numberOfResponses" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseType", propOrder = {
    "errorList",
    "responseParameters"
})
public class ResponseType extends AbstractBodyType {

    @XmlElement(name = "ErrorList")
    private ErrorListType errorList;
    @XmlElementRef(name = "_ResponseParameters", namespace = "http://www.opengis.net/xls", type = JAXBElement.class)
    private JAXBElement<? extends AbstractResponseParametersType> responseParameters;
    @XmlAttribute(required = true)
    private String version;
    @XmlAttribute(required = true)
    private String requestID;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer numberOfResponses;

    /**
     * Gets the value of the errorList property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorListType }
     *     
     */
    public ErrorListType getErrorList() {
        return errorList;
    }

    /**
     * Sets the value of the errorList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorListType }
     *     
     */
    public void setErrorList(ErrorListType value) {
        this.errorList = value;
    }

    /**
     * Gets the value of the responseParameters property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractResponseParametersType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ReverseGeocodeResponseType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SLIAType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DetermineRouteResponseType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GetPortrayMapCapabilitiesResponseType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeocodeResponseType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DirectoryResponseType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PortrayMapResponseType }{@code >}
     *     
     */
    public JAXBElement<? extends AbstractResponseParametersType> getResponseParameters() {
        return responseParameters;
    }

    /**
     * Sets the value of the responseParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractResponseParametersType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ReverseGeocodeResponseType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SLIAType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DetermineRouteResponseType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GetPortrayMapCapabilitiesResponseType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeocodeResponseType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DirectoryResponseType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PortrayMapResponseType }{@code >}
     *     
     */
    public void setResponseParameters(JAXBElement<? extends AbstractResponseParametersType> value) {
        this.responseParameters = ((JAXBElement<? extends AbstractResponseParametersType> ) value);
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the requestID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * Sets the value of the requestID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestID(String value) {
        this.requestID = value;
    }

    /**
     * Gets the value of the numberOfResponses property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfResponses() {
        return numberOfResponses;
    }

    /**
     * Sets the value of the numberOfResponses property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfResponses(Integer value) {
        this.numberOfResponses = value;
    }

}
