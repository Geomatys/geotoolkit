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
package org.geotoolkit.ogcapi.dto.connectedsystems.geojson;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.TimePeriod;

/**
 * The {@code properties} sub-object of a {@link SamplingFeature} GeoJSON feature.
 *
 * <p>Unlike {@link System}, {@link Deployment} and {@link Procedure}, {@code featureType} here is
 * a plain, unrestricted string (no fixed enumeration of allowed values).</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
public class SamplingFeatureProperties extends FeatureProperties {

    public static final String JSON_PROPERTY_VALID_TIME = "validTime";
    private TimePeriod validTime;

    public static final String JSON_PROPERTY_SAMPLED_FEATURE_LINK = "sampledFeature@link";
    private Link sampledFeatureLink;

    /**
     * Time period during which the sampling feature is/was valid. (optional)
     */
    @JsonProperty(JSON_PROPERTY_VALID_TIME)
    public TimePeriod getValidTime() {
        return validTime;
    }

    @JsonProperty(JSON_PROPERTY_VALID_TIME)
    public void setValidTime(TimePeriod validTime) {
        this.validTime = validTime;
    }

    public SamplingFeatureProperties validTime(TimePeriod validTime) {
        setValidTime(validTime);
        return this;
    }

    /**
     * Link to the real world feature of interest sampled by this sampling feature. (required)
     */
    @JsonProperty(JSON_PROPERTY_SAMPLED_FEATURE_LINK)
    public Link getSampledFeatureLink() {
        return sampledFeatureLink;
    }

    @JsonProperty(JSON_PROPERTY_SAMPLED_FEATURE_LINK)
    public void setSampledFeatureLink(Link sampledFeatureLink) {
        this.sampledFeatureLink = sampledFeatureLink;
    }

    public SamplingFeatureProperties sampledFeatureLink(Link sampledFeatureLink) {
        setSampledFeatureLink(sampledFeatureLink);
        return this;
    }

}
