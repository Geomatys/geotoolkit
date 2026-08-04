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
package org.geotoolkit.ogcapi.client;

import org.geotoolkit.client.openapi.OpenApiConfiguration;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractApiTest {

    protected static OpenApiConfiguration newConfig() {
        return OpenApiConfiguration.builder()
                .setScheme("http")
                .setHost("localhost")
                .setPort(8080)
                .setBasePath("ogcapi")
                .build();
    }
}
