/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlType(name = "", propOrder = {
    "identifier"
})
@XmlRootElement(name = "Deploy")
public class Deploy extends RequestBase {

    private Boolean immediateDeployment = true;

    private ProcessDescriptionChoiceType processDescription = null;

    private List<ExecutionUnit> executionUnit = null;

    private String deploymentProfileName = null;

    public Deploy() {}

    public Deploy(String service, String version, String language, ProcessDescriptionChoiceType processDescription, List<ExecutionUnit> executionUnit, String deploymentProfileName) {
        super(service, version, language);
        this.processDescription = processDescription;
        this.deploymentProfileName = deploymentProfileName;
        this.executionUnit = executionUnit;
    }

    public Deploy(String service, String version, String language, org.geotoolkit.wps.json.Deploy json) {
        super(service, version, language);
        if (json != null) {
            this.processDescription = new ProcessDescriptionChoiceType(json.getProcessDescription());
            this.deploymentProfileName = json.getDeploymentProfileName();
            if (json.getExecutionUnit() != null) {
                this.executionUnit = new ArrayList<>();
                for (org.geotoolkit.wps.json.ExecutionUnit unit : json.getExecutionUnit()) {
                    this.executionUnit.add(new ExecutionUnit(unit));
                }
            }
        }
    }

    /**
     * @return the immediateDeployment
     */
    public Boolean getImmediateDeployment() {
        return immediateDeployment;
    }

    /**
     * @param immediateDeployment the immediateDeployment to set
     */
    public void setImmediateDeployment(Boolean immediateDeployment) {
        this.immediateDeployment = immediateDeployment;
    }

    /**
     * @return the processDescription
     */
    public ProcessDescriptionChoiceType getProcessDescription() {
        return processDescription;
    }

    /**
     * @param processDescription the processDescription to set
     */
    public void setProcessDescription(ProcessDescriptionChoiceType processDescription) {
        this.processDescription = processDescription;
    }

    /**
     * @return the executionUnit
     */
    public List<ExecutionUnit> getExecutionUnit() {
        return executionUnit;
    }

    /**
     * @param executionUnit the executionUnit to set
     */
    public void setExecutionUnit(List<ExecutionUnit> executionUnit) {
        this.executionUnit = executionUnit;
    }

    /**
     * @return the deploymentProfileName
     */
    public String getDeploymentProfileName() {
        return deploymentProfileName;
    }

    /**
     * @param deploymentProfileName the deploymentProfileName to set
     */
    public void setDeploymentProfileName(String deploymentProfileName) {
        this.deploymentProfileName = deploymentProfileName;
    }


}
