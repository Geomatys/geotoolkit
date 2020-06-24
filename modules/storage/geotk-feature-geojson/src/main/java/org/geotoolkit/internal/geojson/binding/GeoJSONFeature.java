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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.geotoolkit.storage.geojson.GeoJSONConstants;

/**
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
public class GeoJSONFeature extends GeoJSONObject {

    private GeoJSONGeometry geometry;
    /**
     * Identifier (id attribute) of the feature. According to RFC 7946, it is
     * optional and can either be a number or a string.
     */
    private Object id;
    private Map<String, Object> properties = new HashMap<>();

    public GeoJSONFeature() {
        setType(GeoJSONConstants.FEATURE);
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
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.geometry);
        hash = 19 * hash + Objects.hashCode(this.id);
        hash = 19 * hash + Objects.hashCode(this.properties);
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
        final GeoJSONFeature other = (GeoJSONFeature) obj;
        if (!Objects.equals(this.geometry, other.geometry)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.properties, other.properties)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (id != null) {
            sb.append("id = ");
            sb.append(id);
            sb.append('\n');
        }
        if (geometry != null) {
            sb.append("geometry = ");
            sb.append(geometry);
            sb.append('\n');
        }
        for (Entry<String,Object> entry : properties.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" = ");
            sb.append(entry.getValue());
            sb.append('\n');
        }
        return sb.toString();
    }
}
