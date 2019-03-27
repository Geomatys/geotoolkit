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
 * LandingPage
 */
public class LandingPage {

  private List<JsonLink> links = new ArrayList<>();

  public LandingPage links(List<JsonLink> links) {
    this.links = links;
    return this;
  }

  public LandingPage addLinksItem(JsonLink linksItem) {

    this.links.add(linksItem);
    return this;
  }

  /**
  * Get links
  * @return links
  **/
  public List<JsonLink> getLinks() {
    return links;
  }
  public void setLinks(List<JsonLink> links) {
    this.links = links;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LandingPage landingPage = (LandingPage) o;
    return Objects.equals(this.links, landingPage.links);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(links);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LandingPage {\n");

    sb.append("    links: ").append(toIndentedString(links)).append("\n");
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



