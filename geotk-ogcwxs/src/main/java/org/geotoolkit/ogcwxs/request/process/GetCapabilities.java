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
package org.geotoolkit.ogcwxs.request.process;

import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * To retrieve the service metadata document describing the WPS instance.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetCapabilities extends RequestParameters {

    private String version;
    private String updateSequence;

    /**
     * The WPS version to request. Defaults to {@code "2.0.0"} if left {@code null}.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public GetCapabilities version(String version) {
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
