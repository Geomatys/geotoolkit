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
package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ProcessDescriptionChoiceType {

    private Reference reference;

    private ProcessDescription process;

    private String processVersion = null;

    private List<JobControlOptions> jobControlOptions = null;

    private List<DataTransmissionMode> outputTransmission = null;

    public ProcessDescriptionChoiceType() {

    }
    public ProcessDescriptionChoiceType(org.geotoolkit.wps.json.ProcessDescriptionChoiceType processDescription) {
        if (processDescription != null) {
            if (processDescription.getProcess() != null) {
                this.process = new ProcessDescription(processDescription.getProcess());
            }
            this.reference = new Reference(processDescription.getHref(),
                                           processDescription.getMimeType(),
                                           processDescription.getEncoding(),
                                           processDescription.getSchema());
            this.processVersion = processDescription.getProcessVersion();
            if (processDescription.getJobControlOptions() != null && !processDescription.getJobControlOptions().isEmpty()) {
                this.jobControlOptions = new ArrayList<>(processDescription.getJobControlOptions());
            }
            if (processDescription.getOutputTransmission()!= null && !processDescription.getOutputTransmission().isEmpty()) {
                this.outputTransmission = new ArrayList<>(processDescription.getOutputTransmission());
            }
        }
    }

    /**
     * @return the reference
     */
    public Reference getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    /**
     * @return the processOffering
     */
    public ProcessDescription getProcess() {
        return process;
    }

    /**
     * @param processOffering the processOffering to set
     */
    public void setProcess(ProcessDescription processOffering) {
        this.process = processOffering;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProcessDescriptionChoiceType process = (ProcessDescriptionChoiceType) o;
        return Objects.equals(this.process, process.process)
            && Objects.equals(this.reference, process.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, process);
    }

    /**
     * @return the processVersion
     */
    public String getProcessVersion() {
        return processVersion;
    }

    /**
     * @param processVersion the processVersion to set
     */
    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    /**
     * @return the jobControlOptions
     */
    public List<JobControlOptions> getJobControlOptions() {
        return jobControlOptions;
    }

    /**
     * @param jobControlOptions the jobControlOptions to set
     */
    public void setJobControlOptions(List<JobControlOptions> jobControlOptions) {
        this.jobControlOptions = jobControlOptions;
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
}
