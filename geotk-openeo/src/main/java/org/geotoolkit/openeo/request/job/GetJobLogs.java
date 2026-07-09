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
 * Request parameters for GET /jobs/{jobId}/logs.
 *
 * <p>Retrieves log entries produced during processing of a batch job.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class GetJobLogs extends RequestParameters {

    private String jobId;
    private String offset;
    private Integer limit;
    private String level;

    /**
     * Unique identifier of the batch job. (required)
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @see #getJobId()
     */
    public GetJobLogs jobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    /**
     * Log entry identifier to start from (exclusive); used for pagination. (optional)
     */
    public String getOffset() {
        return offset;
    }

    /**
     * @see #getOffset()
     */
    public GetJobLogs offset(String offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Maximum number of log entries to return. (optional)
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @see #getLimit()
     */
    public GetJobLogs limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Minimum log level to include: {@code debug}, {@code info}, {@code warning} or {@code error}. (optional)
     */
    public String getLevel() {
        return level;
    }

    /**
     * @see #getLevel()
     */
    public GetJobLogs level(String level) {
        this.level = level;
        return this;
    }
}
