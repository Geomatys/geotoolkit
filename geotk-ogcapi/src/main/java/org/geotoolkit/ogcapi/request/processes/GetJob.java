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
 * @author Quentin BIALOTA (Geomatys)
 */
public final class GetJob extends RequestParameters {

    private String jobId;

    /**
     * Identifier of the job (required)
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @see #getJobId()
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @see #getJobId()
     */
    public GetJob jobId(String jobId) {
        setJobId(jobId);
        return this;
    }
}
