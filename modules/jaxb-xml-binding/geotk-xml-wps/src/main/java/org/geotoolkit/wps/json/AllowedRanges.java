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
 * AllowedRanges
 */
public class AllowedRanges {

  private List<Range> allowedRanges = new ArrayList<>();

  public AllowedRanges() {

  }

  public AllowedRanges(List<Range> allowedRanges) {
      this.allowedRanges = new ArrayList<>(allowedRanges);
  }

  public AllowedRanges allowedRanges(List<Range> allowedRanges) {
    this.allowedRanges = allowedRanges;
    return this;
  }

  public AllowedRanges addAllowedRangesItem(Range allowedRangesItem) {

    this.allowedRanges.add(allowedRangesItem);
    return this;
  }

  /**
  * Get allowedRanges
  * @return allowedRanges
  **/
  public List<Range> getAllowedRanges() {
    return allowedRanges;
  }
  public void setAllowedRanges(List<Range> allowedRanges) {
    this.allowedRanges = allowedRanges;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AllowedRanges allowedRanges = (AllowedRanges) o;
    return Objects.equals(this.allowedRanges, allowedRanges.allowedRanges);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(allowedRanges);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AllowedRanges {\n");

    sb.append("    allowedRanges: ").append(toIndentedString(allowedRanges)).append("\n");
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



