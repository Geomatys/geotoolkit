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
package org.geotoolkit.openeo.client;

import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;

/**
 * OpenEO API client facade.
 * Entry point providing access to all API groups:
 * <ul>
 *   <li>{@link CapabilitiesApi} — capabilities, conformance, credentials, user info</li>
 *   <li>{@link ProcessingApi} — process graphs and batch jobs</li>
 *   <li>{@link CollectionsApi} — EO data collections (STAC)</li>
 *   <li>{@link ServicesApi} — secondary web services (WMS, OGC API, etc.)</li>
 *   <li>{@link FilesApi} — user workspace file storage</li>
 * </ul>
 *
 * <p>Each sub-API can also be instantiated independently if needed.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class OpenEOApi extends AbstractOpenApi {

    private final CapabilitiesApi capabilities;
    private final ProcessingApi processing;
    private final CollectionsApi collections;
    private final ServicesApi services;
    private final FilesApi files;

    public OpenEOApi(OpenApiConfiguration config) {
        super(config);
        this.capabilities = new CapabilitiesApi(config);
        this.processing   = new ProcessingApi(config);
        this.collections  = new CollectionsApi(config);
        this.services     = new ServicesApi(config);
        this.files        = new FilesApi(config);
    }

    /**
     * @return capabilities, conformance, credentials and user info API
     */
    public CapabilitiesApi capabilities() {
        return capabilities;
    }

    /**
     * @return process graph and batch job API
     */
    public ProcessingApi processing() {
        return processing;
    }

    /**
     * @return EO data collections (STAC) API
     */
    public CollectionsApi collections() {
        return collections;
    }

    /**
     * @return secondary web services (WMS, OGC API, etc.) API
     */
    public ServicesApi services() {
        return services;
    }

    /**
     * @return user workspace file storage API
     */
    public FilesApi files() {
        return files;
    }
}
