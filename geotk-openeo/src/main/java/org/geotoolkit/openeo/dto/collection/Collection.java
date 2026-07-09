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
package org.geotoolkit.openeo.dto.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.dto.common.CollectionDescription;

import java.util.Map;
import java.util.Objects;

/**
 * A STAC Collection exposed by an OpenEO backend.
 * Extends the OGC API - Common {@link CollectionDescription} (id, title, description, keywords,
 * links, extent) with the STAC-specific fields.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/EO-Data-Discovery/operation/describe-collection">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        Collection.JSON_PROPERTY_STAC_VERSION,
        Collection.JSON_PROPERTY_SUMMARIES,
        Collection.JSON_PROPERTY_ASSETS,
        Collection.JSON_PROPERTY_PROVIDERS
})
@XmlRootElement(name = "Collection")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Collection")
public class Collection extends CollectionDescription {

    public static final String JSON_PROPERTY_STAC_VERSION = "stac_version";
    @XmlElement(name = "stac_version")
    @JacksonXmlProperty(localName = "stac_version")
    @jakarta.annotation.Nonnull
    private String stacVersion;

    public static final String JSON_PROPERTY_SUMMARIES = "summaries";
    @XmlElement(name = "summaries")
    @JacksonXmlProperty(localName = "summaries")
    @jakarta.annotation.Nullable
    private Map<String, Object> summaries;

    public static final String JSON_PROPERTY_ASSETS = "assets";
    @XmlElement(name = "assets")
    @JacksonXmlProperty(localName = "assets")
    @jakarta.annotation.Nullable
    private Map<String, Object> assets;

    public static final String JSON_PROPERTY_PROVIDERS = "providers";
    @XmlElement(name = "providers")
    @JacksonXmlProperty(localName = "providers")
    @jakarta.annotation.Nullable
    private Object providers;

    public Collection stacVersion(String stacVersion) { this.stacVersion = stacVersion; return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_STAC_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_STAC_VERSION)
    public String getStacVersion() { return stacVersion; }

    @JsonProperty(JSON_PROPERTY_STAC_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_STAC_VERSION)
    public void setStacVersion(@jakarta.annotation.Nonnull String stacVersion) { this.stacVersion = stacVersion; }

    public Collection summaries(Map<String, Object> summaries) { this.summaries = summaries; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SUMMARIES)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_SUMMARIES)
    public Map<String, Object> getSummaries() { return summaries; }

    @JsonProperty(JSON_PROPERTY_SUMMARIES)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_SUMMARIES)
    public void setSummaries(@jakarta.annotation.Nullable Map<String, Object> summaries) { this.summaries = summaries; }

    public Collection assets(Map<String, Object> assets) { this.assets = assets; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ASSETS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ASSETS)
    public Map<String, Object> getAssets() { return assets; }

    @JsonProperty(JSON_PROPERTY_ASSETS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ASSETS)
    public void setAssets(@jakarta.annotation.Nullable Map<String, Object> assets) { this.assets = assets; }

    public Collection providers(Object providers) { this.providers = providers; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PROVIDERS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_PROVIDERS)
    public Object getProviders() { return providers; }

    @JsonProperty(JSON_PROPERTY_PROVIDERS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_PROVIDERS)
    public void setProviders(@jakarta.annotation.Nullable Object providers) { this.providers = providers; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        Collection that = (Collection) o;
        return Objects.equals(this.stacVersion, that.stacVersion)
                && Objects.equals(this.summaries, that.summaries)
                && Objects.equals(this.assets, that.assets)
                && Objects.equals(this.providers, that.providers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), stacVersion, summaries, assets, providers);
    }
}
