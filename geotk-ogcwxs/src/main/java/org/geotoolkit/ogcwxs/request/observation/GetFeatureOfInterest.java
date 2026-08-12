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
import org.opengis.filter.Filter;

/**
 * To retrieve a feature-of-interest either by its identifier or by a spatial filter on its location.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetFeatureOfInterest extends RequestParameters {

    private String featureOfInterestId;
    private Filter location;

    /**
     * Identifier of the feature-of-interest to retrieve. (either this or {@link #getLocation()} is required)
     *
     * @return the featureOfInterestId
     */
    public String getFeatureOfInterestId() {
        return featureOfInterestId;
    }

    public void setFeatureOfInterestId(String featureOfInterestId) {
        this.featureOfInterestId = featureOfInterestId;
    }

    public GetFeatureOfInterest featureOfInterestId(String featureOfInterestId) {
        setFeatureOfInterestId(featureOfInterestId);
        return this;
    }

    /**
     * Spatial filter on the feature-of-interest location. (either this or {@link #getFeatureOfInterestId()} is required)
     *
     * @return the location
     */
    public Filter getLocation() {
        return location;
    }

    public void setLocation(Filter location) {
        this.location = location;
    }

    public GetFeatureOfInterest location(Filter location) {
        setLocation(location);
        return this;
    }

}
