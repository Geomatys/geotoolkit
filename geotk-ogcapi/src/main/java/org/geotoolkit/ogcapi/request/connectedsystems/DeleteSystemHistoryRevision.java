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
 *     This will delete a specific revision of a system description.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/systemHistoryById.yaml"> OGC API - Connected Systems - Part 2 (draft) - Delete a system description revision</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DeleteSystemHistoryRevision extends RequestParameters {

    private String systemId;
    private String revId;

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
    public DeleteSystemHistoryRevision systemId(String systemId) {
        setSystemId(systemId);
        return this;
    }

    /**
     * @see #revId(java.lang.String)
     */
    public String getRevId() {
        return revId;
    }

    /**
     * @see #revId(java.lang.String)
     */
    public void setRevId(String revId) {
        this.revId = revId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a historical revision
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/revId.yaml"> OGC API - Connected Systems - Part 2 (draft) - revId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Non-empty string.</li>
     * </ul>
     */
    public DeleteSystemHistoryRevision revId(String revId) {
        setRevId(revId);
        return this;
    }

}
