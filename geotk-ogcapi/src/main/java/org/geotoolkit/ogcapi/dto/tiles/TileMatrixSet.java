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

/**
 * A definition of a tile matrix set following the Tile Matrix Set standard. For tileset metadata, such a description
 * (in &#x60;tileMatrixSet&#x60; property) is only required for offline use, as an alternative to a link with a
 * &#x60;http://www.opengis.net/def/rel/ogc/1.0/tiling-scheme&#x60; relation type.
 */
@JsonPropertyOrder({
    TileMatrixSet.JSON_PROPERTY_TITLE,
    TileMatrixSet.JSON_PROPERTY_DESCRIPTION,
    TileMatrixSet.JSON_PROPERTY_KEYWORDS,
    TileMatrixSet.JSON_PROPERTY_ID,
    TileMatrixSet.JSON_PROPERTY_URI,
    TileMatrixSet.JSON_PROPERTY_ORDERED_AXES,
    TileMatrixSet.JSON_PROPERTY_CRS,
    TileMatrixSet.JSON_PROPERTY_WELL_KNOWN_SCALE_SET,
    TileMatrixSet.JSON_PROPERTY_BOUNDING_BOX,
    TileMatrixSet.JSON_PROPERTY_TILE_MATRICES
})
public final class TileMatrixSet extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    private List<String> keywords = new ArrayList<>();

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_URI = "uri";
    private URI uri;

    public static final String JSON_PROPERTY_ORDERED_AXES = "orderedAxes";
    private List<String> orderedAxes = new ArrayList<>();

    public static final String JSON_PROPERTY_CRS = "crs";
    private Crs crs;

    public static final String JSON_PROPERTY_WELL_KNOWN_SCALE_SET = "wellKnownScaleSet";
    private URI wellKnownScaleSet;

    public static final String JSON_PROPERTY_BOUNDING_BOX = "boundingBox";
    private BoundingBox2D boundingBox;

    public static final String JSON_PROPERTY_TILE_MATRICES = "tileMatrices";
    private List<TileMatrix> tileMatrices = new ArrayList<>();

    public TileMatrixSet() {
    }

    public TileMatrixSet title(String title) {
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

    public TileMatrixSet description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Brief narrative description of this tile matrix set, normally available for display to a human
     *
     * @return description
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDescription(String description) {
        this.description = description;
    }

    public TileMatrixSet keywords(List<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public TileMatrixSet addKeywordsItem(String keywordsItem) {
        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }
        this.keywords.add(keywordsItem);
        return this;
    }

    /**
     * Unordered list of one or more commonly used or formalized word(s) or phrase(s) used to describe this tile matrix
     * set
     *
     * @return keywords
     */
    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getKeywords() {
        return keywords;
    }

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public TileMatrixSet id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Tile matrix set identifier. Implementation of &#39;identifier&#39;
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

    public TileMatrixSet uri(URI uri) {
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

    public TileMatrixSet orderedAxes(List<String> orderedAxes) {
        this.orderedAxes = orderedAxes;
        return this;
    }

    public TileMatrixSet addOrderedAxesItem(String orderedAxesItem) {
        if (this.orderedAxes == null) {
            this.orderedAxes = new ArrayList<>();
        }
        this.orderedAxes.add(orderedAxesItem);
        return this;
    }

    /**
     * Get orderedAxes
     *
     * @return orderedAxes
     */
    @JsonProperty(JSON_PROPERTY_ORDERED_AXES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getOrderedAxes() {
        return orderedAxes;
    }

    @JsonProperty(JSON_PROPERTY_ORDERED_AXES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setOrderedAxes(List<String> orderedAxes) {
        this.orderedAxes = orderedAxes;
    }

    public TileMatrixSet crs(Crs crs) {
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

    public TileMatrixSet wellKnownScaleSet(URI wellKnownScaleSet) {
        this.wellKnownScaleSet = wellKnownScaleSet;
        return this;
    }

    /**
     * Reference to a well-known scale set
     *
     * @return wellKnownScaleSet
     */
    @JsonProperty(JSON_PROPERTY_WELL_KNOWN_SCALE_SET)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public URI getWellKnownScaleSet() {
        return wellKnownScaleSet;
    }

    @JsonProperty(JSON_PROPERTY_WELL_KNOWN_SCALE_SET)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setWellKnownScaleSet(URI wellKnownScaleSet) {
        this.wellKnownScaleSet = wellKnownScaleSet;
    }

    public TileMatrixSet boundingBox(BoundingBox2D boundingBox) {
        this.boundingBox = boundingBox;
        return this;
    }

    /**
     * Get boundingBox
     *
     * @return boundingBox
     */
    @JsonProperty(JSON_PROPERTY_BOUNDING_BOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BoundingBox2D getBoundingBox() {
        return boundingBox;
    }

    @JsonProperty(JSON_PROPERTY_BOUNDING_BOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setBoundingBox(BoundingBox2D boundingBox) {
        this.boundingBox = boundingBox;
    }

    public TileMatrixSet tileMatrices(List<TileMatrix> tileMatrices) {
        this.tileMatrices = tileMatrices;
        return this;
    }

    public TileMatrixSet addTileMatricesItem(TileMatrix tileMatricesItem) {
        if (this.tileMatrices == null) {
            this.tileMatrices = new ArrayList<>();
        }
        this.tileMatrices.add(tileMatricesItem);
        return this;
    }

    /**
     * Describes scale levels and its tile matrices
     *
     * @return tileMatrices
     */
    @JsonProperty(JSON_PROPERTY_TILE_MATRICES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<TileMatrix> getTileMatrices() {
        return tileMatrices;
    }

    @JsonProperty(JSON_PROPERTY_TILE_MATRICES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTileMatrices(List<TileMatrix> tileMatrices) {
        this.tileMatrices = tileMatrices;
    }

    /**
     * Return true if this tileMatrixSet object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TileMatrixSet other = (TileMatrixSet) o;
        return Objects.equals(this.title, other.title)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.keywords, other.keywords)
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.uri, other.uri)
                && Objects.equals(this.orderedAxes, other.orderedAxes)
                && Objects.equals(this.crs, other.crs)
                && Objects.equals(this.wellKnownScaleSet, other.wellKnownScaleSet)
                && Objects.equals(this.boundingBox, other.boundingBox)
                && Objects.equals(this.tileMatrices, other.tileMatrices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, keywords, id, uri, orderedAxes, crs, wellKnownScaleSet, boundingBox, tileMatrices);
    }

}
