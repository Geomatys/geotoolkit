/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.json;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

/**
 * Process
 */
public class Process extends ProcessSummary {

    // literal / complex / boundingBox
    private List<InputType> inputs = null;

    private List<OutputDescription> outputs = null;

    private String executeEndpoint = null;

    public Process() {

    }

    public Process(Process that) {
        super(that);
        this.executeEndpoint = that.executeEndpoint;
        if (that.inputs != null) {
            this.inputs = new ArrayList<>();
            for (InputType in : that.inputs) {
                this.inputs.add(new InputType(in));
            }
        }
        if (that.outputs != null) {
            this.outputs = new ArrayList<>();
            for (OutputDescription out : that.outputs) {
                this.outputs.add(new OutputDescription(out));
            }
        }

    }

    public Process(org.geotoolkit.wps.xml.v200.ProcessOffering desc) {
        super(desc);
        this.executeEndpoint = null; // TODO
        if (desc != null) {
            this.inputs = new ArrayList<>();
            for (org.geotoolkit.wps.xml.v200.InputDescription in : desc.getProcess().getInputs()) {
                this.inputs.add(new InputType(in));
            }
            this.outputs = new ArrayList<>();
            for (org.geotoolkit.wps.xml.v200.OutputDescription out : desc.getProcess().getOutputs()) {
                this.outputs.add(new OutputDescription(out));
            }
        }
    }

    public Process inputs(List<InputType> inputs) {
        this.inputs = inputs;
        return this;
    }

    public Process addInputsItem(InputType inputsItem) {

        if (this.inputs == null) {
            this.inputs = new ArrayList<>();
        }

        this.inputs.add(inputsItem);
        return this;
    }

    public Process addInputs(List<InputType> inputsItem) {

        if (this.inputs == null) {
            this.inputs = new ArrayList<>();
        }

        this.inputs.addAll(inputsItem);
        return this;
    }

    public Process addInputsNoDoublon(List<InputType> inputsItem) {

        if (this.inputs == null) {
            this.inputs = new ArrayList<>();
        }

        for (InputType newInput : inputsItem) {
            if (!containInput(newInput.getId())) {
                this.inputs.add(newInput);
            }
        }
        return this;
    }

    private boolean containInput(String inputId) {
        if (this.inputs != null) {
            for (InputType input : inputs) {
                if (inputId.equals(input.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get inputs
     *
     * @return inputs
  *
     */
    public List<InputType> getInputs() {
        return inputs;
    }

    public void removeInputById(String id) {
        for (InputType input : inputs) {
            if (id.equals(input.getId())) {
                inputs.remove(input);
                return;
            }
        }
    }

    public void setInputs(List<InputType> inputs) {
        this.inputs = inputs;
    }

    public Process outputs(List<OutputDescription> outputs) {
        this.outputs = outputs;
        return this;
    }

    public Process addOutputsItem(OutputDescription outputsItem) {

        if (this.outputs == null) {
            this.outputs = new ArrayList<>();
        }

        this.outputs.add(outputsItem);
        return this;
    }

    /**
     * Get outputs
     *
     * @return outputs
  *
     */
    public List<OutputDescription> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<OutputDescription> outputs) {
        this.outputs = outputs;
    }

    public Process executeEndpoint(String executeEndpoint) {
        this.executeEndpoint = executeEndpoint;
        return this;
    }

    /**
     * Get executeEndpoint
     *
     * @return executeEndpoint
  *
     */
    public String getExecuteEndpoint() {
        return executeEndpoint;
    }

    public void setExecuteEndpoint(String executeEndpoint) {
        this.executeEndpoint = executeEndpoint;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Process process = (Process) o;
        return Objects.equals(this.inputs, process.inputs)
                && Objects.equals(this.outputs, process.outputs)
                && Objects.equals(this.executeEndpoint, process.executeEndpoint)
                && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputs, outputs, executeEndpoint, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Process {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    inputs: ").append(toIndentedString(inputs)).append("\n");
        sb.append("    outputs: ").append(toIndentedString(outputs)).append("\n");
        sb.append("    executeEndpoint: ").append(toIndentedString(executeEndpoint)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
