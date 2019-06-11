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
import org.geotoolkit.atom.xml.Link;

/**
 * JsonLink
 */
public class JsonLink {

  private String href = null;

  private String rel = null;

  private String type = null;

  private String hreflang = null;

  private String title = null;

  public JsonLink() {

  }

  public JsonLink(String href) {
    this.href = href;
  }

  public JsonLink(String href, String rel, String type, String title, String hreflang) {
    this.href = href;
    this.rel = rel;
    this.type = type;
    this.title = title;
    this.hreflang = hreflang;
  }

  public JsonLink(Link that) {
    if (that != null) {
        this.href = that.getHref();
        this.rel = that.getRel();
        this.type = that.getType();
        this.title = that.getTitle();
        this.hreflang = that.getHreflang();
    }
  }

  public JsonLink href(String href) {
    this.href = href;
    return this;
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

  public JsonLink rel(String rel) {
    this.rel = rel;
    return this;
  }


  /**
  * Get rel
  * @return rel
  **/
  public String getRel() {
    return rel;
  }
  public void setRel(String rel) {
    this.rel = rel;
  }

  public JsonLink type(String type) {
    this.type = type;
    return this;
  }


  /**
  * Get type
  * @return type
  **/
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  public JsonLink hreflang(String hreflang) {
    this.hreflang = hreflang;
    return this;
  }


  /**
  * Get hreflang
  * @return hreflang
  **/
  public String getHreflang() {
    return hreflang;
  }
  public void setHreflang(String hreflang) {
    this.hreflang = hreflang;
  }

  public JsonLink title(String title) {
    this.title = title;
    return this;
  }


  /**
  * Get title
  * @return title
  **/
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JsonLink jsonLink = (JsonLink) o;
    return Objects.equals(this.href, jsonLink.href) &&
        Objects.equals(this.rel, jsonLink.rel) &&
        Objects.equals(this.type, jsonLink.type) &&
        Objects.equals(this.hreflang, jsonLink.hreflang) &&
        Objects.equals(this.title, jsonLink.title);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(href, rel, type, hreflang, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JsonLink {\n");

    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    rel: ").append(toIndentedString(rel)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    hreflang: ").append(toIndentedString(hreflang)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
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



