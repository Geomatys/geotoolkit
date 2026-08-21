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
 *     This will delete the system and remove it from all collections it is associated to.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/paths/systemById.yaml"> OGC API - Connected Systems - Part 1 (draft) - Delete a system</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DeleteSystem extends RequestParameters {

    private String systemId;
    private Boolean cascade;

    /**
     * @see #systemId(java.lang.String)
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * @see #systemId(java.lang.String)
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a System
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/systemId.yaml"> OGC API - Connected Systems - Part 1 (draft) - systemId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Non-empty string.</li>
     * </ul>
     */
    public DeleteSystem systemId(String systemId) {
        setSystemId(systemId);
        return this;
    }

    /**
     * @see #cascade(java.lang.Boolean)
     */
    public Boolean getCascade() {
        return cascade;
    }

    /**
     * @see #cascade(java.lang.Boolean)
     */
    public void setCascade(Boolean cascade) {
        this.cascade = cascade;
    }

    /**
     * <blockquote><q>
     *     If set to true, dependent resources are also deleted.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/cascade.yaml"> OGC API - Connected Systems - Part 1 (draft) - cascade parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; default {@code false}.</li>
     *     <li>By default, the server rejects deletion of a System resource that has nested
     *     resources (subsystems, sampling features, datastreams, control streams) or is
     *     associated with a deployment.</li>
     * </ul>
     */
    public DeleteSystem cascade(Boolean cascade) {
        setCascade(cascade);
        return this;
    }

}
