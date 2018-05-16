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
 * Execute
 */
public class Execute {

    private List<Input> inputs = null;

    private List<Output> outputs = new ArrayList<>();

    public Execute inputs(List<Input> inputs) {
        this.inputs = inputs;
        return this;
    }

    public Execute addInputsItem(Input inputsItem) {

        if (this.inputs == null) {
            this.inputs = new ArrayList<>();
        }

        this.inputs.add(inputsItem);
        return this;
    }

    /**
     * Get inputs
     *
     * @return inputs
  *
     */
    public List<Input> getInputs() {
        return inputs;
    }

    public void setInputs(List<Input> inputs) {
        this.inputs = inputs;
    }

    public Execute outputs(List<Output> outputs) {
        this.outputs = outputs;
        return this;
    }

    public Execute addOutputsItem(Output outputsItem) {

        this.outputs.add(outputsItem);
        return this;
    }

    /**
     * Get outputs
     *
     * @return outputs
  *
     */
    public List<Output> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Output> outputs) {
        this.outputs = outputs;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Execute execute = (Execute) o;
        return Objects.equals(this.inputs, execute.inputs)
                && Objects.equals(this.outputs, execute.outputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputs, outputs);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Execute {\n");

        sb.append("    inputs: ").append(toIndentedString(inputs)).append("\n");
        sb.append("    outputs: ").append(toIndentedString(outputs)).append("\n");
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
