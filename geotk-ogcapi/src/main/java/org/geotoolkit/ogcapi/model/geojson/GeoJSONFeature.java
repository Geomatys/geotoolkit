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
package org.geotoolkit.ogcapi.model.geojson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
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
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.common.Link;

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
@XmlRootElement(name = "GeoJSONFeature")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "GeoJSONFeature")
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


    @XmlElement(name = PROPERTY_PROPERTIES)
    @jakarta.annotation.Nullable
    private Map<String, Object> properties;

    @XmlElement(name = PROPERTY_GEOMETRY)
    @jakarta.annotation.Nonnull
    private GeoJSONGeometry geometry;

    @XmlElement(name = PROPERTY_ID)
    @jakarta.annotation.Nullable
    private Object id;

    @XmlElement(name = PROPERTY_LINKS)
    @jakarta.annotation.Nonnull
    private List<Link> links = new ArrayList<>();

    @XmlElement(name = PROPERTY_CONFORMS_TO)
    @jakarta.annotation.Nullable
    private List<String> conformsTo = new ArrayList<>();

    @XmlElement(name = PROPERTY_FEATURE_TYPE)
    @jakarta.annotation.Nullable
    private List<String> featureType = new ArrayList<>();

    @XmlElement(name = PROPERTY_FEATURE_SCHEMA)
    @jakarta.annotation.Nullable
    private String featureSchema;

    @XmlElement(name = PROPERTY_TIME)
    @jakarta.annotation.Nullable
    private JSONFGTime time;

    @XmlElement(name = PROPERTY_COORD_REF_SYS)
    @jakarta.annotation.Nullable
    private JSONFGCoordRefSys coordRefSys;

    @XmlElement(name = PROPERTY_PLACE)
    @jakarta.annotation.Nullable
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
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_ID)
    public Object getId() {
        return id;
    }

    @JsonProperty(PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_ID)
    public void setId(@jakarta.annotation.Nullable Object id) {
        this.id = id;
    }

    /**
     * Get properties
     *
     * @return properties
     */
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_PROPERTIES)
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonProperty(PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_PROPERTIES)
    public void setProperties(@jakarta.annotation.Nullable Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Get geometry
     *
     * @return geometry
     */
    @JsonProperty(PROPERTY_GEOMETRY)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_GEOMETRY)
    public GeoJSONGeometry getGeometry() {
        return geometry;
    }

    @JsonProperty(PROPERTY_GEOMETRY)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_GEOMETRY)
    public void setGeometry(@jakarta.annotation.Nonnull GeoJSONGeometry geometry) {
        this.geometry = geometry;
    }

    /**
     * Get links
     *
     * @return links
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    @JacksonXmlProperty(localName = PROPERTY_LINKS)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    @JacksonXmlProperty(localName = PROPERTY_LINKS)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setLinks(@jakarta.annotation.Nonnull List<Link> links) {
        this.links = links;
    }

    /**
     * This JSON Schema is part of JSON-FG version 0.2.2
     * @return conformsTo
     */
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_CONFORMS_TO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_CONFORMS_TO)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getConformsTo() {
        return conformsTo;
    }

    @JsonProperty(PROPERTY_CONFORMS_TO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_CONFORMS_TO)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setConformsTo(@jakarta.annotation.Nullable List<String> conformsTo) {
        this.conformsTo = conformsTo;
    }

    /**
     * Get featureType
     * @return featureType
     */
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_FEATURE_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_FEATURE_TYPE)
    @JsonFormat( with = {JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED})
    public List<String> getFeatureType() {
        return featureType;
    }

    @JsonProperty(PROPERTY_FEATURE_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_FEATURE_TYPE)
    @JsonFormat( with = {JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED})
    public void setFeatureType(@jakarta.annotation.Nullable List<String> featureType) {
        this.featureType = featureType;
    }

    /**
     * Get featureSchema
     * @return featureSchema
     */
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_FEATURE_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_FEATURE_SCHEMA)
    public String getFeatureSchema() {
        return featureSchema;
    }


    @JsonProperty(PROPERTY_FEATURE_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_FEATURE_SCHEMA)
    public void setFeatureSchema(@jakarta.annotation.Nullable String featureSchema) {
        this.featureSchema = featureSchema;
    }

    /**
     * Get time
     * @return time
     */
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    @JacksonXmlProperty(localName = PROPERTY_TIME)
    public JSONFGTime getTime() {
        return time;
    }


    @JsonProperty(PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    @JacksonXmlProperty(localName = PROPERTY_TIME)
    public void setTime(@jakarta.annotation.Nullable JSONFGTime time) {
        this.time = time;
    }

    /**
     * Get coordRefSys
     * @return coordRefSys
     */
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_COORD_REF_SYS)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    @JacksonXmlProperty(localName = PROPERTY_COORD_REF_SYS)
    public JSONFGCoordRefSys getCoordRefSys() {
        return coordRefSys;
    }


    @JsonProperty(PROPERTY_COORD_REF_SYS)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    @JacksonXmlProperty(localName = PROPERTY_COORD_REF_SYS)
    public void setCoordRefSys(@jakarta.annotation.Nullable JSONFGCoordRefSys coordRefSys) {
        this.coordRefSys = coordRefSys;
    }

    /**
     * Get place
     * @return place
     */
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_PLACE)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    @JacksonXmlProperty(localName = PROPERTY_PLACE)
    public GeoJSONGeometry getPlace() {
        return place;
    }

    @JsonProperty(PROPERTY_PLACE)
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    @JacksonXmlProperty(localName = PROPERTY_PLACE)
    public void setPlace(@jakarta.annotation.Nullable GeoJSONGeometry place) {
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
