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
package org.geotoolkit.ogcapi.dto.movingfeatures;

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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * MF-JSON {@code TemporalPrimitiveGeometry} object: one of MovingPoint, MovingLineString,
 * MovingPolygon or MovingPointCloud.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    TemporalPrimitiveGeometry.JSON_PROPERTY_TYPE,
    TemporalPrimitiveGeometry.JSON_PROPERTY_COORDINATES,
    TemporalPrimitiveGeometry.JSON_PROPERTY_DATETIMES,
    TemporalPrimitiveGeometry.JSON_PROPERTY_INTERPOLATION,
    TemporalPrimitiveGeometry.JSON_PROPERTY_BASE,
    TemporalPrimitiveGeometry.JSON_PROPERTY_ORIENTATIONS,
    TemporalPrimitiveGeometry.JSON_PROPERTY_CRS,
    TemporalPrimitiveGeometry.JSON_PROPERTY_TRS
})
@XmlRootElement(name = "TemporalPrimitiveGeometry")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "TemporalPrimitiveGeometry")
public final class TemporalPrimitiveGeometry extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = JSON_PROPERTY_TYPE)
    @jakarta.annotation.Nonnull
    private String type;

    public static final String JSON_PROPERTY_COORDINATES = "coordinates";
    //ponytail: shape varies with "type" (nested arrays of 2/3/4 numbers depending on
    //point/linestring/polygon/multipoint); modeled as a raw list rather than one class per shape.
    @XmlElement(name = JSON_PROPERTY_COORDINATES)
    @jakarta.annotation.Nonnull
    private List<Object> coordinates;

    public static final String JSON_PROPERTY_DATETIMES = "datetimes";
    @XmlElement(name = JSON_PROPERTY_DATETIMES)
    @jakarta.annotation.Nonnull
    private List<String> datetimes;

    public static final String JSON_PROPERTY_INTERPOLATION = "interpolation";
    @XmlElement(name = JSON_PROPERTY_INTERPOLATION)
    @jakarta.annotation.Nullable
    private String interpolation;

    public static final String JSON_PROPERTY_BASE = "base";
    //ponytail: "base" ({href, type}) is only used by MovingPointCloud; kept as a plain map
    //rather than a dedicated single-purpose class.
    @XmlElement(name = JSON_PROPERTY_BASE)
    @jakarta.annotation.Nullable
    private Map<String, Object> base;

    public static final String JSON_PROPERTY_ORIENTATIONS = "orientations";
    //ponytail: array of {scales:[...], angles:[...]}; kept as a raw list, same reasoning as base.
    @XmlElement(name = JSON_PROPERTY_ORIENTATIONS)
    @jakarta.annotation.Nullable
    private List<Object> orientations;

    public static final String JSON_PROPERTY_CRS = "crs";
    //ponytail: oneOf(namedCRS, linkedCRS); passed through untyped, same as MovingFeature.crs.
    @XmlElement(name = JSON_PROPERTY_CRS)
    @jakarta.annotation.Nullable
    private Object crs;

    public static final String JSON_PROPERTY_TRS = "trs";
    //ponytail: oneOf(linkedCRS, {type:Name,properties}); passed through untyped.
    @XmlElement(name = JSON_PROPERTY_TRS)
    @jakarta.annotation.Nullable
    private Object trs;

    public TemporalPrimitiveGeometry() {
    }

    public TemporalPrimitiveGeometry type(@jakarta.annotation.Nonnull String type) {
        this.type = type;
        return this;
    }

    /**
     * One of MovingPoint, MovingLineString, MovingPolygon or MovingPointCloud.
     * @return type
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nonnull String type) {
        this.type = type;
    }

    public TemporalPrimitiveGeometry coordinates(@jakarta.annotation.Nonnull List<Object> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

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

    public TemporalPrimitiveGeometry datetimes(@jakarta.annotation.Nonnull List<String> datetimes) {
        this.datetimes = datetimes;
        return this;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DATETIMES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "datetimes")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getDatetimes() {
        return datetimes;
    }

    @JsonProperty(JSON_PROPERTY_DATETIMES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "datetimes")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setDatetimes(@jakarta.annotation.Nonnull List<String> datetimes) {
        this.datetimes = datetimes;
    }

    public TemporalPrimitiveGeometry interpolation(@jakarta.annotation.Nullable String interpolation) {
        this.interpolation = interpolation;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_INTERPOLATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "interpolation")
    public String getInterpolation() {
        return interpolation;
    }

    @JsonProperty(JSON_PROPERTY_INTERPOLATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "interpolation")
    public void setInterpolation(@jakarta.annotation.Nullable String interpolation) {
        this.interpolation = interpolation;
    }

    public TemporalPrimitiveGeometry base(@jakarta.annotation.Nullable Map<String, Object> base) {
        this.base = base;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BASE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "base")
    public Map<String, Object> getBase() {
        return base;
    }

    @JsonProperty(JSON_PROPERTY_BASE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "base")
    public void setBase(@jakarta.annotation.Nullable Map<String, Object> base) {
        this.base = base;
    }

    public TemporalPrimitiveGeometry orientations(@jakarta.annotation.Nullable List<Object> orientations) {
        this.orientations = orientations;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ORIENTATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "orientations")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Object> getOrientations() {
        return orientations;
    }

    @JsonProperty(JSON_PROPERTY_ORIENTATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "orientations")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setOrientations(@jakarta.annotation.Nullable List<Object> orientations) {
        this.orientations = orientations;
    }

    public TemporalPrimitiveGeometry crs(@jakarta.annotation.Nullable Object crs) {
        this.crs = crs;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    public Object getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    public void setCrs(@jakarta.annotation.Nullable Object crs) {
        this.crs = crs;
    }

    public TemporalPrimitiveGeometry trs(@jakarta.annotation.Nullable Object trs) {
        this.trs = trs;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "trs")
    public Object getTrs() {
        return trs;
    }

    @JsonProperty(JSON_PROPERTY_TRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "trs")
    public void setTrs(@jakarta.annotation.Nullable Object trs) {
        this.trs = trs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TemporalPrimitiveGeometry other = (TemporalPrimitiveGeometry) o;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.coordinates, other.coordinates)
                && Objects.equals(this.datetimes, other.datetimes)
                && Objects.equals(this.interpolation, other.interpolation)
                && Objects.equals(this.base, other.base)
                && Objects.equals(this.orientations, other.orientations)
                && Objects.equals(this.crs, other.crs)
                && Objects.equals(this.trs, other.trs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, coordinates, datetimes, interpolation, base, orientations, crs, trs);
    }

}
