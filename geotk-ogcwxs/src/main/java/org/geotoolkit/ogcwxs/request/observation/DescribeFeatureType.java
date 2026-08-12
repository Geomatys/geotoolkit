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
 * To retrieve a description of the feature-of-interest type used by a sensor.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DescribeFeatureType extends RequestParameters {

    private String featureId;

    /**
     * Identifier of the sensor whose feature-of-interest type is requested. (required)
     *
     * @return the featureId
     */
    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public DescribeFeatureType featureId(String featureId) {
        setFeatureId(featureId);
        return this;
    }

}
