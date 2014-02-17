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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for TargetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TargetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Subjects" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Resources" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Actions" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}Environments" minOccurs="0"/>
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
@XmlType(name = "TargetType", propOrder =
{"subjects", "resources", "actions", "environments"})
public class TargetType {

   @XmlElement(name = "Subjects")
   private SubjectsType subjects;

   @XmlElement(name = "Resources")
   private ResourcesType resources;

   @XmlElement(name = "Actions")
   private ActionsType actions;

   @XmlElement(name = "Environments")
   private EnvironmentsType environments;

   /**
    * Gets the value of the subjects property.
    */
   public SubjectsType getSubjects() {
      return subjects;
   }

   /**
    * Sets the value of the subjects property.
    */
   public void setSubjects(final SubjectsType value) {
      this.subjects = value;
   }

   /**
    * Gets the value of the resources property.
    * 
    */
   public ResourcesType getResources() {
      return resources;
   }

   /**
    * Sets the value of the resources property.
    */
   public void setResources(final ResourcesType value) {
      this.resources = value;
   }

   /**
    * Gets the value of the actions property.
    */
   public ActionsType getActions() {
      return actions;
   }

   /**
    * Sets the value of the actions property.
    */
   public void setActions(final ActionsType value) {
      this.actions = value;
   }

   /**
    * Gets the value of the environments property.
    */
   public EnvironmentsType getEnvironments() {
      return environments;
   }

   /**
    * Sets the value of the environments property.
    */
   public void setEnvironments(final EnvironmentsType value) {
      this.environments = value;
   }

}
