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

import org.geotoolkit.data.geojson.utils.GeoJSONTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONFeature extends GeoJSONObject {

    private GeoJSONGeometry geometry = null;
    /**
     * Identifier (id attribute) of the feature. According to RFC 7946, it is
     * optional and can either be a number or a string.
     */
    private Object id = null;
    private Map<String, Object> properties = new HashMap<>();

    public GeoJSONFeature() {
        setType(GeoJSONTypes.FEATURE);
    }

    public GeoJSONGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoJSONGeometry geometry) {
        this.geometry = geometry;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (id != null) {
            sb.append("id:").append(id).append('\n');
        }
        if (properties != null) {
            sb.append("properties:").append(properties).append('\n');
        }
        if (geometry != null) {
            sb.append("geometry:").append(geometry).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GeoJSONFeature && super.equals(obj)) {
            GeoJSONFeature that = (GeoJSONFeature) obj;
            return Objects.equals(this.geometry,   that.geometry) &&
                   Objects.equals(this.id,         that.id) &&
                   Objects.equals(this.properties, that.properties);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + super.hashCode();
        hash = 59 * hash + Objects.hashCode(this.geometry);
        hash = 59 * hash + Objects.hashCode(this.id);
        hash = 59 * hash + Objects.hashCode(this.properties);
        return hash;
    }
}
