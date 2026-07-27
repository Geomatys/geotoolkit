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
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * GeoJSONFeature
 */
@JsonPropertyOrder({
    GeoJSONFeature.PROPERTY_TYPE,
    GeoJSONFeature.PROPERTY_ID,
    GeoJSONFeature.PROPERTY_CONFORMS_TO,
    GeoJSONFeature.PROPERTY_FEATURE_TYPE,
    GeoJSONFeature.PROPERTY_FEATURE_SCHEMA,
    GeoJSONFeature.PROPERTY_LINKS,
    GeoJSONFeature.PROPERTY_BBOX,
    GeoJSONFeature.PROPERTY_TIME,
    GeoJSONFeature.PROPERTY_COORD_REF_SYS,
    GeoJSONFeature.PROPERTY_GEOMETRY,
    GeoJSONFeature.PROPERTY_PLACE,
    GeoJSONFeature.PROPERTY_PROPERTIES
})
public class GeoJSONFeature extends GeoJSONObject {

    //geojson
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_GEOMETRY = "geometry";
    public static final String PROPERTY_PROPERTIES = "properties";
    //added in OGC-API features
    public static final String PROPERTY_LINKS = "links";
    //added in JSON-FG
    public static final String PROPERTY_CONFORMS_TO = "conformsTo";
    public static final String PROPERTY_FEATURE_TYPE = "featureType";
    public static final String PROPERTY_FEATURE_SCHEMA = "featureSchema";
    public static final String PROPERTY_TIME = "time";
    public static final String PROPERTY_COORD_REF_SYS = "coordRefSys";
    public static final String PROPERTY_PLACE = "place";


    private Map<String, Object> properties;

    private GeoJSONGeometry geometry;

    private Object id;

    private List<Link> links = new ArrayList<>();

    private List<String> conformsTo = new ArrayList<>();

    private List<String> featureType = new ArrayList<>();

    private String featureSchema;

    private JSONFGTime time;

    private JSONFGCoordRefSys coordRefSys;

    private GeoJSONGeometry place;


    public GeoJSONFeature() {
    }

    @Override
    public String getType() {
        return TYPE_FEATURE;
    }

    /**
     * Get id
     *
     * @return id
     */
    @JsonProperty(PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getId() {
        return id;
    }

    @JsonProperty(PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setId(Object id) {
        this.id = id;
    }

    /**
     * Get properties
     *
     * @return properties
     */
    @JsonProperty(PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonProperty(PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Get geometry
     *
     * @return geometry
     */
    @JsonProperty(PROPERTY_GEOMETRY)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public GeoJSONGeometry getGeometry() {
        return geometry;
    }

    @JsonProperty(PROPERTY_GEOMETRY)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setGeometry(GeoJSONGeometry geometry) {
        this.geometry = geometry;
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
     * This JSON Schema is part of JSON-FG version 0.2.2
     * @return conformsTo
     */
    @JsonProperty(PROPERTY_CONFORMS_TO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getConformsTo() {
        return conformsTo;
    }

    @JsonProperty(PROPERTY_CONFORMS_TO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setConformsTo(List<String> conformsTo) {
        this.conformsTo = conformsTo;
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
     * Get time
     * @return time
     */
    @JsonProperty(PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public JSONFGTime getTime() {
        return time;
    }


    @JsonProperty(PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public void setTime(JSONFGTime time) {
        this.time = time;
    }

    /**
     * Get coordRefSys
     * @return coordRefSys
     */
    @JsonProperty(PROPERTY_COORD_REF_SYS)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public JSONFGCoordRefSys getCoordRefSys() {
        return coordRefSys;
    }


    @JsonProperty(PROPERTY_COORD_REF_SYS)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public void setCoordRefSys(JSONFGCoordRefSys coordRefSys) {
        this.coordRefSys = coordRefSys;
    }

    /**
     * Get place
     * @return place
     */
    @JsonProperty(PROPERTY_PLACE)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public GeoJSONGeometry getPlace() {
        return place;
    }

    @JsonProperty(PROPERTY_PLACE)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public void setPlace(GeoJSONGeometry place) {
        this.place = place;
    }

    /**
     * Return true if this GeoJSON_Feature object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoJSONFeature other = (GeoJSONFeature) o;
        return super.equals(o)
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.properties, other.properties)
                && Objects.equals(this.geometry, other.geometry)
                && Objects.equals(this.links, other.links)
                && Objects.equals(this.conformsTo, other.conformsTo)
                && Objects.equals(this.featureType, other.featureType)
                && Objects.equals(this.featureSchema, other.featureSchema)
                && Objects.equals(this.time, other.time)
                && Objects.equals(this.place, other.place)
                && Objects.equals(this.coordRefSys, other.coordRefSys);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(id, properties, geometry, links, conformsTo, featureType, featureSchema, time, place, coordRefSys);
    }

}
