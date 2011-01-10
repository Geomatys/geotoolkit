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
 * <p>Java class for RuleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RuleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Description" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Target" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Condition" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="RuleId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Effect" use="required" type="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}EffectType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RuleType", propOrder =
{"description", "target", "condition"})
public class RuleType {

   @XmlElement(name = "Description")
   private String description;

   @XmlElement(name = "Target")
   private TargetType target;

   @XmlElement(name = "Condition")
   private ConditionType condition;

   @XmlAttribute(name = "RuleId", required = true)
   private String ruleId;

   @XmlAttribute(name = "Effect", required = true)
   private EffectType effect;

   /**
    * Gets the value of the description property.
    */
   public String getDescription() {
      return description;
   }

   /**
    * Sets the value of the description property.
    */
   public void setDescription(final String value) {
      this.description = value;
   }

   /**
    * Gets the value of the target property.
    */
   public TargetType getTarget() {
      return target;
   }

   /**
    * Sets the value of the target property.
    */
   public void setTarget(final TargetType value) {
      this.target = value;
   }

   /**
    * Gets the value of the condition property.
    */
   public ConditionType getCondition() {
      return condition;
   }

   /**
    * Sets the value of the condition property.
    */
   public void setCondition(final ConditionType value) {
      this.condition = value;
   }

   /**
    * Gets the value of the ruleId property.
    */
   public String getRuleId() {
      return ruleId;
   }

   /**
    * Sets the value of the ruleId property.
    */
   public void setRuleId(final String value) {
      this.ruleId = value;
   }

   /**
    * Gets the value of the effect property.
    */
   public EffectType getEffect() {
      return effect;
   }

   /**
    * Sets the value of the effect property.
    */
   public void setEffect(final EffectType value) {
      this.effect = value;
   }

}
