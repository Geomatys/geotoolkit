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
package org.geotoolkit.ogcwxs.request.observation;

import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * To retrieve the time period during which a feature-of-interest has been observed.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetFeatureOfInterestTime extends RequestParameters {

    private String featureOfInterestId;

    /**
     * Identifier of the feature-of-interest to query. (required)
     *
     * @return the featureOfInterestId
     */
    public String getFeatureOfInterestId() {
        return featureOfInterestId;
    }

    public void setFeatureOfInterestId(String featureOfInterestId) {
        this.featureOfInterestId = featureOfInterestId;
    }

    public GetFeatureOfInterestTime featureOfInterestId(String featureOfInterestId) {
        setFeatureOfInterestId(featureOfInterestId);
        return this;
    }

}
