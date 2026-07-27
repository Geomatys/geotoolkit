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

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geotoolkit.ogcapi.dto.common.Link;


/**
 * GetTileMatrixSetsList200Response
 */
@JsonPropertyOrder({
    GetTileMatrixSetsListResponse.JSON_PROPERTY_TILE_MATRIX_SETS
})
public class GetTileMatrixSetsListResponse {

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_TILE_MATRIX_SETS = "tileMatrixSets";
    private List<TileMatrixSetItem> tileMatrixSets = new ArrayList<>();

    public GetTileMatrixSetsListResponse() {
    }

    public GetTileMatrixSetsListResponse links(List<Link> links) {
        this.links = links;
        return this;
    }

    public GetTileMatrixSetsListResponse addLinksItem(Link linksItem) {
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

    public GetTileMatrixSetsListResponse tileMatrixSets(List<TileMatrixSetItem> tileMatrixSets) {
        this.tileMatrixSets = tileMatrixSets;
        return this;
    }

    public GetTileMatrixSetsListResponse addTileMatrixSetsItem(TileMatrixSetItem tileMatrixSetsItem) {
        if (this.tileMatrixSets == null) {
            this.tileMatrixSets = new ArrayList<>();
        }
        this.tileMatrixSets.add(tileMatrixSetsItem);
        return this;
    }

    /**
     * Get tileMatrixSets
     *
     * @return tileMatrixSets
     */
    @JsonProperty(JSON_PROPERTY_TILE_MATRIX_SETS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<TileMatrixSetItem> getTileMatrixSets() {
        return tileMatrixSets;
    }

    @JsonProperty(JSON_PROPERTY_TILE_MATRIX_SETS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTileMatrixSets(List<TileMatrixSetItem> tileMatrixSets) {
        this.tileMatrixSets = tileMatrixSets;
    }

    /**
     * Return true if this getTileMatrixSetsList_200_response object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GetTileMatrixSetsListResponse other = (GetTileMatrixSetsListResponse) o;
        return Objects.equals(this.links, other.links)
                && Objects.equals(this.tileMatrixSets, other.tileMatrixSets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(links, tileMatrixSets);
    }

}
