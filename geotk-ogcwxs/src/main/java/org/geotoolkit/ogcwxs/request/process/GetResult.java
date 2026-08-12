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
package org.geotoolkit.ogcwxs.request.process;

import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * To retrieve the result of a job that has completed asynchronous execution. Not available in WPS
 * 1.0.0.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetResult extends RequestParameters {

    private String version;
    private String jobId;

    /**
     * The WPS version to request. Defaults to {@code "2.0.0"} if left {@code null}.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public GetResult version(String version) {
        setVersion(version);
        return this;
    }

    /**
     * Identifier of the job whose result is requested. (required)
     *
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId the jobId to set
     * @see #getJobId()
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @param jobId the jobId to set
     * @see #getJobId()
     */
    public GetResult jobId(String jobId) {
        setJobId(jobId);
        return this;
    }

}
