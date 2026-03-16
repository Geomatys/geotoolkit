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
package org.geotoolkit.ogcapi.model.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.ogcapi.model.common.Crs;
import org.geotoolkit.ogcapi.model.common.Link;

/**
 * A minimal tile matrix set element for use within a list of tile matrix sets linking to a full definition.
 */
@JsonPropertyOrder({
    TileMatrixSetItem.JSON_PROPERTY_ID,
    TileMatrixSetItem.JSON_PROPERTY_TITLE,
    TileMatrixSetItem.JSON_PROPERTY_URI,
    TileMatrixSetItem.JSON_PROPERTY_CRS,
    TileMatrixSetItem.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "TileMatrixSetItem")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "TileMatrixSetItem")
public final class TileMatrixSetItem extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nullable
    private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_URI = "uri";
    @XmlElement(name = "uri")
    @jakarta.annotation.Nullable
    private URI uri;

    public static final String JSON_PROPERTY_CRS = "crs";
    @XmlElement(name = "crs")
    @jakarta.annotation.Nullable
    private Crs crs;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    public TileMatrixSetItem() {
    }

    public TileMatrixSetItem id(@jakarta.annotation.Nullable String id) {
        this.id = id;
        return this;
    }

    /**
     * Optional local tile matrix set identifier, e.g. for use as unspecified &#x60;{tileMatrixSetId}&#x60; parameter.
     * Implementation of &#39;identifier&#39;
     *
     * @return id
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "id")
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "id")
    public void setId(@jakarta.annotation.Nullable String id) {
        this.id = id;
    }

    public TileMatrixSetItem title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

    /**
     * Title of this tile matrix set, normally used for display to a human
     *
     * @return title
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "title")
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "title")
    public void setTitle(@jakarta.annotation.Nullable String title) {
        this.title = title;
    }

    public TileMatrixSetItem uri(@jakarta.annotation.Nullable URI uri) {
        this.uri = uri;
        return this;
    }

    /**
     * Reference to an official source for this tileMatrixSet
     *
     * @return uri
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "uri")
    public URI getUri() {
        return uri;
    }

    @JsonProperty(JSON_PROPERTY_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "uri")
    public void setUri(@jakarta.annotation.Nullable URI uri) {
        this.uri = uri;
    }

    public TileMatrixSetItem crs(@jakarta.annotation.Nullable Crs crs) {
        this.crs = crs;
        return this;
    }

    /**
     * Get crs
     *
     * @return crs
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    public Crs getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    public void setCrs(@jakarta.annotation.Nullable Crs crs) {
        this.crs = crs;
    }

    public TileMatrixSetItem links(@jakarta.annotation.Nonnull List<Link> links) {
        this.links = links;
        return this;
    }

    public TileMatrixSetItem addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Links to related resources. A &#39;self&#39; link to the tile matrix set definition is required.
     *
     * @return links
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setLinks(@jakarta.annotation.Nonnull List<Link> links) {
        this.links = links;
    }

    /**
     * Return true if this tileMatrixSet-item object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TileMatrixSetItem other = (TileMatrixSetItem) o;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.title, other.title)
                && Objects.equals(this.uri, other.uri)
                && Objects.equals(this.crs, other.crs)
                && Objects.equals(this.links, other.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, uri, crs, links);
    }

}
