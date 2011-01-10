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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for ResourceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResourceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:context:schema:os}ResourceContent" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:context:schema:os}Attribute" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceType", propOrder =
{"resourceContent", "attribute"})
public class ResourceType {

   @XmlElement(name = "ResourceContent")
   private ResourceContentType resourceContent;

   @XmlElement(name = "Attribute")
   private List<AttributeType> attribute;

   /**
    * Gets the value of the resourceContent property.
    * 
    */
   public ResourceContentType getResourceContent() {
      return resourceContent;
   }

   /**
    * Sets the value of the resourceContent property.
    * 
    */
   public void setResourceContent(final ResourceContentType value) {
      this.resourceContent = value;
   }

   /**
    * Gets the value of the attribute property.
    * 
    */
   public List<AttributeType> getAttribute() {
      if (attribute == null) {
         attribute = new ArrayList<AttributeType>();
      }
      return this.attribute;
   }

}
