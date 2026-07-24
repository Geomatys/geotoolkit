/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.dto.connectedsystems.geojson;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Base of the {@code properties} sub-object shared by every OGC API - Connected Systems - Part 1
 * GeoJSON feature resource ({@link System}, {@link Deployment}, {@link DeployedSystem},
 * {@link Procedure}, {@link SamplingFeature}).
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
public abstract class FeatureProperties extends DataTransferObject {

    public static final String JSON_PROPERTY_FEATURE_TYPE = "featureType";
    private String featureType;

    public static final String JSON_PROPERTY_UID = "uid";
    private String uid;

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    /**
     * Type of feature described, as a URI. (required) Each resource restricts this to its own
     * set of allowed values, see the subclass javadoc.
     */
    @JsonProperty(JSON_PROPERTY_FEATURE_TYPE)
    public String getFeatureType() {
        return featureType;
    }

    @JsonProperty(JSON_PROPERTY_FEATURE_TYPE)
    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    /**
     * Globally unique identifier (URI) of the feature. (required)
     */
    @JsonProperty(JSON_PROPERTY_UID)
    public String getUid() {
        return uid;
    }

    @JsonProperty(JSON_PROPERTY_UID)
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Human readable name of the feature. (required)
     */
    @JsonProperty(JSON_PROPERTY_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Human readable description of the feature. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public void setDescription(String description) {
        this.description = description;
    }

}
