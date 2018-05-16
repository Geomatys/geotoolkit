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
 * Output
 */
public class Output extends DataType {

  private TransmissionMode transmissionMode = null;
  
  public Output transmissionMode(TransmissionMode transmissionMode) {
    this.transmissionMode = transmissionMode;
    return this;
  }

  
  /**
  * Get transmissionMode
  * @return transmissionMode
  **/
  public TransmissionMode getTransmissionMode() {
    return transmissionMode;
  }
  public void setTransmissionMode(TransmissionMode transmissionMode) {
    this.transmissionMode = transmissionMode;
  }
  
  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Output output = (Output) o;
    return Objects.equals(this.transmissionMode, output.transmissionMode) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transmissionMode, super.hashCode());
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Output {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    transmissionMode: ").append(toIndentedString(transmissionMode)).append("\n");
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



