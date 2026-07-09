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
package org.geotoolkit.openeo.request.job;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for GET /jobs/{jobId}/estimate.
 *
 * <p>Retrieves cost and resource estimates for a batch job before queuing it for processing.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class GetJobEstimate extends RequestParameters {

    private String jobId;

    /**
     * Unique identifier of the batch job. (required)
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @see #getJobId()
     */
    public GetJobEstimate jobId(String jobId) {
        this.jobId = jobId;
        return this;
    }
}
