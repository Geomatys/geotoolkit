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

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * Collections
 */
@JsonPropertyOrder({
    Collections.JSON_PROPERTY_LINKS,
    Collections.JSON_PROPERTY_NUMBER_MATCHED,
    Collections.JSON_PROPERTY_NUMBER_RETURNED,
    Collections.JSON_PROPERTY_COLLECTIONS
})
@XmlRootElement(name = "Collections")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Collections")
public final class Collections extends DataTransferObject {

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_NUMBER_MATCHED = "numberMatched";
    @XmlElement(name = "numberMatched")
    @jakarta.annotation.Nullable
    private Integer numberMatched;

    public static final String JSON_PROPERTY_NUMBER_RETURNED = "numberReturned";
    @XmlElement(name = "numberReturned")
    @jakarta.annotation.Nullable
    private Integer numberReturned;

    public static final String JSON_PROPERTY_COLLECTIONS = "collections";
    @XmlElement(name = "collections")
    @jakarta.annotation.Nonnull
    private List<CollectionDescription> collections = new ArrayList<>();

    public Collections() {
    }

    public Collections links(@jakarta.annotation.Nonnull List<Link> links) {
        this.links = links;
        return this;
    }

    public Collections addLinksItem(Link linksItem) {
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

    public Collections numberMatched(@jakarta.annotation.Nullable Integer numberMatched) {
        this.numberMatched = numberMatched;
        return this;
    }

    /**
     * The number of elements in the response that match the selection
     * parameters like &#x60;bbox&#x60;. minimum: 0
     *
     * @return numberMatched
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NUMBER_MATCHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberMatched")
    public Integer getNumberMatched() {
        return numberMatched;
    }

    @JsonProperty(JSON_PROPERTY_NUMBER_MATCHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberMatched")
    public void setNumberMatched(@jakarta.annotation.Nullable Integer numberMatched) {
        this.numberMatched = numberMatched;
    }

    public Collections numberReturned(@jakarta.annotation.Nullable Integer numberReturned) {
        this.numberReturned = numberReturned;
        return this;
    }

    /**
     * The number of elements in the response. A server may omit this
     * information, if the information about the number of elements is not known
     * or difficult to compute. If the value is provided, the value shall be
     * identical to the number of elements in the response. minimum: 0
     *
     * @return numberReturned
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NUMBER_RETURNED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberReturned")
    public Integer getNumberReturned() {
        return numberReturned;
    }

    @JsonProperty(JSON_PROPERTY_NUMBER_RETURNED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberReturned")
    public void setNumberReturned(@jakarta.annotation.Nullable Integer numberReturned) {
        this.numberReturned = numberReturned;
    }

    public Collections collections(@jakarta.annotation.Nonnull List<CollectionDescription> collections) {
        this.collections = collections;
        return this;
    }

    public Collections addCollectionsItem(CollectionDescription collectionsItem) {
        if (this.collections == null) {
            this.collections = new ArrayList<>();
        }
        this.collections.add(collectionsItem);
        return this;
    }

    /**
     * Get collections
     *
     * @return collections
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_COLLECTIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "collections")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<CollectionDescription> getCollections() {
        return collections;
    }

    @JsonProperty(JSON_PROPERTY_COLLECTIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "collections")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setCollections(@jakarta.annotation.Nonnull List<CollectionDescription> collections) {
        this.collections = collections;
    }

    /**
     * Return true if this collections object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Collections collections = (Collections) o;
        return Objects.equals(this.links, collections.links)
                && Objects.equals(this.numberMatched, collections.numberMatched)
                && Objects.equals(this.numberReturned, collections.numberReturned)
                && Objects.equals(this.collections, collections.collections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(links, numberMatched, numberReturned, collections);
    }

}
