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
package org.geotoolkit.xacml.xml.context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.xacml.xml.policy.ObligationsType;

/**
 * <p>Java class for ResultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:context:schema:os}Decision"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:context:schema:os}Status" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Obligations" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ResourceId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultType", propOrder =
{"decision", "status", "obligations"})
public class ResultType {

   @XmlElement(name = "Decision", required = true)
   private DecisionType decision;

   @XmlElement(name = "Status")
   private StatusType status;

   @XmlElement(name = "Obligations", namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os")
   private ObligationsType obligations;

   @XmlAttribute(name = "ResourceId")
   private String resourceId;

   /**
    * Gets the value of the decision property.
    * 
    */
   public DecisionType getDecision() {
      return decision;
   }

   /**
    * Sets the value of the decision property.
    * 
    */
   public void setDecision(DecisionType value) {
      this.decision = value;
   }

   /**
    * Gets the value of the status property.
    * 
    */
   public StatusType getStatus() {
      return status;
   }

   /**
    * Sets the value of the status property.
    * 
    */
   public void setStatus(StatusType value) {
      this.status = value;
   }

   /**
    * Gets the value of the obligations property.
    * 
    */
   public ObligationsType getObligations() {
      return obligations;
   }

   /**
    * Sets the value of the obligations property.
    * 
    */
   public void setObligations(ObligationsType value) {
      this.obligations = value;
   }

   /**
    * Gets the value of the resourceId property.
    * 
    */
   public String getResourceId() {
      return resourceId;
   }

   /**
    * Sets the value of the resourceId property.
    * 
    */
   public void setResourceId(String value) {
      this.resourceId = value;
   }

}
