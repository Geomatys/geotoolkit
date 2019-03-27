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

/**
 * DeployResult
 */
public class DeploymentResult implements WPSJSONResponse {

    private ProcessSummary processSummary = null;

    public DeploymentResult() {

    }

    public DeploymentResult(ProcessSummary processSummary) {
        this.processSummary = processSummary;
    }

    public DeploymentResult(org.geotoolkit.wps.xml.v200.DeployResult result) {
        if (result != null) {
            this.processSummary = new ProcessSummary(result.getProcessSummary());
        }
    }

    public DeploymentResult processSummary(ProcessSummary processSummary) {
        this.processSummary = processSummary;
        return this;
    }

    /**
     * Get processSummary
     *
     * @return processSummary
     *
     */
    public ProcessSummary getProcessSummary() {
        return processSummary;
    }

    public void setProcessSummary(ProcessSummary processSummary) {
        this.processSummary = processSummary;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeploymentResult deployResult = (DeploymentResult) o;
        return Objects.equals(this.processSummary, deployResult.processSummary);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(processSummary);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DeployResult {\n");
        sb.append("    processSummary: ").append(toIndentedString(processSummary)).append("\n");
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
