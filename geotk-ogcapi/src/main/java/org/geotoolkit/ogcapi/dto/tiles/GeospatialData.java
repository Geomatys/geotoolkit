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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Crs;
import org.geotoolkit.ogcapi.dto.common.Link;

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
public final class GeospatialData extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    private String keywords;

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_DATA_TYPE = "dataType";
    private String dataType;

    public static final String JSON_PROPERTY_GEOMETRY_DIMENSION = "geometryDimension";
    private Integer geometryDimension;

    public static final String JSON_PROPERTY_FEATURE_TYPE = "featureType";
    private String featureType;

    public static final String JSON_PROPERTY_ATTRIBUTION = "attribution";
    private String attribution;

    public static final String JSON_PROPERTY_LICENSE = "license";
    private String license;

    public static final String JSON_PROPERTY_POINT_OF_CONTACT = "pointOfContact";
    private String pointOfContact;

    public static final String JSON_PROPERTY_PUBLISHER = "publisher";
    private String publisher;

    public static final String JSON_PROPERTY_THEME = "theme";
    private String theme;

    public static final String JSON_PROPERTY_CRS = "crs";
    private Crs crs;

    public static final String JSON_PROPERTY_EPOCH = "epoch";
    private BigDecimal epoch;

    public static final String JSON_PROPERTY_MIN_SCALE_DENOMINATOR = "minScaleDenominator";
    private BigDecimal minScaleDenominator;

    public static final String JSON_PROPERTY_MAX_SCALE_DENOMINATOR = "maxScaleDenominator";
    private BigDecimal maxScaleDenominator;

    public static final String JSON_PROPERTY_MIN_CELL_SIZE = "minCellSize";
    private BigDecimal minCellSize;

    public static final String JSON_PROPERTY_MAX_CELL_SIZE = "maxCellSize";
    private BigDecimal maxCellSize;

    public static final String JSON_PROPERTY_MAX_TILE_MATRIX = "maxTileMatrix";
    private String maxTileMatrix;

    public static final String JSON_PROPERTY_MIN_TILE_MATRIX = "minTileMatrix";
    private String minTileMatrix;

    public static final String JSON_PROPERTY_BOUNDING_BOX = "boundingBox";
    private BoundingBox2D boundingBox;

    public static final String JSON_PROPERTY_CREATED = "created";
    private Object created;

    public static final String JSON_PROPERTY_UPDATED = "updated";
    private Object updated;

    public static final String JSON_PROPERTY_STYLE = "style";
    private Style style;

    public static final String JSON_PROPERTY_GEO_DATA_CLASSES = "geoDataClasses";
    private List<String> geoDataClasses = new ArrayList<>();

    public static final String JSON_PROPERTY_PROPERTIES_SCHEMA = "propertiesSchema";
    private PropertiesSchema propertiesSchema;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public GeospatialData() {
    }

    public GeospatialData title(String title) {
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

    public GeospatialData description(String description) {
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

    public GeospatialData keywords(String keywords) {
        this.keywords = keywords;
        return this;
    }

    /**
     * Unordered list of one or more commonly used or formalized word(s) or phrase(s) used to describe this layer
     *
     * @return keywords
     */
    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getKeywords() {
        return keywords;
    }

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public GeospatialData id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier of the Layer. Implementation of &#39;identifier&#39;
     *
     * @return id
     */
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setId(String id) {
        this.id = id;
    }

    public GeospatialData dataType(String dataType) {
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

    public GeospatialData geometryDimension(Integer geometryDimension) {
        this.geometryDimension = geometryDimension;
        return this;
    }

    /**
     * The geometry dimension of the features shown in this layer (0: points, 1: curves, 2: surfaces, 3: solids),
     * unspecified: mixed or unknown minimum: 0 maximum: 3
     *
     * @return geometryDimension
     */
    @JsonProperty(JSON_PROPERTY_GEOMETRY_DIMENSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getGeometryDimension() {
        return geometryDimension;
    }

    @JsonProperty(JSON_PROPERTY_GEOMETRY_DIMENSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setGeometryDimension(Integer geometryDimension) {
        this.geometryDimension = geometryDimension;
    }

    public GeospatialData featureType(String featureType) {
        this.featureType = featureType;
        return this;
    }

    /**
     * Feature type identifier. Only applicable to layers of datatype &#39;geometries&#39;
     *
     * @return featureType
     */
    @JsonProperty(JSON_PROPERTY_FEATURE_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getFeatureType() {
        return featureType;
    }

    @JsonProperty(JSON_PROPERTY_FEATURE_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public GeospatialData attribution(String attribution) {
        this.attribution = attribution;
        return this;
    }

    /**
     * Short reference to recognize the author or provider
     *
     * @return attribution
     */
    @JsonProperty(JSON_PROPERTY_ATTRIBUTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getAttribution() {
        return attribution;
    }

    @JsonProperty(JSON_PROPERTY_ATTRIBUTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public GeospatialData license(String license) {
        this.license = license;
        return this;
    }

    /**
     * License applicable to the tiles
     *
     * @return license
     */
    @JsonProperty(JSON_PROPERTY_LICENSE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getLicense() {
        return license;
    }

    @JsonProperty(JSON_PROPERTY_LICENSE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLicense(String license) {
        this.license = license;
    }

    public GeospatialData pointOfContact(String pointOfContact) {
        this.pointOfContact = pointOfContact;
        return this;
    }

    /**
     * Useful information to contact the authors or custodians for the layer (e.g. e-mail address, a physical address,
     * phone numbers, etc)
     *
     * @return pointOfContact
     */
    @JsonProperty(JSON_PROPERTY_POINT_OF_CONTACT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getPointOfContact() {
        return pointOfContact;
    }

    @JsonProperty(JSON_PROPERTY_POINT_OF_CONTACT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPointOfContact(String pointOfContact) {
        this.pointOfContact = pointOfContact;
    }

    public GeospatialData publisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    /**
     * Organization or individual responsible for making the layer available
     *
     * @return publisher
     */
    @JsonProperty(JSON_PROPERTY_PUBLISHER)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getPublisher() {
        return publisher;
    }

    @JsonProperty(JSON_PROPERTY_PUBLISHER)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public GeospatialData theme(String theme) {
        this.theme = theme;
        return this;
    }

    /**
     * Category where the layer can be grouped
     *
     * @return theme
     */
    @JsonProperty(JSON_PROPERTY_THEME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getTheme() {
        return theme;
    }

    @JsonProperty(JSON_PROPERTY_THEME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTheme(String theme) {
        this.theme = theme;
    }

    public GeospatialData crs(Crs crs) {
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

    public GeospatialData epoch(BigDecimal epoch) {
        this.epoch = epoch;
        return this;
    }

    /**
     * Epoch of the Coordinate Reference System (CRS)
     *
     * @return epoch
     */
    @JsonProperty(JSON_PROPERTY_EPOCH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BigDecimal getEpoch() {
        return epoch;
    }

    @JsonProperty(JSON_PROPERTY_EPOCH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setEpoch(BigDecimal epoch) {
        this.epoch = epoch;
    }

    public GeospatialData minScaleDenominator(BigDecimal minScaleDenominator) {
        this.minScaleDenominator = minScaleDenominator;
        return this;
    }

    /**
     * Minimum scale denominator for usage of the layer
     *
     * @return minScaleDenominator
     */
    @JsonProperty(JSON_PROPERTY_MIN_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BigDecimal getMinScaleDenominator() {
        return minScaleDenominator;
    }

    @JsonProperty(JSON_PROPERTY_MIN_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMinScaleDenominator(BigDecimal minScaleDenominator) {
        this.minScaleDenominator = minScaleDenominator;
    }

    public GeospatialData maxScaleDenominator(BigDecimal maxScaleDenominator) {
        this.maxScaleDenominator = maxScaleDenominator;
        return this;
    }

    /**
     * Maximum scale denominator for usage of the layer
     *
     * @return maxScaleDenominator
     */
    @JsonProperty(JSON_PROPERTY_MAX_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BigDecimal getMaxScaleDenominator() {
        return maxScaleDenominator;
    }

    @JsonProperty(JSON_PROPERTY_MAX_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMaxScaleDenominator(BigDecimal maxScaleDenominator) {
        this.maxScaleDenominator = maxScaleDenominator;
    }

    public GeospatialData minCellSize(BigDecimal minCellSize) {
        this.minCellSize = minCellSize;
        return this;
    }

    /**
     * Minimum cell size for usage of the layer
     *
     * @return minCellSize
     */
    @JsonProperty(JSON_PROPERTY_MIN_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BigDecimal getMinCellSize() {
        return minCellSize;
    }

    @JsonProperty(JSON_PROPERTY_MIN_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMinCellSize(BigDecimal minCellSize) {
        this.minCellSize = minCellSize;
    }

    public GeospatialData maxCellSize(BigDecimal maxCellSize) {
        this.maxCellSize = maxCellSize;
        return this;
    }

    /**
     * Maximum cell size for usage of the layer
     *
     * @return maxCellSize
     */
    @JsonProperty(JSON_PROPERTY_MAX_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BigDecimal getMaxCellSize() {
        return maxCellSize;
    }

    @JsonProperty(JSON_PROPERTY_MAX_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMaxCellSize(BigDecimal maxCellSize) {
        this.maxCellSize = maxCellSize;
    }

    public GeospatialData maxTileMatrix(String maxTileMatrix) {
        this.maxTileMatrix = maxTileMatrix;
        return this;
    }

    /**
     * TileMatrix identifier associated with the minScaleDenominator
     *
     * @return maxTileMatrix
     */
    @JsonProperty(JSON_PROPERTY_MAX_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getMaxTileMatrix() {
        return maxTileMatrix;
    }

    @JsonProperty(JSON_PROPERTY_MAX_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMaxTileMatrix(String maxTileMatrix) {
        this.maxTileMatrix = maxTileMatrix;
    }

    public GeospatialData minTileMatrix(String minTileMatrix) {
        this.minTileMatrix = minTileMatrix;
        return this;
    }

    /**
     * TileMatrix identifier associated with the maxScaleDenominator
     *
     * @return minTileMatrix
     */
    @JsonProperty(JSON_PROPERTY_MIN_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getMinTileMatrix() {
        return minTileMatrix;
    }

    @JsonProperty(JSON_PROPERTY_MIN_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMinTileMatrix(String minTileMatrix) {
        this.minTileMatrix = minTileMatrix;
    }

    public GeospatialData boundingBox(BoundingBox2D boundingBox) {
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

    public GeospatialData created(Object created) {
        this.created = created;
        return this;
    }

    /**
     * Get created
     *
     * @return created
     */
    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getCreated() {
        return created;
    }

    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCreated(Object created) {
        this.created = created;
    }

    public GeospatialData updated(Object updated) {
        this.updated = updated;
        return this;
    }

    /**
     * Get updated
     *
     * @return updated
     */
    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getUpdated() {
        return updated;
    }

    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUpdated(Object updated) {
        this.updated = updated;
    }

    public GeospatialData style(Style style) {
        this.style = style;
        return this;
    }

    /**
     * Get style
     *
     * @return style
     */
    @JsonProperty(JSON_PROPERTY_STYLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Style getStyle() {
        return style;
    }

    @JsonProperty(JSON_PROPERTY_STYLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setStyle(Style style) {
        this.style = style;
    }

    public GeospatialData geoDataClasses(List<String> geoDataClasses) {
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
    @JsonProperty(JSON_PROPERTY_GEO_DATA_CLASSES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getGeoDataClasses() {
        return geoDataClasses;
    }

    @JsonProperty(JSON_PROPERTY_GEO_DATA_CLASSES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setGeoDataClasses(List<String> geoDataClasses) {
        this.geoDataClasses = geoDataClasses;
    }

    public GeospatialData propertiesSchema(PropertiesSchema propertiesSchema) {
        this.propertiesSchema = propertiesSchema;
        return this;
    }

    /**
     * Get propertiesSchema
     *
     * @return propertiesSchema
     */
    @JsonProperty(JSON_PROPERTY_PROPERTIES_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public PropertiesSchema getPropertiesSchema() {
        return propertiesSchema;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPropertiesSchema(PropertiesSchema propertiesSchema) {
        this.propertiesSchema = propertiesSchema;
    }

    public GeospatialData links(List<Link> links) {
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
