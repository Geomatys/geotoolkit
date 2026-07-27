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
public final class TemporalPrimitiveGeometry extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_COORDINATES = "coordinates";
    //ponytail: shape varies with "type" (nested arrays of 2/3/4 numbers depending on
    //point/linestring/polygon/multipoint); modeled as a raw list rather than one class per shape.
    private List<Object> coordinates;

    public static final String JSON_PROPERTY_DATETIMES = "datetimes";
    private List<String> datetimes;

    public static final String JSON_PROPERTY_INTERPOLATION = "interpolation";
    private String interpolation;

    public static final String JSON_PROPERTY_BASE = "base";
    //ponytail: "base" ({href, type}) is only used by MovingPointCloud; kept as a plain map
    //rather than a dedicated single-purpose class.
    private Map<String, Object> base;

    public static final String JSON_PROPERTY_ORIENTATIONS = "orientations";
    //ponytail: array of {scales:[...], angles:[...]}; kept as a raw list, same reasoning as base.
    private List<Object> orientations;

    public static final String JSON_PROPERTY_CRS = "crs";
    //ponytail: oneOf(namedCRS, linkedCRS); passed through untyped, same as MovingFeature.crs.
    private Object crs;

    public static final String JSON_PROPERTY_TRS = "trs";
    //ponytail: oneOf(linkedCRS, {type:Name,properties}); passed through untyped.
    private Object trs;

    public TemporalPrimitiveGeometry() {
    }

    public TemporalPrimitiveGeometry type(String type) {
        this.type = type;
        return this;
    }

    /**
     * One of MovingPoint, MovingLineString, MovingPolygon or MovingPointCloud.
     * @return type
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setType(String type) {
        this.type = type;
    }

    public TemporalPrimitiveGeometry coordinates(List<Object> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Object> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setCoordinates(List<Object> coordinates) {
        this.coordinates = coordinates;
    }

    public TemporalPrimitiveGeometry datetimes(List<String> datetimes) {
        this.datetimes = datetimes;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_DATETIMES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<String> getDatetimes() {
        return datetimes;
    }

    @JsonProperty(JSON_PROPERTY_DATETIMES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDatetimes(List<String> datetimes) {
        this.datetimes = datetimes;
    }

    public TemporalPrimitiveGeometry interpolation(String interpolation) {
        this.interpolation = interpolation;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_INTERPOLATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getInterpolation() {
        return interpolation;
    }

    @JsonProperty(JSON_PROPERTY_INTERPOLATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setInterpolation(String interpolation) {
        this.interpolation = interpolation;
    }

    public TemporalPrimitiveGeometry base(Map<String, Object> base) {
        this.base = base;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_BASE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, Object> getBase() {
        return base;
    }

    @JsonProperty(JSON_PROPERTY_BASE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setBase(Map<String, Object> base) {
        this.base = base;
    }

    public TemporalPrimitiveGeometry orientations(List<Object> orientations) {
        this.orientations = orientations;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_ORIENTATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Object> getOrientations() {
        return orientations;
    }

    @JsonProperty(JSON_PROPERTY_ORIENTATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setOrientations(List<Object> orientations) {
        this.orientations = orientations;
    }

    public TemporalPrimitiveGeometry crs(Object crs) {
        this.crs = crs;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCrs(Object crs) {
        this.crs = crs;
    }

    public TemporalPrimitiveGeometry trs(Object trs) {
        this.trs = trs;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_TRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getTrs() {
        return trs;
    }

    @JsonProperty(JSON_PROPERTY_TRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTrs(Object trs) {
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
