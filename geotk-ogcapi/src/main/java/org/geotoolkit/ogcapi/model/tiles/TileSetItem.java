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
 * A minimal tileset element for use within a list of tilesets linking to full description of those tilesets.
 */
@JsonPropertyOrder({
    TileSetItem.JSON_PROPERTY_TITLE,
    TileSetItem.JSON_PROPERTY_DATA_TYPE,
    TileSetItem.JSON_PROPERTY_CRS,
    TileSetItem.JSON_PROPERTY_TILE_MATRIX_SET_U_R_I,
    TileSetItem.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "TileSetItem")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "TileSetItem")
public final class TileSetItem extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DATA_TYPE = "dataType";
    @XmlElement(name = "dataType")
    @jakarta.annotation.Nonnull
    private String dataType;

    public static final String JSON_PROPERTY_CRS = "crs";
    @XmlElement(name = "crs")
    @jakarta.annotation.Nonnull
    private Crs crs;

    public static final String JSON_PROPERTY_TILE_MATRIX_SET_U_R_I = "tileMatrixSetURI";
    @XmlElement(name = "tileMatrixSetURI")
    @jakarta.annotation.Nullable
    private URI tileMatrixSetURI;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    public TileSetItem() {
    }

    public TileSetItem title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

    /**
     * A title for this tileset
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

    public TileSetItem dataType(@jakarta.annotation.Nonnull String dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * Get dataType
     *
     * @return dataType
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "dataType")
    public String getDataType() {
        return dataType;
    }

    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "dataType")
    public void setDataType(@jakarta.annotation.Nonnull String dataType) {
        this.dataType = dataType;
    }

    public TileSetItem crs(@jakarta.annotation.Nonnull Crs crs) {
        this.crs = crs;
        return this;
    }

    /**
     * Get crs
     *
     * @return crs
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "crs")
    public Crs getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "crs")
    public void setCrs(@jakarta.annotation.Nonnull Crs crs) {
        this.crs = crs;
    }

    public TileSetItem tileMatrixSetURI(@jakarta.annotation.Nullable URI tileMatrixSetURI) {
        this.tileMatrixSetURI = tileMatrixSetURI;
        return this;
    }

    /**
     * Reference to a Tile Matrix Set on an offical source for Tile Matrix Sets such as the OGC NA definition server
     * (http://www.opengis.net/def/tms/). Required if the tile matrix set is registered on an open official source.
     *
     * @return tileMatrixSetURI
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TILE_MATRIX_SET_U_R_I)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "tileMatrixSetURI")
    public URI getTileMatrixSetURI() {
        return tileMatrixSetURI;
    }

    @JsonProperty(JSON_PROPERTY_TILE_MATRIX_SET_U_R_I)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "tileMatrixSetURI")
    public void setTileMatrixSetURI(@jakarta.annotation.Nullable URI tileMatrixSetURI) {
        this.tileMatrixSetURI = tileMatrixSetURI;
    }

    public TileSetItem links(@jakarta.annotation.Nonnull List<Link> links) {
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
