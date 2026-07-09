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
 * Response body for GET /files.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/File-Storage/operation/list-files">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        UserFileList.JSON_PROPERTY_FILES,
        UserFileList.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "UserFileList")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "UserFileList")
public class UserFileList extends DataTransferObject {

    public static final String JSON_PROPERTY_FILES = "files";
    @XmlElement(name = "files") @JacksonXmlProperty(localName = "files")
    @jakarta.annotation.Nonnull private List<UserFile> files;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links") @JacksonXmlProperty(localName = "links")
    @jakarta.annotation.Nullable private List<Link> links;

    public UserFileList files(List<UserFile> files) { this.files = files; return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_FILES) @JsonInclude(value = JsonInclude.Include.ALWAYS) @JacksonXmlProperty(localName = JSON_PROPERTY_FILES)
    public List<UserFile> getFiles() { return files; }

    @JsonProperty(JSON_PROPERTY_FILES) @JsonInclude(value = JsonInclude.Include.ALWAYS) @JacksonXmlProperty(localName = JSON_PROPERTY_FILES)
    public void setFiles(@jakarta.annotation.Nonnull List<UserFile> files) { this.files = files; }

    public UserFileList links(List<Link> links) { this.links = links; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public List<Link> getLinks() { return links; }

    @JsonProperty(JSON_PROPERTY_LINKS) @JsonInclude(value = JsonInclude.Include.NON_NULL) @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) { this.links = links; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFileList that = (UserFileList) o;
        return Objects.equals(this.files, that.files) && Objects.equals(this.links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(files, links);
    }
}
