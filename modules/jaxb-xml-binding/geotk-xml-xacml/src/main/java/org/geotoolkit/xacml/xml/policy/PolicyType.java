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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for PolicyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Description" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}PolicyDefaults" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Target"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}CombinerParameters" minOccurs="0"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}RuleCombinerParameters" minOccurs="0"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}VariableDefinition"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Rule"/>
 *         &lt;/choice>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Obligations" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="PolicyId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="Version" type="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}VersionType" default="1.0" />
 *       &lt;attribute name="RuleCombiningAlgId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyType", propOrder =
{"description", "policyDefaults", "target", "combinerParametersOrRuleCombinerParametersOrVariableDefinition",
      "obligations"})
@XmlRootElement( name= "Policy")      
public class PolicyType {

   @XmlElement(name = "Description")
   private String description;

   @XmlElement(name = "PolicyDefaults")
   private DefaultsType policyDefaults;

   @XmlElement(name = "Target", required = true)
   private TargetType target;

   @XmlElements(
        {@XmlElement(name = "CombinerParameters",     type = CombinerParametersType.class),
         @XmlElement(name = "Rule",                   type = RuleType.class),
         @XmlElement(name = "VariableDefinition",     type = VariableDefinitionType.class),
         @XmlElement(name = "RuleCombinerParameters", type = RuleCombinerParametersType.class)})
   private List<Object> combinerParametersOrRuleCombinerParametersOrVariableDefinition;

   @XmlElement(name = "Obligations")
   private ObligationsType obligations;

   @XmlAttribute(name = "PolicyId", required = true)
   @XmlSchemaType(name = "anyURI")
   private String policyId;

   @XmlAttribute(name = "Version")
   private String version;

   @XmlAttribute(name = "RuleCombiningAlgId", required = true)
   @XmlSchemaType(name = "anyURI")
   private String ruleCombiningAlgId;

   /**
    * Gets the value of the description property.
    */
   public String getDescription()  {
      return description;
   }

   /**
    * Sets the value of the description property.
    */
   public void setDescription(String value) {
      this.description = value;
   }

   /**
    * Gets the value of the policyDefaults property.
    * 
    */
   public DefaultsType getPolicyDefaults() {
      return policyDefaults;
   }

   /**
    * Sets the value of the policyDefaults property.
    */
   public void setPolicyDefaults(DefaultsType value) {
      this.policyDefaults = value;
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
   public void setTarget(TargetType value) {
      this.target = value;
   }

   /**
    * Gets the value of the combinerParametersOrRuleCombinerParametersOrVariableDefinition property.
    * 
    */
   public List<Object> getCombinerParametersOrRuleCombinerParametersOrVariableDefinition() {
      if (combinerParametersOrRuleCombinerParametersOrVariableDefinition == null) {
         combinerParametersOrRuleCombinerParametersOrVariableDefinition = new ArrayList<Object>();
      }
      return this.combinerParametersOrRuleCombinerParametersOrVariableDefinition;
   }

   /**
    * Gets the value of the obligations property.
    */
   public ObligationsType getObligations() {
      return obligations;
   }

   /**
    * Sets the value of the obligations property.
    */
   public void setObligations(ObligationsType value) {
      this.obligations = value;
   }

   /**
    * Gets the value of the policyId property.
    */
   public String getPolicyId() {
      return policyId;
   }

   /**
    * Sets the value of the policyId property.
    */
   public void setPolicyId(String value) {
      this.policyId = value;
   }

   /**
    * Gets the value of the version property.
    */
   public String getVersion() {
      if (version == null) {
         return "1.0";
      } else {
         return version;
      }
   }

   /**
    * Sets the value of the version property.
    */
   public void setVersion(String value) {
      this.version = value;
   }

   /**
    * Gets the value of the ruleCombiningAlgId property.
    * 
    */
   public String getRuleCombiningAlgId() {
      return ruleCombiningAlgId;
   }

   /**
    * Sets the value of the ruleCombiningAlgId property.
    */
   public void setRuleCombiningAlgId(String value) {
      this.ruleCombiningAlgId = value;
   }

}
