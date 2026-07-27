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
package org.geotoolkit.ogcapi.dto.geovolumes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * The response of the {@code /collections} resource: the list of 3D-Containers (GeoVolumes) currently
 * available for the dataset.
 *
 * @see <a href="https://docs.ogc.org/DRAFTS/22-029.html#collections_clause">OGC API - 3D GeoVolumes, 7.1.4</a>
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    GeoVolumes.JSON_PROPERTY_LINKS,
    GeoVolumes.JSON_PROPERTY_COLLECTIONS
})
public final class GeoVolumes extends DataTransferObject {

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_COLLECTIONS = "collections";
    private List<GeoVolume> collections = new ArrayList<>();

    public GeoVolumes() {
    }

    public GeoVolumes links(List<Link> links) {
        this.links = links;
        return this;
    }

    public GeoVolumes addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Links related to this resource.
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

    public GeoVolumes collections(List<GeoVolume> collections) {
        this.collections = collections;
        return this;
    }

    public GeoVolumes addCollectionsItem(GeoVolume collectionsItem) {
        if (this.collections == null) {
            this.collections = new ArrayList<>();
        }
        this.collections.add(collectionsItem);
        return this;
    }

    /**
     * The 3D-Containers (GeoVolumes) available for the dataset.
     *
     * @return collections
     */
    @JsonProperty(JSON_PROPERTY_COLLECTIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<GeoVolume> getCollections() {
        return collections;
    }

    @JsonProperty(JSON_PROPERTY_COLLECTIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setCollections(List<GeoVolume> collections) {
        this.collections = collections;
    }

    /**
     * Return true if this geoVolumes object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoVolumes other = (GeoVolumes) o;
        return Objects.equals(this.links, other.links)
                && Objects.equals(this.collections, other.collections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(links, collections);
    }

}
