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
import java.util.ArrayList;
import java.util.List;

/**
 * AllowedValues
 */
public class AllowedValues {

  private List<String> allowedValues = new ArrayList<>();

  public AllowedValues() {

  }

  public AllowedValues(List<String> allowedValues) {
      this.allowedValues = allowedValues;
  }

  public AllowedValues(AllowedValues that) {
      if (that != null) {
        this.allowedValues = that.allowedValues;
      }
  }

  public AllowedValues allowedValues(List<String> allowedValues) {
    this.allowedValues = allowedValues;
    return this;
  }

  public AllowedValues addAllowedValuesItem(String allowedValuesItem) {

    this.allowedValues.add(allowedValuesItem);
    return this;
  }

  /**
  * Get allowedValues
  * @return allowedValues
  **/
  public List<String> getAllowedValues() {
    return allowedValues;
  }
  public void setAllowedValues(List<String> allowedValues) {
    this.allowedValues = allowedValues;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AllowedValues allowedValues = (AllowedValues) o;
    return Objects.equals(this.allowedValues, allowedValues.allowedValues);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(allowedValues);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AllowedValues {\n");

    sb.append("    allowedValues: ").append(toIndentedString(allowedValues)).append("\n");
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



