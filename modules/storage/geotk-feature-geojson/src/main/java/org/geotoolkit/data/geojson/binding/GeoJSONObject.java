/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.geojson.binding;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static org.geotoolkit.data.geojson.binding.GeoJSONGeometry.*;

import java.io.Serializable;

/**
 * @author Quentin Boileau (Geomatys)
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=GeoJSONFeatureCollection.class,    name="FeatureCollection"),
        @JsonSubTypes.Type(value=GeoJSONFeature.class,              name="Feature"),
        @JsonSubTypes.Type(value=GeoJSONPoint.class,                name="Point"),
        @JsonSubTypes.Type(value=GeoJSONLineString.class,           name="LineString"),
        @JsonSubTypes.Type(value=GeoJSONPolygon.class,              name="Polygon"),
        @JsonSubTypes.Type(value=GeoJSONMultiPoint.class,           name="MultiPoint"),
        @JsonSubTypes.Type(value=GeoJSONMultiLineString.class,      name="MultiLineString"),
        @JsonSubTypes.Type(value=GeoJSONMultiPolygon.class,         name="MultiPolygon"),
        @JsonSubTypes.Type(value=GeoJSONGeometryCollection.class,   name="GeometryCollection")
})
public class GeoJSONObject implements Serializable {

    private String type;
    private double[] bbox = null;
    private GeoJSONCRS crs = null;

    public GeoJSONObject() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[] getBbox() {
        return bbox;
    }

    public void setBbox(double[] bbox) {
        this.bbox = bbox;
    }

    public GeoJSONCRS getCrs() {
        return crs;
    }

    public void setCrs(GeoJSONCRS crs) {
        this.crs = crs;
    }
}
