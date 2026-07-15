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
package org.geotoolkit.ogcapi.dto.styles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * One entry of the {@code styles} array returned by the Styles resource. At least one {@code links}
 * item is expected with relation type {@code stylesheet} (a style encoding supported for the style),
 * and optionally one with relation type {@code preview} (a thumbnail).
 *
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    Style.JSON_PROPERTY_ID,
    Style.JSON_PROPERTY_TITLE,
    Style.JSON_PROPERTY_DESCRIPTION,
    Style.JSON_PROPERTY_KEYWORDS,
    Style.JSON_PROPERTY_ATTRIBUTION,
    Style.JSON_PROPERTY_GEO_DATA_CLASSES,
    Style.JSON_PROPERTY_POINT_OF_CONTACT,
    Style.JSON_PROPERTY_AI_ASSISTANCE,
    Style.JSON_PROPERTY_LICENSE,
    Style.JSON_PROPERTY_VERSION,
    Style.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "StyleEntry")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "StyleEntry")
public final class Style extends DataTransferObject {

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

    /**
     * Note: this draft's own schema declares {@code keywords} as a single string, not an array, unlike
     * the unrelated {@link org.geotoolkit.ogcapi.dto.tiles.Style#getKeywords()} (a {@code List<String>}
     * for the OGC API Tiles spec's own, different style schema). Modeled faithfully as declared.
     */
    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    @XmlElement(name = "keywords")
    @jakarta.annotation.Nullable
    private String keywords;

    public static final String JSON_PROPERTY_ATTRIBUTION = "attribution";
    @XmlElement(name = "attribution")
    @jakarta.annotation.Nullable
    private String attribution;

    public static final String JSON_PROPERTY_GEO_DATA_CLASSES = "geoDataClasses";
    @XmlElement(name = "geoDataClasses")
    @jakarta.annotation.Nullable
    private List<URI> geoDataClasses = new ArrayList<>();

    public static final String JSON_PROPERTY_POINT_OF_CONTACT = "pointOfContact";
    @XmlElement(name = "pointOfContact")
    @jakarta.annotation.Nullable
    private String pointOfContact;

    public static final String JSON_PROPERTY_AI_ASSISTANCE = "aiAssistance";
    @XmlElement(name = "aiAssistance")
    @jakarta.annotation.Nullable
    private String aiAssistance;

    public static final String JSON_PROPERTY_LICENSE = "license";
    @XmlElement(name = "license")
    @jakarta.annotation.Nullable
    private String license;

    public static final String JSON_PROPERTY_VERSION = "version";
    @XmlElement(name = "version")
    @jakarta.annotation.Nullable
    private String version;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    public Style() {
    }

    public Style id(@jakarta.annotation.Nonnull String id) {
        this.id = id;
        return this;
    }

    /**
     * An identifier for this style, unique among the styles of the same base resource.
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

    public Style title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

    /**
     * A title for this style.
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

    public Style description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * Brief narrative description of this style.
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

    public Style keywords(@jakarta.annotation.Nullable String keywords) {
        this.keywords = keywords;
        return this;
    }

    /**
     * Keywords about this style.
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

    public Style attribution(@jakarta.annotation.Nullable String attribution) {
        this.attribution = attribution;
        return this;
    }

    /**
     * Attribution for this style.
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

    public Style geoDataClasses(@jakarta.annotation.Nullable List<URI> geoDataClasses) {
        this.geoDataClasses = geoDataClasses;
        return this;
    }

    public Style addGeoDataClassesItem(URI geoDataClassesItem) {
        if (this.geoDataClasses == null) {
            this.geoDataClasses = new ArrayList<>();
        }
        this.geoDataClasses.add(geoDataClassesItem);
        return this;
    }

    /**
     * URIs to the GeoDataClasses registered for the data for which the style is intended. The
     * GeoDataClass definition specifies the logical schema for each feature type present within that
     * GeoDataClass.
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

    public Style pointOfContact(@jakarta.annotation.Nullable String pointOfContact) {
        this.pointOfContact = pointOfContact;
        return this;
    }

    /**
     * Point of contact for this style.
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

    public Style aiAssistance(@jakarta.annotation.Nullable String aiAssistance) {
        this.aiAssistance = aiAssistance;
        return this;
    }

    /**
     * Acknowledgement of the use of AI assistance to generate the style.
     *
     * @return aiAssistance
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_AI_ASSISTANCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "aiAssistance")
    public String getAiAssistance() {
        return aiAssistance;
    }

    @JsonProperty(JSON_PROPERTY_AI_ASSISTANCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "aiAssistance")
    public void setAiAssistance(@jakarta.annotation.Nullable String aiAssistance) {
        this.aiAssistance = aiAssistance;
    }

    public Style license(@jakarta.annotation.Nullable String license) {
        this.license = license;
        return this;
    }

    /**
     * License for this style.
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

    public Style version(@jakarta.annotation.Nullable String version) {
        this.version = version;
        return this;
    }

    /**
     * Version of this style.
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

    public Style links(@jakarta.annotation.Nonnull List<Link> links) {
        this.links = links;
        return this;
    }

    public Style addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Links to style related resources. At least one link with relation type {@code stylesheet} (its
     * {@code type} stating the media type of that style encoding) is expected; a link with relation
     * type {@code preview} may also be provided if a thumbnail is available.
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
     * Return true if this styleEntry object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Style other = (Style) o;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.title, other.title)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.keywords, other.keywords)
                && Objects.equals(this.attribution, other.attribution)
                && Objects.equals(this.geoDataClasses, other.geoDataClasses)
                && Objects.equals(this.pointOfContact, other.pointOfContact)
                && Objects.equals(this.aiAssistance, other.aiAssistance)
                && Objects.equals(this.license, other.license)
                && Objects.equals(this.version, other.version)
                && Objects.equals(this.links, other.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, keywords, attribution, geoDataClasses, pointOfContact, aiAssistance, license, version, links);
    }

}
