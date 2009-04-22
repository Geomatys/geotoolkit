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
 * <p>Java class for SubjectAttributeDesignatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SubjectAttributeDesignatorType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}AttributeDesignatorType">
 *       &lt;attribute name="SubjectCategory" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubjectAttributeDesignatorType")
public class SubjectAttributeDesignatorType extends AttributeDesignatorType {
    

   @XmlAttribute(name = "SubjectCategory")
   @XmlSchemaType(name = "anyURI")
   private String subjectCategory;

   /**
    * An epty constructor used by JAXB.
    */
   public SubjectAttributeDesignatorType() {
       
   }
   
   /**
    * Build a new SubjectAttributeDesignator from an AttributeDesignator. 
    */
   public SubjectAttributeDesignatorType(AttributeDesignatorType attribute, String subjectCategory) {
       super(attribute);
       this.subjectCategory = subjectCategory;
   }
   
   /**
    * Gets the value of the subjectCategory property.
    */
   public String getSubjectCategory() {
      if (subjectCategory == null) {
         return "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
      } else {
         return subjectCategory;
      }
   }

   /**
    * Sets the value of the subjectCategory property.
    */
   public void setSubjectCategory(String value) {
      this.subjectCategory = value;
   }

}
