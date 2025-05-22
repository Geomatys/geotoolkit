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
 * Regular grid with samples spaced at equal intervals
 */
@JsonPropertyOrder({
    RegularGrid.JSON_PROPERTY_RESOLUTION,
    RegularGrid.JSON_PROPERTY_FIRST_COORDINATE,
    RegularGrid.JSON_PROPERTY_RELATIVE_BOUNDS
})
@XmlRootElement(name = "RegularGrid")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "RegularGrid")
public final class RegularGrid extends DataTransferObject {

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

    public RegularGrid() {
    }

    public RegularGrid resolution(@jakarta.annotation.Nonnull Object resolution) {
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

    public RegularGrid firstCoordinate(@jakarta.annotation.Nonnull Object firstCoordinate) {
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

    public RegularGrid relativeBounds(@jakarta.annotation.Nullable List<Object> relativeBounds) {
        this.relativeBounds = relativeBounds;
        return this;
    }

    public RegularGrid addRelativeBoundsItem(Object relativeBoundsItem) {
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

    /**
     * Return true if this regularGrid object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegularGrid regularGrid = (RegularGrid) o;
        return Objects.equals(this.resolution, regularGrid.resolution)
                && Objects.equals(this.firstCoordinate, regularGrid.firstCoordinate)
                && Objects.equals(this.relativeBounds, regularGrid.relativeBounds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resolution, firstCoordinate, relativeBounds);
    }

}
