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
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeatureCollection;

/**
 * MF-JSON {@code movingFeatures} object: response wrapper of {@code GET /collections/{collectionId}/items}.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    GeoJSONFeatureCollection.PROPERTY_TYPE,
    MovingFeatures.JSON_PROPERTY_FEATURES,
    MovingFeatures.JSON_PROPERTY_CRS,
    MovingFeatures.JSON_PROPERTY_TRS,
    GeoJSONFeatureCollection.PROPERTY_BBOX,
    MovingFeatures.JSON_PROPERTY_TIME,
    GeoJSONFeatureCollection.PROPERTY_LINKS,
    GeoJSONFeatureCollection.PROPERTY_TIME_STAMP,
    GeoJSONFeatureCollection.PROPERTY_NUMBER_MATCHED,
    GeoJSONFeatureCollection.PROPERTY_NUMBER_RETURNED
})
public final class MovingFeatures extends GeoJSONFeatureCollection {

    public static final String JSON_PROPERTY_FEATURES = "features";
    //ponytail: shadows the inherited List<GeoJSONFeature> features (generics are invariant, so
    //List<MovingFeature> can't override it) but binds to the same JSON key.
    private List<MovingFeature> features;

    public static final String JSON_PROPERTY_CRS = "crs";
    //ponytail: oneOf(namedCRS, linkedCRS); passed through untyped.
    private Object crs;

    public static final String JSON_PROPERTY_TRS = "trs";
    //ponytail: oneOf(linkedCRS, {type:Name,properties}); passed through untyped.
    private Object trs;

    public static final String JSON_PROPERTY_TIME = "time";
    private List<String> time;

    public MovingFeatures() {
    }

    public MovingFeatures features(List<MovingFeature> features) {
        this.features = features;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_FEATURES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<MovingFeature> getMovingFeatures() {
        return features;
    }

    @JsonProperty(JSON_PROPERTY_FEATURES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setMovingFeatures(List<MovingFeature> features) {
        this.features = features;
    }

    public MovingFeatures crs(Object crs) {
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

    public MovingFeatures trs(Object trs) {
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

    public MovingFeatures time(List<String> time) {
        this.time = time;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getTime() {
        return time;
    }

    @JsonProperty(JSON_PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTime(List<String> time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MovingFeatures other = (MovingFeatures) o;
        return super.equals(o)
                && Objects.equals(this.features, other.features)
                && Objects.equals(this.crs, other.crs)
                && Objects.equals(this.trs, other.trs)
                && Objects.equals(this.time, other.time);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(features, crs, trs, time);
    }

}
