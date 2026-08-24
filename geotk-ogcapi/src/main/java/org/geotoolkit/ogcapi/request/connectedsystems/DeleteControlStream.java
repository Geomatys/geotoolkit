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
 *     This will delete the control stream and remove it from all collections it is associated to.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/controlStreamById.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - Delete a control stream</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DeleteControlStream extends RequestParameters {

    private String controlStreamId;
    private Boolean cascade;

    /**
     * @see #controlStreamId(java.lang.String)
     */
    public String getControlStreamId() {
        return controlStreamId;
    }

    /**
     * @see #controlStreamId(java.lang.String)
     */
    public void setControlStreamId(String controlStreamId) {
        this.controlStreamId = controlStreamId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a Control Stream
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/controlStreamId.yaml"> OGC API - Connected Systems - Part 2 (draft) - controlStreamId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Non-empty string.</li>
     * </ul>
     */
    public DeleteControlStream controlStreamId(String controlStreamId) {
        setControlStreamId(controlStreamId);
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
     *     <li>If used, all commands and command status associated with the control stream are also deleted.</li>
     * </ul>
     */
    public DeleteControlStream cascade(Boolean cascade) {
        setCascade(cascade);
        return this;
    }

}
