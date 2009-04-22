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
package org.geotoolkit.xacml.xml.context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for StatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:context:schema:os}StatusCode"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:context:schema:os}StatusMessage" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:context:schema:os}StatusDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusType", propOrder =
{"statusCode", "statusMessage", "statusDetail"})
public class StatusType {

   @XmlElement(name = "StatusCode", required = true)
   private StatusCodeType statusCode;

   @XmlElement(name = "StatusMessage")
   private String statusMessage;

   @XmlElement(name = "StatusDetail")
   private StatusDetailType statusDetail;

   /**
    * Gets the value of the statusCode property.
    * 
    */
   public StatusCodeType getStatusCode() {
      return statusCode;
   }

   /**
    * Sets the value of the statusCode property.
    * 
    */
   public void setStatusCode(StatusCodeType value) {
      this.statusCode = value;
   }

   /**
    * Gets the value of the statusMessage property.
    * 
    */
   public String getStatusMessage() {
      return statusMessage;
   }

   /**
    * Sets the value of the statusMessage property.
    * 
    */
   public void setStatusMessage(String value) {
      this.statusMessage = value;
   }

   /**
    * Gets the value of the statusDetail property.
    * 
    */
   public StatusDetailType getStatusDetail() {
      return statusDetail;
   }

   /**
    * Sets the value of the statusDetail property.
    */
   public void setStatusDetail(StatusDetailType value) {
      this.statusDetail = value;
   }

}
