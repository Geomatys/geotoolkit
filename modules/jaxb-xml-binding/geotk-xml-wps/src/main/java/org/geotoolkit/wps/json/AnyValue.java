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
 * AnyValue
 */
public class AnyValue {

  private Boolean anyValue = null;

  public AnyValue() {

  }

  public AnyValue(Boolean anyValue) {
      this.anyValue = anyValue;
  }

  public AnyValue anyValue(Boolean anyValue) {
    this.anyValue = anyValue;
    return this;
  }


  /**
  * Get anyValue
  * @return anyValue
  **/
  public Boolean isAnyValue() {
    return anyValue;
  }
  public void setAnyValue(Boolean anyValue) {
    this.anyValue = anyValue;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnyValue anyValue = (AnyValue) o;
    return Objects.equals(this.anyValue, anyValue.anyValue);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(anyValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnyValue {\n");

    sb.append("    anyValue: ").append(toIndentedString(anyValue)).append("\n");
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



