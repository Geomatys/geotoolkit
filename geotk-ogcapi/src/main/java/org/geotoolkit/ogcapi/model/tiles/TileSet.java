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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.ogcapi.model.common.Crs;
import org.geotoolkit.ogcapi.model.common.Link;

/**
 * A resource describing a tileset based on the OGC TileSet Metadata Standard. At least one of the
 * &#39;TileMatrixSet&#39;, or a link with &#39;rel&#39; http://www.opengis.net/def/rel/ogc/1.0/tiling-scheme
 */
@JsonPropertyOrder({
    TileSet.JSON_PROPERTY_TITLE,
    TileSet.JSON_PROPERTY_DESCRIPTION,
    TileSet.JSON_PROPERTY_DATA_TYPE,
    TileSet.JSON_PROPERTY_CRS,
    TileSet.JSON_PROPERTY_TILE_MATRIX_SET_U_R_I,
    TileSet.JSON_PROPERTY_LINKS,
    TileSet.JSON_PROPERTY_TILE_MATRIX_SET_LIMITS,
    TileSet.JSON_PROPERTY_EPOCH,
    TileSet.JSON_PROPERTY_LAYERS,
    TileSet.JSON_PROPERTY_BOUNDING_BOX,
    TileSet.JSON_PROPERTY_CENTER_POINT,
    TileSet.JSON_PROPERTY_STYLE,
    TileSet.JSON_PROPERTY_ATTRIBUTION,
    TileSet.JSON_PROPERTY_LICENSE,
    TileSet.JSON_PROPERTY_ACCESS_CONSTRAINTS,
    TileSet.JSON_PROPERTY_KEYWORDS,
    TileSet.JSON_PROPERTY_VERSION,
    TileSet.JSON_PROPERTY_CREATED,
    TileSet.JSON_PROPERTY_UPDATED,
    TileSet.JSON_PROPERTY_POINT_OF_CONTACT,
    TileSet.JSON_PROPERTY_MEDIA_TYPES
})
@XmlRootElement(name = "TileSet")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "TileSet")
public final class TileSet extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

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

    public static final String JSON_PROPERTY_TILE_MATRIX_SET_LIMITS = "tileMatrixSetLimits";
    @XmlElement(name = "tileMatrixSetLimits")
    @jakarta.annotation.Nullable
    private List<TileMatrixLimits> tileMatrixSetLimits = new ArrayList<>();

    public static final String JSON_PROPERTY_EPOCH = "epoch";
    @XmlElement(name = "epoch")
    @jakarta.annotation.Nullable
    private BigDecimal epoch;

    public static final String JSON_PROPERTY_LAYERS = "layers";
    @XmlElement(name = "layers")
    @jakarta.annotation.Nullable
    private List<GeospatialData> layers = new ArrayList<>();

    public static final String JSON_PROPERTY_BOUNDING_BOX = "boundingBox";
    @XmlElement(name = "boundingBox")
    @jakarta.annotation.Nullable
    private BoundingBox2D boundingBox;

    public static final String JSON_PROPERTY_CENTER_POINT = "centerPoint";
    @XmlElement(name = "centerPoint")
    @jakarta.annotation.Nullable
    private TileSetCenterPoint centerPoint;

    public static final String JSON_PROPERTY_STYLE = "style";
    @XmlElement(name = "style")
    @jakarta.annotation.Nullable
    private Style style;

    public static final String JSON_PROPERTY_ATTRIBUTION = "attribution";
    @XmlElement(name = "attribution")
    @jakarta.annotation.Nullable
    private String attribution;

    public static final String JSON_PROPERTY_LICENSE = "license";
    @XmlElement(name = "license")
    @jakarta.annotation.Nullable
    private String license;

    /**
     * Restrictions on the availability of the Tile Set that the user needs to be aware of before using or
     * redistributing the Tile Set
     */
    @XmlType(name = "AccessConstraintsEnum")
    @XmlEnum(String.class)
    public enum AccessConstraintsEnum {
        @XmlEnumValue("unclassified")
        UNCLASSIFIED(String.valueOf("unclassified")),
        @XmlEnumValue("restricted")
        RESTRICTED(String.valueOf("restricted")),
        @XmlEnumValue("confidential")
        CONFIDENTIAL(String.valueOf("confidential")),
        @XmlEnumValue("secret")
        SECRET(String.valueOf("secret")),
        @XmlEnumValue("topSecret")
        TOP_SECRET(String.valueOf("topSecret"));

        private String value;

        AccessConstraintsEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static AccessConstraintsEnum fromValue(String value) {
            for (AccessConstraintsEnum b : AccessConstraintsEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    public static final String JSON_PROPERTY_ACCESS_CONSTRAINTS = "accessConstraints";
    @XmlElement(name = "accessConstraints")
    @jakarta.annotation.Nullable
    private AccessConstraintsEnum accessConstraints = AccessConstraintsEnum.UNCLASSIFIED;

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    @XmlElement(name = "keywords")
    @jakarta.annotation.Nullable
    private List<String> keywords = new ArrayList<>();

    public static final String JSON_PROPERTY_VERSION = "version";
    @XmlElement(name = "version")
    @jakarta.annotation.Nullable
    private String version;

    public static final String JSON_PROPERTY_CREATED = "created";
    @XmlElement(name = "created")
    @jakarta.annotation.Nullable
    private Object created;

    public static final String JSON_PROPERTY_UPDATED = "updated";
    @XmlElement(name = "updated")
    @jakarta.annotation.Nullable
    private Object updated;

    public static final String JSON_PROPERTY_POINT_OF_CONTACT = "pointOfContact";
    @XmlElement(name = "pointOfContact")
    @jakarta.annotation.Nullable
    private String pointOfContact;

    public static final String JSON_PROPERTY_MEDIA_TYPES = "mediaTypes";
    @XmlElement(name = "mediaTypes")
    @jakarta.annotation.Nullable
    private List<String> mediaTypes = new ArrayList<>();

    public TileSet() {
    }

    public TileSet title(@jakarta.annotation.Nullable String title) {
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

    public TileSet description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * Brief narrative description of this tile set
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

    public TileSet dataType(@jakarta.annotation.Nonnull String dataType) {
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

    public TileSet crs(@jakarta.annotation.Nonnull Crs crs) {
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

    public TileSet tileMatrixSetURI(@jakarta.annotation.Nullable URI tileMatrixSetURI) {
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

    public TileSet links(@jakarta.annotation.Nonnull List<Link> links) {
        this.links = links;
        return this;
    }

    public TileSet addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Links to related resources. Possible link &#39;rel&#39; values are:
     * &#39;http://www.opengis.net/def/rel/ogc/1.0/dataset&#39; for a URL pointing to the dataset, &#39;item&#39; for a
     * URL template to get a tile; &#39;alternate&#39; for a URL pointing to another representation of the
     * TileSetMetadata (e.g a TileJSON file); &#39;http://www.opengis.net/def/rel/ogc/1.0/tiling-scheme&#39; for a
     * definition of the TileMatrixSet; &#39;http://www.opengis.net/def/rel/ogc/1.0/geodata&#39; for pointing to a
     * single collection (if the tileset represents a single collection)
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

    public TileSet tileMatrixSetLimits(@jakarta.annotation.Nullable List<TileMatrixLimits> tileMatrixSetLimits) {
        this.tileMatrixSetLimits = tileMatrixSetLimits;
        return this;
    }

    public TileSet addTileMatrixSetLimitsItem(TileMatrixLimits tileMatrixSetLimitsItem) {
        if (this.tileMatrixSetLimits == null) {
            this.tileMatrixSetLimits = new ArrayList<>();
        }
        this.tileMatrixSetLimits.add(tileMatrixSetLimitsItem);
        return this;
    }

    /**
     * Limits for the TileRow and TileCol values for each TileMatrix in the tileMatrixSet. If missing, there are no
     * limits other that the ones imposed by the TileMatrixSet. If present the TileMatrices listed are limited and the
     * rest not available at all
     *
     * @return tileMatrixSetLimits
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TILE_MATRIX_SET_LIMITS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "tileMatrixSetLimits")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<TileMatrixLimits> getTileMatrixSetLimits() {
        return tileMatrixSetLimits;
    }

    @JsonProperty(JSON_PROPERTY_TILE_MATRIX_SET_LIMITS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "tileMatrixSetLimits")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setTileMatrixSetLimits(@jakarta.annotation.Nullable List<TileMatrixLimits> tileMatrixSetLimits) {
        this.tileMatrixSetLimits = tileMatrixSetLimits;
    }

    public TileSet epoch(@jakarta.annotation.Nullable BigDecimal epoch) {
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

    public TileSet layers(@jakarta.annotation.Nullable List<GeospatialData> layers) {
        this.layers = layers;
        return this;
    }

    public TileSet addLayersItem(GeospatialData layersItem) {
        if (this.layers == null) {
            this.layers = new ArrayList<>();
        }
        this.layers.add(layersItem);
        return this;
    }

    /**
     * Get layers
     *
     * @return layers
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LAYERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "layers")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<GeospatialData> getLayers() {
        return layers;
    }

    @JsonProperty(JSON_PROPERTY_LAYERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "layers")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setLayers(@jakarta.annotation.Nullable List<GeospatialData> layers) {
        this.layers = layers;
    }

    public TileSet boundingBox(@jakarta.annotation.Nullable BoundingBox2D boundingBox) {
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

    public TileSet centerPoint(@jakarta.annotation.Nullable TileSetCenterPoint centerPoint) {
        this.centerPoint = centerPoint;
        return this;
    }

    /**
     * Get centerPoint
     *
     * @return centerPoint
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CENTER_POINT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "centerPoint")
    public TileSetCenterPoint getCenterPoint() {
        return centerPoint;
    }

    @JsonProperty(JSON_PROPERTY_CENTER_POINT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "centerPoint")
    public void setCenterPoint(@jakarta.annotation.Nullable TileSetCenterPoint centerPoint) {
        this.centerPoint = centerPoint;
    }

    public TileSet style(@jakarta.annotation.Nullable Style style) {
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

    public TileSet attribution(@jakarta.annotation.Nullable String attribution) {
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

    public TileSet license(@jakarta.annotation.Nullable String license) {
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

    public TileSet accessConstraints(@jakarta.annotation.Nullable AccessConstraintsEnum accessConstraints) {
        this.accessConstraints = accessConstraints;
        return this;
    }

    /**
     * Restrictions on the availability of the Tile Set that the user needs to be aware of before using or
     * redistributing the Tile Set
     *
     * @return accessConstraints
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ACCESS_CONSTRAINTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "accessConstraints")
    public AccessConstraintsEnum getAccessConstraints() {
        return accessConstraints;
    }

    @JsonProperty(JSON_PROPERTY_ACCESS_CONSTRAINTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "accessConstraints")
    public void setAccessConstraints(@jakarta.annotation.Nullable AccessConstraintsEnum accessConstraints) {
        this.accessConstraints = accessConstraints;
    }

    public TileSet keywords(@jakarta.annotation.Nullable List<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public TileSet addKeywordsItem(String keywordsItem) {
        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }
        this.keywords.add(keywordsItem);
        return this;
    }

    /**
     * keywords about this tileset
     *
     * @return keywords
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "keywords")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getKeywords() {
        return keywords;
    }

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "keywords")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setKeywords(@jakarta.annotation.Nullable List<String> keywords) {
        this.keywords = keywords;
    }

    public TileSet version(@jakarta.annotation.Nullable String version) {
        this.version = version;
        return this;
    }

    /**
     * Version of the Tile Set. Changes if the data behind the tiles has been changed
     *
     * @return version
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_VERSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "version")
    public String getVersion() {
        return version;
    }

    @JsonProperty(JSON_PROPERTY_VERSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "version")
    public void setVersion(@jakarta.annotation.Nullable String version) {
        this.version = version;
    }

    public TileSet created(@jakarta.annotation.Nullable Object created) {
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

    public TileSet updated(@jakarta.annotation.Nullable Object updated) {
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

    public TileSet pointOfContact(@jakarta.annotation.Nullable String pointOfContact) {
        this.pointOfContact = pointOfContact;
        return this;
    }

    /**
     * Useful information to contact the authors or custodians for the Tile Set
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

    public TileSet mediaTypes(@jakarta.annotation.Nullable List<String> mediaTypes) {
        this.mediaTypes = mediaTypes;
        return this;
    }

    public TileSet addMediaTypesItem(String mediaTypesItem) {
        if (this.mediaTypes == null) {
            this.mediaTypes = new ArrayList<>();
        }
        this.mediaTypes.add(mediaTypesItem);
        return this;
    }

    /**
     * Media types available for the tiles
     *
     * @return mediaTypes
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MEDIA_TYPES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "mediaTypes")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getMediaTypes() {
        return mediaTypes;
    }

    @JsonProperty(JSON_PROPERTY_MEDIA_TYPES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "mediaTypes")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setMediaTypes(@jakarta.annotation.Nullable List<String> mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    /**
     * Return true if this tileSet object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TileSet other = (TileSet) o;
        return Objects.equals(this.title, other.title)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.dataType, other.dataType)
                && Objects.equals(this.crs, other.crs)
                && Objects.equals(this.tileMatrixSetURI, other.tileMatrixSetURI)
                && Objects.equals(this.links, other.links)
                && Objects.equals(this.tileMatrixSetLimits, other.tileMatrixSetLimits)
                && Objects.equals(this.epoch, other.epoch)
                && Objects.equals(this.layers, other.layers)
                && Objects.equals(this.boundingBox, other.boundingBox)
                && Objects.equals(this.centerPoint, other.centerPoint)
                && Objects.equals(this.style, other.style)
                && Objects.equals(this.attribution, other.attribution)
                && Objects.equals(this.license, other.license)
                && Objects.equals(this.accessConstraints, other.accessConstraints)
                && Objects.equals(this.keywords, other.keywords)
                && Objects.equals(this.version, other.version)
                && Objects.equals(this.created, other.created)
                && Objects.equals(this.updated, other.updated)
                && Objects.equals(this.pointOfContact, other.pointOfContact)
                && Objects.equals(this.mediaTypes, other.mediaTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, dataType, crs, tileMatrixSetURI, links, tileMatrixSetLimits, epoch, layers, boundingBox, centerPoint, style, attribution, license, accessConstraints, keywords, version, created, updated, pointOfContact, mediaTypes);
    }

}
