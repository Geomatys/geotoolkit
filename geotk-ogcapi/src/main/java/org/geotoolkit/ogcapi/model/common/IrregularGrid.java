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
 * Irregular grid with samples spaced at different intervals
 */
@JsonPropertyOrder({
    IrregularGrid.JSON_PROPERTY_COORDINATES,
    IrregularGrid.JSON_PROPERTY_BOUNDS_COORDINATES
})
@XmlRootElement(name = "IrregularGrid")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "IrregularGrid")
public final class IrregularGrid extends DataTransferObject{

    public static final String JSON_PROPERTY_COORDINATES = "coordinates";
    @XmlElement(name = "coordinates")
    @jakarta.annotation.Nonnull
    private List<Object> coordinates = new ArrayList<>();

    public static final String JSON_PROPERTY_BOUNDS_COORDINATES = "boundsCoordinates";
    @XmlElement(name = "boundsCoordinates")
    @jakarta.annotation.Nullable
    private List<List<Object>> boundsCoordinates = new ArrayList<>();

    public IrregularGrid() {
    }

    public IrregularGrid coordinates(@jakarta.annotation.Nonnull List<Object> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public IrregularGrid addCoordinatesItem(Object coordinatesItem) {
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

    public IrregularGrid boundsCoordinates(@jakarta.annotation.Nullable List<List<Object>> boundsCoordinates) {
        this.boundsCoordinates = boundsCoordinates;
        return this;
    }

    public IrregularGrid addBoundsCoordinatesItem(List<Object> boundsCoordinatesItem) {
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
     * Return true if this irregularGrid object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IrregularGrid irregularGrid = (IrregularGrid) o;
        return Objects.equals(this.coordinates, irregularGrid.coordinates)
                && Objects.equals(this.boundsCoordinates, irregularGrid.boundsCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, boundsCoordinates);
    }

}
