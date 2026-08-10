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
package org.geotoolkit.ogcwxs.request.map;

import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.wms.xml.WMSVersion;

/**
 * To retrieve the service metadata document describing the WMS instance.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetCapabilities extends RequestParameters {

    private WMSVersion version;
    private String updateSequence;

    /**
     * The WMS version to request. Defaults to {@link WMSVersion#v130} if left {@code null}.
     *
     * @return the version
     */
    public WMSVersion getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public void setVersion(WMSVersion version) {
        this.version = version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public GetCapabilities version(WMSVersion version) {
        setVersion(version);
        return this;
    }

    /**
     * Identifier the server can use to know if a cached capabilities document is still valid. (optional)
     *
     * @return the updateSequence
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * @param updateSequence the updateSequence to set
     * @see #getUpdateSequence()
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    /**
     * @param updateSequence the updateSequence to set
     * @see #getUpdateSequence()
     */
    public GetCapabilities updateSequence(String updateSequence) {
        setUpdateSequence(updateSequence);
        return this;
    }

}
