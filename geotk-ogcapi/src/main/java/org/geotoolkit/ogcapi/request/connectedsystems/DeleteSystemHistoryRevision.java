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
 * The {@code DELETE} operation on {@code /systems/{systemId}/history/{revId}}: deletes a
 * historical revision of a System.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class DeleteSystemHistoryRevision extends RequestParameters {

    private String systemId;
    private String revId;

    /**
     * Local identifier of the system. (required)
     *
     * @return the systemId
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * @param systemId the systemId to set
     * @see #getSystemId()
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * @param systemId the systemId to set
     * @see #getSystemId()
     */
    public DeleteSystemHistoryRevision systemId(String systemId) {
        setSystemId(systemId);
        return this;
    }

    /**
     * Identifier of the historical revision. (required)
     *
     * @return the revId
     */
    public String getRevId() {
        return revId;
    }

    /**
     * @param revId the revId to set
     * @see #getRevId()
     */
    public void setRevId(String revId) {
        this.revId = revId;
    }

    /**
     * @param revId the revId to set
     * @see #getRevId()
     */
    public DeleteSystemHistoryRevision revId(String revId) {
        setRevId(revId);
        return this;
    }

}
