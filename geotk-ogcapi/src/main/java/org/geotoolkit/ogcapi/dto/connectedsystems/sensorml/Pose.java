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
import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * The position and/or orientation of a feature relative to a given reference frame.
 *
 * <p>The standard defines this as a {@code oneOf} of 4 variants: {@code GeoPoseLocation}/{@code
 * GeoPoseLocationOrientation} (geodetic position, optionally with YPR angles or a quaternion,
 * relative to a Local Tangent Plane frame given by {@code ltpReferenceFrame}) and {@code
 * RelativePoseLocation}/{@code RelativePoseLocationOrientation} (cartesian position, optionally
 * with YPR angles or a quaternion, relative to an arbitrary frame given by {@code
 * referenceFrame}). Rather than modeling 4 separate classes, this binding flattens all variants
 * into one class with nullable fields; Jackson simply leaves absent fields {@code null} on
 * (de)serialization. Use {@link #getType()} to know which reference-frame field
 * ({@link #getLtpReferenceFrame()} vs {@link #getReferenceFrame()}) applies.</p>
 *
 * <p>{@link #getPosition()} is exposed as a raw {@link JsonNode} since its shape ({@link
 * PositionGeo} vs {@link PositionXYZ}) is not self-describing: callers should manually map it
 * based on {@link #getType()} (e.g. via {@code ObjectMapper.treeToValue(...)}).</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class Pose extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_LTP_REFERENCE_FRAME = "ltpReferenceFrame";
    private String ltpReferenceFrame;

    public static final String JSON_PROPERTY_REFERENCE_FRAME = "referenceFrame";
    private String referenceFrame;

    public static final String JSON_PROPERTY_POSITION = "position";
    private JsonNode position;

    public static final String JSON_PROPERTY_ANGLES = "angles";
    private AnglesYPR angles;

    public static final String JSON_PROPERTY_QUATERNION = "quaternion";
    private Quaternion quaternion;

    /**
     * Discriminates between the {@code "GeoPose"} and {@code "RelativePose"} variants. (optional)
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    public void setType(String type) {
        this.type = type;
    }

    public Pose type(String type) {
        setType(type);
        return this;
    }

    /**
     * URI of the Local Tangent Plane reference frame. Used only when {@link #getType()} is
     * {@code "GeoPose"}. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LTP_REFERENCE_FRAME)
    public String getLtpReferenceFrame() {
        return ltpReferenceFrame;
    }

    @JsonProperty(JSON_PROPERTY_LTP_REFERENCE_FRAME)
    public void setLtpReferenceFrame(String ltpReferenceFrame) {
        this.ltpReferenceFrame = ltpReferenceFrame;
    }

    public Pose ltpReferenceFrame(String ltpReferenceFrame) {
        setLtpReferenceFrame(ltpReferenceFrame);
        return this;
    }

    /**
     * URI of the reference frame this pose is relative to. Used only when {@link #getType()} is
     * {@code "RelativePose"}. (optional)
     */
    @JsonProperty(JSON_PROPERTY_REFERENCE_FRAME)
    public String getReferenceFrame() {
        return referenceFrame;
    }

    @JsonProperty(JSON_PROPERTY_REFERENCE_FRAME)
    public void setReferenceFrame(String referenceFrame) {
        this.referenceFrame = referenceFrame;
    }

    public Pose referenceFrame(String referenceFrame) {
        setReferenceFrame(referenceFrame);
        return this;
    }

    /**
     * Raw position, either a {@link PositionGeo} or a {@link PositionXYZ} depending on
     * {@link #getType()}; callers must manually convert this node to the appropriate type.
     * (optional)
     */
    @JsonProperty(JSON_PROPERTY_POSITION)
    public JsonNode getPosition() {
        return position;
    }

    @JsonProperty(JSON_PROPERTY_POSITION)
    public void setPosition(JsonNode position) {
        this.position = position;
    }

    public Pose position(JsonNode position) {
        setPosition(position);
        return this;
    }

    /**
     * Orientation as yaw/pitch/roll angles. Used only by the YPR variants. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ANGLES)
    public AnglesYPR getAngles() {
        return angles;
    }

    @JsonProperty(JSON_PROPERTY_ANGLES)
    public void setAngles(AnglesYPR angles) {
        this.angles = angles;
    }

    public Pose angles(AnglesYPR angles) {
        setAngles(angles);
        return this;
    }

    /**
     * Orientation as a quaternion. Used only by the quaternion variants. (optional)
     */
    @JsonProperty(JSON_PROPERTY_QUATERNION)
    public Quaternion getQuaternion() {
        return quaternion;
    }

    @JsonProperty(JSON_PROPERTY_QUATERNION)
    public void setQuaternion(Quaternion quaternion) {
        this.quaternion = quaternion;
    }

    public Pose quaternion(Quaternion quaternion) {
        setQuaternion(quaternion);
        return this;
    }

}
