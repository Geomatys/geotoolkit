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
import org.geotoolkit.ogcapi.model.common.CollectionDescription;
import org.geotoolkit.ogcapi.model.common.Extent;
import org.geotoolkit.ogcapi.model.common.Link;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Capabilities">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        Collection.JSON_PROPERTY_STAC_VERSION,
        Collection.JSON_PROPERTY_STAC_EXTENSIONS,
        Collection.JSON_PROPERTY_TYPE,
        Collection.JSON_PROPERTY_KEYWORDS,
        Collection.JSON_PROPERTY_DIMENSIONS,
        Collection.JSON_PROPERTY_DEPRECATED,
        Collection.JSON_PROPERTY_LICENSE,
        Collection.JSON_PROPERTY_SUMMARIES,
        Collection.JSON_PROPERTY_PROVIDERS
})
@XmlRootElement(name = "Collection")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Collection")
public class Collection extends CollectionDescription {
    // --- PROPERTY CONSTANTS ---
    public static final String JSON_PROPERTY_STAC_VERSION = "stac_version";
    public static final String JSON_PROPERTY_STAC_EXTENSIONS = "stac_extensions";
    public static final String JSON_PROPERTY_TYPE = "type";
    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    public static final String JSON_PROPERTY_DIMENSIONS = "cube:dimensions";
    public static final String JSON_PROPERTY_DEPRECATED = "deprecated";
    public static final String JSON_PROPERTY_LICENSE = "license";
    public static final String JSON_PROPERTY_SUMMARIES = "summaries";
    public static final String JSON_PROPERTY_PROVIDERS = "providers";

    public Collection() {}

    public Collection(String id, List<Link> links, String title, String description, String name, Extent extent,
                      List<String> crs, String storageCrs, String stacVersion, Set<String> stacExtensions,
                      List<String> keywords, Map<String, Dimension> dimensions, Boolean deprecated, String license,
                      Map<String, Object> summaries) {
        super(id, links, title, description, name, extent, crs, storageCrs);
        this.stacVersion = stacVersion;
        this.stacExtensions = stacExtensions;
        this.keywords = keywords;
        this.deprecated = deprecated;
        this.license = license;
        this.dimensions = dimensions;
        this.summaries = summaries;
    }

    public Collection(CollectionDescription collection, String stacVersion,
                      Set<String> stacExtensions, List<String> keywords, Map<String, Dimension> dimensions, Boolean deprecated,
                      String license, Map<String, Object> summaries) {
        super(collection);
        this.stacVersion = stacVersion;
        this.stacExtensions = stacExtensions;
        this.keywords = keywords;
        this.deprecated = deprecated;
        this.license = license;
        this.dimensions = dimensions;
        this.summaries = summaries;
    }

    @XmlElement(name = "stac_version")
    private String stacVersion;

    @XmlElement(name = "stac_extensions")
    private Set<String> stacExtensions = new HashSet<>();

    @XmlElement(name = "type")
    private final String type = "Collection";

    @XmlElement(name = "keywords")
    private List<String> keywords = new ArrayList<>();

    @XmlElement(name = "cube:dimensions")
    private Map<String, Dimension> dimensions;

    @XmlElement(name = "deprecated")
    private Boolean deprecated = false;

    @XmlElement(name = "license")
    private String license;

    @XmlElement(name = "summaries")
    private Map<String, Object> summaries;

    @XmlElement(name = "providers")
    private List<Provider> providers = null;

    @JsonProperty(JSON_PROPERTY_STAC_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "stac_version")
    public String getStacVersion() {
        return stacVersion;
    }

    @JsonProperty(JSON_PROPERTY_STAC_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "stac_version")
    public void setStacVersion(String stacVersion) {
        this.stacVersion = stacVersion;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_STAC_EXTENSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "stac_extensions")
    public Set<String> getStacExtensions() {
        return stacExtensions;
    }

    @JsonProperty(JSON_PROPERTY_STAC_EXTENSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "stac_extensions")
    public void setStacExtensions(Set<String> stacExtensions) {
        this.stacExtensions = stacExtensions;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public String getType() {
        return type;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "keywords")
    public List<String> getKeywords() {
        return keywords;
    }

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "keywords")
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    @JsonProperty(JSON_PROPERTY_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "cube:dimensions")
    public Map<String, Dimension> getDimensions() {
        return dimensions;
    }

    @JsonProperty(JSON_PROPERTY_DEPRECATED)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "deprecated")
    public Boolean getDeprecated() {
        return deprecated;
    }

    @JsonProperty(JSON_PROPERTY_DEPRECATED)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "deprecated")
    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    @JsonProperty(JSON_PROPERTY_LICENSE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "license")
    public String getLicense() {
        return license;
    }

    @JsonProperty(JSON_PROPERTY_LICENSE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "license")
    public void setLicense(String license) {
        this.license = license;
    }

    @JsonProperty(JSON_PROPERTY_SUMMARIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "summaries")
    public Map<String, Object> getSummaries() {
        return summaries;
    }

    @JsonProperty(JSON_PROPERTY_PROVIDERS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "providers")
    public List<Provider> getProviders() {
        return providers;
    }

    @JsonProperty(JSON_PROPERTY_PROVIDERS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "providers")
    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collection that = (Collection) o;
        return Objects.equals(stacVersion, that.stacVersion) && Objects.equals(stacExtensions, that.stacExtensions) && Objects.equals(type, that.type) && Objects.equals(keywords, that.keywords) && Objects.equals(deprecated, that.deprecated) && Objects.equals(license, that.license) && Objects.equals(providers, that.providers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stacVersion, stacExtensions, type, keywords, deprecated, license, providers);
    }
}
