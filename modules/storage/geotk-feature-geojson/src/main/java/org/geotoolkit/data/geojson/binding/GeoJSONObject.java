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
import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(this.getClass().getSimpleName()).append("]\n");
        if (type != null) {
            sb.append("type:").append(type).append('\n');
        }
        if (bbox != null) {
            sb.append("bbox:").append(bbox).append('\n');
        }
        if (crs != null) {
            sb.append("crs:").append(crs).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GeoJSONObject) {
            GeoJSONObject that = (GeoJSONObject) obj;
            return Objects.equals(this.type, that.type) &&
                   Objects.equals(this.bbox, that.bbox) &&
                   Objects.equals(this.crs,  that.crs);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.type);
        hash = 59 * hash + Arrays.hashCode(this.bbox);
        hash = 59 * hash + Objects.hashCode(this.crs);
        return hash;
    }


}
