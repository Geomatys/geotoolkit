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
package org.geotoolkit.ogcapi.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * CollectionDesc
 */
@JsonPropertyOrder({
    CollectionDescription.JSON_PROPERTY_ID,
    CollectionDescription.JSON_PROPERTY_TITLE,
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
@XmlRootElement(name = "CollectionDesc")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "CollectionDesc")
public final class CollectionDescription extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_ATTRIBUTION = "attribution";
    @XmlElement(name = "attribution")
    @jakarta.annotation.Nullable
    private String attribution;

    public static final String JSON_PROPERTY_ACCESS_CONSTRAINTS = "accessConstraints";
    @XmlElement(name = "accessConstraints")
    @jakarta.annotation.Nullable
    private AccessConstraintsCode accessConstraints;

    public static final String JSON_PROPERTY_PUBLISHER = "publisher";
    @XmlElement(name = "publisher")
    @jakarta.annotation.Nullable
    private String publisher;

    public static final String JSON_PROPERTY_LICENSE = "license";
    @XmlElement(name = "license")
    @jakarta.annotation.Nullable
    private String license;

    public static final String JSON_PROPERTY_RIGHTS = "rights";
    @XmlElement(name = "rights")
    @jakarta.annotation.Nullable
    private String rights;

    public static final String JSON_PROPERTY_FORMATS = "formats";
    @XmlElement(name = "formats")
    @jakarta.annotation.Nullable
    private List<Format> formats = new ArrayList<>();

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    @XmlElement(name = "keywords")
    @jakarta.annotation.Nullable
    private List<String> keywords = new ArrayList<>();

    public static final String JSON_PROPERTY_THEMES = "themes";
    @XmlElement(name = "themes")
    @jakarta.annotation.Nullable
    private List<Theme> themes = new ArrayList<>();

    public static final String JSON_PROPERTY_CONTACTS = "contacts";
    @XmlElement(name = "contacts")
    @jakarta.annotation.Nullable
    private List<Contact> contacts = new ArrayList<>();

    public static final String JSON_PROPERTY_RESOURCE_LANGUAGES = "resourceLanguages";
    @XmlElement(name = "resourceLanguages")
    @jakarta.annotation.Nullable
    private List<Language> resourceLanguages = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_ITEM_TYPE = "itemType";
    @XmlElement(name = "itemType")
    @jakarta.annotation.Nullable
    private String itemType = "feature";

    public static final String JSON_PROPERTY_CRS = "crs";
    @XmlElement(name = "crs")
    @jakarta.annotation.Nullable
    private List<String> crs = new ArrayList<>(Arrays.asList("http://www.opengis.net/def/crs/OGC/1.3/CRS84"));

    public static final String JSON_PROPERTY_STORAGE_CRS = "storageCrs";
    @XmlElement(name = "storageCrs")
    @jakarta.annotation.Nullable
    private String storageCrs = "http://www.opengis.net/def/crs/OGC/1.3/CRS84";

    public static final String JSON_PROPERTY_EPOCH = "epoch";
    @XmlElement(name = "epoch")
    @jakarta.annotation.Nullable
    private BigDecimal epoch;

    /**
     * e.g. :  map, vector, coverage, point clouds, meshes...
     */
    public static final String JSON_PROPERTY_DATA_TYPE = "dataType";
    @XmlElement(name = "dataType")
    @jakarta.annotation.Nullable
    private String dataType;

    public static final String JSON_PROPERTY_GEO_DATA_CLASSES = "geoDataClasses";
    @XmlElement(name = "geoDataClasses")
    @jakarta.annotation.Nullable
    private List<URI> geoDataClasses = new ArrayList<>();

    public static final String JSON_PROPERTY_GEOMETRY_DIMENSION = "geometryDimension";
    @XmlElement(name = "geometryDimension")
    @jakarta.annotation.Nullable
    private Integer geometryDimension;

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

    public static final String JSON_PROPERTY_CREATED = "created";
    @XmlElement(name = "created")
    @jakarta.annotation.Nullable
    private OffsetDateTime created;

    public static final String JSON_PROPERTY_UPDATED = "updated";
    @XmlElement(name = "updated")
    @jakarta.annotation.Nullable
    private OffsetDateTime updated;

    public static final String JSON_PROPERTY_EXTENT = "extent";
    @XmlElement(name = "extent")
    @jakarta.annotation.Nullable
    private Extent extent;

    public CollectionDescription() {
    }

    public CollectionDescription id(@jakarta.annotation.Nonnull String id) {
        this.id = id;
        return this;
    }

    /**
     * identifier of the collection used, for example, in URIs
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

    public CollectionDescription title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

    /**
     * human readable title of the collection
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

    public CollectionDescription description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * a description of the data in the collection
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

    public CollectionDescription attribution(@jakarta.annotation.Nullable String attribution) {
        this.attribution = attribution;
        return this;
    }

    /**
     * Get attribution
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

    public CollectionDescription accessConstraints(@jakarta.annotation.Nullable AccessConstraintsCode accessConstraints) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ACCESS_CONSTRAINTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "accessConstraints")
    public AccessConstraintsCode getAccessConstraints() {
        return accessConstraints;
    }

    @JsonProperty(JSON_PROPERTY_ACCESS_CONSTRAINTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "accessConstraints")
    public void setAccessConstraints(@jakarta.annotation.Nullable AccessConstraintsCode accessConstraints) {
        this.accessConstraints = accessConstraints;
    }

    public CollectionDescription publisher(@jakarta.annotation.Nullable String publisher) {
        this.publisher = publisher;
        return this;
    }

    /**
     * Organization or individual responsible for making the data available
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

    public CollectionDescription license(@jakarta.annotation.Nullable String license) {
        this.license = license;
        return this;
    }

    /**
     * The legal provisions under which the data ofthis collection is made
     * available.
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

    public CollectionDescription rights(@jakarta.annotation.Nullable String rights) {
        this.rights = rights;
        return this;
    }

    /**
     * A statement that concerns all rights not addressed by the license such as
     * a copyright statement.
     *
     * @return rights
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RIGHTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "rights")
    public String getRights() {
        return rights;
    }

    @JsonProperty(JSON_PROPERTY_RIGHTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "rights")
    public void setRights(@jakarta.annotation.Nullable String rights) {
        this.rights = rights;
    }

    public CollectionDescription formats(@jakarta.annotation.Nullable List<Format> formats) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_FORMATS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "formats")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Format> getFormats() {
        return formats;
    }

    @JsonProperty(JSON_PROPERTY_FORMATS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "formats")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setFormats(@jakarta.annotation.Nullable List<Format> formats) {
        this.formats = formats;
    }

    public CollectionDescription keywords(@jakarta.annotation.Nullable List<String> keywords) {
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

    public CollectionDescription themes(@jakarta.annotation.Nullable List<Theme> themes) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_THEMES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "themes")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Theme> getThemes() {
        return themes;
    }

    @JsonProperty(JSON_PROPERTY_THEMES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "themes")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setThemes(@jakarta.annotation.Nullable List<Theme> themes) {
        this.themes = themes;
    }

    public CollectionDescription contacts(@jakarta.annotation.Nullable List<Contact> contacts) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONTACTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "contacts")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Contact> getContacts() {
        return contacts;
    }

    @JsonProperty(JSON_PROPERTY_CONTACTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "contacts")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setContacts(@jakarta.annotation.Nullable List<Contact> contacts) {
        this.contacts = contacts;
    }

    public CollectionDescription resourceLanguages(@jakarta.annotation.Nullable List<Language> resourceLanguages) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RESOURCE_LANGUAGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "resourceLanguages")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Language> getResourceLanguages() {
        return resourceLanguages;
    }

    @JsonProperty(JSON_PROPERTY_RESOURCE_LANGUAGES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "resourceLanguages")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setResourceLanguages(@jakarta.annotation.Nullable List<Language> resourceLanguages) {
        this.resourceLanguages = resourceLanguages;
    }

    public CollectionDescription links(@jakarta.annotation.Nonnull List<Link> links) {
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

    public CollectionDescription itemType(@jakarta.annotation.Nullable String itemType) {
        this.itemType = itemType;
        return this;
    }

    /**
     * indicator about the type of the items in the collection if the collection
     * has an accessible /collections/{collectionId}/items endpoint
     *
     * @return itemType
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ITEM_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "itemType")
    public String getItemType() {
        return itemType;
    }

    @JsonProperty(JSON_PROPERTY_ITEM_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "itemType")
    public void setItemType(@jakarta.annotation.Nullable String itemType) {
        this.itemType = itemType;
    }

    public CollectionDescription crs(@jakarta.annotation.Nullable List<String> crs) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setCrs(@jakarta.annotation.Nullable List<String> crs) {
        this.crs = crs;
    }

    public CollectionDescription storageCrs(@jakarta.annotation.Nullable String storageCrs) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_STORAGE_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "storageCrs")
    public String getStorageCrs() {
        return storageCrs;
    }

    @JsonProperty(JSON_PROPERTY_STORAGE_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "storageCrs")
    public void setStorageCrs(@jakarta.annotation.Nullable String storageCrs) {
        this.storageCrs = storageCrs;
    }

    public CollectionDescription epoch(@jakarta.annotation.Nullable BigDecimal epoch) {
        this.epoch = epoch;
        return this;
    }

    /**
     * Epoch of the native (storage) Coordinate Reference System (CRS)
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

    public CollectionDescription dataType(@jakarta.annotation.Nullable String dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * Get dataType.
     * e.g. :  map, vector, coverage, point clouds, meshes...
     *
     * @return dataType
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "dataType")
    public String getDataType() {
        return dataType;
    }

    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "dataType")
    public void setDataType(@jakarta.annotation.Nullable String dataType) {
        this.dataType = dataType;
    }

    public CollectionDescription geoDataClasses(@jakarta.annotation.Nullable List<URI> geoDataClasses) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_GEO_DATA_CLASSES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "geoDataClasses")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<URI> getGeoDataClasses() {
        return geoDataClasses;
    }

    @JsonProperty(JSON_PROPERTY_GEO_DATA_CLASSES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "geoDataClasses")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setGeoDataClasses(@jakarta.annotation.Nullable List<URI> geoDataClasses) {
        this.geoDataClasses = geoDataClasses;
    }

    public CollectionDescription geometryDimension(@jakarta.annotation.Nullable Integer geometryDimension) {
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

    public CollectionDescription minScaleDenominator(@jakarta.annotation.Nullable BigDecimal minScaleDenominator) {
        this.minScaleDenominator = minScaleDenominator;
        return this;
    }

    /**
     * Minimum scale denominator for usage of the collection
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

    public CollectionDescription maxScaleDenominator(@jakarta.annotation.Nullable BigDecimal maxScaleDenominator) {
        this.maxScaleDenominator = maxScaleDenominator;
        return this;
    }

    /**
     * Maximum scale denominator for usage of the collection
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

    public CollectionDescription minCellSize(@jakarta.annotation.Nullable BigDecimal minCellSize) {
        this.minCellSize = minCellSize;
        return this;
    }

    /**
     * Minimum cell size for usage of the collection
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

    public CollectionDescription maxCellSize(@jakarta.annotation.Nullable BigDecimal maxCellSize) {
        this.maxCellSize = maxCellSize;
        return this;
    }

    /**
     * Maximum cell size for usage of the collection
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

    public CollectionDescription created(@jakarta.annotation.Nullable OffsetDateTime created) {
        this.created = created;
        return this;
    }

    /**
     * Timestamp indicating when the collection was first produced
     *
     * @return created
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "created")
    public OffsetDateTime getCreated() {
        return created;
    }

    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "created")
    public void setCreated(@jakarta.annotation.Nullable OffsetDateTime created) {
        this.created = created;
    }

    public CollectionDescription updated(@jakarta.annotation.Nullable OffsetDateTime updated) {
        this.updated = updated;
        return this;
    }

    /**
     * Timestamp of the last change/revision to the collection
     *
     * @return updated
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "updated")
    public OffsetDateTime getUpdated() {
        return updated;
    }

    @JsonProperty(JSON_PROPERTY_UPDATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "updated")
    public void setUpdated(@jakarta.annotation.Nullable OffsetDateTime updated) {
        this.updated = updated;
    }

    public CollectionDescription extent(@jakarta.annotation.Nullable Extent extent) {
        this.extent = extent;
        return this;
    }

    /**
     * Get extent
     *
     * @return extent
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "extent")
    public Extent getExtent() {
        return extent;
    }

    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "extent")
    public void setExtent(@jakarta.annotation.Nullable Extent extent) {
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
