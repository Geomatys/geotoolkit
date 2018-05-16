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
 * DataType
 */
public class DataType {

  private String id = null;
  
  private Format format = null;
  
  public DataType id(String id) {
    this.id = id;
    return this;
  }

  
  /**
  * Get id
  * @return id
  **/
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  
  public DataType format(Format format) {
    this.format = format;
    return this;
  }

  
  /**
  * Get format
  * @return format
  **/
  public Format getFormat() {
    return format;
  }
  public void setFormat(Format format) {
    this.format = format;
  }
  
  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataType dataType = (DataType) o;
    return Objects.equals(this.id, dataType.id) &&
        Objects.equals(this.format, dataType.format);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, format);
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataType {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    format: ").append(toIndentedString(format)).append("\n");
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



