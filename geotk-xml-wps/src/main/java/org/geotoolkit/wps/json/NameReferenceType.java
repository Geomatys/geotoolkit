/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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

package org.geotoolkit.wps.json;

import java.util.Objects;

/**
 * LiteralDataDomainTypeDataType
 */
public class NameReferenceType {

  private String name = null;

  private String reference = null;

  public NameReferenceType() {

  }

  public NameReferenceType(NameReferenceType that) {
      if (that != null) {
        this.name = that.name;
        this.reference = that.reference;
      }
  }

  public NameReferenceType(String name, String reference) {
      this.name = name;
      this.reference = reference;
  }

  public NameReferenceType name(String name) {
    this.name = name;
    return this;
  }


  /**
  * Get name
  * @return name
  **/
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public NameReferenceType reference(String reference) {
    this.reference = reference;
    return this;
  }


  /**
  * Get reference
  * @return reference
  **/
  public String getReference() {
    return reference;
  }
  public void setReference(String reference) {
    this.reference = reference;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NameReferenceType literalDataDomainTypeDataType = (NameReferenceType) o;
    return Objects.equals(this.name, literalDataDomainTypeDataType.name) &&
        Objects.equals(this.reference, literalDataDomainTypeDataType.reference);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(name, reference);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LiteralDataDomainTypeDataType {\n");

    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


}



