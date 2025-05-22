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
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * Provides information about the limited availability of data within the
 * collection organized as a grid (regular or irregular) along the dimension.
 */
@JsonPropertyOrder({
    Grid.JSON_PROPERTY_CELLS_COUNT,
    Grid.JSON_PROPERTY_RESOLUTION,
    Grid.JSON_PROPERTY_FIRST_COORDINATE,
    Grid.JSON_PROPERTY_RELATIVE_BOUNDS,
    Grid.JSON_PROPERTY_COORDINATES,
    Grid.JSON_PROPERTY_BOUNDS_COORDINATES
})
@XmlRootElement(name = "Grid")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Grid")
public final class Grid extends DataTransferObject {

    public static final String JSON_PROPERTY_CELLS_COUNT = "cellsCount";
    @XmlElement(name = "cellsCount")
    @jakarta.annotation.Nonnull
    private Integer cellsCount;

    public static final String JSON_PROPERTY_RESOLUTION = "resolution";
    @XmlElement(name = "resolution")
    @jakarta.annotation.Nonnull
    private Object resolution;

    public static final String JSON_PROPERTY_FIRST_COORDINATE = "firstCoordinate";
    @XmlElement(name = "firstCoordinate")
    @jakarta.annotation.Nonnull
    private Object firstCoordinate;

    public static final String JSON_PROPERTY_RELATIVE_BOUNDS = "relativeBounds";
    @XmlElement(name = "relativeBounds")
    @jakarta.annotation.Nullable
    private List<Object> relativeBounds = new ArrayList<>();

    public static final String JSON_PROPERTY_COORDINATES = "coordinates";
    @XmlElement(name = "coordinates")
    @jakarta.annotation.Nonnull
    private List<Object> coordinates = new ArrayList<>();

    public static final String JSON_PROPERTY_BOUNDS_COORDINATES = "boundsCoordinates";
    @XmlElement(name = "boundsCoordinates")
    @jakarta.annotation.Nullable
    private List<List<Object>> boundsCoordinates = new ArrayList<>();

    public Grid() {
    }

    public Grid cellsCount(@jakarta.annotation.Nonnull Integer cellsCount) {
        this.cellsCount = cellsCount;
        return this;
    }

    /**
     * Number of samples available along the dimension for data organized as a
     * regular or irregular grid.
     *
     * @return cellsCount
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_CELLS_COUNT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "cellsCount")
    public Integer getCellsCount() {
        return cellsCount;
    }

    @JsonProperty(JSON_PROPERTY_CELLS_COUNT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "cellsCount")
    public void setCellsCount(@jakarta.annotation.Nonnull Integer cellsCount) {
        this.cellsCount = cellsCount;
    }

    public Grid resolution(@jakarta.annotation.Nonnull Object resolution) {
        this.resolution = resolution;
        return this;
    }

    /**
     * Get resolution
     *
     * @return resolution
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_RESOLUTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "resolution")
    public Object getResolution() {
        return resolution;
    }

    @JsonProperty(JSON_PROPERTY_RESOLUTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "resolution")
    public void setResolution(@jakarta.annotation.Nonnull Object resolution) {
        this.resolution = resolution;
    }

    public Grid firstCoordinate(@jakarta.annotation.Nonnull Object firstCoordinate) {
        this.firstCoordinate = firstCoordinate;
        return this;
    }

    /**
     * Get firstCoordinate
     *
     * @return firstCoordinate
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_FIRST_COORDINATE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "firstCoordinate")
    public Object getFirstCoordinate() {
        return firstCoordinate;
    }

    @JsonProperty(JSON_PROPERTY_FIRST_COORDINATE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "firstCoordinate")
    public void setFirstCoordinate(@jakarta.annotation.Nonnull Object firstCoordinate) {
        this.firstCoordinate = firstCoordinate;
    }

    public Grid relativeBounds(@jakarta.annotation.Nullable List<Object> relativeBounds) {
        this.relativeBounds = relativeBounds;
        return this;
    }

    public Grid addRelativeBoundsItem(Object relativeBoundsItem) {
        if (this.relativeBounds == null) {
            this.relativeBounds = new ArrayList<>();
        }
        this.relativeBounds.add(relativeBoundsItem);
        return this;
    }

    /**
     * Distance in units from coordinate to the lower and upper bounds of each
     * cell for regular grids, describing the geometry of the cells
     *
     * @return relativeBounds
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RELATIVE_BOUNDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "relativeBounds")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Object> getRelativeBounds() {
        return relativeBounds;
    }

    @JsonProperty(JSON_PROPERTY_RELATIVE_BOUNDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "relativeBounds")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setRelativeBounds(@jakarta.annotation.Nullable List<Object> relativeBounds) {
        this.relativeBounds = relativeBounds;
    }

    public Grid coordinates(@jakarta.annotation.Nonnull List<Object> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public Grid addCoordinatesItem(Object coordinatesItem) {
        if (this.coordinates == null) {
            this.coordinates = new ArrayList<>();
        }
        this.coordinates.add(coordinatesItem);
        return this;
    }

    /**
     * List of coordinates along the dimension for which data organized as an
     * irregular grid in the collection is available (e.g., 2, 10, 80, 100).
     *
     * @return coordinates
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "coordinates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Object> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "coordinates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setCoordinates(@jakarta.annotation.Nonnull List<Object> coordinates) {
        this.coordinates = coordinates;
    }

    public Grid boundsCoordinates(@jakarta.annotation.Nullable List<List<Object>> boundsCoordinates) {
        this.boundsCoordinates = boundsCoordinates;
        return this;
    }

    public Grid addBoundsCoordinatesItem(List<Object> boundsCoordinatesItem) {
        if (this.boundsCoordinates == null) {
            this.boundsCoordinates = new ArrayList<>();
        }
        this.boundsCoordinates.add(boundsCoordinatesItem);
        return this;
    }

    /**
     * Coordinates of the lower and upper bounds of each cell in absolute units
     * for irregular grids describing the geometry each cell
     *
     * @return boundsCoordinates
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BOUNDS_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "boundsCoordinates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<List<Object>> getBoundsCoordinates() {
        return boundsCoordinates;
    }

    @JsonProperty(JSON_PROPERTY_BOUNDS_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "boundsCoordinates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setBoundsCoordinates(@jakarta.annotation.Nullable List<List<Object>> boundsCoordinates) {
        this.boundsCoordinates = boundsCoordinates;
    }

    /**
     * Return true if this grid object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Grid grid = (Grid) o;
        return Objects.equals(this.cellsCount, grid.cellsCount)
                && Objects.equals(this.resolution, grid.resolution)
                && Objects.equals(this.firstCoordinate, grid.firstCoordinate)
                && Objects.equals(this.relativeBounds, grid.relativeBounds)
                && Objects.equals(this.coordinates, grid.coordinates)
                && Objects.equals(this.boundsCoordinates, grid.boundsCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellsCount, resolution, firstCoordinate, relativeBounds, coordinates, boundsCoordinates);
    }

}
