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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for ApplyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ApplyType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}ExpressionType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Expression" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="FunctionId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApplyType", propOrder =
{"expression"})
public class ApplyType extends ExpressionType {

   @XmlElementRef(name = "Expression", namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", type = JAXBElement.class)
   private List<JAXBElement<?>> expression;

   @XmlAttribute(name = "FunctionId", required = true)
   @XmlSchemaType(name = "anyURI")
   private String functionId;

   /**
    * Gets the value of the expression property.
    */
   public List<JAXBElement<?>> getExpression() {
      if (expression == null) {
         expression = new ArrayList<JAXBElement<?>>();
      }
      return this.expression;
   }

   /**
    * Gets the value of the functionId property.
    */
   public String getFunctionId() {
      return functionId;
   }

   /**
    * Sets the value of the functionId property.
    */
   public void setFunctionId(final String value) {
      this.functionId = value;
   }

}
