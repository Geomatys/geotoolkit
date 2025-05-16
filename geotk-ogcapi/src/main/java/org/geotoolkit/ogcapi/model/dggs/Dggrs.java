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
package org.geotoolkit.ogcapi.model.dggs;

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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.ogcapi.model.common.Link;

/**
 * A description of a Discrete Global Grid Reference System provided by the API.
 */
@JsonPropertyOrder({
    Dggrs.JSON_PROPERTY_ID,
    Dggrs.JSON_PROPERTY_TITLE,
    Dggrs.JSON_PROPERTY_DESCRIPTION,
    Dggrs.JSON_PROPERTY_KEYWORDS,
    Dggrs.JSON_PROPERTY_URI,
    Dggrs.JSON_PROPERTY_CRS,
    Dggrs.JSON_PROPERTY_DEFAULT_DEPTH,
    Dggrs.JSON_PROPERTY_MAX_REFINEMENT_LEVEL,
    Dggrs.JSON_PROPERTY_MAX_RELATIVE_DEPTH,
    Dggrs.JSON_PROPERTY_LINKS,
    Dggrs.JSON_PROPERTY_LINK_TEMPLATES
})
@XmlRootElement(name = "Dggrs")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Dggrs")
public class Dggrs extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nonnull
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nonnull
    private String description;

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    @XmlElement(name = "keywords")
    @jakarta.annotation.Nullable
    private List<String> keywords = new ArrayList<>();

    public static final String JSON_PROPERTY_URI = "uri";
    @XmlElement(name = "uri")
    @jakarta.annotation.Nullable
    private URI uri;

    public static final String JSON_PROPERTY_CRS = "crs";
    @XmlElement(name = "crs")
    @jakarta.annotation.Nullable
    private Crs crs;

    /**Integer or String **/
    public static final String JSON_PROPERTY_DEFAULT_DEPTH = "defaultDepth";
    @XmlElement(name = "defaultDepth")
    @jakarta.annotation.Nonnull
    private Object defaultDepth;

    public static final String JSON_PROPERTY_MAX_REFINEMENT_LEVEL = "maxRefinementLevel";
    @XmlElement(name = "maxRefinementLevel")
    @jakarta.annotation.Nullable
    private Integer maxRefinementLevel;

    public static final String JSON_PROPERTY_MAX_RELATIVE_DEPTH = "maxRelativeDepth";
    @XmlElement(name = "maxRelativeDepth")
    @jakarta.annotation.Nullable
    private Integer maxRelativeDepth;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_LINK_TEMPLATES = "linkTemplates";
    @XmlElement(name = "linkTemplates")
    @jakarta.annotation.Nullable
    private List<DggrsLinkTemplatesInner> linkTemplates = new ArrayList<>();

    public Dggrs() {
    }

    public Dggrs id(@jakarta.annotation.Nonnull String id) {
        this.id = id;
        return this;
    }

    /**
     * Local DGGRS identifier consistent with the &#x60;{dggrsId}&#x60; parameter of &#x60;/dggs/{dggrsId}&#x60;
     * resources.
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

    public Dggrs title(@jakarta.annotation.Nonnull String title) {
        this.title = title;
        return this;
    }

    /**
     * Title of this Discrete Global Grid Rfeference System, intended for displaying to a human
     *
     * @return title
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "title")
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "title")
    public void setTitle(@jakarta.annotation.Nonnull String title) {
        this.title = title;
    }

    public Dggrs description(@jakarta.annotation.Nonnull String description) {
        this.description = description;
        return this;
    }

    /**
     * Brief narrative description of this Discrete Global Grid System, normally available for display to a human
     *
     * @return description
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nonnull String description) {
        this.description = description;
    }

    public Dggrs keywords(@jakarta.annotation.Nullable List<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public Dggrs addKeywordsItem(String keywordsItem) {
        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }
        this.keywords.add(keywordsItem);
        return this;
    }

    /**
     * Unordered list of one or more commonly used or formalized word(s) or phrase(s) used to describe this Discrete
     * Global Grid Reference System
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

    public Dggrs uri(@jakarta.annotation.Nullable URI uri) {
        this.uri = uri;
        return this;
    }

    /**
     * Identifier for this Discrete Global Grid Reference System registered with an authority.
     *
     * @return uri
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "uri")
    public URI getUri() {
        return uri;
    }

    @JsonProperty(JSON_PROPERTY_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "uri")
    public void setUri(@jakarta.annotation.Nullable URI uri) {
        this.uri = uri;
    }

    public Dggrs crs(@jakarta.annotation.Nullable Crs crs) {
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

    public Dggrs defaultDepth(@jakarta.annotation.Nonnull Object defaultDepth) {
        this.defaultDepth = defaultDepth;
        return this;
    }

    /**
     * Get defaultDepth
     *
     * @return defaultDepth
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DEFAULT_DEPTH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "defaultDepth")
    public Object getDefaultDepth() {
        return defaultDepth;
    }

    @JsonProperty(JSON_PROPERTY_DEFAULT_DEPTH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "defaultDepth")
    public void setDefaultDepth(@jakarta.annotation.Nonnull Object defaultDepth) {
        this.defaultDepth = defaultDepth;
    }

    public Dggrs maxRefinementLevel(@jakarta.annotation.Nullable Integer maxRefinementLevel) {
        this.maxRefinementLevel = maxRefinementLevel;
        return this;
    }

    /**
     * The maximum refinement level at which the full resolution of the data can be retrieved for this DGGRS and origin
     * (using a &#x60;zone-depth&#x60; relative depth of 0) and/or used for performing the most accurate zone queries
     * (using that value for &#x60;zone-level&#x60;) minimum: 0
     *
     * @return maxRefinementLevel
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAX_REFINEMENT_LEVEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxRefinementLevel")
    public Integer getMaxRefinementLevel() {
        return maxRefinementLevel;
    }

    @JsonProperty(JSON_PROPERTY_MAX_REFINEMENT_LEVEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxRefinementLevel")
    public void setMaxRefinementLevel(@jakarta.annotation.Nullable Integer maxRefinementLevel) {
        this.maxRefinementLevel = maxRefinementLevel;
    }

    public Dggrs maxRelativeDepth(@jakarta.annotation.Nullable Integer maxRelativeDepth) {
        this.maxRelativeDepth = maxRelativeDepth;
        return this;
    }

    /**
     * The maximum relative depth at which the full resolution of the data can be retrieved for this DGGRS and origin
     * minimum: 0
     *
     * @return maxRelativeDepth
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAX_RELATIVE_DEPTH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxRelativeDepth")
    public Integer getMaxRelativeDepth() {
        return maxRelativeDepth;
    }

    @JsonProperty(JSON_PROPERTY_MAX_RELATIVE_DEPTH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxRelativeDepth")
    public void setMaxRelativeDepth(@jakarta.annotation.Nullable Integer maxRelativeDepth) {
        this.maxRelativeDepth = maxRelativeDepth;
    }

    public Dggrs links(@jakarta.annotation.Nonnull List<Link> links) {
        this.links = links;
        return this;
    }

    public Dggrs addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Links to related resources. A &#x60;self&#x60; link to the Discrete Global Grid Reference System description and
     * an &#x60;[ogc-rel:dggrs-definition]&#x60; link to the DGGRS definition (using the schema defined by
     * https://github.com/opengeospatial/ogcapi-discrete-global-grid-systems/blob/master/core/schemas/dggrs-definition/dggrs-definition-proposed.yaml)
     * are required. An &#x60;[ogc-rel:dggrs-zone-query]&#x60; link to query DGGS zones should also be included if _DGGS
     * Zone Query_ is supported.
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

    public Dggrs linkTemplates(@jakarta.annotation.Nullable List<DggrsLinkTemplatesInner> linkTemplates) {
        this.linkTemplates = linkTemplates;
        return this;
    }

    public Dggrs addLinkTemplatesItem(DggrsLinkTemplatesInner linkTemplatesItem) {
        if (this.linkTemplates == null) {
            this.linkTemplates = new ArrayList<>();
        }
        this.linkTemplates.add(linkTemplatesItem);
        return this;
    }

    /**
     * Templated Links to related resources. A templated &#x60;[ogc-rel:dggrs-zone-data]&#x60; link to retrieve data
     * should be included if _DGGS Zone Data_ is supported.
     *
     * @return linkTemplates
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINK_TEMPLATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "linkTemplates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<DggrsLinkTemplatesInner> getLinkTemplates() {
        return linkTemplates;
    }

    @JsonProperty(JSON_PROPERTY_LINK_TEMPLATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "linkTemplates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setLinkTemplates(@jakarta.annotation.Nullable List<DggrsLinkTemplatesInner> linkTemplates) {
        this.linkTemplates = linkTemplates;
    }

    /**
     * Return true if this dggrs object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dggrs dggrs = (Dggrs) o;
        return Objects.equals(this.id, dggrs.id)
                && Objects.equals(this.title, dggrs.title)
                && Objects.equals(this.description, dggrs.description)
                && Objects.equals(this.keywords, dggrs.keywords)
                && Objects.equals(this.uri, dggrs.uri)
                && Objects.equals(this.crs, dggrs.crs)
                && Objects.equals(this.defaultDepth, dggrs.defaultDepth)
                && Objects.equals(this.maxRefinementLevel, dggrs.maxRefinementLevel)
                && Objects.equals(this.maxRelativeDepth, dggrs.maxRelativeDepth)
                && Objects.equals(this.links, dggrs.links)
                && Objects.equals(this.linkTemplates, dggrs.linkTemplates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, keywords, uri, crs, defaultDepth, maxRefinementLevel, maxRelativeDepth, links, linkTemplates);
    }

}
