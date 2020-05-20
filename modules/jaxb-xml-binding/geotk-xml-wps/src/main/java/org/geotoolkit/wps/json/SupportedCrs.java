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
import org.geotoolkit.wps.xml.v200.SupportedCRS;

/**
 * SupportedCrs
 */
public class SupportedCrs {

  private String crs = null;

  private Boolean _default = null;

  public SupportedCrs() {

  }

  public SupportedCrs(String crs, Boolean _default) {
      this._default = _default;
      this.crs = crs;
  }

  public SupportedCrs(SupportedCRS crs) {
      if (crs != null) {
          this._default = crs.isDefault();
          this.crs = crs.getValue();
      }
  }

  public SupportedCrs(SupportedCrs crs) {
      if (crs != null) {
          this._default = crs.isDefault();
          this.crs = crs.getCrs();
      }
  }


  public SupportedCrs crs(String crs) {
    this.crs = crs;
    return this;
  }


  /**
  * Get crs
  * @return crs
  **/
  public String getCrs() {
    return crs;
  }
  public void setCrs(String crs) {
    this.crs = crs;
  }

  public SupportedCrs _default(Boolean _default) {
    this._default = _default;
    return this;
  }


  /**
  * Get _default
  * @return _default
  **/
  public Boolean isDefault() {
    return _default;
  }
  public void setDefault(Boolean _default) {
    this._default = _default;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SupportedCrs supportedCrs = (SupportedCrs) o;
    return Objects.equals(this.crs, supportedCrs.crs) &&
        Objects.equals(this._default, supportedCrs._default);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(crs, _default);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SupportedCrs {\n");

    sb.append("    crs: ").append(toIndentedString(crs)).append("\n");
    sb.append("    _default: ").append(toIndentedString(_default)).append("\n");
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



