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
import org.geotoolkit.ows.xml.AbstractMetadata;

/**
 * Metadata
 */
public class Metadata {

  private String role = null;

  private String href = null;

  private String value = null;

  private String rel = null;

  private String type = null;

  private String hreflang = null;

  private String title = null;

  public Metadata() {

  }

  public Metadata(Metadata that) {
      if (that != null) {
          this.href = that.href;
          this.role = that.role;
          this.hreflang = that.hreflang;
          this.rel = that.rel;
          this.title = that.title;
          this.type = that.type;
          this.value = that.value;
      }
  }

  public Metadata(AbstractMetadata that) {
      if (that != null) {
          this.href = that.getHref();
          this.role = that.getRole();
          this.title = that.getTitle();
          this.type  = that.getType();
      }
  }

  public Metadata role(String role) {
    this.role = role;
    return this;
  }

  /**
  * Get role
  * @return role
  **/
  public String getRole() {
    return role;
  }
  public void setRole(String role) {
    this.role = role;
  }

  public Metadata href(String href) {
    this.href = href;
    return this;
  }

  /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the rel
     */
    public String getRel() {
        return rel;
    }

    /**
     * @param rel the rel to set
     */
    public void setRel(String rel) {
        this.rel = rel;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the hreflang
     */
    public String getHreflang() {
        return hreflang;
    }

    /**
     * @param hreflang the hreflang to set
     */
    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }


  /**
  * Get href
  * @return href
  **/
  public String getHref() {
    return href;
  }
  public void setHref(String href) {
    this.href = href;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Metadata metadata = (Metadata) o;
    return Objects.equals(this.role, metadata.role) &&
        Objects.equals(this.href, metadata.href);
  }

  @Override
  public int hashCode() {
    return Objects.hash(role, href);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Metadata {\n");

    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
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



