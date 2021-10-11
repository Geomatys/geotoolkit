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
import org.geotoolkit.ows.xml.v200.RangeType;

/**
 * Range
 */
public class Range {

  private String minimumValue = null;

  private String maximumValue = null;

  private String spacing = null;

  public Range() {

  }

  public Range(RangeType range) {
      if (range != null) {
          if (range.getMaximumValue() != null) {
            this.maximumValue = range.getMaximumValue().getValue();
          }
          if (range.getMinimumValue()!= null) {
            this.minimumValue = range.getMinimumValue().getValue();
          }
          if (range.getSpacing()!= null) {
            this.spacing = range.getSpacing().getValue();
          }
          if (range.getRangeClosure() != null && !range.getRangeClosure().isEmpty()) {
              this.rangeClosure = RangeClosureEnum.fromValue(range.getRangeClosure().get(0));
          }
      }

  }
  /**
   * Gets or Sets rangeClosure
   */
  public enum RangeClosureEnum {

    CLOSED("closed"),
    OPEN("open"),
    OPEN_CLOSED("open-closed"),
    CLOSED_OPEN("closed-open");

    private String value;

    RangeClosureEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static RangeClosureEnum fromValue(String text) {
      for (RangeClosureEnum b : RangeClosureEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }

  private RangeClosureEnum rangeClosure = null;

  public Range minimumValue(String minimumValue) {
    this.minimumValue = minimumValue;
    return this;
  }


  /**
  * Get minimumValue
  * @return minimumValue
  **/
  public String getMinimumValue() {
    return minimumValue;
  }
  public void setMinimumValue(String minimumValue) {
    this.minimumValue = minimumValue;
  }

  public Range maximumValue(String maximumValue) {
    this.maximumValue = maximumValue;
    return this;
  }


  /**
  * Get maximumValue
  * @return maximumValue
  **/
  public String getMaximumValue() {
    return maximumValue;
  }
  public void setMaximumValue(String maximumValue) {
    this.maximumValue = maximumValue;
  }

  public Range spacing(String spacing) {
    this.spacing = spacing;
    return this;
  }


  /**
  * Get spacing
  * @return spacing
  **/
  public String getSpacing() {
    return spacing;
  }
  public void setSpacing(String spacing) {
    this.spacing = spacing;
  }

  public Range rangeClosure(RangeClosureEnum rangeClosure) {
    this.rangeClosure = rangeClosure;
    return this;
  }


  /**
  * Get rangeClosure
  * @return rangeClosure
  **/
  public RangeClosureEnum getRangeClosure() {
    return rangeClosure;
  }
  public void setRangeClosure(RangeClosureEnum rangeClosure) {
    this.rangeClosure = rangeClosure;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Range range = (Range) o;
    return Objects.equals(this.minimumValue, range.minimumValue) &&
        Objects.equals(this.maximumValue, range.maximumValue) &&
        Objects.equals(this.spacing, range.spacing) &&
        Objects.equals(this.rangeClosure, range.rangeClosure);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(minimumValue, maximumValue, spacing, rangeClosure);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Range {\n");

    sb.append("    minimumValue: ").append(toIndentedString(minimumValue)).append("\n");
    sb.append("    maximumValue: ").append(toIndentedString(maximumValue)).append("\n");
    sb.append("    spacing: ").append(toIndentedString(spacing)).append("\n");
    sb.append("    rangeClosure: ").append(toIndentedString(rangeClosure)).append("\n");
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



