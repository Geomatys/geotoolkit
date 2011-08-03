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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Defines the core information of a service request message.
 * 
 * <p>Java class for RequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractBodyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}_RequestParameters" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="methodName" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="requestID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="maximumResponses" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestType", propOrder = {
    "requestParameters"
})
public class RequestType extends AbstractBodyType {

    @XmlElementRef(name = "_RequestParameters", namespace = "http://www.opengis.net/xls", type = JAXBElement.class)
    private JAXBElement<? extends AbstractRequestParametersType> requestParameters;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    private String methodName;
    @XmlAttribute(required = true)
    private String version;
    @XmlAttribute(required = true)
    private String requestID;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer maximumResponses;

    /**
     * Gets the value of the requestParameters property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SLIRType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GetPortrayMapCapabilitiesRequestType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeocodeRequestType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractRequestParametersType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DetermineRouteRequestType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PortrayMapRequestType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ReverseGeocodeRequestType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DirectoryRequestType }{@code >}
     *     
     */
    public JAXBElement<? extends AbstractRequestParametersType> getRequestParameters() {
        return requestParameters;
    }

    /**
     * Sets the value of the requestParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SLIRType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GetPortrayMapCapabilitiesRequestType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeocodeRequestType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractRequestParametersType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DetermineRouteRequestType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PortrayMapRequestType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ReverseGeocodeRequestType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DirectoryRequestType }{@code >}
     *     
     */
    public void setRequestParameters(JAXBElement<? extends AbstractRequestParametersType> value) {
        this.requestParameters = ((JAXBElement<? extends AbstractRequestParametersType> ) value);
    }

    /**
     * Gets the value of the methodName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the value of the methodName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethodName(String value) {
        this.methodName = value;
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
     * Gets the value of the maximumResponses property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaximumResponses() {
        return maximumResponses;
    }

    /**
     * Sets the value of the maximumResponses property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaximumResponses(Integer value) {
        this.maximumResponses = value;
    }

}
