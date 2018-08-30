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

/**
 * Deploy
 */
public class Deploy {

    private Boolean immediateDeployment = true;

    private ProcessDescriptionChoiceType processDescription = null;

    private List<ExecutionUnit> executionUnit = null;

    private String deploymentProfileName = null;

    public Deploy() {

    }

    public Deploy(ProcessDescriptionChoiceType processDescription, List<ExecutionUnit> executionUnit, String deploymentProfileName, Boolean immediateDeployment) {
        this.deploymentProfileName = deploymentProfileName;
        this.processDescription = processDescription;
        this.executionUnit = executionUnit;
        this.immediateDeployment = immediateDeployment;
    }

    /**
     * Get immediateDeployment
     *
     * @return immediateDeployment
    *
     */
    public Boolean isImmediateDeployment() {
        return immediateDeployment;
    }

    public void setImmediateDeployment(Boolean immediateDeployment) {
        this.immediateDeployment = immediateDeployment;
    }

    public Deploy processDescription(ProcessDescriptionChoiceType processDescription) {
        this.processDescription = processDescription;
        return this;
    }

    /**
     * Get processDescription
     *
     * @return processDescription
  *
     */
    public ProcessDescriptionChoiceType getProcessDescription() {
        return processDescription;
    }

    public void setProcessDescription(ProcessDescriptionChoiceType processDescription) {
        this.processDescription = processDescription;
    }

    public Deploy executionUnit(List<ExecutionUnit> executionUnit) {
        this.executionUnit = executionUnit;
        return this;
    }

    public Deploy addExecutionUnitItem(ExecutionUnit executionUnitItem) {

        if (this.executionUnit == null) {
            this.executionUnit = new ArrayList<>();
        }

        this.executionUnit.add(executionUnitItem);
        return this;
    }

    /**
     * Get executionUnit
     *
     * @return executionUnit
  *
     */
    public List<ExecutionUnit> getExecutionUnit() {
        return executionUnit;
    }

    public void setExecutionUnit(List<ExecutionUnit> executionUnit) {
        this.executionUnit = executionUnit;
    }

    public Deploy deploymentProfileName(String deploymentProfileName) {
        this.deploymentProfileName = deploymentProfileName;
        return this;
    }

    /**
     * Get deploymentProfileName
     *
     * @return deploymentProfileName
  *
     */
    public String getDeploymentProfileName() {
        return deploymentProfileName;
    }

    public void setDeploymentProfileName(String deploymentProfileName) {
        this.deploymentProfileName = deploymentProfileName;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Deploy deploy = (Deploy) o;
        return Objects.equals(this.immediateDeployment, deploy.immediateDeployment)
                && Objects.equals(this.processDescription, deploy.processDescription)
                && Objects.equals(this.executionUnit, deploy.executionUnit)
                && Objects.equals(this.deploymentProfileName, deploy.deploymentProfileName);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(immediateDeployment, processDescription, executionUnit, deploymentProfileName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Deploy {\n");

        sb.append("    immediateDeployment: ").append(toIndentedString(immediateDeployment)).append("\n");
        sb.append("    processDescription: ").append(toIndentedString(processDescription)).append("\n");
        sb.append("    executionUnit: ").append(toIndentedString(executionUnit)).append("\n");
        sb.append("    deploymentProfileName: ").append(toIndentedString(deploymentProfileName)).append("\n");
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
