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
 * A minimal tileset element for use within a list of tilesets linking to full description of those tilesets.
 */
@JsonPropertyOrder({
    TileSetItem.JSON_PROPERTY_TITLE,
    TileSetItem.JSON_PROPERTY_DATA_TYPE,
    TileSetItem.JSON_PROPERTY_CRS,
    TileSetItem.JSON_PROPERTY_TILE_MATRIX_SET_U_R_I,
    TileSetItem.JSON_PROPERTY_LINKS
})
public final class TileSetItem extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DATA_TYPE = "dataType";
    private String dataType;

    public static final String JSON_PROPERTY_CRS = "crs";
    private Crs crs;

    public static final String JSON_PROPERTY_TILE_MATRIX_SET_U_R_I = "tileMatrixSetURI";
    private URI tileMatrixSetURI;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public TileSetItem() {
    }

    public TileSetItem title(String title) {
        this.title = title;
        return this;
    }

    /**
     * A title for this tileset
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

    public TileSetItem dataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * Get dataType
     *
     * @return dataType
     */
    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getDataType() {
        return dataType;
    }

    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public TileSetItem crs(Crs crs) {
        this.crs = crs;
        return this;
    }

    /**
     * Get crs
     *
     * @return crs
     */
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Crs getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setCrs(Crs crs) {
        this.crs = crs;
    }

    public TileSetItem tileMatrixSetURI(URI tileMatrixSetURI) {
        this.tileMatrixSetURI = tileMatrixSetURI;
        return this;
    }

    /**
     * Reference to a Tile Matrix Set on an offical source for Tile Matrix Sets such as the OGC NA definition server
     * (http://www.opengis.net/def/tms/). Required if the tile matrix set is registered on an open official source.
     *
     * @return tileMatrixSetURI
     */
    @JsonProperty(JSON_PROPERTY_TILE_MATRIX_SET_U_R_I)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public URI getTileMatrixSetURI() {
        return tileMatrixSetURI;
    }

    @JsonProperty(JSON_PROPERTY_TILE_MATRIX_SET_U_R_I)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTileMatrixSetURI(URI tileMatrixSetURI) {
        this.tileMatrixSetURI = tileMatrixSetURI;
    }

    public TileSetItem links(List<Link> links) {
        this.links = links;
        return this;
    }

    public TileSetItem addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Links to related resources. A &#39;self&#39; link to the tileset as well as a
     * &#39;http://www.opengis.net/def/rel/ogc/1.0/tiling-scheme&#39; link to a definition of the TileMatrixSet are
     * required.
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
     * Return true if this tileSet-item object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TileSetItem other = (TileSetItem) o;
        return Objects.equals(this.title, other.title)
                && Objects.equals(this.dataType, other.dataType)
                && Objects.equals(this.crs, other.crs)
                && Objects.equals(this.tileMatrixSetURI, other.tileMatrixSetURI)
                && Objects.equals(this.links, other.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, dataType, crs, tileMatrixSetURI, links);
    }

}
