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
package org.geotoolkit.ogcapi.dto.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Crs;
import org.geotoolkit.ogcapi.dto.common.Link;

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
public final class TileMatrixSetItem extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_URI = "uri";
    private URI uri;

    public static final String JSON_PROPERTY_CRS = "crs";
    private Crs crs;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public TileMatrixSetItem() {
    }

    public TileMatrixSetItem id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Optional local tile matrix set identifier, e.g. for use as unspecified &#x60;{tileMatrixSetId}&#x60; parameter.
     * Implementation of &#39;identifier&#39;
     *
     * @return id
     */
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setId(String id) {
        this.id = id;
    }

    public TileMatrixSetItem title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Title of this tile matrix set, normally used for display to a human
     *
     * @return title
     */
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTitle(String title) {
        this.title = title;
    }

    public TileMatrixSetItem uri(URI uri) {
        this.uri = uri;
        return this;
    }

    /**
     * Reference to an official source for this tileMatrixSet
     *
     * @return uri
     */
    @JsonProperty(JSON_PROPERTY_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public URI getUri() {
        return uri;
    }

    @JsonProperty(JSON_PROPERTY_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUri(URI uri) {
        this.uri = uri;
    }

    public TileMatrixSetItem crs(Crs crs) {
        this.crs = crs;
        return this;
    }

    /**
     * Get crs
     *
     * @return crs
     */
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Crs getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCrs(Crs crs) {
        this.crs = crs;
    }

    public TileMatrixSetItem links(List<Link> links) {
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
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setLinks(List<Link> links) {
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
