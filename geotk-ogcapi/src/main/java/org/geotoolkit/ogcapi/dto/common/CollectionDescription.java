/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * CollectionDesc
 */
@JsonPropertyOrder({
    CollectionDescription.JSON_PROPERTY_ID,
    CollectionDescription.JSON_PROPERTY_TITLE,
    CollectionDescription.JSON_PROPERTY_NAME,
    CollectionDescription.JSON_PROPERTY_DESCRIPTION,
    CollectionDescription.JSON_PROPERTY_ATTRIBUTION,
    CollectionDescription.JSON_PROPERTY_ACCESS_CONSTRAINTS,
    CollectionDescription.JSON_PROPERTY_PUBLISHER,
    CollectionDescription.JSON_PROPERTY_LICENSE,
    CollectionDescription.JSON_PROPERTY_RIGHTS,
    CollectionDescription.JSON_PROPERTY_FORMATS,
    CollectionDescription.JSON_PROPERTY_KEYWORDS,
    CollectionDescription.JSON_PROPERTY_THEMES,
    CollectionDescription.JSON_PROPERTY_CONTACTS,
    CollectionDescription.JSON_PROPERTY_RESOURCE_LANGUAGES,
    CollectionDescription.JSON_PROPERTY_LINKS,
    CollectionDescription.JSON_PROPERTY_ITEM_TYPE,
    CollectionDescription.JSON_PROPERTY_CRS,
    CollectionDescription.JSON_PROPERTY_STORAGE_CRS,
    CollectionDescription.JSON_PROPERTY_EPOCH,
    CollectionDescription.JSON_PROPERTY_DATA_TYPE,
    CollectionDescription.JSON_PROPERTY_GEO_DATA_CLASSES,
    CollectionDescription.JSON_PROPERTY_GEOMETRY_DIMENSION,
    CollectionDescription.JSON_PROPERTY_MIN_SCALE_DENOMINATOR,
    CollectionDescription.JSON_PROPERTY_MAX_SCALE_DENOMINATOR,
    CollectionDescription.JSON_PROPERTY_MIN_CELL_SIZE,
    CollectionDescription.JSON_PROPERTY_MAX_CELL_SIZE,
    CollectionDescription.JSON_PROPERTY_CREATED,
    CollectionDescription.JSON_PROPERTY_UPDATED,
    CollectionDescription.JSON_PROPERTY_EXTENT
})
public class CollectionDescription extends DataTransferObject implements CommonResponse {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_ATTRIBUTION = "attribution";
    private String attribution;

    public static final String JSON_PROPERTY_ACCESS_CONSTRAINTS = "accessConstraints";
    private AccessConstraintsCode accessConstraints;

    public static final String JSON_PROPERTY_PUBLISHER = "publisher";
    private String publisher;

    public static final String JSON_PROPERTY_LICENSE = "license";
    private String license;

    public static final String JSON_PROPERTY_RIGHTS = "rights";
    private String rights;

    public static final String JSON_PROPERTY_FORMATS = "formats";
    private List<Format> formats = new ArrayList<>();

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    private List<String> keywords = new ArrayList<>();

    public static final String JSON_PROPERTY_THEMES = "themes";
    private List<Theme> themes = new ArrayList<>();

    public static final String JSON_PROPERTY_CONTACTS = "contacts";
    private List<Contact> contacts = new ArrayList<>();

    public static final String JSON_PROPERTY_RESOURCE_LANGUAGES = "resourceLanguages";
    private List<Language> resourceLanguages = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_ITEM_TYPE = "itemType";
    private String itemType = "feature";

    public static final String JSON_PROPERTY_CRS = "crs";
    private List<String> crs = new ArrayList<>(Arrays.asList("http://www.opengis.net/def/crs/OGC/1.3/CRS84"));

    public static final String JSON_PROPERTY_STORAGE_CRS = "storageCrs";
    private String storageCrs = "http://www.opengis.net/def/crs/OGC/1.3/CRS84";


    public static final String JSON_PROPERTY_EPOCH = "epoch";
    private BigDecimal epoch;

    /**
     * e.g. :  map, vector, coverage, point clouds, meshes...
     */
    public static final String JSON_PROPERTY_DATA_TYPE = "dataType";
    private String dataType;

    public static final String JSON_PROPERTY_GEO_DATA_CLASSES = "geoDataClasses";
    private List<URI> geoDataClasses = new ArrayList<>();

    public static final String JSON_PROPERTY_GEOMETRY_DIMENSION = "geometryDimension";
    private Integer geometryDimension;

    public static final String JSON_PROPERTY_MIN_SCALE_DENOMINATOR = "minScaleDenominator";
    private BigDecimal minScaleDenominator;

    public static final String JSON_PROPERTY_MAX_SCALE_DENOMINATOR = "maxScaleDenominator";
    private BigDecimal maxScaleDenominator;

