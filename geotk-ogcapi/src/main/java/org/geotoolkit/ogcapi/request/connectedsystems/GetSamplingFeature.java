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
 * The {@code GET} operation on {@code /samplingFeatures/{featureId}}: retrieves a single Sampling
 * Feature resource.
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 * @author Johann Sorel (Geomatys)
 */
public final class GetSamplingFeature extends RequestParameters {

    private String featureId;

    /**
     * Local identifier of the sampling feature. (required)
     *
     * @return the featureId
     */
    public String getFeatureId() {
        return featureId;
    }

    /**
     * @param featureId the featureId to set
     * @see #getFeatureId()
     */
    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    /**
     * @param featureId the featureId to set
     * @see #getFeatureId()
     */
    public GetSamplingFeature featureId(String featureId) {
        setFeatureId(featureId);
        return this;
    }

}
