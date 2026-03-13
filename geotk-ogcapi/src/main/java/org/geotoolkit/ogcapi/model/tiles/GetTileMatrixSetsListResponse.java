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

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import org.geotoolkit.ogcapi.model.common.Link;


/**
 * GetTileMatrixSetsList200Response
 */
@JsonPropertyOrder({
    GetTileMatrixSetsListResponse.JSON_PROPERTY_TILE_MATRIX_SETS
})
@XmlRootElement(name = "GetTileMatrixSetsList200Response")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "GetTileMatrixSetsList200Response")
public class GetTileMatrixSetsListResponse {

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_TILE_MATRIX_SETS = "tileMatrixSets";
    @XmlElement(name = "tileMatrixSets")
    @jakarta.annotation.Nullable
    private List<TileMatrixSetItem> tileMatrixSets = new ArrayList<>();

    public GetTileMatrixSetsListResponse() {
    }

    public GetTileMatrixSetsListResponse links(@jakarta.annotation.Nullable List<Link> links) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
    }

    public GetTileMatrixSetsListResponse tileMatrixSets(@jakarta.annotation.Nullable List<TileMatrixSetItem> tileMatrixSets) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TILE_MATRIX_SETS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "tileMatrixSets")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<TileMatrixSetItem> getTileMatrixSets() {
        return tileMatrixSets;
    }

    @JsonProperty(JSON_PROPERTY_TILE_MATRIX_SETS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "tileMatrixSets")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setTileMatrixSets(@jakarta.annotation.Nullable List<TileMatrixSetItem> tileMatrixSets) {
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
