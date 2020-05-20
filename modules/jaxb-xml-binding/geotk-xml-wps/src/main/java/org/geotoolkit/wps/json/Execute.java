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
import java.util.Map;
import org.geotoolkit.wps.xml.v200.Execute.Response;
import org.geotoolkit.wps.xml.v200.Execute.Mode;

/**
 * Execute
 */
public class Execute implements WPSJSONResponse {

    private List<Input> inputs = null;

    private List<Output> outputs = new ArrayList<>();

    private Mode mode = null;

    private Response response = null;

    public Execute() {

    }

    public Execute(Execute that) {
        if (that != null) {
            if (that.inputs != null) {
                this.inputs = new ArrayList<>();
                for (Input thatIn : that.inputs) {
                    this.inputs.add(new Input(thatIn));
                }
            }
            if (that.outputs != null) {
                this.outputs = new ArrayList<>();
                for (Output thatOut : that.outputs) {
                    this.outputs.add(new Output(thatOut));
                }
            }
            this.mode = that.mode;
            this.response = that.response;
        }

    }

    public Execute(List<Input> inputs, List<Output> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

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

    public List<Input> getInputs(String inputId) {
        List<Input> results = new ArrayList<>();
        if (inputs != null) {
            for (Input in : inputs) {
                if (in.getId().equals(inputId)) {
                    results.add(in);
                }
            }
        }
        return results;
    }


    public Execute outputs(List<Output> outputs) {
        this.outputs = outputs;
        return this;
    }

    public Execute addOutputsItem(Output outputsItem) {

        this.outputs.add(outputsItem);
        return this;
    }

    public Execute subRequest(List<String> stepInputs) {
        Execute result = new Execute(this);
        List<Input> subInputs = new ArrayList<>();
        for (Input in : result.inputs) {
            if (stepInputs.contains(in.getId())) {
                subInputs.add(in);
            }
        }
        result.setInputs(subInputs);
        return result;
    }

    public Execute subRequest(Map<String, String> stepInputs) {
        Execute result = new Execute(this);
        List<Input> subInputs = new ArrayList<>();
        for (Input in : result.inputs) {
            if (stepInputs.containsKey(in.getId())) {
                in.setId(stepInputs.get(in.getId()));
                subInputs.add(in);
            }
        }
        result.setInputs(subInputs);
        return result;
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

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
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
        sb.append("    mode: ").append(toIndentedString(mode)).append("\n");
        sb.append("    response: ").append(toIndentedString(response)).append("\n");
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
