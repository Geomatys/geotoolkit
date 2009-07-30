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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the org.geotoolkit.xacml.xml.policy.package.
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content. 
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding of schema type definitions, 
 * element declarations and model groups. 
 * Factory methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory
{

   private static final QName _PolicySetCombinerParam_QNAME    = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "PolicySetCombinerParameters");
   private static final QName _Obligations_QNAME               = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Obligations");
   private static final QName _RuleCombinerParameters_QNAME    = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "RuleCombinerParameters");
   private static final QName _AttributeValue_QNAME            = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "AttributeValue");
   private static final QName _VariableDefinition_QNAME        = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "VariableDefinition");
   private static final QName _Apply_QNAME                     = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Apply");
   private static final QName _Environments_QNAME              = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Environments");
   private static final QName _SubjectMatch_QNAME              = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "SubjectMatch");
   private static final QName _ResourceMatch_QNAME             = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "ResourceMatch");
   private static final QName _ActionAttrDesignator_QNAME      = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "ActionAttributeDesignator");
   private static final QName _PolicyCombinerParam_QNAME       = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "PolicyCombinerParameters");
   private static final QName _Obligation_QNAME                = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Obligation");
   private static final QName _ResourceAttrDesignator_QNAME    = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "ResourceAttributeDesignator");
   private static final QName _Function_QNAME                  = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Function");
   private static final QName _EnvironmentAttrDesignator_QNAME = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "EnvironmentAttributeDesignator");
   private static final QName _VariableReference_QNAME         = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "VariableReference");
   private static final QName _AttributeAssignment_QNAME       = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "AttributeAssignment");
   private static final QName _Resource_QNAME                  = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Resource");
   private static final QName _Condition_QNAME                 = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Condition");
   private static final QName _PolicyDefaults_QNAME            = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "PolicyDefaults");
   private static final QName _Rule_QNAME                      = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Rule");
   private static final QName _Resources_QNAME                 = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Resources");
   private static final QName _Policy_QNAME                    = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Policy");
   private static final QName _Target_QNAME                    = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Target");
   private static final QName _Subject_QNAME                   = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Subject");
   private static final QName _Subjects_QNAME                  = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Subjects");
   private static final QName _PolicySetIdReference_QNAME      = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "PolicySetIdReference");
   private static final QName _PolicySetDefaults_QNAME         = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "PolicySetDefaults");
   private static final QName _XPathVersion_QNAME              = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "XPathVersion");
   private static final QName _PolicyIdReference_QNAME         = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "PolicyIdReference");
   private static final QName _CombinerParameters_QNAME        = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "CombinerParameters");
   private static final QName _EnvironmentMatch_QNAME          = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "EnvironmentMatch");
   private static final QName _Environment_QNAME               = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Environment");
   private static final QName _Actions_QNAME                   = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Actions");
   private static final QName _Action_QNAME                    = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Action");
   private static final QName _ActionMatch_QNAME               = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "ActionMatch");
   private static final QName _AttributeSelector_QNAME         = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "AttributeSelector");
   private static final QName _Description_QNAME               = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Description");
   private static final QName _PolicySet_QNAME                 = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "PolicySet");
   private static final QName _SubjectAttrDesignator_QNAME     = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "SubjectAttributeDesignator");
   private static final QName _Expression_QNAME                = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "Expression");
   private static final QName _CombinerParameter_QNAME         = new QName("urn:oasis:names:tc:xacml:2.0:policy:schema:os", "CombinerParameter");

   /**
    * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.geotoolkit.xacml.xml.policy.
    * 
    */
   public ObjectFactory() {
   }

   /**
    * Create an instance of {@link EnvironmentMatchType }
    * 
    */
   public EnvironmentMatchType createEnvironmentMatchType() {
      return new EnvironmentMatchType();
   }

   /**
    * Create an instance of {@link AttributeDesignatorType }
    * 
    */
   public AttributeDesignatorType createAttributeDesignatorType() {
      return new AttributeDesignatorType();
   }

   /**
    * Create an instance of {@link AttributeAssignmentType }
    * 
    */
   public AttributeAssignmentType createAttributeAssignmentType() {
      return new AttributeAssignmentType();
   }

   /**
    * Create an instance of {@link PolicyCombinerParametersType }
    * 
    */
   public PolicyCombinerParametersType createPolicyCombinerParametersType() {
      return new PolicyCombinerParametersType();
   }

   /**
    * Create an instance of {@link PolicySetType }
    * 
    */
   public PolicySetType createPolicySetType() {
      return new PolicySetType();
   }

   /**
    * Create an instance of {@link EnvironmentType }
    * 
    */
   public EnvironmentType createEnvironmentType() {
      return new EnvironmentType();
   }

   /**
    * Create an instance of {@link ActionsType }
    * 
    */
   public ActionsType createActionsType() {
      return new ActionsType();
   }

   /**
    * Create an instance of {@link ActionMatchType }
    * 
    */
   public ActionMatchType createActionMatchType() {
      return new ActionMatchType();
   }

   /**
    * Create an instance of {@link ActionType }
    * 
    */
   public ActionType createActionType() {
      return new ActionType();
   }

   /**
    * Create an instance of {@link CombinerParametersType }
    * 
    */
   public CombinerParametersType createCombinerParametersType() {
      return new CombinerParametersType();
   }

   /**
    * Create an instance of {@link ConditionType }
    * 
    */
   public ConditionType createConditionType() {
      return new ConditionType();
   }

   /**
    * Create an instance of {@link EnvironmentsType }
    * 
    */
   public EnvironmentsType createEnvironmentsType() {
      return new EnvironmentsType();
   }

   /**
    * Create an instance of {@link SubjectsType }
    * 
    */
   public SubjectsType createSubjectsType() {
      return new SubjectsType();
   }

   /**
    * Create an instance of {@link FunctionType }
    * 
    */
   public FunctionType createFunctionType() {
      return new FunctionType();
   }

   /**
    * Create an instance of {@link RuleType }
    * 
    */
   public RuleType createRuleType() {
      return new RuleType();
   }

   /**
    * Create an instance of {@link AttributeSelectorType }
    * 
    */
   public AttributeSelectorType createAttributeSelectorType()  {
      return new AttributeSelectorType();
   }

   /**
    * Create an instance of {@link AttributeValueType }
    * 
    */
   public AttributeValueType createAttributeValueType() {
      return new AttributeValueType();
   }

   /**
    * Create an instance of {@link ObligationType }
    * 
    */
   public ObligationType createObligationType() {
      return new ObligationType();
   }

   /**
    * Create an instance of {@link VariableReferenceType }
    * 
    */
   public VariableReferenceType createVariableReferenceType() {
      return new VariableReferenceType();
   }

   /**
    * Create an instance of {@link PolicyType }
    * 
    */
   public PolicyType createPolicyType() {
      return new PolicyType();
   }

   /**
    * Create an instance of {@link SubjectType }
    * 
    */
   public SubjectType createSubjectType() {
      return new SubjectType();
   }

   /**
    * Create an instance of {@link PolicySetCombinerParametersType }
    * 
    */
   public PolicySetCombinerParametersType createPolicySetCombinerParametersType() {
      return new PolicySetCombinerParametersType();
   }

   /**
    * Create an instance of {@link RuleCombinerParametersType }
    * 
    */
   public RuleCombinerParametersType createRuleCombinerParametersType() {
      return new RuleCombinerParametersType();
   }

   /**
    * Create an instance of {@link ResourceType }
    * 
    */
   public ResourceType createResourceType() {
      return new ResourceType();
   }

   /**
    * Create an instance of {@link VariableDefinitionType }
    * 
    */
   public VariableDefinitionType createVariableDefinitionType() {
      return new VariableDefinitionType();
   }

   /**
    * Create an instance of {@link DefaultsType }
    * 
    */
   public DefaultsType createDefaultsType() {
      return new DefaultsType();
   }

   /**
    * Create an instance of {@link ObligationsType }
    * 
    */
   public ObligationsType createObligationsType() {
      return new ObligationsType();
   }

   /**
    * Create an instance of {@link SubjectMatchType }
    * 
    */
   public SubjectMatchType createSubjectMatchType() {
      return new SubjectMatchType();
   }

   /**
    * Create an instance of {@link TargetType }
    * 
    */
   public TargetType createTargetType() {
      return new TargetType();
   }

   /**
    * Create an instance of {@link CombinerParameterType }
    * 
    */
   public CombinerParameterType createCombinerParameterType() {
      return new CombinerParameterType();
   }

   /**
    * Create an instance of {@link ApplyType }
    * 
    */
   public ApplyType createApplyType() {
      return new ApplyType();
   }

   /**
    * Create an instance of {@link IdReferenceType }
    * 
    */
   public IdReferenceType createIdReferenceType() {
      return new IdReferenceType();
   }

   /**
    * Create an instance of {@link ResourcesType }
    * 
    */
   public ResourcesType createResourcesType() {
      return new ResourcesType();
   }

   /**
    * Create an instance of {@link SubjectAttributeDesignatorType }
    * 
    */
   public SubjectAttributeDesignatorType createSubjectAttributeDesignatorType() {
      return new SubjectAttributeDesignatorType();
   }

   /**
    * Create an instance of {@link ResourceMatchType }
    * 
    */
   public ResourceMatchType createResourceMatchType() {
      return new ResourceMatchType();
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link PolicySetCombinerParametersType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "PolicySetCombinerParameters")
   public JAXBElement<PolicySetCombinerParametersType> createPolicySetCombinerParameters(PolicySetCombinerParametersType value) {
      return new JAXBElement<PolicySetCombinerParametersType>(_PolicySetCombinerParam_QNAME,
            PolicySetCombinerParametersType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link ObligationsType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Obligations")
   public JAXBElement<ObligationsType> createObligations(ObligationsType value) {
      return new JAXBElement<ObligationsType>(_Obligations_QNAME, ObligationsType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link RuleCombinerParametersType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "RuleCombinerParameters")
   public JAXBElement<RuleCombinerParametersType> createRuleCombinerParameters(RuleCombinerParametersType value) {
      return new JAXBElement<RuleCombinerParametersType>(_RuleCombinerParameters_QNAME,
            RuleCombinerParametersType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link AttributeValueType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "AttributeValue", substitutionHeadNamespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", substitutionHeadName = "Expression")
   public JAXBElement<AttributeValueType> createAttributeValue(AttributeValueType value) {
      return new JAXBElement<AttributeValueType>(_AttributeValue_QNAME, AttributeValueType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link VariableDefinitionType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "VariableDefinition")
   public JAXBElement<VariableDefinitionType> createVariableDefinition(VariableDefinitionType value) {
      return new JAXBElement<VariableDefinitionType>(_VariableDefinition_QNAME, VariableDefinitionType.class, null,
            value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link ApplyType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Apply", substitutionHeadNamespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", substitutionHeadName = "Expression")
   public JAXBElement<ApplyType> createApply(ApplyType value) {
      return new JAXBElement<ApplyType>(_Apply_QNAME, ApplyType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link EnvironmentsType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Environments")
   public JAXBElement<EnvironmentsType> createEnvironments(EnvironmentsType value) {
      return new JAXBElement<EnvironmentsType>(_Environments_QNAME, EnvironmentsType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link SubjectMatchType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "SubjectMatch")
   public JAXBElement<SubjectMatchType> createSubjectMatch(SubjectMatchType value) {
      return new JAXBElement<SubjectMatchType>(_SubjectMatch_QNAME, SubjectMatchType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link ResourceMatchType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "ResourceMatch")
   public JAXBElement<ResourceMatchType> createResourceMatch(ResourceMatchType value) {
      return new JAXBElement<ResourceMatchType>(_ResourceMatch_QNAME, ResourceMatchType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link AttributeDesignatorType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "ActionAttributeDesignator", substitutionHeadNamespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", substitutionHeadName = "Expression")
   public JAXBElement<AttributeDesignatorType> createActionAttributeDesignator(AttributeDesignatorType value) {
      return new JAXBElement<AttributeDesignatorType>(_ActionAttrDesignator_QNAME, AttributeDesignatorType.class,
            null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link PolicyCombinerParametersType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "PolicyCombinerParameters")
   public JAXBElement<PolicyCombinerParametersType> createPolicyCombinerParameters(PolicyCombinerParametersType value) {
      return new JAXBElement<PolicyCombinerParametersType>(_PolicyCombinerParam_QNAME,
            PolicyCombinerParametersType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link ObligationType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Obligation")
   public JAXBElement<ObligationType> createObligation(ObligationType value) {
      return new JAXBElement<ObligationType>(_Obligation_QNAME, ObligationType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link AttributeDesignatorType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "ResourceAttributeDesignator", substitutionHeadNamespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", substitutionHeadName = "Expression")
   public JAXBElement<AttributeDesignatorType> createResourceAttributeDesignator(AttributeDesignatorType value) {
      return new JAXBElement<AttributeDesignatorType>(_ResourceAttrDesignator_QNAME, AttributeDesignatorType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link FunctionType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Function", substitutionHeadNamespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", substitutionHeadName = "Expression")
   public JAXBElement<FunctionType> createFunction(FunctionType value) {
      return new JAXBElement<FunctionType>(_Function_QNAME, FunctionType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link AttributeDesignatorType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "EnvironmentAttributeDesignator", substitutionHeadNamespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", substitutionHeadName = "Expression")
   public JAXBElement<AttributeDesignatorType> createEnvironmentAttributeDesignator(AttributeDesignatorType value) {
      return new JAXBElement<AttributeDesignatorType>(_EnvironmentAttrDesignator_QNAME,
            AttributeDesignatorType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link VariableReferenceType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "VariableReference", substitutionHeadNamespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", substitutionHeadName = "Expression")
   public JAXBElement<VariableReferenceType> createVariableReference(VariableReferenceType value) {
      return new JAXBElement<VariableReferenceType>(_VariableReference_QNAME, VariableReferenceType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link AttributeAssignmentType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "AttributeAssignment")
   public JAXBElement<AttributeAssignmentType> createAttributeAssignment(AttributeAssignmentType value) {
      return new JAXBElement<AttributeAssignmentType>(_AttributeAssignment_QNAME, AttributeAssignmentType.class, null,
            value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link ResourceType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Resource")
   public JAXBElement<ResourceType> createResource(ResourceType value) {
      return new JAXBElement<ResourceType>(_Resource_QNAME, ResourceType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link ConditionType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Condition")
   public JAXBElement<ConditionType> createCondition(ConditionType value) {
      return new JAXBElement<ConditionType>(_Condition_QNAME, ConditionType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link DefaultsType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "PolicyDefaults")
   public JAXBElement<DefaultsType> createPolicyDefaults(DefaultsType value) {
      return new JAXBElement<DefaultsType>(_PolicyDefaults_QNAME, DefaultsType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link RuleType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Rule")
   public JAXBElement<RuleType> createRule(RuleType value) {
      return new JAXBElement<RuleType>(_Rule_QNAME, RuleType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link ResourcesType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Resources")
   public JAXBElement<ResourcesType> createResources(ResourcesType value) {
      return new JAXBElement<ResourcesType>(_Resources_QNAME, ResourcesType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link PolicyType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Policy")
   public JAXBElement<PolicyType> createPolicy(PolicyType value) {
      return new JAXBElement<PolicyType>(_Policy_QNAME, PolicyType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link TargetType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Target")
   public JAXBElement<TargetType> createTarget(TargetType value) {
      return new JAXBElement<TargetType>(_Target_QNAME, TargetType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link SubjectType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Subject")
   public JAXBElement<SubjectType> createSubject(SubjectType value) {
      return new JAXBElement<SubjectType>(_Subject_QNAME, SubjectType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link SubjectsType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Subjects")
   public JAXBElement<SubjectsType> createSubjects(SubjectsType value) {
      return new JAXBElement<SubjectsType>(_Subjects_QNAME, SubjectsType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link IdReferenceType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "PolicySetIdReference")
   public JAXBElement<IdReferenceType> createPolicySetIdReference(IdReferenceType value) {
      return new JAXBElement<IdReferenceType>(_PolicySetIdReference_QNAME, IdReferenceType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link DefaultsType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "PolicySetDefaults")
   public JAXBElement<DefaultsType> createPolicySetDefaults(DefaultsType value) {
      return new JAXBElement<DefaultsType>(_PolicySetDefaults_QNAME, DefaultsType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "XPathVersion")
   public JAXBElement<String> createXPathVersion(String value) {
      return new JAXBElement<String>(_XPathVersion_QNAME, String.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link IdReferenceType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "PolicyIdReference")
   public JAXBElement<IdReferenceType> createPolicyIdReference(IdReferenceType value) {
      return new JAXBElement<IdReferenceType>(_PolicyIdReference_QNAME, IdReferenceType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link CombinerParametersType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "CombinerParameters")
   public JAXBElement<CombinerParametersType> createCombinerParameters(CombinerParametersType value) {
      return new JAXBElement<CombinerParametersType>(_CombinerParameters_QNAME, CombinerParametersType.class, null,
            value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link EnvironmentMatchType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "EnvironmentMatch")
   public JAXBElement<EnvironmentMatchType> createEnvironmentMatch(EnvironmentMatchType value) {
      return new JAXBElement<EnvironmentMatchType>(_EnvironmentMatch_QNAME, EnvironmentMatchType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link EnvironmentType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Environment")
   public JAXBElement<EnvironmentType> createEnvironment(EnvironmentType value) {
      return new JAXBElement<EnvironmentType>(_Environment_QNAME, EnvironmentType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link ActionsType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Actions")
   public JAXBElement<ActionsType> createActions(ActionsType value) {
      return new JAXBElement<ActionsType>(_Actions_QNAME, ActionsType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link ActionType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Action")
   public JAXBElement<ActionType> createAction(ActionType value) {
      return new JAXBElement<ActionType>(_Action_QNAME, ActionType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link ActionMatchType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "ActionMatch")
   public JAXBElement<ActionMatchType> createActionMatch(ActionMatchType value) {
      return new JAXBElement<ActionMatchType>(_ActionMatch_QNAME, ActionMatchType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link AttributeSelectorType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "AttributeSelector", substitutionHeadNamespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", substitutionHeadName = "Expression")
   public JAXBElement<AttributeSelectorType> createAttributeSelector(AttributeSelectorType value) {
      return new JAXBElement<AttributeSelectorType>(_AttributeSelector_QNAME, AttributeSelectorType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Description")
   public JAXBElement<String> createDescription(String value) {
      return new JAXBElement<String>(_Description_QNAME, String.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link PolicySetType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "PolicySet")
   public JAXBElement<PolicySetType> createPolicySet(PolicySetType value) {
      return new JAXBElement<PolicySetType>(_PolicySet_QNAME, PolicySetType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link SubjectAttributeDesignatorType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "SubjectAttributeDesignator", substitutionHeadNamespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", substitutionHeadName = "Expression")
   public JAXBElement<SubjectAttributeDesignatorType> createSubjectAttributeDesignator(SubjectAttributeDesignatorType value) {
      return new JAXBElement<SubjectAttributeDesignatorType>(_SubjectAttrDesignator_QNAME,
            SubjectAttributeDesignatorType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link ExpressionType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "Expression")
   public JAXBElement<ExpressionType> createExpression(ExpressionType value) {
      return new JAXBElement<ExpressionType>(_Expression_QNAME, ExpressionType.class, null, value);
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link CombinerParameterType }{@code >}}
    * 
    */
   @XmlElementDecl(namespace = "urn:oasis:names:tc:xacml:2.0:policy:schema:os", name = "CombinerParameter")
   public JAXBElement<CombinerParameterType> createCombinerParameter(CombinerParameterType value) {
      return new JAXBElement<CombinerParameterType>(_CombinerParameter_QNAME, CombinerParameterType.class, null, value);
   }

}
