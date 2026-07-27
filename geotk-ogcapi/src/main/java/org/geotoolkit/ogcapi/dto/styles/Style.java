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
public final class Style extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    /**
     * Note: this draft's own schema declares {@code keywords} as a single string, not an array, unlike
     * the unrelated {@link org.geotoolkit.ogcapi.dto.tiles.Style#getKeywords()} (a {@code List<String>}
     * for the OGC API Tiles spec's own, different style schema). Modeled faithfully as declared.
     */
    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    private String keywords;

    public static final String JSON_PROPERTY_ATTRIBUTION = "attribution";
    private String attribution;

    public static final String JSON_PROPERTY_GEO_DATA_CLASSES = "geoDataClasses";
    private List<URI> geoDataClasses = new ArrayList<>();

    public static final String JSON_PROPERTY_POINT_OF_CONTACT = "pointOfContact";
    private String pointOfContact;

    public static final String JSON_PROPERTY_AI_ASSISTANCE = "aiAssistance";
    private String aiAssistance;

    public static final String JSON_PROPERTY_LICENSE = "license";
    private String license;

    public static final String JSON_PROPERTY_VERSION = "version";
    private String version;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public Style() {
    }

    public Style id(String id) {
        this.id = id;
        return this;
    }

    /**
     * An identifier for this style, unique among the styles of the same base resource.
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

    public Style title(String title) {
        this.title = title;
        return this;
    }

    /**
     * A title for this style.
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

    public Style description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Brief narrative description of this style.
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

    public Style keywords(String keywords) {
        this.keywords = keywords;
        return this;
    }

    /**
     * Keywords about this style.
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

    public Style attribution(String attribution) {
        this.attribution = attribution;
        return this;
    }

    /**
     * Attribution for this style.
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

    public Style geoDataClasses(List<URI> geoDataClasses) {
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

    public Style pointOfContact(String pointOfContact) {
        this.pointOfContact = pointOfContact;
        return this;
    }

    /**
     * Point of contact for this style.
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

    public Style aiAssistance(String aiAssistance) {
        this.aiAssistance = aiAssistance;
        return this;
    }

    /**
     * Acknowledgement of the use of AI assistance to generate the style.
     *
     * @return aiAssistance
     */
    @JsonProperty(JSON_PROPERTY_AI_ASSISTANCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getAiAssistance() {
        return aiAssistance;
    }

    @JsonProperty(JSON_PROPERTY_AI_ASSISTANCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAiAssistance(String aiAssistance) {
        this.aiAssistance = aiAssistance;
    }

    public Style license(String license) {
        this.license = license;
        return this;
    }

    /**
     * License for this style.
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

    public Style version(String version) {
        this.version = version;
        return this;
    }

    /**
     * Version of this style.
     *
     * @return version
     */
    @JsonProperty(JSON_PROPERTY_VERSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getVersion() {
        return version;
    }

    @JsonProperty(JSON_PROPERTY_VERSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setVersion(String version) {
        this.version = version;
    }

    public Style links(List<Link> links) {
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
