/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.storage;

import org.apache.sis.storage.AbstractResource;
import org.geotoolkit.client.openapi.OpenApiConfiguration;

/**
 * An OGCAPI resource of undefined type.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class UndefinedResource extends AbstractResource {

    public UndefinedResource(OpenApiConfiguration config) {
        super(null);
    }

}
