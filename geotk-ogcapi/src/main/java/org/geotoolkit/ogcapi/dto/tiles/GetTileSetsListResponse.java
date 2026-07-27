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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * DatasetVectorGetTileSetsList200Response
 */
@JsonPropertyOrder({
    GetTileSetsListResponse.JSON_PROPERTY_LINKS,
    GetTileSetsListResponse.JSON_PROPERTY_TILESETS
})
public final class GetTileSetsListResponse extends DataTransferObject {

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_TILESETS = "tilesets";
    private List<TileSetItem> tilesets = new ArrayList<>();

    public GetTileSetsListResponse() {
    }

    public GetTileSetsListResponse links(List<Link> links) {
        this.links = links;
        return this;
    }

    public GetTileSetsListResponse addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Get links
     *
     * @return links
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public GetTileSetsListResponse tilesets(List<TileSetItem> tilesets) {
        this.tilesets = tilesets;
        return this;
    }

    public GetTileSetsListResponse addTilesetsItem(TileSetItem tilesetsItem) {
        if (this.tilesets == null) {
            this.tilesets = new ArrayList<>();
        }
        this.tilesets.add(tilesetsItem);
        return this;
    }

    /**
     * Get tilesets
     *
     * @return tilesets
     */
    @JsonProperty(JSON_PROPERTY_TILESETS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<TileSetItem> getTilesets() {
        return tilesets;
    }

    @JsonProperty(JSON_PROPERTY_TILESETS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTilesets(List<TileSetItem> tilesets) {
        this.tilesets = tilesets;
    }

    /**
     * Return true if this _dataset_vector_getTileSetsList_200_response object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GetTileSetsListResponse other = (GetTileSetsListResponse) o;
        return Objects.equals(this.links, other.links)
                && Objects.equals(this.tilesets, other.tilesets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(links, tilesets);
    }

}
