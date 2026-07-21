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
package org.geotoolkit.ogcapi.dto.joins;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * Response of {@code GET /collections/{collectionId}/keys}, describing the key fields of a
 * collection that can be used to join data with it.
 *
 * @see https://docs.ogc.org/DRAFTS/22-026.html#_response_schema_for_the_collection_s_key_fields
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    CollectionKeys.JSON_PROPERTY_KEYS,
    CollectionKeys.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "CollectionKeys")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "CollectionKeys")
public final class CollectionKeys extends DataTransferObject {

    public static final String JSON_PROPERTY_KEYS = "keys";
    @XmlElement(name = "keys")
    @jakarta.annotation.Nonnull
    private List<CollectionKey> keys = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    public CollectionKeys() {
    }

    public CollectionKeys keys(@jakarta.annotation.Nonnull List<CollectionKey> keys) {
        this.keys = keys;
        return this;
    }

    public CollectionKeys addKeysItem(CollectionKey keysItem) {
        if (this.keys == null) {
            this.keys = new ArrayList<>();
        }
        this.keys.add(keysItem);
        return this;
    }

    /**
     * The key fields of the collection. Exactly one entry has {@code isDefault} set to
     * {@code true}.
     * @return keys
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_KEYS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "keys")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<CollectionKey> getKeys() {
        return keys;
    }

    @JsonProperty(JSON_PROPERTY_KEYS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "keys")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setKeys(@jakarta.annotation.Nonnull List<CollectionKey> keys) {
        this.keys = keys;
    }

    public CollectionKeys links(@jakarta.annotation.Nonnull List<Link> links) {
        this.links = links;
        return this;
    }

    public CollectionKeys addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Links supporting hypermedia navigation, including a {@code self} link to this response and
     * an {@code alternate} link for every other media type supported by the service.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CollectionKeys that = (CollectionKeys) o;
        return Objects.equals(this.keys, that.keys)
                && Objects.equals(this.links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keys, links);
    }

}
