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
import org.geotoolkit.wps.xml.v200.Format;
import org.geotoolkit.wps.xml.v200.InputDescription;
import org.geotoolkit.wps.xml.v200.OutputDescription;

/**
 * DataDescription
 */
public class DataDescriptionType extends DescriptionType {

  private List<FormatDescription> formats = new ArrayList<>();

  public DataDescriptionType() {

  }

  public DataDescriptionType(InputDescription desc) {
      super(desc);
      if (desc != null && desc.getDataDescription() != null) {
          this.formats = new ArrayList<>();
          for (Format format : desc.getDataDescription().getFormat()) {
              this.formats.add(new FormatDescription(format));
          }
      }
  }

  public DataDescriptionType(OutputDescription desc) {
      super(desc);
      if (desc != null && desc.getDataDescription() != null) {
          this.formats = new ArrayList<>();
          for (Format format : desc.getDataDescription().getFormat()) {
              this.formats.add(new FormatDescription(format));
          }
      }
  }

  public DataDescriptionType formats(List<FormatDescription> formats) {
    this.formats = formats;
    return this;
  }

  public DataDescriptionType addFormatsItem(FormatDescription formatsItem) {

    this.formats.add(formatsItem);
    return this;
  }

  /**
  * Get formats
  * @return formats
  **/
  public List<FormatDescription> getFormats() {
    return formats;
  }
  public void setFormats(List<FormatDescription> formats) {
    this.formats = formats;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataDescriptionType dataDescriptionType = (DataDescriptionType) o;
    return Objects.equals(this.formats, dataDescriptionType.formats) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formats, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataDescriptionType {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    formats: ").append(toIndentedString(formats)).append("\n");
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



