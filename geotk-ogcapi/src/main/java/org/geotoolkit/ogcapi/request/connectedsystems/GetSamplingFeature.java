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
 *     Get a sampling feature by ID
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/paths/samplingFeatureById.yaml"> OGC API - Connected Systems - Part 1 (draft) - Get a sampling feature by ID</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetSamplingFeature extends RequestParameters {

    private String featureId;

    /**
     * @see #featureId(java.lang.String)
     */
    public String getFeatureId() {
        return featureId;
    }

    /**
     * @see #featureId(java.lang.String)
     */
    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a Feature
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/featureId.yaml"> OGC API - Connected Systems - Part 1 (draft) - featureId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetSamplingFeature featureId(String featureId) {
        setFeatureId(featureId);
        return this;
    }

}
