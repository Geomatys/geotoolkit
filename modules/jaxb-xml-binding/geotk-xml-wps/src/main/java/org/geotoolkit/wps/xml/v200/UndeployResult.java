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

import org.geotoolkit.wps.xml.WPSResponse;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class UndeployResult implements WPSResponse {

    private String deploymentDone;

    private String id;

    public UndeployResult() {

    }

    public UndeployResult(String id, String deploymentDone) {
        this.id = id;
        this.deploymentDone = deploymentDone;
    }

    /**
     * @return the deploymentDone
     */
    public String getDeploymentDone() {
        return deploymentDone;
    }

    /**
     * @param deploymentDone the deploymentDone to set
     */
    public void setDeploymentDone(String deploymentDone) {
        this.deploymentDone = deploymentDone;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

}
