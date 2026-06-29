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
import org.geotoolkit.ogcapi.dto.common.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Represents a STAC ItemCollection.
 * <p>
 * Conceptually, this is a "Search Results Page". An ItemCollection does not describe a dataset itself
 * (unlike {@link Collection}). Instead, it is a container carrying a list of individual {@link Item}s
 * that match a specific query or belong to a specific page.
 * <p>
 * It holds an array of actual STAC items (the features array) and pagination links (e.g., "next" link).
 * It is a transient payload returning the results of a search performed on a Catalog or Collection.
 */
@JsonPropertyOrder({
        ItemCollection.JSON_PROPERTY_STAC_VERSION,
        ItemCollection.JSON_PROPERTY_STAC_EXTENSIONS,
        ItemCollection.JSON_PROPERTY_TYPE,
        ItemCollection.JSON_PROPERTY_FEATURES,
        ItemCollection.JSON_PROPERTY_LINKS
})
public class ItemCollection extends DataTransferObject {

    public static final String JSON_PROPERTY_STAC_VERSION = "stac_version";
    public static final String JSON_PROPERTY_STAC_EXTENSIONS = "stac_extensions";
    public static final String JSON_PROPERTY_TYPE = "type";
    public static final String JSON_PROPERTY_FEATURES = "features";
    public static final String JSON_PROPERTY_LINKS = "links";

    @JsonProperty(JSON_PROPERTY_STAC_VERSION)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String stacVersion;

    @JsonProperty(JSON_PROPERTY_STAC_EXTENSIONS)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> stacExtensions = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_TYPE)
    private final String type = "FeatureCollection";

    @JsonProperty(JSON_PROPERTY_FEATURES)
    private List<Item> features = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Link> links = new ArrayList<>();

    public ItemCollection() {}

    public String getStacVersion() {
        return stacVersion;
    }

    public void setStacVersion(String stacVersion) {
        this.stacVersion = stacVersion;
    }

    public List<String> getStacExtensions() {
        return stacExtensions;
    }

    public void setStacExtensions(List<String> stacExtensions) {
        this.stacExtensions = stacExtensions;
    }

    public String getType() {
        return type;
    }

    public List<Item> getFeatures() {
        return features;
    }

    public void setFeatures(List<Item> features) {
        this.features = features;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemCollection that = (ItemCollection) o;
        return Objects.equals(stacVersion, that.stacVersion) && Objects.equals(stacExtensions, that.stacExtensions) && Objects.equals(type, that.type) && Objects.equals(features, that.features) && Objects.equals(links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stacVersion, stacExtensions, type, features, links);
    }
}
