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
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Cartesian (x/y/z) position, one of the two possible shapes of {@link Pose#getPosition()}
 * (used when {@link Pose#getType()} is {@code "RelativePose"}).
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class PositionXYZ extends DataTransferObject {

    public static final String JSON_PROPERTY_X = "x";
    private Double x;

    public static final String JSON_PROPERTY_Y = "y";
    private Double y;

    public static final String JSON_PROPERTY_Z = "z";
    private Double z;

    /**
     * X coordinate. (required)
     */
    @JsonProperty(JSON_PROPERTY_X)
    public Double getX() {
        return x;
    }

    @JsonProperty(JSON_PROPERTY_X)
    public void setX(Double x) {
        this.x = x;
    }

    public PositionXYZ x(Double x) {
        setX(x);
        return this;
    }

    /**
     * Y coordinate. (required)
     */
    @JsonProperty(JSON_PROPERTY_Y)
    public Double getY() {
        return y;
    }

    @JsonProperty(JSON_PROPERTY_Y)
    public void setY(Double y) {
        this.y = y;
    }

    public PositionXYZ y(Double y) {
        setY(y);
        return this;
    }

    /**
     * Z coordinate. (required)
     */
    @JsonProperty(JSON_PROPERTY_Z)
    public Double getZ() {
        return z;
    }

    @JsonProperty(JSON_PROPERTY_Z)
    public void setZ(Double z) {
        this.z = z;
    }

    public PositionXYZ z(Double z) {
        setZ(z);
        return this;
    }

}
