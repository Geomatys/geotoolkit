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
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;

/**
 * GeometrycollectionGeoJSON
 */
@JsonPropertyOrder({
    GeoJSONGeometryCollection.PROPERTY_TYPE,
    GeoJSONGeometryCollection.PROPERTY_GEOMETRIES
})
@XmlRootElement(name = "GeoJSONGeometryCollection")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "GeoJSONGeometryCollection")
public class GeoJSONGeometryCollection extends GeoJSONGeometry {

    public static final String PROPERTY_GEOMETRIES = "geometries";
    @XmlElement(name = PROPERTY_GEOMETRIES)
    @jakarta.annotation.Nonnull
    private List<GeoJSONGeometry> geometries = new ArrayList<>();

    public GeoJSONGeometryCollection() {
    }

    @Override
    public String getType() {
        return TYPE_GEOMETRYCOLLECTION;
    }

    /**
     * Get geometries
     *
     * @return geometries
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(PROPERTY_GEOMETRIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_GEOMETRIES)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<GeoJSONGeometry> getGeometries() {
        return geometries;
    }

    @JsonProperty(PROPERTY_GEOMETRIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_GEOMETRIES)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setGeometries(@jakarta.annotation.Nonnull List<GeoJSONGeometry> geometries) {
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
        GeoJSONGeometryCollection geometrycollectionGeoJSON = (GeoJSONGeometryCollection) o;
        return super.equals(o)
                && Objects.equals(this.geometries, geometrycollectionGeoJSON.geometries);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(geometries);
    }

}
