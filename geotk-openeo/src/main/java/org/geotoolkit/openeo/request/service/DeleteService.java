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
 * Request parameters for DELETE /services/{serviceId}.
 *
 * <p>Deletes a secondary web service and stops it if it is still running.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class DeleteService extends RequestParameters {

    private String serviceId;

    /**
     * Unique identifier of the secondary service to delete. (required)
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * @see #getServiceId()
     */
    public DeleteService serviceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }
}
