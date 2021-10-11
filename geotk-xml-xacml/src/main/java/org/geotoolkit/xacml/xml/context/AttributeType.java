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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for AttributeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:context:schema:os}AttributeValue" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="AttributeId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="DataType" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="Issuer" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeType", propOrder =
{"attributeValue"})
public class AttributeType {

   @XmlElement(name = "AttributeValue", required = true)
   private List<AttributeValueType> attributeValue;

   @XmlAttribute(name = "AttributeId", required = true)
   @XmlSchemaType(name = "anyURI")
   private String attributeId;

   @XmlAttribute(name = "DataType", required = true)
   @XmlSchemaType(name = "anyURI")
   private String dataType;

   @XmlAttribute(name = "Issuer")
   private String issuer;

   /**
    * Gets the value of the attributeValue property.
    *
    */
   public List<AttributeValueType> getAttributeValue() {
      if (attributeValue == null) {
         attributeValue = new ArrayList<AttributeValueType>();
      }
      return this.attributeValue;
   }

   /**
    * Gets the value of the attributeId property.
    *
    */
   public String getAttributeId() {
      return attributeId;
   }

   /**
    * Sets the value of the attributeId property.
    *
    */
   public void setAttributeId(final String value)
   {
      this.attributeId = value;
   }

   /**
    * Gets the value of the dataType property.
    *
    */
   public String getDataType() {
      return dataType;
   }

   /**
    * Sets the value of the dataType property.
    *
    */
   public void setDataType(final String value) {
      this.dataType = value;
   }

   /**
    * Gets the value of the issuer property.
    *
    */
   public String getIssuer() {
      return issuer;
   }

   /**
    * Sets the value of the issuer property.
    *
    */
   public void setIssuer(final String value) {
      this.issuer = value;
   }

}
