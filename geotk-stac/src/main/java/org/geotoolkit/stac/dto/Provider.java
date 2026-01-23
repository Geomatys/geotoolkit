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
package org.geotoolkit.stac.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/EO-Data-Discovery">OpenEO Doc</a>
 * Based on : <a href="https://github.com/radiantearth/stac-spec/blob/master/collection-spec/collection-spec.md">STAC Spec Github</a>
 */
@JsonPropertyOrder({
        Provider.JSON_PROPERTY_NAME,
        Provider.JSON_PROPERTY_URL,
        Provider.JSON_PROPERTY_ROLES,
        Provider.JSON_PROPERTY_DESCRIPTION
})
@XmlRootElement(name = "Provider")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Provider")
public class Provider extends DataTransferObject {

    // --- PROPERTY CONSTANTS ---
    public static final String JSON_PROPERTY_NAME = "name";
    public static final String JSON_PROPERTY_URL = "url";
    public static final String JSON_PROPERTY_ROLES = "roles";
    public static final String JSON_PROPERTY_DESCRIPTION = "description";

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "url")
    private URI url;

    @XmlElement(name = "roles")
    private List<String> roles = new ArrayList<>();

    @XmlElement(name = "description")
    private String description;

    public Provider name(String name) {
        this.name = name;
        return this;
    }

    public Provider url(URI url) {
        this.url = url;
        return this;
    }

    public Provider description(String description) {
        this.description = description;
        return this;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(JSON_PROPERTY_URL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "url")
    public URI getUrl() {
        return url;
    }

    @JsonProperty(JSON_PROPERTY_URL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "url")
    public void setUrl(URI url) {
        this.url = url;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ROLES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "roles")
    public List<String> getRoles() {
        return roles;
    }

    @JsonProperty(JSON_PROPERTY_ROLES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "roles")
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Provider provider = (Provider) o;
        return Objects.equals(this.name, provider.name) &&
                Objects.equals(this.url, provider.url) &&
                Objects.equals(this.roles, provider.roles) &&
                Objects.equals(this.description, provider.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, roles, description);
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
