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
package org.geotoolkit.openeo.request.file;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for DELETE /files/{path}.
 *
 * <p>Deletes a file from the authenticated user's workspace.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class DeleteFile extends RequestParameters {

    private String path;

    /**
     * Path of the file to delete within the user's workspace. (required)
     */
    public String getPath() {
        return path;
    }

    /**
     * @see #getPath()
     */
    public DeleteFile path(String path) {
        this.path = path;
        return this;
    }
}
