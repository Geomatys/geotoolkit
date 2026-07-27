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
import org.geotoolkit.ogcapi.dto.common.CollectionDescription;
import org.geotoolkit.ogcapi.dto.common.Extent;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * A 3D-Container, also referred to as a GeoVolume: a spatial information resource with a distinct
 * bounding volume, containing at most one 3D model dataset relevant to that volume ({@code content}),
 * represented by references to one or more distributions, and including or referencing other GeoVolumes
 * ({@code children}) whose bounding volumes are fully contained by this container's bounding volume.
 *
 * <p>
 * This resource is not the same as the {@code Collection} resource of OGC API — Common, though it can
 * be considered a specialized type of it; the collection information document schema is extended in
 * ways that echo the 3D Tiles root node resource ({@code tileset.json}).</p>
 *
 * @see <a href="https://docs.ogc.org/DRAFTS/22-029.html#_d4889363_3004_42c7_a216_118ee98b4590">OGC API - 3D GeoVolumes, Annex C.1</a>
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    GeoVolume.JSON_PROPERTY_ID,
    GeoVolume.JSON_PROPERTY_TITLE,
    GeoVolume.JSON_PROPERTY_DESCRIPTION,
    GeoVolume.JSON_PROPERTY_COLLECTION_TYPE,
    GeoVolume.JSON_PROPERTY_ITEM_TYPE,
    GeoVolume.JSON_PROPERTY_EXTENT,
    GeoVolume.JSON_PROPERTY_CONTENT_EXTENT,
    GeoVolume.JSON_PROPERTY_CRS,
    GeoVolume.JSON_PROPERTY_LINKS,
    GeoVolume.JSON_PROPERTY_CHILDREN,
    GeoVolume.JSON_PROPERTY_CONTENT
})
public final class GeoVolume extends CollectionDescription {

    public static final String JSON_PROPERTY_COLLECTION_TYPE = "collectionType";
    private String collectionType = "3d-container";

    /**
     * Note: Annex C.2 explicitly types this member as {@code boundingVolume} (box/region/sphere), while
     * the normative Core schema in Requirement 10.E shows it as a flat array of numbers. Modeled after
     * the more complete Annex C description.
     */
    public static final String JSON_PROPERTY_CONTENT_EXTENT = "contentExtent";
    private BoundingVolume contentExtent;

    public static final String JSON_PROPERTY_CHILDREN = "children";
    private List<GeoVolume> children = new ArrayList<>();

    public static final String JSON_PROPERTY_CONTENT = "content";
    private List<Link> content = new ArrayList<>();

    public GeoVolume() {
    }

    public GeoVolume collectionType(String collectionType) {
        this.collectionType = collectionType;
        return this;
    }

    /**
     * Discriminator property, expected to be {@code "3d-container"}.
     *
     * @return collectionType
     */
    @JsonProperty(JSON_PROPERTY_COLLECTION_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getCollectionType() {
        return collectionType;
    }

    @JsonProperty(JSON_PROPERTY_COLLECTION_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public GeoVolume contentExtent(BoundingVolume contentExtent) {
        this.contentExtent = contentExtent;
        return this;
    }

    /**
     * Optional 3D spatial extent of the {@code content} of this 3D-Container, as box, region or sphere.
     *
     * @return contentExtent
     */
    @JsonProperty(JSON_PROPERTY_CONTENT_EXTENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BoundingVolume getContentExtent() {
        return contentExtent;
    }

    @JsonProperty(JSON_PROPERTY_CONTENT_EXTENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setContentExtent(BoundingVolume contentExtent) {
        this.contentExtent = contentExtent;
    }

    public GeoVolume children(List<GeoVolume> children) {
        this.children = children;
        return this;
    }

    public GeoVolume addChildrenItem(GeoVolume childrenItem) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(childrenItem);
        return this;
    }

    /**
     * Zero or more child 3D-Containers whose bounding volumes are fully contained by this container's
     * bounding volume (may only carry the required properties: {@code id}, a {@code self} link and
     * {@code extent}).
     *
     * @return children
     */
    @JsonProperty(JSON_PROPERTY_CHILDREN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<GeoVolume> getChildren() {
        return children;
    }

    @JsonProperty(JSON_PROPERTY_CHILDREN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setChildren(List<GeoVolume> children) {
        this.children = children;
    }

    public GeoVolume content(List<Link> content) {
        this.content = content;
        return this;
    }

    public GeoVolume addContentItem(Link contentItem) {
        if (this.content == null) {
            this.content = new ArrayList<>();
        }
        this.content.add(contentItem);
        return this;
    }

    /**
     * Zero or more references to distributions of the single dataset that is at most relevant to this
     * 3D-Container. The {@code rel} of each link indicates its relation to the dataset, such as
     * {@code original} (the distribution representing the most original version of the dataset, e.g. a
     * CityGML model) or {@code alternate} (other distributions in different encodings or for different
     * platforms). What the link targets depends on the content type, e.g.: 3D Tiles → {@code
     * tileset.json}; I3S → {@code NodeIndexDocument}; CityGML → collection document and/or {@code
     * CityModel} feature; CDB → root folder; 2D features → collection information document.
     *
     * @return content
     */
    @JsonProperty(JSON_PROPERTY_CONTENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Link> getContent() {
        return content;
    }

    @JsonProperty(JSON_PROPERTY_CONTENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setContent(List<Link> content) {
        this.content = content;
    }

    /**
     * Return true if this geoVolume object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoVolume other = (GeoVolume) o;
        return super.equals(o)
                && Objects.equals(this.collectionType, other.collectionType)
                && Objects.equals(this.contentExtent, other.contentExtent)
                && Objects.equals(this.children, other.children)
                && Objects.equals(this.content, other.content);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(collectionType, contentExtent, children, content);
    }

}
