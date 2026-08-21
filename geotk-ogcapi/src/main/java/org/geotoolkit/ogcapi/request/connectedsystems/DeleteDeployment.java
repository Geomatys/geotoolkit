/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogcapi.request.connectedsystems;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     This will delete the deployment and remove it from all collections it is associated to.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/paths/deploymentById.yaml"> OGC API - Connected Systems - Part 1: Feature Resources - Deployment by ID (DELETE)</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DeleteDeployment extends RequestParameters {

    private String deploymentId;

    /**
     * @see #deploymentId(java.lang.String)
     */
    public String getDeploymentId() {
        return deploymentId;
    }

    /**
     * @see #deploymentId(java.lang.String)
     */
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a Deployment
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/deploymentId.yaml"> OGC API - Connected Systems - Part 1 - deploymentId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public DeleteDeployment deploymentId(String deploymentId) {
        setDeploymentId(deploymentId);
        return this;
    }

}
