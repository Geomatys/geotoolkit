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
package org.geotoolkit.xacml.xml.policy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for AttributeSelectorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AttributeSelectorType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}ExpressionType">
 *       &lt;attribute name="RequestContextPath" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="DataType" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="MustBePresent" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeSelectorType")
public class AttributeSelectorType extends ExpressionType {

   @XmlAttribute(name = "RequestContextPath", required = true)
   private String requestContextPath;

   @XmlAttribute(name = "DataType", required = true)
   @XmlSchemaType(name = "anyURI")
   private String dataType;

   @XmlAttribute(name = "MustBePresent")
   private Boolean mustBePresent;

   /**
    * Gets the value of the requestContextPath property.
    */
   public String getRequestContextPath() {
      return requestContextPath;
   }

   /**
    * Sets the value of the requestContextPath property.
    */
   public void setRequestContextPath(String value) {
      this.requestContextPath = value;
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
    * Gets the value of the mustBePresent property.
    */
   public boolean isMustBePresent() {
      if (mustBePresent == null) {
         return false;
      } else {
         return mustBePresent;
      }
   }

   /**
    * Sets the value of the mustBePresent property.
    */
   public void setMustBePresent(Boolean value) {
      this.mustBePresent = value;
   }

}
