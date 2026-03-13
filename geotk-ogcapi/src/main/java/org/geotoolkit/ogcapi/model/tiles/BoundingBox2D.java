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
package org.geotoolkit.ogcapi.model.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.ogcapi.model.common.Crs;

/**
 * Minimum bounding rectangle surrounding a 2D resource in the CRS indicated elsewhere
 */
@JsonPropertyOrder({
    BoundingBox2D.JSON_PROPERTY_LOWER_LEFT,
    BoundingBox2D.JSON_PROPERTY_UPPER_RIGHT,
    BoundingBox2D.JSON_PROPERTY_CRS,
    BoundingBox2D.JSON_PROPERTY_ORDERED_AXES
})
@XmlRootElement(name = "Model2DBoundingBox")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Model2DBoundingBox")
public final class BoundingBox2D extends DataTransferObject {

    public static final String JSON_PROPERTY_LOWER_LEFT = "lowerLeft";
    @XmlElement(name = "lowerLeft")
    @jakarta.annotation.Nonnull
    private List<Double> lowerLeft = new ArrayList<>();

    public static final String JSON_PROPERTY_UPPER_RIGHT = "upperRight";
    @XmlElement(name = "upperRight")
    @jakarta.annotation.Nonnull
    private List<Double> upperRight = new ArrayList<>();

    public static final String JSON_PROPERTY_CRS = "crs";
    @XmlElement(name = "crs")
    @jakarta.annotation.Nullable
    private Crs crs;

    public static final String JSON_PROPERTY_ORDERED_AXES = "orderedAxes";
    @XmlElement(name = "orderedAxes")
    @jakarta.annotation.Nullable
    private List<String> orderedAxes = new ArrayList<>();

    public BoundingBox2D() {
    }

    public BoundingBox2D lowerLeft(@jakarta.annotation.Nonnull List<Double> lowerLeft) {
        this.lowerLeft = lowerLeft;
        return this;
    }

    public BoundingBox2D addLowerLeftItem(Double lowerLeftItem) {
        if (this.lowerLeft == null) {
            this.lowerLeft = new ArrayList<>();
        }
        this.lowerLeft.add(lowerLeftItem);
        return this;
    }

    /**
     * A 2D Point in the CRS indicated elsewhere
     *
     * @return lowerLeft
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_LOWER_LEFT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "lowerLeft")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Double> getLowerLeft() {
        return lowerLeft;
    }

    @JsonProperty(JSON_PROPERTY_LOWER_LEFT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "lowerLeft")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setLowerLeft(@jakarta.annotation.Nonnull List<Double> lowerLeft) {
        this.lowerLeft = lowerLeft;
    }

    public BoundingBox2D upperRight(@jakarta.annotation.Nonnull List<Double> upperRight) {
        this.upperRight = upperRight;
        return this;
    }

    public BoundingBox2D addUpperRightItem(Double upperRightItem) {
        if (this.upperRight == null) {
            this.upperRight = new ArrayList<>();
        }
        this.upperRight.add(upperRightItem);
        return this;
    }

    /**
     * A 2D Point in the CRS indicated elsewhere
     *
     * @return upperRight
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_UPPER_RIGHT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "upperRight")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Double> getUpperRight() {
        return upperRight;
    }

    @JsonProperty(JSON_PROPERTY_UPPER_RIGHT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "upperRight")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setUpperRight(@jakarta.annotation.Nonnull List<Double> upperRight) {
        this.upperRight = upperRight;
    }

    public BoundingBox2D crs(@jakarta.annotation.Nullable Crs crs) {
        this.crs = crs;
        return this;
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
    public Crs getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    public void setCrs(@jakarta.annotation.Nullable Crs crs) {
        this.crs = crs;
    }

    public BoundingBox2D orderedAxes(@jakarta.annotation.Nullable List<String> orderedAxes) {
        this.orderedAxes = orderedAxes;
        return this;
    }

    public BoundingBox2D addOrderedAxesItem(String orderedAxesItem) {
        if (this.orderedAxes == null) {
            this.orderedAxes = new ArrayList<>();
        }
        this.orderedAxes.add(orderedAxesItem);
        return this;
    }

    /**
     * Get orderedAxes
     *
     * @return orderedAxes
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ORDERED_AXES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "orderedAxes")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getOrderedAxes() {
        return orderedAxes;
    }

    @JsonProperty(JSON_PROPERTY_ORDERED_AXES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "orderedAxes")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setOrderedAxes(@jakarta.annotation.Nullable List<String> orderedAxes) {
        this.orderedAxes = orderedAxes;
    }

    /**
     * Return true if this 2DBoundingBox object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoundingBox2D other = (BoundingBox2D) o;
        return Objects.equals(this.lowerLeft, other.lowerLeft)
                && Objects.equals(this.upperRight, other.upperRight)
                && Objects.equals(this.crs, other.crs)
                && Objects.equals(this.orderedAxes, other.orderedAxes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowerLeft, upperRight, crs, orderedAxes);
    }

}
