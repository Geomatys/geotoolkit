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
 * <blockquote><q>
 *     Every `Procedure` resource exposed by the server SHALL be accessible through its
 *     canonical URL of the form `{api_root}/procedures/{id}` where `id` is the local
 *     identifier of the `Procedure` resource.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/standard/requirements/procedure/req_canonical_url.adoc"> OGC API - Connected Systems - Part 1 - Requirement /req/procedure/canonical-url</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetProcedure extends RequestParameters {

    private String procedureId;

    /**
     * @see #procedureId(java.lang.String)
     */
    public String getProcedureId() {
        return procedureId;
    }

    /**
     * @see #procedureId(java.lang.String)
     */
    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a Procedure
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/procedureId.yaml"> OGC API - Connected Systems - Part 1 - procedureId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetProcedure procedureId(String procedureId) {
        setProcedureId(procedureId);
        return this;
    }

}
