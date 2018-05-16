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
import java.util.ArrayList;
import java.util.List;

/**
 * DescriptionType
 */
public class DescriptionType {

  private String id = null;
  
  private String title = null;
  
  @JsonProperty("abstract")
  private String _abstract = null;
  
  private List<String> keywords = null;
  
  private List<Metadata> metadata = null;
  
  public DescriptionType id(String id) {
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
  
  public DescriptionType title(String title) {
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
  
  public DescriptionType _abstract(String _abstract) {
    this._abstract = _abstract;
    return this;
  }

  
  /**
  * Get _abstract
  * @return _abstract
  **/
  public String getAbstract() {
    return _abstract;
  }
  public void setAbstract(String _abstract) {
    this._abstract = _abstract;
  }
  
  public DescriptionType keywords(List<String> keywords) {
    this.keywords = keywords;
    return this;
  }

  public DescriptionType addKeywordsItem(String keywordsItem) {
    
    if (this.keywords == null) {
      this.keywords = new ArrayList<>();
    }
    
    this.keywords.add(keywordsItem);
    return this;
  }
  
  /**
  * Get keywords
  * @return keywords
  **/
  public List<String> getKeywords() {
    return keywords;
  }
  public void setKeywords(List<String> keywords) {
    this.keywords = keywords;
  }
  
  public DescriptionType metadata(List<Metadata> metadata) {
    this.metadata = metadata;
    return this;
  }

  public DescriptionType addMetadataItem(Metadata metadataItem) {
    
    if (this.metadata == null) {
      this.metadata = new ArrayList<Metadata>();
    }
    
    this.metadata.add(metadataItem);
    return this;
  }
  
  /**
  * Get metadata
  * @return metadata
  **/
  public List<Metadata> getMetadata() {
    return metadata;
  }
  public void setMetadata(List<Metadata> metadata) {
    this.metadata = metadata;
  }
  
  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DescriptionType descriptionType = (DescriptionType) o;
    return Objects.equals(this.id, descriptionType.id) &&
        Objects.equals(this.title, descriptionType.title) &&
        Objects.equals(this._abstract, descriptionType._abstract) &&
        Objects.equals(this.keywords, descriptionType.keywords) &&
        Objects.equals(this.metadata, descriptionType.metadata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, _abstract, keywords, metadata);
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DescriptionType {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    _abstract: ").append(toIndentedString(_abstract)).append("\n");
    sb.append("    keywords: ").append(toIndentedString(keywords)).append("\n");
    sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
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



