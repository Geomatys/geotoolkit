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
package org.geotoolkit.ogcwxs.request.transformation;

import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * To retrieve the capabilities document of a WCTS.
 *
 * <blockquote>
 * "The mandatory GetCapabilities operation allows WCTS clients to retrieve service metadata
 * from a server. The response to a GetCapabilities request shall be a XML document containing
 * service metadata about the server, including specific information about its transformation
 * abilities."
 * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, §8.1</a>
 * </blockquote>
 *
 * <pre>
 * www.lat-lon.de/transform?service=WCTS&amp;request=getCapabilities
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetCapabilitiesRequest extends RequestParameters {

    private String updateSequence;

    /**
     * <blockquote>
     * "The 'service', 'request', and 'AcceptVersions' parameters shall be implemented by all
     * WCTS servers. The 'Sections' and 'updateSequence' parameters are optional implementation
     * by WCTS servers."
     * <br>&mdash; <a href="https://docs.ogc.org/dp/07-055r1/07-055r1/zip">OGC 05-013, §8.2</a>
     * </blockquote>
     *
     * @return the update sequence
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * @param updateSequence the update sequence to set
     * @see #getUpdateSequence()
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    /**
     * @param updateSequence the update sequence to set
     * @see #getUpdateSequence()
     */
    public GetCapabilitiesRequest updateSequence(String updateSequence) {
        setUpdateSequence(updateSequence);
        return this;
    }
}
