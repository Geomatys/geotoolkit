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
 * Request parameters for DELETE /jobs/{jobId}.
 *
 * <p>Deletes a batch job and all its associated resources.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class DeleteJob extends RequestParameters {

    private String jobId;

    /**
     * Unique identifier of the batch job to delete. (required)
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @see #getJobId()
     */
    public DeleteJob jobId(String jobId) {
        this.jobId = jobId;
        return this;
    }
}
