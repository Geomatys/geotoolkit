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
 * GeoJSONLineString
 */
@JsonPropertyOrder({
    GeoJSONLineString.JSON_PROPERTY_TYPE,
    GeoJSONLineString.JSON_PROPERTY_COORDINATES,
    GeoJSONLineString.JSON_PROPERTY_BBOX
})
@XmlRootElement(name = "GeoJSONLineString")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "GeoJSONLineString")
public class GeoJSONLineString extends DataTransferObject {

    /**
     * Gets or Sets type
     */
    @XmlType(name = "TypeEnum")
    @XmlEnum(String.class)
    public enum TypeEnum {
        @XmlEnumValue("LineString")
        LINE_STRING(String.valueOf("LineString"));

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

    public static final String JSON_PROPERTY_COORDINATES = "coordinates";
    @XmlElement(name = "coordinates")
    @jakarta.annotation.Nonnull
    private List<List<BigDecimal>> coordinates = new ArrayList<>();

    public static final String JSON_PROPERTY_BBOX = "bbox";
    @XmlElement(name = "bbox")
    @jakarta.annotation.Nullable
    private List<BigDecimal> bbox = new ArrayList<>();

    public GeoJSONLineString() {
    }

    public GeoJSONLineString type(@jakarta.annotation.Nonnull TypeEnum type) {
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

    public GeoJSONLineString coordinates(@jakarta.annotation.Nonnull List<List<BigDecimal>> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public GeoJSONLineString addCoordinatesItem(List<BigDecimal> coordinatesItem) {
        if (this.coordinates == null) {
            this.coordinates = new ArrayList<>();
        }
        this.coordinates.add(coordinatesItem);
        return this;
    }

    /**
     * Get coordinates
     *
     * @return coordinates
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "coordinates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<List<BigDecimal>> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "coordinates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setCoordinates(@jakarta.annotation.Nonnull List<List<BigDecimal>> coordinates) {
        this.coordinates = coordinates;
    }

    public GeoJSONLineString bbox(@jakarta.annotation.Nullable List<BigDecimal> bbox) {
        this.bbox = bbox;
        return this;
    }

    public GeoJSONLineString addBboxItem(BigDecimal bboxItem) {
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
     * Return true if this GeoJSON_LineString object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoJSONLineString geoJSONLineString = (GeoJSONLineString) o;
        return Objects.equals(this.type, geoJSONLineString.type)
                && Objects.equals(this.coordinates, geoJSONLineString.coordinates)
                && Objects.equals(this.bbox, geoJSONLineString.bbox);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, coordinates, bbox);
    }

}
