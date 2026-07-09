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
import org.geotoolkit.openeo.dto.service.UpdateServiceBody;

/**
 * Request parameters for PATCH /services/{serviceId}.
 *
 * <p>Updates metadata or configuration of an existing secondary web service.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class UpdateService extends RequestParameters {

    private String serviceId;
    private UpdateServiceBody body;

    /**
     * Unique identifier of the secondary service to update. (required)
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * @see #getServiceId()
     */
    public UpdateService serviceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    /**
     * Fields to update; only provided properties are modified. (optional)
     */
    public UpdateServiceBody getBody() {
        return body;
    }

    /**
     * @see #getBody()
     */
    public UpdateService body(UpdateServiceBody body) {
        this.body = body;
        return this;
    }
}
