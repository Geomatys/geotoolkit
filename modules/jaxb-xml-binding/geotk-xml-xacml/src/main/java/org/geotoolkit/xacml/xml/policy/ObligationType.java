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
package org.geotoolkit.xacml.xml.policy;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for ObligationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ObligationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}AttributeAssignment" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ObligationId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="FulfillOn" use="required" type="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}EffectType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObligationType", propOrder =
{"attributeAssignment"})
public class ObligationType {

   @XmlElement(name = "AttributeAssignment")
   private List<AttributeAssignmentType> attributeAssignment;

   @XmlAttribute(name = "ObligationId", required = true)
   @XmlSchemaType(name = "anyURI")
   private String obligationId;

   @XmlAttribute(name = "FulfillOn", required = true)
   private EffectType fulfillOn;

   /**
    * Gets the value of the attributeAssignment property.
    */
   public List<AttributeAssignmentType> getAttributeAssignment() {
      if (attributeAssignment == null) {
         attributeAssignment = new ArrayList<AttributeAssignmentType>();
      }
      return this.attributeAssignment;
   }

   /**
    * Gets the value of the obligationId property.
    */
   public String getObligationId() {
      return obligationId;
   }

   /**
    * Sets the value of the obligationId property.
    */
   public void setObligationId(final String value) {
      this.obligationId = value;
   }

   /**
    * Gets the value of the fulfillOn property.
    */
   public EffectType getFulfillOn() {
      return fulfillOn;
   }

   /**
    * Sets the value of the fulfillOn property.
    */
   public void setFulfillOn(final EffectType value) {
      this.fulfillOn = value;
   }

}
