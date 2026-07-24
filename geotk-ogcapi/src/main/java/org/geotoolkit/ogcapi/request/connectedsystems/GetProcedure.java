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
package org.geotoolkit.ogcapi.request.connectedsystems;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code GET} operation on {@code /procedures/{procedureId}}: retrieves a single Procedure
 * resource.
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 * @author Johann Sorel (Geomatys)
 */
public final class GetProcedure extends RequestParameters {

    private String procedureId;

    /**
     * Local identifier of the procedure. (required)
     *
     * @return the procedureId
     */
    public String getProcedureId() {
        return procedureId;
    }

    /**
     * @param procedureId the procedureId to set
     * @see #getProcedureId()
     */
    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    /**
     * @param procedureId the procedureId to set
     * @see #getProcedureId()
     */
    public GetProcedure procedureId(String procedureId) {
        setProcedureId(procedureId);
        return this;
    }

}
