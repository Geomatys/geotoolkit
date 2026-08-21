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
 * <blockquote><q>
 *     This will completely replace the existing description of the deployment with the provided content.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/paths/deploymentById.yaml"> OGC API - Connected Systems - Part 1: Feature Resources - Deployment by ID (PUT)</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ReplaceDeployment extends RequestParameters {

    private String deploymentId;
    private byte[] content;
    private String contentType;
    private Boolean dryRun;
    private PreferHandling handling;

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
    public ReplaceDeployment deploymentId(String deploymentId) {
        setDeploymentId(deploymentId);
        return this;
    }

    /**
     * @see #content(byte[])
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @see #content(byte[])
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * <blockquote><q>
     *     The body of the PUT request SHALL contain a representation of the new resource content.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/20-002r1.html#replace"> OGC API - Features - Part 4: Create, Replace, Update and Delete (draft) - Replacing an existing resource (REPLACE)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>The raw resource document that replaces the existing one (e.g. GeoJSON or SensorML-JSON).</li>
     * </ul>
     */
    public ReplaceDeployment content(byte[] content) {
        setContent(content);
        return this;
    }

    /**
     * @see #contentType(java.lang.String)
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @see #contentType(java.lang.String)
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * <blockquote><q>
     *     The Implementation SHALL infer the media type of the request body containing
     *     a representation of the resource to be added from the Content-Type header.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/20-002r1.html#create"> OGC API - Features - Part 4: Create, Replace, Update and Delete (draft) - Adding a new resource to a collection (CREATE)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; a {@code 400} error is returned if not provided.</li>
     *     <li>E.g. {@code application/geo+json} or {@code application/sml+json}.</li>
     * </ul>
     */
    public ReplaceDeployment contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

    /**
     * @see #dryRun(java.lang.Boolean)
     */
    public Boolean getDryRun() {
        return dryRun;
    }

    /**
     * @see #dryRun(java.lang.Boolean)
     */
    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * If {@code true}, the server validates and otherwise processes the request except that no
     * resource is actually replaced, returning a {@code 204} response on success.
     * <br>
     * <ul>
     *     <li>Optional; default {@code false}.</li>
     *     <li>Not defined by OGC API - Connected Systems - Part 1 (draft), which has no
     *     dry-run requirement at the time of writing; this is a server-specific extension
     *     (no verbatim spec text available for this field).</li>
     * </ul>
     */
    public ReplaceDeployment dryRun(Boolean dryRun) {
        setDryRun(dryRun);
        return this;
    }

    /**
     * @see #handling(org.geotoolkit.ogcapi.request.PreferHandling)
     */
    public PreferHandling getHandling() {
        return handling;
    }

    /**
     * @see #handling(org.geotoolkit.ogcapi.request.PreferHandling)
     */
    public void setHandling(PreferHandling handling) {
        this.handling = handling;
    }

    /**
     * The {@code Prefer} header preference (RFC 7240 section 4.4) for document validation.
     * <br>
     * <ul>
     *     <li>Optional; leave {@code null} to omit the {@code Prefer} header entirely.</li>
     *     <li>Not defined by OGC API - Connected Systems - Part 1 (draft), which does not
     *     mention the {@code Prefer} header at the time of writing; this is a server-specific
     *     extension (no verbatim spec text available for this field).</li>
     * </ul>
     */
    public ReplaceDeployment handling(PreferHandling handling) {
        setHandling(handling);
        return this;
    }

}
