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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for PolicySetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicySetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Description" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}PolicySetDefaults" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Target"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}PolicySet"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Policy"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}PolicySetIdReference"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}PolicyIdReference"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}CombinerParameters"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}PolicyCombinerParameters"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}PolicySetCombinerParameters"/>
 *         &lt;/choice>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Obligations" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="PolicySetId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="Version" type="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}VersionType" default="1.0" />
 *       &lt;attribute name="PolicyCombiningAlgId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name      = "PolicySetType", 
         propOrder ={ "description", 
                      "policySetDefaults", 
                      "target", 
                      "policySetOrPolicyOrPolicySetIdReference", 
                      "obligations"})
@XmlRootElement(name="PolicySet")
public class PolicySetType {

   @XmlElement(name = "Description")
   private String description;

   @XmlElement(name = "PolicySetDefaults")
   private DefaultsType policySetDefaults;

   @XmlElement(name = "Target", required = true)
   private TargetType target;

   @XmlElementRefs(
   {
         @XmlElementRef(name = "Policy",                      namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", type = JAXBElement.class),
         @XmlElementRef(name = "PolicySetIdReference",        namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", type = JAXBElement.class),
         @XmlElementRef(name = "PolicyCombinerParameters",    namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", type = JAXBElement.class),
         @XmlElementRef(name = "CombinerParameters",          namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", type = JAXBElement.class),
         @XmlElementRef(name = "PolicySet",                   namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", type = JAXBElement.class),
         @XmlElementRef(name = "PolicyIdReference",           namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", type = JAXBElement.class),
         @XmlElementRef(name = "PolicySetCombinerParameters", namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", type = JAXBElement.class)})
   private List<JAXBElement<?>> policySetOrPolicyOrPolicySetIdReference;

   @XmlElement(name = "Obligations")
   private ObligationsType obligations;

   @XmlAttribute(name = "PolicySetId", required = true)
   @XmlSchemaType(name = "anyURI")
   private String policySetId;

   @XmlAttribute(name = "Version")
   private String version;

   @XmlAttribute(name = "PolicyCombiningAlgId", required = true)
   @XmlSchemaType(name = "anyURI")
   private String policyCombiningAlgId;

   /**
    * Gets the value of the description property.
    */
   public String getDescription() {
      return description;
   }

   /**
    * Sets the value of the description property.
    */
   public void setDescription(String value) {
      this.description = value;
   }

   /**
    * Gets the value of the policySetDefaults property.
    */
   public DefaultsType getPolicySetDefaults() {
      return policySetDefaults;
   }

   /**
    * Sets the value of the policySetDefaults property.
    */
   public void setPolicySetDefaults(DefaultsType value) {
      this.policySetDefaults = value;
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
    * Gets the value of the policySetOrPolicyOrPolicySetIdReference property.
    * 
    */
   public List<JAXBElement<?>> getPolicySetOrPolicyOrPolicySetIdReference() {
      if (policySetOrPolicyOrPolicySetIdReference == null) {
         policySetOrPolicyOrPolicySetIdReference = new ArrayList<JAXBElement<?>>();
      }
      return this.policySetOrPolicyOrPolicySetIdReference;
   }
   
   /**
    * Gets the value of the policies child.
    * 
    */
   public List<PolicyType> getPoliciesChild() {
      List<PolicyType> policies = new ArrayList<PolicyType>();
      if (policySetOrPolicyOrPolicySetIdReference != null) {
         for (JAXBElement<?> jb: policySetOrPolicyOrPolicySetIdReference) {
             if (jb.getName().getLocalPart().equals("Policy")) {
                policies.add((PolicyType)jb.getValue());
             } else if (jb.getName().getLocalPart().equals("PolicySet")) {
                 PolicySetType policySetChild = (PolicySetType) jb.getValue();
                 policies.addAll(policySetChild.getPoliciesChild());
             }
         }
      }
      return policies;
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
    * Gets the value of the policySetId property.
    * 
    */
   public String getPolicySetId() {
      return policySetId;
   }

   /**
    * Sets the value of the policySetId property.
    */
   public void setPolicySetId(String value) {
      this.policySetId = value;
   }

   /**
    * Gets the value of the version property.
    * 
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
    * 
    */
   public void setVersion(String value) {
      this.version = value;
   }

   /**
    * Gets the value of the policyCombiningAlgId property.
    * 
    */
   public String getPolicyCombiningAlgId() {
      return policyCombiningAlgId;
   }

   /**
    * Sets the value of the policyCombiningAlgId property.
    * 
    */
   public void setPolicyCombiningAlgId(String value) {
      this.policyCombiningAlgId = value;
   }

}
