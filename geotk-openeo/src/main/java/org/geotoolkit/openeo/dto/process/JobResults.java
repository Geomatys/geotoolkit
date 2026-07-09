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
package org.geotoolkit.openeo.dto.process;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.dto.common.Link;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Batch job results as a STAC FeatureCollection.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Batch-Jobs/operation/list-results">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        JobResults.JSON_PROPERTY_TYPE,
        JobResults.JSON_PROPERTY_FEATURES,
        JobResults.JSON_PROPERTY_ASSETS,
        JobResults.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "JobResults")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "JobResults")
public class JobResults extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @JacksonXmlProperty(localName = "type")
    @jakarta.annotation.Nonnull
    private String type;

    public static final String JSON_PROPERTY_FEATURES = "features";
    @XmlElementWrapper(name = "features")
    @XmlElement(name = "feature")
    @JacksonXmlElementWrapper(localName = "features", useWrapping = false)
    @JacksonXmlProperty(localName = "feature")
    @jakarta.annotation.Nonnull
    private List<Object> features = new ArrayList<>();

    public static final String JSON_PROPERTY_ASSETS = "assets";
    @XmlElement(name = "assets")
    @JacksonXmlProperty(localName = "assets")
    @jakarta.annotation.Nullable
    private Map<String, Object> assets;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @JacksonXmlElementWrapper(localName = "links", useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public JobResults type(String type) { this.type = type; return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_TYPE)
    public String getType() { return type; }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_TYPE)
    public void setType(@jakarta.annotation.Nonnull String type) { this.type = type; }

    public JobResults features(List<Object> features) { this.features = features; return this; }
    public JobResults addFeaturesItem(Object feature) { this.features.add(feature); return this; }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_FEATURES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_FEATURES)
    public List<Object> getFeatures() { return features; }

    @JsonProperty(JSON_PROPERTY_FEATURES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_FEATURES)
    public void setFeatures(@jakarta.annotation.Nonnull List<Object> features) { this.features = features; }

    public JobResults assets(Map<String, Object> assets) { this.assets = assets; return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ASSETS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ASSETS)
    public Map<String, Object> getAssets() { return assets; }

    @JsonProperty(JSON_PROPERTY_ASSETS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_ASSETS)
    public void setAssets(@jakarta.annotation.Nullable Map<String, Object> assets) { this.assets = assets; }

    public JobResults links(List<Link> links) { this.links = links; return this; }
    public JobResults addLinksItem(Link link) { if (this.links == null) this.links = new ArrayList<>(); this.links.add(link); return this; }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public List<Link> getLinks() { return links; }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) { this.links = links; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobResults that = (JobResults) o;
        return Objects.equals(this.type, that.type)
                && Objects.equals(this.features, that.features)
                && Objects.equals(this.assets, that.assets)
                && Objects.equals(this.links, that.links);
    }

    @Override
    public int hashCode() { return Objects.hash(type, features, assets, links); }
}
