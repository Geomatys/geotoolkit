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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.ogcapi.model.common.Crs;
import org.geotoolkit.ogcapi.model.common.Link;

/**
 * GeospatialData
 */
@JsonPropertyOrder({
    GeospatialData.JSON_PROPERTY_TITLE,
    GeospatialData.JSON_PROPERTY_DESCRIPTION,
    GeospatialData.JSON_PROPERTY_KEYWORDS,
    GeospatialData.JSON_PROPERTY_ID,
    GeospatialData.JSON_PROPERTY_DATA_TYPE,
    GeospatialData.JSON_PROPERTY_GEOMETRY_DIMENSION,
    GeospatialData.JSON_PROPERTY_FEATURE_TYPE,
    GeospatialData.JSON_PROPERTY_ATTRIBUTION,
    GeospatialData.JSON_PROPERTY_LICENSE,
    GeospatialData.JSON_PROPERTY_POINT_OF_CONTACT,
    GeospatialData.JSON_PROPERTY_PUBLISHER,
    GeospatialData.JSON_PROPERTY_THEME,
    GeospatialData.JSON_PROPERTY_CRS,
    GeospatialData.JSON_PROPERTY_EPOCH,
    GeospatialData.JSON_PROPERTY_MIN_SCALE_DENOMINATOR,
    GeospatialData.JSON_PROPERTY_MAX_SCALE_DENOMINATOR,
    GeospatialData.JSON_PROPERTY_MIN_CELL_SIZE,
    GeospatialData.JSON_PROPERTY_MAX_CELL_SIZE,
    GeospatialData.JSON_PROPERTY_MAX_TILE_MATRIX,
    GeospatialData.JSON_PROPERTY_MIN_TILE_MATRIX,
    GeospatialData.JSON_PROPERTY_BOUNDING_BOX,
    GeospatialData.JSON_PROPERTY_CREATED,
    GeospatialData.JSON_PROPERTY_UPDATED,
    GeospatialData.JSON_PROPERTY_STYLE,
    GeospatialData.JSON_PROPERTY_GEO_DATA_CLASSES,
    GeospatialData.JSON_PROPERTY_PROPERTIES_SCHEMA,
    GeospatialData.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "GeospatialData")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "GeospatialData")
