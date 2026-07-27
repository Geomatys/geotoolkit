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
package org.geotoolkit.ogcapi.dto.geojson;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geotoolkit.ogcapi.dto.common.Link;


/**
 * FeatureCollection
 */
@JsonPropertyOrder({
    GeoJSONFeatureCollection.PROPERTY_TYPE,
    GeoJSONFeatureCollection.PROPERTY_LINKS,
    GeoJSONFeatureCollection.PROPERTY_TIME_STAMP,
    GeoJSONFeatureCollection.PROPERTY_NUMBER_MATCHED,
    GeoJSONFeatureCollection.PROPERTY_NUMBER_RETURNED,
    GeoJSONFeatureCollection.PROPERTY_FEATURE_TYPE,
    GeoJSONFeatureCollection.PROPERTY_GEOMETRY_DIMENSION,
    GeoJSONFeatureCollection.PROPERTY_FEATURE_SCHEMA,
    GeoJSONFeatureCollection.PROPERTY_COORD_REF_SYS,
    GeoJSONFeatureCollection.PROPERTY_BBOX,
    GeoJSONFeatureCollection.PROPERTY_FEATURES,
})
public class GeoJSONFeatureCollection extends GeoJSONObject {

    //geojson
    public static final String PROPERTY_FEATURES = "features";
    //added in OGC-API features
    public static final String PROPERTY_LINKS = "links";
    public static final String PROPERTY_TIME_STAMP = "timeStamp";
    public static final String PROPERTY_NUMBER_MATCHED = "numberMatched";
    public static final String PROPERTY_NUMBER_RETURNED = "numberReturned";
    //added in JSON-FG
    public static final String PROPERTY_FEATURE_TYPE = "featureType";
    public static final String PROPERTY_GEOMETRY_DIMENSION = "geometryDimension";
    public static final String PROPERTY_FEATURE_SCHEMA = "featureSchema";
    public static final String PROPERTY_COORD_REF_SYS = "coordRefSys";

    private List<GeoJSONFeature> features = new ArrayList<>();

    private List<Link> links = new ArrayList<>();

    private OffsetDateTime timeStamp;

    private Integer numberMatched;

    private Integer numberReturned;

    private List<String> featureType;

    private Integer geometryDimension;

    private String featureSchema;

    private JSONFGCoordRefSys coordRefSys;

    public GeoJSONFeatureCollection() {
    }

    @Override
    public String getType() {
        return TYPE_FEATURE_COLLECTION;
    }

    /**
     * Get features
     *
     * @return features
     */
    @JsonProperty(PROPERTY_FEATURES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<GeoJSONFeature> getFeatures() {
        return features;
    }

    @JsonProperty(PROPERTY_FEATURES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setFeatures(List<GeoJSONFeature> features) {
        this.features = features;
    }

    /**
     * Get links
     *
     * @return links
     */
    @JsonProperty(PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    /**
     * This property indicates the time and date when the response was generated.
     *
     * @return timeStamp
     */
    @JsonProperty(PROPERTY_TIME_STAMP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public OffsetDateTime getTimeStamp() {
        return timeStamp;
    }

    @JsonProperty(PROPERTY_TIME_STAMP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTimeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * The number of features of the feature type that match the selection parameters like &#x60;bbox&#x60;. minimum: 0
     *
     * @return numberMatched
     */
    @JsonProperty(PROPERTY_NUMBER_MATCHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getNumberMatched() {
        return numberMatched;
    }

    @JsonProperty(PROPERTY_NUMBER_MATCHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setNumberMatched(Integer numberMatched) {
        this.numberMatched = numberMatched;
    }

    /**
     * The number of features in the feature collection. A server may omit this information in a response, if the
     * information about the number of features is not known or difficult to compute. If the value is provided, the
     * value shall be identical to the number of items in the \&quot;features\&quot; array. minimum: 0
     *
     * @return numberReturned
     */
    @JsonProperty(PROPERTY_NUMBER_RETURNED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getNumberReturned() {
        return numberReturned;
    }

    @JsonProperty(PROPERTY_NUMBER_RETURNED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setNumberReturned(Integer numberReturned) {
        this.numberReturned = numberReturned;
    }

    /**
     * Get geometryDimension
     * minimum: 0
     * maximum: 3
     * @return geometryDimension
     */
    @JsonProperty(PROPERTY_GEOMETRY_DIMENSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getGeometryDimension() {
        return geometryDimension;
    }


    @JsonProperty(PROPERTY_GEOMETRY_DIMENSION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setGeometryDimension(Integer geometryDimension) {
        this.geometryDimension = geometryDimension;
    }

    /**
     * Get featureType
     * @return featureType
     */
    @JsonProperty(PROPERTY_FEATURE_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JsonFormat( with = {JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED})
    public List<String> getFeatureType() {
        return featureType;
    }

    @JsonProperty(PROPERTY_FEATURE_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JsonFormat( with = {JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED})
    public void setFeatureType(List<String> featureType) {
        this.featureType = featureType;
    }

    /**
     * Get featureSchema
     * @return featureSchema
     */
    @JsonProperty(PROPERTY_FEATURE_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getFeatureSchema() {
        return featureSchema;
    }

    @JsonProperty(PROPERTY_FEATURE_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setFeatureSchema(String featureSchema) {
        this.featureSchema = featureSchema;
    }

    /**
     * Get coordRefSys
     * @return coordRefSys
     */
    @JsonProperty(PROPERTY_COORD_REF_SYS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public JSONFGCoordRefSys getCoordRefSys() {
        return coordRefSys;
    }


    @JsonProperty(PROPERTY_COORD_REF_SYS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCoordRefSys(JSONFGCoordRefSys coordRefSys) {
        this.coordRefSys = coordRefSys;
    }

    /**
     * Return true if this FeatureCollection object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoJSONFeatureCollection other = (GeoJSONFeatureCollection) o;
        return super.equals(o)
                && Objects.equals(this.features, other.features)
                && Objects.equals(this.links, other.links)
                && Objects.equals(this.timeStamp, other.timeStamp)
                && Objects.equals(this.numberMatched, other.numberMatched)
                && Objects.equals(this.numberReturned, other.numberReturned)
                && Objects.equals(this.geometryDimension, other.geometryDimension)
                && Objects.equals(this.featureType, other.featureType)
                && Objects.equals(this.featureSchema, other.featureSchema)
                && Objects.equals(this.coordRefSys, other.coordRefSys);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(features, links, timeStamp, numberMatched,
                numberReturned, geometryDimension, featureType, featureSchema, coordRefSys);
    }

}
