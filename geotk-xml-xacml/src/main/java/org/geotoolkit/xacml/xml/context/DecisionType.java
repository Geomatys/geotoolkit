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
package org.geotoolkit.xacml.xml.context;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java class for DecisionType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DecisionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Permit"/>
 *     &lt;enumeration value="Deny"/>
 *     &lt;enumeration value="Indeterminate"/>
 *     &lt;enumeration value="NotApplicable"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "DecisionType")
@XmlEnum
public enum DecisionType {

   @XmlEnumValue("Permit")
   PERMIT("Permit"), @XmlEnumValue("Deny")
   DENY("Deny"), @XmlEnumValue("Indeterminate")
   INDETERMINATE("Indeterminate"), @XmlEnumValue("NotApplicable")
   NOT_APPLICABLE("NotApplicable");
   private final String value;

   DecisionType(final String v) {
      value = v;
   }

   public String value() {
      return value;
   }

   public static DecisionType fromValue(final String v) {
      for (DecisionType c : DecisionType.values()) {
         if (c.value.equals(v)) {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }

}