public final class GeospatialData extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    @XmlElement(name = "keywords")
    @jakarta.annotation.Nullable
    private String keywords;

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_DATA_TYPE = "dataType";
    @XmlElement(name = "dataType")
    @jakarta.annotation.Nonnull
    private String dataType;

    public static final String JSON_PROPERTY_GEOMETRY_DIMENSION = "geometryDimension";
    @XmlElement(name = "geometryDimension")
    @jakarta.annotation.Nullable
    private Integer geometryDimension;

    public static final String JSON_PROPERTY_FEATURE_TYPE = "featureType";
    @XmlElement(name = "featureType")
    @jakarta.annotation.Nullable
    private String featureType;

    public static final String JSON_PROPERTY_ATTRIBUTION = "attribution";
    @XmlElement(name = "attribution")
    @jakarta.annotation.Nullable
    private String attribution;

    public static final String JSON_PROPERTY_LICENSE = "license";
    @XmlElement(name = "license")
    @jakarta.annotation.Nullable
    private String license;

    public static final String JSON_PROPERTY_POINT_OF_CONTACT = "pointOfContact";
    @XmlElement(name = "pointOfContact")
    @jakarta.annotation.Nullable
    private String pointOfContact;

    public static final String JSON_PROPERTY_PUBLISHER = "publisher";
    @XmlElement(name = "publisher")
    @jakarta.annotation.Nullable
    private String publisher;

    public static final String JSON_PROPERTY_THEME = "theme";
    @XmlElement(name = "theme")
    @jakarta.annotation.Nullable
    private String theme;

    public static final String JSON_PROPERTY_CRS = "crs";
    @XmlElement(name = "crs")
    @jakarta.annotation.Nullable
    private Crs crs;

    public static final String JSON_PROPERTY_EPOCH = "epoch";
    @XmlElement(name = "epoch")
    @jakarta.annotation.Nullable
    private BigDecimal epoch;

    public static final String JSON_PROPERTY_MIN_SCALE_DENOMINATOR = "minScaleDenominator";
    @XmlElement(name = "minScaleDenominator")
    @jakarta.annotation.Nullable
    private BigDecimal minScaleDenominator;

    public static final String JSON_PROPERTY_MAX_SCALE_DENOMINATOR = "maxScaleDenominator";
    @XmlElement(name = "maxScaleDenominator")
    @jakarta.annotation.Nullable
    private BigDecimal maxScaleDenominator;

    public static final String JSON_PROPERTY_MIN_CELL_SIZE = "minCellSize";
    @XmlElement(name = "minCellSize")
    @jakarta.annotation.Nullable
    private BigDecimal minCellSize;

    public static final String JSON_PROPERTY_MAX_CELL_SIZE = "maxCellSize";
    @XmlElement(name = "maxCellSize")
    @jakarta.annotation.Nullable
    private BigDecimal maxCellSize;

    public static final String JSON_PROPERTY_MAX_TILE_MATRIX = "maxTileMatrix";
    @XmlElement(name = "maxTileMatrix")
    @jakarta.annotation.Nullable
    private String maxTileMatrix;

    public static final String JSON_PROPERTY_MIN_TILE_MATRIX = "minTileMatrix";
    @XmlElement(name = "minTileMatrix")
    @jakarta.annotation.Nullable
    private String minTileMatrix;

    public static final String JSON_PROPERTY_BOUNDING_BOX = "boundingBox";
    @XmlElement(name = "boundingBox")
    @jakarta.annotation.Nullable
    private BoundingBox2D boundingBox;

    public static final String JSON_PROPERTY_CREATED = "created";
    @XmlElement(name = "created")
    @jakarta.annotation.Nullable
    private Object created;

    public static final String JSON_PROPERTY_UPDATED = "updated";
    @XmlElement(name = "updated")
    @jakarta.annotation.Nullable
    private Object updated;

    public static final String JSON_PROPERTY_STYLE = "style";
    @XmlElement(name = "style")
    @jakarta.annotation.Nullable
    private Style style;

    public static final String JSON_PROPERTY_GEO_DATA_CLASSES = "geoDataClasses";
    @XmlElement(name = "geoDataClasses")
    @jakarta.annotation.Nullable
    private List<String> geoDataClasses = new ArrayList<>();

    public static final String JSON_PROPERTY_PROPERTIES_SCHEMA = "propertiesSchema";
    @XmlElement(name = "propertiesSchema")
    @jakarta.annotation.Nullable
    private PropertiesSchema propertiesSchema;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public GeospatialData() {
    }

    public GeospatialData title(@jakarta.annotation.Nullable String title) {
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

    public GeospatialData description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * Brief narrative description of this tile matrix set, normally available for display to a human
     *
     * @return description
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nullable String description) {
        this.description = description;
    }

    public GeospatialData keywords(@jakarta.annotation.Nullable String keywords) {
        this.keywords = keywords;
        return this;
    }

    /**
     * Unordered list of one or more commonly used or formalized word(s) or phrase(s) used to describe this layer
     *
     * @return keywords
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "keywords")
    public String getKeywords() {
        return keywords;
    }

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "keywords")
    public void setKeywords(@jakarta.annotation.Nullable String keywords) {
        this.keywords = keywords;
    }

    public GeospatialData id(@jakarta.annotation.Nonnull String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier of the Layer. Implementation of &#39;identifier&#39;
     *
     * @return id
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "id")
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "id")
    public void setId(@jakarta.annotation.Nonnull String id) {
        this.id = id;
    }

    public GeospatialData dataType(@jakarta.annotation.Nonnull String dataType) {
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

    public GeospatialData geometryDimension(@jakarta.annotation.Nullable Integer geometryDimension) {
        this.geometryDimension = geometryDimension;
        return this;
    }

    /**
     * The geometry dimension of the features shown in this layer (0: points, 1: curves, 2: surfaces, 3: solids),
     * unspecified: mixed or unknown minimum: 0 maximum: 3
     *
     * @return geometryDimension
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_GEOMETRY_DIMENSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "geometryDimension")
    public Integer getGeometryDimension() {
        return geometryDimension;
    }

    @JsonProperty(JSON_PROPERTY_GEOMETRY_DIMENSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "geometryDimension")
    public void setGeometryDimension(@jakarta.annotation.Nullable Integer geometryDimension) {
        this.geometryDimension = geometryDimension;
    }

    public GeospatialData featureType(@jakarta.annotation.Nullable String featureType) {
        this.featureType = featureType;
        return this;
    }

    /**
     * Feature type identifier. Only applicable to layers of datatype &#39;geometries&#39;
     *
     * @return featureType
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_FEATURE_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "featureType")
    public String getFeatureType() {
        return featureType;
    }

    @JsonProperty(JSON_PROPERTY_FEATURE_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "featureType")
    public void setFeatureType(@jakarta.annotation.Nullable String featureType) {
        this.featureType = featureType;
    }

    public GeospatialData attribution(@jakarta.annotation.Nullable String attribution) {
        this.attribution = attribution;
        return this;
    }

    /**
     * Short reference to recognize the author or provider
     *
     * @return attribution
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ATTRIBUTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "attribution")
    public String getAttribution() {
        return attribution;
    }

    @JsonProperty(JSON_PROPERTY_ATTRIBUTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "attribution")
    public void setAttribution(@jakarta.annotation.Nullable String attribution) {
        this.attribution = attribution;
    }

    public GeospatialData license(@jakarta.annotation.Nullable String license) {
        this.license = license;
        return this;
    }

    /**
     * License applicable to the tiles
     *
     * @return license
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LICENSE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "license")
    public String getLicense() {
        return license;
    }

    @JsonProperty(JSON_PROPERTY_LICENSE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "license")
    public void setLicense(@jakarta.annotation.Nullable String license) {
        this.license = license;
    }

    public GeospatialData pointOfContact(@jakarta.annotation.Nullable String pointOfContact) {
        this.pointOfContact = pointOfContact;
        return this;
    }

    /**
     * Useful information to contact the authors or custodians for the layer (e.g. e-mail address, a physical address,
     * phone numbers, etc)
     *
     * @return pointOfContact
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_POINT_OF_CONTACT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "pointOfContact")
    public String getPointOfContact() {
        return pointOfContact;
    }

    @JsonProperty(JSON_PROPERTY_POINT_OF_CONTACT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "pointOfContact")
    public void setPointOfContact(@jakarta.annotation.Nullable String pointOfContact) {
        this.pointOfContact = pointOfContact;
    }

    public GeospatialData publisher(@jakarta.annotation.Nullable String publisher) {
        this.publisher = publisher;
        return this;
    }

    /**
     * Organization or individual responsible for making the layer available
     *
     * @return publisher
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PUBLISHER)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "publisher")
    public String getPublisher() {
        return publisher;
    }

    @JsonProperty(JSON_PROPERTY_PUBLISHER)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "publisher")
    public void setPublisher(@jakarta.annotation.Nullable String publisher) {
        this.publisher = publisher;
    }

    public GeospatialData theme(@jakarta.annotation.Nullable String theme) {
        this.theme = theme;
        return this;
    }

    /**
     * Category where the layer can be grouped
     *
     * @return theme
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_THEME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "theme")
    public String getTheme() {
        return theme;
    }

    @JsonProperty(JSON_PROPERTY_THEME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "theme")
    public void setTheme(@jakarta.annotation.Nullable String theme) {
        this.theme = theme;
    }

    public GeospatialData crs(@jakarta.annotation.Nullable Crs crs) {
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

    public GeospatialData epoch(@jakarta.annotation.Nullable BigDecimal epoch) {
        this.epoch = epoch;
        return this;
    }

    /**
     * Epoch of the Coordinate Reference System (CRS)
     *
     * @return epoch
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EPOCH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "epoch")
    public BigDecimal getEpoch() {
        return epoch;
    }

    @JsonProperty(JSON_PROPERTY_EPOCH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "epoch")
    public void setEpoch(@jakarta.annotation.Nullable BigDecimal epoch) {
        this.epoch = epoch;
    }

    public GeospatialData minScaleDenominator(@jakarta.annotation.Nullable BigDecimal minScaleDenominator) {
        this.minScaleDenominator = minScaleDenominator;
        return this;
    }

    /**
     * Minimum scale denominator for usage of the layer
     *
     * @return minScaleDenominator
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MIN_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minScaleDenominator")
    public BigDecimal getMinScaleDenominator() {
        return minScaleDenominator;
    }

    @JsonProperty(JSON_PROPERTY_MIN_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minScaleDenominator")
    public void setMinScaleDenominator(@jakarta.annotation.Nullable BigDecimal minScaleDenominator) {
        this.minScaleDenominator = minScaleDenominator;
    }

    public GeospatialData maxScaleDenominator(@jakarta.annotation.Nullable BigDecimal maxScaleDenominator) {
        this.maxScaleDenominator = maxScaleDenominator;
        return this;
    }

    /**
     * Maximum scale denominator for usage of the layer
     *
     * @return maxScaleDenominator
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAX_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxScaleDenominator")
    public BigDecimal getMaxScaleDenominator() {
        return maxScaleDenominator;
    }

    @JsonProperty(JSON_PROPERTY_MAX_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxScaleDenominator")
    public void setMaxScaleDenominator(@jakarta.annotation.Nullable BigDecimal maxScaleDenominator) {
        this.maxScaleDenominator = maxScaleDenominator;
    }

    public GeospatialData minCellSize(@jakarta.annotation.Nullable BigDecimal minCellSize) {
        this.minCellSize = minCellSize;
        return this;
    }

    /**
     * Minimum cell size for usage of the layer
     *
     * @return minCellSize
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MIN_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minCellSize")
    public BigDecimal getMinCellSize() {
        return minCellSize;
    }

    @JsonProperty(JSON_PROPERTY_MIN_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minCellSize")
    public void setMinCellSize(@jakarta.annotation.Nullable BigDecimal minCellSize) {
        this.minCellSize = minCellSize;
    }

    public GeospatialData maxCellSize(@jakarta.annotation.Nullable BigDecimal maxCellSize) {
        this.maxCellSize = maxCellSize;
        return this;
    }

    /**
     * Maximum cell size for usage of the layer
     *
     * @return maxCellSize
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAX_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxCellSize")
    public BigDecimal getMaxCellSize() {
        return maxCellSize;
    }

    @JsonProperty(JSON_PROPERTY_MAX_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxCellSize")
    public void setMaxCellSize(@jakarta.annotation.Nullable BigDecimal maxCellSize) {
        this.maxCellSize = maxCellSize;
    }

    public GeospatialData maxTileMatrix(@jakarta.annotation.Nullable String maxTileMatrix) {
        this.maxTileMatrix = maxTileMatrix;
        return this;
    }

    /**
     * TileMatrix identifier associated with the minScaleDenominator
     *
     * @return maxTileMatrix
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAX_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxTileMatrix")
    public String getMaxTileMatrix() {
        return maxTileMatrix;
    }

    @JsonProperty(JSON_PROPERTY_MAX_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxTileMatrix")
    public void setMaxTileMatrix(@jakarta.annotation.Nullable String maxTileMatrix) {
        this.maxTileMatrix = maxTileMatrix;
    }

    public GeospatialData minTileMatrix(@jakarta.annotation.Nullable String minTileMatrix) {
        this.minTileMatrix = minTileMatrix;
        return this;
    }

    /**
     * TileMatrix identifier associated with the maxScaleDenominator
     *
     * @return minTileMatrix
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MIN_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minTileMatrix")
    public String getMinTileMatrix() {
        return minTileMatrix;
    }

    @JsonProperty(JSON_PROPERTY_MIN_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minTileMatrix")
    public void setMinTileMatrix(@jakarta.annotation.Nullable String minTileMatrix) {
        this.minTileMatrix = minTileMatrix;
    }

    public GeospatialData boundingBox(@jakarta.annotation.Nullable BoundingBox2D boundingBox) {
        this.boundingBox = boundingBox;
        return this;
    }

    /**
     * Get boundingBox
     *
     * @return boundingBox
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BOUNDING_BOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "boundingBox")
    public BoundingBox2D getBoundingBox() {
        return boundingBox;
    }

    @JsonProperty(JSON_PROPERTY_BOUNDING_BOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "boundingBox")
    public void setBoundingBox(@jakarta.annotation.Nullable BoundingBox2D boundingBox) {
        this.boundingBox = boundingBox;
    }

    public GeospatialData created(@jakarta.annotation.Nullable Object created) {
        this.created = created;
        return this;
    }

    /**
     * Get created
     *
     * @return created
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "created")
    public Object getCreated() {
        return created;
    }

    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "created")
    public void setCreated(@jakarta.annotation.Nullable Object created) {
        this.created = created;
    }

    public GeospatialData updated(@jakarta.annotation.Nullable Object updated) {
        this.updated = updated;
        return this;
    }

    /**
     * Get updated
     *
     * @return updated
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "updated")
    public Object getUpdated() {
        return updated;
    }

    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "updated")
    public void setUpdated(@jakarta.annotation.Nullable Object updated) {
        this.updated = updated;
    }

    public GeospatialData style(@jakarta.annotation.Nullable Style style) {
        this.style = style;
        return this;
    }

    /**
     * Get style
     *
     * @return style
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_STYLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "style")
    public Style getStyle() {
        return style;
    }

    @JsonProperty(JSON_PROPERTY_STYLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "style")
    public void setStyle(@jakarta.annotation.Nullable Style style) {
        this.style = style;
    }

    public GeospatialData geoDataClasses(@jakarta.annotation.Nullable List<String> geoDataClasses) {
        this.geoDataClasses = geoDataClasses;
        return this;
    }

    public GeospatialData addGeoDataClassesItem(String geoDataClassesItem) {
        if (this.geoDataClasses == null) {
            this.geoDataClasses = new ArrayList<>();
        }
        this.geoDataClasses.add(geoDataClassesItem);
        return this;
    }

    /**
     * URI identifying a class of data contained in this layer (useful to determine compatibility with styles or
     * processes)
     *
     * @return geoDataClasses
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_GEO_DATA_CLASSES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "geoDataClasses")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getGeoDataClasses() {
        return geoDataClasses;
    }

    @JsonProperty(JSON_PROPERTY_GEO_DATA_CLASSES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "geoDataClasses")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setGeoDataClasses(@jakarta.annotation.Nullable List<String> geoDataClasses) {
        this.geoDataClasses = geoDataClasses;
    }

    public GeospatialData propertiesSchema(@jakarta.annotation.Nullable PropertiesSchema propertiesSchema) {
        this.propertiesSchema = propertiesSchema;
        return this;
    }

    /**
     * Get propertiesSchema
     *
     * @return propertiesSchema
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PROPERTIES_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "propertiesSchema")
    public PropertiesSchema getPropertiesSchema() {
        return propertiesSchema;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "propertiesSchema")
    public void setPropertiesSchema(@jakarta.annotation.Nullable PropertiesSchema propertiesSchema) {
        this.propertiesSchema = propertiesSchema;
    }

    public GeospatialData links(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
        return this;
    }

    public GeospatialData addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Links related to this layer. Possible link &#39;rel&#39; values are: &#39;geodata&#39; for a URL pointing to the
     * collection of geospatial data.
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

    /**
     * Return true if this geospatialData object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeospatialData other = (GeospatialData) o;
        return Objects.equals(this.title, other.title)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.keywords, other.keywords)
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.dataType, other.dataType)
                && Objects.equals(this.geometryDimension, other.geometryDimension)
                && Objects.equals(this.featureType, other.featureType)
                && Objects.equals(this.attribution, other.attribution)
                && Objects.equals(this.license, other.license)
                && Objects.equals(this.pointOfContact, other.pointOfContact)
                && Objects.equals(this.publisher, other.publisher)
                && Objects.equals(this.theme, other.theme)
                && Objects.equals(this.crs, other.crs)
                && Objects.equals(this.epoch, other.epoch)
                && Objects.equals(this.minScaleDenominator, other.minScaleDenominator)
                && Objects.equals(this.maxScaleDenominator, other.maxScaleDenominator)
                && Objects.equals(this.minCellSize, other.minCellSize)
                && Objects.equals(this.maxCellSize, other.maxCellSize)
                && Objects.equals(this.maxTileMatrix, other.maxTileMatrix)
                && Objects.equals(this.minTileMatrix, other.minTileMatrix)
                && Objects.equals(this.boundingBox, other.boundingBox)
                && Objects.equals(this.created, other.created)
                && Objects.equals(this.updated, other.updated)
                && Objects.equals(this.style, other.style)
                && Objects.equals(this.geoDataClasses, other.geoDataClasses)
                && Objects.equals(this.propertiesSchema, other.propertiesSchema)
                && Objects.equals(this.links, other.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, keywords, id, dataType, geometryDimension, featureType, attribution, license, pointOfContact, publisher, theme, crs, epoch, minScaleDenominator, maxScaleDenominator, minCellSize, maxCellSize, maxTileMatrix, minTileMatrix, boundingBox, created, updated, style, geoDataClasses, propertiesSchema, links);
    }

}
