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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>Java class for IdReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IdReferenceType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anyURI">
 *       &lt;attribute name="Version" type="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}VersionMatchType" />
 *       &lt;attribute name="EarliestVersion" type="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}VersionMatchType" />
 *       &lt;attribute name="LatestVersion" type="{urn:oasis:names:tc:xacml:2.0:policy:schema:os}VersionMatchType" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdReferenceType", propOrder =
{"value"})
public class IdReferenceType {

   @XmlValue
   @XmlSchemaType(name = "anyURI")
   private String value;

   @XmlAttribute(name = "Version")
   private String version;

   @XmlAttribute(name = "EarliestVersion")
   private String earliestVersion;

   @XmlAttribute(name = "LatestVersion")
   private String latestVersion;

   /**
    * Gets the value of the value property.
    */
   public String getValue() {
      return value;
   }

   /**
    * Sets the value of the value property.
    */
   public void setValue(String value) {
      this.value = value;
   }

   /**
    * Gets the value of the version property.
    */
   public String getVersion() {
      return version;
   }

   /**
    * Sets the value of the version property.
    */
   public void setVersion(String value) {
      this.version = value;
   }

   /**
    * Gets the value of the earliestVersion property.
    */
   public String getEarliestVersion() {
      return earliestVersion;
   }

   /**
    * Sets the value of the earliestVersion property.
    */
   public void setEarliestVersion(String value) {
      this.earliestVersion = value;
   }

   /**
    * Gets the value of the latestVersion property.
    */
   public String getLatestVersion() {
      return latestVersion;
   }

   /**
    * Sets the value of the latestVersion property.
    */
   public void setLatestVersion(String value) {
      this.latestVersion = value;
   }

}
