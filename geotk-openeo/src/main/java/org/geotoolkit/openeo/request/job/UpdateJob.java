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

import org.geotoolkit.openeo.dto.process.UpdateJobBody;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for PATCH /jobs/{jobId}.
 *
 * <p>Modifies metadata or the process graph of a queued or created batch job.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class UpdateJob extends RequestParameters {

    private String jobId;
    private UpdateJobBody body;

    /**
     * Unique identifier of the batch job to update. (required)
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @see #getJobId()
     */
    public UpdateJob jobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    /**
     * Fields to update; only provided properties are modified. (optional)
     */
    public UpdateJobBody getBody() {
        return body;
    }

    /**
     * @see #getBody()
     */
    public UpdateJob body(UpdateJobBody body) {
        this.body = body;
        return this;
    }
}
