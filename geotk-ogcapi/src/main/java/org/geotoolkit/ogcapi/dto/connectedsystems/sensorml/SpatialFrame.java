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
package org.geotoolkit.ogcapi.dto.connectedsystems.sensorml;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A local spatial reference frame, e.g. as used by
 * {@link AbstractPhysicalProcess#getLocalReferenceFrames()}.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class SpatialFrame extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_LABEL = "label";
    private String label;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_ORIGIN = "origin";
    private String origin;

    public static final String JSON_PROPERTY_AXES = "axes";
    private List<Axis> axes;

    /**
     * Local identifier of this reference frame. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    public void setId(String id) {
        this.id = id;
    }

    public SpatialFrame id(String id) {
        setId(id);
        return this;
    }

    /**
     * Human readable label. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    public void setLabel(String label) {
        this.label = label;
    }

    public SpatialFrame label(String label) {
        setLabel(label);
        return this;
    }

    /**
     * Description of the reference frame. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public void setDescription(String description) {
        this.description = description;
    }

    public SpatialFrame description(String description) {
        setDescription(description);
        return this;
    }

    /**
     * Description of the origin of this reference frame. (required)
     */
    @JsonProperty(JSON_PROPERTY_ORIGIN)
    public String getOrigin() {
        return origin;
    }

    @JsonProperty(JSON_PROPERTY_ORIGIN)
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public SpatialFrame origin(String origin) {
        setOrigin(origin);
        return this;
    }

    /**
     * Axes defining this reference frame. (required)
     */
    @JsonProperty(JSON_PROPERTY_AXES)
    public List<Axis> getAxes() {
        return axes;
    }

    @JsonProperty(JSON_PROPERTY_AXES)
    public void setAxes(List<Axis> axes) {
        this.axes = axes;
    }

    public SpatialFrame axes(List<Axis> axes) {
        setAxes(axes);
        return this;
    }

}
