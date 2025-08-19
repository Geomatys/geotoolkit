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
import java.util.Objects;

/**
 * GeoJSONMultiPolygon
 */
@JsonPropertyOrder({
    GeoJSONMultiPolygon.PROPERTY_TYPE,
    GeoJSONMultiPolygon.PROPERTY_BBOX,
    GeoJSONMultiPolygon.PROPERTY_COORDINATES
})
@XmlRootElement(name = "GeoJSONMultiPolygon")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "GeoJSONMultiPolygon")
public class GeoJSONMultiPolygon extends GeoJSONGeometry {

    public static final String PROPERTY_COORDINATES = "coordinates";
    @XmlElement(name = PROPERTY_COORDINATES)
    @jakarta.annotation.Nonnull
    private List<List<List<List<Double>>>> coordinates = new ArrayList<>();

    public GeoJSONMultiPolygon() {
    }

    @Override
    public String getType() {
        return TYPE_MULTIPOLYGON;
    }

    /**
     * Get coordinates
     *
     * @return coordinates
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_COORDINATES)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<List<List<List<Double>>>> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_COORDINATES)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setCoordinates(@jakarta.annotation.Nonnull List<List<List<List<Double>>>> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Return true if this GeoJSON_MultiPolygon object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoJSONMultiPolygon geoJSONMultiPolygon = (GeoJSONMultiPolygon) o;
        return super.equals(o)
                && Objects.equals(this.coordinates, geoJSONMultiPolygon.coordinates);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(coordinates);
    }

}
