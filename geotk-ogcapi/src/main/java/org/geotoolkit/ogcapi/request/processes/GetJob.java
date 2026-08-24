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
package org.geotoolkit.ogcapi.request.processes;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Used for {@code GET /jobs/{jobId}}, {@code GET /jobs/{jobId}/results} and
 * {@code DELETE /jobs/{jobId}}.
 *
 * <blockquote><q>
 *     The following section describes the requirements to retrieve information about the
 *     status of a job.
 *     </q><i><small><a href="https://docs.ogc.org/is/18-062r2/18-062r2.html#sc_retrieve_status_info"> OGC API - Processes - Part 1: Core - Retrieve status information about a job</a></small></i>
 * </blockquote>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public final class GetJob extends RequestParameters {

    private String jobId;

    /**
     * @see #jobId(java.lang.String)
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @see #jobId(java.lang.String)
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * <blockquote><q>
     *     local identifier of a job
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/processes/part1/1.0/openapi/parameters/jobId.yaml"> OGC API - Processes - Part 1: Core - jobId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetJob jobId(String jobId) {
        setJobId(jobId);
        return this;
    }
}
