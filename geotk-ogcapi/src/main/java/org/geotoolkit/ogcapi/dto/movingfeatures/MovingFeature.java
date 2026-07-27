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
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeature;

/**
 * MF-JSON {@code movingFeature} object.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    GeoJSONFeature.PROPERTY_TYPE,
    GeoJSONFeature.PROPERTY_ID,
    MovingFeature.JSON_PROPERTY_TEMPORAL_GEOMETRY,
    MovingFeature.JSON_PROPERTY_TEMPORAL_PROPERTIES,
    GeoJSONFeature.PROPERTY_GEOMETRY,
    GeoJSONFeature.PROPERTY_PROPERTIES,
    GeoJSONFeature.PROPERTY_BBOX,
    MovingFeature.JSON_PROPERTY_TIME,
    MovingFeature.JSON_PROPERTY_CRS,
    MovingFeature.JSON_PROPERTY_TRS,
    GeoJSONFeature.PROPERTY_LINKS
})
public final class MovingFeature extends GeoJSONFeature {

    public static final String JSON_PROPERTY_TEMPORAL_GEOMETRY = "temporalGeometry";
    //ponytail: spec's temporalGeometry is oneOf(temporalPrimitiveGeometry, temporalComplexGeometry);
    //only the dominant single-primitive case is modeled.
    private TemporalPrimitiveGeometry temporalGeometry;

    public static final String JSON_PROPERTY_TEMPORAL_PROPERTIES = "temporalProperties";
    //ponytail: array of "parametricValues"-shaped keyed objects; kept as a raw list, same
    //reasoning as TemporalProperties#temporalProperties.
    private List<Object> temporalProperties;

    public static final String JSON_PROPERTY_TIME = "time";
    //ponytail: shadows the inherited JSON-FG GeoJSONFeature#time (a JSONFGTime object); MF-JSON's
    //own "time" is the moving feature's temporal extent, an array of 2 date-times. Java accessors
    //are renamed (can't override getTime()/setTime(), the types are unrelated) but the JSON key
    //stays "time" per the MF-JSON schema.
    private List<String> temporalExtent;

    public static final String JSON_PROPERTY_CRS = "crs";
    //ponytail: oneOf(namedCRS, linkedCRS); passed through untyped.
    private Object crs;

    public static final String JSON_PROPERTY_TRS = "trs";
    //ponytail: oneOf(linkedCRS, {type:Name,properties}); passed through untyped.
    private Object trs;

    public MovingFeature() {
    }

    public MovingFeature temporalGeometry(TemporalPrimitiveGeometry temporalGeometry) {
        this.temporalGeometry = temporalGeometry;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_TEMPORAL_GEOMETRY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public TemporalPrimitiveGeometry getTemporalGeometry() {
        return temporalGeometry;
    }

    @JsonProperty(JSON_PROPERTY_TEMPORAL_GEOMETRY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTemporalGeometry(TemporalPrimitiveGeometry temporalGeometry) {
        this.temporalGeometry = temporalGeometry;
    }

    public MovingFeature temporalProperties(List<Object> temporalProperties) {
        this.temporalProperties = temporalProperties;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_TEMPORAL_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Object> getTemporalProperties() {
        return temporalProperties;
    }

    @JsonProperty(JSON_PROPERTY_TEMPORAL_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTemporalProperties(List<Object> temporalProperties) {
        this.temporalProperties = temporalProperties;
    }

    public MovingFeature temporalExtent(List<String> temporalExtent) {
        this.temporalExtent = temporalExtent;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getTemporalExtent() {
        return temporalExtent;
    }

    @JsonProperty(JSON_PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTemporalExtent(List<String> temporalExtent) {
        this.temporalExtent = temporalExtent;
    }

    public MovingFeature crs(Object crs) {
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

    public MovingFeature trs(Object trs) {
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
        MovingFeature other = (MovingFeature) o;
        return super.equals(o)
                && Objects.equals(this.temporalGeometry, other.temporalGeometry)
                && Objects.equals(this.temporalProperties, other.temporalProperties)
                && Objects.equals(this.temporalExtent, other.temporalExtent)
                && Objects.equals(this.crs, other.crs)
                && Objects.equals(this.trs, other.trs);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(temporalGeometry, temporalProperties, temporalExtent, crs, trs);
    }

}
