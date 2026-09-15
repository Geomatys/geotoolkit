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
package org.geotoolkit.ogcwxs.request.catalog;

import org.geotoolkit.csw.xml.CSWVersion;
import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * To retrieve the service metadata document describing the CSW instance.
 *
 * <blockquote><q>
 * Allows clients to retrieve service metadata describing Catalogue Service instance
 * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_6">OGC 12-168r6, Table 6</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 * @see #version(CSWVersion)
 * @see #updateSequence(String)
 */
public final class GetCapabilities extends RequestParameters {

    private CSWVersion version;
    private String updateSequence;

    /**
     * The CSW version to request. Defaults to {@link CSWVersion#v300} if left {@code null}.
     *
     * @return the version
     * @see #version(CSWVersion)
     */
    public CSWVersion getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     * @see #version(CSWVersion)
     */
    public void setVersion(CSWVersion version) {
        this.version = version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     * @see #setVersion(CSWVersion)
     */
    public GetCapabilities version(CSWVersion version) {
        setVersion(version);
        return this;
    }

    /**
     * Identifier the server can use to know if a cached capabilities document is still valid. (optional)
     *
     * <blockquote><q>
     * Optional identifier(s) of requested parts of the complete service metadata document
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_6">OGC 12-168r6, Table 6 (Receives)</a></small></i>
     * </blockquote>
     *
     * @return the updateSequence
     * @see #updateSequence(String)
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * @param updateSequence the updateSequence to set
     * @see #updateSequence(String)
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    /**
     * @param updateSequence the updateSequence to set
     * @see #getUpdateSequence()
     * @see #setUpdateSequence(String)
     */
    public GetCapabilities updateSequence(String updateSequence) {
        setUpdateSequence(updateSequence);
        return this;
    }

}
