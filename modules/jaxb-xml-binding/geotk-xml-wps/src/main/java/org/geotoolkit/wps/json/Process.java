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
import org.geotoolkit.wps.xml.v200.BoundingBoxData;
import org.geotoolkit.wps.xml.v200.ComplexData;
import org.geotoolkit.wps.xml.v200.JobControlOptions;
import org.geotoolkit.wps.xml.v200.LiteralData;
import org.geotoolkit.wps.xml.v200.ProcessDescription;

/**
 * Process
 */
public class Process extends DescriptionType {

    // literal / complex / boundingBox
    private List<Object> inputs = null;

    private List<OutputDescription> outputs = null;

    private String version = null;

    private List<JobControlOptions> jobControlOptions = null;

    private List<TransmissionMode> outputTransmission = null;

    private String executeEndpoint = null;

    public Process() {

    }

    public Process(org.geotoolkit.wps.xml.v200.ProcessOffering offering) {
        super(offering.getProcess());
        this.executeEndpoint = null; // TODO
        this.outputTransmission = null; // TODO

        this.version = offering.getProcessVersion();
        this.jobControlOptions = new ArrayList<>(offering.getJobControlOptions());

        final ProcessDescription desc = offering.getProcess();
        if (desc != null) {
            this.inputs = new ArrayList<>();
            for (org.geotoolkit.wps.xml.v200.InputDescription in : desc.getInputs()) {
                if (in.getDataDescription() instanceof LiteralData) {
                    this.inputs.add(new LiteralInputType(in));
                } else if (in.getDataDescription() instanceof ComplexData) {
                    this.inputs.add(new ComplexInputType(in));
                } else if (in.getDataDescription() instanceof BoundingBoxData) {
                    this.inputs.add(new BoundingBoxInputType(in));
                }
            }
            this.outputs = new ArrayList<>();
            for (org.geotoolkit.wps.xml.v200.OutputDescription out : desc.getOutputs()) {
                this.outputs.add(new OutputDescription(out));
            }
        }
    }

    public Process inputs(List<Object> inputs) {
        this.inputs = inputs;
        return this;
    }

    public Process addInputsItem(Object inputsItem) {

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
    public List<Object> getInputs() {
        return inputs;
    }

    public void setInputs(List<Object> inputs) {
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

    public Process version(String version) {
        this.version = version;
        return this;
    }

    /**
     * Get version
     *
     * @return version
  *
     */
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Process jobControlOptions(List<JobControlOptions> jobControlOptions) {
        this.jobControlOptions = jobControlOptions;
        return this;
    }

    public Process addJobControlOptionsItem(JobControlOptions jobControlOptionsItem) {

        if (this.jobControlOptions == null) {
            this.jobControlOptions = new ArrayList<>();
        }

        this.jobControlOptions.add(jobControlOptionsItem);
        return this;
    }

    /**
     * Get jobControlOptions
     *
     * @return jobControlOptions
  *
     */
    public List<JobControlOptions> getJobControlOptions() {
        return jobControlOptions;
    }

    public void setJobControlOptions(List<JobControlOptions> jobControlOptions) {
        this.jobControlOptions = jobControlOptions;
    }

    public Process outputTransmission(List<TransmissionMode> outputTransmission) {
        this.outputTransmission = outputTransmission;
        return this;
    }

    public Process addOutputTransmissionItem(TransmissionMode outputTransmissionItem) {

        if (this.outputTransmission == null) {
            this.outputTransmission = new ArrayList<>();
        }

        this.outputTransmission.add(outputTransmissionItem);
        return this;
    }

    /**
     * Get outputTransmission
     *
     * @return outputTransmission
  *
     */
    public List<TransmissionMode> getOutputTransmission() {
        return outputTransmission;
    }

    public void setOutputTransmission(List<TransmissionMode> outputTransmission) {
        this.outputTransmission = outputTransmission;
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
                && Objects.equals(this.version, process.version)
                && Objects.equals(this.jobControlOptions, process.jobControlOptions)
                && Objects.equals(this.outputTransmission, process.outputTransmission)
                && Objects.equals(this.executeEndpoint, process.executeEndpoint)
                && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputs, outputs, version, jobControlOptions, outputTransmission, executeEndpoint, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Process {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    inputs: ").append(toIndentedString(inputs)).append("\n");
        sb.append("    outputs: ").append(toIndentedString(outputs)).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    jobControlOptions: ").append(toIndentedString(jobControlOptions)).append("\n");
        sb.append("    outputTransmission: ").append(toIndentedString(outputTransmission)).append("\n");
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
