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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for ActionMatchType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ActionMatchType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}AttributeValue"/>
 *         &lt;choice>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}ActionAttributeDesignator"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}AttributeSelector"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="MatchId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionMatchType", propOrder =
{"attributeValue", "actionAttributeDesignator", "attributeSelector"})
public class ActionMatchType {

   @XmlElement(name = "AttributeValue", required = true)
   private AttributeValueType attributeValue;

   @XmlElement(name = "ActionAttributeDesignator")
   private AttributeDesignatorType actionAttributeDesignator;

   @XmlElement(name = "AttributeSelector")
   private AttributeSelectorType attributeSelector;

   @XmlAttribute(name = "MatchId", required = true)
   @XmlSchemaType(name = "anyURI")
   private String matchId;

   /**
    * Gets the value of the attributeValue property.
    *
    */
   public AttributeValueType getAttributeValue() {
      return attributeValue;
   }

   /**
    * Sets the value of the attributeValue property.
    *
    */
   public void setAttributeValue(final AttributeValueType value) {
      this.attributeValue = value;
   }

   /**
    * Gets the value of the actionAttributeDesignator property.
    *
    */
   public AttributeDesignatorType getActionAttributeDesignator() {
      return actionAttributeDesignator;
   }

   /**
    * Sets the value of the actionAttributeDesignator property.
    *
    */
   public void setActionAttributeDesignator(final AttributeDesignatorType value) {
      this.actionAttributeDesignator = value;
   }

   /**
    * Gets the value of the attributeSelector property.
    *
    */
   public AttributeSelectorType getAttributeSelector() {
      return attributeSelector;
   }

   /**
    * Sets the value of the attributeSelector property.
    *
    */
   public void setAttributeSelector(final AttributeSelectorType value) {
      this.attributeSelector = value;
   }

   /**
    * Gets the value of the matchId property.
    *
    */
   public String getMatchId() {
      return matchId;
   }

   /**
    * Sets the value of the matchId property.
    */
   public void setMatchId(final String value) {
      this.matchId = value;
   }

}
