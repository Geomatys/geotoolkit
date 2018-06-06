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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * InputDescription
 */
public class InputDescription {

  private Integer minOccurs = null;

  private Integer maxOccurs = null;

  @JsonProperty("LiteralDataDomain")
  private Object literalDataDomain = null;

  public InputDescription minOccurs(Integer minOccurs) {
    this.minOccurs = minOccurs;
    return this;
  }


  /**
  * Get minOccurs
  * @return minOccurs
  **/
  public Integer getMinOccurs() {
    return minOccurs;
  }
  public void setMinOccurs(Integer minOccurs) {
    this.minOccurs = minOccurs;
  }

  public InputDescription maxOccurs(Integer maxOccurs) {
    this.maxOccurs = maxOccurs;
    return this;
  }


  /**
  * Get maxOccurs
  * @return maxOccurs
  **/
  public Integer getMaxOccurs() {
    return maxOccurs;
  }
  public void setMaxOccurs(Integer maxOccurs) {
    this.maxOccurs = maxOccurs;
  }

  public InputDescription literalDataDomain(Object literalDataDomain) {
    this.literalDataDomain = literalDataDomain;
    return this;
  }


  /**
  * Get literalDataDomain
  * @return literalDataDomain
  **/
  public Object getLiteralDataDomain() {
    return literalDataDomain;
  }
  public void setLiteralDataDomain(Object literalDataDomain) {
    this.literalDataDomain = literalDataDomain;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InputDescription inputDescription = (InputDescription) o;
    return Objects.equals(this.minOccurs, inputDescription.minOccurs) &&
        Objects.equals(this.maxOccurs, inputDescription.maxOccurs) &&
        Objects.equals(this.literalDataDomain, inputDescription.literalDataDomain);
  }

  @Override
  public int hashCode() {
    return Objects.hash(minOccurs, maxOccurs, literalDataDomain);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InputDescription {\n");

    sb.append("    minOccurs: ").append(toIndentedString(minOccurs)).append("\n");
    sb.append("    maxOccurs: ").append(toIndentedString(maxOccurs)).append("\n");
    sb.append("    literalDataDomain: ").append(toIndentedString(literalDataDomain)).append("\n");
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



