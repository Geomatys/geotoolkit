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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for CombinerParameterType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CombinerParameterType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}AttributeValue"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ParameterName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CombinerParameterType", propOrder =
{"attributeValue"})
public class CombinerParameterType {

   @XmlElement(name = "AttributeValue", required = true)
   private AttributeValueType attributeValue;

   @XmlAttribute(name = "ParameterName", required = true)
   private String parameterName;

   /**
    * Gets the value of the attributeValue property.
    */
   public AttributeValueType getAttributeValue() {
      return attributeValue;
   }

   /**
    * Sets the value of the attributeValue property.
    */
   public void setAttributeValue(AttributeValueType value) {
      this.attributeValue = value;
   }

   /**
    * Gets the value of the parameterName property.
    */
   public String getParameterName() {
      return parameterName;
   }

   /**
    * Sets the value of the parameterName property.
    */
   public void setParameterName(String value) {
      this.parameterName = value;
   }

}
