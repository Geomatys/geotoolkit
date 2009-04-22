/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ows.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Complete reference to a remote resource that needs to be retrieved from an OWS using an XML-encoded operation request. This element shall be used, within an InputData or Manifest element that is used for input data, when that input data needs to be retrieved from another web service using a XML-encoded OWS operation request. This element shall not be used for local payload input data or for requesting the resource from a web server using HTTP Get. 
 * 
 * <p>Java class for ServiceReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceReferenceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}ReferenceType">
 *       &lt;choice>
 *         &lt;element name="RequestMessage" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="RequestMessageReference" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceReferenceType", propOrder = {
    "requestMessage",
    "requestMessageReference"
})
public class ServiceReferenceType extends ReferenceType {

    @XmlElement(name = "RequestMessage")
    private Object requestMessage;
    @XmlElement(name = "RequestMessageReference")
    @XmlSchemaType(name = "anyURI")
    private String requestMessageReference;

    /**
     * Gets the value of the requestMessage property.
     * 
     */
    public Object getRequestMessage() {
        return requestMessage;
    }

    /**
     * Sets the value of the requestMessage property.
     * 
     */
    public void setRequestMessage(Object value) {
        this.requestMessage = value;
    }

    /**
     * Gets the value of the requestMessageReference property.
     * 
     */
    public String getRequestMessageReference() {
        return requestMessageReference;
    }

    /**
     * Sets the value of the requestMessageReference property.
     * 
     */
    public void setRequestMessageReference(String value) {
        this.requestMessageReference = value;
    }

}
