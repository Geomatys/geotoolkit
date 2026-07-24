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
import java.util.List;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;

/**
 * Base of every physically instantiated process ({@link PhysicalComponent}, {@link
 * PhysicalSystem}), i.e. one that is deployed and has a physical presence.
 *
 * <p>Abstract: not itself listed in {@link DescribedObject}'s {@code @JsonSubTypes} — only its
 * concrete leaves are.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public abstract class AbstractPhysicalProcess extends AbstractProcess {

    public static final String JSON_PROPERTY_ATTACHED_TO = "attachedTo";
    private Link attachedTo;

    public static final String JSON_PROPERTY_LOCAL_REFERENCE_FRAMES = "localReferenceFrames";
    private List<SpatialFrame> localReferenceFrames;

    public static final String JSON_PROPERTY_LOCAL_TIME_FRAMES = "localTimeFrames";
    private List<TemporalFrame> localTimeFrames;

    public static final String JSON_PROPERTY_POSITION = "position";
    private JsonNode position;

    /**
     * Link to the physical system or platform this process is attached to. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ATTACHED_TO)
    public Link getAttachedTo() {
        return attachedTo;
    }

    @JsonProperty(JSON_PROPERTY_ATTACHED_TO)
    public void setAttachedTo(Link attachedTo) {
        this.attachedTo = attachedTo;
    }

    public AbstractPhysicalProcess attachedTo(Link attachedTo) {
        setAttachedTo(attachedTo);
        return this;
    }

    /**
     * Local spatial reference frames defined by this process. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LOCAL_REFERENCE_FRAMES)
    public List<SpatialFrame> getLocalReferenceFrames() {
        return localReferenceFrames;
    }

    @JsonProperty(JSON_PROPERTY_LOCAL_REFERENCE_FRAMES)
    public void setLocalReferenceFrames(List<SpatialFrame> localReferenceFrames) {
        this.localReferenceFrames = localReferenceFrames;
    }

    public AbstractPhysicalProcess localReferenceFrames(List<SpatialFrame> localReferenceFrames) {
        setLocalReferenceFrames(localReferenceFrames);
        return this;
    }

    /**
     * Local temporal reference frames defined by this process. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LOCAL_TIME_FRAMES)
    public List<TemporalFrame> getLocalTimeFrames() {
        return localTimeFrames;
    }

    @JsonProperty(JSON_PROPERTY_LOCAL_TIME_FRAMES)
    public void setLocalTimeFrames(List<TemporalFrame> localTimeFrames) {
        this.localTimeFrames = localTimeFrames;
    }

    public AbstractPhysicalProcess localTimeFrames(List<TemporalFrame> localTimeFrames) {
        setLocalTimeFrames(localTimeFrames);
        return this;
    }

    /**
     * Position of this process. Simplification: the standard's position union has 8 variants
     * with no shared discriminator — a GeoJSON Point, a {@link Pose}, a nested {@link
     * AbstractProcess} (e.g. a positioning sensor), free {@code Text}, a by-reference {@link
     * Link}, or a SWE Common {@code Vector}/{@code DataRecord}/{@code DataArray} — so this field
     * is kept raw and should be manually mapped by callers based on its shape. (optional)
     *
     * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
     */
    @JsonProperty(JSON_PROPERTY_POSITION)
    public JsonNode getPosition() {
        return position;
    }

    @JsonProperty(JSON_PROPERTY_POSITION)
    public void setPosition(JsonNode position) {
        this.position = position;
    }

    public AbstractPhysicalProcess position(JsonNode position) {
        setPosition(position);
        return this;
    }

}
