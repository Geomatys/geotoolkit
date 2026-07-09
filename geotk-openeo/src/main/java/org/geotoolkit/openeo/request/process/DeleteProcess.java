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
package org.geotoolkit.openeo.request.process;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for DELETE /processes/{processId}.
 *
 * <p>Deletes a user-defined process stored on the back-end.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class DeleteProcess extends RequestParameters {

    private String processId;

    /**
     * Unique identifier of the user-defined process to delete. (required)
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * @see #getProcessId()
     */
    public DeleteProcess processId(String processId) {
        this.processId = processId;
        return this;
    }
}
