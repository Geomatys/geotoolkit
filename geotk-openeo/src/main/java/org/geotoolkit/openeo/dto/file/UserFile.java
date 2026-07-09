/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.openeo.dto.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;

import java.util.List;
import java.util.Objects;

/**
 * Metadata about a file stored in the user's workspace.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/File-Storage/operation/list-files">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        UserFile.JSON_PROPERTY_PATH,
        UserFile.JSON_PROPERTY_SIZE,
        UserFile.JSON_PROPERTY_MODIFIED,
        UserFile.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "UserFile")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "UserFile")
public class UserFile extends DataTransferObject {

    public static final String JSON_PROPERTY_PATH = "path";
    @XmlElement(name = "path") @JacksonXmlProperty(localName = "path")
    @jakarta.annotation.Nonnull private String path;

    public static final String JSON_PROPERTY_SIZE = "size";
    @XmlElement(name = "size") @JacksonXmlProperty(localName = "size")
    @jakarta.annotation.Nullable private Long size;

    public static final String JSON_PROPERTY_MODIFIED = "modified";
    @XmlElement(name = "modified") @JacksonXmlProperty(localName = "modified")
    @jakarta.annotation.Nullable private String modified;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links") @JacksonXmlProperty(localName = "links")
    @jakarta.annotation.Nullable private List<Link> links;

    public UserFile path(String path) { this.path = path; return this; }
    @jakarta.annotation.Nonnull @JsonProperty(JSON_PROPERTY_PATH) @JsonInclude(value = JsonInclude.Include.ALWAYS) @JacksonXmlProperty(localName = JSON_PROPERTY_PATH)
    public String getPath() { return path; }
    @JsonProperty(JSON_PROPERTY_PATH) @JsonInclude(value = JsonInclude.Include.ALWAYS) @JacksonXmlProperty(localName = JSON_PROPERTY_PATH)
    public void setPath(@jakarta.annotation.Nonnull String path) { this.path = path; }

    public UserFile size(Long size) { this.size = size; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_SIZE) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_SIZE)
    public Long getSize() { return size; }
    @JsonProperty(JSON_PROPERTY_SIZE) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_SIZE)
    public void setSize(@jakarta.annotation.Nullable Long size) { this.size = size; }

    public UserFile modified(String modified) { this.modified = modified; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_MODIFIED) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_MODIFIED)
    public String getModified() { return modified; }
    @JsonProperty(JSON_PROPERTY_MODIFIED) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_MODIFIED)
    public void setModified(@jakarta.annotation.Nullable String modified) { this.modified = modified; }

    public UserFile links(List<Link> links) { this.links = links; return this; }
    @jakarta.annotation.Nullable @JsonProperty(JSON_PROPERTY_LINKS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public List<Link> getLinks() { return links; }
    @JsonProperty(JSON_PROPERTY_LINKS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) { this.links = links; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFile that = (UserFile) o;
        return Objects.equals(this.path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
