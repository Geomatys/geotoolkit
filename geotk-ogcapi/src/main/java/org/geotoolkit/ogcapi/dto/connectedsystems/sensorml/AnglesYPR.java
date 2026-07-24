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
 * Yaw/pitch/roll orientation angles, used by the YPR variants of {@link Pose#getAngles()}.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class AnglesYPR extends DataTransferObject {

    public static final String JSON_PROPERTY_YAW = "yaw";
    private Double yaw;

    public static final String JSON_PROPERTY_PITCH = "pitch";
    private Double pitch;

    public static final String JSON_PROPERTY_ROLL = "roll";
    private Double roll;

    /**
     * Yaw angle. (required)
     */
    @JsonProperty(JSON_PROPERTY_YAW)
    public Double getYaw() {
        return yaw;
    }

    @JsonProperty(JSON_PROPERTY_YAW)
    public void setYaw(Double yaw) {
        this.yaw = yaw;
    }

    public AnglesYPR yaw(Double yaw) {
        setYaw(yaw);
        return this;
    }

    /**
     * Pitch angle. (required)
     */
    @JsonProperty(JSON_PROPERTY_PITCH)
    public Double getPitch() {
        return pitch;
    }

    @JsonProperty(JSON_PROPERTY_PITCH)
    public void setPitch(Double pitch) {
        this.pitch = pitch;
    }

    public AnglesYPR pitch(Double pitch) {
        setPitch(pitch);
        return this;
    }

    /**
     * Roll angle. (required)
     */
    @JsonProperty(JSON_PROPERTY_ROLL)
    public Double getRoll() {
        return roll;
    }

    @JsonProperty(JSON_PROPERTY_ROLL)
    public void setRoll(Double roll) {
        this.roll = roll;
    }

    public AnglesYPR roll(Double roll) {
        setRoll(roll);
        return this;
    }

}
