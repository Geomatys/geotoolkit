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
}
