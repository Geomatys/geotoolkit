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
package org.geotoolkit.ogcapi.dto.processes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * Full description of a process : a {@link ProcessSummary} plus its inputs
 * and outputs, as returned by {@code GET /processes/{processID}}.
 */
public final class ProcessDescription extends ProcessSummary {

    public static final String JSON_PROPERTY_INPUTS = "inputs";
    private Map<String, InputDescription> inputs;

    public static final String JSON_PROPERTY_OUTPUTS = "outputs";
    private Map<String, OutputDescription> outputs;

    public ProcessDescription() {
    }

    public ProcessDescription inputs(Map<String, InputDescription> inputs) {
        this.inputs = inputs;
        return this;
    }

    public ProcessDescription putInputsItem(String key, InputDescription inputsItem) {
        if (this.inputs == null) {
            this.inputs = new HashMap<>();
        }
        this.inputs.put(key, inputsItem);
        return this;
    }

    @JsonProperty(JSON_PROPERTY_INPUTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, InputDescription> getInputs() {
        return inputs;
    }

    @JsonProperty(JSON_PROPERTY_INPUTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setInputs(Map<String, InputDescription> inputs) {
        this.inputs = inputs;
    }

    public ProcessDescription outputs(Map<String, OutputDescription> outputs) {
        this.outputs = outputs;
        return this;
    }

    public ProcessDescription putOutputsItem(String key, OutputDescription outputsItem) {
        if (this.outputs == null) {
            this.outputs = new HashMap<>();
        }
        this.outputs.put(key, outputsItem);
        return this;
    }

    @JsonProperty(JSON_PROPERTY_OUTPUTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, OutputDescription> getOutputs() {
        return outputs;
    }

    @JsonProperty(JSON_PROPERTY_OUTPUTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setOutputs(Map<String, OutputDescription> outputs) {
        this.outputs = outputs;
    }
}
