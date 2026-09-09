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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * LandingPage
 */
@JsonPropertyOrder({
    LandingPage.JSON_PROPERTY_TITLE,
    LandingPage.JSON_PROPERTY_DESCRIPTION,
    LandingPage.JSON_PROPERTY_ATTRIBUTION,
    LandingPage.JSON_PROPERTY_LINKS
})
public final class LandingPage extends DataTransferObject implements CommonResponse {

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_ATTRIBUTION = "attribution";
    private String attribution;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public LandingPage() {
    }

    public LandingPage(List<Link> links) {
        this.links = links;
    }

    public LandingPage title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Get title
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

    public LandingPage description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
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

    public LandingPage attribution(String attribution) {
        this.attribution = attribution;
        return this;
    }

    /**
     * The &#x60;attribution&#x60; should be short and intended for presentation
     * to a user, for example, in a corner of a map. Parts of the text can be
     * links to other resources if additional information is needed. The string
     * can include HTML markup.
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

    public LandingPage links(List<Link> links) {
        this.links = links;
        return this;
    }

    public LandingPage addLinksItem(Link linksItem) {
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

    /**
     * Return true if this landingPage object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LandingPage landingPage = (LandingPage) o;
        return Objects.equals(this.title, landingPage.title)
                && Objects.equals(this.description, landingPage.description)
                && Objects.equals(this.attribution, landingPage.attribution)
                && Objects.equals(this.links, landingPage.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, attribution, links);
    }

}
