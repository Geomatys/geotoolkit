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
package org.geotoolkit.openeo.request.service;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for GET /services/{serviceId}/logs.
 *
 * <p>Retrieves log entries for a secondary web service.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class GetServiceLogs extends RequestParameters {

    private String serviceId;
    private String offset;
    private Integer limit;
    private String level;

    /**
     * Unique identifier of the secondary service. (required)
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * @see #getServiceId()
     */
    public GetServiceLogs serviceId(String serviceId) {
        this.serviceId = serviceId;
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
    public GetServiceLogs offset(String offset) {
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
    public GetServiceLogs limit(Integer limit) {
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
    public GetServiceLogs level(String level) {
        this.level = level;
        return this;
    }
}
