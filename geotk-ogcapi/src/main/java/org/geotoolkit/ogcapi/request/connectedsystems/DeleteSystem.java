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
 * The {@code DELETE} operation on {@code /systems/{systemId}}: deletes a System feature resource.
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 * @author Johann Sorel (Geomatys)
 */
public final class DeleteSystem extends RequestParameters {

    private String systemId;
    private Boolean cascade;

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
    public DeleteSystem systemId(String systemId) {
        setSystemId(systemId);
        return this;
    }

    /**
     * If {@code true}, related resources are deleted as well. (optional)
     *
     * @return the cascade
     */
    public Boolean getCascade() {
        return cascade;
    }

    /**
     * @param cascade the cascade to set
     * @see #getCascade()
     */
    public void setCascade(Boolean cascade) {
        this.cascade = cascade;
    }

    /**
     * @param cascade the cascade to set
     * @see #getCascade()
     */
    public DeleteSystem cascade(Boolean cascade) {
        setCascade(cascade);
        return this;
    }

}
