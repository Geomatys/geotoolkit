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
package org.geotoolkit.openeo.process.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * @author Quentin BIALOTA (Geomatys)
 */
@JsonPropertyOrder({
        BoundingBox.JSON_PROPERTY_WEST,
        BoundingBox.JSON_PROPERTY_SOUTH,
        BoundingBox.JSON_PROPERTY_EAST,
        BoundingBox.JSON_PROPERTY_NORTH,
        BoundingBox.JSON_PROPERTY_BASE,
        BoundingBox.JSON_PROPERTY_HEIGHT,
        BoundingBox.JSON_PROPERTY_CRS
})
@XmlRootElement(name = "BoundingBox")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "BoundingBox")
public class BoundingBox extends DataTransferObject {

    public static final String JSON_PROPERTY_WEST = "west";
    @XmlElement(name = "west")
    @jakarta.annotation.Nonnull
    private double west;

    public static final String JSON_PROPERTY_SOUTH = "south";
    @XmlElement(name = "south")
    @jakarta.annotation.Nonnull
    private double south;

    public static final String JSON_PROPERTY_EAST = "east";
    @XmlElement(name = "east")
    @jakarta.annotation.Nonnull
    private double east;

    public static final String JSON_PROPERTY_NORTH = "north";
    @XmlElement(name = "north")
    @jakarta.annotation.Nonnull
    private double north;

    public static final String JSON_PROPERTY_BASE = "base";
    @XmlElement(name = "base")
    @jakarta.annotation.Nullable
    private double base;

    public static final String JSON_PROPERTY_HEIGHT = "height";
    @XmlElement(name = "height")
    @jakarta.annotation.Nullable
    private double height;

    public static final String JSON_PROPERTY_CRS = "crs";
    @XmlElement(name = "crs")
    @jakarta.annotation.Nullable
    private String crs;

    /**
     * Get west
     *
     * @return west
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_WEST)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "west")
    public double getWest() {
        return west;
    }

    @JsonProperty(JSON_PROPERTY_WEST)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "west")
    public void setWest(@jakarta.annotation.Nonnull double west) {
        this.west = west;
    }

    /**
     * Get south
     *
     * @return south
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_SOUTH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "south")
    public double getSouth() {
        return south;
    }

    @JsonProperty(JSON_PROPERTY_SOUTH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "south")
    public void setSouth(@jakarta.annotation.Nonnull double south) {
        this.south = south;
    }

    /**
     * Get east
     *
     * @return east
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_EAST)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "east")
    public double getEast() {
        return east;
    }

    @JsonProperty(JSON_PROPERTY_EAST)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "east")
    public void setEast(@jakarta.annotation.Nonnull double east) {
        this.east = east;
    }

    /**
     * Get north
     *
     * @return north
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_NORTH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "north")
    public double getNorth() {
        return north;
    }

    @JsonProperty(JSON_PROPERTY_NORTH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "north")
    public void setNorth(@jakarta.annotation.Nonnull double north) {
        this.north = north;
    }

    /**
     * Get base
     *
     * @return base
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BASE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "base")
    public double getBase() {
        return base;
    }

    @JsonProperty(JSON_PROPERTY_BASE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "base")
    public void setBase(@jakarta.annotation.Nullable double base) {
        this.base = base;
    }

    /**
     * Get height
     *
     * @return height
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_HEIGHT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "height")
    public double getHeight() {
        return height;
    }

    @JsonProperty(JSON_PROPERTY_HEIGHT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "height")
    public void setHeight(@jakarta.annotation.Nullable double height) {
        this.height = height;
    }

    /**
     * Get crs
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
}
