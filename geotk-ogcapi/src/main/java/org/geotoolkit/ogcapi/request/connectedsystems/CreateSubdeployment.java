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

import org.geotoolkit.ogcapi.request.PreferHandling;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code POST} operation on {@code /deployments/{deploymentId}/subdeployments}: creates a new
 * child Deployment resource nested under a Deployment.
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 * @author Johann Sorel (Geomatys)
 */
public final class CreateSubdeployment extends RequestParameters {

    private String deploymentId;
    private byte[] content;
    private String contentType;
    private Boolean dryRun;
    private PreferHandling handling;

    /**
     * Local identifier of the deployment. (required)
     *
     * @return the deploymentId
     */
    public String getDeploymentId() {
        return deploymentId;
    }

    /**
     * @param deploymentId the deploymentId to set
     * @see #getDeploymentId()
     */
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    /**
     * @param deploymentId the deploymentId to set
     * @see #getDeploymentId()
     */
    public CreateSubdeployment deploymentId(String deploymentId) {
        setDeploymentId(deploymentId);
        return this;
    }

    /**
     * The raw resource document to submit (e.g. GeoJSON or SensorML-JSON). (required)
     *
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @param content the content to set
     * @see #getContent()
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * @param content the content to set
     * @see #getContent()
     */
    public CreateSubdeployment content(byte[] content) {
        setContent(content);
        return this;
    }

    /**
     * The media type of the document encoding, sent as the {@code Content-Type} header (e.g. {@code
     * application/geo+json}). (required)
     *
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     * @see #getContentType()
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @param contentType the contentType to set
     * @see #getContentType()
     */
    public CreateSubdeployment contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

    /**
     * If {@code true}, the server validates and otherwise processes the request except that no
     * resource is actually created, returning a {@code 204} response on success. (optional, default to
     * false)
     *
     * @return the dryRun
     */
    public Boolean getDryRun() {
        return dryRun;
    }

    /**
     * @param dryRun the dryRun to set
     * @see #getDryRun()
     */
    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * @param dryRun the dryRun to set
     * @see #getDryRun()
     */
    public CreateSubdeployment dryRun(Boolean dryRun) {
        setDryRun(dryRun);
        return this;
    }

    /**
     * The {@code Prefer} header preference (RFC 7240 section 4.4) for document validation. Leave
     * {@code null} to omit the {@code Prefer} header entirely. (optional)
     *
     * @return the handling
     */
    public PreferHandling getHandling() {
        return handling;
    }

    /**
     * @param handling the handling to set
     * @see #getHandling()
     */
    public void setHandling(PreferHandling handling) {
        this.handling = handling;
    }

    /**
     * @param handling the handling to set
     * @see #getHandling()
     */
    public CreateSubdeployment handling(PreferHandling handling) {
        setHandling(handling);
        return this;
    }

}
