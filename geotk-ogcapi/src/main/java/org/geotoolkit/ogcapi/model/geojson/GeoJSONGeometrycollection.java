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

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * GeometrycollectionGeoJSON
 */
@JsonPropertyOrder({
    GeoJSONGeometrycollection.JSON_PROPERTY_TYPE,
    GeoJSONGeometrycollection.JSON_PROPERTY_GEOMETRIES
})
@XmlRootElement(name = "GeoJSONGeometrycollection")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "GeoJSONGeometrycollection")
public class GeoJSONGeometrycollection extends DataTransferObject {

    /**
     * Gets or Sets type
     */
    @XmlType(name = "TypeEnum")
    @XmlEnum(String.class)
    public enum TypeEnum {
        @XmlEnumValue("GeometryCollection")
        GEOMETRY_COLLECTION(String.valueOf("GeometryCollection"));

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

    public static final String JSON_PROPERTY_GEOMETRIES = "geometries";
    @XmlElement(name = "geometries")
    @jakarta.annotation.Nonnull
    private List<GeoJSONFeatureGeometry> geometries = new ArrayList<>();

    public GeoJSONGeometrycollection() {
    }

    public GeoJSONGeometrycollection type(@jakarta.annotation.Nonnull TypeEnum type) {
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

    public GeoJSONGeometrycollection geometries(@jakarta.annotation.Nonnull List<GeoJSONFeatureGeometry> geometries) {
        this.geometries = geometries;
        return this;
    }

    public GeoJSONGeometrycollection addGeometriesItem(GeoJSONFeatureGeometry geometriesItem) {
        if (this.geometries == null) {
            this.geometries = new ArrayList<>();
        }
        this.geometries.add(geometriesItem);
        return this;
    }

    /**
     * Get geometries
     *
     * @return geometries
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_GEOMETRIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "geometries")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<GeoJSONFeatureGeometry> getGeometries() {
        return geometries;
    }

    @JsonProperty(JSON_PROPERTY_GEOMETRIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "geometries")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setGeometries(@jakarta.annotation.Nonnull List<GeoJSONFeatureGeometry> geometries) {
        this.geometries = geometries;
    }

    /**
     * Return true if this geometrycollectionGeoJSON object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoJSONGeometrycollection geometrycollectionGeoJSON = (GeoJSONGeometrycollection) o;
        return Objects.equals(this.type, geometrycollectionGeoJSON.type)
                && Objects.equals(this.geometries, geometrycollectionGeoJSON.geometries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, geometries);
    }

}