    public static final String JSON_PROPERTY_MIN_CELL_SIZE = "minCellSize";
    private BigDecimal minCellSize;

    public static final String JSON_PROPERTY_MAX_CELL_SIZE = "maxCellSize";
    private BigDecimal maxCellSize;

    public static final String JSON_PROPERTY_CREATED = "created";
    private OffsetDateTime created;

    public static final String JSON_PROPERTY_UPDATED = "updated";
    private OffsetDateTime updated;

    public static final String JSON_PROPERTY_EXTENT = "extent";
    private Extent extent;

    public CollectionDescription() {
    }

    public CollectionDescription id(String id) {
        this.id = id;
        return this;
    }

    public CollectionDescription(String id, List<Link> links, String title, String description, String name, Extent extent, List<String> crs, String storageCrs) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.name = name;
        this.extent = extent;
        this.crs = crs;
        this.storageCrs = storageCrs;
        this.links = links;
    }

    public CollectionDescription(CollectionDescription sourceCollection) {
        this.id = sourceCollection.getId();
        this.title = sourceCollection.getTitle();
        this.description = sourceCollection.getDescription();
        this.name = sourceCollection.getName();
        this.extent = sourceCollection.getExtent();
        this.crs = sourceCollection.getCrs();
        this.storageCrs = sourceCollection.getStorageCrs();
        this.links = sourceCollection.getLinks();
    }

    /**
     * identifier of the collection used, for example, in URIs
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

    public CollectionDescription title(String title) {
        this.title = title;
        return this;
    }

    /**
     * human readable title of the collection
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

    public CollectionDescription description(String description) {
        this.description = description;
        return this;
    }

    /**
     * name of the collection
     *
     * @return name
     */
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setName(String name) {
        this.name = name;
    }

    /**
     * a description of the data in the collection
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

    public CollectionDescription attribution(String attribution) {
        this.attribution = attribution;
        return this;
    }

    /**
     * Get attribution
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

    public CollectionDescription accessConstraints(AccessConstraintsCode accessConstraints) {
        this.accessConstraints = accessConstraints;
        return this;
    }

    /**
     * Restrictions on the availability of the collection that the user needs to
     * be aware of before using or redistributing the data: * unclassified:
     * Available for general disclosure * restricted: Not for general disclosure
     * * confidential: Available for someone who can be entrusted with
     * information * secret: Kept or meant to be kept private, unknown, or
     * hidden from all but a select group of people * topSecret: Of the highest
     * secrecy
     *
     * @return accessConstraints
     */
    @JsonProperty(JSON_PROPERTY_ACCESS_CONSTRAINTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public AccessConstraintsCode getAccessConstraints() {
        return accessConstraints;
    }

    @JsonProperty(JSON_PROPERTY_ACCESS_CONSTRAINTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAccessConstraints(AccessConstraintsCode accessConstraints) {
        this.accessConstraints = accessConstraints;
    }

    public CollectionDescription publisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    /**
     * Organization or individual responsible for making the data available
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

    public CollectionDescription license(String license) {
        this.license = license;
        return this;
    }

    /**
     * The legal provisions under which the data ofthis collection is made
     * available.
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

    public CollectionDescription rights(String rights) {
        this.rights = rights;
        return this;
    }

    /**
     * A statement that concerns all rights not addressed by the license such as
     * a copyright statement.
     *
     * @return rights
     */
    @JsonProperty(JSON_PROPERTY_RIGHTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getRights() {
        return rights;
    }

    @JsonProperty(JSON_PROPERTY_RIGHTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRights(String rights) {
        this.rights = rights;
    }

    public CollectionDescription formats(List<Format> formats) {
        this.formats = formats;
        return this;
    }

    public CollectionDescription addFormatsItem(Format formatsItem) {
        if (this.formats == null) {
            this.formats = new ArrayList<>();
        }
        this.formats.add(formatsItem);
        return this;
    }

    /**
     * A list of formats in which the data of this collection is distributed
     *
     * @return formats
     */
    @JsonProperty(JSON_PROPERTY_FORMATS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Format> getFormats() {
        return formats;
    }

    @JsonProperty(JSON_PROPERTY_FORMATS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }

    public CollectionDescription keywords(List<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public CollectionDescription addKeywordsItem(String keywordsItem) {
        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }
        this.keywords.add(keywordsItem);
        return this;
    }

    /**
     * The topic or topics of the resource. Typically represented using
     * free-form keywords, tags, key phrases, or classification codes.
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

    public CollectionDescription themes(List<Theme> themes) {
        this.themes = themes;
        return this;
    }

    public CollectionDescription addThemesItem(Theme themesItem) {
        if (this.themes == null) {
            this.themes = new ArrayList<>();
        }
        this.themes.add(themesItem);
        return this;
    }

    /**
     * A knowledge organization system used to classify the resource.
     *
     * @return themes
     */
    @JsonProperty(JSON_PROPERTY_THEMES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Theme> getThemes() {
        return themes;
    }

    @JsonProperty(JSON_PROPERTY_THEMES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setThemes(List<Theme> themes) {
        this.themes = themes;
    }

    public CollectionDescription contacts(List<Contact> contacts) {
        this.contacts = contacts;
        return this;
    }

    public CollectionDescription addContactsItem(Contact contactsItem) {
        if (this.contacts == null) {
            this.contacts = new ArrayList<>();
        }
        this.contacts.add(contactsItem);
        return this;
    }

    /**
     * A list of contacts qualified by their role(s) in association to the
     * collection.
     *
     * @return contacts
     */
    @JsonProperty(JSON_PROPERTY_CONTACTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Contact> getContacts() {
        return contacts;
    }

    @JsonProperty(JSON_PROPERTY_CONTACTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public CollectionDescription resourceLanguages(List<Language> resourceLanguages) {
        this.resourceLanguages = resourceLanguages;
        return this;
    }

    public CollectionDescription addResourceLanguagesItem(Language resourceLanguagesItem) {
        if (this.resourceLanguages == null) {
            this.resourceLanguages = new ArrayList<>();
        }
        this.resourceLanguages.add(resourceLanguagesItem);
        return this;
    }

    /**
     * The list of languages in which the data of this collection is available.
     *
     * @return resourceLanguages
     */
    @JsonProperty(JSON_PROPERTY_RESOURCE_LANGUAGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Language> getResourceLanguages() {
        return resourceLanguages;
    }

    @JsonProperty(JSON_PROPERTY_RESOURCE_LANGUAGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setResourceLanguages(List<Language> resourceLanguages) {
        this.resourceLanguages = resourceLanguages;
    }

    public CollectionDescription links(List<Link> links) {
        this.links = links;
        return this;
    }

    public CollectionDescription addLinksItem(Link linksItem) {
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
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public CollectionDescription itemType(String itemType) {
        this.itemType = itemType;
        return this;
    }

    /**
     * indicator about the type of the items in the collection if the collection
     * has an accessible /collections/{collectionId}/items endpoint
     *
     * @return itemType
     */
    @JsonProperty(JSON_PROPERTY_ITEM_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getItemType() {
        return itemType;
    }

    @JsonProperty(JSON_PROPERTY_ITEM_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public CollectionDescription crs(List<String> crs) {
        this.crs = crs;
        return this;
    }

    public CollectionDescription addCrsItem(String crsItem) {
        if (this.crs == null) {
            this.crs = new ArrayList<>(Arrays.asList("http://www.opengis.net/def/crs/OGC/1.3/CRS84"));
        }
        this.crs.add(crsItem);
        return this;
    }

    /**
     * the list of coordinate reference systems supported by the API; the first
     * item is the default coordinate reference system
     *
     * @return crs
     */
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCrs(List<String> crs) {
        this.crs = crs;
    }

    public CollectionDescription storageCrs(String storageCrs) {
        this.storageCrs = storageCrs;
        return this;
    }

    /**
     * the native coordinate reference system (i.e., the most efficient CRS in
     * which to request the data, possibly how the data is stored on the
     * server); this is the default output coordinate reference system for Maps
     * and Coverages
     *
     * @return storageCrs
     */
    @JsonProperty(JSON_PROPERTY_STORAGE_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getStorageCrs() {
        return storageCrs;
    }

    @JsonProperty(JSON_PROPERTY_STORAGE_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setStorageCrs(String storageCrs) {
        this.storageCrs = storageCrs;
    }

    public CollectionDescription epoch(BigDecimal epoch) {
        this.epoch = epoch;
        return this;
    }

    /**
     * Epoch of the native (storage) Coordinate Reference System (CRS)
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

    public CollectionDescription dataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * Get dataType.
     * e.g. :  map, vector, coverage, point clouds, meshes...
     *
     * @return dataType
     */
    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDataType() {
        return dataType;
    }

    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public CollectionDescription geoDataClasses(List<URI> geoDataClasses) {
        this.geoDataClasses = geoDataClasses;
        return this;
    }

    public CollectionDescription addGeoDataClassesItem(URI geoDataClassesItem) {
        if (this.geoDataClasses == null) {
            this.geoDataClasses = new ArrayList<>();
        }
        this.geoDataClasses.add(geoDataClassesItem);
        return this;
    }

    /**
     * URIs identifying a class of data contained in the geospatial data (useful
     * for example to determine compatibility with styles or processes)
     *
     * @return geoDataClasses
     */
    @JsonProperty(JSON_PROPERTY_GEO_DATA_CLASSES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<URI> getGeoDataClasses() {
        return geoDataClasses;
    }

    @JsonProperty(JSON_PROPERTY_GEO_DATA_CLASSES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setGeoDataClasses(List<URI> geoDataClasses) {
        this.geoDataClasses = geoDataClasses;
    }

    public CollectionDescription geometryDimension(Integer geometryDimension) {
        this.geometryDimension = geometryDimension;
        return this;
    }

    /**
     * The geometry dimension of the features shown in this layer (0: points, 1:
     * curves, 2: surfaces, 3: solids), unspecified: mixed or unknown minimum: 0
     * maximum: 3
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

    public CollectionDescription minScaleDenominator(BigDecimal minScaleDenominator) {
        this.minScaleDenominator = minScaleDenominator;
        return this;
    }

    /**
     * Minimum scale denominator for usage of the collection
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

    public CollectionDescription maxScaleDenominator(BigDecimal maxScaleDenominator) {
        this.maxScaleDenominator = maxScaleDenominator;
        return this;
    }

    /**
     * Maximum scale denominator for usage of the collection
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

    public CollectionDescription minCellSize(BigDecimal minCellSize) {
        this.minCellSize = minCellSize;
        return this;
    }

    /**
     * Minimum cell size for usage of the collection
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

    public CollectionDescription maxCellSize(BigDecimal maxCellSize) {
        this.maxCellSize = maxCellSize;
        return this;
    }

    /**
     * Maximum cell size for usage of the collection
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

    public CollectionDescription created(OffsetDateTime created) {
        this.created = created;
        return this;
    }

    /**
     * Timestamp indicating when the collection was first produced
     *
     * @return created
     */
    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public OffsetDateTime getCreated() {
        return created;
    }

    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public CollectionDescription updated(OffsetDateTime updated) {
        this.updated = updated;
        return this;
    }

    /**
     * Timestamp of the last change/revision to the collection
     *
     * @return updated
     */
    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public OffsetDateTime getUpdated() {
        return updated;
    }

    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUpdated(OffsetDateTime updated) {
        this.updated = updated;
    }

    public CollectionDescription extent(Extent extent) {
        this.extent = extent;
        return this;
    }

    /**
     * Get extent
     *
     * @return extent
     */
    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Extent getExtent() {
        return extent;
    }

    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setExtent(Extent extent) {
        this.extent = extent;
    }

    /**
     * Return true if this collectionDesc object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CollectionDescription collectionDesc = (CollectionDescription) o;
        return Objects.equals(this.id, collectionDesc.id)
                && Objects.equals(this.title, collectionDesc.title)
                && Objects.equals(this.description, collectionDesc.description)
                && Objects.equals(this.attribution, collectionDesc.attribution)
                && Objects.equals(this.accessConstraints, collectionDesc.accessConstraints)
                && Objects.equals(this.publisher, collectionDesc.publisher)
                && Objects.equals(this.license, collectionDesc.license)
                && Objects.equals(this.rights, collectionDesc.rights)
                && Objects.equals(this.formats, collectionDesc.formats)
                && Objects.equals(this.keywords, collectionDesc.keywords)
                && Objects.equals(this.themes, collectionDesc.themes)
                && Objects.equals(this.contacts, collectionDesc.contacts)
                && Objects.equals(this.resourceLanguages, collectionDesc.resourceLanguages)
                && Objects.equals(this.links, collectionDesc.links)
                && Objects.equals(this.itemType, collectionDesc.itemType)
                && Objects.equals(this.crs, collectionDesc.crs)
                && Objects.equals(this.storageCrs, collectionDesc.storageCrs)
                && Objects.equals(this.epoch, collectionDesc.epoch)
                && Objects.equals(this.dataType, collectionDesc.dataType)
                && Objects.equals(this.geoDataClasses, collectionDesc.geoDataClasses)
                && Objects.equals(this.geometryDimension, collectionDesc.geometryDimension)
                && Objects.equals(this.minScaleDenominator, collectionDesc.minScaleDenominator)
                && Objects.equals(this.maxScaleDenominator, collectionDesc.maxScaleDenominator)
                && Objects.equals(this.minCellSize, collectionDesc.minCellSize)
                && Objects.equals(this.maxCellSize, collectionDesc.maxCellSize)
                && Objects.equals(this.created, collectionDesc.created)
                && Objects.equals(this.updated, collectionDesc.updated)
                && Objects.equals(this.extent, collectionDesc.extent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, attribution, accessConstraints,
                publisher, license, rights, formats, keywords, themes, contacts,
                resourceLanguages, links, itemType, crs, storageCrs, epoch, dataType,
                geoDataClasses, geometryDimension, minScaleDenominator,
                maxScaleDenominator, minCellSize, maxCellSize, created, updated, extent);
    }

}
