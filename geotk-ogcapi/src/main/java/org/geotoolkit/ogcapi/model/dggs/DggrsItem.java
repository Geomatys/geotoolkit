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
 * A minimal Discrete Global Grid Reference System element for use within a list of DGGRS linking to a full description.
 */
@JsonPropertyOrder({
    DggrsItem.JSON_PROPERTY_ID,
    DggrsItem.JSON_PROPERTY_TITLE,
    DggrsItem.JSON_PROPERTY_URI,
    DggrsItem.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "DggrsItem")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggrsItem")
public final class DggrsItem extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nonnull
    private String title;

    public static final String JSON_PROPERTY_URI = "uri";
    @XmlElement(name = "uri")
    @jakarta.annotation.Nullable
    private URI uri;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    public DggrsItem() {
    }

    public DggrsItem id(@jakarta.annotation.Nonnull String id) {
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

    public DggrsItem title(@jakarta.annotation.Nonnull String title) {
        this.title = title;
        return this;
    }

    /**
     * Title of this Discrete Global Grid System, normally used for display to a human
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

    public DggrsItem uri(@jakarta.annotation.Nullable URI uri) {
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

    public DggrsItem links(@jakarta.annotation.Nonnull List<Link> links) {
        this.links = links;
        return this;
    }

    public DggrsItem addLinksItem(Link linksItem) {
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
     * are required.
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
     * Return true if this dggrs-item object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggrsItem dggrsItem = (DggrsItem) o;
        return Objects.equals(this.id, dggrsItem.id)
                && Objects.equals(this.title, dggrsItem.title)
                && Objects.equals(this.uri, dggrsItem.uri)
                && Objects.equals(this.links, dggrsItem.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, uri, links);
    }

}
