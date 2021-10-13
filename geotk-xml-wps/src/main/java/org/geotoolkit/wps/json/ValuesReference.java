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
 * ValuesReference
 */
public class ValuesReference {

  private String valueReference = null;

  public ValuesReference() {

  }

  public ValuesReference(String valueReference) {
      this.valueReference = valueReference;
  }

  public ValuesReference valueReference(String valueReference) {
    this.valueReference = valueReference;
    return this;
  }


  /**
  * Get valueReference
  * @return valueReference
  **/
  public String getValueReference() {
    return valueReference;
  }
  public void setValueReference(String valueReference) {
    this.valueReference = valueReference;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValuesReference valuesReference = (ValuesReference) o;
    return Objects.equals(this.valueReference, valuesReference.valueReference);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(valueReference);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValuesReference {\n");

    sb.append("    valueReference: ").append(toIndentedString(valueReference)).append("\n");
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



