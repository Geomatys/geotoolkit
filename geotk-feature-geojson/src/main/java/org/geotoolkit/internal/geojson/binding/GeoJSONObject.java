/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.internal.geojson.binding;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONGeometryCollection;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONLineString;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONMultiLineString;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONMultiPoint;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONMultiPolygon;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONPoint;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONPolygon;
import org.geotoolkit.util.DeltaComparable;

/**
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = GeoJSONFeatureCollection.class, name = "FeatureCollection"),
    @JsonSubTypes.Type(value = GeoJSONFeature.class, name = "Feature"),
    @JsonSubTypes.Type(value = GeoJSONPoint.class, name = "Point"),
    @JsonSubTypes.Type(value = GeoJSONLineString.class, name = "LineString"),
    @JsonSubTypes.Type(value = GeoJSONPolygon.class, name = "Polygon"),
    @JsonSubTypes.Type(value = GeoJSONMultiPoint.class, name = "MultiPoint"),
    @JsonSubTypes.Type(value = GeoJSONMultiLineString.class, name = "MultiLineString"),
    @JsonSubTypes.Type(value = GeoJSONMultiPolygon.class, name = "MultiPolygon"),
    @JsonSubTypes.Type(value = GeoJSONGeometryCollection.class, name = "GeometryCollection")
})
public class GeoJSONObject implements Serializable, DeltaComparable {

    private String type;
    private double[] bbox;
    private GeoJSONCRS crs;

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
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.type);
        hash = 41 * hash + Arrays.hashCode(this.bbox);
        hash = 41 * hash + Objects.hashCode(this.crs);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeoJSONObject other = (GeoJSONObject) obj;
        return Objects.equals(this.type, other.type) &&
               Arrays.equals(this.bbox, other.bbox) &&
               Objects.equals(this.crs, other.crs);
    }

    @Override
    public boolean equals(Object obj, float delta) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeoJSONObject other = (GeoJSONObject) obj;
        return Objects.equals(this.type, other.type) &&
               DeltaComparable.arrayEquals(this.bbox, other.bbox, delta) &&
               Objects.equals(this.crs, other.crs);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (type != null) {
            sb.append("type = ");
            sb.append(type);
            sb.append('\n');
        }
        if (bbox != null) {
            sb.append("bbox = ");
            sb.append(Arrays.toString(bbox));
            sb.append('\n');
        }
        if (crs != null) {
            sb.append("crs = ");
            sb.append(crs);
            sb.append('\n');
        }
        return sb.toString();
    }

}
