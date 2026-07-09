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
 * Request parameters for PUT /files/{path}.
 *
 * <p>Uploads or replaces a file in the authenticated user's workspace.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class UploadFile extends RequestParameters {

    private String path;
    private byte[] content;

    /**
     * Destination path within the user's workspace. (required)
     */
    public String getPath() {
        return path;
    }

    /**
     * @see #getPath()
     */
    public UploadFile path(String path) {
        this.path = path;
        return this;
    }

    /**
     * Raw file content to upload. (required)
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @see #getContent()
     */
    public UploadFile content(byte[] content) {
        this.content = content;
        return this;
    }
}
