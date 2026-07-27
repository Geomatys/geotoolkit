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
package org.geotoolkit.ogcapi.dto.dggs;

import org.geotoolkit.ogcapi.dto.common.Crs;
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
public class Dggrs extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    private List<String> keywords = new ArrayList<>();

    public static final String JSON_PROPERTY_URI = "uri";
    private URI uri;

    public static final String JSON_PROPERTY_CRS = "crs";
    private Crs crs;

    /**Integer or String **/
    public static final String JSON_PROPERTY_DEFAULT_DEPTH = "defaultDepth";
    private Object defaultDepth;

    public static final String JSON_PROPERTY_MAX_REFINEMENT_LEVEL = "maxRefinementLevel";
    private Integer maxRefinementLevel;

    public static final String JSON_PROPERTY_MAX_RELATIVE_DEPTH = "maxRelativeDepth";
    private Integer maxRelativeDepth;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_LINK_TEMPLATES = "linkTemplates";
    private List<DggrsLinkTemplatesInner> linkTemplates = new ArrayList<>();

    public Dggrs() {
    }

    public Dggrs id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Local DGGRS identifier consistent with the &#x60;{dggrsId}&#x60; parameter of &#x60;/dggs/{dggrsId}&#x60;
     * resources.
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

    public Dggrs title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Title of this Discrete Global Grid Rfeference System, intended for displaying to a human
     *
     * @return title
     */
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTitle(String title) {
        this.title = title;
    }

    public Dggrs description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Brief narrative description of this Discrete Global Grid System, normally available for display to a human
     *
     * @return description
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDescription(String description) {
        this.description = description;
    }

    public Dggrs keywords(List<String> keywords) {
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

    public Dggrs uri(URI uri) {
        this.uri = uri;
        return this;
    }

    /**
     * Identifier for this Discrete Global Grid Reference System registered with an authority.
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

    public Dggrs crs(Crs crs) {
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

    public Dggrs defaultDepth(Object defaultDepth) {
        this.defaultDepth = defaultDepth;
        return this;
    }

    /**
     * Get defaultDepth.
     * The defaultDepth property indicates the Implementation’s default depth for when the zone-depth query parameter is omitted.
     *
     * @return defaultDepth
     */
    @JsonProperty(JSON_PROPERTY_DEFAULT_DEPTH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Object getDefaultDepth() {
        return defaultDepth;
    }

    @JsonProperty(JSON_PROPERTY_DEFAULT_DEPTH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDefaultDepth(Object defaultDepth) {
        this.defaultDepth = defaultDepth;
    }

    public Dggrs maxRefinementLevel(Integer maxRefinementLevel) {
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
    @JsonProperty(JSON_PROPERTY_MAX_REFINEMENT_LEVEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getMaxRefinementLevel() {
        return maxRefinementLevel;
    }

    @JsonProperty(JSON_PROPERTY_MAX_REFINEMENT_LEVEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMaxRefinementLevel(Integer maxRefinementLevel) {
        this.maxRefinementLevel = maxRefinementLevel;
    }

    public Dggrs maxRelativeDepth(Integer maxRelativeDepth) {
        this.maxRelativeDepth = maxRelativeDepth;
        return this;
    }

    /**
     * The maximum relative depth at which the full resolution of the data can be retrieved for this DGGRS and origin
     * minimum: 0
     *
     * @return maxRelativeDepth
     */
    @JsonProperty(JSON_PROPERTY_MAX_RELATIVE_DEPTH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getMaxRelativeDepth() {
        return maxRelativeDepth;
    }

    @JsonProperty(JSON_PROPERTY_MAX_RELATIVE_DEPTH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMaxRelativeDepth(Integer maxRelativeDepth) {
        this.maxRelativeDepth = maxRelativeDepth;
    }

    public Dggrs links(List<Link> links) {
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

    public Dggrs linkTemplates(List<DggrsLinkTemplatesInner> linkTemplates) {
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
    @JsonProperty(JSON_PROPERTY_LINK_TEMPLATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<DggrsLinkTemplatesInner> getLinkTemplates() {
        return linkTemplates;
    }

    @JsonProperty(JSON_PROPERTY_LINK_TEMPLATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLinkTemplates(List<DggrsLinkTemplatesInner> linkTemplates) {
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
