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
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A set of parameter/configuration values to apply to a process, used by
 * {@link AbstractProcess#getConfiguration()}, {@link Mode#getConfiguration()} and
 * {@link DeployedSystem#getConfiguration()}.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class Settings extends DataTransferObject {

    public static final String JSON_PROPERTY_SET_VALUES = "setValues";
    private List<SetValue> setValues;

    public static final String JSON_PROPERTY_SET_ARRAY_VALUES = "setArrayValues";
    private List<SetArrayValue> setArrayValues;

    public static final String JSON_PROPERTY_SET_MODES = "setModes";
    private List<SetMode> setModes;

    public static final String JSON_PROPERTY_SET_CONSTRAINTS = "setConstraints";
    private List<JsonNode> setConstraints;

    public static final String JSON_PROPERTY_SET_STATUS = "setStatus";
    private List<SetStatus> setStatus;

    /**
     * Scalar value assignments. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SET_VALUES)
    public List<SetValue> getSetValues() {
        return setValues;
    }

    @JsonProperty(JSON_PROPERTY_SET_VALUES)
    public void setSetValues(List<SetValue> setValues) {
        this.setValues = setValues;
    }

    public Settings setValues(List<SetValue> setValues) {
        setSetValues(setValues);
        return this;
    }

    /**
     * Array value assignments. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SET_ARRAY_VALUES)
    public List<SetArrayValue> getSetArrayValues() {
        return setArrayValues;
    }

    @JsonProperty(JSON_PROPERTY_SET_ARRAY_VALUES)
    public void setSetArrayValues(List<SetArrayValue> setArrayValues) {
        this.setArrayValues = setArrayValues;
    }

    public Settings setArrayValues(List<SetArrayValue> setArrayValues) {
        setSetArrayValues(setArrayValues);
        return this;
    }

    /**
     * Mode selections. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SET_MODES)
    public List<SetMode> getSetModes() {
        return setModes;
    }

    @JsonProperty(JSON_PROPERTY_SET_MODES)
    public void setSetModes(List<SetMode> setModes) {
        this.setModes = setModes;
    }

    public Settings setModes(List<SetMode> setModes) {
        setSetModes(setModes);
        return this;
    }

    /**
     * Constraint assignments. Each entry mixes a {@code ref} path reference with an arbitrary
     * SWE Common constraint object; kept raw as {@link JsonNode} since there is no dedicated,
     * statically representable shape for it. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SET_CONSTRAINTS)
    public List<JsonNode> getSetConstraints() {
        return setConstraints;
    }

    @JsonProperty(JSON_PROPERTY_SET_CONSTRAINTS)
    public void setSetConstraints(List<JsonNode> setConstraints) {
        this.setConstraints = setConstraints;
    }

    public Settings setConstraints(List<JsonNode> setConstraints) {
        setSetConstraints(setConstraints);
        return this;
    }

    /**
     * Enabled/disabled status assignments. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SET_STATUS)
    public List<SetStatus> getSetStatus() {
        return setStatus;
    }

    @JsonProperty(JSON_PROPERTY_SET_STATUS)
    public void setSetStatus(List<SetStatus> setStatus) {
        this.setStatus = setStatus;
    }

    public Settings setStatus(List<SetStatus> setStatus) {
        setSetStatus(setStatus);
        return this;
    }

}
