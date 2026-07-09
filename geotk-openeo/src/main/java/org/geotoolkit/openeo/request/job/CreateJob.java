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

import org.geotoolkit.openeo.dto.process.CreateJobBody;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for POST /jobs.
 *
 * <p>Creates a new batch job from a process graph definition.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class CreateJob extends RequestParameters {

    private CreateJobBody body;

    /**
     * Job definition including the process graph and optional execution settings. (required)
     */
    public CreateJobBody getBody() {
        return body;
    }

    /**
     * @see #getBody()
     */
    public CreateJob body(CreateJobBody body) {
        this.body = body;
        return this;
    }
}
