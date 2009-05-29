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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * <p>Java class for AttributeValueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AttributeValueType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}ExpressionType">
 *       &lt;sequence>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="DataType" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeValueType", propOrder =
{"content"})
public class AttributeValueType {

   @XmlMixed
   @XmlAnyElement(lax = true)
   private List<Object> content;

   @XmlAttribute(name = "DataType", required = true)
   @XmlSchemaType(name = "anyURI")
   private String dataType;

   @XmlAnyAttribute
   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   /**
    * Gets the value of the content property.
    * 
    */
   public List<Object> getContent() {
      if (content == null) {
         content = new ArrayList<Object>();
      }
      return this.content;
   }

   /**
    * Gets the value of the dataType property.
    */
   public String getDataType() {
      return dataType;
   }

   /**
    * Sets the value of the dataType property.
    */
   public void setDataType(String value) {
      this.dataType = value;
   }

   /**
    * Gets a map that contains attributes that aren't bound to any typed property on this class.
    * 
    */
   public Map<QName, String> getOtherAttributes() {
      return otherAttributes;
   }

}
