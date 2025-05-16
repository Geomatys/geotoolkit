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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * GeoJSONFeature
 */
@JsonPropertyOrder({
    GeoJSONFeature.JSON_PROPERTY_TYPE,
    GeoJSONFeature.JSON_PROPERTY_ID,
    GeoJSONFeature.JSON_PROPERTY_PROPERTIES,
    GeoJSONFeature.JSON_PROPERTY_GEOMETRY,
    GeoJSONFeature.JSON_PROPERTY_BBOX
})
@XmlRootElement(name = "GeoJSONFeature")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "GeoJSONFeature")
public class GeoJSONFeature extends DataTransferObject {

    /**
     * Gets or Sets type
     */
    @XmlType(name = "TypeEnum")
    @XmlEnum(String.class)
    public enum TypeEnum {
        @XmlEnumValue("Feature")
        FEATURE(String.valueOf("Feature"));

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static TypeEnum fromValue(String value) {
            for (TypeEnum b : TypeEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nonnull
    private TypeEnum type;

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nullable
    private Object id;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    @XmlElement(name = "properties")
    @jakarta.annotation.Nullable
    private Object properties;

    public static final String JSON_PROPERTY_GEOMETRY = "geometry";
    @XmlElement(name = "geometry")
    @jakarta.annotation.Nonnull
    private GeoJSONFeatureGeometry geometry;

    public static final String JSON_PROPERTY_BBOX = "bbox";
    @XmlElement(name = "bbox")
    @jakarta.annotation.Nullable
    private List<BigDecimal> bbox = new ArrayList<>();

    public GeoJSONFeature() {
    }

    public GeoJSONFeature type(@jakarta.annotation.Nonnull TypeEnum type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public TypeEnum getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nonnull TypeEnum type) {
        this.type = type;
    }

    public GeoJSONFeature id(@jakarta.annotation.Nullable Object id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "id")
    public Object getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "id")
    public void setId(@jakarta.annotation.Nullable Object id) {
        this.id = id;
    }

    public GeoJSONFeature properties(@jakarta.annotation.Nullable Object properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Get properties
     *
     * @return properties
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "properties")
    public Object getProperties() {
        return properties;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "properties")
    public void setProperties(@jakarta.annotation.Nullable Object properties) {
        this.properties = properties;
    }

    public GeoJSONFeature geometry(@jakarta.annotation.Nonnull GeoJSONFeatureGeometry geometry) {
        this.geometry = geometry;
        return this;
    }

    /**
     * Get geometry
     *
     * @return geometry
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_GEOMETRY)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "geometry")
    public GeoJSONFeatureGeometry getGeometry() {
        return geometry;
    }

    @JsonProperty(JSON_PROPERTY_GEOMETRY)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "geometry")
    public void setGeometry(@jakarta.annotation.Nonnull GeoJSONFeatureGeometry geometry) {
        this.geometry = geometry;
    }

    public GeoJSONFeature bbox(@jakarta.annotation.Nullable List<BigDecimal> bbox) {
        this.bbox = bbox;
        return this;
    }

    public GeoJSONFeature addBboxItem(BigDecimal bboxItem) {
        if (this.bbox == null) {
            this.bbox = new ArrayList<>();
        }
        this.bbox.add(bboxItem);
        return this;
    }

    /**
     * Get bbox
     *
     * @return bbox
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BBOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "bbox")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<BigDecimal> getBbox() {
        return bbox;
    }

    @JsonProperty(JSON_PROPERTY_BBOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "bbox")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setBbox(@jakarta.annotation.Nullable List<BigDecimal> bbox) {
        this.bbox = bbox;
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
        GeoJSONFeature geoJSONFeature = (GeoJSONFeature) o;
        return Objects.equals(this.type, geoJSONFeature.type)
                && Objects.equals(this.id, geoJSONFeature.id)
                && Objects.equals(this.properties, geoJSONFeature.properties)
                && Objects.equals(this.geometry, geoJSONFeature.geometry)
                && Objects.equals(this.bbox, geoJSONFeature.bbox);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id, properties, geometry, bbox);
    }

}
