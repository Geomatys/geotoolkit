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

/**
 * The spatial extent of the data in the collection.
 */
@JsonPropertyOrder({
    SpatialExtent.JSON_PROPERTY_BBOX,
    SpatialExtent.JSON_PROPERTY_STORAGE_CRS_BBOX,
    SpatialExtent.JSON_PROPERTY_CRS,
    SpatialExtent.JSON_PROPERTY_GRID
})
@XmlRootElement(name = "SpatialExtent")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "SpatialExtent")
public final class SpatialExtent {

    public static final String CRS84 = "http://www.opengis.net/def/crs/OGC/1.3/CRS84";
    public static final String CRS84H = "http://www.opengis.net/def/crs/OGC/1.3/CRS84h";

    public static final String JSON_PROPERTY_BBOX = "bbox";
    @XmlElement(name = "bbox")
    @jakarta.annotation.Nullable
    private List<List<Double>> bbox = new ArrayList<>();

    public static final String JSON_PROPERTY_STORAGE_CRS_BBOX = "storageCrsBbox";
    @XmlElement(name = "storageCrsBbox")
    @jakarta.annotation.Nullable
    private List<List<Double>> storageCrsBbox = new ArrayList<>();

    /**
     * Coordinate reference system of the coordinates of the &#x60;bbox&#x60;
     * property. The default reference system is WGS 84 longitude/latitude. WGS
     * 84 longitude/latitude/ellipsoidal height for coordinates with height. For
     * non-terrestrial coordinate reference system, another CRS may be
     * specified.
     */
    public static final String JSON_PROPERTY_CRS = "crs";
    @XmlElement(name = "crs")
    @jakarta.annotation.Nullable
    private String crs = CRS84;

    public static final String JSON_PROPERTY_GRID = "grid";
    @XmlElement(name = "grid")
    @jakarta.annotation.Nullable
    private List<Grid> grid = new ArrayList<>();

    public SpatialExtent() {
    }

    public SpatialExtent bbox(@jakarta.annotation.Nullable List<List<Double>> bbox) {
        this.bbox = bbox;
        return this;
    }

    public SpatialExtent addBboxItem(List<Double> bboxItem) {
        if (this.bbox == null) {
            this.bbox = new ArrayList<>();
        }
        this.bbox.add(bboxItem);
        return this;
    }

    /**
     * One or more bounding boxes that describe the spatial extent of the
     * dataset. The first bounding box describes the overall spatial extent of
     * the data. All subsequent bounding boxes describe more precise bounding
     * boxes, e.g., to identify clusters of data. Clients only interested in the
     * overall spatial extent will only need to access the first item in each
     * array.
     *
     * @return bbox
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BBOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "bbox")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<List<Double>> getBbox() {
        return bbox;
    }

    @JsonProperty(JSON_PROPERTY_BBOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "bbox")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setBbox(@jakarta.annotation.Nullable List<List<Double>> bbox) {
        this.bbox = bbox;
    }

    public SpatialExtent storageCrsBbox(@jakarta.annotation.Nullable List<List<Double>> storageCrsBbox) {
        this.storageCrsBbox = storageCrsBbox;
        return this;
    }

    public SpatialExtent addStorageCrsBboxItem(List<Double> storageCrsBboxItem) {
        if (this.storageCrsBbox == null) {
            this.storageCrsBbox = new ArrayList<>();
        }
        this.storageCrsBbox.add(storageCrsBboxItem);
        return this;
    }

    /**
     * One or more bounding boxes that describe the spatial extent of the
     * dataset in the storage (native) CRS (&#x60;storageCrs&#x60; property).
     * The first bounding box describes the overall spatial extent of the data.
     * All subsequent bounding boxes describe more precise bounding boxes, e.g.,
     * to identify clusters of data. Clients only interested in the overall
     * spatial extent will only need to access the first item in each array.
     *
     * @return storageCrsBbox
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_STORAGE_CRS_BBOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "storageCrsBbox")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<List<Double>> getStorageCrsBbox() {
        return storageCrsBbox;
    }

    @JsonProperty(JSON_PROPERTY_STORAGE_CRS_BBOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "storageCrsBbox")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setStorageCrsBbox(@jakarta.annotation.Nullable List<List<Double>> storageCrsBbox) {
        this.storageCrsBbox = storageCrsBbox;
    }

    public SpatialExtent crs(@jakarta.annotation.Nullable String crs) {
        this.crs = crs;
        return this;
    }

    /**
     * Coordinate reference system of the coordinates of the &#x60;bbox&#x60;
     * property. The default reference system is WGS 84 longitude/latitude. WGS
     * 84 longitude/latitude/ellipsoidal height for coordinates with height. For
     * non-terrestrial coordinate reference system, another CRS may be
     * specified.
     *
     * @return crs
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    public String getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    public void setCrs(@jakarta.annotation.Nullable String crs) {
        this.crs = crs;
    }

    public SpatialExtent grid(@jakarta.annotation.Nullable List<Grid> grid) {
        this.grid = grid;
        return this;
    }

    public SpatialExtent addGridItem(Grid gridItem) {
        if (this.grid == null) {
            this.grid = new ArrayList<>();
        }
        this.grid.add(gridItem);
        return this;
    }

    /**
     * Provides information about the limited availability of data within the
     * collection organized as a grid (regular or irregular) along each spatial
     * dimension.
     *
     * @return grid
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_GRID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "grid")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Grid> getGrid() {
        return grid;
    }

    @JsonProperty(JSON_PROPERTY_GRID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "grid")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setGrid(@jakarta.annotation.Nullable List<Grid> grid) {
        this.grid = grid;
    }

    /**
     * Return true if this extent_allOf_spatial object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SpatialExtent extentAllOfSpatial = (SpatialExtent) o;
        return Objects.equals(this.bbox, extentAllOfSpatial.bbox)
                && Objects.equals(this.storageCrsBbox, extentAllOfSpatial.storageCrsBbox)
                && Objects.equals(this.crs, extentAllOfSpatial.crs)
                && Objects.equals(this.grid, extentAllOfSpatial.grid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bbox, storageCrsBbox, crs, grid);
    }

}
