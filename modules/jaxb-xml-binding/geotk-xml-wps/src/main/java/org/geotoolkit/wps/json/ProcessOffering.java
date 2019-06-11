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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.wps.xml.v200.DataTransmissionMode;
import org.geotoolkit.wps.xml.v200.JobControlOptions;

/**
 * ProcessOffering
 */
public class ProcessOffering implements WPSJSONResponse {

    private Process process = null;

    private String processVersion = null;

    private List<JobControlOptions> jobControlOptions = null;

    private List<DataTransmissionMode> outputTransmission = null;

    public ProcessOffering() {

    }

    public ProcessOffering(ProcessOffering that) {
        if (that != null) {
            if (that.process != null) {
                this.process = new Process(that.process);
            }
            this.processVersion = that.processVersion;
            if (that.jobControlOptions != null && !that.jobControlOptions.isEmpty()) {
                this.jobControlOptions = new ArrayList<>(that.jobControlOptions);
            }
            if (that.outputTransmission != null && !that.outputTransmission.isEmpty()) {
                this.outputTransmission = new ArrayList<>(that.outputTransmission);
            }
        }
    }

    public ProcessOffering(ProcessDescriptionChoiceType that) {
        if (that != null) {
            if (that.getProcess() != null) {
                this.process = new Process(that.getProcess());
            }
            this.processVersion = that.getProcessVersion();
            if (that.getJobControlOptions() != null && !that.getJobControlOptions().isEmpty()) {
                this.jobControlOptions = new ArrayList<>(that.getJobControlOptions());
            }
            if (that.getOutputTransmission() != null && !that.getOutputTransmission().isEmpty()) {
                this.outputTransmission = new ArrayList<>(that.getOutputTransmission());
            }
        }
    }

    public ProcessOffering(org.geotoolkit.wps.xml.v200.ProcessOffering offering) {
        if (offering != null) {
            this.process = new Process(offering.getProcess());
            if (offering.getOutputTransmission() != null && !offering.getOutputTransmission().isEmpty()) {
                this.outputTransmission =new ArrayList<>(offering.getOutputTransmission());
            }
            this.processVersion = offering.getProcessVersion();
            if (offering.getJobControlOptions()!= null && !offering.getJobControlOptions().isEmpty()) {
                this.jobControlOptions = new ArrayList<>(offering.getJobControlOptions());
            }
        }
    }

    public ProcessOffering process(Process process) {
        this.process = process;
        return this;
    }

    /**
     * Get process
     *
     * @return process
  *
     */
    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    /**
     * Get version
     *
     * @return version
  *
     */
    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String version) {
        this.processVersion = version;
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

    /**
     * Get outputTransmission
     *
     * @return outputTransmission
  *
     */
    public List<DataTransmissionMode> getOutputTransmission() {
        return outputTransmission;
    }

    public void setOutputTransmission(List<DataTransmissionMode> outputTransmission) {
        this.outputTransmission = outputTransmission;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProcessOffering processOffering = (ProcessOffering) o;
        return Objects.equals(this.process, processOffering.process)
            && Objects.equals(this.processVersion, processOffering.processVersion)
            && Objects.equals(this.jobControlOptions, processOffering.jobControlOptions)
            && Objects.equals(this.outputTransmission, processOffering.outputTransmission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(process, processVersion, jobControlOptions, outputTransmission);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProcessOffering {\n");

        sb.append("    process: ").append(toIndentedString(process)).append("\n");
        sb.append("    processVersion: ").append(toIndentedString(processVersion)).append("\n");
        sb.append("    jobControlOptions: ").append(toIndentedString(jobControlOptions)).append("\n");
        sb.append("    outputTransmission: ").append(toIndentedString(outputTransmission)).append("\n");
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
