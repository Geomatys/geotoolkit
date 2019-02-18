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
import org.geotoolkit.wps.xml.v200.DataTransmissionMode;
import org.geotoolkit.wps.xml.v200.JobControlOptions;

/**
 * ProcessSummary
 */
public class ProcessSummary extends DescriptionType {

    private String version = null;

    private List<JobControlOptions> jobControlOptions = null;

    private List<DataTransmissionMode> outputTransmission = null;

    private String processDescriptionURL = null;

    public ProcessSummary() {

    }

    public ProcessSummary(ProcessOffering offering) {
        super(offering.getProcess());
        if (offering != null) {
            this.version = offering.getProcessVersion();
            this.jobControlOptions = offering.getJobControlOptions();
            this.outputTransmission = offering.getOutputTransmission();
        }
    }

    public ProcessSummary(ProcessDescriptionChoiceType offering) {
        super(offering.getProcess());
        if (offering != null) {
            this.version = offering.getProcessVersion();
            this.jobControlOptions = offering.getJobControlOptions();
            this.outputTransmission = offering.getOutputTransmission();
        }
    }

    public ProcessSummary(org.geotoolkit.wps.xml.v200.ProcessSummary process) {
        super(process);
        if (process != null) {
            this.version = process.getProcessVersion();
            this.jobControlOptions = new ArrayList<>(process.getJobControlOptions());
            this.outputTransmission = new ArrayList<>(process.getOutputTransmission());
            this.processDescriptionURL = null; //TODO
        }
    }

    public ProcessSummary version(String version) {
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

    public ProcessSummary jobControlOptions(List<JobControlOptions> jobControlOptions) {
        this.jobControlOptions = jobControlOptions;
        return this;
    }

    public ProcessSummary addJobControlOptionsItem(JobControlOptions jobControlOptionsItem) {

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

    public ProcessSummary processDescriptionURL(String processDescriptionURL) {
        this.processDescriptionURL = processDescriptionURL;
        return this;
    }

    /**
     * Get processDescriptionURL
     *
     * @return processDescriptionURL
  *
     */
    public String getProcessDescriptionURL() {
        return processDescriptionURL;
    }

    public void setProcessDescriptionURL(String processDescriptionURL) {
        this.processDescriptionURL = processDescriptionURL;
    }

    /**
     * @return the outputTransmission
     */
    public List<DataTransmissionMode> getOutputTransmission() {
        return outputTransmission;
    }

    /**
     * @param outputTransmission the outputTransmission to set
     */
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
        ProcessSummary processSummary = (ProcessSummary) o;
        return Objects.equals(this.version, processSummary.version)
                && Objects.equals(this.jobControlOptions, processSummary.jobControlOptions)
                && Objects.equals(this.processDescriptionURL, processSummary.processDescriptionURL)
                && Objects.equals(this.outputTransmission, processSummary.outputTransmission)
                && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, jobControlOptions, processDescriptionURL, outputTransmission, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProcessSummary {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    jobControlOptions: ").append(toIndentedString(jobControlOptions)).append("\n");
        sb.append("    processDescriptionURL: ").append(toIndentedString(processDescriptionURL)).append("\n");
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
