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
import java.util.List;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.TimePeriod;

/**
 * The {@code properties} sub-object of a {@link Deployment} GeoJSON feature.
 *
 * <p>{@code featureType} is restricted to {@code http://www.w3.org/ns/sosa/Deployment} or the
 * CURIE form {@code sosa:Deployment}.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
public class DeploymentProperties extends FeatureProperties {

    public static final String JSON_PROPERTY_VALID_TIME = "validTime";
    private TimePeriod validTime;

    public static final String JSON_PROPERTY_PLATFORM_LINK = "platform@link";
    private Link platformLink;

    public static final String JSON_PROPERTY_DEPLOYED_SYSTEMS_LINK = "deployedSystems@link";
    private List<Link> deployedSystemsLink;

    /**
     * Time period during which the deployment is/was active. (required)
     */
    @JsonProperty(JSON_PROPERTY_VALID_TIME)
    public TimePeriod getValidTime() {
        return validTime;
    }

    @JsonProperty(JSON_PROPERTY_VALID_TIME)
    public void setValidTime(TimePeriod validTime) {
        this.validTime = validTime;
    }

    public DeploymentProperties validTime(TimePeriod validTime) {
        setValidTime(validTime);
        return this;
    }

    /**
     * Link to the platform on which the systems are deployed. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PLATFORM_LINK)
    public Link getPlatformLink() {
        return platformLink;
    }

    @JsonProperty(JSON_PROPERTY_PLATFORM_LINK)
    public void setPlatformLink(Link platformLink) {
        this.platformLink = platformLink;
    }

    public DeploymentProperties platformLink(Link platformLink) {
        setPlatformLink(platformLink);
        return this;
    }

    /**
     * Links to the systems deployed as part of this deployment. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DEPLOYED_SYSTEMS_LINK)
    public List<Link> getDeployedSystemsLink() {
        return deployedSystemsLink;
    }

    @JsonProperty(JSON_PROPERTY_DEPLOYED_SYSTEMS_LINK)
    public void setDeployedSystemsLink(List<Link> deployedSystemsLink) {
        this.deployedSystemsLink = deployedSystemsLink;
    }

    public DeploymentProperties deployedSystemsLink(List<Link> deployedSystemsLink) {
        setDeployedSystemsLink(deployedSystemsLink);
        return this;
    }

}
