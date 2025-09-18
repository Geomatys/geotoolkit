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
import jakarta.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    GeoJSONObject.PROPERTY_TYPE,
    GeoJSONObject.PROPERTY_BBOX
})
public abstract class GeoJSONObject extends DataTransferObject {

    public static final String TYPE_FEATURE = "Feature";
    public static final String TYPE_FEATURE_COLLECTION = "FeatureCollection";
    public static final String TYPE_POINT = "Point";
    public static final String TYPE_LINESTRING = "LineString";
    public static final String TYPE_POLYGON = "Polygon";
    public static final String TYPE_MULTIPOINT = "MultiPoint";
    public static final String TYPE_MULTILINESTRING = "MultiLineString";
    public static final String TYPE_MULTIPOLYGON = "MultiPolygon";
    public static final String TYPE_GEOMETRYCOLLECTION = "GeometryCollection";
    // JSON-FG
    public static final String TYPE_POLYHEDRON = "Polyhedron";
    public static final String TYPE_MULTIPOLYHEDRON = "MultiPolyhedron";
    public static final String TYPE_PRISM = "Prism";
    public static final String TYPE_MULTIPRISM = "MultiPrism";

    public static final String PROPERTY_TYPE = "type";
    public static final String PROPERTY_BBOX = "bbox";

    @XmlElement(name = PROPERTY_BBOX)
    @jakarta.annotation.Nullable
    private List<Double> bbox = new ArrayList<>();

    @jakarta.annotation.Nonnull
    @JsonProperty(PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = PROPERTY_TYPE)
    public abstract String getType();

    /**
     * Get bbox
     *
     * @return bbox [minx,miny,maxx,maxy]
     */
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_BBOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_BBOX)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Double> getBbox() {
        return bbox;
    }

    @JsonProperty(PROPERTY_BBOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_BBOX)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setBbox(@jakarta.annotation.Nullable List<Double> bbox) {
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
        final GeoJSONObject other = (GeoJSONObject) o;
        return Objects.equals(this.getType(), other.getType())
                && Objects.equals(this.bbox, other.bbox);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), bbox);
    }
}
